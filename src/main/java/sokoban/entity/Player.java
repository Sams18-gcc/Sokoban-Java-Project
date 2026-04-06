package sokoban.entity;

import sokoban.core.Direction;
import sokoban.core.Position;

public class Player {

    /*--------------------------------------------------
                        ATTRIBUTS
    --------------------------------------------------*/
    private final Position pos; // position actuelle du joueur

    /*--------------------------------------------------
                        CONSTRUCTEUR
    --------------------------------------------------*/
    public Player(int y, int x) {
        pos = new Position(y, x);
    }

    /*--------------------------------------------------
                        GETTERS
    --------------------------------------------------*/

    // retourne une copie de la position pour éviter les modifications externes
    public Position getPosition() {
        return new Position(pos.getY(), pos.getX());
    }

    /*--------------------------------------------------
                        METHODES
    --------------------------------------------------*/

    // déplace le joueur dans la direction donnée
    public void move(Direction d) {
        if (d == null) throw new NullPointerException();
        pos.translate(d);
    }
}
