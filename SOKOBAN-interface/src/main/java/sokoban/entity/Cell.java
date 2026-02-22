package sokoban.entity;

import sokoban.core.CellType;
import sokoban.core.Position;

public class Cell {
    private final CellType cell;
    private final Position pos;
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

    public boolean getCellStatus()
    {
         return this.isFree;
    }

    public CellType getCellType()
    {
         return this.cell;
    }
}