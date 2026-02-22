package sokoban.entity;

import sokoban.core.Direction;
import sokoban.core.Position;

public class Box {

    private boolean isInTarget;
    private final Position pos;

    public Box(int y, int x) {
        this.pos = new Position(y, x);
        isInTarget = false;
    }

    public Position getPosition() {

        return new Position(pos.getY(), pos.getX());
    }

    public void move(Direction d) {

        pos.translate(d);
    }

    public void setInTarget() {
        this.isInTarget = true;
    }

    public void setOutOfTarget(){

        this.isInTarget = false;
    }

    public boolean isInTarget()
    {
         return isInTarget;
    }

    public boolean equals(Object o)
    {
         if(o == null || !(o instanceof Box)) return false;
         if(o == this) return true;
         Box box = (Box) o;
         return getPosition().equals(box.getPosition());
    }

    public int hashCode()
    {
         return getPosition().hashCode();
    }



}
