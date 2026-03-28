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
    private final ArrayList<World> worlds;
    private int actWorld;

    private WorldNode root;
    private WorldNode currentNode;
    private VictoryCondition  victoryCondition;
    private final GameLogic logic;
    private final LoadGame loader;
    private final StateManager sm;

    private int numLevel;
    private LevelState state;

    /*--------------------------------------------------
                       GETTERS
   --------------------------------------------------*/
    public World getCurrentWorld() {
        return currentNode != null ? currentNode.getWorldact() : worlds.get(actWorld);
    }

    public WorldNode getCurrentNode() {
        return currentNode; }
    public WorldNode getRoot()        {
        return root; }
    public LevelState getState()      {
        return state; }
    public int getNumLevel()          {
        return numLevel; }
    public int getActWorldRef()       {
        return actWorld; }
    public ArrayList<World> getWorlds() {
        return worlds; }

    /*--------------------------------------------------
                        CONSTRUCTEUR
    --------------------------------------------------*/
    public Level(int numLevel, StateManager sm) {
        this.numLevel = numLevel;
        this.sm       = sm;
        this.logic    = GameLogic.logic;
        this.loader   = LoadGame.gameLoader;
        this.state    = LevelState.RUNNING;
        this.root        = null;
        this.currentNode = null;
        this.actWorld    = 0;
        this.worlds      = new ArrayList<>();
        this.victoryCondition  = VictoryCondition.ALL_WORLDS;
    }

    /*--------------------------------------------------
                        INIT
    --------------------------------------------------*/
    public void init() {
        int index = 0;
        if (!loader.loadGrids(numLevel)) {
            System.out.println("ERREUR : chargement échoué pour le niveau " + numLevel);
            return;
        }

        ArrayList<char[][]> grids = loader.getGrids();
        for (char[][] g : grids) {
            World w = new World(g.length, g[0].length, index);
            w.loadWorld(g);
            worlds.add(w);
            index++;
        }

        buildTree();
    }

    /*--------------------------------------------------
                CONSTRUCTION DE L'ARBRE
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
                NAVIGATION DANS L'ARBRE
    --------------------------------------------------*/
    public void enterBox(Box box) {
        if (box == null) throw new NullPointerException();
        if (!(box instanceof PortalBox)) return;

        PortalBox portalBox = (PortalBox) box;
        WorldNode destination = portalBox.traverse(currentNode);

        if (destination == null) return;

        currentNode = destination;
        actWorld    = currentNode.getWorldact().getWorldRef();
    }



    /*--------------------------------------------------
                        STATE
    --------------------------------------------------*/
    public boolean isRunning() { return state == LevelState.RUNNING; }
    public void pause()   {
        state = LevelState.PAUSED;  }
    public void resume()  {
        state = LevelState.RUNNING; }
    public void stop()    {
        state = LevelState.STOPPED; }
    public void victory() {
        state = LevelState.WON;     }
    /*------------------------------------------------------
                          changeActWorld
       --------------------------------------------------*/
    public void changeActWorld(int ref) {
        if (ref < 0 || ref >= worlds.size()) throw new IndexOutOfBoundsException();
        actWorld = ref;
    }

    /*--------------------------------------------------
                   CHECKVICTORY — récursivité
    --------------------------------------------------*/



    // vérifie la victoire selon le mode choisi
    public boolean checkVictory() {
        switch (victoryCondition) {
            case ALL_WORLDS:  return checkVictoryDFS(root);
            case LEAVES_ONLY: return checkVictoryFirstLeaf(root);
            case BFS_ALL:     return checkVictoryBFS(root);
            case ROOT_ONLY:   return root.getWorldact().allBoxesInTarget();
            default:          return false;
        }
    }

    // DFS : TOUS les mondes de l'arbre doivent être résolus
    private boolean checkVictoryDFS(WorldNode node) {
        if (!node.getWorldact().allBoxesInTarget()) return false;

        for (WorldNode child : node.getChildren()) {
            if (!checkVictoryDFS(child)) return false;
        }

        return true;
    }

    // LEAVES_ONLY : dès qu'on atteint UNE feuille résolue → victoire
    private boolean checkVictoryFirstLeaf(WorldNode node) {

        // si c'est une feuille ET résolue → victoire immédiate
        if (node.isLeaf()) {
            return node.getWorldact().allBoxesInTarget();
        }

        // sinon on descend → dès qu'un enfant retourne true on gagne
        for (WorldNode child : node.getChildren()) {
            if (checkVictoryFirstLeaf(child)) return true;
        }

        return false;
    }

    // BFS : parcours niveau par niveau, tous les mondes doivent être résolus
    private boolean checkVictoryBFS(WorldNode root) {
        Queue<WorldNode> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            WorldNode node = queue.poll();
            if (!node.getWorldact().allBoxesInTarget()) return false;

            for (WorldNode child : node.getChildren()) {
                queue.add(child);
            }
        }

        return true;
    }

    // setter pour choisir le mode de victoire
    public void setVictoryCondition(VictoryCondition vc) {
        this.victoryCondition = vc;
    }

    /*--------------------------------------------------
                    ACTIONS
    --------------------------------------------------*/
    public Action executeUserAction(LogicKey key) {
        if (key == null) throw new NullPointerException();
        if (state != LevelState.RUNNING) return Action.NOTHING;

        // on passe `this` à GameLogic pour qu'il puisse appeler enterBox si besoin
        Action result = logic.executeUserAction(key, getCurrentWorld(), this);

        if (result == Action.PAUSE) {
            pause();
            return result;
        }

        if (checkVictory()) victory();

        return result;
    }

    public List<Direction> executePathFinding(Position dest) {
        if (dest == null) throw new NullPointerException();
        if (state != LevelState.RUNNING) return null;

        return logic.executePathFinding(
                getCurrentWorld(),
                getCurrentWorld().getPlayerPosition(),
                dest
        );
    }

    public Action executeMove(Direction d) {
        // on passe `this` pour la cohérence avec la nouvelle signature
        Action result = logic.movePlayer(d, getCurrentWorld(), this);

        if (result == Action.BOX_IN_TARGET) {
            if (checkVictory()) victory();
        }

        return result;
    }

    /*--------------------------------------------------
                    SAVE / UNDO
    --------------------------------------------------*/
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