package sokoban.entity;

import sokoban.core.CellType;
import sokoban.core.Position;

public class Cell {

    /*--------------------------------------------------
                        ATTRIBUTS
    --------------------------------------------------*/
    private final CellType cell;    // type de la case (wall, floor, target...)
    private final Position pos;     // position de la case dans la grille
    private       boolean  isFree;  // indique si la case est libre ou occupée

    /*--------------------------------------------------
                        CONSTRUCTEUR
    --------------------------------------------------*/
    public Cell(int x, int y, CellType cell, boolean free) {
        this.pos    = new Position(x, y);
        this.cell   = cell;
        this.isFree = free;
    }

    /*--------------------------------------------------
                        GETTERS
    --------------------------------------------------*/
    public boolean  isFree()      { return this.isFree; }
    public CellType getCellType() { return this.cell;   }

    /*--------------------------------------------------
                        SETTERS
    --------------------------------------------------*/
    public void setOccupied() { this.isFree = false; }
    public void setFree()     { this.isFree = true;  }
}
