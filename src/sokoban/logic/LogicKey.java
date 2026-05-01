package sokoban.logic;

/*
 * Représente les actions logiques envoyees au noyau du jeu.
 * Elles viennent de l'interface, mais restent independantes
 * des touches ou des clics utilises pour les declencher.
 */
public enum LogicKey {
    MOVE_UP,
    MOVE_DOWN,
    MOVE_LEFT,
    MOVE_RIGHT,

    // traverser vers un monde enfant
    TRAVERSE_CHILD_UP,
    TRAVERSE_CHILD_DOWN,
    TRAVERSE_CHILD_LEFT,
    TRAVERSE_CHILD_RIGHT,

    // traverser vers le monde parent
    TRAVERSE_PARENT_UP,
    TRAVERSE_PARENT_DOWN,
    TRAVERSE_PARENT_LEFT,
    TRAVERSE_PARENT_RIGHT,

    ESCAPE,
    FIND_PATH,
    AUTO_SOLVE,
    LOAD,
    SAVE,
    UNDO,
    RELOAD//si on veut restart le jeu mid playing
}