package sokoban.app;

import sokoban.core.Direction;
import sokoban.core.PortalLink;
import sokoban.core.Position;
import sokoban.core.World;
import sokoban.core.WorldNode;
import sokoban.entity.Box;
import sokoban.entity.PortalBox;
import sokoban.logic.Action;
import sokoban.logic.ActionCategory;
import sokoban.logic.GameLogic;
import sokoban.logic.LogicKey;
import sokoban.logic.ResultOfAction;
import sokoban.saving.LoadGame;
import sokoban.saving.StateManager;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Level — version récursive.
// ajout rec — gère un arbre de WorldNode construit récursivement (buildTree / buildTreeRecursive).
//             Supporte trois modes de victoire (VictoryCondition) et la navigation
//             inter-mondes (traverseToChild / traverseToParent).
//             Expose handleUserActionGUI() pour rester compatible avec l'interface graphique.
public class Level {

    /*--------------------------------------------------
                        ATTRIBUTS
    --------------------------------------------------*/
    private final ArrayList<World> worlds;
    private       int              actWorld;

    private WorldNode        root;             // ajout rec — racine de l'arbre de mondes
    private WorldNode        currentNode;      // ajout rec — noeud courant dans l'arbre
    private VictoryCondition victoryCondition; // ajout rec — mode de victoire actif

    private final GameLogic    logic;
    private final LoadGame     loader;
    private final StateManager sm;

    private int        numLevel;
    private LevelState state;
    private int        nextPortalId;  // ajout rec — compteur d'ID de portails

    /*--------------------------------------------------
                        GETTERS
    --------------------------------------------------*/
    public World           getCurrentWorld()      { return currentNode != null ? currentNode.getWorldact() : worlds.get(actWorld); }
    public WorldNode       getCurrentNode()       { return currentNode;       }  // ajout rec
    public WorldNode       getRoot()              { return root;              }  // ajout rec
    public LevelState      getState()             { return state;             }
    public int             getNumLevel()          { return numLevel;          }
    public int             getActWorldRef()       { return actWorld;          }
    public ArrayList<World> getWorlds()           { return worlds;            }
    public VictoryCondition getVictoryCondition() { return victoryCondition;  }  // ajout rec

    /*--------------------------------------------------
                        CONSTRUCTEUR
    --------------------------------------------------*/

    // constructeur aligné sur le main : new Level(numLevel, levelsFolder, sm)
    public Level(int numLevel, String levelsFolder, StateManager sm) {
        this.numLevel         = numLevel;
        this.sm               = sm;
        this.logic            = GameLogic.logic;
        this.loader           = LoadGame.gameLoader;
        this.loader.setLevelsFolder(levelsFolder);
        this.state            = LevelState.RUNNING;
        this.root             = null;           // ajout rec
        this.currentNode      = null;           // ajout rec
        this.actWorld         = 0;
        this.worlds           = new ArrayList<>();
        this.victoryCondition = VictoryCondition.ALL_WORLDS; // ajout rec
        this.nextPortalId     = 1;              // ajout rec
    }

    /*--------------------------------------------------
                        INIT
    --------------------------------------------------*/

    // charge les grilles du niveau, crée les World et construit l'arbre récursif
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

