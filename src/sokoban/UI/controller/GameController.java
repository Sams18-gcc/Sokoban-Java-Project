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
import javafx.scene.layout.AnchorPane;
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

public class GameController {

    List<Direction> path;
    Stage stage;
    private double playerX;
    private double playerY;
    private final double smooth = 0.33;
    private String levelsDirectoryName;
    private int nbLevels;

    private double visualX = 0;
    private double visualY = 0;

    private World world;

    @FXML
    private Canvas canva;

    private Grid grid;

    private final Image player = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/pion.png")));
    private final Image wall = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/wall.png")));
    private final Image target = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/target.png")));
    private final Image box = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/box.png")));
    private final Image floor = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/floor.png")));

    private GraphicsContext GD;

    AnimationTimer timer;
    StateManager stat = new StateManager();
    Level level;

    public void initialize() {
        RELOAD_BUTTON.setFocusTraversable(false);
        BACK_BUTTON.setFocusTraversable(false);
        SAVE_BUTTON.setFocusTraversable(false);
        LOAD_BUTTON.setFocusTraversable(false);
        UNDO_BUTTON.setFocusTraversable(false);

        GD = canva.getGraphicsContext2D();

        canva.widthProperty().bind(((AnchorPane) canva.getParent()).widthProperty().subtract(250));
        canva.heightProperty().bind(((AnchorPane) canva.getParent()).heightProperty().subtract(50));

        canva.widthProperty().addListener(evt -> drawWorld());
        canva.heightProperty().addListener(evt -> drawWorld());
    }

    private double getDynamicTileSize() {
        double sidePadding = 60;
        double availableWidth = Math.max(canva.getWidth() - sidePadding, 100);
        double availableHeight = Math.max(canva.getHeight() - sidePadding, 100);
        double sizeX = availableWidth / grid.getWidth();
        double sizeY = availableHeight / grid.getLength();
        return Math.min(sizeX, sizeY);
    }

    public void drawWorld() {
        if (grid == null) return;

        GD.clearRect(0, 0, canva.getWidth(), canva.getHeight());
        double dynamicTaille = getDynamicTileSize();

        double gridWidth = grid.getWidth() * dynamicTaille;
        double gridHeight = grid.getLength() * dynamicTaille;

        double startX = (canva.getWidth() - gridWidth) / 2;
        double startY = (canva.getHeight() - gridHeight) / 2;

        for (int i = 0; i < grid.getLength(); i++) {
            for (int j = 0; j < grid.getWidth(); j++) {
                char typecell = grid.getElement(i, j);
                double x = startX + (j * dynamicTaille);
                double y = startY + (i * dynamicTaille);

                GD.drawImage(floor, x, y, dynamicTaille, dynamicTaille);

                if (typecell == '#') GD.drawImage(wall, x, y, dynamicTaille, dynamicTaille);
                else if (typecell == 'O') GD.drawImage(box, x, y, dynamicTaille, dynamicTaille);
                else if (typecell == 'x') GD.drawImage(target, x, y, dynamicTaille, dynamicTaille);
            }
        }

        GD.drawImage(player, startX + visualX, startY + visualY, dynamicTaille, dynamicTaille);
    }

    public void handleUserAction(KeyEvent event) {
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
        } else if (code == KeyCode.H) {
            k = LogicKey.AUTO_SOLVE;
        }

