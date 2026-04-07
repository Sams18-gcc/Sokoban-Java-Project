package sokoban.editor;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

// éditeur de grille Sokoban utilisable en terminal ou via EditorGUI.
// Gère la saisie des éléments, le déplacement du curseur, la sauvegarde
// et le chargement au format projet (worldN.txt) ou format Sokoban standard.
public class LevelEditor {

    /*--------------------------------------------------
                        ATTRIBUTS
    --------------------------------------------------*/
    private char[][] grid;
    private int      rows;
    private int      cols;
    private int      cursorRow;
    private int      cursorCol;
    private char     worldName;

    /*--------------------------------------------------
                        CONSTRUCTEUR
    --------------------------------------------------*/
    public LevelEditor(int rows, int cols, char worldName) {
        if (rows < 5 || cols < 5) throw new IllegalArgumentException("Taille minimale : 5x5");
        this.rows      = rows;
        this.cols      = cols;
        this.worldName = worldName;
        this.cursorRow = 1;
        this.cursorCol = 1;
        initEmpty();
    }

    /*--------------------------------------------------
                        GETTERS
    --------------------------------------------------*/
    public int  getRows()      { return rows;      }
    public int  getCols()      { return cols;      }
    public char getWorldName() { return worldName; }
    public int  getCursorRow() { return cursorRow; }
    public int  getCursorCol() { return cursorCol; }

