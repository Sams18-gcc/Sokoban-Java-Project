package sokoban.app;

// ajout rec — enum définissant les modes de condition de victoire disponibles
public enum VictoryCondition {
    ALL_WORLDS,   // DFS : tous les mondes de l'arbre doivent être résolus
    LEAVES_ONLY,  // première feuille résolue = victoire
    ROOT_ONLY     // seul le monde racine (world0) doit être résolu
}
