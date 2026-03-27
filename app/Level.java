package sokoban.app;

import sokoban.core.Direction;
import sokoban.core.Position;
import sokoban.core.World;
import sokoban.core.WorldNode;
import sokoban.entity.Box;
import sokoban.entity.PortalBox;
import sokoban.logic.Action;
import sokoban.logic.GameLogic;
import sokoban.logic.LogicKey;
import sokoban.saving.LoadGame;
import sokoban.saving.StateManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Level {

    /*--------------------------------------------------
                        ATTRIBUTS
    --------------------------------------------------*/

    // liste des mondes du niveau
    private final ArrayList<World> worlds;

    // logique principale du jeu
    private final GameLogic logic;

    // permet de charger le jeu
    private final LoadGame loader;

    private final StateManager sm;

    // indice du monde actuellement joue
    private int actWorld;

    // numero du level
    private int numLevel;

    // etat actuel de la partie
    private LevelState state;

    // AJOUT rec : racine et noeud courant de l'arbre des mondes
    private WorldNode root;
    private WorldNode currentNode;

    // AJOUT rec : mode de victoire choisi
    private VictoryCondition victoryCondition;

    /*--------------------------------------------------
                        GETTERS
    --------------------------------------------------*/

    // MODIFIE rec : utilise currentNode en priorite
    public World getCurrentWorld() {
        return currentNode != null ? currentNode.getWorldact() : worlds.get(actWorld);
    }

    public int getActWorldRef()       {
        return actWorld;
    }
    public LevelState getState()      {
        return state;
    }
    public int getNumLevel()          {
        return numLevel;
    }
    public ArrayList<World> getWorlds() {
        return worlds;
    }

    // AJOUT rec
    public WorldNode getCurrentNode() {
        return currentNode;
    }
    public WorldNode getRoot()        {
        return root;
    }

    /*--------------------------------------------------
                        CONSTRUCTEUR
    --------------------------------------------------*/

    public Level(int numLevel, StateManager sm) {
        worlds = new ArrayList<World>();
        logic = GameLogic.logic;
        loader = LoadGame.gameLoader;
        this.sm = sm;
        this.numLevel = numLevel;
        actWorld = 0;
        state = LevelState.RUNNING;

        // AJOUT rec
        this.root             = null;
        this.currentNode      = null;
        this.victoryCondition = VictoryCondition.ALL_WORLDS;
    }

    /*--------------------------------------------------
                        INIT
    --------------------------------------------------*/

    // MODIFIE rec : appelle buildTree() apres le chargement
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

        // AJOUT rec
        buildTree();
    }

    /*--------------------------------------------------
            AJOUT rec : construction de l'arbre
    --------------------------------------------------*/

    private void buildTree() {
        if (worlds.isEmpty()) return;

        root        = new WorldNode(worlds.get(0));
        currentNode = root;

        buildTreeRecursive(root, 1);
    }

    private int buildTreeRecursive(WorldNode node, int nextIndex) {
        for (Box box : node.getWorldact().getBoxes()) {
            if (!(box instanceof PortalBox)) continue;
            if (nextIndex >= worlds.size()) break;

            PortalBox portalBox = (PortalBox) box;
            WorldNode childNode = new WorldNode(worlds.get(nextIndex));
            node.addChild(childNode, portalBox);

            nextIndex = buildTreeRecursive(childNode, nextIndex + 1);
        }
        return nextIndex;
    }

    /*--------------------------------------------------
            AJOUT rec : navigation dans l'arbre
    --------------------------------------------------*/

    public void enterBox(Box box) {
        if (box == null) throw new NullPointerException();
        if (!(box instanceof PortalBox)) return;

        PortalBox portalBox   = (PortalBox) box;
        WorldNode destination = portalBox.traverse(currentNode);

        if (destination == null) return;

        currentNode = destination;
        actWorld    = currentNode.getWorldact().getWorldRef();
    }

    /*--------------------------------------------------
                        STATE
    --------------------------------------------------*/

    public boolean isRunning() { return state == LevelState.RUNNING; }
    public void pause()        {
        state = LevelState.PAUSED;
    }
    public void resume()       {
            state = LevelState.RUNNING;
    }
    public void stop()         {
        state = LevelState.STOPPED;
    }
    public void victory()      {
        state = LevelState.WON;
    }

    public void changeActWorld(int ref) {
        if (ref < 0 || ref >= worlds.size()) {
            throw new IndexOutOfBoundsException();
        }
        actWorld = ref;
    }

    /*--------------------------------------------------
            MODIFIE rec : checkVictory avec modes
    --------------------------------------------------*/

    // verifie la victoire selon le mode choisi
    public boolean checkVictory() {
        switch (victoryCondition) {
            case ALL_WORLDS:
                return checkVictoryDFS(root);
            case LEAVES_ONLY:
                return checkVictoryFirstLeaf(root);
            case BFS_ALL:
                return checkVictoryBFS(root);
            case ROOT_ONLY:
                return root.getWorldact().allBoxesInTarget();
            default:
                return false;
        }
    }

    // DFS : tous les mondes doivent etre resolus
    private boolean checkVictoryDFS(WorldNode node) {
        if (!node.getWorldact().allBoxesInTarget()) return false;
        for (WorldNode child : node.getChildren()) {
            if (!checkVictoryDFS(child)) return false;
        }
        return true;
    }

    // LEAVES_ONLY : des qu'une feuille est resolue -> victoire
    private boolean checkVictoryFirstLeaf(WorldNode node) {
        if (node.isLeaf()) return node.getWorldact().allBoxesInTarget();
        for (WorldNode child : node.getChildren()) {
            if (checkVictoryFirstLeaf(child)) return true;
        }
        return false;
    }

    // BFS : tous les mondes niveau par niveau
    private boolean checkVictoryBFS(WorldNode root) {
        Queue<WorldNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            WorldNode node = queue.poll();
            if (!node.getWorldact().allBoxesInTarget()) return false;
            for (WorldNode child : node.getChildren()) queue.add(child);
        }
        return true;
    }

    // AJOUT rec : setter du mode de victoire
    public void setVictoryCondition(VictoryCondition vc) {
        this.victoryCondition = vc;
    }

    /*--------------------------------------------------
                        ACTIONS
    --------------------------------------------------*/

    // execute une action utilisateur simple (deplacement, pause...)
    // MODIFIE rec : passe `this` a GameLogic
    public Action executeUserAction(LogicKey key) {
        if (key == null) {
            throw new NullPointerException();
        }
        if (state != LevelState.RUNNING) {
            return Action.NOTHING;
        }

        Action result = logic.executeUserAction(key, getCurrentWorld(), this);

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
    // MODIFIE rec : passe `this` a GameLogic
    public Action executeMove(Direction d) {
        Action result = logic.movePlayer(d, getCurrentWorld(), this);

        if (result == Action.BOX_IN_TARGET) {
            if (checkVictory()) {
                victory();
            }
        }

        return result;
    }

    /*--------------------------------------------------
                    SAVE / UNDO
    --------------------------------------------------*/

    public ArrayList<World> getWorlds_() {
        return worlds; }

    public void replaceWorld(int ref, World world) {
        worlds.set(ref, world);
    }

    public void saveMove() {
        sm.saveUndoSnapshot(getCurrentWorld());
    }

    public void undo() {
        sm.undo(getCurrentWorld());
    }
}