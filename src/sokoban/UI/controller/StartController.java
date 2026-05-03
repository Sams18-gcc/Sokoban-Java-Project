package sokoban.UI.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import sokoban.UI.view.SceneNavigator;
import sokoban.app.Level;
import sokoban.saving.StateManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class StartController {

    @FXML private Button BACKbutton;
    @FXML private AnchorPane SCENE;
    @FXML private TilePane levelsContainer;
    @FXML private ImageView background;
    @FXML private ImageView backImage;
    @FXML private StackPane ROOT;

    private String levelDirectoryName;

    // images chargees une seule fois pour toutes les cartes
    private Image imgLocked;
    private Image imgLockedHover;
    private Image imgUnlocked;
    private Image imgUnlockedHover;

    public void initialize() {
        backImage.setImage(load("/sokoban/UI/resources/assets/BackButton.png"));
        backImage.fitWidthProperty().bind(ROOT.widthProperty().multiply(0.18));
        backImage.fitHeightProperty().bind(ROOT.heightProperty().multiply(0.1));
        background.fitWidthProperty().bind(ROOT.widthProperty());
        background.fitHeightProperty().bind(ROOT.heightProperty());
        levelsContainer.setPadding(new Insets(20));
        levelsContainer.prefWidthProperty().bind(ROOT.widthProperty().multiply(0.6));

        // chargement unique des images de cartes
        imgLocked       = load("/sokoban/UI/resources/assets/lockedGold.png");
        imgLockedHover  = load("/sokoban/UI/resources/assets/lockedHover.png");
        imgUnlocked     = load("/sokoban/UI/resources/assets/unlocked.png");
        imgUnlockedHover= load("/sokoban/UI/resources/assets/unlockedHover.png");
    }



    @FXML
    public void BackAction(ActionEvent event) {
        Stage stage = (Stage) ROOT.getScene().getWindow();
        new SceneNavigator(stage).goToModeMenu();
    }


    public void constructLevels() {
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

    public int getNbLevels() {
        File levelsFolder = new File(levelDirectoryName);
        if (!levelsFolder.exists() || !levelsFolder.isDirectory())
            throw new IllegalStateException("Dossier invalide : " + levelDirectoryName);
        File[] levels = levelsFolder.listFiles(File::isDirectory);
        return (levels == null) ? 0 : levels.length;
    }

    private VBox createLevelCard(int numLevel, String state) {
        VBox card = new VBox();
        card.setSpacing(4);
        card.setPrefWidth(135);
        card.setPrefHeight(175);
        card.setStyle("-fx-alignment: center;");

        state = (state == null) ? "" : state.trim();
        ImageView icon;

        if ("locked".equals(state)) {
            icon = new ImageView(imgLocked);
            card.setOnMouseEntered(e -> icon.setImage(imgLockedHover));
            card.setOnMouseExited(e  -> icon.setImage(imgLocked));
        } else {
            icon = new ImageView(imgUnlocked);
            card.setOnMouseEntered(e -> icon.setImage(imgUnlockedHover));
            card.setOnMouseExited(e  -> icon.setImage(imgUnlocked));
            card.setOnMouseClicked(e -> {
                Stage stage = (Stage) card.getScene().getWindow();
                new SceneNavigator(stage).goToLevel(buildLevel(numLevel), levelDirectoryName, getNbLevels());
            });
        }

        icon.fitWidthProperty().bind(ROOT.widthProperty().multiply(0.12));
        icon.fitHeightProperty().bind(ROOT.heightProperty().multiply(0.2));
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



    @FXML
    private void mouseEnterBack(javafx.scene.input.MouseEvent event) {
        backImage.setScaleX(1.1);
        backImage.setScaleY(1.1);
    }

    @FXML
    private void mouseExitBack(javafx.scene.input.MouseEvent event) {
        backImage.setScaleX(1.0);
        backImage.setScaleY(1.0);
    }



    public void setLevelDirectoryName(String levelDirectoryName) {
        this.levelDirectoryName = levelDirectoryName;
    }

    public void setBackground(String imageName) {
        background.setImage(load("/sokoban/UI/resources/assets/" + imageName));
    }



    private Level buildLevel(int numLevel) {
        StateManager sm = new StateManager();
        Level level = new Level(numLevel, levelDirectoryName, sm);
        level.init();
        return level;
    }

    private Image load(String path) {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
    }
}