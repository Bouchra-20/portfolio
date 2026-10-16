import math

class Reconnaisseur:
    """
    Fournit les outils mathématiques pour la reconnaissance de formes gestuelles.
    Permet de normaliser les tracés (rééchantillonnage) et de calculer la 
    similitude entre deux tracés (algorithme DTW).
    """
    def __init__(self):
        self.stroke = []

    def interpolate(self, a: tuple, b: tuple, d: int) -> tuple:
        """
        Calcule un point situé sur le segment [a, b] à une distance exacte 'd' de 'a'.
        
        Args:
            a: Point de départ (x, y).
            b: Point d'arrivée (x, y).
            d: Distance souhaitée depuis le point 'a'.
        Returns:
            Un tuple (px, py) représentant le nouveau point.
        """
        # Calcul de la distance totale entre les deux points a et b
        length = math.dist(a, b)

        # Calcul du vecteur directionnel (dx, dy)
        dx = b[0] - a[0]
        dy = b[1] - a[1]
        
        # Le nouveau point est le point 'a' + (vecteur unitaire * distance d)
        px = a[0] + (dx / length) * d
        py = a[1] + (dy / length) * d

        return (px, py)

    def resample(self, stroke: list, d: int) -> list:
        """
        Redessine le tracé pour que chaque point soit séparé du précédent 
        par une distance fixe 'd'.
        
        Cette étape élimine les variations dues à la vitesse du mouvement 
        et permet de comparer des tracés de longueurs différentes.
        """
        result = [stroke[0]]   
        # 'remaining' stocke la distance "consommée" sur le segment précédent       
        remaining = 0.0               

        # On parcourt tous les segments du tracé (de i-1 vers i)
        for i in range(1, len(stroke)):
            a = stroke[i - 1]
            b = stroke[i]
            segment_length = math.dist(a, b)

            # Distance totale disponible en incluant le reliquat du segment précédent
            accumulated = remaining + segment_length

            # Tant qu'on peut placer un nouveau point à distance 'd'
            while accumulated >= d:
                # Calcul de la distance nécessaire sur le segment actuel [a,b]
                dist_from_a = d - remaining
                # Création du nouveau point intermédiaire
                new_point = self.interpolate(a, b, dist_from_a)
                result.append(new_point)
                
                # Le nouveau point devient le point de départ pour la suite du segment
                a = new_point
                # On soustrait la distance 'd' consommée
                accumulated -= d
                # On remet le reliquat à zéro puisqu'on repart du nouveau point
                remaining = 0.0
                
            # Ce qu'il reste du segment (trop court pour faire 'd') devient le 'remaining'
            remaining = accumulated 

        return result

    def dtw(self, s: list, t: list) -> float:
        """
        Calcule la distance Dynamic Time Warping (DTW) entre deux tracés s et t.
        
        Le DTW mesure la similarité de forme entre deux séquences de points, 
        même si l'une est légèrement décalée, étirée ou compressée par rapport à l'autre.
        """
        n = len(s)
        m = len(t)
        # Initialisation de la matrice DTW avec des valeurs infinies
        # Taille (n+1)x(m+1) pour gérer les cas aux limites
        DTW = [[float('inf')] * (m + 1) for _ in range(n + 1)]

        # Le coût de départ (base de la matrice) est zéro
        DTW[0][0] = 0

        # Remplissage de la matrice par programmation dynamique
        for i in range(1, n + 1):
            for j in range(1, m + 1):
                # Coût local : distance euclidienne entre le point i de S et j de T
                cost = math.dist(s[i - 1], t[j - 1])

                # Le coût cumulé est le coût local + le minimum des voisins précédents
                # On regarde à gauche, en haut et en diagonale (i-1, j-1)
                DTW[i][j] = cost + min(
                    DTW[i - 1][j],      # Insertion
                    DTW[i][j - 1],      # Suppression
                    DTW[i - 1][j - 1]   # Correspondance (Match)
                )

        # La distance finale de similarité se trouve dans la dernière cellule
        return DTW[n][m]
