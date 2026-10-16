
class Mot:
    """
    Représente une entrée lexicale associée à sa signature géométrique.
    
    Attributs:
        word (str): La chaîne de caractères (ex: "bonjour").
        stroke (list): Le tracé théorique ré-échantillonné passant par les 
                       centres des touches du clavier pour ce mot.
    """
    def __init__(self, word: str, stroke: list):
        self.word   = word
        self.stroke = stroke

class Dictionnaire:
    """
    Gère la base de données de mots et l'algorithme de reconnaissance gestuelle.
    
    Cette classe permet de charger un lexique et de trouver les mots les plus 
    proches d'un tracé effectué par l'utilisateur.
    """
    def __init__(self):
        self.mots: list[Mot] = []

    def load(self, filepath: str, keyboard, d: int):
        """
        Charge un fichier texte (un mot par ligne) et pré-calcule le tracé 
        théorique de chaque mot en fonction du layout du clavier.
        
        Args:
            filepath (str): Chemin du fichier .txt.
            keyboard: Instance du KeyboardWidget pour utiliser wordToStroke.
            d (int): Distance de ré-échantillonnage pour les tracés.
        """
        with open(filepath, "r", encoding="utf-8") as f:
            lines = f.read().splitlines()

        for word in lines:
            word = word.strip()
            if not word:
                continue

            stroke = keyboard.wordToStroke(word.upper(), d)

            if stroke:
                self.mots.append(Mot(word, stroke))

    def recherche(self, stroke: list, reconnaisseur, nb_reco: int,
                  keyboard) -> list[str]:
        """
        Compare le tracé utilisateur avec les tracés du dictionnaire.
        Utilise une optimisation par première lettre et l'algorithme DTW.
        
        Args:
            stroke (list): Le tracé de l'utilisateur ré-échantillonné.
            reconnaisseur: Instance contenant l'algorithme DTW.
            nb_reco (int): Nombre de suggestions à retourner.
            keyboard: Utilisé pour identifier la touche de départ.
            
        Returns:
            list[str]: Les N mots les plus probables.
        """

        first_point = stroke[0]
        first_key = keyboard._key_at_tuple(first_point)
        first_letter = first_key.symbol.upper() if first_key else None

        candidats = []
        for mot in self.mots:
            if first_letter and not mot.word.upper().startswith(first_letter):
                continue

            distance = reconnaisseur.dtw(stroke, mot.stroke)
            candidats.append((distance, mot.word))

        candidats.sort(key=lambda x: x[0]) #trie du plus proche au plus éloigné.
        return [word for (_, word) in candidats[:nb_reco]]

        