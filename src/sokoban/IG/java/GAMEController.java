package sokoban.IG.java;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import sokoban.app.Level;
import sokoban.core.Direction;
import sokoban.core.Grid;
import sokoban.core.Position;
import sokoban.core.World;
import sokoban.logic.Action;
import sokoban.logic.LogicKey;
import sokoban.saving.StateManager;

import java.io.IOException;
import java.util.List;
import java.util.Objects;


/// A revoir:
/// - ne pas piloter le jeu directement avec World + GameLogic
/// - passer par Level pour rester coherent avec l'architecture actuelle
/// - separer plus clairement l'affichage JavaFX et le deroulement du jeu
/// - reprendre une logique proche de TerminalUi pour les actions utilisateur
/// SAMY : je travaille actuellement sur le code de cette branche dans
/// une autre branche nommée interface-copy, veuillez ne pas y toucher svp.
public class GAMEController {
    int taille_case = 60;


    List<Direction> path;
    Stage stage;
    private double playerX;
    private double playerY;
    private final double smooth = 0.33;


    private World world;
    @FXML
    private Canvas canva;

    private Grid grid;

    private final Image player = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/essaypion.gif")));
    private final Image wall = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/stone.jpg")));
    private final Image target = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/target.png")));
    private final Image box = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/vrai_box.png")));
    private final Image floor = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/ground.png")));


    private GraphicsContext GD;

    AnimationTimer timer;
    StateManager stat = new StateManager();
    Level level;


    public void initialize() {


        RELOAD_BUTTON.setFocusTraversable(false);
        BACK_BUTTON.setFocusTraversable(false);
        SAVE_BUTTON.setFocusTraversable(false);
        PAUSE_BUTTON.setFocusTraversable(false);
        UNDO_BUTTON.setFocusTraversable(false);


        GD = canva.getGraphicsContext2D();


    }


    public void drawWorld() {
        GD.clearRect(0, 0, canva.getWidth(), canva.getHeight());


        // calculer la taille total de la grille
        double gridWidth = grid.getWidth() * taille_case;
        double gridHeight = grid.getLength() * taille_case;

        //pour centrer le dessin
        double startX = (canva.getWidth() - gridWidth) / 2;
        double startY = (canva.getHeight() - gridHeight) / 2;


        for (int i = 0; i < grid.getLength(); i++) {
            for (int j = 0; j < grid.getWidth(); j++) {
                char typecell = grid.getElement(i, j);
                int taille_case = 60;


                double x = startX + (j * taille_case);
                double y = startY + (i * taille_case);


                GD.drawImage(floor, x, y, taille_case, taille_case);

                if (typecell == '#') {
                    GD.drawImage(wall, x, y, taille_case, taille_case);

                } else if (typecell == 'O') {
                    GD.drawImage(box, x, y, taille_case, taille_case);
                } else if (typecell == 'x') {
                    GD.drawImage(target, x, y, taille_case, taille_case);
                }

            }


        }

        double playerPixelX = startX + playerX;
        double playerPixelY = startY + playerY;

        GD.drawImage(player, playerPixelX - 18, playerPixelY - 18, 95, 95);


    }


    //private LevelState levelState = level.getState();

    public void getUserAction(KeyEvent event) {


        LogicKey k = null;
        KeyCode code = event.getCode();


        if (code == KeyCode.W || code == KeyCode.UP) {
            k = LogicKey.MOVE_UP;
        } else if (code == KeyCode.S || code == KeyCode.DOWN) {
            k = LogicKey.MOVE_DOWN;
        } else if (code == KeyCode.A || code == KeyCode.LEFT) {
            k = LogicKey.MOVE_LEFT;
        } else if (code == KeyCode.D || code == KeyCode.RIGHT) {
            k = LogicKey.MOVE_RIGHT;
        } else if (code == KeyCode.Z || code == KeyCode.U) {
            k = LogicKey.UNDO;
        } else if (code == KeyCode.V) {
            k = LogicKey.SAVE;
        } else if (code == KeyCode.L) {
            k = LogicKey.LOAD;
        } else if (code == KeyCode.ESCAPE) {
            k = LogicKey.ESCAPE;
        } else if (code == KeyCode.R) {
            k = LogicKey.RELOAD;
        }

        executeUserAction(k);
    }

    public void executeUserAction(LogicKey k) {
        if (k == null) {
            return;
        }

        switch (k) {
            case MOVE_UP:
            case MOVE_DOWN:
            case MOVE_LEFT:
            case MOVE_RIGHT:


                if(level.simulateMove(k))
                {
                    level.saveState();
                    level.executeUserAction(k);


                }

                refreshView();
                break;

            case UNDO:
                level.undo();
                refreshView();
                break;

            case SAVE:
                save();
                refreshView();
                break;

            case LOAD:
                load();
                refreshView();
                break;

            case RELOAD:
                reload();
                refreshView();
                break;

            case ESCAPE:
                pause(); // ou retour menu
                break;

            default:
                break;
        }
    }

