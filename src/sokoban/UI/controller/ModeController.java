package sokoban.UI.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import sokoban.UI.app.SokobanApp;

import java.io.IOException;
import java.util.Objects;

public class ModeController {

    @FXML
    private Button storyModeButton;
    @FXML
    private Button personalizedModeButton;
    @FXML
    private Button backButton;
    @FXML
    private ImageView background;
    @FXML
    private ImageView personalizedModeImage;
    @FXML
    private ImageView storyImage;
    @FXML
    private ImageView backImage;
    @FXML
    private StackPane SCENE;

    public void initialize() {

        background.fitHeightProperty().bind(SCENE.heightProperty());
        background.fitWidthProperty().bind(SCENE.widthProperty());
        storyModeButton.prefWidthProperty().bind(SCENE.widthProperty().multiply(0.4));
        storyModeButton.prefHeightProperty().bind((SCENE.widthProperty().multiply(0.3)));
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

    public void toStory(ActionEvent event) throws IOException {
        FXMLLoader start = new FXMLLoader(
                SokobanApp.class.getResource("/sokoban/UI/resources/fxml/Start.fxml")
        );
        Parent root = start.load();

        StartController controller = start.getController();
        controller.setLevelDirectoryName("levels/storyMode");
        controller.setBackground("StoryModeBackground.png");
        controller.constructLevels();

        Scene sceneSTART = new Scene(root, 800, 660);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setTitle("LEVELS");
        sceneSTART.getStylesheets().add(
                getClass().getResource("/sokoban/UI/resources/style/Mode.css").toExternalForm()
        );
        stage.setScene(sceneSTART);
        stage.setResizable(true);
        stage.show();


    }

    public void toPersonalized(ActionEvent event)
    {
        FXMLLoader start = new FXMLLoader(
                SokobanApp.class.getResource("/sokoban/UI/resources/fxml/Start.fxml")
        );
       try{
           Parent root = start.load();
           StartController controller = start.getController();
           controller.setLevelDirectoryName("levels/personalized");
           controller.constructLevels();
           controller.setBackground("FreeModeBackground.png");

           Scene sceneSTART = new Scene(root, 990, 660);
           Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

           stage.setTitle("LEVELS");
           sceneSTART.getStylesheets().add(
                   getClass().getResource("/sokoban/UI/resources/style/Mode.css").toExternalForm()
           );
           stage.setScene(sceneSTART);
           stage.setResizable(true);
           stage.show();
       }catch(IOException e)
       {
           e.printStackTrace();
       }




    }

    public void back(ActionEvent event) {


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(SokobanApp.sceneInterface);

        stage.setResizable(false);

        stage.show();


    }
}

