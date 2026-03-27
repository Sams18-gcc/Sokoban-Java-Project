package sokoban.logic;

import sokoban.app.Level;
import sokoban.core.CellType;
import sokoban.core.Direction;
import sokoban.core.Position;
import sokoban.core.World;
import sokoban.entity.Box;
import sokoban.entity.PortalBox;
import sokoban.pathfinding.PathSeek;

import java.util.List;

public class GameLogic {

    /*--------------------------------------------------
                        SINGLETON
    --------------------------------------------------*/
    public static final GameLogic logic = new GameLogic();

    /*--------------------------------------------------
                        CONSTRUCTEUR
    --------------------------------------------------*/
    private GameLogic() {}

    /*--------------------------------------------------
                        METHODES
    --------------------------------------------------*/

    // traduit une action logique en comportement du jeu
    public Action executeUserAction(LogicKey u, World world, Level level) {
        if (u == null || world == null || level == null) throw new NullPointerException();

        Direction d = null;

        switch (u) {
            case MOVE_UP:    d = Direction.UP;    break;
            case MOVE_DOWN:  d = Direction.DOWN;  break;
            case MOVE_LEFT:  d = Direction.LEFT;  break;
            case MOVE_RIGHT: d = Direction.RIGHT; break;
            case ESCAPE:     return Action.PAUSE;
        }

        return movePlayer(d, world, level);
    }

    /*
     * Gere le deplacement du joueur.
     * MODIFIÉ : prend Level en paramètre pour gérer les PortalBox
     * Retourne :
     * - BLOCKED      si le mouvement est impossible
     * - MOVED        si le joueur bouge normalement
     * - BOX_IN_TARGET si une boite entre dans une target
     * - TRAVERSE     si le joueur traverse une PortalBox
     */
    public final Action movePlayer(Direction d, World world, Level level) {
        if (d == null || world == null || level == null) throw new NullPointerException();

        if (!world.checkMove(d)) return Action.BLOCKED;

        Position actualPos = world.getPlayerPosition();
        Position nextPos   = world.getPlayerPosition();
        nextPos.translate(d);

        if (world.isBox(nextPos)) {
            Action result = interactWithBox(d, nextPos, world, level);

            // TRAVERSE : le joueur avance sur la case du portail
            // MOVED / BOX_IN_TARGET : la boîte a bougé, le joueur aussi
            if (result == Action.TRAVERSE || result == Action.MOVED || result == Action.BOX_IN_TARGET) {
                world.updateCells(actualPos, nextPos);
                world.updateWorldData(actualPos, nextPos,
                        world.getCellatPosition(actualPos).getCellType());
                world.changePlayerPosition(d);
            }

            return result;
        }

        // déplacement normal sans boîte
        world.updateCells(actualPos, nextPos);
        world.updateWorldData(actualPos, nextPos,
                world.getCellatPosition(actualPos).getCellType());
        world.changePlayerPosition(d);

        return Action.MOVED;
    }

    /*
     * Deplace une boite dans la direction donnee.
     * Retourne true si la boite finit dans une target.
     */
    public final boolean moveBox(Direction d, Position pos, World world) {
        if (d == null || pos == null || world == null) throw new NullPointerException();

        Position actualPos = new Position(pos.getY(), pos.getX());
        Position nextPos   = new Position(pos.getY(), pos.getX());
        nextPos.translate(d);

        world.updateWorldData(actualPos, nextPos,
                world.getCellatPosition(actualPos).getCellType());

        Box box = world.getBoxatPosition(pos);
        if (box == null) throw new IllegalStateException();

        box.move(d);
        world.updateCells(actualPos, nextPos);

        if (world.getCellatPosition(nextPos).getCellType() == CellType.TARGET) {
            box.setInTarget();
            return true;
        } else {
            box.setOutOfTarget();
            return false;
        }
    }

    /*
     * Décide si on traverse ou on pousse une boîte.
     * MODIFIÉ : traverse automatiquement si PortalBox ouverte
     */
    public Action interactWithBox(Direction d, Position pos, World world, Level level) {
        if (d == null || pos == null || world == null || level == null)
            throw new NullPointerException();

        Box box = world.getBoxatPosition(pos);
        if (box == null) throw new IllegalStateException();

        // PortalBox ouverte → traversée automatique
        if (box instanceof PortalBox && ((PortalBox) box).isOpen()) {
            level.enterBox(box);
            return Action.TRAVERSE;
        }

        // boîte normale → on pousse
        boolean inTarget = moveBox(d, pos, world);
        return inTarget ? Action.BOX_IN_TARGET : Action.MOVED;
    }

    // calcule un chemin entre 2 positions
    public List<Direction> executePathFinding(World world, Position start, Position dest) {
        if (world == null || start == null || dest == null) throw new NullPointerException();
        return PathSeek.findShortestPath(world, start, dest);
    }
}