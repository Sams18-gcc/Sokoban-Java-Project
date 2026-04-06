package sokoban.UI.controller;

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
import sokoban.UI.app.SokobanApp;
import sokoban.app.Level;
import sokoban.core.Direction;
import sokoban.core.Grid;
import sokoban.core.Position;
import sokoban.core.World;
import sokoban.logic.LogicKey;
import sokoban.logic.ResultOfAction;
import sokoban.saving.StateManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

// contrôleur de la scène de jeu JavaFX.
// Gère le rendu Canvas, la saisie clavier, le pathfinding par clic et les boutons.
// Note : passe par Level.handleUserActionGUI() pour rester cohérent avec l'architecture.
public class GameController {

    /*--------------------------------------------------
                        ATTRIBUTS
    --------------------------------------------------*/
    int    taille_case = 60;
    List<Direction> path;
    Stage  stage;

    private double playerX;
    private double playerY;
    private final double smooth = 0.33;     // facteur d'interpolation pour l'animation

    private String levelsDirectoryName;
    private int    nbLevels;

    private World world;
    private Grid  grid;

    // images du jeu
    private final Image player  = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/player.gif")));
    private final Image wall    = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/wall.png")));
    private final Image target  = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/target.png")));
    private final Image box     = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/box.png")));
    private final Image floor   = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/floor.png")));

    private GraphicsContext GD;
    AnimationTimer timer;

    StateManager stat  = new StateManager();
    Level        level;

    @FXML private Canvas  canva;
    @FXML private Button  BACK_BUTTON;
    @FXML private Button  SAVE_BUTTON;
    @FXML private Button  RELOAD_BUTTON;
    @FXML private Button  PAUSE_BUTTON;
    @FXML private Button  UNDO_BUTTON;
    @FXML private Label   Winner_label;
    @FXML private ImageView Winner_image;
    @FXML private Text    Winner_text;
    @FXML private Button  Winner_next;

    /*--------------------------------------------------
                        INIT JAVAFX
    --------------------------------------------------*/

    // appelé automatiquement par JavaFX après le chargement du FXML
    public void initialize() {
        RELOAD_BUTTON.setFocusTraversable(false);
        BACK_BUTTON.setFocusTraversable(false);
        SAVE_BUTTON.setFocusTraversable(false);
        PAUSE_BUTTON.setFocusTraversable(false);
        UNDO_BUTTON.setFocusTraversable(false);
        GD = canva.getGraphicsContext2D();
    }

    /*--------------------------------------------------
                        METHODES — RENDU
    --------------------------------------------------*/

    // dessine le monde courant sur le Canvas en centrant la grille
    public void drawWorld() {
        GD.clearRect(0, 0, canva.getWidth(), canva.getHeight());

        double gridWidth  = grid.getWidth()  * taille_case;
        double gridHeight = grid.getLength() * taille_case;
        double startX     = (canva.getWidth()  - gridWidth)  / 2;
        double startY     = (canva.getHeight() - gridHeight) / 2;

        for (int i = 0; i < grid.getLength(); i++) {
            for (int j = 0; j < grid.getWidth(); j++) {
                char   typecell = grid.getElement(i, j);
                double x        = startX + (j * taille_case);
                double y        = startY + (i * taille_case);

                GD.drawImage(floor, x, y, taille_case, taille_case);

                if      (typecell == '#')                    GD.drawImage(wall,   x, y, taille_case, taille_case);
                else if (typecell == 'O' || typecell == 'P') GD.drawImage(box,    x, y, taille_case, taille_case);
                else if (typecell == 'x')                    GD.drawImage(target, x, y, taille_case, taille_case);
            }
        }

        GD.drawImage(player,
                startX + playerX - 18,
                startY + playerY - 18,
                95, 95);
    }

    /*--------------------------------------------------
                        METHODES — SAISIE
    --------------------------------------------------*/

    // traduit un KeyEvent en LogicKey et délègue à processUserAction
    public void handleUserAction(KeyEvent event) {
        LogicKey k    = null;
        KeyCode  code = event.getCode();

        if      (code == KeyCode.W || code == KeyCode.UP)    k = LogicKey.MOVE_UP;
        else if (code == KeyCode.S || code == KeyCode.DOWN)  k = LogicKey.MOVE_DOWN;
        else if (code == KeyCode.A || code == KeyCode.LEFT)  k = LogicKey.MOVE_LEFT;
        else if (code == KeyCode.D || code == KeyCode.RIGHT) k = LogicKey.MOVE_RIGHT;
        else if (code == KeyCode.Z || code == KeyCode.U)     k = LogicKey.UNDO;
        else if (code == KeyCode.V)                          k = LogicKey.SAVE;
        else if (code == KeyCode.L)                          k = LogicKey.LOAD;
        else if (code == KeyCode.ESCAPE)                     k = LogicKey.ESCAPE;
        else if (code == KeyCode.R)                          k = LogicKey.RELOAD;

        processUserAction(k);
    }

    // exécute l'action et met à jour la vue en fonction du ResultOfAction
    public void processUserAction(LogicKey k) {
        if (k == null) return;

        ResultOfAction resultOfAction = level.handleUserActionGUI(k);

        switch (resultOfAction) {
            case MOVED:
            case BOX_IN_TARGET:
            case BLOCKED:
            case LOADED:
            case RELOADED:
            case UNDONE:
                refreshView();
                break;
            case SAVED:
                break;
            case WON:
                refreshView();
                unlockNextLevel();
                victoryDisplay();
                break;
            case PATH_FINDING_REQUESTED:
                break;
            case PAUSED:
                break;
            case NOTHING:
            default:
                break;
        }
    }

