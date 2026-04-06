package sokoban.saving;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// objet sérialisable représentant la sauvegarde complète d'un niveau
public class LevelSave implements Serializable {

    /*--------------------------------------------------
                        ATTRIBUTS
    --------------------------------------------------*/
    private final int            numLevel;
    private final int            actWorld;
    private final List<WorldSave> worlds;

    /*--------------------------------------------------
                        CONSTRUCTEUR
    --------------------------------------------------*/
    public LevelSave(int numLevel, int actWorld, List<WorldSave> worlds) {
        this.numLevel = numLevel;
        this.actWorld = actWorld;
        this.worlds   = new ArrayList<>(worlds);
    }

    /*--------------------------------------------------
                        GETTERS
    --------------------------------------------------*/
    public int             getNumLevel() { return numLevel; }
    public int             getActWorld() { return actWorld; }
    public List<WorldSave> getWorlds()   { return worlds;   }
}
