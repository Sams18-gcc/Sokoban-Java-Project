package sokoban.logic;

import sokoban.core.CellType;
import sokoban.core.Direction;
import sokoban.core.Position;
import sokoban.core.World;
import sokoban.entity.Box;
import sokoban.pathfinding.PathSeek;


import java.util.List;

/*
 * Classe qui contient les regles principales du jeu.
 * Elle applique les actions sur le monde et renvoie un resultat.
 */
public class GameLogic {

    // instance unique de la logique
    public static final GameLogic logic = new GameLogic();

    private GameLogic(){}

    /*
     * Gere le deplacement du joueur.
     * Retourne :
     * - BLOCKED si le mouvement est impossible
     * - MOVED si le joueur bouge normalement
     * - BOX_IN_TARGET si une boite entre dans une target
     */
    public final ResultOfAction movePlayer(Direction d, World world) {
        if (d == null || world == null) {
            throw new NullPointerException();
        }

        if (!world.checkMove(d)) {
            return ResultOfAction.BLOCKED;
        }

        boolean boxInTarget = false;
        Position actualPos = world.getPlayerPosition();
        Position nextPos = world.getPlayerPosition();
        nextPos.translate(d);

        // si une boite est devant, on la deplace aussi
        if (world.isBox(nextPos)) {
            boxInTarget = moveBox(d, nextPos, world);
        }

        world.updateCells(actualPos, nextPos);
        world.updateWorldData(actualPos, nextPos, world.getCellatPosition(actualPos).getCellType());
        world.changePlayerPosition(d);


        if (boxInTarget) {
            return ResultOfAction.BOX_IN_TARGET;
        }

        return ResultOfAction.MOVED;
    }

    /*
     * Deplace une boite dans la direction donnee.
     * Retourne true si la boite finit dans une target.
     */
    public final boolean moveBox(Direction d, Position pos, World world) {
        if (d == null || pos == null || world == null) throw new NullPointerException();

        Position actualPos = new Position(pos.getY(), pos.getX());
        Position nextPos = new Position(pos.getY(), pos.getX());
        nextPos.translate(d);

        world.updateWorldData(actualPos, nextPos, world.getCellatPosition(actualPos).getCellType());

        Box box = world.getBoxatPosition(pos);
        if (box == null)
            throw new IllegalStateException();
        else
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

    // traduit une action logique en comportement du jeu
    public ResultOfAction handleMoveKey(LogicKey k, World world) {
        if (k == null || world == null) throw new NullPointerException();

        Direction d = null;

        switch (k) {
            case MOVE_UP:
                d = Direction.UP;
                break;
            case MOVE_DOWN:
                d = Direction.DOWN;
                break;
            case MOVE_LEFT:
                d = Direction.LEFT;
                break;
            case MOVE_RIGHT:
                d = Direction.RIGHT;
                break;
        }

        return movePlayer(d, world);
    }


    // calcule un chemin entre 2 positions
    public List<Direction> executePathFinding(World world, Position start, Position dest) {
        if(world == null || start == null || dest == null)
            throw new NullPointerException();

        return PathSeek.findShortestPath(world, start, dest);
    }
    // a appeler a chaque fois que le joueur bouge

}