package sokoban.app;

// definit les differents modes de victoire possibles
public enum VictoryCondition {

    // victoire si tous les mondes de l'arbre sont resolus
    ALL_WORLDS,

    // victoire si au moins une feuille de l'arbre est resolue
    LEAVES_ONLY,

    // victoire si seulement la racine est resolue
    ROOT_ONLY
}