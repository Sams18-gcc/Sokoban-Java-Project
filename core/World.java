package sokoban.core;

import sokoban.entity.Box;
import sokoban.entity.Cell;
import sokoban.entity.Player;
import sokoban.entity.PortalBox;

import java.util.ArrayList;
import java.util.List;

public class World {

    /*--------------------------------------------------
                        ATTRIBUTS
    --------------------------------------------------*/

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

    /*--------------------------------------------------
                        CONSTRUCTEUR
    --------------------------------------------------*/

    public World(int length, int width, int worldRef) {
        if (width < 5 || length < 5 || worldRef < 0) throw new IllegalArgumentException();
        grid = new Grid(length, width);
        cells = new Cell[length][width];
        player = null;
        boxes = new ArrayList<Box>();
        this.worldRef = worldRef;
    }

    /*--------------------------------------------------
                        INIT
    --------------------------------------------------*/

    // initialise le monde a partir de la grille
    // MODIFIE rec : ajout du cas 'P' pour creer une PortalBox
    public void loadWorld(char[][] g) {
        grid.initGrid(g);
        // quand je load une saved partie le add ajoute a la liste existante deja donc ca cree des doublons
        // donc vaut mieux initializer a vide a chaque load
        player = null;
        boxes.clear();
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

                    // AJOUT rec : PortalBox representee par 'P'
                } else if (grid.getElement(i, j) == 'P') {
                    cells[i][j] = new Cell(i, j, CellType.FLOOR, false);
                    boxes.add(new PortalBox(i, j));

                } else {
                    cells[i][j] = new Cell(i, j, CellType.FLOOR, true);
                }
            }
        }
    }

    /*--------------------------------------------------
                        LOGIQUE DE DEPLACEMENT
    --------------------------------------------------*/

    // retourne vrai si c'est une box a la position pos
    // MODIFIE rec : verifie aussi 'P' pour les PortalBox
    public boolean isBox(Position pos) {
        if (pos == null) throw new NullPointerException();
        char c = grid.getElement(pos.getY(), pos.getX());
        return c == 'O' || c == 'P';
    }

    // verifie si le joueur peut avancer dans cette direction
    // si y a une boite on regarde aussi la case d'apres
    // MODIFIE rec : PortalBox ouverte = traversable directement
    public boolean checkMove(Direction d) {
        if (d == null) throw new NullPointerException();

        Position actualPos = player.getPosition();
        actualPos.translate(d);

        if (!cells[actualPos.getY()][actualPos.getX()].isFree()) {

            // AJOUT rec : PortalBox ouverte -> le joueur peut avancer dessus
            Box box = getBoxatPosition(actualPos);
            if (box instanceof PortalBox && ((PortalBox) box).isOpen()) {
                return true;
            }

            // boite normale -> on regarde la case d'apres
            if (grid.getElement(actualPos.getY(), actualPos.getX()) == 'O') {
                actualPos.translate(d);
            }
        }

        return cells[actualPos.getY()][actualPos.getX()].isFree();
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

    /*--------------------------------------------------
                        GETTERS
    --------------------------------------------------*/

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

    // on renvoie un clone pour eviter de modifier la vraie grille depuis l'exterieur
    public Grid getGrid() {
        return grid.clone();
    }

    public ArrayList<Box> getBoxes() {
        return new ArrayList<>(boxes);
    }

    public int getWorldRef() {
        return worldRef;
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

    /*--------------------------------------------------
                        SETTERS
    --------------------------------------------------*/

    public void setPlayerAt(Position pos) {
        this.player = new Player(pos.getY(), pos.getX());
    }

    public void setGridArray(char[][] newGrid) {
        for (int i = 0; i < grid.getLength(); i++) {
            for (int j = 0; j < grid.getWidth(); j++) {
                grid.setElement(i, j, newGrid[i][j]);
            }
        }
    }

    public void setBoxesFromPositions(List<Position> positions) {
        if (positions == null) throw new NullPointerException();

        boxes = new ArrayList<>();
        for (Position pos : positions) {
            Box box = new Box(pos.getY(), pos.getX());
            if (getCellatPosition(pos).getCellType() == CellType.TARGET)
                box.setInTarget();
            boxes.add(box);
        }
    }

    /*--------------------------------------------------
                        AFFICHAGE
    --------------------------------------------------*/

    public void displayWorld() {
        grid.drawGrid();
    }
}