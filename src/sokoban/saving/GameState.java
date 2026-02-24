package sokoban.saving;

import sokoban.core.*;
import sokoban.entity.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class GameState implements Serializable {
    private final int playerY, playerX;
    private final List<int[]> boxPositions;
    private final char[][] gridSnapshot;

    public GameState(int playerY, int playerX, List<Box> boxes, char[][] grid) {
        this.playerY = playerY;
        this.playerX = playerX;

        this.boxPositions = new ArrayList<>();
        for (Box b : boxes) {
            boxPositions.add(new int[]{b.getPosition().getY(), b.getPosition().getX()});
        }

        //je copie la grid aussi 
        this.gridSnapshot = new char[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            System.arraycopy(grid[i], 0, gridSnapshot[i], 0, grid[i].length);
        }
    }

    public int getPlayerY() { return playerY; }
    public int getPlayerX() { return playerX; }
    public List<int[]> getBoxPositions() { return boxPositions; }
    public char[][] getGridSnapshot() { return gridSnapshot; }
}