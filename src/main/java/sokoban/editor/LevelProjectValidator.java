package sokoban.editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// validateur de niveaux Sokoban au format projet.
// Vérifie la cohérence d'un monde individuel (validateWorldGrid)
// ou d'un niveau complet avec tous ses mondes (validateProjectLevel).
// Fournit aussi un rapport de distribution de l'arbre (buildDistributionReport).
public final class LevelProjectValidator {

    private LevelProjectValidator() {}

    /*--------------------------------------------------
                VALIDATION D'UN MONDE
    --------------------------------------------------*/

    // vérifie qu'une grille individuelle est valide :
    // bordures fermées, un seul joueur, autant de boîtes que de cibles, etc.
    // Retourne une liste de warnings (vide si valide).
    public static ArrayList<String> validateWorldGrid(char[][] grid) {
        ArrayList<String> warnings = new ArrayList<>();
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            warnings.add("Grille vide.");
            return warnings;
        }

        int rows        = grid.length;
        int cols        = grid[0].length;
        int playerCount = 0;
        int boxCount    = 0;
        int targetCount = 0;
        int portalCount = 0;
        int exitCount   = 0;

        for (int i = 0; i < rows; i++) {
            if (grid[i].length != cols) {
                warnings.add("Toutes les lignes doivent avoir la même longueur.");
                return warnings;
            }
            for (int j = 0; j < cols; j++) {
                char c = grid[i][j];
                if (c == '@') playerCount++;
                if (c == 'O' || c == 'P') boxCount++;
                if (c == 'P') portalCount++;
                if (c == 'x') targetCount++;
                if (c == 'e') exitCount++;

                // la bordure doit être entièrement composée de murs
                if (i == 0 || j == 0 || i == rows - 1 || j == cols - 1) {
                    if (c != '#') {
                        warnings.add("La bordure doit être entièrement fermée par des murs (#).");
                        return warnings;
                    }
                }
            }
        }

        if (playerCount == 0) warnings.add("Pas de joueur (@) dans le monde.");
        if (playerCount > 1)  warnings.add("Plusieurs joueurs (@) détectés dans le même monde.");
        if (boxCount    == 0) warnings.add("Pas de boîte dans le monde (O ou P).");
        if (targetCount == 0) warnings.add("Pas de cible (x) dans le monde.");
        if (boxCount != targetCount)
            warnings.add("Nombre total de boîtes (O + P = " + boxCount
                    + ") différent du nombre de cibles x (" + targetCount + ").");
        if (portalCount > 0 && targetCount < portalCount)
            warnings.add("Il faut au moins autant de cibles que de PortalBox pour pouvoir les ouvrir.");
        if (exitCount > 1)
            warnings.add("Plusieurs sorties (e) détectées dans le même monde.");

