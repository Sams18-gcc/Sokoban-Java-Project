package sokoban.logic;

/*
 * Résultat d'une action exécutée dans le jeu.
 * L'interface (terminale ou graphique) lit cette valeur
 * pour savoir quoi afficher ou comment réagir.
 */
public enum ResultOfAction {
    MOVED,                      // le joueur a bien bougé
    BLOCKED,                    // le mouvement était impossible
    BOX_IN_TARGET,              // une boîte a été poussée dans une target
    PAUSED,                     // le joueur a demandé une pause
    SAVED,                      // la partie a été sauvegardée
    LOADED,                     // une sauvegarde a été chargée
    RELOADED,                   // le niveau a été rechargé depuis le début
    PATH_FINDING_REQUESTED,     // un chemin vers la destination a été trouvé
    UNDONE,                     // la dernière action a été annulée
    WON,                        // toutes les conditions de victoire sont remplies
    BOXES_CANT_REACH_TARGET,    // au moins une boîte ne peut plus atteindre de target
    NOTHING                     // aucun effet particulier
}
