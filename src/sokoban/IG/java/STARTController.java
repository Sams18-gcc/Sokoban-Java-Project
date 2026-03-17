package sokoban.IG.java;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class STARTController {

    @FXML
    private Button BACKbutton;
    @FXML
    private ImageView LEVEL1,LEVEL2,LEVEL3,LEVEL4,LEVEL5,LEVEL6,LEVEL7,LEVEL8;
    @FXML
    private AnchorPane LEVELS_fenetre,SCENE;
    @FXML
    private Label TITRE2;
    @FXML
    private Pane Background1,Background2,Background3,Background4,Background5,Background6,Background7,Background8;

    private Stage stage;
    public void initialize() {

        LEVEL1.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/padlock.png"))));
        LEVEL2.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/padlock.png"))));
        LEVEL3.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/padlock.png"))));
        LEVEL4.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/padlock.png"))));
        LEVEL5.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/padlock.png"))));
        LEVEL6.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/padlock.png"))));
        LEVEL7.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/padlock.png"))));
        LEVEL8.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/padlock.png"))));


    }


    public void BackAction(ActionEvent event)
    {
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(Interface.sceneInterface);

        stage.setResizable(false);

        stage.show();
    }

    public void CHOSENlevel(MouseEvent mouseEvent) throws IOException {

        FXMLLoader GAME = new FXMLLoader(Interface.class.getResource("/sokoban/IG/resources/designe/GAME.fxml"));
        Scene sceneSTART=new Scene(GAME.load(),660, 660);
        stage = (Stage) ((Node)mouseEvent.getSource()).getScene().getWindow();


        stage.setTitle("LEVEL1");
        sceneSTART.getStylesheets().add(getClass().getResource("/sokoban/IG/resources/designe/GAME.css").toExternalForm());
        stage.setScene(sceneSTART);
        stage.setResizable(false);

        stage.show();
    }
}



