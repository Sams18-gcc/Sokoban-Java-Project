package sokoban.UI.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sokoban.UI.view.SceneNavigator;

import java.util.Objects;

public class RulesController {

    @FXML private Text text1, text2, text3, text4;
    @FXML private ImageView RulesView, view1, view2, view3, view4;
    @FXML private ImageView wasdView, PionView, BoxView, TargetView;
    @FXML private Button BACKbutton;
    @FXML private Label sous_titre1, sous_titre2, sous_titre3, sous_titre4;
    @FXML private Text sous_text1, sous_text2, sous_text3, sous_text4;
    @FXML private ImageView background;
    @FXML private ImageView backImage;
    @FXML private StackPane ROOT;

    public void initialize() {
        background.setImage(load("/sokoban/UI/resources/assets/RulesBackground.png"));
        background.fitWidthProperty().bind(ROOT.widthProperty());
        background.fitHeightProperty().bind(ROOT.heightProperty());

        backImage.setImage(load("/sokoban/UI/resources/assets/BackButton.png"));
        backImage.fitWidthProperty().bind(ROOT.widthProperty().multiply(0.18));
        backImage.fitHeightProperty().bind(ROOT.heightProperty().multiply(0.1));

        RulesView.setImage(load("/sokoban/UI/resources/assets/book.png"));

        // alarm chargee une seule fois et assignee aux 4 vues
        Image alarm = load("/sokoban/UI/resources/assets/alarm.png");
        view1.setImage(alarm);
        view2.setImage(alarm);
        view3.setImage(alarm);
        view4.setImage(alarm);

        wasdView.setImage(load("/sokoban/UI/resources/assets/wasd.png"));
        PionView.setImage(load("/sokoban/UI/resources/assets/pion.png"));
        BoxView.setImage(load("/sokoban/UI/resources/assets/box.png"));
        TargetView.setImage(load("/sokoban/UI/resources/assets/target.png"));
    }



    @FXML
    public void BackAction(ActionEvent event) {
        Stage stage = (Stage) ROOT.getScene().getWindow();
        new SceneNavigator(stage).goToInterface();
    }



    @FXML
    private void mouseEnterBack(javafx.scene.input.MouseEvent event) {
        backImage.setScaleX(1.1);
        backImage.setScaleY(1.1);
    }

    @FXML
    private void mouseExitBack(javafx.scene.input.MouseEvent event) {
        backImage.setScaleX(1.0);
        backImage.setScaleY(1.0);
    }



    private Image load(String path) {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
    }
}