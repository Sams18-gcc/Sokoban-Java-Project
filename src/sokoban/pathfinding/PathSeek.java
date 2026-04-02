package sokoban.pathfinding;

import sokoban.core.CellType;
import sokoban.core.Direction;
import sokoban.core.Position;
import sokoban.core.World;

import java.util.*;

/**
 * La classe PathSeek fournit l'algorithme de recherche de chemin (rôle Chemins).
 * Elle utilise un parcours en largeur (BFS) pour déterminer le plus court chemin 
 * du joueur vers une cible, sans déplacer de boîtes.
 * * @author KHIMOUM Aimen Mohamed
 */
public class PathSeek {

    /**
     * Nœud interne pour mémoriser l'historique du chemin exploré.
     * Permet de retracer la séquence de déplacements une fois la cible atteinte.
     */
    private static class PathNode {
        Position pos;
        PathNode parent;  // Le parent du nœud, null pour le nœud de départ
        Direction dirFromParent; // La direction prise depuis le parent pour arriver ici

        public PathNode(Position pos, PathNode parent, Direction dirFromParent) {
            this.pos = pos;
            this.parent = parent;
            this.dirFromParent = dirFromParent;
        }
    }

    /**
     * Calcule le plus court chemin entre la position de départ et la destination.
     * * @param world L'état actuel du monde (grille, murs, boîtes, etc.).
     * @param start La position de départ (généralement celle du joueur).
     * @param dest  La position cible à atteindre.
     * @return Une liste contenant les directions à suivre dans l'ordre chronologique, 
     * ou null si aucun chemin n'est possible.
     */
    public static List<Direction> findShortestPath(World world, Position start, Position dest) {
        if (start.equals(dest)) {
            return new ArrayList<>(); // Si on est déjà sur la case
        }

        Queue<PathNode> queue = new LinkedList<>(); // File d'attente pour la recherche en largeur
        Set<Position> visited = new HashSet<>();    // Pour éviter de revisiter les mêmes positions

        queue.add(new PathNode(start, null, null));
        visited.add(start);

        // Tant que la file n'est pas vide, on continue à explorer les nœuds
        while (!queue.isEmpty()) {
            PathNode current = queue.poll();

            // Si on a atteint la cible, on remonte l'arbre pour extraire les directions
            if (current.pos.equals(dest)) {
                return reconstructPath(current);
            }

            // On teste les 4 directions cardinales
            for (Direction dir : Direction.values()) {
                Position nextPos = new Position(current.pos.getY(), current.pos.getX());
                nextPos.translate(dir);

                // Si la position suivante est traversable et n'a pas encore été visitée
                if (isWalkable(world, nextPos) && !visited.contains(nextPos)) {
                    visited.add(nextPos); // Marquer la position comme visitée
                    queue.add(new PathNode(nextPos, current, dir)); // Ajouter à la file
                }
            }
        }

        return null; // Aucun chemin trouvé
    }

    /**
     * Vérifie si une case est traversable (ce n'est ni un mur, ni une boîte).
     * * @param world L'état actuel du monde.
     * @param pos   La position à vérifier sur la grille.
     * @return true si la case est libre et accessible, false sinon.
     */
    private static boolean isWalkable(World world, Position pos) {
        try {
            // Rejeter si c'est un mur
            if (world.getCellatPosition(pos).getCellType() == CellType.WALL) {
                return false;
            }
            // Rejeter s'il y a une boîte (on ne peut pas les pousser en mode automatique)
            if (world.getBoxatPosition(pos) != null) {
                return false;
            }
            return true;
        } catch (ArrayIndexOutOfBoundsException e) { 
            // Dans le cas d'un clic hors de la map
            return false;
        }
    }

    /**
     * Remonte l'arborescence des nœuds pour créer la liste ordonnée des déplacements.
     * * @param node Le nœud final correspondant à la position cible.
     * @return La liste des directions dans le bon ordre de parcours.
     */
    private static List<Direction> reconstructPath(PathNode node) {
        List<Direction> path = new ArrayList<>(); // La liste des directions à suivre
        PathNode current = node;
        
        // Tant que le nœud actuel a un parent, on ajoute la direction à la liste
        while (current.parent != null) {
            path.add(current.dirFromParent);
            current = current.parent;
        }
        
        Collections.reverse(path); // Remettre le chemin dans l'ordre chronologique
        return path;
    }
}