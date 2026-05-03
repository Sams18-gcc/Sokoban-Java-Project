package sokoban.UI.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import sokoban.UI.view.SceneNavigator;
import sokoban.UI.view.GameRenderer;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import sokoban.UI.app.SokobanApp;

import java.io.IOException;
import java.util.Objects;

public class InterfaceController {

    @FXML
    private MediaView BackgroundVideo;

    private Media media;

    @FXML
    private Button buttonSTART;
    @FXML
    private Button buttonSETTINGS;
    @FXML
    private Button buttonRULES;
    @FXML
    private Button buttonEXIT;
    @FXML
    private Button buttonEDITOR;
    @FXML
    private StackPane SCENE;

    private Stage stage;

    @FXML
    private ImageView skull1, skull2, skull3, skull4, skull5;

    @FXML
    public void initialize() {

        media = new Media(Objects.requireNonNull(
                getClass().getResource("/sokoban/UI/resources/assets/BackgroundVideo.mp4")
        ).toExternalForm());


        SokobanApp.mediaPlayer = new MediaPlayer(media);
        BackgroundVideo.setMediaPlayer(SokobanApp.mediaPlayer);


        BackgroundVideo.setPreserveRatio(false);
        SokobanApp.mediaPlayer.setAutoPlay(true);
        SokobanApp.mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        BackgroundVideo.fitWidthProperty().bind(SCENE.widthProperty());
        BackgroundVideo.fitHeightProperty().bind(SCENE.heightProperty());

        skull1.setImage(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/sokoban/UI/resources/assets/skull.png")
        )));
        skull2.setImage(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/sokoban/UI/resources/assets/skull.png")
        )));
        skull3.setImage(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/sokoban/UI/resources/assets/skull.png")
        )));
        skull4.setImage(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/sokoban/UI/resources/assets/skull.png")
        )));
        skull5.setImage(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/sokoban/UI/resources/assets/skull.png")
        )));

        skull1.setVisible(false);
        skull2.setVisible(false);
        skull3.setVisible(false);
        skull4.setVisible(false);
        skull5.setVisible(false);


        SokobanApp.menuSELECTION = new AudioClip(
                getClass().getResource("/sokoban/UI/resources/assets/menu-selection.mp3").toExternalForm()
        );
    }

    public void AFFICHESKULL(Event event) {
        Button temp = (Button) event.getSource();

        if (temp == buttonSTART) {
            SokobanApp.menuSELECTION.play();
            skull1.setVisible(true);
        } else if (temp == buttonSETTINGS) {
            SokobanApp.menuSELECTION.play();
            skull2.setVisible(true);
        } else if (temp == buttonRULES) {
            SokobanApp.menuSELECTION.play();
            skull3.setVisible(true);
        } else if (temp == buttonEDITOR) {
            SokobanApp.menuSELECTION.play();
            skull4.setVisible(true);
        } else if (temp == buttonEXIT) {
        SokobanApp.menuSELECTION.play();
        skull5.setVisible(true);
    }
    }

    public void CAHCERSKULL(Event event) {
        Button temp = (Button) event.getSource();

        if (temp == buttonSTART) {
            skull1.setVisible(false);
        } else if (temp == buttonSETTINGS) {
            skull2.setVisible(false);
        } else if (temp == buttonRULES) {
            skull3.setVisible(false);
        } else if (temp == buttonEDITOR) {
            skull4.setVisible(false);
        } else if (temp == buttonEXIT) {
            skull5.setVisible(false);
        }
    }



    public void exit(ActionEvent event) {
        Alert alerte = new Alert(Alert.AlertType.CONFIRMATION);
        alerte.setContentText("VOUS VOULEZ QUITTER LE JEU ?");
        alerte.setHeaderText("SORTIR DE JEU");
        alerte.setTitle("QUITTER");

        if (alerte.showAndWait().get() == ButtonType.OK) {
            stage = (Stage) (SCENE.getScene().getWindow());
            stage.close();
        }
    }
    @FXML
    public void start(ActionEvent event) {
        getNavigator(event).goToModeMenu();
    }



    @FXML
    public void settings(ActionEvent event) {
        getNavigator(event).goToSettings();
    }

    @FXML
    public void rules(ActionEvent event) {
        getNavigator(event).goToRules();
    }

    @FXML
    public void editor(ActionEvent event) {
        Stage editorStage = new Stage();
        sokoban.editor.EditorGUI editorGUI = new sokoban.editor.EditorGUI();
        try {
            editorGUI.start(editorStage);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Impossible d'ouvrir l'editeur : " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void MenuSOUND(Event event) {
        Button temp = (Button) event.getSource();

        if (temp == buttonSTART) {
            SokobanApp.menuSELECTION.play();
        } else if (temp == buttonSETTINGS) {
            SokobanApp.menuSELECTION.play();
        } else if (temp == buttonRULES) {
            SokobanApp.menuSELECTION.play();
        } else {
            SokobanApp.menuSELECTION.play();
        }
    }
    private SceneNavigator getNavigator(ActionEvent event) {
        Stage stage = (Stage) SCENE.getScene().getWindow();
        return new SceneNavigator(stage);
    }

}