    // recharge world et grid depuis le level et redessine
    private void refreshView() {
        world   = level.getCurrentWorld();
        grid    = world.getGrid();
        playerX = world.getPlayerPosition().getX() * 60;
        playerY = world.getPlayerPosition().getY() * 60;
        drawWorld();
        canva.requestFocus();
    }

    /*--------------------------------------------------
                        METHODES — PATHFINDING
    --------------------------------------------------*/

    // calcule la position cliquée dans la grille et lance le pathfinding
    public void executePathFinding(MouseEvent event) {
        double gridWidth  = grid.getWidth()  * taille_case;
        double gridHeight = grid.getLength() * taille_case;
        double startX     = (canva.getWidth()  - gridWidth)  / 2;
        double startY     = (canva.getHeight() - gridHeight) / 2;

        int colonne = (int) ((event.getX() - startX) / taille_case);
        int row     = (int) ((event.getY() - startY) / taille_case);

        path = level.executePathFinding(new Position(row, colonne));
        if (path != null && !path.isEmpty())
            moveInPath(path);
        else
            throw new IllegalStateException("PATH NON TROUVÉ");
    }

    // exécute le déplacement pas à pas avec une pause de 400ms entre chaque étape
    public void moveInPath(List<Direction> path) {
        Direction d  = path.remove(0);
        LogicKey  lk = level.directionToLogicKey(d);
        level.handleUserActionGUI(lk);
        refreshView();

        if (!path.isEmpty()) {
            PauseTransition pause = new PauseTransition(Duration.millis(400));
            pause.setOnFinished(event -> moveInPath(path));
            pause.play();
        }
    }

    /*--------------------------------------------------
                        METHODES — NAVIGATION SCENES
    --------------------------------------------------*/

    // charge le niveau suivant
    @FXML
    public void next(ActionEvent event) throws IOException {
        StateManager sm        = new StateManager();
        Level        nextLevel = new Level(level.getNumLevel() + 1, levelsDirectoryName, sm);
        nextLevel.init();

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/sokoban/UI/resources/fxml/Game.fxml"));
            Parent root = loader.load();

            GameController controller = loader.getController();
            controller.setLevel(level);
            controller.setLevelsInfo(levelsDirectoryName, nbLevels);

            Scene scene = new Scene(root, 660, 660);
            scene.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("/sokoban/UI/resources/style/Game.css")
            ).toExternalForm());

            stage.setTitle("LEVEL " + nextLevel.getNumLevel());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // retourne à l'écran de sélection des niveaux
    public void back(ActionEvent event) throws IOException {
        FXMLLoader  loader     = new FXMLLoader(SokobanApp.class.getResource("/sokoban/UI/resources/fxml/Start.fxml"));
        Parent      root       = loader.load();
        StartController controller = loader.getController();
        controller.setLevelDirectoryName(levelsDirectoryName);
        controller.constructLevels();

        Scene sceneST = new Scene(root, 660, 660);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("RULES");
        sceneST.getStylesheets().add(getClass().getResource("/sokoban/UI/resources/style/Start.css").toExternalForm());
        stage.setScene(sceneST);
        stage.setResizable(false);
        stage.show();
    }

    /*--------------------------------------------------
                        METHODES — VICTOIRE
    --------------------------------------------------*/

    // affiche les éléments de l'écran de victoire
    @FXML
    public void victoryDisplay() {
        Image img = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/sokoban/UI/resources/assets/playerCelebrating.gif")));
        Winner_image.setImage(img);
        Winner_image.setVisible(true);
        Winner_text.setVisible(true);
        Winner_label.setVisible(true);
        Winner_next.setVisible(true);
    }

    // déverrouille le niveau suivant en écrivant "unlocked" dans son state.txt
    public void unlockNextLevel() {
        int nextLevelNum = (level.getNumLevel() == nbLevels) ? -1 : level.getNumLevel() + 1;
        if (nextLevelNum == -1) return;

        File nextLevelDirectory = new File(levelsDirectoryName, "level" + nextLevelNum);
        if (!nextLevelDirectory.exists() || !nextLevelDirectory.isDirectory())
            throw new IllegalStateException();

        File stateFile = new File(nextLevelDirectory, "state.txt");
        if (!stateFile.exists() || !stateFile.isFile())
            throw new IllegalStateException();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(stateFile))) {
            writer.write("unlocked");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*--------------------------------------------------
                        METHODES — ANIMATION
    --------------------------------------------------*/

    // démarre le timer d'animation qui redessine 60 fois par seconde
    // avec interpolation de la position du joueur
    public void startTimer() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double targetX = world.getPlayerPosition().getX() * 60;
                double targetY = world.getPlayerPosition().getY() * 60;
                playerX += (targetX - playerX) * smooth;
                playerY += (targetY - playerY) * smooth;
                drawWorld();
            }
        };
        timer.start();
    }

    /*--------------------------------------------------
                        METHODES — CONFIGURATION
    --------------------------------------------------*/

    // initialise le contrôleur avec un level déjà chargé
    public void setLevel(Level level) {
        this.level  = level;
        world       = level.getCurrentWorld();
        grid        = world.getGrid();
        this.playerX = world.getPlayerPosition().getX() * 60;
        this.playerY = world.getPlayerPosition().getY() * 60;
        drawWorld();
        startTimer();
    }

    // transmet les infos du répertoire de niveaux et le nombre total de niveaux
    public void setLevelsInfo(String levelsDirectory, int nbLevels) {
        this.levelsDirectoryName = levelsDirectory;
        this.nbLevels            = nbLevels;
    }

    /*--------------------------------------------------
                        BOUTONS FXML (non implémentés)
    --------------------------------------------------*/
    @FXML public void save()   {}
    @FXML public void reload() {}
    @FXML public void undo()   {}
    @FXML public void load()   {}
    @FXML public void pause()  {}
}
