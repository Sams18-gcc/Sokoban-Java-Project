package sokoban.app;

import sokoban.core.Direction;
import sokoban.core.Position;
import sokoban.core.World;
import sokoban.logic.ActionCategory;
import sokoban.logic.ResultOfAction;
import sokoban.logic.GameLogic;
import sokoban.logic.LogicKey;
import sokoban.saving.LoadGame;
import sokoban.saving.StateManager;

import java.util.ArrayList;
import java.util.List;

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

    // renvoie le numero du level
    public int getNumLevel() {

        return numLevel;
    }

    // renvoie la liste des mondes du niveau
    public ArrayList<World> getWorlds() {

        return worlds;
    }

    // renvoie la reference du monde actuellement actif
    public int getActWorldRef() {

        return actWorld;
    }

    // renvoie l'etat actuel du niveau
    public LevelState getState() {

        return state;
    }

    // verifie si le niveau est en cours d'execution
    public boolean isRunning() {

        return state == LevelState.RUNNING;
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
        saveState();
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

    // remplace un monde a une position donnee
    public void replaceWorld(int ref, World world) {

        worlds.set(ref, world);

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
    public ResultOfAction handleUserAction(LogicKey k) {
        if (k == null) {
            throw new NullPointerException();
        }

        if (state != LevelState.RUNNING) {
            return ResultOfAction.NOTHING;
        }

        ActionCategory actionCategory = classifyAction(k);

        switch(actionCategory) {
            case MOVE:
                return handleMoveAction(k);

            case PATH_FINDING:
                return ResultOfAction.PATH_FINDING_REQUESTED;

            case INTERFACE:
                return handleInterfaceAction(k);

            case STATE:
                return handleStateAction(k);

            default:
                return ResultOfAction.NOTHING;
        }

    }

    public ActionCategory classifyAction(LogicKey k) {
        switch (k) {
            case MOVE_DOWN:
            case MOVE_LEFT:
            case MOVE_RIGHT:
            case MOVE_UP:
                return ActionCategory.MOVE;

            case LOAD:
            case SAVE:
            case RELOAD:
            case UNDO:
                return ActionCategory.STATE;


            case ESCAPE:
                return ActionCategory.INTERFACE;

            case FIND_PATH:
                return ActionCategory.PATH_FINDING;

            default:
                return ActionCategory.UNHANDLED;

        }
    }

    // retourne vrai si le move est possible en le simulant
    public boolean simulateMove(LogicKey k) {
        int worldLength = getCurrentWorld().getGrid().getLength();
        int worldWidth = getCurrentWorld().getGrid().getWidth();

        World copy = new World(worldLength, worldWidth, 0);
        copy.loadWorld(getCurrentWorld().getGridArray());

        ResultOfAction resultSimulation = logic.handleMoveKey(k, copy);

        return resultSimulation == ResultOfAction.MOVED
                || resultSimulation == ResultOfAction.BOX_IN_TARGET;
    }


    public ResultOfAction handleMoveAction(LogicKey k) {
        if (!simulateMove(k)) {
            return ResultOfAction.BLOCKED;
        }

        saveState();

        ResultOfAction resultOfAction = logic.handleMoveKey(k, getCurrentWorld());

        if (checkVictory()) {
            return ResultOfAction.WON;
        }

        return resultOfAction;
    }

    public ResultOfAction handleInterfaceAction(LogicKey k) {
        switch (k) {
            case ESCAPE:
                pause();
                return ResultOfAction.PAUSED;

            default:
                return ResultOfAction.NOTHING;
        }

        /* J'ai fait un switch dans le cas ou on voudrait
        ajouter d'autres fonctionnalités */
    }

    public ResultOfAction handleStateAction(LogicKey k) {

        switch (k) {
            case SAVE:
                saveGame();
                return ResultOfAction.SAVED;


            case LOAD:
                loadGame();
                return ResultOfAction.LOADED;


            case RELOAD:
                reloadGame();
                return ResultOfAction.RELOADED;


            case UNDO:
                undo();
                return ResultOfAction.UNDONE;


            default:
                return ResultOfAction.NOTHING;

        }
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
    public ResultOfAction executeMove(Direction d) {

        ResultOfAction result = logic.movePlayer(d, getCurrentWorld());

        // si une box atteint un but, on verifie les autres
        if (result == ResultOfAction.BOX_IN_TARGET) {
            if (checkVictory()) {
                victory();
            }
        }

        return result;
    }

    // sauvegarde l'etat courant pour un futur undo
    public void saveState() {

        sm.saveUndoSnapshot(getCurrentWorld());
    }

    // sauvegarde la partie complete
    public void saveGame() {
        sm.save(this);
    }

    // charge une partie sauvegardee
    public void loadGame() {
        sm.load(this);

    }

    // recharge une partie fraiche depuis le debut
    public void reloadGame() {
        sm.loadFresh(this);
    }

    // annule la derniere action
    public void undo() {
        sm.undo(getCurrentWorld());
    }
}