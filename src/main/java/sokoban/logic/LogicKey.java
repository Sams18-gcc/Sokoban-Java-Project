package sokoban.logic;

// Actions logiques envoyées au noyau du jeu depuis l'interface.
// ajout rec — ajout des commandes TRAVERSE_CHILD et TRAVERSE_PARENT (8 directions)
//             et FIND_PATH_MODE pour le pathfinding guidé par le mode de victoire
public enum LogicKey {
    MOVE_UP,
    MOVE_DOWN,
    MOVE_LEFT,
    MOVE_RIGHT,
    ESCAPE,
    FIND_PATH,
    FIND_PATH_MODE,           // ajout rec — pathfinding adapté au mode de victoire courant
    LOAD,
    SAVE,
    UNDO,
    RELOAD,
    TRAVERSE_CHILD_UP,        // ajout rec — descendre dans le monde enfant (portail au nord)
    TRAVERSE_CHILD_DOWN,      // ajout rec — descendre dans le monde enfant (portail au sud)
    TRAVERSE_CHILD_LEFT,      // ajout rec — descendre dans le monde enfant (portail à l'ouest)
    TRAVERSE_CHILD_RIGHT,     // ajout rec — descendre dans le monde enfant (portail à l'est)
    TRAVERSE_PARENT_UP,       // ajout rec — remonter vers le monde parent (portail au nord)
    TRAVERSE_PARENT_DOWN,     // ajout rec — remonter vers le monde parent (portail au sud)
    TRAVERSE_PARENT_LEFT,     // ajout rec — remonter vers le monde parent (portail à l'ouest)
    TRAVERSE_PARENT_RIGHT     // ajout rec — remonter vers le monde parent (portail à l'est)
}
