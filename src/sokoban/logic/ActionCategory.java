package sokoban.logic;

/*
 * Cet enum sert a classer les actions utilisateur selon leur nature.
 * Il permet de savoir quel traitement appliquer dans le niveau.
 *
 * Exemples :
 * - un deplacement du joueur -> MOVE
 * - une recherche de chemin -> PATH_FINDING
 * - une action liee a la sauvegarde ou au chargement -> STATE
 * - une action liee a l'interface, comme pause -> INTERFACE
 * - une traversee entre mondes -> TRAVERSE
 */
public enum ActionCategory {
    // action de deplacement du joueur
    MOVE,

    // action de traversee entre les mondes
    TRAVERSE,

    // action de recherche de chemin
    PATH_FINDING,

    SOLVER,

    // action de gestion d'etat (save, load, reload, undo...)
    STATE,

    // action de gestion d'interface (pause, retour menu, etc.)
    INTERFACE,

    // action pas prise en charge
    UNHANDLED,
}