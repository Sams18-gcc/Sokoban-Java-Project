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
    private final Grid      grid;       // grille du monde pour l'affichage
    private final Cell[][]  cells;      // tableau des cellules logiques
    private       Player    player;     // joueur du monde
    private       ArrayList<Box> boxes; // liste des boites presentes
    private       int       worldRef;   // ref du monde dans le niveau

    /*--------------------------------------------------
                        CONSTRUCTEUR
    --------------------------------------------------*/
    public World(int length, int width, int worldRef) {
        if (width < 5 || length < 5 || worldRef < 0) throw new IllegalArgumentException();
        grid      = new Grid(length, width);
        cells     = new Cell[length][width];
        player    = null;
        boxes     = new ArrayList<>();
        this.worldRef = worldRef;
    }

    /*--------------------------------------------------
                        GETTERS
    --------------------------------------------------*/
    public int           getWorldRef()      { return worldRef; }
    public ArrayList<Box> getBoxes()        { return new ArrayList<>(boxes); }

    // retourne un clone de la grille pour éviter de la modifier depuis l'extérieur
    public Grid          getGrid()          { return grid.clone(); }

    // position actuelle du joueur
    public Position      getPlayerPosition(){ return player.getPosition(); }

    /*--------------------------------------------------
                        SETTERS
    --------------------------------------------------*/
    public void setPlayerAt(Position pos) {
        this.player = new Player(pos.getY(), pos.getX());
    }

    public void setGridArray(char[][] newGrid) {
        for (int i = 0; i < grid.getLength(); i++)
            for (int j = 0; j < grid.getWidth(); j++)
                grid.setElement(i, j, newGrid[i][j]);
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
                        METHODES
    --------------------------------------------------*/

    // initialise le monde à partir d'une grille de caractères.
    // Remet player et boxes à zéro pour éviter les doublons lors d'un rechargement.
    // ajout rec — reconnaît le symbole 'P' pour créer une PortalBox
    public void loadWorld(char[][] g) {
        grid.initGrid(g);
        player = null;
        boxes.clear();

        for (int i = 0; i < grid.getLength(); i++) {
            for (int j = 0; j < grid.getWidth(); j++) {

                if      (grid.getElement(i, j) == CellType.EXIT.getSymbole())
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
                    boxes.add(new Box(i, j));

                } else if (grid.getElement(i, j) == 'P') {
                    // ajout rec — boîte portail : symbol 'P' dans le fichier de niveau
                    cells[i][j] = new Cell(i, j, CellType.FLOOR, false);
                    boxes.add(new PortalBox(i, j));

                } else {
                    cells[i][j] = new Cell(i, j, CellType.FLOOR, true);
                }
            }
        }
    }

    // retourne vrai si une boîte (normale ou portail) est à la position donnée
    // ajout rec — prend en compte le symbole 'P' des PortalBox
    public boolean isBox(Position pos) {
        if (pos == null) throw new NullPointerException();
        char c = grid.getElement(pos.getY(), pos.getX());
        return c == 'O' || c == 'P'; // ajout rec — 'P' = PortalBox
    }

    // renvoie la boîte à une position donnée, ou null si aucune
    public Box getBoxatPosition(Position pos) {
        if (pos == null) throw new NullPointerException();
        for (Box box : boxes)
            if (box.getPosition().equals(pos)) return box;
        return null;
    }

    // renvoie la cellule logique à la position donnée
    public Cell getCellatPosition(Position pos) {
        if (pos == null) throw new NullPointerException();
        return cells[pos.getY()][pos.getX()];
    }

    // vérifie si toutes les boîtes sont sur une target
    public boolean allBoxesInTarget() {
        for (Box box : boxes)
            if (!box.isInTarget()) return false;
        return true;
    }

    // affiche la grille dans le terminal
    public void displayWorld() {
        grid.drawGrid();
    }

    // vérifie si le joueur peut avancer dans la direction donnée.
    // Si une boîte (normale ou portail) est devant, on regarde aussi la case d'après.
    // ajout rec — étendu pour prendre en compte les PortalBox (symbole 'P')
    public boolean checkMove(Direction d) {
        if (d == null) throw new NullPointerException();

        Position actualPos = player.getPosition();
        actualPos.translate(d);

        if (!cells[actualPos.getY()][actualPos.getX()].isFree()) {
            char sym = grid.getElement(actualPos.getY(), actualPos.getX());
            // ajout rec — 'P' traité comme une boîte poussable
            if (sym == 'O' || sym == 'P')
                actualPos.translate(d);
        }

        return cells[actualPos.getY()][actualPos.getX()].isFree();
    }

    // libère la case de départ et occupe la case d'arrivée après un déplacement
    public void updateCells(Position actualPos, Position nextPos) {
        if (actualPos == null || nextPos == null) throw new NullPointerException();
        cells[actualPos.getY()][actualPos.getX()].setFree();
        cells[nextPos.getY()][nextPos.getX()].setOccupied();
    }

    // met à jour la grille après le déplacement d'un élément
    public void updateWorldData(Position elemPos, Position nextPos, CellType cell) {
        if (elemPos == null || nextPos == null || cell == null) throw new NullPointerException();
        grid.updateGrid(elemPos, nextPos, cell);
    }

    // met à jour la position interne du joueur
    public void changePlayerPosition(Direction d) {
        if (d == null) throw new NullPointerException();
        player.move(d);
    }

    // retourne une copie de la grille sous forme de tableau de char
    public char[][] getGridArray() {
        char[][] copy = new char[grid.getLength()][grid.getWidth()];
        for (int i = 0; i < grid.getLength(); i++)
            for (int j = 0; j < grid.getWidth(); j++)
                copy[i][j] = grid.getElement(i, j);
        return copy;
    }
}
