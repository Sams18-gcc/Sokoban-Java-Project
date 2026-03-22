package sokoban.IG.java;


import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
    private AnchorPane SCENE;

    private Stage stage;




    @FXML
    private ImageView skull1,skull2,skull3,skull4,skull5;




    @FXML
    public void initialize() {

        media = new Media(Objects.requireNonNull(getClass().getResource("/sokoban/IG/resources/photo/BackgroundVideo.mp4")).toExternalForm());
        Interface.mediaPlayer = new MediaPlayer(media);
        BackgroundVideo.setMediaPlayer(Interface.mediaPlayer);
        //BackgorundVideo.setFitHeight(720);
        //BackgroundVideo.setFitWidth(1280);


        BackgroundVideo.setPreserveRatio(true);
        Interface.mediaPlayer.setAutoPlay(true);


        Interface.mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);


        skull1.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/skull.png"))));
        skull2.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/skull.png"))));
        skull3.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/skull.png"))));
        skull4.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/skull.png"))));
        skull5.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/skull.png"))));


        skull1.setVisible(false);
        skull2.setVisible(false);
        skull3.setVisible(false);
        skull4.setVisible(false);
        skull5.setVisible(false);



        Interface.menuSELECTION=new AudioClip(getClass().getResource("/sokoban/IG/resources/photo/menu-selection.mp3").toExternalForm());

    }
    public void AFFICHESKULL(Event event)
    {

        Button temp=(Button)event.getSource();

        if(temp==buttonSTART)
        {
            Interface.menuSELECTION.play();
            skull1.setVisible(true);
        }
        else if(temp==buttonSETTINGS)
        {
            Interface.menuSELECTION.play();
            skull2.setVisible(true);
        }
        else if(temp==buttonRULES)
        {
            Interface.menuSELECTION.play();
            skull3.setVisible(true);
        }
        else if(temp==buttonEDITOR)
        {
            Interface.menuSELECTION.play();
            skull5.setVisible(true);
        }
        else
        {
            Interface.menuSELECTION.play();
            skull4.setVisible(true);
        }
    }
    public void CAHCERSKULL(Event event)
    {

        Button temp=(Button)event.getSource();

        if(temp==buttonSTART)
        {
            skull1.setVisible(false);
        }
        else if(temp==buttonSETTINGS)
        {
            skull2.setVisible(false);
        }
        else if(temp==buttonRULES)
        {
            skull3.setVisible(false);
        }
        else if(temp==buttonEDITOR)
        {
            skull5.setVisible(false);
        }
        else
        {
            skull4.setVisible(false);
        }
    }




    public void START(ActionEvent event) throws IOException {
        FXMLLoader START = new FXMLLoader(Interface.class.getResource("/sokoban/IG/resources/designe/START.fxml"));
        Scene sceneSTART=new Scene(START.load(),660, 660);
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();


        stage.setTitle("RULES");
        sceneSTART.getStylesheets().add(getClass().getResource("/sokoban/IG/resources/designe/START.css").toExternalForm());
        stage.setScene(sceneSTART);
        stage.setResizable(false);

        stage.show();

    }
    public void SETTINGS(ActionEvent event) throws IOException {
        FXMLLoader SETTINGS = new FXMLLoader(Interface.class.getResource("/sokoban/IG/resources/designe/SETTINGS.fxml"));
        Scene sceneSETTINGS=new Scene(SETTINGS.load(),660, 660);
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();


        stage.setTitle("RULES");
        sceneSETTINGS.getStylesheets().add(getClass().getResource("/sokoban/IG/resources/designe/SETTINGS.css").toExternalForm());
        stage.setScene(sceneSETTINGS);
        stage.setResizable(false);

        stage.show();

    }
    public void RULES(ActionEvent event) throws IOException {
        FXMLLoader RULES = new FXMLLoader(Interface.class.getResource("/sokoban/IG/resources/designe/RULES.fxml"));
        Scene sceneRULES=new Scene(RULES.load(),660, 660);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();


        stage.setTitle("RULES");
        sceneRULES.getStylesheets().add(getClass().getResource("/sokoban/IG/resources/designe/RULES.css").toExternalForm());
        stage.setScene(sceneRULES);
        stage.setResizable(false);

        stage.show();
    }
    public void EDITOR(ActionEvent event) {
        // Ouvrir l'éditeur de plateau dans une nouvelle fenêtre
        Interface.menuSELECTION.play();
        Stage editorStage = new Stage();
        sokoban.editor.EditorGUI editorGUI = new sokoban.editor.EditorGUI();
        try {
            editorGUI.start(editorStage);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Impossible d'ouvrir l'éditeur : " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void EXIT(ActionEvent event)
    {
        Alert alerte=new Alert(Alert.AlertType.CONFIRMATION);
        alerte.setContentText("VOUS VOULEZ QUITTER LE JEU ?");
        alerte.setHeaderText("SORTIR DE JEU");
        alerte.setTitle("QUITTER");
        if(alerte.showAndWait().get()== ButtonType.OK)
        {
            stage=(Stage)(SCENE.getScene().getWindow());
            stage.close();
        }


    }

    public void MenuSOUND(Event event)
    {

        Button temp=(Button)event.getSource();

        if(temp==buttonSTART)
        {
            Interface.menuSELECTION.play();

        }
        else if(temp==buttonSETTINGS)
        {
            Interface.menuSELECTION.play();

        }
        else if(temp==buttonRULES)
        {
            Interface.menuSELECTION.play();

        }
        else
        {
            Interface.menuSELECTION.play();

        }
    }



}
