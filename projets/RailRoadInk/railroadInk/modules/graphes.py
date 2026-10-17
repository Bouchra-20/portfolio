import sys
import os
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..')))

from railroadInk.composants.board import Board

class Graphes :
    def __init__(self):
        self.edge = []

    def add_edge(self, coord1, coord2, road_type):
        self.edge.append((coord1, coord2, road_type))

    def create_graphe(self, player, type=None):
        """Crée le graphe en connectant les tuiles compatibles."""
        tuiles = [(i, j) for i in range(7) for j in range(7) if player.getBoard().getTile(i, j) is not None]
        sommets_fixes = [(2, 0), (4, 0), (-1, 8), (9, 0), (9, 4), (9, 8)]
        sommets = tuiles + sommets_fixes

        for i, j in tuiles:
            for k, l in tuiles:
                if player.getBoard().tiles_compatible(i, j, k, l):
                    self.add_edge((i, j), (k, l), 1) 



    def longest_path(self):
        """Trouve le plus long chemin dans le graphe en utilisant DFS avec backtracking."""
        max_path = []  # Stocke le plus long chemin trouvé
        graph = self._build_adjacency_list()  # Convertir la liste des arêtes en dictionnaire d'adjacence

        def dfs(node, path, visited):
            nonlocal max_path
            if len(path) > len(max_path):
                max_path = path[:]  # Sauvegarde du plus long chemin

            for neighbor, _ in graph.get(node, []):
                if neighbor not in visited:  # Évite de revisiter un nœud dans un même chemin
                    visited.add(neighbor)
                    dfs(neighbor, path + [neighbor], visited)
                    visited.remove(neighbor)  # Backtracking

        # Lancer DFS à partir de chaque sommet
        for start_node in graph:
            dfs(start_node, [start_node], {start_node})

        return max_path
    

    def _build_adjacency_list(self):
        """Transforme la liste des arêtes en un dictionnaire d'adjacence."""
        adjacency_list = {}
        for coord1, coord2, road_type in self.edge:
            if coord1 not in adjacency_list:
                adjacency_list[coord1] = []
            if coord2 not in adjacency_list:
                adjacency_list[coord2] = []
            adjacency_list[coord1].append((coord2, road_type))
            adjacency_list[coord2].append((coord1, road_type))  # Graphe non orienté
        return adjacency_list
    

    def get_routes(self):
        """Retourne la liste des routes sous forme d'arêtes (avec type de route)."""
        return self.edge


def main():

    graphe = Graphes()
    graphe.add_edge((0, 0), (0, 1), 1)
    graphe.add_edge((0, 1), (1, 1), 1)
    graphe.add_edge((1, 1), (2, 1), 1)
    graphe.add_edge((2, 1), (3, 1), 1)
    graphe.add_edge((3, 1), (1, 1), 1)  # Ajout d'un cycle

    longest = graphe.longest_path()
    print("Chemin le plus long :", longest)
    #print("Nombre de sommets :", len(longest))
    print("Routes :", graphe.get_routes())

        
if __name__ == "__main__":
    main()