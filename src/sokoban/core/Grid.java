package sokoban.core;

import sokoban.entity.Box;

public class Grid {
    private final char[][] grid;
    private final int width;
    private final int length;

    public Grid(int length, int width) {
        if (width < 5 || length < 5) throw new IllegalArgumentException();
        this.length = length;
        this.width = width;
        grid = new char[length][width];

    }

    public void initGrid() {


        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++) {
                if (i == 0 || j == 0 || i == length - 1 || j == width - 1) {
                    grid[i][j] = '#';
                } else
                    this.grid[i][j] = ' ';
            }
        }
        this.grid[1][width - 3] = '@';
        this.grid[2][width - 2] = 'O';
        this.grid[3][2] = 'O';
        this.grid[length - 2][width - 5] = 'x';

    }

    public void drawGrid() {
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++) {
                System.out.printf("%c ", grid[i][j]);
            }
            System.out.println();
        }
    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }

    public void permutePositions(Position p1, Position p2) {
        if (p1 == null || p2 == null) throw new NullPointerException();
        char temp;
        temp = grid[p1.getY()][p1.getX()];
        grid[p1.getY()][p1.getX()] = grid[p2.getY()][p2.getX()];
        grid[p2.getY()][p2.getX()] = temp;
    }

    public void updateGrid(Position elemPos, Position nextPos, CellType cell) {
        if (elemPos == null || nextPos == null || cell == null)
            throw new NullPointerException();
        grid[nextPos.getY()][nextPos.getX()] = grid[elemPos.getY()][elemPos.getX()];
        grid[elemPos.getY()][elemPos.getX()] = cell.getSymbole();


    }

    public char getElement(int y, int x) {
        return grid[y][x];
    }

}