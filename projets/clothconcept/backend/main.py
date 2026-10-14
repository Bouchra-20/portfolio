import base64
import io
import os
import time
from pathlib import Path
from datetime import datetime
from typing import Optional

from fastapi import Depends, FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import FileResponse
from pydantic import BaseModel
import requests
from PIL import Image
from sqlalchemy.orm import Session
from dotenv import load_dotenv

from data import GeneratedDesignModel, SessionLocal, get_db
from fabric_service import FabricService
from utils import hex_to_color_name, NECKLINE_KEYWORDS, SLEEVES_KEYWORDS, WAIST_KEYWORDS

app = FastAPI(title="ClotheConcep API", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173", "http://127.0.0.1:5173", "http://localhost:3000"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

load_dotenv(dotenv_path=Path(__file__).parent / ".env", override=True)

STABILITY_API_KEY = os.getenv("STABILITY_API_KEY")

# ─── Les 2 endpoints utilisés dans le pipeline ────────────────────────────────
SKETCH_URL    = "https://api.stability.ai/v2beta/stable-image/control/sketch"
STRUCTURE_URL = "https://api.stability.ai/v2beta/stable-image/control/structure"

PROJECT_DIRECTORY  = Path(__file__).resolve().parent.parent
CROQUIS_DIRECTORY  = PROJECT_DIRECTORY / "assets" / "patrons"
OUTPUTS_DIRECTORY  = PROJECT_DIRECTORY / "outputs" / "generated"
OUTPUTS_DIRECTORY.mkdir(parents=True, exist_ok=True)


class DesignSelection(BaseModel):
    gender: str
    category: str
    silhouette: str
    neckline: Optional[str] = None
    sleeves: Optional[str] = None
    waist: Optional[str] = None
    fabric_type: str
    hex_color: str = ""
    prompt: str = ""
    control_strength: float = 0.6
    save_to_disk: bool = True


# ─── Helpers ──────────────────────────────────────────────────────────────────

def get_auth_headers() -> dict:
    return {
        "authorization": f"Bearer {STABILITY_API_KEY}",
        "accept": "image/*",
    }


def call_stability(url: str, img_bytes: bytes, data: dict) -> bytes:
    """
    Appel générique à un endpoint Stability AI.
    Envoie une image + des paramètres, retourne les bytes de l'image résultat.
    """
    response = requests.post(
        url,
        headers=get_auth_headers(),
        files={"image": ("image.png", img_bytes, "image/png")},
        data=data,
        timeout=90,
    )
    if response.status_code != 200:
        raise Exception(f"Stability AI [{url.split('/')[-1]}] erreur {response.status_code}: {response.text}")
    return response.content


def prepare_croquis(croquis_path: Path) -> bytes:
    """Charge et normalise le croquis en 1024x1024 sur fond blanc."""
    img = Image.open(croquis_path).convert("RGBA")
    canvas = Image.new("RGBA", (1024, 1024), (255, 255, 255, 255))
    img.thumbnail((1024, 1024), Image.Resampling.LANCZOS)
    offset = ((1024 - img.width) // 2, (1024 - img.height) // 2)
    canvas.paste(img, offset, img)
    buf = io.BytesIO()
    canvas.convert("RGB").save(buf, format="PNG")
    return buf.getvalue()


def find_croquis_path(selection: DesignSelection) -> Path:
    parts = [selection.silhouette]
    if selection.neckline: parts.append(selection.neckline)
    if selection.sleeves:  parts.append(selection.sleeves)
    if selection.waist:    parts.append(selection.waist)

    for i in range(len(parts), 0, -1):
        candidate = CROQUIS_DIRECTORY / ("_".join(parts[:i]) + ".png")
        if candidate.exists():
            return candidate

    raise HTTPException(status_code=404, detail=f"Croquis introuvable : {'_'.join(parts)}.png")


def build_prompt(selection: DesignSelection, fabric_context: str = "") -> str:
    category   = selection.category.replace("tmpl_", "").replace("_", " ")
    color_name = hex_to_color_name(selection.hex_color)

    details = []
    if selection.neckline in NECKLINE_KEYWORDS: details.append(NECKLINE_KEYWORDS[selection.neckline])
    if selection.sleeves  in SLEEVES_KEYWORDS:  details.append(SLEEVES_KEYWORDS[selection.sleeves])
    if selection.waist    in WAIST_KEYWORDS:     details.append(WAIST_KEYWORDS[selection.waist])
    details_str = ", ".join(details) if details else "classic design"

    fabric_desc = fabric_context if fabric_context else selection.fabric_type.replace("_", " ")

    return (
        f"Professional fashion photography, a ({color_name}:1.4) {category} "
        f"with {details_str}, made of {color_name} {fabric_desc} fabric, "
        f"worn on a mannequin, realistic fabric texture and folds, "
        f"studio lighting, clean light grey background, "
        f"e-commerce product shot, high resolution, photorealistic, masterpiece"
    )


def save_result(image_bytes: bytes, selection: DesignSelection) -> str:
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    parts = [timestamp, selection.silhouette,
             selection.neckline or "none", selection.sleeves or "none",
             selection.waist or "none", selection.fabric_type,
             selection.hex_color.replace("#", "")]
    filename = "_".join(parts) + ".png"
    try:
        with open(OUTPUTS_DIRECTORY / filename, "wb") as f:
            f.write(image_bytes)
        print(f"Image sauvegardée : {filename}")
        return filename
    except Exception as e:
        print(f"Erreur sauvegarde : {e}")
        return ""


def save_to_database(db: Session, selection: DesignSelection, prompt: str, saved_filename: str) -> Optional[int]:
    try:
        record = GeneratedDesignModel(
            gender=selection.gender, category=selection.category,
            silhouette=selection.silhouette, neckline=selection.neckline,
            sleeves=selection.sleeves, waist=selection.waist,
            fabric_type=selection.fabric_type, hex_color=selection.hex_color,
            prompt=prompt, image_url_or_path=saved_filename or None
        )
        db.add(record)
        db.commit()
        db.refresh(record)
        return record.id
    except Exception as e:
        print(f"Erreur BDD : {e}")
        db.rollback()
        return None


# ─── Pipeline 2 étapes ─────────────────────────────────────────────────────────

def generate_pipeline(croquis_bytes: bytes, prompt: str, control_strength: float) -> tuple[bytes, float]:
    """
    Pipeline en 2 appels Stability AI :

    ┌─────────────┐   control/sketch    ┌──────────────────┐
    │   Croquis   │ ──────────────────► │ Image étape 1    │
    │ (patron SVG)│  control_strength   │ (bonne silhouette│
    └─────────────┘  = 0.7 (strict)    │  style général)  │
                                        └────────┬─────────┘
                                                 │
                                  control/structure
                                  control_strength = [user]
                                  (garde la forme,
                                   applique le tissu)
                                                 │
                                        ┌────────▼─────────┐
                                        │  Image finale    │
                                        │ (forme + tissu)  │
                                        └──────────────────┘
    """
    negative_prompt = (
        "sketch lines, drawing, flat illustration, low quality, blurry, "
        "deformed, bad proportions, watermark, text, brown tint, sepia"
    )

    # ── Étape 1 : Croquis -> Silhouette correcte ──────────────────────────────
    t0 = time.time()
    step1_bytes = call_stability(
        SKETCH_URL,
        croquis_bytes,
        {
            "prompt": prompt,
            "negative_prompt": negative_prompt,
            "control_strength": 0.7,    # strict : respecte la forme du croquis
            "output_format": "png",
            "style_preset": "photographic",
        },
    )

    # ── Étape 2 : Silhouette -> Tissu appliqué (structure préservée) ──────────
    t1 = time.time()
    step2_bytes = call_stability(
        STRUCTURE_URL,
        step1_bytes,                   # ← image de l'étape 1
        {
            "prompt": prompt,          # ← même prompt avec description du tissu
            "negative_prompt": negative_prompt,
            "control_strength": control_strength,  # réglable par l'utilisateur
            "output_format": "png",
            "style_preset": "photographic",
        },
    )

    total = time.time() - t0
    return step2_bytes, total


# ─── Endpoints ─────────────────────────────────────────────────────────────────

@app.post("/api/generate")
async def generate_pattern(selection: DesignSelection, db: Session = Depends(get_db)):
    if not STABILITY_API_KEY:
        raise HTTPException(status_code=500, detail="STABILITY_API_KEY manquante")

    # 1. Récupérer le tissu depuis la BDD
    fabric_service  = FabricService(db)
    fabric_context  = ""
    fabric_name_log = selection.fabric_type

    try:
        fabric_data    = fabric_service.get_fabric_with_texture(selection.fabric_type, selection.hex_color)
        fabric_context = fabric_data["prompt_context"]
        fabric_name_log = fabric_data["fabric"].name
    except Exception as e:
        print(f"Tissu non trouvé en BDD ({e}), fallback prompt textuel")

    # 2. Croquis
    croquis_path  = find_croquis_path(selection)
    croquis_bytes = prepare_croquis(croquis_path)

    # 3. Prompt
    prompt = build_prompt(selection, fabric_context)

    try:
        # 4. Pipeline 2 étapes
        final_bytes, elapsed = generate_pipeline(
            croquis_bytes,
            prompt,
            selection.control_strength,
        )

        # 5. Sauvegarde disque + BDD
        saved_filename = save_result(final_bytes, selection) if selection.save_to_disk else ""
        design_id = save_to_database(db, selection, prompt, saved_filename)


        return {
            "success": True,
            "image": base64.b64encode(final_bytes).decode("utf-8"),
            "saved_filename": saved_filename,
            "design_id": design_id,
            "provider": "stability-ai-2-step",
            "time_seconds": round(elapsed, 1),
            "fabric_used": fabric_name_log,
        }

    except Exception as e:
        print(f" Erreur pipeline : {e}")
        raise HTTPException(status_code=500, detail=str(e))


@app.get("/api/outputs/{filename}")
async def get_generated_image(filename: str):
    filepath = OUTPUTS_DIRECTORY / filename
    if not filepath.exists():
        raise HTTPException(status_code=404, detail="Image introuvable")
    return FileResponse(filepath, media_type="image/png")


@app.get("/api/outputs")
async def list_generated_images():
    files = [
        {"filename": f.name, "url": f"/api/outputs/{f.name}",
         "size": f.stat().st_size,
         "created": datetime.fromtimestamp(f.stat().st_ctime).isoformat()}
        for f in sorted(OUTPUTS_DIRECTORY.glob("*.png"), reverse=True)
    ]
    return {"images": files, "total": len(files)}


@app.get("/api/fabrics")
async def list_fabrics(db: Session = Depends(get_db)):
    fabric_service = FabricService(db)
    fabrics = fabric_service.list_fabrics()
    for f in fabrics:
        f["imageUrl"] = f"http://localhost:8000/api/fabrics/{f['id']}/image"
    return {"fabrics": fabrics}


@app.get("/api/fabrics/{fabric_id}/image")
async def get_fabric_image(fabric_id: str, db: Session = Depends(get_db)):
    fabric_service = FabricService(db)
    try:
        data = fabric_service.get_fabric_with_texture(fabric_id)
        path = data["texture_path"]
        suffix = path.suffix.lower()
        media_type = {".jpg": "image/jpeg", ".jpeg": "image/jpeg",
                      ".png": "image/png", ".webp": "image/webp"}.get(suffix, "image/png")
        return FileResponse(path=path, media_type=media_type,
                            headers={"Cache-Control": "public, max-age=86400"})
    except Exception as e:
        raise HTTPException(status_code=404, detail=str(e))