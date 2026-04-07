package sokoban.UI.app;

import javafx.application.Application;

// point d'entrée JavaFX — délègue à SokobanApp
public class Launcher {
    public static void main(String[] args) {
        Application.launch(SokobanApp.class, args);
    }
}
