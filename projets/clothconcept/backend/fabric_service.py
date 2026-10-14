from pathlib import Path
from sqlalchemy.orm import Session
from data import Fabric
from utils import hex_to_color_name

class FabricService:
    """
    Service pour gérer les tissus et leurs textures.
    Fournit une interface unifiée pour récupérer les tissus et leurs assets.
    """
    def __init__(self, db: Session, textures_dir: Path = None):
        self.db = db
        if textures_dir is None:
            project_dir = Path(__file__).resolve().parent.parent
            self.textures_dir = project_dir / "assets" / "fabrics"
        else:
            self.textures_dir = textures_dir
    
    def get_fabric_with_texture(self, fabric_id: str, hex_color: str = None) -> dict:
        """
        Récupère un tissu avec son image de texture.
        
        Args:
            fabric_id: Identifiant du tissu (ex: 'cotton_uni')
            hex_color: Couleur personnalisée (optionnel)
        
        Returns:
            Dictionnaire contenant:
            - fabric: Objet Fabric
            - texture_path: Chemin vers l'image de texture
            - texture_bytes: Contenu binaire de l'image
            - prompt_context: Contexte textuel pour le prompt IA
        
        Raises:
            ValueError: Si le tissu n'est pas trouvé
            FileNotFoundError: Si l'image de texture n'existe pas
        """
        
        fabric = self.db.query(Fabric).filter(Fabric.id == fabric_id).first()
        if not fabric:
            raise ValueError(f"Tissu {fabric_id} introuvable")
        
        # Déterminer le chemin de l'image de texture
        texture_path = self._resolve_texture_path(fabric, hex_color)
        
        if not texture_path.exists():
            raise FileNotFoundError(f"Image de texture introuvable: {texture_path} "
                                    f"Vérifie que le fichier existe dans {self.textures_dir}"
            )
        
        # Charger l'image
        texture_bytes = self._load_image(texture_path)
        
        # Construire le contexte pour le prompt
        prompt_context = self._build_prompt_context(fabric, hex_color)
        
        return {
            "fabric": fabric,
            "texture_path": texture_path,
            "texture_bytes": texture_bytes,
            "prompt_context": prompt_context
        }
    
    def _resolve_texture_path(self, fabric: Fabric, hex_color: str = None) -> Path:
        """
        Détermine le chemin de l'image de texture à utiliser.
        
        Stratégie:
        1. Si hex_color == default_color → utiliser l'image par défaut
        2. Sinon → utiliser l'image par défaut (future: générer variante)
        """
        # Pour l'instant, on utilise toujours l'image par défaut du tissu
        # Future amélioration: générer des variantes de couleur dynamiquement
        texture_filename = fabric.texture_img_path
        
        if not texture_filename:
            raise ValueError(f"Le tissu '{fabric.id}' n'a pas d'image de texture configurée")
        
        return self.textures_dir / texture_filename
    
    def _load_image(self, path: Path) -> bytes:
        """
        Charge une image depuis le disque.
        
        Args:
            path: Chemin vers le fichier image
        
        Returns:
            Contenu binaire de l'image
        """
        try:
            with open(path, "rb") as f:
                return f.read()
        except IOError as e:
            raise IOError(f"Impossible de lire l'image {path}: {str(e)}")
    
    def _build_prompt_context(self, fabric: Fabric, hex_color: str) -> str:
        """
        Construit le contexte textuel pour enrichir le prompt IA.
        
        Args:
            fabric: Objet Fabric
            hex_color: Couleur personnalisée (optionnel)
        
        Returns:
            Chaîne de contexte pour le prompt
        """
        # Déterminer la couleur
        color_to_use = hex_color if hex_color else fabric.default_color
        color_name = hex_to_color_name(color_to_use)
        
        # Construire le contexte
        parts = [
            f"{fabric.name} fabric",
            f"dyed in {color_name}",
            f"texture type: {fabric.type.lower()}",
        ]
        
        return ", ".join(parts)
    
    def list_fabrics(self) -> list:
        """
        Récupère la liste de tous les tissus disponibles.
        
        Returns:
            Liste de dictionnaires avec les informations des tissus
        """
        fabrics = self.db.query(Fabric).order_by(Fabric.name).all()
        
        return [
            {
                "id": f.id,
                "name": f.name,
                "type": f.type,
                "default_color": f.default_color,
                "allow_custom_color": f.allow_custom_color,
                "has_texture": bool(f.texture_img_path)
            }
            for f in fabrics
        ]