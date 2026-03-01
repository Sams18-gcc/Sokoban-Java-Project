package sokoban.editor;

import java.util.Scanner;
public class LevelEditor {

    private char[][] grid;
    private int rows;
    private int cols;
    private int cursorRow;
    private int cursorCol;

    // Nom du monde (une lettre) et numéro de taille pour le format de fichier
    private char worldName;

    public LevelEditor(int rows, int cols, char worldName) {
        if (rows < 5 || cols < 5) throw new IllegalArgumentException("Taille minimale : 5x5");
        this.rows = rows;
        this.cols = cols;
        this.worldName = worldName;
        this.cursorRow = 1;
        this.cursorCol = 1;
        initEmpty();
    }

    private void initEmpty() {
        grid = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i == 0 || j == 0 || i == rows - 1 || j == cols - 1)
                    grid[i][j] = '#';
                else
                    grid[i][j] = ' ';
            }
        }
    }
    public void display() {
        System.out.println();
        System.out.print("    ");
        for (int j = 0; j < cols; j++) System.out.printf("%2d", j);
        System.out.println();

        for (int i = 0; i < rows; i++) {
            System.out.printf("%3d ", i);
            for (int j = 0; j < cols; j++) {
                if (i == cursorRow && j == cursorCol) {
                    System.out.print("[" + grid[i][j] + "]");
                } else {
                    System.out.print(" " + grid[i][j] + " ");
                }
            }
            System.out.println();
        }
        System.out.println();
        System.out.printf("Curseur : (%d, %d)  |  Case : '%c'%n", cursorRow, cursorCol, grid[cursorRow][cursorCol]);
        printHelp();
    }

    private void printHelp() {
        System.out.println("--- Commandes ---");
        System.out.println("Déplacer curseur : w(haut) s(bas) a(gauche) d(droite)");
        System.out.println("Placer : # mur | x cible | @ joueur | o boîte | . vide");
        System.out.println("Autres : n(nouvelle grille) | save | load | quit");
        System.out.print("> ");
    }

    /** deplace le curseur. */
    private void moveCursor(int dr, int dc) {
        int nr = cursorRow + dr;
        int nc = cursorCol + dc;
        if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
            cursorRow = nr;
            cursorCol = nc;
        }
    }

    /** place un element la position du curseur. */
    private void placeElement(char c) {
        grid[cursorRow][cursorCol] = c;
    }


    public String exportLevel() {
        // verification basiques
        boolean hasPlayer = false;
        int boxCount = 0, targetCount = 0;
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == '@') hasPlayer = true;
                if (grid[i][j] == 'o') boxCount++;
                if (grid[i][j] == 'x') targetCount++;
            }
        if (!hasPlayer) System.out.println("[AVERTISSEMENT] Pas de joueur (@) dans le niveau !");
        if (boxCount == 0) System.out.println("[AVERTISSEMENT] Pas de boîte (o) dans le niveau !");
        if (targetCount == 0) System.out.println("[AVERTISSEMENT] Pas de cible (x) dans le niveau !");
        if (boxCount != targetCount)
            System.out.println("[AVERTISSEMENT] Nombre de boîtes (" + boxCount + ") ≠ nombre de cibles (" + targetCount + ") !");

        // taille carrée attendue par le format — on prend max(rows, cols) pour le champ <taille>
        int size = Math.max(rows, cols);

        StringBuilder sb = new StringBuilder();
        sb.append(worldName).append(" ").append(size).append("\n");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char c = grid[i][j];
                // conversion vers le format standard Sokoban
                if (c == 'x') c = '.';
                else if (c == 'o') c = '$';
                sb.append(c);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public void importLevel(String text) {




    }

    /** boucle principale de l'diteur. */
    public void run() {
        Scanner sc = new Scanner(System.in);
        display();
        while (sc.hasNextLine()) {
            String input = sc.nextLine().trim();
            if (input.isEmpty()) { display(); continue; }

            switch (input) {
                case "w": moveCursor(-1, 0); break;
                case "s": moveCursor(1, 0); break;
                case "a": moveCursor(0, -1); break;
                case "d": moveCursor(0, 1); break;
                case "#": placeElement('#'); break;
                case "x": placeElement('x'); break;
                case "@": placeElement('@'); break;
                case "o": placeElement('o'); break;
                case ".": placeElement(' '); break;
                case " ": placeElement(' '); break;
                case "quit": System.out.println("À bientôt !"); return;
                case "save":
                    System.out.println("=== DÉBUT DU NIVEAU (copier dans un fichier .txt) ===");
                    System.out.println(exportLevel());
                    System.out.println("=== FIN DU NIVEAU ===");
                    break;
                case "load":
                    System.out.println("Collez le niveau (terminez avec une ligne contenant 'END') :");
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while (sc.hasNextLine() && !(line = sc.nextLine()).equals("END")) {
                        sb.append(line).append("\n");
                    }
                    importLevel(sb.toString());
                    break;
                case "n":
                    System.out.print("Nombre de lignes (min 5) : ");
                    int r = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Nombre de colonnes (min 5) : ");
                    int c2 = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Nom du monde (une lettre) : ");
                    char name = sc.nextLine().trim().charAt(0);
                    this.rows = r; this.cols = c2; this.worldName = name;
                    initEmpty();
                    cursorRow = 1; cursorCol = 1;
                    System.out.println("Nouvelle grille créée !");
                    break;
                default:
                    System.out.println("Commande inconnue : '" + input + "'");
            }
            display();
        }
    }
}