    // retourne le caractère à la position (r, c), ou ' ' si hors limites
    public char getCell(int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= cols) return ' ';
        return grid[r][c];
    }

    // retourne une copie complète de la grille
    public char[][] getGrid() {
        char[][] copy = new char[rows][cols];
        for (int i = 0; i < rows; i++)
            System.arraycopy(grid[i], 0, copy[i], 0, cols);
        return copy;
    }

    /*--------------------------------------------------
                        SETTERS
    --------------------------------------------------*/

    // place un caractère à la position (r, c) si elle est valide
    public void setCell(int r, int c, char ch) {
        if (r >= 0 && r < rows && c >= 0 && c < cols)
            grid[r][c] = ch;
    }

    // déplace le curseur vers (r, c) si la position est valide
    public void setCursor(int r, int c) {
        if (r >= 0 && r < rows && c >= 0 && c < cols) {
            cursorRow = r;
            cursorCol = c;
        }
    }

    // remplace toute la grille par newGrid et ajuste les dimensions et le curseur
    public void setGrid(char[][] newGrid) {
        this.rows = newGrid.length;
        this.cols = newGrid[0].length;
        this.grid = new char[rows][cols];
        for (int i = 0; i < rows; i++)
            System.arraycopy(newGrid[i], 0, grid[i], 0, cols);
        cursorRow = Math.min(cursorRow, rows - 1);
        cursorCol = Math.min(cursorCol, cols - 1);
    }

    /*--------------------------------------------------
                        METHODES — INITIALISATION
    --------------------------------------------------*/

    // crée une grille vide entourée de murs
    private void initEmpty() {
        grid = new char[rows][cols];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                grid[i][j] = (i == 0 || j == 0 || i == rows - 1 || j == cols - 1) ? '#' : ' ';
    }

    // recrée une grille de dimensions différentes et repositionne le curseur
    public void resize(int newRows, int newCols, char wName) {
        if (newRows < 5 || newCols < 5) throw new IllegalArgumentException("Taille minimale : 5x5");
        this.rows      = newRows;
        this.cols      = newCols;
        this.worldName = wName;
        this.cursorRow = 1;
        this.cursorCol = 1;
        initEmpty();
    }

    /*--------------------------------------------------
                        METHODES — TERMINAL
    --------------------------------------------------*/

    // affiche la grille avec le curseur mis en évidence
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
        System.out.printf("Curseur : (%d, %d)  |  Case : '%c'%n", cursorRow, cursorCol, grid[cursorRow][cursorCol]);
        printHelp();
    }

    // affiche les commandes disponibles
    private void printHelp() {
        System.out.println("--- Commandes ---");
        System.out.println("Déplacer curseur : w(haut) s(bas) a(gauche) d(droite)");
        System.out.println("Placer : # mur | x cible | @ joueur | O boîte | P portalBox | e sortie | . vide");
        System.out.println("Fichier : save | load | savefile | loadfile | quit");
        System.out.println("Autres : n(nouvelle grille) | validate | validatelevel | reportlevel | export");
        System.out.print("> ");
    }

    // déplace le curseur de (dr, dc) si la nouvelle position est valide
    private void moveCursor(int dr, int dc) {
        int nr = cursorRow + dr;
        int nc = cursorCol + dc;
        if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
            cursorRow = nr;
            cursorCol = nc;
        }
    }

    // place l'élément courant à la position du curseur
    private void placeElement(char c) {
        grid[cursorRow][cursorCol] = c;
    }

    /*--------------------------------------------------
                        METHODES — VALIDATION
    --------------------------------------------------*/

    // délègue la validation au LevelProjectValidator et retourne les warnings
    public ArrayList<String> validate() {
        return LevelProjectValidator.validateWorldGrid(getGrid());
    }

    /*--------------------------------------------------
                        METHODES — EXPORT / IMPORT
    --------------------------------------------------*/

    // export au format Sokoban standard (avec header nom_monde taille)
    public String exportLevel() {
        ArrayList<String> warnings = validate();
        for (String w : warnings) System.out.println("[AVERTISSEMENT] " + w);

        int           size = Math.max(rows, cols);
        StringBuilder sb   = new StringBuilder();
        sb.append(worldName).append(" ").append(size).append("\n");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char c = grid[i][j];
                if (c == 'x')      c = '.';
                else if (c == 'O') c = '$';
                sb.append(c);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // import depuis le format Sokoban standard (avec header)
    public void importLevel(String text) {
        if (text == null || text.trim().isEmpty()) {
            System.out.println("[ERREUR] Texte vide, import annulé.");
            return;
        }
        String[] lines = text.split("\\r?\\n");
        if (lines.length < 2) { System.out.println("[ERREUR] Format invalide."); return; }

        String   header = lines[0].trim();
        String[] parts  = header.split("\\s+");
        if (parts.length >= 2) worldName = parts[0].charAt(0);

        ArrayList<String> gridLines = new ArrayList<>();
        for (int i = 1; i < lines.length; i++)
            if (!lines[i].isEmpty()) gridLines.add(lines[i]);

        if (gridLines.isEmpty()) { System.out.println("[ERREUR] Aucune ligne de grille."); return; }

        int newRows = gridLines.size();
        int newCols = 0;
        for (String l : gridLines) newCols = Math.max(newCols, l.length());

        if (newRows < 5 || newCols < 5) { System.out.println("[ERREUR] Grille trop petite (min 5x5)."); return; }

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
                        case '*': c = 'O'; break;
                        case '.': c = 'x'; break;
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
        System.out.println("Niveau importé ! (" + rows + "x" + cols + ", monde '" + worldName + "')");
    }

    // export au format projet (worldN.txt brut, sans header)
    public String exportProjectFormat() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) sb.append(grid[i][j]);
            sb.append("\n");
        }
        return sb.toString();
    }

    // import depuis le format projet (worldN.txt brut sans header)
    public void importProjectFormat(String text) {
        if (text == null || text.trim().isEmpty()) { System.out.println("[ERREUR] Texte vide."); return; }

        String[]          lines     = text.split("\\r?\\n");
        ArrayList<String> gridLines = new ArrayList<>();
        for (String l : lines) if (!l.isEmpty()) gridLines.add(l);
        if (gridLines.isEmpty()) return;

        int newRows = gridLines.size();
        int newCols = 0;
        for (String l : gridLines) newCols = Math.max(newCols, l.length());

        if (newRows < 5 || newCols < 5) { System.out.println("[ERREUR] Grille trop petite."); return; }

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

    /*--------------------------------------------------
                        METHODES — FICHIERS PROJET
    --------------------------------------------------*/

    // sauvegarde la grille dans levels/personnalized/levelN/worldN.txt
    // et met à jour nbWorlds.txt
    public void saveToProjectFile(int levelNum, int worldIndex, int totalWorlds) {
        ArrayList<String> warnings = validate();
        for (String w : warnings) System.out.println("[AVERTISSEMENT] " + w);

        File levelDir = new File("levels/personnalized/level" + levelNum);
        if (!levelDir.exists()) levelDir.mkdirs();

        File worldFile = new File(levelDir, "world" + worldIndex + ".txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(worldFile))) {
            pw.print(exportProjectFormat());
            System.out.println("Sauvegardé : " + worldFile.getPath());
        } catch (IOException e) {
            System.out.println("[ERREUR] " + e.getMessage());
        }

        File nbFile = new File(levelDir, "nbWorlds.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(nbFile))) {
            pw.println(totalWorlds);
        } catch (IOException e) {
            System.out.println("[ERREUR] nbWorlds : " + e.getMessage());
        }
    }

    // charge un monde depuis levels/personnalized/levelN/worldN.txt
    // Retourne true si le chargement a réussi
    public boolean loadFromProjectFile(int levelNum, int worldIndex) {
        File worldFile = new File("levels/personnalized/level" + levelNum + "/world" + worldIndex + ".txt");
        if (!worldFile.exists()) {
            System.out.println("[ERREUR] Fichier pas trouvé : " + worldFile.getPath());
            return false;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(worldFile))) {
            StringBuilder sb = new StringBuilder();
            String        line;
            while ((line = br.readLine()) != null) sb.append(line).append("\n");
            importProjectFormat(sb.toString());
            System.out.println("Chargé : " + worldFile.getPath());
            return true;
        } catch (IOException e) {
            System.out.println("[ERREUR] " + e.getMessage());
            return false;
        }
    }

    /*--------------------------------------------------
                        METHODES — BOUCLE TERMINALE
    --------------------------------------------------*/

    // boucle principale du mode terminal
    public void run() {
        Scanner sc = new Scanner(System.in);
        display();
        while (sc.hasNextLine()) {
            String input = sc.nextLine().trim();
            if (input.isEmpty()) { display(); continue; }

            switch (input) {
                case "w": moveCursor(-1,  0); break;
                case "s": moveCursor( 1,  0); break;
                case "a": moveCursor( 0, -1); break;
                case "d": moveCursor( 0,  1); break;
                case "#": placeElement('#'); break;
                case "x": placeElement('x'); break;
                case "@": placeElement('@'); break;
                case "O": placeElement('O'); break;
                case "e": placeElement('e'); break;
                case "P": placeElement('P'); break;
                case ".":
                case " ": placeElement(' '); break;
                case "quit": System.out.println("À bientôt !"); return;

                case "validate":
                    ArrayList<String> warnings = validate();
                    if (warnings.isEmpty()) System.out.println("Niveau valide !");
                    else for (String w2 : warnings) System.out.println("[!] " + w2);
                    break;

                case "export":
                    System.out.println("=== FORMAT STANDARD ===");
                    System.out.println(exportLevel());
                    System.out.println("=== FORMAT PROJET ===");
                    System.out.println(exportProjectFormat());
                    break;

                case "save":
                    System.out.println("=== DÉBUT DU NIVEAU ===");
                    System.out.println(exportLevel());
                    System.out.println("=== FIN ===");
                    break;

                case "savefile":
                    System.out.print("Numéro du level : ");
                    int ln = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Index du monde (0, 1, ...) : ");
                    int wi = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Nombre total de mondes dans ce level : ");
                    int tw = Integer.parseInt(sc.nextLine().trim());
                    saveToProjectFile(ln, wi, tw);
                    ArrayList<String> levelWarnings = LevelProjectValidator.validateProjectLevel(
                            new File("levels/personnalized/level" + ln));
                    if (levelWarnings.isEmpty()) System.out.println("[OK] Niveau complet valide.");
                    else for (String w3 : levelWarnings) System.out.println("[!] " + w3);
                    break;

                case "loadfile":
                    System.out.print("Numéro du level : ");
                    int ln2 = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Index du monde (0, 1, ...) : ");
                    int wi2 = Integer.parseInt(sc.nextLine().trim());
                    loadFromProjectFile(ln2, wi2);
                    break;

                case "load":
                    System.out.println("Collez le niveau (terminez avec 'END') :");
                    StringBuilder sb2 = new StringBuilder();
                    String        line;
                    while (sc.hasNextLine() && !(line = sc.nextLine()).equals("END"))
                        sb2.append(line).append("\n");
                    importLevel(sb2.toString());
                    break;

                case "validatelevel":
                    System.out.print("Numéro du level : ");
                    int vln = Integer.parseInt(sc.nextLine().trim());
                    ArrayList<String> levelWarnings2 = LevelProjectValidator.validateProjectLevel(
                            new File("levels/personnalized/level" + vln));
                    if (levelWarnings2.isEmpty()) System.out.println("Niveau complet valide !");
                    else for (String w3 : levelWarnings2) System.out.println("[!] " + w3);
                    break;

                case "reportlevel":
                    System.out.print("Numéro du level : ");
                    int rln = Integer.parseInt(sc.nextLine().trim());
                    System.out.println(LevelProjectValidator.buildDistributionReport(
                            new File("levels/personnalized/level" + rln)));
                    break;

                case "n":
                    System.out.print("Nombre de lignes (min 5) : ");
                    int r = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Nombre de colonnes (min 5) : ");
                    int c2 = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Nom du monde (une lettre) : ");
                    char name = sc.nextLine().trim().charAt(0);
                    resize(r, c2, name);
                    System.out.println("Nouvelle grille créée !");
                    break;

                default:
                    System.out.println("Commande inconnue : '" + input + "'");
            }
            display();
        }
    }
}
