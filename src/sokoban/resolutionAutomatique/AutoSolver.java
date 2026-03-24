package sokoban.solver;

import sokoban.core.CellType;
import sokoban.core.Direction;
import sokoban.core.Position;
import sokoban.core.World;
import sokoban.entity.Box;

import java.util.*;


public class AutoSolver {
    
    // Le monde d'origine (utilise uniquement pour lire la grille statique : murs et taille)
    private final World world;
    
    // Les positions des cibles ou l'on doit placer les boites.
    // On les stocke une seule fois au debut car elles ne bougent jamais.
    private final Set<Position> targets;

    /**
     * Constructeur du solveur.
     * Il prend le monde actuel et extrait immediatement les positions des cibles.
     */
    public AutoSolver(World world) {
        this.world = world;
        this.targets = world.getTargetPositions(); 
    }

    /**
     * Methode principale qui lance la recherche de la solution.
     * return Une liste de directions menant a la victoire, ou une liste vide si c'est impossible.
     */
    public List<Direction> solve() {
        // --- 1. INITIALISATION ---
        // On recupere la position initiale de toutes les boites du vrai jeu.
        Set<Position> initialBoxes = new HashSet<>();
        for (Box b : world.getBoxes()) {
            initialBoxes.add(b.getPosition());
        }

        // On cree la toute premiere "photo" (l'etat de depart du niveau).
        MapState initialState = new MapState(world.getPlayerPosition(), initialBoxes, null, null);

        // --- 2. STRUCTURES DE DONNeES DE L'ALGORITHME ---
        // La "File" (Queue) contient les etats qu'il nous reste à explorer
        // Dans un BFS, on explore d'abord tous les mouvements à 1 distance, puis 2, puis 3...
        Queue<MapState> queue = new LinkedList<>();//FIFO
        
        // garde en memoire toutes les configurations deja visitees
        // pour eviter les boucles infinis
        Set<MapState> visited = new HashSet<>();

        // On insere l'etat de depart dans nos structures.
        queue.add(initialState);
        visited.add(initialState);

        // --- 3. BOUCLE PRINCIPALE D'EXPLORATION ---
        // Tant qu'il reste des chemins possibles à explorer... 
        while (!queue.isEmpty()) {
            
            // On sort le prochain etat à analyser de la file.
            MapState current = queue.poll();

            // Condition d'arrêt : on verifie si cet etat est une victoire.
            // Si oui, on a trouve la solution la plus courte !
            if (isWin(current)) {
                return reconstructPath(current);
            }

            // On essaie de bouger le joueur dans les 4 directions possibles (Haut, Bas, Gauche, Droite).
            for (Direction dir : Direction.values()) {
                // On simule le mouvement. Si c'est un mur, tryMove renverra 'null'.
                MapState nextState = tryMove(current, dir);
                
                // Si le mouvement est valide ET que l'on n'a jamais vu cette configuration exacte...
                if (nextState != null && !visited.contains(nextState)) {
                    visited.add(nextState); // On la note dans notre liste des visites 
                    queue.add(nextState);   // On l'ajoute à la file d'attente pour l'explorer plus tard
                }
            }
        }
        
        // Si la file se vide completement, c'est qu'il n'y a aucune solution possible.
        return Collections.emptyList(); 
    }

    /**
     * return Un nouvel etat MapState si le mouvement est legal, ou null s'il est bloque (mur, etc.).
     */
    private MapState tryMove(MapState state, Direction dir) {
        
        // On calcule la future position du joueur sans modifier sa vraie position
        Position actPosition = state.getPlayerPos();
        Position nextPlayerPos = actPosition.translate(dir);
        );

        // 1ere regle : Un joueur ne peut pas traverser un mur
        if (isWall(nextPlayerPos)) return null;

        // 2eme regle : Le joueur veut avancer sur une case où se trouve une boite
        if (state.getBoxesPos().contains(nextPlayerPos)) {
            
            // On calcule où la boite serait poussee.
            Position nextBoxPos = nextPlayerPos.translate(dir);

            // La boite ne peut pas être poussee dans un mur, ni sur une autre boite
            if (isWall(nextBoxPos) || state.getBoxesPos().contains(nextBoxPos)) {
                return null; // Mouvement totalement bloque.
            }

            // Le mouvement est legal avec une poussee : on cree la nouvelle liste des boites.
            Set<Position> newBoxes = new HashSet<>(state.getBoxesPos());
            newBoxes.remove(nextPlayerPos); // La boite quitte sa case actuelle
            newBoxes.add(nextBoxPos);       // Elle arrive sur sa nouvelle case
            
            // On renvoie la nouvelle "photo" du jeu.
            return new MapState(nextPlayerPos, newBoxes, state, dir);
        }

        // 3eme regle : La case est vide. C'est un simple deplacement de joueur
        return new MapState(nextPlayerPos, state.getBoxesPos(), state, dir);
    }

    /**
     * Verifie si une position donnee est un mur ou en dehors du plateau.
     */
    private boolean isWall(Position pos) {
        // on verifie qu'on ne sort pas des limites du tableau 
        // Cela empeche l'erreur fatale (IndexOutOfBoundsException) lors des explorations.
        if (pos.getY() < 0 || pos.getY() >= world.getLength() || pos.getX() < 0 || pos.getX() >= world.getWidth()) {
            return true;
        }
        
        // Si on est dans le plateau, on interroge la grille du modele.
        return world.getCellatPosition(pos).getCellType() == CellType.WALL;
    }

    /**
     * Verifie si l'etat actuel est l'etat de victoire.
     * C'est vrai uniquement si l'ensemble des positions des boites 
     * contient exactement toutes les cibles du niveau.
     */
    private boolean isWin(MapState state) {
        return targets.containsAll(state.getBoxesPos());
    }

    /**
     * Une fois la solution trouvee, on remonte l'arbre des etats parents 
     * pour reconstruire la chronologie exacte des deplacements.
     */
    private List<Direction> reconstructPath(MapState state) {
        List<Direction> path = new ArrayList<>();
        MapState current = state;
        
        // On remonte jusqu'à l'etat initial (qui est le seul à avoir un parent 'null').
        while (current.getParent() != null) {
            path.add(current.getMoveFromParent());
            current = current.getParent();
        }
        
        // Comme on est parti de la fin, on doit inverser la liste pour avoir 
        // les mouvements dans le bon ordre chronologique.
        Collections.reverse(path);
        return path;
    }
}
