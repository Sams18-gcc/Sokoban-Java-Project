package sokoban.logic;

import sokoban.core.CellType;
import sokoban.core.Direction;
import sokoban.core.Position;
import sokoban.core.World;
import sokoban.entity.Box;
import sokoban.pathfinding.PathSeek;

import java.util.List;

public class GameLogic {


    public static final GameLogic logic = new GameLogic();
    private GameLogic(){};


    public final Action movePlayer(Direction d, World world) {
        if (d == null || world == null) {
            throw new NullPointerException();
        }

        if (!world.checkMove(d)) {
            return Action.BLOCKED;
        }

        boolean boxInTarget = false;
        Position actualPos = world.getPlayerPosition();
        Position nextPos = world.getPlayerPosition();
        nextPos.translate(d);

        if (world.isBox(nextPos)) {
            boxInTarget = moveBox(d, nextPos, world);
        }

        world.updateCells(actualPos, nextPos);
        world.updateWorldData(actualPos, nextPos, world.getCellatPosition(actualPos).getCellType());
        world.changePlayerPosition(d);

        if (boxInTarget) {
            return Action.BOX_IN_TARGET;
        }

        return Action.MOVED;
    }


    public final boolean moveBox(Direction d, Position pos, World world) {
        if (d == null || pos == null || world == null || world == null) throw new NullPointerException();


        Position actualPos = new Position(pos.getY(), pos.getX());
        Position nextPos = new Position(pos.getY(), pos.getX());
        nextPos.translate(d);
        world.updateWorldData(actualPos, nextPos, world.getCellatPosition(actualPos).getCellType());
        Box box = world.getBoxatPosition(pos);
        if (box == null)
            throw new IllegalStateException();
        else box.move(d);
        world.updateCells(actualPos, nextPos);
        if (world.getCellatPosition(nextPos).getCellType() == CellType.TARGET) {
            box.setInTarget();
            return true;

        } else {
            box.setOutOfTarget();
            return false;

        }

    }

    public Action executeUserAction(LogicKey u, World world )
    {

        Direction d = null;

        switch (u) {
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
            case ESCAPE:
                return Action.PAUSE;
        }

       return movePlayer(d, world);
    }

    public List<Direction> executePathFinding(World world, Position start, Position dest)
    {
        if(world == null || start == null || dest == null)
            throw new NullPointerException();
        List<Direction> path = PathSeek.findShortestPath(world, start, dest);
        return path;
    }







}