package sokoban.UI.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import sokoban.UI.view.SceneNavigator;

import java.util.Objects;

public class ModeController {

    @FXML private Button storyModeButton;
    @FXML private Button personalizedModeButton;
    @FXML private Button backButton;
    @FXML private ImageView background;
    @FXML private ImageView personalizedModeImage;
    @FXML private ImageView storyImage;
    @FXML private ImageView backImage;
    @FXML private StackPane SCENE;

    // images chargees une seule fois
    private Image imgStory;
    private Image imgFreeMode;
    private Image imgBack;

    public void initialize() {
        background.fitHeightProperty().bind(SCENE.heightProperty());
        background.fitWidthProperty().bind(SCENE.widthProperty());

        storyModeButton.prefWidthProperty().bind(SCENE.widthProperty().multiply(0.4));
        storyModeButton.prefHeightProperty().bind(SCENE.widthProperty().multiply(0.3));
        storyImage.fitWidthProperty().bind(SCENE.widthProperty().multiply(0.4));
        storyImage.fitHeightProperty().bind(SCENE.heightProperty().multiply(0.3));

        personalizedModeButton.prefWidthProperty().bind(SCENE.widthProperty().multiply(0.4));
        personalizedModeButton.prefHeightProperty().bind(SCENE.heightProperty().multiply(0.3));
        personalizedModeImage.fitWidthProperty().bind(SCENE.widthProperty().multiply(0.4));
        personalizedModeImage.fitHeightProperty().bind(SCENE.heightProperty().multiply(0.3));

        backButton.prefWidthProperty().bind(SCENE.widthProperty().multiply(0.2));
        backButton.prefHeightProperty().bind(SCENE.heightProperty().multiply(0.1));
        backImage.fitWidthProperty().bind(SCENE.widthProperty().multiply(0.2));
        backImage.fitHeightProperty().bind(SCENE.heightProperty().multiply(0.1));

        // chargement unique des images
        imgStory    = load("/sokoban/UI/resources/assets/StoryMode.png");
        imgFreeMode = load("/sokoban/UI/resources/assets/FreeMode.png");
        imgBack     = load("/sokoban/UI/resources/assets/BackButton.png");

        storyImage.setImage(imgStory);
        personalizedModeImage.setImage(imgFreeMode);
        backImage.setImage(imgBack);
    }



    @FXML
    private void mouseOnStoryMode(MouseEvent event) {

        storyImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/StoryMode.png"))));

    }

    @FXML
    private void mouseExitStoryMode(MouseEvent event) {

        storyImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/StoryMode.png"))));
    }

    @FXML
    private void mouseEnterFreeMode(MouseEvent event) {

        personalizedModeImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/FreeMode.png"))));

    }

    @FXML
    private void mouseExitFreeMode(MouseEvent event) {

        personalizedModeImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/FreeMode.png"))));
    }

    @FXML
    private void mouseEnterBackButton(MouseEvent event) {

        backImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/BackButton.png"))));

    }

    @FXML
    private void mouseExitBackButton(MouseEvent event) {

        backImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/BackButton.png"))));
    }



    @FXML
    public void toStory(ActionEvent event) {
        getNavigator(event).goToStory();
    }

    @FXML
    public void toPersonalized(ActionEvent event) {
        getNavigator(event).goToPersonalized();
    }

    @FXML
    public void back(ActionEvent event) {
        getNavigator(event).goToInterface();
    }



    private SceneNavigator getNavigator(ActionEvent event) {
        Stage stage = (Stage) SCENE.getScene().getWindow();
        return new SceneNavigator(stage);
    }

    private Image load(String path) {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
    }
}