package sokoban.entity;

import sokoban.core.Direction;
import sokoban.core.Position;

public class Box {

    private boolean isInTarget;
    private final Position pos;

    public Box(int x, int y, Position pos) {
        this.pos = new Position(x, y);
        isInTarget = false;
    }

    public void move(Direction d) {
        pos.translate(d);
    }

    public void setInTarget() {
        this.isInTarget = true;
    }


}
