package sokoban.pathfinding;

import sokoban.core.CellType;
import sokoban.core.Direction;
import sokoban.core.Position;
import sokoban.core.World;

import java.util.*;

/**
 * Algorithme de recherche de chemin (BFS).
 * Calcule le plus court chemin entre deux positions
 * sans déplacer de boîtes.
 */
public class PathSeek {

    /*--------------------------------------------------
                    TYPE INTERNE
    --------------------------------------------------*/

    // nœud interne pour mémoriser l'historique du chemin exploré
    private static class PathNode {

        /*--------------------------------------------------
                            ATTRIBUTS
        --------------------------------------------------*/
        Position  pos;
        PathNode  parent;           // null pour le nœud de départ
        Direction dirFromParent;    // direction prise depuis le parent pour arriver ici

        /*--------------------------------------------------
                            CONSTRUCTEUR
        --------------------------------------------------*/
        public PathNode(Position pos, PathNode parent, Direction dirFromParent) {
            this.pos           = pos;
            this.parent        = parent;
            this.dirFromParent = dirFromParent;
        }
    }

    /*--------------------------------------------------
                        METHODES
    --------------------------------------------------*/

    /**
     * Calcule le plus court chemin entre start et dest via BFS.
     * Retourne la liste ordonnée des directions, ou null si aucun chemin.
     */
    public static List<Direction> findShortestPath(World world, Position start, Position dest) {
        if (start.equals(dest)) return new ArrayList<>();

        Queue<PathNode> queue   = new LinkedList<>();
        Set<Position>   visited = new HashSet<>();

        queue.add(new PathNode(start, null, null));
        visited.add(start);

        while (!queue.isEmpty()) {
            PathNode current = queue.poll();

            if (current.pos.equals(dest))
                return reconstructPath(current);

            for (Direction dir : Direction.values()) {
                Position nextPos = new Position(current.pos.getY(), current.pos.getX());
                nextPos.translate(dir);

                if (isWalkable(world, nextPos) && !visited.contains(nextPos)) {
                    visited.add(nextPos);
                    queue.add(new PathNode(nextPos, current, dir));
                }
            }
        }

        return null; // aucun chemin trouvé
    }

    // vérifie si une case est traversable (ni mur, ni boîte)
    private static boolean isWalkable(World world, Position pos) {
        try {
            if (world.getCellatPosition(pos).getCellType() == CellType.WALL) return false;
            if (world.getBoxatPosition(pos) != null) return false;
            return true;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false; // clic hors de la map
        }
    }

    // remonte l'arborescence des nœuds pour créer la liste ordonnée des déplacements
    private static List<Direction> reconstructPath(PathNode node) {
        List<Direction> path    = new ArrayList<>();
        PathNode        current = node;
        while (current.parent != null) {
            path.add(current.dirFromParent);
            current = current.parent;
        }
        Collections.reverse(path);
        return path;
    }
}
