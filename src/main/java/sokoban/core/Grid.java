package sokoban.core;

public class Grid implements Cloneable {

    /*--------------------------------------------------
                        ATTRIBUTS
    --------------------------------------------------*/
    private final char[][] grid;
    private final int      width;
    private final int      length;

    /*--------------------------------------------------
                        CONSTRUCTEUR
    --------------------------------------------------*/
    public Grid(int length, int width) {
        if (width < 5 || length < 5) throw new IllegalArgumentException();
        this.length = length;
        this.width  = width;
        grid = new char[length][width];
    }

    /*--------------------------------------------------
                        GETTERS
    --------------------------------------------------*/
    public int  getWidth()             { return width;        }
    public int  getLength()            { return length;       }
    public char getElement(int y, int x){ return grid[y][x];  }

    /*--------------------------------------------------
                        SETTERS
    --------------------------------------------------*/
    public void setElement(int y, int x, char c) { grid[y][x] = c; }

    /*--------------------------------------------------
                        METHODES
    --------------------------------------------------*/

    // initialise la grille depuis un tableau de char
    public void initGrid(char[][] g) {
        for (int i = 0; i < length; i++)
            for (int j = 0; j < width; j++)
                grid[i][j] = g[i][j];
    }

    // affiche la grille dans le terminal
    public void drawGrid() {
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++)
                System.out.printf("%c ", grid[i][j]);
            System.out.println();
        }
    }

    // échange le contenu de deux positions dans la grille
    public void permutePositions(Position p1, Position p2) {
        if (p1 == null || p2 == null) throw new NullPointerException();
        char temp = grid[p1.getY()][p1.getX()];
        grid[p1.getY()][p1.getX()] = grid[p2.getY()][p2.getX()];
        grid[p2.getY()][p2.getX()] = temp;
    }

    // met à jour la grille après le déplacement d'un élément
    public void updateGrid(Position elemPos, Position nextPos, CellType cell) {
        if (elemPos == null || nextPos == null || cell == null) throw new NullPointerException();
        grid[nextPos.getY()][nextPos.getX()] = grid[elemPos.getY()][elemPos.getX()];
        grid[elemPos.getY()][elemPos.getX()] = cell.getSymbole();
    }

    // retourne une vraie copie de la grille
    @Override
    public Grid clone() {
        Grid copy = new Grid(this.length, this.width);
        for (int i = 0; i < this.length; i++)
            copy.grid[i] = this.grid[i].clone();
        return copy;
    }
}
