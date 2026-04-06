package sokoban.UI.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import sokoban.UI.app.SokobanApp;

import java.io.IOException;
import java.util.Objects;

// contrôleur de l'écran d'accueil.
// Gère la vidéo de fond, les effets sonores et la navigation vers les autres écrans.
public class InterfaceController {

    /*--------------------------------------------------
                        ATTRIBUTS FXML
    --------------------------------------------------*/
    @FXML private MediaView   BackgroundVideo;
    @FXML private Button      buttonSTART;
    @FXML private Button      buttonSETTINGS;
    @FXML private Button      buttonRULES;
    @FXML private Button      buttonEXIT;
    @FXML private Button      buttonEDITOR;
    @FXML private AnchorPane  SCENE;
    @FXML private ImageView   skull1, skull2, skull3, skull4, skull5;

    /*--------------------------------------------------
                        ATTRIBUTS
    --------------------------------------------------*/
    private Media media;
    private Stage stage;

    /*--------------------------------------------------
                        INIT JAVAFX
    --------------------------------------------------*/

    // initialise la vidéo de fond, les images et les effets sonores
    @FXML
    public void initialize() {
        media = new Media(Objects.requireNonNull(
                getClass().getResource("/sokoban/UI/resources/assets/BackgroundVideo.mp4")
        ).toExternalForm());

        SokobanApp.mediaPlayer = new MediaPlayer(media);
        BackgroundVideo.setMediaPlayer(SokobanApp.mediaPlayer);
        BackgroundVideo.setPreserveRatio(true);
        SokobanApp.mediaPlayer.setAutoPlay(true);
        SokobanApp.mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        skull1.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/skull.png"))));
        skull2.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/skull.png"))));
        skull3.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/skull.png"))));
        skull4.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/skull.png"))));
        skull5.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/skull.png"))));

        skull1.setVisible(false);
        skull2.setVisible(false);
        skull3.setVisible(false);
        skull4.setVisible(false);
        skull5.setVisible(false);

        SokobanApp.menuSELECTION = new AudioClip(
                getClass().getResource("/sokoban/UI/resources/assets/menu-selection.mp3").toExternalForm());
    }

    /*--------------------------------------------------
                        METHODES — NAVIGATION
    --------------------------------------------------*/

    // navigue vers l'écran de sélection du mode de jeu
    public void start(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(SokobanApp.class.getResource("/sokoban/UI/resources/fxml/Mode.fxml"));
        Parent root = loader.load();
        Scene  sceneSTART = new Scene(root, 990, 660);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("MODE");
        sceneSTART.getStylesheets().add(getClass().getResource("/sokoban/UI/resources/style/Mode.css").toExternalForm());
        stage.setScene(sceneSTART);
        stage.setResizable(false);
        stage.show();
    }

    // navigue vers l'écran des paramètres
    public void SETTINGS(ActionEvent event) throws IOException {
        FXMLLoader SETTINGS = new FXMLLoader(SokobanApp.class.getResource("/sokoban/UI/resources/fxml/Settings.fxml"));
        Scene sceneSETTINGS = new Scene(SETTINGS.load(), 660, 660);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("RULES");
        sceneSETTINGS.getStylesheets().add(getClass().getResource("/sokoban/UI/resources/style/Settings.css").toExternalForm());
        stage.setScene(sceneSETTINGS);
        stage.setResizable(false);
        stage.show();
    }

    // navigue vers l'écran des règles
    public void RULES(ActionEvent event) throws IOException {
        FXMLLoader RULES = new FXMLLoader(SokobanApp.class.getResource("/sokoban/UI/resources/fxml/Rules.fxml"));
        Scene sceneRULES = new Scene(RULES.load(), 660, 660);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("RULES");
        sceneRULES.getStylesheets().add(getClass().getResource("/sokoban/UI/resources/style/Rules.css").toExternalForm());
        stage.setScene(sceneRULES);
        stage.setResizable(false);
        stage.show();
    }

    // ouvre l'éditeur dans une nouvelle fenêtre et cache le menu principal
    public void EDITOR(ActionEvent event) {
        SokobanApp.menuSELECTION.play();
        Stage mainStage  = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage editorStage = new Stage();
        sokoban.editor.EditorGUI editorGUI = new sokoban.editor.EditorGUI();
        editorGUI.setMainStage(mainStage);
        try {
            editorGUI.start(editorStage);
            mainStage.hide();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Impossible d'ouvrir l'éditeur : " + e.getMessage());
            alert.showAndWait();
        }
    }

    // demande confirmation avant de quitter l'application
    public void EXIT(ActionEvent event) {
        Alert alerte = new Alert(Alert.AlertType.CONFIRMATION);
        alerte.setContentText("VOUS VOULEZ QUITTER LE JEU ?");
        alerte.setHeaderText("SORTIR DE JEU");
        alerte.setTitle("QUITTER");
        if (alerte.showAndWait().get() == ButtonType.OK) {
            stage = (Stage) (SCENE.getScene().getWindow());
            stage.close();
        }
    }

    /*--------------------------------------------------
                        METHODES — EFFETS HOVER
    --------------------------------------------------*/

    // affiche le skull du bouton survolé
    public void AFFICHESKULL(Event event) {
        Button temp = (Button) event.getSource();
        SokobanApp.menuSELECTION.play();
        if      (temp == buttonSTART)    skull1.setVisible(true);
        else if (temp == buttonSETTINGS) skull2.setVisible(true);
        else if (temp == buttonRULES)    skull3.setVisible(true);
        else if (temp == buttonEDITOR)   skull4.setVisible(true);
        else if (temp == buttonEXIT)     skull5.setVisible(true);
    }

    // cache le skull quand la souris quitte le bouton
    public void CAHCERSKULL(Event event) {
        Button temp = (Button) event.getSource();
        if      (temp == buttonSTART)    skull1.setVisible(false);
        else if (temp == buttonSETTINGS) skull2.setVisible(false);
        else if (temp == buttonRULES)    skull3.setVisible(false);
        else if (temp == buttonEDITOR)   skull4.setVisible(false);
        else if (temp == buttonEXIT)     skull5.setVisible(false);
    }

    // joue l'effet sonore de sélection au survol d'un bouton
    public void MenuSOUND(Event event) {
        SokobanApp.menuSELECTION.play();
    }
}
