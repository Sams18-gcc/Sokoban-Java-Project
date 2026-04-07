package sokoban.saving;

import java.io.Serializable;

// objet sérialisable représentant la sauvegarde d'un monde individuel
public class WorldSave implements Serializable {

    /*--------------------------------------------------
                        ATTRIBUTS
    --------------------------------------------------*/
    private final int       worldRef;
    private final GameState state;

    /*--------------------------------------------------
                        CONSTRUCTEUR
    --------------------------------------------------*/
    public WorldSave(int worldRef, GameState state) {
        this.worldRef = worldRef;
        this.state    = state;
    }

    /*--------------------------------------------------
                        GETTERS
    --------------------------------------------------*/
    public int       getWorldRef() { return worldRef; }
    public GameState getState()    { return state;    }
}
