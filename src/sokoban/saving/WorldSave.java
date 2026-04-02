

package sokoban.saving;


import java.io.Serializable;
import java.util.List;
import sokoban.saving.*;

public class WorldSave implements Serializable {
    private final int worldRef;
    private final GameState state;

    public WorldSave(int worldRef, GameState state) {
        this.worldRef = worldRef;
        this.state = state;
    }

    public int getWorldRef() { return worldRef; }
    public GameState getState() { return state; }
}
