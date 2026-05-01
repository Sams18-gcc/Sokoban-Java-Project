package sokoban.logic;

/*
 * Cet enum sert a decrire le resultat d'une action executee dans le jeu.
 * L'interface (terminale ou graphique) peut ensuite lire cette valeur
 * pour savoir quoi afficher ou comment reagir.
 *
 * Exemples :
 * - si le joueur se deplace normalement -> MOVED
 * - si le mouvement est impossible -> BLOCKED
 * - si une boite entre dans une target -> BOX_IN_TARGET
 * - si le joueur demande une pause -> PAUSED
 * - si rien ne se passe -> NOTHING
 */
public enum ResultOfAction {
    // le joueur a bien bouge
    MOVED,

    // le mouvement etait impossible
    BLOCKED,

    // une boite a ete poussee dans une target
    BOX_IN_TARGET,

    // le joueur a demande une pause
    PAUSED,

    // la partie a ete sauvegardee
    SAVED,

    // une sauvegarde a ete chargee
    LOADED,

    // le niveau a ete recharge depuis le debut
    RELOADED,

    // un chemin vers la destination a ete trouve
    PATH_FINDING_REQUESTED,

    SOLVER_REQUESTED,

    // la derniere action a ete annulee
    UNDONE,

    // toutes les conditions de victoire sont remplies
    WON,

    // au moins une boite ne peut plus atteindre de target
    BOXES_CANT_REACH_TARGET,

    // aucun effet particulier
    NOTHING
}