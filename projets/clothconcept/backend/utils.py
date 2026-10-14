# Palette de couleurs fashion courantes (RGB) - VERSION ÉTENDUE
COLOR_NAMES = {
    # Blancs et crèmes
    "white": (255, 255, 255),
    "ivory": (255, 255, 240),
    "cream": (245, 245, 220),
    "off-white": (250, 250, 245),
    
    # Gris
    "light grey": (211, 211, 211),
    "grey": (128, 128, 128),
    "charcoal": (54, 54, 54),
    "dark grey": (80, 80, 80),
    "silver": (192, 192, 192),
    
    # Noirs
    "black": (0, 0, 0),
    "jet black": (10, 10, 10),
    
    # Bleus - AJOUT DE NUANCES
    "navy blue": (0, 0, 128),
    "dark blue": (0, 0, 139),
    "blue": (0, 102, 204),
    "royal blue": (65, 105, 225),
    "sky blue": (135, 206, 235),
    "light blue": (173, 216, 230),
    "denim blue": (43, 67, 96),  # ← NOUVEAU : Couleur exacte du denim
    "indigo": (75, 0, 130),
    "cobalt": (0, 71, 171),
    "teal": (0, 128, 128),
    "turquoise": (64, 224, 208),
    
    # Rouges et roses
    "red": (200, 30, 30),
    "dark red": (139, 0, 0),
    "burgundy": (128, 0, 32),
    "maroon": (128, 0, 0),
    "crimson": (220, 20, 60),
    "pink": (255, 182, 193),
    "hot pink": (255, 105, 180),
    "rose": (255, 0, 127),
    "salmon": (250, 128, 114),
    
    # Oranges et jaunes
    "orange": (255, 140, 0),
    "dark orange": (255, 100, 0),
    "coral": (255, 127, 80),
    "yellow": (255, 215, 0),
    "mustard": (212, 175, 55),
    "gold": (255, 215, 0),
    "amber": (255, 191, 0),
    
    # Verts
    "green": (34, 139, 34),
    "dark green": (0, 100, 0),
    "olive": (107, 142, 35),
    "emerald green": (0, 128, 96),
    "forest green": (34, 139, 34),
    "mint": (152, 255, 152),
    "sage": (188, 184, 138),
    
    # Violets
    "purple": (128, 0, 128),
    "dark purple": (75, 0, 130),
    "lavender": (200, 180, 230),
    "violet": (138, 43, 226),
    "plum": (142, 69, 133),
    
    # Bruns et beiges
    "brown": (139, 69, 19),
    "dark brown": (101, 67, 33),
    "tan": (210, 180, 140),
    "beige": (222, 196, 176),
    "khaki": (195, 176, 145),
    "camel": (193, 154, 107),
    "chocolate": (123, 63, 0),
}


def hex_to_color_name(hex_color: str) -> str:
    """
    Convertit un code hexadécimal (#RRGGBB) en nom de couleur le plus proche.
    
    Utilise une distance euclidienne dans l'espace RGB avec une table étendue
    pour une meilleure précision sémantique.
    
    Args:
        hex_color: Code couleur au format hexadécimal (ex: "#2b4360")
    
    Returns:
        Nom de couleur en anglais (ex: "denim blue")
    """
    if not hex_color:
        return "white"
    
    # Nettoyer le code hex
    hex_color = hex_color.lstrip("#")
    if len(hex_color) != 6:
        return "white"

    try:
        r, g, b = tuple(int(hex_color[i:i+2], 16) for i in (0, 2, 4))
    except ValueError:
        return "white"

    # Calculer la distance avec chaque couleur de la table
    closest_name = "white"
    closest_distance = float("inf")
    
    for name, (cr, cg, cb) in COLOR_NAMES.items():
        # Distance euclidienne dans l'espace RGB
        distance = (r - cr) ** 2 + (g - cg) ** 2 + (b - cb) ** 2
        
        if distance < closest_distance:
            closest_distance = distance
            closest_name = name

    return closest_name

# Traduction des choix FR -> mots-clés EN pour un meilleur prompt
NECKLINE_KEYWORDS = {
    "v_neck": "V-neck",
    "round_neck": "round neckline",
    "collar_neck": "collared neckline",
}
SLEEVES_KEYWORDS = {
    "short_sleeves": "short sleeves",
    "long_sleeves": "long sleeves",
}
WAIST_KEYWORDS = {
    "waist_low": "low waist",
    "waist_high": "high waist",
}

