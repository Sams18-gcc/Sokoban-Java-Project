package sokoban.UI.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sokoban.UI.app.SokobanApp;
import sokoban.UI.view.SceneNavigator;

import java.util.Objects;

public class SettingsController {

    @FXML private Button BACKbutton;
    @FXML private BorderPane SCENE;
    @FXML private ImageView music_image, effect_image;
    @FXML private Label MusicVolume, EffectVolume, LabelMusic, LabelEffect;
    @FXML private Slider SliderEFFECT, SliderMUSIC;
    @FXML private ImageView backImage;
    @FXML private ImageView background;
    @FXML private StackPane ROOT;

    public void initialize() {
        background.setImage(load("/sokoban/UI/resources/assets/SettingsBackground.png"));
        background.fitWidthProperty().bind(ROOT.widthProperty());
        background.fitHeightProperty().bind(ROOT.heightProperty());

        backImage.setImage(load("/sokoban/UI/resources/assets/BackButton.png"));
        backImage.fitWidthProperty().bind(ROOT.widthProperty().multiply(0.18));
        backImage.fitHeightProperty().bind(ROOT.heightProperty().multiply(0.1));

        music_image.setImage(load("/sokoban/UI/resources/assets/music.png"));
        effect_image.setImage(load("/sokoban/UI/resources/assets/effect.png"));

        EffectVolume.setText((int) SliderEFFECT.getValue() + " %");
        MusicVolume.setText((int) SliderMUSIC.getValue() + " %");


        SliderMUSIC.valueProperty().addListener((obs, oldVal, newVal) -> {
            MusicVolume.setText((int) newVal.doubleValue() + " %");
            if (SokobanApp.mediaPlayer != null)
                SokobanApp.mediaPlayer.setVolume(newVal.doubleValue() / 100);
        });

        SliderEFFECT.valueProperty().addListener((obs, oldVal, newVal) -> {
            EffectVolume.setText((int) newVal.doubleValue() + " %");
            if (SokobanApp.menuSELECTION != null)
                SokobanApp.menuSELECTION.setVolume(newVal.doubleValue() / 100);
        });
    }



    @FXML
    public void BackAction(ActionEvent event) {
        Stage stage = (Stage) ROOT.getScene().getWindow();
        new SceneNavigator(stage).goToInterface();
    }



    @FXML
    private void mouseEnterBack(MouseEvent event) {
        backImage.setScaleX(1.1);
        backImage.setScaleY(1.1);
    }

    @FXML
    private void mouseExitBack(MouseEvent event) {
        backImage.setScaleX(1.0);
        backImage.setScaleY(1.0);
    }



    private Image load(String path) {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
    }
}