package sokoban.logic;

// Résultat d'une action exécutée dans le jeu.
// ajout rec — étend ResultOfAction du main avec TRAVERSE et PATH_MODE_REQUESTED
public enum Action {
    MOVED,
    BLOCKED,
    BOX_IN_TARGET,
    PAUSE,
    NOTHING,
    TRAVERSE,               // ajout rec — traversée d'un portail inter-mondes
    SAVED,
    LOADED,
    RELOADED,
    UNDONE,
    WON,
    PATH_FINDING_REQUESTED,
    PATH_MODE_REQUESTED     // ajout rec — pathfinding guidé par le mode de victoire (commande pv)
}
