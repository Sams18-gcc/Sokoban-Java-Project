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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import sokoban.UI.app.SokobanApp;

import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.Objects;

public class SettingsController {



    @FXML
    private Button BACKbutton;
    @FXML
    private BorderPane SCENE;

    @FXML
    private ImageView music_image,effect_image;
    @FXML
    private Label MusicVolume,EffectVolume,LabelMusic,LabelEffect;
    @FXML
    private Slider SliderEFFECT,SliderMUSIC;
    @FXML
    private ImageView backImage;

    @FXML private ImageView background;
    @FXML private StackPane ROOT;



    public void initialize() {
        backImage.setImage(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/sokoban/UI/resources/assets/BackButton.png"))));
        background.setImage(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/sokoban/UI/resources/assets/SettingsBackground.png"))));
        background.fitWidthProperty().bind(ROOT.widthProperty());
        background.fitHeightProperty().bind(ROOT.heightProperty());

        backImage.fitWidthProperty().bind(ROOT.widthProperty().multiply(0.18));
        backImage.fitHeightProperty().bind(ROOT.heightProperty().multiply(0.1));
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

    public void BackAction(ActionEvent event) throws IOException {

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(SokobanApp.sceneInterface);

        stage.setResizable(false);

        stage.show();
    }
    }
