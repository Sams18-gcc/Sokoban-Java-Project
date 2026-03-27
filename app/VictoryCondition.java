
    package sokoban.app;

    // mode de victoire choisi au lancement
    public enum VictoryCondition {
        ALL_WORLDS,     // DFS : tous les mondes doivent être résolus
        LEAVES_ONLY,    // première feuille résolue = victoire
        BFS_ALL,        // BFS : tous les mondes niveau par niveau
        ROOT_ONLY       // seul le monde surface doit être résolu
    }

