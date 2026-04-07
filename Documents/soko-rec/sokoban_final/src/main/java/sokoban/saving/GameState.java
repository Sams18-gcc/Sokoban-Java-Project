package sokoban.saving;

import sokoban.core.Position;
import sokoban.entity.Box;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// snapshot sérialisable de l'état d'un monde à un instant donné (pour undo et save)
public class GameState implements Serializable {

    /*--------------------------------------------------
                        ATTRIBUTS
    --------------------------------------------------*/
    private final int            playerY;
    private final int            playerX;
    private final List<Position> boxPositions;
    private final char[][]       gridSnapshot;

    /*--------------------------------------------------
                        CONSTRUCTEUR
    --------------------------------------------------*/
    public GameState(int playerY, int playerX, List<Box> boxes, char[][] grid) {
        this.playerY = playerY;
        this.playerX = playerX;

        this.boxPositions = new ArrayList<>();
        for (Box b : boxes)
            boxPositions.add(new Position(b.getPosition().getY(), b.getPosition().getX()));

        // copie défensive de la grille
        this.gridSnapshot = new char[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++)
            System.arraycopy(grid[i], 0, gridSnapshot[i], 0, grid[i].length);
    }

    /*--------------------------------------------------
                        GETTERS
    --------------------------------------------------*/
    public int            getPlayerY()      { return playerY;      }
    public int            getPlayerX()      { return playerX;      }
    public List<Position> getBoxPositions() { return boxPositions; }
    public char[][]       getGridSnapshot() { return gridSnapshot; }
}
