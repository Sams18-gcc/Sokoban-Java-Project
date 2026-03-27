package sokoban.editor;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class LevelEditor {

    private char[][] grid;
    private int rows;
    private int cols;
    private int cursorRow;
    private int cursorCol;
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
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++) {
                if (i == 0 || j == 0 || i == rows-1 || j == cols-1)
                    grid[i][j] = '#';
                else
                    grid[i][j] = ' ';
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
                if (i == cursorRow && j == cursorCol)
                    System.out.print("[" + grid[i][j] + "]");
                else
                    System.out.print(" " + grid[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
        System.out.printf("Curseur : (%d, %d)  |  Case : '%c'%n",
                cursorRow, cursorCol, grid[cursorRow][cursorCol]);
        printHelp();
    }

    private void printHelp() {
        System.out.println("--- Commandes ---");
        System.out.println("Deplacer : w/a/s/d");
        System.out.println("Placer : # mur | x cible | @ joueur | O boite | e sortie | . vide");
        System.out.println("Fichier : save | load | savefile | loadfile | quit");
        System.out.println("Autres : n(nouvelle grille) | validate | export");
        System.out.print("> ");
    }

    private void moveCursor(int dr, int dc) {
        int nr = cursorRow + dr;
        int nc = cursorCol + dc;
        if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
            cursorRow = nr;
            cursorCol = nc;
        }
    }

    private void placeElement(char c) {
        setCell(cursorRow, cursorCol, c);
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public char getWorldName() { return worldName; }
    public int getCursorRow() { return cursorRow; }
    public int getCursorCol() { return cursorCol; }

    public char getCell(int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= cols) return ' ';
        return grid[r][c];
    }

    // les bordures restent des murs quoi qu'il arrive
    public void setCell(int r, int c, char ch) {
        if (r >= 0 && r < rows && c >= 0 && c < cols) {
            if (r == 0 || c == 0 || r == rows-1 || c == cols-1)
                grid[r][c] = '#';
            else
                grid[r][c] = ch;
        }
    }

    public boolean borduresIntactes() {
        for (int i = 0; i < rows; i++)
            if (grid[i][0] != '#' || grid[i][cols-1] != '#') return false;
        for (int j = 0; j < cols; j++)
            if (grid[0][j] != '#' || grid[rows-1][j] != '#') return false;
        return true;
    }

    public void setCursor(int r, int c) {
        if (r >= 0 && r < rows && c >= 0 && c < cols) {
            cursorRow = r;
            cursorCol = c;
        }
    }

    public char[][] getGrid() {
        char[][] copy = new char[rows][cols];
        for (int i = 0; i < rows; i++)
            System.arraycopy(grid[i], 0, copy[i], 0, cols);
        return copy;
    }

    public void setGrid(char[][] newGrid) {
        this.rows = newGrid.length;
        this.cols = newGrid[0].length;
        this.grid = new char[rows][cols];
        for (int i = 0; i < rows; i++)
            System.arraycopy(newGrid[i], 0, grid[i], 0, cols);
        cursorRow = Math.min(cursorRow, rows - 1);
        cursorCol = Math.min(cursorCol, cols - 1);
    }

    public void resize(int newRows, int newCols, char wName) {
        if (newRows < 5 || newCols < 5) throw new IllegalArgumentException("Taille minimale : 5x5");
        this.rows = newRows;
        this.cols = newCols;
        this.worldName = wName;
        this.cursorRow = 1;
        this.cursorCol = 1;
        initEmpty();
    }

    // check joueur, boites, cibles
    public ArrayList<String> validate() {
        ArrayList<String> warnings = new ArrayList<>();
        boolean hasPlayer = false;
        int boxCount = 0, targetCount = 0;

        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++) {
                char c = grid[i][j];
                if (c == '@') {
                    if (hasPlayer) warnings.add("Plusieurs joueurs (@) detectes !");
                    hasPlayer = true;
                }
                if (c == 'O') boxCount++;
                if (c == 'x') targetCount++;
            }

        if (!hasPlayer) warnings.add("Pas de joueur (@)");
        if (boxCount == 0) warnings.add("Pas de boite (O)");
        if (targetCount == 0) warnings.add("Pas de cible (x)");
        if (boxCount != targetCount)
            warnings.add("Boites (" + boxCount + ") != cibles (" + targetCount + ")");

        return warnings;
    }

    // export sokoban standard avec header
    public String exportLevel() {
        ArrayList<String> warnings = validate();
        for (String w : warnings) System.out.println("[!] " + w);

        int size = Math.max(rows, cols);
        StringBuilder sb = new StringBuilder();
        sb.append(worldName).append(" ").append(size).append("\n");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char c = grid[i][j];
                if (c == 'x') c = '.';
                else if (c == 'O') c = '$';
                sb.append(c);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // import sokoban standard
    public void importLevel(String text) {
        if (text == null || text.trim().isEmpty()) {
            System.out.println("[ERREUR] Texte vide.");
            return;
        }

        String[] lines = text.split("\\r?\\n");
        if (lines.length < 2) {
            System.out.println("[ERREUR] Format invalide.");
            return;
        }

        String[] parts = lines[0].trim().split("\\s+");
        if (parts.length >= 2)
            worldName = parts[0].charAt(0);

        ArrayList<String> gridLines = new ArrayList<>();
        for (int i = 1; i < lines.length; i++)
            if (!lines[i].isEmpty()) gridLines.add(lines[i]);

        if (gridLines.isEmpty()) {
            System.out.println("[ERREUR] Pas de grille.");
            return;
        }

        int newRows = gridLines.size();
        int newCols = 0;
        for (String l : gridLines) newCols = Math.max(newCols, l.length());

        if (newRows < 5 || newCols < 5) {
            System.out.println("[ERREUR] Trop petit.");
            return;
        }

        this.rows = newRows;
        this.cols = newCols;
        grid = new char[rows][cols];

        for (int i = 0; i < rows; i++) {
            String line = gridLines.get(i);
            for (int j = 0; j < cols; j++) {
                if (j < line.length()) {
                    char c = line.charAt(j);
                    switch (c) {
                        case '$': c = 'O'; break;
                        case '.': c = 'x'; break;
                        case '*': c = 'O'; break;
                        case '+': c = '@'; break;
                    }
                    grid[i][j] = c;
                } else {
                    grid[i][j] = ' ';
                }
            }
        }
        cursorRow = Math.min(1, rows - 1);
        cursorCol = Math.min(1, cols - 1);
        System.out.println("Importé ! (" + rows + "x" + cols + ")");
    }

    // format projet brut (memes symboles que World.java)
    public String exportProjectFormat() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++)
                sb.append(grid[i][j]);
            sb.append("\n");
        }
        return sb.toString();
    }

    // import worldN.txt brut
    public void importProjectFormat(String text) {
        if (text == null || text.trim().isEmpty()) {
            System.out.println("[ERREUR] Texte vide.");
            return;
        }

        String[] lines = text.split("\\r?\\n");
        ArrayList<String> gridLines = new ArrayList<>();
        for (String l : lines)
            if (!l.isEmpty()) gridLines.add(l);

        if (gridLines.isEmpty()) return;

        int newRows = gridLines.size();
        int newCols = 0;
        for (String l : gridLines) newCols = Math.max(newCols, l.length());

        if (newRows < 5 || newCols < 5) {
            System.out.println("[ERREUR] Trop petit.");
            return;
        }

        this.rows = newRows;
        this.cols = newCols;
        grid = new char[rows][cols];

        for (int i = 0; i < rows; i++) {
            String line = gridLines.get(i);
            for (int j = 0; j < cols; j++)
                grid[i][j] = (j < line.length()) ? line.charAt(j) : ' ';
        }
        cursorRow = Math.min(1, rows - 1);
        cursorCol = Math.min(1, cols - 1);
    }

    // sauvegarde dans levels/levelN/worldN.txt
    // refuse si invalide, calcule nbWorlds auto
    public boolean saveToProjectFile(int levelNum, int worldIndex, int totalWorlds) {
        ArrayList<String> warnings = validate();
        if (!warnings.isEmpty()) {
            for (String w : warnings) System.out.println("[ERREUR] " + w);
            System.out.println("Sauvegarde annulee.");
            return false;
        }
        if (!borduresIntactes()) {
            System.out.println("[ERREUR] Bordures cassees.");
            return false;
        }

        File levelDir = new File("levels/level" + levelNum);
        if (!levelDir.exists()) levelDir.mkdirs();

        File worldFile = new File(levelDir, "world" + worldIndex + ".txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(worldFile))) {
            pw.print(exportProjectFormat());
            System.out.println("Sauvegardé : " + worldFile.getPath());
        } catch (IOException e) {
            System.out.println("[ERREUR] " + e.getMessage());
            return false;
        }

        // compter les worldN.txt existants
        int realCount = 0;
        File[] files = levelDir.listFiles();
        if (files != null)
            for (File f : files)
                if (f.getName().matches("world\\d+\\.txt"))
                    realCount++;

        File nbFile = new File(levelDir, "nbWorlds.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(nbFile))) {
            pw.println(realCount);
        } catch (IOException e) {
            System.out.println("[ERREUR] " + e.getMessage());
        }
        return true;
    }

    public boolean loadFromProjectFile(int levelNum, int worldIndex) {
        File worldFile = new File("levels/level" + levelNum + "/world" + worldIndex + ".txt");
        if (!worldFile.exists()) {
            System.out.println("[ERREUR] Pas trouvé : " + worldFile.getPath());
            return false;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(worldFile))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null)
                sb.append(line).append("\n");
            importProjectFormat(sb.toString());
            System.out.println("Chargé : " + worldFile.getPath());
            return true;
        } catch (IOException e) {
            System.out.println("[ERREUR] " + e.getMessage());
            return false;
        }
    }

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
                case "O": placeElement('O'); break;
                case "e": placeElement('e'); break;
                case ".": placeElement(' '); break;
                case " ": placeElement(' '); break;
                case "quit": System.out.println("A bientot !"); return;

                case "validate":
                    ArrayList<String> warnings = validate();
                    if (warnings.isEmpty())
                        System.out.println("Niveau valide !");
                    else
                        for (String w2 : warnings) System.out.println("[!] " + w2);
                    break;

                case "export":
                    System.out.println("=== FORMAT STANDARD ===");
                    System.out.println(exportLevel());
                    System.out.println("=== FORMAT PROJET ===");
                    System.out.println(exportProjectFormat());
                    break;

                case "save":
                    System.out.println("=== NIVEAU ===");
                    System.out.println(exportLevel());
                    System.out.println("=== FIN ===");
                    break;

                case "savefile":
                    System.out.print("Numero du level : ");
                    int ln = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Index du monde : ");
                    int wi = Integer.parseInt(sc.nextLine().trim());
                    if (!saveToProjectFile(ln, wi, 0))
                        System.out.println("Echec.");
                    break;

                case "loadfile":
                    System.out.print("Numero du level : ");
                    int ln2 = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Index du monde : ");
                    int wi2 = Integer.parseInt(sc.nextLine().trim());
                    loadFromProjectFile(ln2, wi2);
                    break;

                case "load":
                    System.out.println("Collez le niveau (terminez avec 'END') :");
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while (sc.hasNextLine() && !(line = sc.nextLine()).equals("END"))
                        sb.append(line).append("\n");
                    importLevel(sb.toString());
                    break;

                case "n":
                    System.out.print("Lignes (min 5) : ");
                    int r = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Colonnes (min 5) : ");
                    int c2 = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Monde (1 lettre) : ");
                    char name = sc.nextLine().trim().charAt(0);
                    resize(r, c2, name);
                    System.out.println("Nouvelle grille !");
                    break;

                default:
                    System.out.println("Commande inconnue : '" + input + "'");
            }
            display();
        }
    }
}