        processUserAction(k);
    }

    public void processUserAction(LogicKey k) {
        if (k == null) return;

        ResultOfAction resultOfAction = level.handleUserAction(k);

        switch (resultOfAction) {
            case MOVED:
            case BOX_IN_TARGET:
            case BLOCKED:
            case LOADED:
            case RELOADED:
            case UNDONE:
                refreshView();
            case SAVED:
                break;

            case WON:
                refreshView();
                unlockNextLevel();
                victoryDisplay();
                break;

            case SOLVER_REQUESTED:
                executeAutoSolver();
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

    private void refreshView() {
        world = level.getCurrentWorld();
        grid = world.getGrid();

        double dynamicTaille = getDynamicTileSize();
        playerX = world.getPlayerPosition().getX() * dynamicTaille;
        playerY = world.getPlayerPosition().getY() * dynamicTaille;

        drawWorld();
        canva.requestFocus();
    }

    public void executePathFinding(MouseEvent event) {
        double dynamicTaille = getDynamicTileSize();

        double gridWidth = grid.getWidth() * dynamicTaille;
        double gridHeight = grid.getLength() * dynamicTaille;
        double startX = (canva.getWidth() - gridWidth) / 2;
        double startY = (canva.getHeight() - gridHeight) / 2;

        int colonne = (int) ((event.getX() - startX) / dynamicTaille);
        int row = (int) ((event.getY() - startY) / dynamicTaille);

        Position posTarget = new Position(row, colonne);
        path = level.executePathFinding(posTarget);

        if (path != null && !path.isEmpty()) {
            moveInPath(path);
        }
    }

    public void executeAutoSolver() {
        List<Direction> solution = level.executeAutoSolver();
        if (solution != null && !solution.isEmpty()) {
            moveInPath(solution);
        }
    }

    public void moveInPath(List<Direction> path) {
        Direction d = path.remove(0);

        LogicKey lk = level.directionToLogicKey(d);
        ResultOfAction result = level.handleUserAction(lk);

        refreshView();

        if (result == ResultOfAction.WON) {
            unlockNextLevel();
            victoryDisplay();
            return;
        }

        if (!path.isEmpty()) {
            PauseTransition pause = new PauseTransition(Duration.millis(400));
            pause.setOnFinished(event -> moveInPath(path));
            pause.play();
        }
    }

    @FXML
    public void next(ActionEvent event) throws IOException {
        if (timer != null) timer.stop();

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        if (level.getNumLevel() >= nbLevels) {
            fin();
            return;
        }

        StateManager sm = new StateManager();
        Level nextLevel = new Level(level.getNumLevel() + 1, levelsDirectoryName, sm);
        nextLevel.init();

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/sokoban/UI/resources/fxml/Game.fxml")
            );

            Parent root = loader.load();

            GameController controller = loader.getController();
            controller.setLevel(nextLevel);
            controller.setLevelsInfo(levelsDirectoryName, nbLevels);

            Scene scene = new Scene(root, 660, 660);
            scene.getStylesheets().add(
                    Objects.requireNonNull(
                            getClass().getResource("/sokoban/UI/resources/style/Game.css")
                    ).toExternalForm()
            );

            stage.setTitle("LEVEL " + nextLevel.getNumLevel());
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setFullScreen(true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fin() {
        try {
            if (timer != null) timer.stop();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sokoban/UI/resources/fxml/Start.fxml"));
            Parent root = loader.load();

            if (stage == null) {
                stage = (Stage) canva.getScene().getWindow();
            }

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(
                    Objects.requireNonNull(
                            getClass().getResource("/sokoban/UI/resources/style/Start.css")
                    ).toExternalForm()
            );
            stage.setScene(scene);
            stage.setTitle("Congratulations!");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Button BACK_BUTTON;
    @FXML
    private Button SAVE_BUTTON;
    @FXML
    private Button RELOAD_BUTTON;
    @FXML
    private Button LOAD_BUTTON;
    @FXML
    private Button UNDO_BUTTON;
    @FXML
    private Label Winner_label;
    @FXML
    private ImageView Winner_image;
    @FXML
    private Text text;
    @FXML
    private Button Winner_next;

    @FXML
    public void victoryDisplay() {
        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/UI/resources/assets/playerCelebrating.gif")));
        Winner_image.setImage(img);
        Winner_image.setVisible(true);
        text.setVisible(true);
        Winner_label.setVisible(true);
        Winner_next.setVisible(true);
    }

    public void back(ActionEvent event) throws IOException {
        if (timer != null) timer.stop();

        FXMLLoader loader = new FXMLLoader(
                SokobanApp.class.getResource("/sokoban/UI/resources/fxml/Mode.fxml")
        );
        Parent root = loader.load();

        Scene sceneMode = new Scene(root, 900, 900);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setTitle("GAME MODE");
        sceneMode.getStylesheets().add(
                getClass().getResource("/sokoban/UI/resources/style/Mode.css").toExternalForm()
        );
        stage.setScene(sceneMode);
        stage.show();
    }

    public void setLevel(Level level) {
        this.level = level;
        world = level.getCurrentWorld();
        grid = world.getGrid();

        double dynamicTaille = getDynamicTileSize();
        this.playerX = world.getPlayerPosition().getX() * dynamicTaille;
        this.playerY = world.getPlayerPosition().getY() * dynamicTaille;

        this.visualX = this.playerX;
        this.visualY = this.playerY;
        drawWorld();
        startTimer();
    }

    public void setLevelsInfo(String levelsDirectory, int nbLevels) {
        this.levelsDirectoryName = levelsDirectory;
        this.nbLevels = nbLevels;
    }

    public void unlockNextLevel() {
        int nextLevelNum = (level.getNumLevel() == nbLevels) ? -1 : level.getNumLevel() + 1;
        if (nextLevelNum == -1) {
            return;
        }
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

    public void startTimer() {
        if (timer != null) timer.stop();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double dynamicTaille = getDynamicTileSize();

                double targetX = world.getPlayerPosition().getX() * dynamicTaille;
                double targetY = world.getPlayerPosition().getY() * dynamicTaille;

                visualX += (targetX - visualX) * smooth;
                visualY += (targetY - visualY) * smooth;

                drawWorld();
            }
        };
        timer.start();
    }

    @FXML
    public void save() {
        stat.save(level);
    }

    @FXML
    public void reload() {
        level.reloadGame();

        world = level.getCurrentWorld();
        grid = world.getGrid();

        double dynamicTaille = getDynamicTileSize();
        visualX = world.getPlayerPosition().getX() * dynamicTaille;
        visualY = world.getPlayerPosition().getY() * dynamicTaille;

        refreshView();
        startTimer();
    }

    @FXML
    public void undo() {
        level.undo();
        double dynamicTaille = getDynamicTileSize();
        visualX = level.getCurrentWorld().getPlayerPosition().getX() * dynamicTaille;
        visualY = level.getCurrentWorld().getPlayerPosition().getY() * dynamicTaille;
        refreshView();
    }

    @FXML
    public void load() {
        level.loadGame();

        world = level.getCurrentWorld();
        grid = world.getGrid();

        double dynamicTaille = getDynamicTileSize();
        visualX = world.getPlayerPosition().getX() * dynamicTaille;
        visualY = world.getPlayerPosition().getY() * dynamicTaille;

        refreshView();
        startTimer();
    }
}