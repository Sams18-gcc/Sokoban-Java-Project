package sokoban.core;

public enum Direction {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

     final int dx;
     final int dy;

    Direction(int dy, int dx) {
        this.dx = dx;
        this.dy = dy;
    }
}
 
