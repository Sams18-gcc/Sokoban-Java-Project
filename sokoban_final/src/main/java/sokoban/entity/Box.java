package sokoban.entity;

import sokoban.core.Direction;
import sokoban.core.Position;

public class Box {

    /*--------------------------------------------------
                        ATTRIBUTS
    --------------------------------------------------*/
    private boolean  isInTarget; // indique si la boîte est sur une target
    private final Position pos;  // position actuelle de la boîte

    /*--------------------------------------------------
                        CONSTRUCTEUR
    --------------------------------------------------*/
    public Box(int y, int x) {
        this.pos       = new Position(y, x);
        this.isInTarget = false;
    }

    /*--------------------------------------------------
                        GETTERS
    --------------------------------------------------*/

    // retourne une copie pour éviter de modifier la vraie position depuis l'extérieur
    public Position getPosition() {
        return new Position(pos.getY(), pos.getX());
    }

    public boolean isInTarget() { return isInTarget; }

    /*--------------------------------------------------
                        SETTERS
    --------------------------------------------------*/
    public void setInTarget()    { this.isInTarget = true;  }
    public void setOutOfTarget() { this.isInTarget = false; }

    /*--------------------------------------------------
                        METHODES
    --------------------------------------------------*/

    // déplace la boîte dans la direction donnée
    public void move(Direction d) {
        pos.translate(d);
    }

    // deux boîtes sont considérées égales si elles sont à la même position
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Box)) return false;
        if (o == this) return true;
        Box box = (Box) o;
        return getPosition().equals(box.getPosition());
    }

    @Override
    public int hashCode() {
        return getPosition().hashCode();
    }
}
