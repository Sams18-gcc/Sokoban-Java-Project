package sokoban.UI.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sokoban.UI.app.SokobanApp;

import java.io.IOException;
import java.util.Objects;

public class SettingsController {



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
    ///  VERIFIER CE QUI N'EST PAS UTILISE DANS LE CODE


    public void initialize() {
        music_image.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/music.png"))));
        effect_image.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/effect.png"))));

        EffectVolume.setText((int)SliderEFFECT.getValue() +" %");
        MusicVolume.setText((int)SliderMUSIC.getValue() +" %");

        SliderEFFECT.valueProperty().addListener(new ChangeListener<Number>() {
            @Override

            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                EffectVolume.setText((int)SliderEFFECT.getValue() +" %");
            }
        });
        /// ON PEUT REGROUPER LES METHODES 2 PAR 2

        SliderMUSIC.valueProperty().addListener(new ChangeListener<Number>() {
            @Override

            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                MusicVolume.setText((int)SliderMUSIC.getValue() +" %");
            }
        });




        SliderMUSIC.valueProperty().addListener((observableValue, oldval, newval) ->
        {
            if (SokobanApp.mediaPlayer != null) {
                SokobanApp.mediaPlayer.setVolume(newval.doubleValue() / 100);
            }

        });


        SliderEFFECT.valueProperty().addListener((observableValue, oldval, newval) ->
        {
            if (SokobanApp.menuSELECTION != null) {
                SokobanApp.menuSELECTION.setVolume(newval.doubleValue() / 100);
            }

        });



    }



    public void BackAction(ActionEvent event) throws IOException {

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(SokobanApp.sceneInterface);

        stage.setResizable(false);

        stage.show();
    }
    }
