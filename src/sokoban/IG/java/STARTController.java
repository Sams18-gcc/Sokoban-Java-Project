package sokoban.IG.java;


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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import sokoban.app.Level;
import sokoban.saving.StateManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class STARTController {

    @FXML
    private Button BACKbutton;

    @FXML
    private AnchorPane LEVELS_fenetre,SCENE;
    @FXML
    private Label TITRE2;
    @FXML
    private TilePane levelsContainer;

    private Stage stage;
    private String levelDirectoryName;
    public void initialize() {

    }




    public void BackAction(ActionEvent event)
    {
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(Interface.sceneInterface);

        stage.setResizable(false);

        stage.show();
    }


    public void setLevelDirectoryName(String levelDirectoryName)
    {
        this.levelDirectoryName = levelDirectoryName;
    }

    public void constructLevels() {
        File levelsFolder = new File(levelDirectoryName);

        if (!levelsFolder.exists() || !levelsFolder.isDirectory()) {
            throw new IllegalStateException("Dossier invalide : " + levelDirectoryName);
        }

        File[] levels = levelsFolder.listFiles();

        if (levels == null) {
            throw new IllegalStateException("Impossible de lire le dossier : " + levelDirectoryName);
        }

        int index = 1;
        while(index <= getNbLevels())
        {
            File levelFolder = new File(levelDirectoryName, "level" + index);
            if(!levelFolder.exists() || !levelFolder.isDirectory())
                throw new IllegalStateException("level" +index+ " manquant dans : " + levelDirectoryName);
            File stateFile = new File(levelFolder, "state.txt");
            if (!stateFile.exists() || !stateFile.isFile()) {
                throw new IllegalStateException("state.txt manquant dans : " + levelFolder.getName());
            }

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

        if (!levelsFolder.exists() || !levelsFolder.isDirectory()) {
            throw new IllegalStateException("Dossier invalide : " + levelDirectoryName);
        }

        File[] levels = levelsFolder.listFiles(File::isDirectory);

        if (levels == null) {
            return 0;
        }

        return levels.length;
    }



    private VBox createLevelCard(int numLevel, String state) {
        VBox card = new VBox();
        card.setSpacing(4);
        card.setPrefWidth(135);
        card.setPrefHeight(175);

        card.setStyle("-fx-alignment: center;");
        ImageView icon;
        state = (state == null) ? "" : state.trim();
        Image locked = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/lockedGold.png")));
        Image lockedHover = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/lockedHover.png")));
        Image unlocked = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/unlocked.png")));
        Image unlockedHover = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/unlockedHover.png")));
        if("locked".equals(state))
        {
             icon = new ImageView(
                   locked
            );
            card.setOnMouseEntered(e -> icon.setImage(lockedHover));
            card.setOnMouseExited(e -> icon.setImage(locked));

        }else{
             icon = new ImageView(
                    unlocked
            );
             card.setOnMouseEntered(e -> icon.setImage(unlockedHover));
             card.setOnMouseExited(e -> icon.setImage(unlocked));
             card.setOnMouseClicked(e -> {
                Stage stage = (Stage) card.getScene().getWindow();
                launchLevel(numLevel, stage);
             });

        }
        icon.setFitWidth(130);
        icon.setFitHeight(170);
        icon.setPreserveRatio(true);





        Label label = new Label(Integer.toString(numLevel));
        label.setPrefWidth(135);          // même largeur que la card
        label.setMinWidth(135);
        label.setMaxWidth(135);
        label.setAlignment(Pos.CENTER);   // centre le texte dans le label
        label.setTextAlignment(TextAlignment.CENTER);
        label.setStyle("-fx-text-fill: #D8B35A; -fx-font-size: 16px;");
        card.getChildren().addAll(icon, label);



        return card;
    }

    public void launchLevel(int numLevel, Stage stage) {
        StateManager sm = new StateManager();
        Level level = new Level(2, sm);
        level.init();

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/sokoban/IG/resources/designe/Game.fxml")
            );

            Parent root = loader.load();

            GAMEController controller = loader.getController();
            controller.setLevel(level);

            Scene scene = new Scene(root, 660, 660);
            scene.getStylesheets().add(
                    Objects.requireNonNull(
                            getClass().getResource("/sokoban/IG/resources/designe/Game.css")
                    ).toExternalForm()
            );

            stage.setTitle("LEVEL " + numLevel);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



