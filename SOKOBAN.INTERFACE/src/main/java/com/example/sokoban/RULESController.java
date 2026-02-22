package com.example.sokoban;



import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;

import java.io.IOException;


public class RULESController {

    @FXML
    private Text text1,text2,text3,text4;

    @FXML
    private ImageView RulesView,view1,view2,view3,view4,wasdView,PionView,BoxView,TargetView;

    @FXML
    private Label TITRE2;

    @FXML
    private Button BACKbutton;

   @FXML
   private Label sous_titre1,sous_titre2,sous_titre3,sous_titre4;
   @FXML
   private Text sous_text1,sous_text2,sous_text3,sous_text4;

    public void initialize()
    {
        RulesView.setImage(new Image(getClass().getResourceAsStream("/book.png")));
        view1.setImage(new Image(getClass().getResourceAsStream("/alarm.png")));
        view2.setImage(new Image(getClass().getResourceAsStream("/alarm.png")));
        view3.setImage(new Image(getClass().getResourceAsStream("/alarm.png")));
        view4.setImage(new Image(getClass().getResourceAsStream("/alarm.png")));
        wasdView.setImage(new Image(getClass().getResourceAsStream("/wasd.png")));
        PionView.setImage(new Image(getClass().getResourceAsStream("/pion.png")));
        BoxView.setImage(new Image(getClass().getResourceAsStream("/vrai_box.png")));
        TargetView.setImage(new Image(getClass().getResourceAsStream("/target.png")));




    }


    public void BackAction(ActionEvent event) throws IOException {

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(Interface.sceneInterface);

        stage.setResizable(false);

        stage.show();
    }


}
