package fil.sr1.tree.printers;

import fil.sr1.tree.Tree;

/**
 * Classe abstraite définissant le contrat pour les stratégies d'affichage de l'arborescence.
 * Suit le patron de conception (Design Pattern) Strategy pour permettre une extension facile
 * des formats de sortie (Texte, JSON, etc.).
 * * @author Bouchra Bouamama
 */
public abstract class PrintTreeStrategy {
    /**
     * Convertit un objet Tree en une représentation textuelle selon la stratégie choisie.
     * @param tree L'arborescence à convertir.
     * @return Une chaîne de caractères représentant l'arbre.
     */
    public abstract String treeToString(Tree tree);

    /**
     * Affiche la représentation textuelle de l'arbre sur la sortie standard.
     * @param tree L'arborescence à afficher.
     */
    public void printTree(Tree tree) {
        System.out.println(treeToString(tree));
    }
}
