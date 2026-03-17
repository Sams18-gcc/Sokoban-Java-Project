package sokoban.core;

import sokoban.entity.Box;
import sokoban.entity.Cell;
import sokoban.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class World {
    // grille du monde pour l'affichage
    private final Grid grid;

    // tableau des cellules logiques
    private final Cell[][] cells;

    // joueur du monde
    private Player player;

    // liste des boites presentes
    private ArrayList<Box> boxes;

    // ref du monde dans le niveau
    private int worldRef;

    public World(int length, int width, int worldRef) {
        if (width < 5 || length < 5 || worldRef < 0) throw new IllegalArgumentException();
        grid = new Grid(length, width);
        cells = new Cell[length][width];
        player = null;
        boxes = new ArrayList<Box>();
        this.worldRef = worldRef;
    }

    // initialise le monde a partir de la grille
    public void loadWorld(char[][] g) {
        grid.initGrid(g);

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

                } else {
                    cells[i][j] = new Cell(i, j, CellType.FLOOR, true);
                }
            }
        }
    }
    // retourne vrai si c'est une box a la position pos
    public boolean isBox(Position pos) {
        if (pos == null) throw new NullPointerException();
        return grid.getElement(pos.getY(), pos.getX()) == 'O';
    }

    // renvoie la boite a une position donnee si elle existe
    public Box getBoxatPosition(Position pos) {
        if (pos == null)
            throw new NullPointerException();

        for (Box box : boxes) {
            if (box.getPosition().equals(pos))
                return box;
        }
        return null;
    }
    // renvoie le type de cellule a la position donnee
    public Cell getCellatPosition(Position pos) {
        if (pos == null) throw new NullPointerException();
        return cells[pos.getY()][pos.getX()];
    }

    // position actuelle du joueur
    public Position getPlayerPosition() {
        return player.getPosition();
    }

    // verifie si toutes les boites sont sur une target
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

    // verifie si le joueur peut avancer dans cette direction
    // si y a une boite on regarde aussi la case d'apres
    public boolean checkMove(Direction d) {
        if (d == null) throw new NullPointerException();

        Position actualPos = player.getPosition();
        actualPos.translate(d);

        if (!cells[actualPos.getY()][actualPos.getX()].isFree()) {
            if (grid.getElement(actualPos.getY() ,actualPos.getX()) == 'O')
            actualPos.translate(d);
        }

        return cells[actualPos.getY()][actualPos.getX()].isFree();
    }

    // met a jour les cellules apres un deplacement
    public void updateCells(Position actualPos, Position nextPos) {
        if (actualPos == null || nextPos == null)
            throw new NullPointerException();

        cells[actualPos.getY()][actualPos.getX()].setFree();
        cells[nextPos.getY()][nextPos.getX()].setOccupied();
    }

    // met a jour la grille apres le deplacement d'un element
    public void updateWorldData(Position elemPos, Position nextPos, CellType cell) {
        if (elemPos == null || nextPos == null || cell == null) throw new NullPointerException();
        grid.updateGrid(elemPos, nextPos, cell);
    }

    public void changePlayerPosition(Direction d) {
        if (d == null) throw new NullPointerException();
        player.move(d);
    }

    // on renvoie un clone pour eviter de modifier la vraie grille depuis l'exterieur
    public Grid getGrid() {
        return grid.clone();
    }


    public ArrayList<Box> getBoxes() {
        return new ArrayList<>(boxes);
    }

    public char[][] getGridArray() {
        char[][] copy = new char[grid.getLength()][grid.getWidth()];
        for (int i = 0; i < grid.getLength(); i++) {
            for (int j = 0; j < grid.getWidth(); j++) {
                copy[i][j] = grid.getElement(i, j);
            }
        }
        return copy;
    }

    public void setPlayerAt(int y, int x) {
        this.player = new Player(y, x);
    }

    public void setGridArray(char[][] newGrid) {
        for (int i = 0; i < grid.getLength(); i++) {
            for (int j = 0; j < grid.getWidth(); j++) {
                grid.setElement(i, j, newGrid[i][j]);
            }
        }
    }

    public void setBoxesFromPositions(List<int[]> positions) {
        if (positions == null) throw new NullPointerException();

        boxes = new ArrayList<>();
        for (int[] pos : positions) {
            boxes.add(new Box(pos[0], pos[1]));
        }
    }
}