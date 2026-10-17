from enum import Enum

class Variante(Enum):
    RIVERS = 1
    LAKES = 2
    CHALLENGE = 3

varianteStr = {
    Variante.RIVERS : "rivers",
    Variante.LAKES : "lakes",
    Variante.CHALLENGE : "challenge"
}