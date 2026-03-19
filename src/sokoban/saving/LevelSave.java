import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class LevelSave implements Serializable {
    private final int numLevel;
    private final int actWorld;
    private final List<WorldSave> worlds;

    public LevelSave(int numLevel, int actWorld, List<WorldSave> worlds) {
        this.numLevel = numLevel;
        this.actWorld = actWorld;
        this.worlds = new ArrayList<>(worlds);
    }

    public int getNumLevel()          { return numLevel; }
    public int getActWorld()          { return actWorld; }
    public List<WorldSave> getWorlds() { return worlds; }
}
