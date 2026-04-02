package sokoban.UI.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.media.AudioClip;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SokobanApp extends Application {
    public static Scene sceneInterface;
    public static MediaPlayer mediaPlayer;
    public static AudioClip menuSELECTION;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader interface1 = new FXMLLoader(SokobanApp.class.getResource("/sokoban/UI/resources/fxml/Interface.fxml"));
         sceneInterface = new Scene(interface1.load(), 660, 660);

        stage.setTitle("Interface");
        stage.setScene(sceneInterface);
        sceneInterface.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/sokoban/UI/resources/style/InterfaceStyle.css")).toExternalForm());

        stage.setResizable(false);

        stage.show();

        stage.setOnCloseRequest(e->
        {
            e.consume();
            EXIT(stage);
        });
    }

    public void EXIT(Stage stage) {
        Alert alerte = new Alert(Alert.AlertType.CONFIRMATION);
        alerte.setContentText("VOUS VOULEZ QUITTER LE JEU ?");
        alerte.setHeaderText("SORTIR DE JEU");
        alerte.setTitle("QUITTER");
        if (alerte.showAndWait().get() == ButtonType.OK) {

            stage.close();
        }
    }
}
