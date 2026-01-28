package sokoban.entity;

import sokoban.core.Direction;
import sokoban.core.Position;


public class Joueur {
    private final Position pos;

    public Joueur(int x, int y) {
        pos = new Position(x, y);
    }


    public Position getPosition() {
        return new Position(pos.getX(), pos.getY());
    }

    public void move(Direction d) {
        if (d == null) throw new NullPointerException();
        pos.translate(d);
    }


}
