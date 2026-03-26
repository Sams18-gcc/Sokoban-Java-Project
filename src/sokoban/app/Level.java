package sokoban.app;

import sokoban.core.Direction;
import sokoban.core.Position;
import sokoban.core.World;
import sokoban.logic.Action;
import sokoban.logic.GameLogic;
import sokoban.logic.LogicKey;
import sokoban.saving.LoadGame;
import sokoban.saving.StateManager;

import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

public class Level {
    // liste des mondes du niveau
    private final ArrayList<World> worlds;

    // logique principale du jeu
    private final GameLogic logic;

    // permet de charger le jeu
    private final LoadGame loader;

    private final StateManager sm;

    // indice du monde actuellement joue
    private int actWorld;

    // nulmero du level
    private int numLevel;

    // etat actuel de la partie
    private LevelState state;

    public Level(int numLevel, StateManager sm) {


        worlds = new ArrayList<World>();
        logic = GameLogic.logic;
        loader = LoadGame.gameLoader;
        this.sm = sm;
        this.numLevel = numLevel;
        actWorld = 0;
        state = LevelState.RUNNING;
    }
//YANIS
    public LoadGame getLoader() {
        return loader;
    }

    // initialise le monde courant
    public void init() {

        int index = 0;
        if (!loader.loadGrids(numLevel))
            return;

        ArrayList<char[][]> grids = loader.getGrids();
        for (char[][] g : grids) {
            World w = new World(g.length, g[0].length, index);
            w.loadWorld(g);
            worlds.add(w);
            index++;
        }
    }

    // renvoie le monde actuellement actif
    public World getCurrentWorld() {
        return worlds.get(actWorld);
    }

    // change le monde courant
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

    // mettre le jeu en pause
    public void pause() {
        state = LevelState.PAUSED;
    }

    // continuer le jeu
    public void resume() {
        state = LevelState.RUNNING;
    }

    // arret du jeu
    public void stop() {
        state = LevelState.STOPPED;
    }

    // victoire
    public void victory() {
        state = LevelState.WON;
    }

    // verifie si toutes les boites de tous les mondes sont bien placees
    public boolean checkVictory() {
        for (World w : worlds) {
            if (!w.allBoxesInTarget()) {
                return false;
            }
        }
        return true;
    }

    // execute une action utilisateur simple (deplacement, pause...)
    public Action executeUserAction(LogicKey key) {
        if (key == null) {
            throw new NullPointerException();
        }
        // si la partie n'est pas en cours, on fait rien
        if (state != LevelState.RUNNING) {
            return Action.NOTHING;
        }
        // recuperer l'action qui vient d'etre executee

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

    // calcule un chemin vers une destination donnee
    public List<Direction> executePathFinding(Position dest) {
        if (dest == null) {
            throw new NullPointerException();
        }
        // si la partie n'est pas en cours, on fait rien
        if (state != LevelState.RUNNING) {
            return null;
        }

        return logic.executePathFinding(
                getCurrentWorld(),
                getCurrentWorld().getPlayerPosition(),
                dest
        );
    }

    // execute directement un mouvement dans une direction
    public Action executeMove(Direction d) {

        Action result = logic.movePlayer(d, getCurrentWorld());

        // si une box atteint un but, on verifie les autres
        if (result == Action.BOX_IN_TARGET) {
            if (checkVictory()) {
                victory();
            }
        }

        return result;
    }

    //------------------------------------j'ai ajouté deux getters
    public int getNumLevel() {
        return numLevel;
    }



    public ArrayList<World> getWorlds() {
        return worlds;
    }

    public void replaceWorld(int ref, World world)
    {

        worlds.set(ref, world);

    }

    public void saveMove()
    {
        sm.saveUndoSnapshot(getCurrentWorld());
    }

    public void undo()
    {
        sm.undo(getCurrentWorld());
    }
}