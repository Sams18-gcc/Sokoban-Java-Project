package sokoban.UI.controller;

import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import sokoban.UI.view.GameRenderer;
import sokoban.UI.view.SceneNavigator;
import sokoban.app.Level;
import sokoban.core.Direction;
import sokoban.core.Grid;
import sokoban.core.Position;
import sokoban.logic.LogicKey;
import sokoban.logic.ResultOfAction;
import sokoban.saving.StateManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;


 //  recevoir les evenements utilisateur clavier boutons
 // les traduire en LogicKey les deleguer a Level puis mettre a jour la vue.


 // il delegue a GameRenderer et SceneNavigator.

public class GameController {


    @FXML private Canvas canva;
    @FXML private Button BACK_BUTTON;
    @FXML private Button SAVE_BUTTON;
    @FXML private Button RELOAD_BUTTON;
    @FXML private Button LOAD_BUTTON;
    @FXML private Button UNDO_BUTTON;
    @FXML private Button AUTO_BUTTON;
    @FXML private Label     Winner_label;
    @FXML private ImageView Winner_image;
    @FXML private Text      text;
    @FXML private Button    Winner_next;


    private Level          level;
    private GameRenderer   renderer;
    private SceneNavigator navigator;

    private String levelsDirectoryName;
    private int    nbLevels;


    public void initialize() {

        RELOAD_BUTTON.setFocusTraversable(false);
        BACK_BUTTON  .setFocusTraversable(false);
        SAVE_BUTTON  .setFocusTraversable(false);
        LOAD_BUTTON  .setFocusTraversable(false);
        UNDO_BUTTON  .setFocusTraversable(false);
        AUTO_BUTTON  .setFocusTraversable(false);

        // Le canvas occupe tout lespace disponible moins le panneau latera
        canva.widthProperty() .bind(((AnchorPane) canva.getParent()).widthProperty() .subtract(250));
        canva.heightProperty().bind(((AnchorPane) canva.getParent()).heightProperty().subtract(50));

        renderer = new GameRenderer(canva);

        // redessiner si la fenetre est redimensionnee
        canva.widthProperty() .addListener(evt -> redraw());
        canva.heightProperty().addListener(evt -> redraw());
    }



