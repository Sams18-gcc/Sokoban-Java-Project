package sokoban.core;

import sokoban.entity.Box;
import sokoban.entity.Cell;
import sokoban.entity.Player;

import java.lang.reflect.AccessFlag;
import java.util.ArrayList;

public class World {
    private final Grid grid;
    private final Cell[][] cells;
    private Player player;
    private ArrayList<Box> boxes;

    public World(int length, int width) {
        if (width < 5 || length < 5) throw new IllegalArgumentException();
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

    public Box getBoxinPosition(Position pos) {
        if(pos == null )
            throw new NullPointerException();
        for (Box box : boxes) {
            if (box.getPosition().equals(pos))
                return box;
        }
        return null;
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

    public void movePlayer(Direction d) {
        if (d == null) throw new NullPointerException();
        Position actualPos = player.getPosition();
        Position nextPos = player.getPosition();
        nextPos.translate(d);
        if (isBox(nextPos))
        {
            moveBox(d, nextPos);
        }

        updateCells(actualPos, nextPos);
        grid.updateGrid(actualPos, nextPos, cells[actualPos.getY()][actualPos.getX()].getCellType());
        player.move(d);
    }


    public void moveBox(Direction d, Position pos) {
        if (d == null || pos == null) throw new NullPointerException();
        Position actualPos = new Position(pos.getY(), pos.getX());
        Position nextPos = new Position(pos.getY(), pos.getX());
        nextPos.translate(d);
        grid.updateGrid(actualPos, nextPos, cells[actualPos.getY()][actualPos.getX()].getCellType());
        Box box = getBoxinPosition(pos);
        if(box == null)
             throw new IllegalStateException();
        else box.move(d);
        updateCells(actualPos, nextPos);
    }

    public void updateCells(Position actualPos, Position nextPos)
    {
         if(actualPos == null || nextPos == null)
             throw new NullPointerException();
         cells[actualPos.getY()][actualPos.getX()].setFree();
         cells[nextPos.getY()][nextPos.getX()].setOccupied();
    }


    public void displayWorld() {
        grid.drawGrid();
    }
}
