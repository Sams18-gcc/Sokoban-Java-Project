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

import java.util.Objects;

// contrôleur de l'écran des paramètres.
// Gère les sliders de volume pour la musique et les effets sonores.
public class SettingsController {

    /*--------------------------------------------------
                        ATTRIBUTS FXML
    --------------------------------------------------*/
    @FXML private Button     BACKbutton;
    @FXML private AnchorPane Settings_fenetre, SCENE;
    @FXML private ImageView  music_image, effect_image;
    @FXML private Label      MusicVolume, EffectVolume, TITRE, LabelMusic, LabelEffect;
    @FXML private Slider     SliderEFFECT, SliderMUSIC;

    /*--------------------------------------------------
                        INIT JAVAFX
    --------------------------------------------------*/

    // initialise les images et branche les listeners sur les sliders de volume
    public void initialize() {
        music_image.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/music.png"))));
        effect_image.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/effect.png"))));

        EffectVolume.setText((int) SliderEFFECT.getValue() + " %");
        MusicVolume.setText((int)  SliderMUSIC.getValue()  + " %");

        // met à jour le label quand le slider effet change
        SliderEFFECT.valueProperty().addListener((obs, oldVal, newVal) ->
                EffectVolume.setText((int) SliderEFFECT.getValue() + " %"));

        // met à jour le label quand le slider musique change
        SliderMUSIC.valueProperty().addListener((obs, oldVal, newVal) ->
                MusicVolume.setText((int) SliderMUSIC.getValue() + " %"));

        // applique le volume de musique au MediaPlayer
        SliderMUSIC.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (SokobanApp.mediaPlayer != null)
                SokobanApp.mediaPlayer.setVolume(newVal.doubleValue() / 100);
        });

        // applique le volume des effets à l'AudioClip
        SliderEFFECT.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (SokobanApp.menuSELECTION != null)
                SokobanApp.menuSELECTION.setVolume(newVal.doubleValue() / 100);
        });
    }

    /*--------------------------------------------------
                        METHODES — NAVIGATION
    --------------------------------------------------*/

    // retourne à l'écran d'accueil
    public void BackAction(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(SokobanApp.sceneInterface);
        stage.setResizable(false);
        stage.show();
    }
}
