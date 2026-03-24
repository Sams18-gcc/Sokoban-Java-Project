package sokoban.solver;

import sokoban.core.Direction;
import sokoban.core.Position;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

//une map secondaire pour faire les tests sur le meilleur chemin que on doit prendre
public class MapState {
    
    // La position du joueur dans cet etat precis.
    private final Position playerPos;
    
    // L'ensemble des positions de toutes les boîtes dans cet etat
  
    private final Set<Position> boxesPos;
    
    // L'etat precedent qui a mené a cet etat (le "chemin" parcouru);
    private final MapState parent;
    
    // La direction que le joueur a pris depuis l'etat 'parent' pour arriver ici.
    private final Direction moveFromParent;

    /**
     * Constructeur permettant de créer une nouvelle photographie du jeu.
     */
    public MapState(Position playerPos, Set<Position> boxesPos, MapState parent, Direction moveFromParent) {
        this.playerPos = playerPos;
        
        // On copie l'ensemble des boîtes et on le rend "non-modifiable" (immuable).
        // C'est une sécurité vitale en algorithmique : un état passé ne doit 
        // jamais pouvoir être modifié par erreur lors des explorations futures !
        this.boxesPos = Collections.unmodifiableSet(new HashSet<>(boxesPos));
        
        this.parent = parent;
        this.moveFromParent = moveFromParent;
    }

    //  Getters
    public Position getPlayerPos() { return playerPos; }
    public Set<Position> getBoxesPos() { return boxesPos; }
    public MapState getParent() { return parent; }
    public Direction getMoveFromParent() { return moveFromParent; }


    @Override
    public boolean equals(Object o) {
       
        if (this == o) return true;
        
        // Si l'objet à comparer est nul ou n'est pas un MapState, c'est faux.
        if (o == null || getClass() != o.getClass()) return false;
        
        MapState MapState = (MapState) o; // cast
        
        // On vérifie que la position du joueur et les positions des boîtes sont les mêmes.
        return Objects.equals(playerPos, MapState.playerPos) &&
               Objects.equals(boxesPos, MapState.boxesPos);
    }

 
    @Override
    public int hashCode() {
        return Objects.hash(playerPos, boxesPos);
    }
}