    public void setLevel(Level level) {
        this.level = level;

        Grid grid = level.getCurrentWorld().getGrid();
        Position playerPos = level.getCurrentWorld().getPlayerPosition();

        // Attendre que le canvas ait ses vraies dimensions
        canva.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> obs, Number oldVal, Number newVal) {
                if (newVal.doubleValue() > 0) {
                    canva.widthProperty().removeListener(this);
                    renderer.snapToPosition(grid, playerPos);
                    renderer.draw(grid, playerPos);
                    renderer.startSmoothMove(grid, playerPos);
                }
            }
        });

        canva.requestFocus();
    }

    public void setLevelsInfo(String levelsDirectory, int nbLevels) {
        this.levelsDirectoryName = levelsDirectory;
        this.nbLevels            = nbLevels;
    }



    @FXML
    public void handleUserAction(KeyEvent event) {
        LogicKey k = keyCodeToLogicKey(event.getCode());
        processAction(k);
    }

    private LogicKey keyCodeToLogicKey(KeyCode code) {
        switch (code) {
            case W: case UP:    return LogicKey.MOVE_UP;
            case S: case DOWN:  return LogicKey.MOVE_DOWN;
            case A: case LEFT:  return LogicKey.MOVE_LEFT;
            case D: case RIGHT: return LogicKey.MOVE_RIGHT;
            case Z: case U:     return LogicKey.UNDO;
            case V:             return LogicKey.SAVE;
            case L:             return LogicKey.LOAD;
            case R:             return LogicKey.RELOAD;
            case H:             return LogicKey.AUTO_SOLVE;
            default:            return null;
        }
    }



    private void processAction(LogicKey k) {
        if (k == null) return;

        ResultOfAction result = level.handleUserAction(k);

        switch (result) {
            case MOVED:
            case BOX_IN_TARGET:
            case BLOCKED:
            case LOADED:
            case RELOADED:
            case UNDONE:
                refreshView();
                break;

            case WON:
                refreshView();
                unlockNextLevel();
                showVictoryScreen();
                if (level.getNumLevel() >= nbLevels) {
                    PauseTransition pause = new PauseTransition(Duration.seconds(2));
                    pause.setOnFinished(e -> getNavigator().goToEndScreen());
                    pause.play();
                }
                break;

            case SOLVER_REQUESTED:
                runAutoSolver();
                break;

            case SAVED:
            case PAUSED:
            case NOTHING:
            default:
                break;
        }
    }


    @FXML
    public void executePathFinding(MouseEvent event) {
        Grid   grid     = level.getCurrentWorld().getGrid();
        double tileSize = renderer.getTileSize(grid);

        double gridWidth  = grid.getWidth()  * tileSize;
        double gridHeight = grid.getLength() * tileSize;
        double startX     = (canva.getWidth()  - gridWidth)  / 2;
        double startY     = (canva.getHeight() - gridHeight) / 2;

        int col = (int) ((event.getX() - startX) / tileSize);
        int row = (int) ((event.getY() - startY) / tileSize);

        List<Direction> path = level.executePathFinding(new Position(row, col));

        if (path != null && !path.isEmpty()) {
            followPath(path);
        }
    }

    private void followPath(List<Direction> path) {
        Direction d      = path.remove(0);
        LogicKey  lk     = level.directionToLogicKey(d);
        ResultOfAction result = level.handleUserAction(lk);
        refreshView();

        if (result == ResultOfAction.WON) {
            unlockNextLevel();
            showVictoryScreen();
            if (level.getNumLevel() >= nbLevels) {
                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(e -> getNavigator().goToEndScreen());
                pause.play();
            }
            return;
        }

        if (!path.isEmpty()) {
            PauseTransition pause = new PauseTransition(Duration.millis(400));
            pause.setOnFinished(e -> followPath(path));
            pause.play();
        }
    }


    private void runAutoSolver() {
        List<Direction> solution = level.executeAutoSolver();
        if (solution != null && !solution.isEmpty()) {
            followPath(solution);
        }
    }


    @FXML
    public void save() {
        level.saveGame();
    }

    @FXML
    public void reload() {
        level.reloadGame();
        snapAndRefresh();
    }

    @FXML
    public void undo() {
        level.undo();
        snapAndRefresh();
    }

    @FXML
    public void load() {
        level.loadGame();
        snapAndRefresh();
    }

    @FXML
    public void auto() {
        runAutoSolver();
    }

    @FXML
    public void next(ActionEvent event) {
        renderer.stopTimer();

        if (level.getNumLevel() >= nbLevels) {
            getNavigator().goToEndScreen();
            return;
        }

        StateManager sm        = new StateManager();
        Level        nextLevel = new Level(level.getNumLevel() + 1, levelsDirectoryName, sm);
        nextLevel.init();

        getNavigator().goToLevel(nextLevel, levelsDirectoryName, nbLevels);
    }

    @FXML
    public void back(ActionEvent event) {
        renderer.stopTimer();
        getNavigator().goToModeMenu();
    }



    private void showVictoryScreen() {
        Winner_next.setVisible(level.getNumLevel() < nbLevels);

        Image gif = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream(
                        "/sokoban/UI/resources/assets/playerCelebrating.gif")));
        Winner_image.setImage(gif);
        Winner_image.setVisible(true);
        text        .setVisible(true);
        Winner_label.setVisible(true);
    }


    // Deblocage du niveau suivant



    private void unlockNextLevel() {
        int next = (level.getNumLevel() == nbLevels) ? -1 : level.getNumLevel() + 1;
        if (next == -1) return;

        File dir       = new File(levelsDirectoryName, "level" + next);
        File stateFile = new File(dir, "state.txt");

        if (!dir.isDirectory() || !stateFile.isFile())
            throw new IllegalStateException("Dossier ou fichier state.txt introuvable pour level " + next);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(stateFile))) {
            bw.write("unlocked");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //Redessine sans changer la position visuelle comme redementioner
    private void redraw() {
        if (level == null) return;
        renderer.draw(level.getCurrentWorld().getGrid(),
                level.getCurrentWorld().getPlayerPosition());
    }

    // Rafraichit la vue apres un mouvement
    private void refreshView() {
        Grid     grid      = level.getCurrentWorld().getGrid();
        Position playerPos = level.getCurrentWorld().getPlayerPosition();
        renderer.startSmoothMove(grid, playerPos);
        canva.requestFocus();
    }

    //Snap et refresh pour les actions qui teleportent lepion undo relaod et load
    private void snapAndRefresh() {
        Grid     grid      = level.getCurrentWorld().getGrid();
        Position playerPos = level.getCurrentWorld().getPlayerPosition();
        renderer.snapToPosition(grid, playerPos);
        renderer.startSmoothMove(grid, playerPos);
        canva.requestFocus();
    }

    // Construit SceneNavigator
    private SceneNavigator getNavigator() {
        if (navigator == null) {
            Stage stage = (Stage) canva.getScene().getWindow();
            navigator = new SceneNavigator(stage);
        }
        return navigator;
    }
}