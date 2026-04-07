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

// application JavaFX principale.
// Charge la scène Interface au démarrage et gère la confirmation de fermeture.
public class SokobanApp extends Application {

    /*--------------------------------------------------
                        ATTRIBUTS STATIQUES
    --------------------------------------------------*/
    public static Scene       sceneInterface;   // scène du menu principal (partagée entre contrôleurs)
    public static MediaPlayer mediaPlayer;      // lecteur de la musique de fond
    public static AudioClip   menuSELECTION;   // effet sonore de sélection dans les menus

    /*--------------------------------------------------
                        METHODES
    --------------------------------------------------*/

    // initialise la scène principale et affiche la fenêtre
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader interface1 = new FXMLLoader(
                SokobanApp.class.getResource("/sokoban/UI/resources/fxml/Interface.fxml"));
        sceneInterface = new Scene(interface1.load(), 660, 660);

        stage.setTitle("Interface");
        stage.setScene(sceneInterface);
        sceneInterface.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("/sokoban/UI/resources/style/InterfaceStyle.css")
        ).toExternalForm());

        stage.setResizable(false);
        stage.show();

        // confirmation avant fermeture
        stage.setOnCloseRequest(e -> { e.consume(); EXIT(stage); });
    }

    // affiche une boîte de dialogue de confirmation avant de fermer l'application
    public void EXIT(Stage stage) {
        Alert alerte = new Alert(Alert.AlertType.CONFIRMATION);
        alerte.setContentText("VOUS VOULEZ QUITTER LE JEU ?");
        alerte.setHeaderText("SORTIR DE JEU");
        alerte.setTitle("QUITTER");
        if (alerte.showAndWait().get() == ButtonType.OK)
            stage.close();
    }
}
