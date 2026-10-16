package fil.sr1.tree.printers;

import java.util.List;
import java.util.ArrayList;

import fil.sr1.tree.Tree;
import fil.sr1.tree.Node;

/**
 * Stratégie d'affichage utilisant un parcours en profondeur (DFS - Depth First Search).
 * Produit une sortie visuelle similaire à la commande shell "tree" de Linux.
 * * @author Bouchra Bouamama
 */
public class DFSPrintStrategy extends PrintTreeStrategy {

    /**
     * Déclenche la conversion récursive de l'arbre en format texte structuré.
     * * @param tree L'arbre à parcourir.
     * @return La chaîne formatée représentant l'arbre.
     */
    @Override
    public String treeToString(Tree tree) {
        return recursiveTreeToString(tree.getRoot(), "");
    }

    /**
     * Parcourt récursivement les nœuds pour construire la structure visuelle avec des préfixes.
     * * @param node Le nœud actuel.
     * @param prefix La chaîne de caractères de retrait pour l'alignement visuel.
     * @return La représentation du sous-arbre actuel.
     */
    private String recursiveTreeToString(Node node, String prefix) {
        StringBuilder string = new StringBuilder();
        List<Node> children = new ArrayList<>(node.getChildren());
        int size = children.size();
        for (int i = 0; i < size; i++) {
            Node child = children.get(i);
            System.out.println(prefix + (i == size - 1 ? "└── " : "├── ") + child.getName());
            if (!node.isLeaf()) {
                recursiveTreeToString(child, prefix + (i == size - 1 ? "    " : "│   "));
            }
        }
        return string.toString();
    }
}
