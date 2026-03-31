package sokoban.autosolver;

import sokoban.core.Direction;
import sokoban.core.Position;
import java.util.*;

public class PuzzleState implements Comparable<PuzzleState> {
    
    private final Position playerPos;
    private final Set<Position> boxes;
    
    // Pour reconstruire la solution complète
    private final PuzzleState parent;
    private final List<Direction> pathToReach; // Chemin du joueur (calculé par PathSeek)
    private final Direction pushDirection;     // Direction de la poussée de la boîte
    
    // Scores pour l'algorithme A*
    private final int g; // Coût (nombre total de pas depuis le début)
    private final int h; // Heuristique 

    public PuzzleState(Position playerPos, Set<Position> boxes, PuzzleState parent, 
                       List<Direction> pathToReach, Direction pushDirection, int g, int h) {
        this.playerPos = playerPos;
        this.boxes = new HashSet<>(boxes);
        this.parent = parent;
        this.pathToReach = pathToReach;
        this.pushDirection = pushDirection;
        this.g = g;
        this.h = h;
    }

    public Position getPlayerPos() { return playerPos; }
    public Set<Position> getBoxes() { return boxes; }
    public PuzzleState getParent() { return parent; }
    public List<Direction> getPathToReach() { return pathToReach; }
    public Direction getPushDirection() { return pushDirection; }
    
    public int getG() { return g; }
    public int getF() { return g + h; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PuzzleState that = (PuzzleState) o;
        return playerPos.equals(that.playerPos) && boxes.equals(that.boxes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerPos, boxes);
    }

    @Override
    public int compareTo(PuzzleState other) {
        return Integer.compare(this.getF(), other.getF());
    }
}
