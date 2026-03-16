package sokoban.app;

import sokoban.core.Direction;
import sokoban.core.Position;
import sokoban.core.World;
import sokoban.logic.Action;
import sokoban.logic.GameLogic;
import sokoban.logic.LogicKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Level {
    private final ArrayList<World> worlds;
    private final GameLogic logic;
    private int actWorld;
    private LevelState state;

    public Level(Collection<World> c) {
        if (c == null || c.contains(null)) {
            throw new NullPointerException();
        }

        worlds = new ArrayList<>(c);
        logic = GameLogic.logic;
        actWorld = 0;
        state = LevelState.RUNNING;
    }

    public void init() {
        getCurrentWorld().loadWorld();
    }

    public World getCurrentWorld() {
        return worlds.get(actWorld);
    }

    public void changeActWorld(int ref) {
        if (ref < 0 || ref >= worlds.size()) {
            throw new IndexOutOfBoundsException();
        }
        actWorld = ref;
    }

    public int getActWorldRef() {
        return actWorld;
    }

    public LevelState getState() {
        return state;
    }

    public boolean isRunning() {
        return state == LevelState.RUNNING;
    }

    public void pause() {
        state = LevelState.PAUSED;
    }

    public void resume() {
        state = LevelState.RUNNING;
    }

    public void stop() {
        state = LevelState.STOPPED;
    }

    public void victory()
    {
        state = LevelState.WON;
    }

    public boolean checkVictory() {
        for (World w : worlds) {
            if (!w.allBoxesInTarget()) {
                return false;
            }
        }
        return true;
    }

    public Action executeUserAction(LogicKey key) {
        if (key == null) {
            throw new NullPointerException();
        }

        if (state != LevelState.RUNNING) {
            return Action.NOTHING;
        }

        Action result = logic.executeUserAction(key, getCurrentWorld());

        if (result == Action.PAUSE) {
            pause();
            return result;
        }

        if (checkVictory()) {
            victory();
        }

        return result;
    }

    public List<Direction> executePathFinding(Position dest) {
        if (dest == null) {
            throw new NullPointerException();
        }

        if (state != LevelState.RUNNING) {
            return null;
        }

        return logic.executePathFinding(
                getCurrentWorld(),
                getCurrentWorld().getPlayerPosition(),
                dest
        );
    }

    public Action executeMove(Direction d)
    {
       Action result = logic.movePlayer(d, worlds.get(actWorld));
       if(result == Action.BOX_IN_TARGET)
           if(checkVictory())
               victory();
       return result;
    }
}