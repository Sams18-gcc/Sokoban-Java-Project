package sokoban.editor;

import javafx.application.Application;

// point d'entrée JavaFX de l'éditeur en mode standalone
public class EditorLauncher {
    public static void main(String[] args) {
        Application.launch(EditorGUI.class, args);
    }
}
