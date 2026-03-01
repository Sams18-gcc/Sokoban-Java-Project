package sokoban.logic;

import sokoban.core.CellType;
import sokoban.core.Direction;
import sokoban.core.Position;
import sokoban.core.World;

import java.util.*;

public class PathSeek{

    /**
     * noeud interne pour mémoriser le chemin parcouru  
     */
    private static class PathNode {
        Position pos;
        PathNode parent;  //le parent du noeud , null pour le noeud de départ
        Direction dirFromParent; //la direction à partir de laquelle on est arrivé à ce noeud depuis son parent

        public PathNode(Position pos, PathNode parent, Direction dirFromParent) {
            this.pos = pos;
            this.parent = parent;
            this.dirFromParent = dirFromParent;
        }
    }

    public static List<Direction> findShortestPath(World world, Position start, Position but) {
        if (start.equals(but)) 
        {
            return new ArrayList<>(); // si déjà sur la case
        }

        Queue<PathNode> queue = new LinkedList<>(); //file d'attente pour la recherche en largeur
        Set<Position> visited = new HashSet<>(); // pour éviter de revisiter les mêmes positions


        queue.add(new PathNode (start , null, null));
        visited.add(start);

            // tant que la file n'est pas vide, on continue à explorer les noeuds
        while (! queue.isEmpty() ) {
            PathNode current = queue.poll();

            // si on a atteint la cible, on remonte l'arbre pour extraire les directions
            if (current.pos.equals(but)) {
                return reconstructPath(current);
            }

            // on teste les 4 directions cardinales
            for (Direction dir : Direction.values()) {
                Position nextPos = new Position(current.pos.getY(), current.pos.getX());
                nextPos.translate(dir);

              // si la position suivante est traversable et n'a pas encore été visitée, on l'ajoute à la file d'attente
                if (isWalkable(world, nextPos) && !visited.contains(nextPos)) {
                    visited.add(nextPos);     // marquer la position comme visitée
                    queue.add(new PathNode(nextPos, current, dir));  // ajouter la nouvelle position à la file d'attente avec le noeud actuel comme parent et la direction correspondante
                }
            }
        }

        return null; //aucun chemin trouvé
    }

    /**
     * vérifie si une case est traversable = pas un mur || pas de boîte).
     */
    private static boolean isWalkable(World world, Position pos) {
        try {
            //rejeter si c'est un mur
            if (world.getCellatPosition(pos).getCellType() == CellType.WALL) {
                return false;
            }
            //rejeter s'il y a une boîte (la logique de déplacement des boîtes est gérée ailleurs, ici on considère que le joueur ne peut pas traverser une boîte)
            if(world.getBoxatPosition(pos) != null) {
                return false;
            }
            return true;
        } catch (ArrayIndexOutOfBoundsException e) { 
            //dans le cas d'un clic hors de la map
            return false;
        }
    }

    /**
     * remonte l'arborescence des noeuds pour créer la liste ordonnée des déplacements.
     */
    private static List<Direction> reconstructPath(PathNode node) {
        List<Direction> path = new ArrayList<>(); // la liste des directions à suivre pour atteindre la cible
        PathNode current = node;
        
        // tant que le noeud actuel a un parent, on ajoute la direction correspondante à la liste du chemin
        while (current.parent != null) {
            path.add(current.dirFromParent);
            current = current.parent;
        }
        
        Collections.reverse(path); // le chemin a été construit à l'envers donc on doit le remettre à l'endroit
        return path;
    }
}