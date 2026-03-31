package sokoban.autosolver;

import sokoban.core.CellType;
import sokoban.core.Direction;
import sokoban.core.Position;
import sokoban.core.World;
import sokoban.entity.Box;
import sokoban.pathfinding.PathSeek;

import java.util.*;

public class AutoSolver {

    private final World initialWorld;
    private final World workingWorld; // Un monde "brouillon" pour utiliser PathSeek
    private final List<Position> targets;

    public AutoSolver(World world) {
        this.initialWorld = world;
        this.targets = findTargets(world);
        
        // On clone le monde pour que PathSeek puisse l'utiliser sans modifier le vrai jeu
        this.workingWorld = new World(world.getGrid().getLength(), world.getGrid().getWidth(), world.getWorldRef());
        this.workingWorld.loadWorld(world.getGridArray());
    }

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

            // C'est ici que l'on intègre PathSeek. On met à jour notre monde brouillon :
            workingWorld.setPlayerAt(current.getPlayerPos());
            workingWorld.setBoxesFromPositions(new ArrayList<>(current.getBoxes()));

            // Pour chaque boîte, on essaie de la pousser dans les 4 directions
            for (Position boxPos : current.getBoxes()) {
                for (Direction pushDir : Direction.values()) {
                    
                    // Où le joueur doit-il se tenir pour pousser ? (À l'opposé de la direction)
                    Direction approachDir = getOpposite(pushDir);
                    Position playerStartPos = new Position(boxPos.getY(), boxPos.getX());
                    playerStartPos.translate(approachDir);
                    
                    // Où va atterrir la boîte ?
                    Position nextBoxPos = new Position(boxPos.getY(), boxPos.getX());
                    nextBoxPos.translate(pushDir);
                    
                    // 1. La case d'approche du joueur est-elle libre ?
                    // 2. La case de destination de la boîte est-elle libre ?
                    if (isFree(playerStartPos, current.getBoxes()) && isFree(nextBoxPos, current.getBoxes())) {
                        
                        // 3. Le joueur peut-il atteindre la case d'approche ?
                        List<Direction> playerPath = PathSeek.findShortestPath(workingWorld, current.getPlayerPos(), playerStartPos);
                        
                        if (playerPath != null) { // Chemin trouvé par PathSeek !
                            Set<Position> nextBoxes = new HashSet<>(current.getBoxes());
                            nextBoxes.remove(boxPos);
                            nextBoxes.add(nextBoxPos);
                            
                            // Après avoir poussé, le joueur prend la place de l'ancienne boîte
                            Position nextPlayerPos = new Position(boxPos.getY(), boxPos.getX());
                            
                            int newG = current.getG() + playerPath.size() + 1; // +1 pour la poussée
                            
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

    private boolean isFree(Position pos, Set<Position> currentBoxes) {
        try {
            if (initialWorld.getCellatPosition(pos).getCellType() == CellType.WALL) return false;
            if (currentBoxes.contains(pos)) return false;
            return true;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    private Direction getOpposite(Direction d) {
        switch(d) {
            case UP: return Direction.DOWN;
            case DOWN: return Direction.UP;
            case LEFT: return Direction.RIGHT;
            case RIGHT: return Direction.LEFT;
        }
        return null;
    }

    private List<Direction> reconstructSolution(PuzzleState state) {
        List<Direction> fullSolution = new ArrayList<>();
        PuzzleState current = state;
        
        while (current.getParent() != null) {
            // On ajoute la poussée finale
            fullSolution.add(current.getPushDirection());
            
            // On ajoute le chemin du joueur à l'envers (car on remonte l'arbre)
            List<Direction> approach = current.getPathToReach();
            for (int i = approach.size() - 1; i >= 0; i--) {
                fullSolution.add(approach.get(i));
            }
            current = current.getParent();
        }
        
        // On remet toute la liste à l'endroit
        Collections.reverse(fullSolution);
        return fullSolution;
    }
}
