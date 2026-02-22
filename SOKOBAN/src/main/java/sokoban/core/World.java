package sokoban.core;

import sokoban.entity.Box;
import sokoban.entity.Cell;
import sokoban.entity.Player;


import java.util.ArrayList;

public class World {
    private final Grid grid;
    private final Cell[][] cells;
    private Player player;
    private ArrayList<Box> boxes;
    private int worldRef;

    public World(int length, int width, int worldRef) {
        if (width < 5 || length < 5 || worldRef < 0) throw new IllegalArgumentException();
        grid = new Grid(length, width);
        cells = new Cell[length][width];
        player = null;
        boxes = new ArrayList<Box>();
    }


    public void loadWorld() {
        grid.initGrid();
        for (int i = 0; i < grid.getLength(); i++) {
            for (int j = 0; j < grid.getWidth(); j++) {
                if (grid.getElement(i, j) == CellType.EXIT.getSymbole())
                    cells[i][j] = new Cell(i, j, CellType.EXIT, true);

                else if (grid.getElement(i, j) == CellType.WALL.getSymbole())
                    cells[i][j] = new Cell(i, j, CellType.WALL, false);

                else if (grid.getElement(i, j) == CellType.TARGET.getSymbole())
                    cells[i][j] = new Cell(i, j, CellType.TARGET, true);


                else if (grid.getElement(i, j) == '@') {
                    cells[i][j] = new Cell(i, j, CellType.FLOOR, false);
                    player = new Player(i, j);
                } else if (grid.getElement(i, j) == 'O') {
                    cells[i][j] = new Cell(i, j, CellType.FLOOR, false);
                    Box box = new Box(i, j);
                    boxes.add(box);
                } else
                    cells[i][j] = new Cell(i, j, CellType.FLOOR, true);


            }
        }


    }

    public void displayBoxes() {
        for (Box b : boxes) {
            System.out.println(b.getPosition().getY() + " " + b.getPosition().getX());
        }
    }

    public boolean isBox(Position pos) {
        if (pos == null) throw new NullPointerException();
        return grid.getElement(pos.getY(), pos.getX()) == 'O';
    }

    public Box getBoxatPosition(Position pos) {
        if (pos == null)
            throw new NullPointerException();
        for (Box box : boxes) {
            if (box.getPosition().equals(pos))
                return box;
        }
        return null;
    }

    public Cell getCellatPosition(Position pos) {
        if (pos == null) throw new NullPointerException();
        return cells[pos.getY()][pos.getX()];
    }

    public Position getPlayerPosition() {
        return player.getPosition();
    }

    public boolean allBoxesInTarget() {
        for (Box box : boxes) {
            if (!box.isInTarget())
                return false;
        }

        return true;
    }


    public void displayWorld() {
        grid.drawGrid();
    }

    public boolean checkMove(Direction d) {
        if (d == null) throw new NullPointerException();
        Position actualPos = player.getPosition();
        actualPos.translate(d);
        if (!cells[actualPos.getY()][actualPos.getX()].getCellStatus()) {
            if (grid.getElement(actualPos.getY(), actualPos.getX()) == 'O')
                actualPos.translate(d);

        }
        return cells[actualPos.getY()][actualPos.getX()].getCellStatus();

    }


    public void updateCells(Position actualPos, Position nextPos) {
        if (actualPos == null || nextPos == null)
            throw new NullPointerException();
        cells[actualPos.getY()][actualPos.getX()].setFree();
        cells[nextPos.getY()][nextPos.getX()].setOccupied();
    }

    public void updateWorldData(Position elemPos, Position nextPos, CellType cell) {
        if (elemPos == null || nextPos == null || cell == null) throw new NullPointerException();
        grid.updateGrid(elemPos, nextPos, cell);
    }

    public void changePlayerPosition(Direction d) {
        if (d == null) throw new NullPointerException();
        player.move(d);
    }




    /// C EST YANIS QUI A AJOUTER CA

    public Grid getGrid() {
        return this.grid;
    }

}
