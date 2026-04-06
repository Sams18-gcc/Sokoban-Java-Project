package sokoban.UI.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import sokoban.UI.app.SokobanApp;
import sokoban.app.Level;
import sokoban.saving.StateManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

// contrôleur de l'écran de sélection des niveaux.
// Lit les dossiers de niveaux, construit les cartes de niveaux (verrouillées / déverrouillées)
// et lance le niveau sélectionné.
public class StartController {

    /*--------------------------------------------------
                        ATTRIBUTS FXML
    --------------------------------------------------*/
    @FXML private Button     BACKbutton;
    @FXML private AnchorPane LEVELS_fenetre, SCENE;
    @FXML private Label      TITRE2;
    @FXML private TilePane   levelsContainer;

    /*--------------------------------------------------
                        ATTRIBUTS
    --------------------------------------------------*/
    private Stage  stage;
    private String levelDirectoryName;

    /*--------------------------------------------------
                        SETTERS
    --------------------------------------------------*/
    public void setLevelDirectoryName(String levelDirectoryName) {
        this.levelDirectoryName = levelDirectoryName;
    }

    /*--------------------------------------------------
                        METHODES — CONSTRUCTION UI
    --------------------------------------------------*/

    // lit tous les niveaux du dossier et crée une carte pour chacun
    public void constructLevels() {
        File levelsFolder = new File(levelDirectoryName);
        if (!levelsFolder.exists() || !levelsFolder.isDirectory())
            throw new IllegalStateException("Dossier invalide : " + levelDirectoryName);

        int index = 1;
        while (index <= getNbLevels()) {
            File levelFolder = new File(levelDirectoryName, "level" + index);
            if (!levelFolder.exists() || !levelFolder.isDirectory())
                throw new IllegalStateException("level" + index + " manquant dans : " + levelDirectoryName);
            File stateFile = new File(levelFolder, "state.txt");
            if (!stateFile.exists() || !stateFile.isFile())
                throw new IllegalStateException("state.txt manquant dans : " + levelFolder.getName());

            try (BufferedReader reader = new BufferedReader(new FileReader(stateFile))) {
                String line = reader.readLine();
                levelsContainer.getChildren().add(createLevelCard(index, line));
                index++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // retourne le nombre de sous-dossiers (= nombre de niveaux) dans levelDirectoryName
    public int getNbLevels() {
        File levelsFolder = new File(levelDirectoryName);
        if (!levelsFolder.exists() || !levelsFolder.isDirectory())
            throw new IllegalStateException("Dossier invalide : " + levelDirectoryName);
        File[] levels = levelsFolder.listFiles(File::isDirectory);
        return levels == null ? 0 : levels.length;
    }

    // crée la carte visuelle d'un niveau (verrouillé ou déverrouillé)
    private VBox createLevelCard(int numLevel, String state) {
        VBox card = new VBox();
        card.setSpacing(4);
        card.setPrefWidth(135);
        card.setPrefHeight(175);
        card.setStyle("-fx-alignment: center;");

        Image locked        = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/lockedGold.png")));
        Image lockedHover   = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/lockedHover.png")));
        Image unlocked      = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/unlocked.png")));
        Image unlockedHover = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/unlockedHover.png")));

        state = (state == null) ? "" : state.trim();
        ImageView icon;

        if ("locked".equals(state)) {
            icon = new ImageView(locked);
            card.setOnMouseEntered(e -> icon.setImage(lockedHover));
            card.setOnMouseExited(e  -> icon.setImage(locked));
        } else {
            icon = new ImageView(unlocked);
            card.setOnMouseEntered(e -> icon.setImage(unlockedHover));
            card.setOnMouseExited(e  -> icon.setImage(unlocked));
            card.setOnMouseClicked(e -> {
                Stage stage = (Stage) card.getScene().getWindow();
                launchLevel(numLevel, stage);
            });
        }

        icon.setFitWidth(130);
        icon.setFitHeight(170);
        icon.setPreserveRatio(true);

        Label label = new Label(Integer.toString(numLevel));
        label.setPrefWidth(135);
        label.setMinWidth(135);
        label.setMaxWidth(135);
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setStyle("-fx-text-fill: #D8B35A; -fx-font-size: 16px;");

        card.getChildren().addAll(icon, label);
        return card;
    }

    /*--------------------------------------------------
                        METHODES — LANCEMENT NIVEAU
    --------------------------------------------------*/

    // initialise le niveau et charge la scène Game
    public void launchLevel(int numLevel, Stage stage) {
        StateManager sm    = new StateManager();
        Level        level = new Level(numLevel, levelDirectoryName, sm);
        level.init();

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/sokoban/UI/resources/fxml/Game.fxml"));
            Parent root = loader.load();

            GameController controller = loader.getController();
            controller.setLevel(level);
            controller.setLevelsInfo(levelDirectoryName, getNbLevels());

            Scene scene = new Scene(root, 660, 660);
            scene.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("/sokoban/UI/resources/style/Game.css")
            ).toExternalForm());

            stage.setTitle("LEVEL " + numLevel);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*--------------------------------------------------
                        METHODES — NAVIGATION
    --------------------------------------------------*/

    // retourne à l'écran d'accueil
    public void BackAction(ActionEvent event) {
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(SokobanApp.sceneInterface);
        stage.setResizable(false);
        stage.show();
    }
}
