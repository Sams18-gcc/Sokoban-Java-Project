package sokoban.autosolver;

import sokoban.core.CellType;
import sokoban.core.Direction;
import sokoban.core.Position;
import sokoban.core.World;
import sokoban.entity.Box;
import sokoban.pathfinding.PathSeek;

import java.util.*;

/**
 * Moteur de résolution automatique d'un niveau de Sokoban utilisant l'algorithme A* (A-Star).
 * <p>
 * Cette classe implémente une stratégie de recherche par "macro-mouvements" :
 * l'algorithme réfléchit en termes de poussées de boîtes, et utilise la classe {@link PathSeek}
 * pour vérifier si le joueur peut physiquement atteindre la position requise pour effectuer la poussée.
 * </p>
 */
public class AutoSolver {

    private final World initialWorld;
    private final World workingWorld; 
    private final List<Position> targets;

    /**
     * Initialise le solveur automatique pour un monde donné.
     *
     * @param world Le monde (World) initial à résoudre.
     */
    public AutoSolver(World world) {
        this.initialWorld = world;
        this.targets = findTargets(world);
        
        // Clonage du monde pour les calculs internes de PathSeek
        this.workingWorld = new World(world.getGrid().getLength(), world.getGrid().getWidth(), world.getWorldRef());
        this.workingWorld.loadWorld(world.getGridArray());
    }

    /**
     * Scanne la grille pour lister les positions de toutes les cibles.
     *
     * @param world Le monde à analyser.
     * @return Une liste contenant les positions des cibles.
     */
    private List<Position> findTargets(World world) {
        List<Position> t = new ArrayList<>();
        int rows = world.getGrid().getLength();
        int cols = world.getGrid().getWidth();
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (world.getCellatPosition(new Position(y, x)).getCellType() == CellType.TARGET) {
                    t.add(new Position(y, x));
                }
            }
        }
        return t;
    }

    /**
     * Calcule l'heuristique (distance estimée jusqu'à la victoire) en utilisant la distance de Manhattan.
     *
     * @param boxes L'ensemble des positions actuelles des boîtes.
     * @return La somme des distances minimales entre chaque boîte et une cible.
     */
    private int calculateHeuristic(Set<Position> boxes) {
        int totalDistance = 0;
        for (Position box : boxes) {
            int minDistance = Integer.MAX_VALUE;
            for (Position target : targets) {
                int dist = Math.abs(box.getX() - target.getX()) + Math.abs(box.getY() - target.getY());
                if (dist < minDistance) minDistance = dist;
            }
            totalDistance += minDistance;
        }
        return totalDistance;
    }

    /**
     * Vérifie si l'état actuel est un état de victoire (toutes les boîtes sont sur des cibles).
     *
     * @param boxes L'ensemble des positions actuelles des boîtes.
     * @return true si le niveau est résolu, false sinon.
     */
    private boolean isVictory(Set<Position> boxes) {
        for (Position box : boxes) {
            boolean onTarget = false;
            for (Position target : targets) {
                if (box.equals(target)) {
                    onTarget = true;
                    break;
                }
            }
            if (!onTarget) return false;
        }
        return true;
    }

    /**
     * Lance l'algorithme de résolution automatique A*.
     * <p>
     * Explore les combinaisons de poussées de boîtes en favorisant les états ayant
     * la plus faible distance heuristique. Réutilise le composant PathSeek pour valider
     * les déplacements du joueur.
     * </p>
     *
     * @return Une liste ordonnée des directions à appliquer pour résoudre le niveau, 
     * ou null si aucune solution n'a pu être trouvée.
     */
    public List<Direction> solve() {
        Set<Position> initialBoxes = new HashSet<>();
        for (Box b : initialWorld.getBoxes()) {
            initialBoxes.add(b.getPosition());
        }
        
        PuzzleState startState = new PuzzleState(
                initialWorld.getPlayerPosition(), initialBoxes, null, null, null, 0, calculateHeuristic(initialBoxes)
        );

        PriorityQueue<PuzzleState> openSet = new PriorityQueue<>();
        Set<PuzzleState> closedSet = new HashSet<>();
        openSet.add(startState);

        while (!openSet.isEmpty()) {
            PuzzleState current = openSet.poll();

            if (isVictory(current.getBoxes())) {
                return reconstructSolution(current);
            }

            if (closedSet.contains(current)) continue;
            closedSet.add(current);

            workingWorld.setPlayerAt(current.getPlayerPos());
            workingWorld.setBoxesFromPositions(new ArrayList<>(current.getBoxes()));

            for (Position boxPos : current.getBoxes()) {
                for (Direction pushDir : Direction.values()) {
                    
                    Direction approachDir = getOpposite(pushDir);
                    Position playerStartPos = new Position(boxPos.getY(), boxPos.getX());
                    playerStartPos.translate(approachDir);
                    
                    Position nextBoxPos = new Position(boxPos.getY(), boxPos.getX());
                    nextBoxPos.translate(pushDir);
                    
                    if (isFree(playerStartPos, current.getBoxes()) && isFree(nextBoxPos, current.getBoxes())) {
                        
                        List<Direction> playerPath = PathSeek.findShortestPath(workingWorld, current.getPlayerPos(), playerStartPos);
                        
                        if (playerPath != null) { 
                            Set<Position> nextBoxes = new HashSet<>(current.getBoxes());
                            nextBoxes.remove(boxPos);
                            nextBoxes.add(nextBoxPos);
                            
                            Position nextPlayerPos = new Position(boxPos.getY(), boxPos.getX());
                            int newG = current.getG() + playerPath.size() + 1; 
                            
                            PuzzleState nextState = new PuzzleState(
                                    nextPlayerPos, nextBoxes, current, playerPath, pushDir, newG, calculateHeuristic(nextBoxes)
                            );
                            
                            if (!closedSet.contains(nextState)) {
                                openSet.add(nextState);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Vérifie si une position donnée est traversable (pas de mur et pas de boîte).
     *
     * @param pos          La position à vérifier.
     * @param currentBoxes L'ensemble des boîtes dans l'état actuel.
     * @return true si la case est libre, false sinon.
     */
    private boolean isFree(Position pos, Set<Position> currentBoxes) {
        try {
            if (initialWorld.getCellatPosition(pos).getCellType() == CellType.WALL) return false;
            if (currentBoxes.contains(pos)) return false;
            return true;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false; 
        }
    }

    /**
     * Renvoie la direction opposée à celle fournie (utile pour trouver la position d'approche).
     *
     * @param d La direction initiale.
     * @return La direction strictement opposée.
     */
    private Direction getOpposite(Direction d) {
        switch(d) {
            case UP: return Direction.DOWN;
            case DOWN: return Direction.UP;
            case LEFT: return Direction.RIGHT;
            case RIGHT: return Direction.LEFT;
        }
        return null;
    }

    /**
     * Remonte l'arbre des états parents depuis l'état final pour générer la liste complète des actions.
     *
     * @param state L'état victorieux du puzzle.
     * @return La liste complète et chronologique des directions à effectuer par le joueur.
     */
    private List<Direction> reconstructSolution(PuzzleState state) {
        List<Direction> fullSolution = new ArrayList<>();
        PuzzleState current = state;
        
        while (current.getParent() != null) {
            fullSolution.add(current.getPushDirection());
            
            List<Direction> approach = current.getPathToReach();
            for (int i = approach.size() - 1; i >= 0; i--) {
                fullSolution.add(approach.get(i));
            }
            
            current = current.getParent();
        }
        
        Collections.reverse(fullSolution);
        return fullSolution;
    }
}
