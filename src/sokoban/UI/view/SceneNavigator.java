package sokoban.UI.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sokoban.UI.controller.GameController;
import sokoban.app.Level;

import java.io.IOException;
import java.util.Objects;


 // charger et afficher les scenes JavaFX.
 // Ne contient aucune logique de jeu.

public class SceneNavigator {

    private static final String FXML_GAME = "/sokoban/UI/resources/fxml/Game.fxml";
    private static final String FXML_MODE = "/sokoban/UI/resources/fxml/Mode.fxml";
    private static final String CSS_GAME  = "/sokoban/UI/resources/style/Game.css";
    private static final String CSS_MODE  = "/sokoban/UI/resources/style/Mode.css";

    private final Stage stage;

    public SceneNavigator(Stage stage) {
        this.stage = stage;
    }

     // Charge et affiche l'ecran de jeu pour le niveau donne.

    public void goToLevel(Level level, String levelsDirectory, int nbLevels) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_GAME));
            Parent root = loader.load();

            GameController controller = loader.getController();
            controller.setLevel(level);
            controller.setLevelsInfo(levelsDirectory, nbLevels);

            Scene scene = buildScene(root, CSS_GAME, 660, 660);
            stage.setTitle("LEVEL " + level.getNumLevel());
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setFullScreen(true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


      //Retourne au menu de selection de mode.

    public void goToModeMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_MODE));
            Parent root = loader.load();

            Scene scene = buildScene(root, CSS_MODE,
                    stage.getWidth(), stage.getHeight());
            stage.setTitle("GAME MODE");
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Affiche l'ecran de fin .

    public void goToEndScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_MODE));
            Parent root = loader.load();

            Scene scene = buildScene(root, CSS_MODE,
                    stage.getWidth(), stage.getHeight());
            stage.setTitle("Congratulations!");
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private Scene buildScene(Parent root, String cssPath,
                             double width, double height) {
        Scene scene = new Scene(root, width, height);
        String css = Objects.requireNonNull(
                getClass().getResource(cssPath)).toExternalForm();
        scene.getStylesheets().add(css);
        return scene;
    }
}