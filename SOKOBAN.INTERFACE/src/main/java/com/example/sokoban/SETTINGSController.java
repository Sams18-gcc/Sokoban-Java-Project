package com.example.sokoban;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SETTINGSController {



    @FXML
    private Button BACKbutton;
    @FXML
    private AnchorPane Settings_fenetre,SCENE;
    @FXML
    private ImageView music_image,effect_image;
    @FXML
    private Label MusicVolume,EffectVolume,TITRE,LabelMusic,LabelEffect;
    @FXML
    private Slider SliderEFFECT,SliderMUSIC;



    public void initialize() {
        music_image.setImage(new Image(getClass().getResourceAsStream("/music.png")));
        effect_image.setImage(new Image(getClass().getResourceAsStream("/effect.png")));

        EffectVolume.setText((int)SliderEFFECT.getValue() +" %");
        MusicVolume.setText((int)SliderMUSIC.getValue() +" %");

        SliderEFFECT.valueProperty().addListener(new ChangeListener<Number>() {
            @Override

            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                EffectVolume.setText((int)SliderEFFECT.getValue() +" %");
            }
        });


        SliderMUSIC.valueProperty().addListener(new ChangeListener<Number>() {
            @Override

            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                MusicVolume.setText((int)SliderMUSIC.getValue() +" %");
            }
        });




        SliderMUSIC.valueProperty().addListener((observableValue, oldval, newval) ->
        {
            if (Interface.mediaPlayer != null) {
                Interface.mediaPlayer.setVolume(newval.doubleValue() / 100);
            }

        });


        SliderEFFECT.valueProperty().addListener((observableValue, oldval, newval) ->
        {
            if (Interface.menuSELECTION != null) {
                Interface.menuSELECTION.setVolume(newval.doubleValue() / 100);
            }

        });



    }



    public void BackAction(ActionEvent event) throws IOException {

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(Interface.sceneInterface);

        stage.setResizable(false);

        stage.show();
    }
    }
