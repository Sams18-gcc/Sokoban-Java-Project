package sokoban.UI.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sokoban.UI.controller.GameController;
import sokoban.UI.controller.StartController;
import sokoban.app.Level;

import java.io.IOException;
import java.util.Objects;

public class SceneNavigator {

    private static final String FXML_GAME     = "/sokoban/UI/resources/fxml/Game.fxml";
    private static final String FXML_MODE     = "/sokoban/UI/resources/fxml/Mode.fxml";
    private static final String FXML_SETTINGS = "/sokoban/UI/resources/fxml/Settings.fxml";
    private static final String FXML_RULES    = "/sokoban/UI/resources/fxml/Rules.fxml";
    private static final String FXML_START    = "/sokoban/UI/resources/fxml/Start.fxml";

    private static final String CSS_GAME     = "/sokoban/UI/resources/style/Game.css";
    private static final String CSS_MODE     = "/sokoban/UI/resources/style/Mode.css";
    private static final String CSS_SETTINGS = "/sokoban/UI/resources/style/Settings.css";
    private static final String CSS_RULES    = "/sokoban/UI/resources/style/Rules.css";

    private final Stage stage;

    public SceneNavigator(Stage stage) {
        this.stage = stage;
    }

    public void goToLevel(Level level, String levelsDirectory, int nbLevels) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_GAME));
            Parent root = loader.load();
            GameController controller = loader.getController();

            controller.setLevelsInfo(levelsDirectory, nbLevels);
            Scene scene = buildScene(root, CSS_GAME, 660, 660);
            stage.setTitle("LEVEL " + level.getNumLevel());
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
            stage.show();
            javafx.application.Platform.runLater(() ->
                    javafx.application.Platform.runLater(() ->
                            controller.setLevel(level)
                    )
            );
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void goToModeMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_MODE));
            Parent root = loader.load();
            Scene scene = buildScene(root, CSS_MODE, stage.getWidth(), stage.getHeight());
            stage.setTitle("GAME MODE");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void goToStory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_START));
            Parent root = loader.load();
            StartController controller = loader.getController();
            controller.setLevelDirectoryName("levels/storyMode");
            controller.setBackground("StoryModeBackground.png");
            controller.constructLevels();
            Scene scene = buildScene(root, CSS_MODE, 800, 660);
            stage.setTitle("LEVELS");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void goToPersonalized() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_START));
            Parent root = loader.load();
            StartController controller = loader.getController();
            controller.setLevelDirectoryName("levels/personalized");
            controller.setBackground("FreeModeBackground.png");
            controller.constructLevels();
            Scene scene = buildScene(root, CSS_MODE, 990, 660);
            stage.setTitle("LEVELS");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void goToSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_SETTINGS));
            Parent root = loader.load();
            Scene scene = buildScene(root, CSS_SETTINGS, stage.getWidth(), stage.getHeight());
            stage.setTitle("Settings");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void goToRules() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_RULES));
            Parent root = loader.load();
            Scene scene = buildScene(root, CSS_RULES, stage.getWidth(), stage.getHeight());
            stage.setTitle("Rules");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void goToEndScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_MODE));
            Parent root = loader.load();
            Scene scene = buildScene(root, CSS_MODE, stage.getWidth(), stage.getHeight());
            stage.setTitle("Congratulations!");
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void goToInterface() {
        stage.setScene(sokoban.UI.app.SokobanApp.sceneInterface);
        stage.setResizable(false);
        stage.show();
    }

    private Scene buildScene(Parent root, String cssPath, double width, double height) {
        Scene scene = new Scene(root, width, height);
        String css = Objects.requireNonNull(getClass().getResource(cssPath)).toExternalForm();
        scene.getStylesheets().add(css);
        return scene;
    }
}