package fil.sr1.TreeFtp;

import static org.junit.Assert.*;

import fil.sr1.tree.printers.JSONPrintStrategy;
import org.junit.Test;
import fil.sr1.tree.Tree;
import fil.sr1.tree.Node;

/**
 * Vérifie le formattage de la sortie JSON.
 * @author Bouchra Bouamama
 */
public class JSONStrategyTest {

    @Test
    public void testJSONFormat() {
        Tree tree = new Tree();
        tree.getRoot().addChild(new Node("test.txt"));

        JSONPrintStrategy strategy = new JSONPrintStrategy();
        String output = strategy.treeToString(tree);

        assertTrue("Le JSON doit être une liste (commencer par [)", output.trim().startsWith("["));
        assertTrue("Le JSON doit contenir le nom du fichier", output.contains("test.txt"));
    }
}