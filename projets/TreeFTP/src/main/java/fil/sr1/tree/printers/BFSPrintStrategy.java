package fil.sr1.tree.printers;

import java.util.ArrayList;
import java.util.List;

import fil.sr1.tree.Node;
import fil.sr1.tree.Tree;

/**
 * Stratégie d'affichage utilisant un parcours en largeur (BFS - Breadth First Search).
 * Affiche les éléments de l'arborescence niveau par niveau.
 * * @author Bouchra Bouamama
 */
public class BFSPrintStrategy extends PrintTreeStrategy {

    /**
     * Parcourt l'arbre niveau par niveau à l'aide d'une file (Queue).
     * * @param tree L'arbre à parcourir.
     * @return Une chaîne représentant l'arbre étage par étage.
     */
    @Override
    public String treeToString(Tree tree) {
        StringBuilder string = new StringBuilder();
        List<Node> queue = new ArrayList<>(tree.getRoot().getChildren());

        int deepth = 0;
        while (!queue.isEmpty()) {
            int levelSize = queue.size();

            string.append("Niveau " + deepth + ":\n");
            string.append("\t");

            for (int i = 0; i < levelSize; i++) {
                Node node = queue.remove(0);
                String name = node.getName();

                if (!node.isLeaf()) {
                    string.append("Dossier: \"" + name + "\"");
                    for (Node child : node.getChildren()) {
                        queue.add(child);
                    }
                } else {
                    string.append("Fichier: \"" + name + "\"");
                }
                if (i < levelSize - 1) {
                    string.append(" | ");
                }
            }

            string.append("\n");
            deepth++;
        }
        return string.toString();
    }
}
