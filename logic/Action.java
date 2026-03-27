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
 * - si le joueur demande une pause -> PAUSE
 * - si rien ne se passe -> NOTHING
 */
public enum Action {
    // le joueur a bien bouge
    MOVED,

    // le mouvement etait impossible
    BLOCKED,

    // une boite a ete poussee dans une target
    BOX_IN_TARGET,

    // le joueur a demande une pause
    PAUSE,

    // aucun effet particulier
    NOTHING,

    TRAVERSE,
}