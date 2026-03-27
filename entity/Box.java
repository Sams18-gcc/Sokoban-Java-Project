package sokoban.entity;

import sokoban.core.Direction;
import sokoban.core.Position;

public class Box {

    // indique si la boite est actuellement sur une target
    private boolean isInTarget;

    // position actuelle de la boite
    private final Position pos;

    public Box(int y, int x) {
        this.pos = new Position(y, x);
        isInTarget = false;
    }

    // on renvoie une copie pour eviter de modifier la vraie position depuis l'exterieur
    public Position getPosition() {
        return new Position(pos.getY(), pos.getX());
    }

    public void move(Direction d) {
        pos.translate(d);
    }

    public void setInTarget() {
        this.isInTarget = true;
    }

    public void setOutOfTarget() {
        this.isInTarget = false;
    }

    public boolean isInTarget() {
        return isInTarget;
    }

    // deux boites sont considerees egales si elles sont a la meme position
    @Override
    public boolean equals(Object o)
    {
        if(o == null || !(o instanceof Box)) return false;
        if(o == this) return true;
        Box box = (Box) o;
        return getPosition().equals(box.getPosition());
    }

    @Override
    public int hashCode()
    {
        return getPosition().hashCode();
    }
}