    private void refreshView() {
        world = level.getCurrentWorld();
        grid = world.getGrid();

        playerX = world.getPlayerPosition().getX() * 60;
        playerY = world.getPlayerPosition().getY() * 60;

        drawWorld();
        canva.requestFocus();
    }




    public void executePathFinding(MouseEvent event) {

        // calculer la taille total de la grille
        double gridWidth = grid.getWidth() * taille_case;
        double gridHeight = grid.getLength() * taille_case;

        //pour centrer le dessin
        double startX = (canva.getWidth() - gridWidth) / 2;
        double startY = (canva.getHeight() - gridHeight) / 2;


        //parceque j ai utiliser canvas qui a ces coordonnee comme des doubles
        //donc je dois divisier sur la taille d une seule case
        int colonne = (int) ((event.getX() - startX) / taille_case);
        int row = (int) ((event.getY() - startY) / taille_case);


        Position posTarget = new Position(row, colonne);


        path = level.executePathFinding(posTarget);
        if (path != null && !path.isEmpty()) {
            moveInPath(path);
        } else {
            throw new IllegalStateException("PATH NON TROUVÉ");
        }


    }

    public void moveInPath(List<Direction> path) {

        LogicKey k = null;


        Direction d = path.remove(0);
        level.executeMove(d);
        level.saveState();
        grid = world.getGrid();
        drawWorld();
        // pour le delais
        PauseTransition pause = new PauseTransition(Duration.millis(400));
        pause.setOnFinished(
                event -> {
                    // on rappelle la methode pour faire le pas suivant
                    moveInPath(path);
                }
        );

        pause.play();

    }

    @FXML
    public void next(ActionEvent event) throws IOException {


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sokoban/IG/resources/designe/START.fxml"));
        Parent root = loader.load();


        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();


        Scene newScene = new Scene(root, 660, 660);


        newScene.getStylesheets().add(getClass().getResource("/sokoban/IG/resources/designe/START.css").toExternalForm());

        stage.setScene(newScene);
        stage.show();


    }

    @FXML
    private Button BACK_BUTTON;
    @FXML
    private Button SAVE_BUTTON;
    @FXML
    private Button RELOAD_BUTTON;
    @FXML
    private Button PAUSE_BUTTON;
    @FXML
    private Button UNDO_BUTTON;

    @FXML
    private Label Winner_label;

    @FXML
    private ImageView Winner_image;
    @FXML
    private Text Winner_text;
    @FXML
    private Button Winner_next;


    @FXML
    public void win() {
        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/Winner_pion.gif")));
        Winner_image.setImage(img);
        Winner_image.setVisible(true);
        Winner_text.setVisible(true);
        Winner_label.setVisible(true);
        Winner_next.setVisible(true);


    }

    @FXML
    public void pause() {
        // pas encore fini


    }

    public void load(){

    }

   public void reload() {
       /* // pas encore fini


        if (timer != null) {
            timer.stop();
        }
        // stat.loadFresh(lvl);
        initialize(level.getNumLevel());
*/

    }

    @FXML
    public void undo() {


        level.undo();
        world = level.getCurrentWorld();
        grid = world.getGrid();
        canva.requestFocus();


    }

    public void save() {


        // pas encore fini
        stat.save(level);
    }

    public void back(ActionEvent event) throws IOException {

        FXMLLoader GAME = new FXMLLoader(getClass().getResource("/sokoban/IG/resources/designe/START.fxml"));
        Scene sceneSTART = new Scene(GAME.load(), 660, 660);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();


        stage.setTitle("LEVEL1");
        sceneSTART.getStylesheets().add(getClass().getResource("/sokoban/IG/resources/designe/START.css").toExternalForm());
        stage.setScene(sceneSTART);
        stage.setResizable(false);

        stage.show();

    }

    public void setLevel(Level level) {
        this.level = level;
        world = level.getCurrentWorld();
        grid = world.getGrid();
        this.playerX = world.getPlayerPosition().getX() * 60;
        this.playerY = world.getPlayerPosition().getY() * 60;
        drawWorld();
        startTimer();
    }


  public void startTimer(){
      timer = new AnimationTimer() {
          @Override
          public void handle(long now) {

              double targetX = world.getPlayerPosition().getX() * 60;
              double targetY = world.getPlayerPosition().getY() * 60;// On réduit l espace entre la postion actuelle et la prochaine
              playerX += (targetX - playerX) * smooth;
              playerY += (targetY - playerY) * smooth;


              // cette methode est appele automatiquement 60 fois par seconde pour redessine le level a chaque fois
              drawWorld(); //
          }
      };
              timer.start();

          // canva.setFocusTraversable(true);
          //canva.requestFocus();


          // lancer la boucle pour refraicher
  }







}


