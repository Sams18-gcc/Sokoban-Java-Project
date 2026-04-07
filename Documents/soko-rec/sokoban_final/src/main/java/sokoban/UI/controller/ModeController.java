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

import java.io.IOException;
import java.util.Objects;

// contrôleur de l'écran de sélection du mode de jeu (Story / Personnalisé).
// Gère les effets hover sur les boutons et la navigation vers l'écran de niveaux.
public class ModeController {

    /*--------------------------------------------------
                        ATTRIBUTS FXML
    --------------------------------------------------*/
    @FXML private Button    storyButton;
    @FXML private Button    personalizedButton;
    @FXML private Button    backButton;
    @FXML private ImageView background;
    @FXML private ImageView persoImage;
    @FXML private ImageView storyImage;
    @FXML private ImageView backImage;

    /*--------------------------------------------------
                        METHODES — HOVER
    --------------------------------------------------*/

    // effets visuels au survol des boutons de mode
    @FXML private void mouseOnStoryMode(MouseEvent event)   { storyImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/StoryModeHover.png")))); }
    @FXML private void mouseExitStoryMode(MouseEvent event) { storyImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/StoryMode.png")))); }
    @FXML private void mouseEnterFreeMode(MouseEvent event) { persoImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/freeModeHover.png")))); }
    @FXML private void mouseExitFreeMode(MouseEvent event)  { persoImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/FreeMode.png")))); }
    @FXML private void mouseEnterBackButton(MouseEvent event){ backImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/backButtonHover.png")))); }
    @FXML private void mouseExitBackButton(MouseEvent event) { backImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/BackButton.png")))); }

    /*--------------------------------------------------
                        METHODES — NAVIGATION
    --------------------------------------------------*/

    // navigue vers la sélection des niveaux en mode Story
    public void toStory(ActionEvent event) throws IOException {
        FXMLLoader start = new FXMLLoader(SokobanApp.class.getResource("/sokoban/UI/resources/fxml/Start.fxml"));
        Parent root = start.load();
        StartController controller = start.getController();
        controller.setLevelDirectoryName("levels/storyMode");
        controller.constructLevels();

        Scene sceneSTART = new Scene(root, 990, 660);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("LEVELS");
        sceneSTART.getStylesheets().add(getClass().getResource("/sokoban/UI/resources/style/Mode.css").toExternalForm());
        stage.setScene(sceneSTART);
        stage.setResizable(false);
        stage.show();
    }

    // navigue vers la sélection des niveaux en mode Personnalisé
    public void toPersonalized(ActionEvent event) {
        FXMLLoader start = new FXMLLoader(SokobanApp.class.getResource("/sokoban/UI/resources/fxml/Start.fxml"));
        try {
            Parent root = start.load();
            StartController controller = start.getController();
            controller.setLevelDirectoryName("levels/personnalized");
            controller.constructLevels();

            Scene sceneSTART = new Scene(root, 990, 660);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("LEVELS");
            sceneSTART.getStylesheets().add(getClass().getResource("/sokoban/UI/resources/style/Mode.css").toExternalForm());
            stage.setScene(sceneSTART);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // retourne à l'écran d'accueil
    public void back(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(SokobanApp.sceneInterface);
        stage.setResizable(false);
        stage.show();
    }
}