        return warnings;
    }

    /*--------------------------------------------------
                VALIDATION D'UN NIVEAU COMPLET
    --------------------------------------------------*/

    // vérifie qu'un dossier de niveau est cohérent :
    // nbWorlds.txt présent, tous les worldN.txt présents et valides,
    // et tous les mondes accessibles depuis world0 via l'arbre récursif.
    // Retourne une liste de warnings (vide si valide).
    public static ArrayList<String> validateProjectLevel(File levelDir) {
        ArrayList<String> warnings = new ArrayList<>();
        if (levelDir == null || !levelDir.exists() || !levelDir.isDirectory()) {
            warnings.add("Dossier de niveau introuvable : " + (levelDir == null ? "null" : levelDir.getPath()));
            return warnings;
        }

        File nbWorldsFile = new File(levelDir, "nbWorlds.txt");
        if (!nbWorldsFile.exists()) { warnings.add("nbWorlds.txt manquant."); return warnings; }

        int totalWorlds = readNbWorlds(nbWorldsFile, warnings);
        if (totalWorlds <= 0) return warnings;

        ArrayList<char[][]> worlds     = new ArrayList<>();
        int                 totalPortalCount = 0;

        for (int i = 0; i < totalWorlds; i++) {
            File worldFile = new File(levelDir, "world" + i + ".txt");
            if (!worldFile.exists()) {
                warnings.add("Fichier manquant : world" + i + ".txt");
                return warnings;
            }
            try {
                char[][] grid = readGrid(worldFile);
                worlds.add(grid);
                ArrayList<String> worldWarnings = validateWorldGrid(grid);
                for (String w : worldWarnings) warnings.add("world" + i + " : " + w);
                totalPortalCount += countChar(grid, 'P');
            } catch (IOException e) {
                warnings.add("Impossible de lire world" + i + ".txt : " + e.getMessage());
                return warnings;
            }
        }

        if (totalWorlds == 1 && totalPortalCount > 0)
            warnings.add("Le niveau ne contient qu'un monde : les PortalBox seront inutilisées.");

        // simule la construction de l'arbre pour détecter les mondes inaccessibles
        ArrayList<Integer> attachedOrder = simulateAttachment(worlds);
        if (attachedOrder.size() != totalWorlds) {
            Set<Integer>     reached = new HashSet<>(attachedOrder);
            ArrayList<Integer> missing = new ArrayList<>();
            for (int i = 0; i < totalWorlds; i++)
                if (!reached.contains(i)) missing.add(i);
            warnings.add("Mondes inaccessibles depuis world0 : " + missing
                    + ". Ajoutez des PortalBox dans les mondes parents ou supprimez ces mondes.");
        }

        if (totalWorlds > 0 && countChar(worlds.get(0), '@') != 1)
            warnings.add("world0 doit contenir exactement un joueur pour démarrer correctement.");

        return warnings;
    }

    /*--------------------------------------------------
                RAPPORT DE DISTRIBUTION
    --------------------------------------------------*/

    // construit un rapport textuel de la distribution DFS de l'arbre de mondes
    public static String buildDistributionReport(File levelDir) {
        ArrayList<String> warnings      = new ArrayList<>();
        File              nbWorldsFile  = new File(levelDir, "nbWorlds.txt");
        int               totalWorlds   = readNbWorlds(nbWorldsFile, warnings);
        if (totalWorlds <= 0) return String.join("\n", warnings);

        ArrayList<char[][]> worlds = new ArrayList<>();
        for (int i = 0; i < totalWorlds; i++) {
            File worldFile = new File(levelDir, "world" + i + ".txt");
            try {
                worlds.add(readGrid(worldFile));
            } catch (IOException e) {
                return "Impossible de lire world" + i + ".txt : " + e.getMessage();
            }
        }

        StringBuilder sb        = new StringBuilder();
        int[]         nextIndex = {1};
        sb.append("Distribution DFS des mondes\n");
        appendDistribution(sb, worlds, 0, 0, nextIndex);

        if (nextIndex[0] < totalWorlds) {
            sb.append("Mondes non utilisés après construction : ");
            for (int i = nextIndex[0]; i < totalWorlds; i++) {
                if (i > nextIndex[0]) sb.append(", ");
                sb.append("world").append(i);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /*--------------------------------------------------
                METHODES INTERNES
    --------------------------------------------------*/

    // affiche récursivement l'arbre de mondes avec indentation
    private static void appendDistribution(StringBuilder sb, List<char[][]> worlds,
                                           int worldIndex, int depth, int[] nextIndex) {
        indent(sb, depth).append("world").append(worldIndex)
                .append(" (PortalBox: ").append(countChar(worlds.get(worldIndex), 'P'))
                .append(")\n");

        int portalCount = countChar(worlds.get(worldIndex), 'P');
        for (int portal = 1; portal <= portalCount && nextIndex[0] < worlds.size(); portal++) {
            int child = nextIndex[0]++;
            indent(sb, depth + 1).append("portal ").append(portal)
                    .append(" -> world").append(child).append("\n");
            appendDistribution(sb, worlds, child, depth + 2, nextIndex);
        }
    }

    // ajoute `depth * 2` espaces d'indentation
    private static StringBuilder indent(StringBuilder sb, int depth) {
        for (int i = 0; i < depth; i++) sb.append("  ");
        return sb;
    }

    // simule la construction récursive de l'arbre et retourne les mondes atteints dans l'ordre
    private static ArrayList<Integer> simulateAttachment(List<char[][]> worlds) {
        ArrayList<Integer> reached   = new ArrayList<>();
        if (worlds.isEmpty()) return reached;
        int[] nextIndex = {1};
        simulateRecursive(worlds, 0, nextIndex, reached);
        return reached;
    }

    // DFS récursif de simulation
    private static void simulateRecursive(List<char[][]> worlds, int worldIndex,
                                          int[] nextIndex, ArrayList<Integer> reached) {
        reached.add(worldIndex);
        int portalCount = countChar(worlds.get(worldIndex), 'P');
        for (int portal = 0; portal < portalCount && nextIndex[0] < worlds.size(); portal++) {
            int child = nextIndex[0]++;
            simulateRecursive(worlds, child, nextIndex, reached);
        }
    }

    // lit le nombre de mondes depuis nbWorlds.txt ; ajoute un warning et retourne -1 si invalide
    private static int readNbWorlds(File nbWorldsFile, ArrayList<String> warnings) {
        if (nbWorldsFile == null || !nbWorldsFile.exists()) {
            warnings.add("nbWorlds.txt manquant.");
            return -1;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(nbWorldsFile))) {
            String line = reader.readLine();
            if (line == null) { warnings.add("nbWorlds.txt est vide."); return -1; }
            int total = Integer.parseInt(line.trim());
            if (total <= 0) warnings.add("nbWorlds doit être > 0.");
            return total;
        } catch (Exception e) {
            warnings.add("nbWorlds.txt invalide : " + e.getMessage());
            return -1;
        }
    }

    // lit un fichier worldN.txt et le convertit en char[][]
    private static char[][] readGrid(File file) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null)
                if (!line.isEmpty()) lines.add(line);
        }
        if (lines.isEmpty()) throw new IOException("grille vide");
        int rows = lines.size();
        int cols = lines.get(0).length();
        char[][] grid = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            if (lines.get(i).length() != cols)
                throw new IOException("lignes de tailles différentes dans " + file.getName());
            for (int j = 0; j < cols; j++)
                grid[i][j] = lines.get(i).charAt(j);
        }
        return grid;
    }

    // compte les occurrences d'un caractère dans une grille
    private static int countChar(char[][] grid, char target) {
        int count = 0;
        for (char[] chars : grid)
            for (char c : chars)
                if (c == target) count++;
        return count;
    }
}
