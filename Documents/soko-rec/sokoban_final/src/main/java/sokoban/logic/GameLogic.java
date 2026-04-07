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

// Logique principale du jeu — singleton.
// ajout rec — les méthodes acceptent désormais un Level en paramètre
//             pour gérer les traversées de portails inter-mondes.
//             Retourne Action (équivalent étendu de ResultOfAction).
public class GameLogic {

    /*--------------------------------------------------
                        SINGLETON
    --------------------------------------------------*/
    public static final GameLogic logic = new GameLogic();

    private GameLogic() {}

    /*--------------------------------------------------
                        METHODES
    --------------------------------------------------*/

    // traduit une LogicKey en action de jeu.
    // ajout rec — délègue les TRAVERSE_CHILD / TRAVERSE_PARENT à Level
    public Action executeUserAction(LogicKey u, World world, Level level) {
        if (u == null || world == null || level == null) throw new NullPointerException();

        switch (u) {
            case MOVE_UP:    return movePlayer(Direction.UP,    world, level);
            case MOVE_DOWN:  return movePlayer(Direction.DOWN,  world, level);
            case MOVE_LEFT:  return movePlayer(Direction.LEFT,  world, level);
            case MOVE_RIGHT: return movePlayer(Direction.RIGHT, world, level);

            // ajout rec — navigation vers un monde enfant
            case TRAVERSE_CHILD_UP:    return level.traverseToChild(Direction.UP);
            case TRAVERSE_CHILD_DOWN:  return level.traverseToChild(Direction.DOWN);
            case TRAVERSE_CHILD_LEFT:  return level.traverseToChild(Direction.LEFT);
            case TRAVERSE_CHILD_RIGHT: return level.traverseToChild(Direction.RIGHT);

            // ajout rec — remontée vers le monde parent
            case TRAVERSE_PARENT_UP:    return level.traverseToParent(Direction.UP);
            case TRAVERSE_PARENT_DOWN:  return level.traverseToParent(Direction.DOWN);
            case TRAVERSE_PARENT_LEFT:  return level.traverseToParent(Direction.LEFT);
            case TRAVERSE_PARENT_RIGHT: return level.traverseToParent(Direction.RIGHT);

            case ESCAPE:          return Action.PAUSE;
            case FIND_PATH:       return Action.PATH_FINDING_REQUESTED;
            case FIND_PATH_MODE:  return Action.PATH_MODE_REQUESTED;   // ajout rec
            case SAVE:            return Action.SAVED;
            case LOAD:            return Action.LOADED;
            case RELOAD:          return Action.RELOADED;
            case UNDO:            return Action.UNDONE;

            default: return Action.NOTHING;
        }
    }

    // déplace le joueur dans la direction donnée.
    // Retourne :
    //   BLOCKED      si le mouvement est impossible
    //   MOVED        si le joueur bouge normalement
    //   BOX_IN_TARGET si une boîte entre dans une target
    //   TRAVERSE     si le joueur traverse une PortalBox ouverte (ajout rec)
    public final Action movePlayer(Direction d, World world, Level level) {
        if (d == null || world == null || level == null) throw new NullPointerException();

        if (!world.checkMove(d)) return Action.BLOCKED;

        Position actualPos = world.getPlayerPosition();
        Position nextPos   = world.getPlayerPosition();
        nextPos.translate(d);

        if (world.isBox(nextPos)) {
            Action result = interactWithBox(d, nextPos, world, level);

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

    // déplace une boîte dans la direction donnée.
    // Retourne true si la boîte finit sur une target.
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

    // décide si on pousse ou on traverse une boîte.
    // ajout rec — une PortalBox ouverte se pousse comme une boîte normale
    public Action interactWithBox(Direction d, Position pos, World world, Level level) {
        if (d == null || pos == null || world == null || level == null)
            throw new NullPointerException();

        Box box = world.getBoxatPosition(pos);
        if (box == null) throw new IllegalStateException();

        boolean inTarget = moveBox(d, pos, world);
        return inTarget ? Action.BOX_IN_TARGET : Action.MOVED;
    }

    // calcule un chemin entre deux positions via A*
    public List<Direction> executePathFinding(World world, Position start, Position dest) {
        if (world == null || start == null || dest == null) throw new NullPointerException();
        return PathSeek.findShortestPath(world, start, dest);
    }
}
