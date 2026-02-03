package sokoban.core;

public class Position {
    private int x;
    private int y;


    public Position(int y, int x) {
        this.x = x;
        this.y = y;
    }

    public int getY() {
        return y;
    }


    public int getX() {
        return x;
    }

    public void translate(Direction d) {
        if (d == null) throw new NullPointerException();
        this.x += d.dx;
        this.y += d.dy;
    }

    public boolean equals(Object o)
    {
         if(o == null || !(o instanceof Position )) return false;
         if(o == this) return true;
         Position pos = (Position) o;
         return (getX() == pos.getX()) && (getY() == pos.getY());

    }

}
