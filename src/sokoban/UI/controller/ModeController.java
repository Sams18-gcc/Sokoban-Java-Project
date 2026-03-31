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
import javafx.stage.Stage;
import sokoban.UI.app.SokobanApp;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.Objects;

public class ModeController {

    @FXML
    private Button STORY_BUTTON;
    @FXML
    private Button PERSONALIZED_BUTTON;
    @FXML
    private Button BACK_BUTTON;
    @FXML
    private ImageView BACKGROUND;
    @FXML
    private ImageView PERSO_IMAGE;
    @FXML
    private ImageView STORY_IMAGE;
    @FXML
    private ImageView BACK_IMAGE;

    public void initialize() {


    }
    @FXML
    private void MouseEnter_Story(MouseEvent event) {

        STORY_IMAGE.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/StoryModeHover.png"))));

    }

    @FXML
    private void MouseExit_Story(MouseEvent event) {

        STORY_IMAGE.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/StoryMode.png"))));
    }

    @FXML
    private void MouseEnter_Free(MouseEvent event) {

        PERSO_IMAGE.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/freeModeHover.png"))));

    }

    @FXML
    private void MouseExit_Free(MouseEvent event) {

        PERSO_IMAGE.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/FreeMode.png"))));
    }

    @FXML
    private void MouseEnter_BACK(MouseEvent event) {

        BACK_IMAGE.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/backButtonHover.png"))));

    }

    @FXML
    private void MouseExit_BACK(MouseEvent event) {

        BACK_IMAGE.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/BackButton.png"))));
    }

    public void TO_STORY(ActionEvent event) throws IOException {
        System.out.println("aww1");
        FXMLLoader START = new FXMLLoader(
                SokobanApp.class.getResource("/sokoban/UI/resources/fxml/Start.fxml")
        );
        Parent root = START.load();

         StartController controller = START.getController();
         controller.setLevelDirectoryName("levels/storyMode");
         controller.constructLevels();

        Scene sceneSTART = new Scene(root, 990, 660);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setTitle("LEVELS");
        sceneSTART.getStylesheets().add(
                getClass().getResource("/sokoban/UI/resources/style/Mode.css").toExternalForm()
        );
        stage.setScene(sceneSTART);
        stage.setResizable(false);
        stage.show();



    }

    public void TO_PERSONALIZED()
    {
        System.out.println("aww2");

    }

    public void BACK(ActionEvent event)
    {

        System.out.println("aww3");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(SokobanApp.sceneInterface);

        stage.setResizable(false);

        stage.show();


    }
}
