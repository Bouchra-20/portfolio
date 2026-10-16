package fil.sr1.TreeFtp;

import static org.junit.Assert.*;

import fil.sr1.tree.Node;
import org.junit.Before;
import org.junit.Test;

/**
 * Classe de test pour les nœuds de l'arbre.
 * @author Bouchra Bouamama
 */
public class NodeTest {
    private Node parent;
    private Node child;

    /**
     * Initialise l'environnement de test avant chaque méthode.
     */
    @Before
    public void setUp() {
        parent = new Node("parent");
        child = new Node("child");
    }

    /**
     * Vérifie que l'ajout d'un enfant met à jour correctement la liste
     * et l'état de feuille du nœud.
     */
    @Test
    public void testHierarchy() {
        assertTrue("Le nœud devrait être une feuille au départ", parent.isLeaf());
        parent.addChild(child);
        assertFalse("Le nœud ne devrait plus être une feuille", parent.isLeaf());
        assertEquals("Le nombre d'enfants est incorrect", 1, parent.getChildren().size());
        assertSame("L'enfant récupéré n'est pas celui ajouté", child, parent.getChildren().get(0));
    }
}