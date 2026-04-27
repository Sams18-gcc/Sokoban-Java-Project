package sokoban.autosolver;

import sokoban.core.Direction;
import sokoban.core.Position;
import java.util.*;

/**
 * Représente un état instantané du plateau de jeu pour l'algorithme de résolution automatique.
 * <p>
 * Pour optimiser la mémoire lors de la recherche A*, cette classe ne stocke que les
 * éléments mobiles du jeu (le joueur et les boîtes). Les éléments statiques (murs, cibles)
 * sont gérés par le solveur.
 * </p>
 */
public class PuzzleState implements Comparable<PuzzleState> {
    
    private final Position playerPos;
    private final Set<Position> boxes;
    
    private final PuzzleState parent;
    private final List<Direction> pathToReach; 
    private final Direction pushDirection;     
    
    private final int g; 
    private final int h; 

    /**
     * Construit un nouvel état du puzzle.
     *
     * @param playerPos     La position actuelle du joueur.
     * @param boxes         L'ensemble des positions de toutes les boîtes.
     * @param parent        L'état parent depuis lequel cet état a été généré (utilisé pour reconstruire la solution).
     * @param pathToReach   Le chemin parcouru par le joueur pour se mettre en position de pousser (calculé par PathSeek).
     * @param pushDirection La direction dans laquelle la boîte a été poussée pour atteindre cet état.
     * @param g             Le coût exact du chemin depuis l'état initial (nombre de pas + poussées).
     * @param h             L'heuristique (estimation du coût restant pour atteindre la victoire).
     */
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

    /** @return La position du joueur dans cet état. */
    public Position getPlayerPos() { return playerPos; }
    
    /** @return L'ensemble des positions des boîtes dans cet état. */
    public Set<Position> getBoxes() { return boxes; }
    
    /** @return L'état précédent qui a mené à cet état (null si état initial). */
    public PuzzleState getParent() { return parent; }
    
    /** @return La liste des directions prises par le joueur pour se positionner avant la poussée. */
    public List<Direction> getPathToReach() { return pathToReach; }
    
    /** @return La direction de la poussée qui a généré cet état. */
    public Direction getPushDirection() { return pushDirection; }
    
    /** @return Le coût G (distance parcourue depuis le départ). */
    public int getG() { return g; }
    
    /**
     * Calcule le score F de l'état pour l'algorithme A* (F = G + H).
     * @return Le score total F.
     */
    public int getF() { return g + h; }

    /**
     * Vérifie l'égalité entre deux états.
     * Deux états sont considérés identiques si le joueur et toutes les boîtes sont aux mêmes positions.
     *
     * @param o L'objet à comparer.
     * @return true si les états sont identiques, false sinon.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PuzzleState that = (PuzzleState) o;
        return playerPos.equals(that.playerPos) && boxes.equals(that.boxes);
    }

    /**
     * Génère un code de hachage basé sur la position du joueur et des boîtes.
     * Indispensable pour l'utilisation correcte des HashSet dans l'algorithme A*.
     *
     * @return Le code de hachage de l'état.
     */
    @Override
    public int hashCode() {
        return Objects.hash(playerPos, boxes);
    }

    /**
     * Compare cet état avec un autre pour définir leur priorité dans la file d'attente (PriorityQueue).
     * Les états avec le plus petit score F sont prioritaires.
     *
     * @param other L'autre état à comparer.
     * @return Un entier négatif, nul ou positif selon que ce score F est inférieur, égal ou supérieur.
     */
    @Override
    public int compareTo(PuzzleState other) {
        return Integer.compare(this.getF(), other.getF());
    }
}
