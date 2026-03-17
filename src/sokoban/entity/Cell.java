package sokoban.entity;

import sokoban.core.CellType;
import sokoban.core.Position;

public class Cell {
    // type de la case (wall, floor, target...)
    private final CellType cell;

    // position de la case dans la grille
    private final Position pos;

    // indique si la case est libre ou occupee
    private boolean isFree;

    public Cell(int x, int y, CellType cell, boolean free)
    {
        this.pos = new Position(x, y);
        this.cell = cell;
        this.isFree = free;
    }

    public void setOccupied()
    {
        this.isFree = false;
    }

    public void setFree()
    {
        this.isFree = true;
    }

    public boolean isFree()
    {
        return this.isFree;
    }

    public CellType getCellType()
    {
        return this.cell;
    }
}