package fil.sr1.tree.printers;

import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

import fil.sr1.tree.Node;
import fil.sr1.tree.Tree;

/**
 * Stratégie d'affichage convertissant l'arborescence en un document JSON.
 * Cette structure est idéale pour l'exportation ou le traitement par d'autres applications.
 * @author Bouchra Bouamama
 */
public class JSONPrintStrategy extends PrintTreeStrategy {

    /**
     * Convertit l'arbre en format JSON.
     * @param tree L'arbre à convertir.
     * @return Une chaîne formatée au standard JSON.
     */
    @Override
    public String treeToString(Tree tree) {
        return recursiveTreeToString(tree.getRoot());
    }

    /**
     * Construit récursivement le contenu JSON pour chaque nœud et ses enfants.
     * @param node Le nœud actuel à traiter.
     * @return Un fragment de chaîne JSON.
     */
    private String recursiveTreeToString(Node node) {

        StringBuilder json = new StringBuilder();
        List<Node> children = new ArrayList<>(node.getChildren());
        int size = children.size();

        json.append("[\n");

        for (int i = 0; i < size; i++) {
            Node child = children.get(i);

            json.append("\t{");
            json.append("\n\t\t\"name\": \"" + child.getName() + "\",");
            json.append("\n\t\t\"type\": ");

            if (!child.isLeaf()) {
                json.append("\"directory\",");
                json.append("\n\t\t\"children\": " + recursiveTreeToString(child));
            } else {
                json.append("\"file\"");
            }

            json.append("\n\t}");

            if (i < size - 1) {
                json.append(",");
            }

            json.append("\n");
        }

        json.append("]");
        return json.toString();
    }

    /**
     * Sauvegarde le résultat JSON dans un fichier physique dans le répertoire courant.
     * @param json La chaîne JSON à écrire.
     * @param fileName Le nom du fichier cible (ex: output.json).
     */
    public void writeJsonToFile(String json, String fileName) {
        try {
            Files.write(Paths.get(System.getProperty("user.dir"), fileName), json.getBytes());
            System.out.println("JSON écrit dans le fichier : " + fileName);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture du JSON dans le fichier : " + e.getMessage());
        }
    }
}
