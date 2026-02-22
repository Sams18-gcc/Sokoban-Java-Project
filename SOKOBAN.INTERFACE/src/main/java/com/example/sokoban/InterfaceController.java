package com.example.sokoban;



import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;


import javafx.event.Event;

import java.io.IOException;


public class InterfaceController {


@FXML
private MediaView BackgorundVideo;


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
    private AnchorPane SCENE;

    private Stage stage;




    @FXML
    private ImageView skull1,skull2,skull3,skull4;




@FXML
public void initialize() {

    media = new Media(getClass().getResource("/BackgroundVideo.mp4").toExternalForm());
    Interface.mediaPlayer = new MediaPlayer(media);
    BackgorundVideo.setMediaPlayer(Interface.mediaPlayer);
   //BackgorundVideo.setFitHeight(720);
    //BackgroundVideo.setFitWidth(1280);


    BackgorundVideo.setPreserveRatio(true);
    Interface.mediaPlayer.setAutoPlay(true);


    Interface.mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);


    skull1.setImage(new Image(getClass().getResourceAsStream("/skull.png")));
    skull2.setImage(new Image(getClass().getResourceAsStream("/skull.png")));
    skull3.setImage(new Image(getClass().getResourceAsStream("/skull.png")));
    skull4.setImage(new Image(getClass().getResourceAsStream("/skull.png")));


    skull1.setVisible(false);
    skull2.setVisible(false);
    skull3.setVisible(false);
    skull4.setVisible(false);



    Interface.menuSELECTION=new AudioClip(getClass().getResource("/menu-selection.mp3").toExternalForm());

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
        else
        {
            skull4.setVisible(false);
        }
    }




    public void START(ActionEvent event) throws IOException {
        FXMLLoader START = new FXMLLoader(Interface.class.getResource("/com/example/sokoban/START.fxml"));
        Scene sceneSTART=new Scene(START.load(),660, 660);
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();


        stage.setTitle("RULES");
        sceneSTART.getStylesheets().add(getClass().getResource("/com/example/sokoban/START.css").toExternalForm());
        stage.setScene(sceneSTART);
        stage.setResizable(false);

        stage.show();

    }
    public void SETTINGS(ActionEvent event) throws IOException {
        FXMLLoader SETTINGS = new FXMLLoader(Interface.class.getResource("/com/example/sokoban/SETTINGS.fxml"));
        Scene sceneSETTINGS=new Scene(SETTINGS.load(),660, 660);
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();


        stage.setTitle("RULES");
        sceneSETTINGS.getStylesheets().add(getClass().getResource("/com/example/sokoban/SETTINGS.css").toExternalForm());
        stage.setScene(sceneSETTINGS);
        stage.setResizable(false);

        stage.show();

    }
    public void RULES(ActionEvent event) throws IOException {
        FXMLLoader RULES = new FXMLLoader(Interface.class.getResource("/com/example/sokoban/RULES.fxml"));
        Scene sceneRULES=new Scene(RULES.load(),660, 660);
         stage = (Stage) ((Node) event.getSource()).getScene().getWindow();


        stage.setTitle("RULES");
        sceneRULES.getStylesheets().add(getClass().getResource("/com/example/sokoban/RULES.css").toExternalForm());
        stage.setScene(sceneRULES);
        stage.setResizable(false);

        stage.show();
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
