package sokoban.entity;

import sokoban.core.Direction;
import sokoban.core.Position;

public class Player {
    // position actuelle du joueur
    private final Position pos;

    public Player(int y, int x) {
        pos = new Position(y, x);
    }

    // on renvoie une copie de la position
    public Position getPosition() {
        return new Position(pos.getY(), pos.getX());
    }

    public void move(Direction d) {
        if (d == null) throw new NullPointerException();
        pos.translate(d);
    }
}