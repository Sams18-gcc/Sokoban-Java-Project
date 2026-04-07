package sokoban.logic;

// Classe les actions utilisateur selon leur nature.
// ajout rec — ajout de la catégorie TRAVERSE pour la navigation inter-mondes
public enum ActionCategory {
    MOVE,
    TRAVERSE,       // ajout rec — navigation entre mondes via portail
    PATH_FINDING,
    STATE,
    INTERFACE,
    UNHANDLED
}