        buildTree();    // ajout rec
        saveState();    // snapshot initial pour undo
    }

    /*--------------------------------------------------
            CONSTRUCTION DE L'ARBRE (récursivité)
    --------------------------------------------------*/

    // ajout rec — point d'entrée : crée la racine puis délègue à buildTreeRecursive
    private void buildTree() {
        if (worlds.isEmpty()) return;

        root         = new WorldNode(worlds.get(0));
        currentNode  = root;
        nextPortalId = 1;

        buildTreeRecursive(root, 1);
    }

    // ajout rec — construit l'arbre en profondeur d'abord (DFS).
    // Pour chaque PortalBox du noeud courant, crée un WorldNode enfant
    // et lui affecte un ID de portail unique, puis descend récursivement.
    private int buildTreeRecursive(WorldNode node, int nextIndex) {
        for (Box box : node.getWorldact().getBoxes()) {
            if (!(box instanceof PortalBox)) continue;
            if (nextIndex >= worlds.size()) break;

            PortalBox portalBox = (PortalBox) box;
            portalBox.setPortalId(nextPortalId++);
            WorldNode childNode = new WorldNode(worlds.get(nextIndex));
            node.addChild(childNode, portalBox);

            nextIndex = buildTreeRecursive(childNode, nextIndex + 1);
        }
        return nextIndex;
    }

    /*--------------------------------------------------
            NAVIGATION DANS L'ARBRE
    --------------------------------------------------*/

    // ajout rec — retourne la PortalBox adjacente au joueur dans la direction donnée, ou null
    public PortalBox getAdjacentPortal(Direction direction) {
        if (direction == null) throw new NullPointerException();

        Position portalPos = getCurrentWorld().getPlayerPosition();
        portalPos.translate(direction);

        Box box = getCurrentWorld().getBoxatPosition(portalPos);
        if (!(box instanceof PortalBox)) return null;
        return (PortalBox) box;
    }

    // ajout rec — descend dans le monde enfant lié au portail adjacent.
    // Retourne BLOCKED si aucun portail ouvert n'est adjacent, TRAVERSE sinon.
    public Action traverseToChild(Direction direction) {
        if (direction == null) throw new NullPointerException();

        PortalBox portalBox = getAdjacentPortal(direction);
        if (portalBox == null || !portalBox.isOpen() || portalBox.getNextWorld() == null)
            return Action.BLOCKED;

        moveToNode(portalBox.getNextWorld());
        return Action.TRAVERSE;
    }

    // ajout rec — remonte vers le monde parent depuis le noeud courant.
    // Retourne BLOCKED si on est déjà à la racine, TRAVERSE sinon.
    public Action traverseToParent(Direction direction) {
        if (direction == null) throw new NullPointerException();

        PortalBox portalBox = getAdjacentPortal(direction);
        if (portalBox == null || currentNode == null || currentNode.getParent() == null)
            return Action.BLOCKED;

        moveToNode(currentNode.getParent());
        return Action.TRAVERSE;
    }

    // ajout rec — met à jour currentNode et actWorld en une seule opération
    private void moveToNode(WorldNode destination) {
        if (destination == null) throw new NullPointerException();
        currentNode = destination;
        actWorld    = currentNode.getWorldact().getWorldRef();
    }

    /*--------------------------------------------------
                        STATE
    --------------------------------------------------*/
    public boolean isRunning() { return state == LevelState.RUNNING; }
    public void    pause()     { state = LevelState.PAUSED;  }
    public void    resume()    { state = LevelState.RUNNING; }
    public void    stop()      { state = LevelState.STOPPED; }
    public void    victory()   { state = LevelState.WON;     }

    public void changeActWorld(int ref) {
        if (ref < 0 || ref >= worlds.size()) throw new IndexOutOfBoundsException();
        actWorld = ref;
    }

    /*--------------------------------------------------
         CHECKVICTORY — DFS / LEAVES / ROOT
    --------------------------------------------------*/

    // ajout rec — délègue la vérification selon le VictoryCondition actif
    public boolean checkVictory() {
        switch (victoryCondition) {
            case ALL_WORLDS:  return checkVictoryDFS(root);
            case LEAVES_ONLY: return checkVictoryFirstLeaf(root);
            case ROOT_ONLY:   return root.getWorldact().allBoxesInTarget();
            default:          return false;
        }
    }

    // ajout rec — DFS : TOUS les mondes de l'arbre doivent être résolus
    private boolean checkVictoryDFS(WorldNode node) {
        if (!node.getWorldact().allBoxesInTarget()) return false;
        for (WorldNode child : node.getChildren())
            if (!checkVictoryDFS(child)) return false;
        return true;
    }

    // ajout rec — LEAVES_ONLY : la première feuille entièrement résolue = victoire
    private boolean checkVictoryFirstLeaf(WorldNode node) {
        if (node.isLeaf()) return node.getWorldact().allBoxesInTarget();
        for (WorldNode child : node.getChildren())
            if (checkVictoryFirstLeaf(child)) return true;
        return false;
    }

    // ajout rec — change le mode de victoire à la volée
    public void setVictoryCondition(VictoryCondition vc) { this.victoryCondition = vc; }

    /*--------------------------------------------------
                    ACTIONS
    --------------------------------------------------*/

    // point d'entrée principal : classifie l'action, l'exécute et retourne un Action.
    // ajout rec — gère la catégorie TRAVERSE en plus des catégories du main
    public Action handleUserAction(LogicKey k) {
        if (k == null) throw new NullPointerException();
        if (state != LevelState.RUNNING) return Action.NOTHING;

        switch (classifyAction(k)) {
            case MOVE:          return handleMoveAction(k);
            case TRAVERSE:      return handleTraverseAction(k);  // ajout rec
            case PATH_FINDING:
                if (k == LogicKey.FIND_PATH_MODE) return Action.PATH_MODE_REQUESTED; // ajout rec
                return Action.PATH_FINDING_REQUESTED;
            case INTERFACE:     return handleInterfaceAction(k);
            case STATE:         return handleStateAction(k);
            default:            return Action.NOTHING;
        }
    }

    // ajout rec — classe la LogicKey dans sa catégorie d'action
    public ActionCategory classifyAction(LogicKey k) {
        switch (k) {
            case MOVE_UP: case MOVE_DOWN: case MOVE_LEFT: case MOVE_RIGHT:
                return ActionCategory.MOVE;

            // ajout rec — 8 clés de traversée portail
            case TRAVERSE_CHILD_UP:  case TRAVERSE_CHILD_DOWN:
            case TRAVERSE_CHILD_LEFT: case TRAVERSE_CHILD_RIGHT:
            case TRAVERSE_PARENT_UP: case TRAVERSE_PARENT_DOWN:
            case TRAVERSE_PARENT_LEFT: case TRAVERSE_PARENT_RIGHT:
                return ActionCategory.TRAVERSE;

            case LOAD: case SAVE: case RELOAD: case UNDO:
                return ActionCategory.STATE;

            case ESCAPE:
                return ActionCategory.INTERFACE;

            case FIND_PATH:
            case FIND_PATH_MODE:         // ajout rec
                return ActionCategory.PATH_FINDING;

            default:
                return ActionCategory.UNHANDLED;
        }
    }

    // simule un mouvement sans modifier l'état réel — utilisé avant d'appliquer le déplacement
    public boolean simulateMove(LogicKey k) {
        int worldLength = getCurrentWorld().getGrid().getLength();
        int worldWidth  = getCurrentWorld().getGrid().getWidth();

        World copy = new World(worldLength, worldWidth, 0);
        copy.loadWorld(getCurrentWorld().getGridArray());

        Direction d = logicKeyToDirection(k);
        if (d == null) return false;
        return copy.checkMove(d);
    }

    // convertit une LogicKey de mouvement en Direction
    private Direction logicKeyToDirection(LogicKey k) {
        switch (k) {
            case MOVE_UP:    return Direction.UP;
            case MOVE_DOWN:  return Direction.DOWN;
            case MOVE_LEFT:  return Direction.LEFT;
            case MOVE_RIGHT: return Direction.RIGHT;
            default:         return null;
        }
    }

    // exécute un mouvement : simule d'abord, sauvegarde l'état, puis applique.
    // Vérifie la victoire après chaque BOX_IN_TARGET ou MOVED.
    public Action handleMoveAction(LogicKey k) {
        if (!simulateMove(k)) return Action.BLOCKED;

        saveState();

        Action result = logic.executeUserAction(k, getCurrentWorld(), this);

        if (result == Action.BOX_IN_TARGET || result == Action.MOVED) {
            if (checkVictory()) {
                victory();
                return Action.WON;
            }
        }
        return result;
    }

    // ajout rec — délègue la traversée de portail à GameLogic
    public Action handleTraverseAction(LogicKey k) {
        return logic.executeUserAction(k, getCurrentWorld(), this);
    }

    // gère les actions d'interface (pause, etc.)
    public Action handleInterfaceAction(LogicKey k) {
        switch (k) {
            case ESCAPE: pause(); return Action.PAUSE;
            default:             return Action.NOTHING;
        }
    }

    // gère les actions de sauvegarde / chargement / undo
    public Action handleStateAction(LogicKey k) {
        switch (k) {
            case SAVE:   saveGame();   return Action.SAVED;
            case LOAD:   loadGame();   return Action.LOADED;
            case RELOAD: reloadGame(); return Action.RELOADED;
            case UNDO:   undo();       return Action.UNDONE;
            default:                   return Action.NOTHING;
        }
    }

    /*--------------------------------------------------
                    PATHFINDING
    --------------------------------------------------*/

    // calcule un chemin A* du joueur vers la destination dans le monde courant
    public List<Direction> executePathFinding(Position dest) {
        if (dest == null) throw new NullPointerException();
        if (state != LevelState.RUNNING) return null;
        return logic.executePathFinding(
                getCurrentWorld(),
                getCurrentWorld().getPlayerPosition(),
                dest);
    }

    // convertit une Direction en LogicKey de mouvement
    public LogicKey directionToLogicKey(Direction d) {
        switch (d) {
            case UP:    return LogicKey.MOVE_UP;
            case DOWN:  return LogicKey.MOVE_DOWN;
            case LEFT:  return LogicKey.MOVE_LEFT;
            case RIGHT: return LogicKey.MOVE_RIGHT;
            default:    return null;
        }
    }

    /*--------------------------------------------------
          CHEMINS DANS L'ARBRE (WorldTreePath)
    --------------------------------------------------*/

    // ajout rec — retourne tous les chemins racine→feuille triés du plus long au plus court
    public List<WorldTreePath> getLeafPathsLongestToShortest() {
        if (root == null) return Collections.emptyList();

        List<WorldTreePath> paths = new ArrayList<>();
        collectLeafPaths(root, new ArrayList<>(), new ArrayList<>(), paths);

        paths.sort(Comparator.comparingInt(WorldTreePath::worldCount).reversed()
                .thenComparing(WorldTreePath::toDisplayString));
        return paths;
    }

    // ajout rec — BFS dans l'arbre pour trouver le chemin le plus court
    //             du noeud courant jusqu'à une feuille
    public WorldTreePath getShortestPathFromCurrentNodeToLeaf() {
        if (currentNode == null) return null;
        if (currentNode.isLeaf()) {
            List<Integer> refs = new ArrayList<>();
            refs.add(currentNode.getWorldact().getWorldRef());
            return new WorldTreePath(refs, new ArrayList<>());
        }

        Map<WorldNode, WorldNode>          previous     = new HashMap<>();
        Map<WorldNode, WorldTreePath.Step> previousStep = new HashMap<>();
        ArrayDeque<WorldNode>              queue        = new ArrayDeque<>();

        queue.add(currentNode);
        previous.put(currentNode, null);

        WorldNode targetLeaf = null;

        while (!queue.isEmpty()) {
            WorldNode node = queue.poll();
            if (node.isLeaf()) { targetLeaf = node; break; }

            WorldNode parent = node.getParent();
            if (parent != null && !previous.containsKey(parent)) {
                previous.put(parent, node);
                previousStep.put(parent, new WorldTreePath.Step(
                        node.getWorldact().getWorldRef(),
                        parent.getWorldact().getWorldRef(),
                        WorldTreePath.StepType.TO_PARENT, null));
                queue.add(parent);
            }

            for (PortalLink link : node.getPortalLinks()) {
                WorldNode child = link.getChild();
                if (previous.containsKey(child)) continue;
                previous.put(child, node);
                previousStep.put(child, new WorldTreePath.Step(
                        node.getWorldact().getWorldRef(),
                        child.getWorldact().getWorldRef(),
                        WorldTreePath.StepType.TO_CHILD,
                        link.getPortalBox().getPortalId()));
                queue.add(child);
            }
        }

        if (targetLeaf == null) return null;

        // reconstruction du chemin en remontant depuis la feuille
        List<WorldTreePath.Step> reversedSteps = new ArrayList<>();
        WorldNode cursor = targetLeaf;
        while (!cursor.equals(currentNode)) {
            WorldTreePath.Step step = previousStep.get(cursor);
            if (step == null) break;
            reversedSteps.add(step);
            cursor = previous.get(cursor);
        }

        Collections.reverse(reversedSteps);
        List<Integer> worldRefs = new ArrayList<>();
        worldRefs.add(currentNode.getWorldact().getWorldRef());
        for (WorldTreePath.Step step : reversedSteps)
            worldRefs.add(step.getToWorldRef());

        return new WorldTreePath(worldRefs, reversedSteps);
    }

    // ajout rec — DFS récursif pour collecter tous les chemins racine→feuille
    private void collectLeafPaths(WorldNode node,
                                  List<Integer>          currentWorldRefs,
                                  List<WorldTreePath.Step> currentSteps,
                                  List<WorldTreePath>    paths) {
        currentWorldRefs.add(node.getWorldact().getWorldRef());

        if (node.isLeaf()) {
            paths.add(new WorldTreePath(currentWorldRefs, currentSteps));
        } else {
            for (PortalLink link : node.getPortalLinks()) {
                WorldNode child = link.getChild();
                currentSteps.add(new WorldTreePath.Step(
                        node.getWorldact().getWorldRef(),
                        child.getWorldact().getWorldRef(),
                        WorldTreePath.StepType.TO_CHILD,
                        link.getPortalBox().getPortalId()));
                collectLeafPaths(child, currentWorldRefs, currentSteps, paths);
                currentSteps.remove(currentSteps.size() - 1);
            }
        }

        currentWorldRefs.remove(currentWorldRefs.size() - 1);
    }

    /*--------------------------------------------------
                    SAVE / UNDO / RELOAD
    --------------------------------------------------*/
    public void replaceWorld(int ref, World world) { worlds.set(ref, world); }

    // sauvegarde un snapshot du monde courant pour undo
    public void saveState()  { sm.saveUndoSnapshot(getCurrentWorld()); }
    public void saveGame()   { sm.save(this);        }
    public void loadGame()   { sm.load(this);        }
    public void reloadGame() { sm.loadFresh(this);   }
    public void undo()       { sm.undo(getCurrentWorld()); }

    /*--------------------------------------------------
          COMPATIBILITÉ INTERFACE GRAPHIQUE
    --------------------------------------------------*/

    // exécute un mouvement directement depuis une Direction.
    // Appelé par GameController.moveInPath().
    public void executeMove(Direction d) {
        LogicKey lk = directionToLogicKey(d);
        if (lk != null) handleMoveAction(lk);
    }

    // convertit Action → ResultOfAction pour que GameController reste inchangé.
    // ajout rec — pont entre le nouveau enum Action et l'ancien ResultOfAction
    public ResultOfAction handleUserActionGUI(LogicKey k) {
        Action a = handleUserAction(k);
        switch (a) {
            case MOVED:                  return ResultOfAction.MOVED;
            case BOX_IN_TARGET:          return ResultOfAction.BOX_IN_TARGET;
            case BLOCKED:                return ResultOfAction.BLOCKED;
            case WON:                    return ResultOfAction.WON;
            case SAVED:                  return ResultOfAction.SAVED;
            case LOADED:                 return ResultOfAction.LOADED;
            case RELOADED:               return ResultOfAction.RELOADED;
            case UNDONE:                 return ResultOfAction.UNDONE;
            case PAUSE:                  return ResultOfAction.PAUSED;
            case PATH_FINDING_REQUESTED: return ResultOfAction.PATH_FINDING_REQUESTED;
            default:                     return ResultOfAction.NOTHING;
        }
    }
}
