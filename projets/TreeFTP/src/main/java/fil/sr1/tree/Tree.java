package fil.sr1.tree;

/**
 * Structure de données représentant l'arborescence complète récupérée sur le serveur.
 * Elle sert de point d'entrée pour les différentes stratégies d'affichage.
 * * @author Bouchra Bouamama
 */
public class Tree {
    private Node root;

    /**
     * Initialise un arbre avec un nœud racine (nommé null ou représentant la racine distante).
     */
    public Tree() {
        this.root = new Node(null);
    }

    /**
     * @return Le nœud racine de l'arbre.
     */
    public Node getRoot() {
        return root;
    }
}

