package sokoban.core;

public class Position {

    /*--------------------------------------------------
                        ATTRIBUTS
    --------------------------------------------------*/
    private int x;
    private int y;

    /*--------------------------------------------------
                        CONSTRUCTEUR
    --------------------------------------------------*/
    public Position(int y, int x) {
        this.x = x;
        this.y = y;
    }

    /*--------------------------------------------------
                        GETTERS
    --------------------------------------------------*/
    public int getY() { return y; }
    public int getX() { return x; }

    /*--------------------------------------------------
                        METHODES
    --------------------------------------------------*/

    // déplace la position selon la direction passée
    public void translate(Direction d) {
        if (d == null) throw new NullPointerException();
        this.x += d.dx;
        this.y += d.dy;
    }

    // deux positions sont égales si elles ont les mêmes coordonnées
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Position)) return false;
        if (o == this) return true;
        Position pos = (Position) o;
        return (getX() == pos.getX()) && (getY() == pos.getY());
    }

    // utile surtout pour les HashSet
    @Override
    public int hashCode() {
        return x * 31 + y + 7;
    }
}
