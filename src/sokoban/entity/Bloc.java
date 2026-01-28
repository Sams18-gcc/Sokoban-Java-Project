package sokoban.entity;

import sokoban.core.BlocType;
import sokoban.core.Position;

public class Bloc {
    private final BlocType bloc;
    private final Position pos;


    public Bloc(int x, int y, BlocType bloc)
    {
        this.pos = new Position(x, y);
        this.bloc = bloc;
    }
}