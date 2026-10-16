package fil.sr1.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un nœud dans l'arborescence de fichiers.
 * Chaque nœud possède un nom et une liste de nœuds enfants (fichiers ou dossiers).
 * * @author Bouchra Bouamama
 */
public class Node {
    private String name;
    private List<Node> children;

    /**
     * Initialise un nouveau nœud avec le nom spécifié.
     * @param name Le nom du fichier ou du répertoire.
     */
    public Node(String name) {
        this.name = name;
        this.children = new ArrayList<>();
    }

    /**
     * Ajoute un nœud enfant à ce nœud.
     * @param node Le nœud enfant à ajouter.
     */
    public void addChild(Node node) {
        this.children.add(node);
    }

    /**
     * @return Le nom du nœud.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return La liste des nœuds enfants.
     */
    public List<Node> getChildren() {
        return this.children;
    }

    /**
     * Indique si le nœud est une feuille (n'a pas d'enfants).
     * Dans le contexte FTP, une feuille est généralement un fichier simple.
     * @return true si le nœud n'a pas d'enfants, false sinon.
     */
    public boolean isLeaf() {
        return this.children.isEmpty();
    }
}

