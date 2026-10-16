class NormalCursor:
    """
    Initialise le curseur normal qui interagit avec les cibles.
    
    Cette classe gère la logique de détection de collision avec les cibles
    et met à jour leur état visuel (highlighted) en conséquence.
    """
    def __init__(self, targets):
        """
        Initialise le curseur normal.
        
        :param targets: Référence vers la liste des cibles présentes dans le widget.
        """
        self.x = 0
        self.y = 0
        self.targets = targets
        self.closest = None

    def move(self, x, y):
        """
        Met à jour la position du curseur et déclenche le recalcul de la cible la plus proche.
        
        L'algorithme vérifie pour chaque cible si la distance entre le curseur 
        et le centre est inférieure ou égale au rayon de la cible.

        :param x: Position X du curseur.
        :param y: Position Y du curseur.
        """
        self.x = x
        self.y = y
        self.closest = None

        for t in self.targets:
            dx = t.x - x
            dy = t.y - y
            inside = dx*dx + dy*dy <= (t.size/2)**2
            t.highlighted = inside

            if inside:
                self.closest = t
