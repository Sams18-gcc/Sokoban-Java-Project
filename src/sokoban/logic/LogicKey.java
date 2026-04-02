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
    ESCAPE,
    FIND_PATH,
    LOAD,
    SAVE,
    UNDO,
    RELOAD//si on veut restart le jeu mid playing
}