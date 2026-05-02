package sokoban.UI.view;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import sokoban.core.Grid;
import sokoban.core.Position;

import java.util.Objects;


public class GameRenderer {

    private static final double SIDE_PADDING = 60.0;
    private static final double SMOOTH_FACTOR = 0.33;

    private final Canvas canvas;
    private final GraphicsContext gc;

    private final Image imgPlayer = load("/sokoban/UI/resources/assets/pion.png");
    private final Image imgWall   = load("/sokoban/UI/resources/assets/wall.png");
    private final Image imgTarget = load("/sokoban/UI/resources/assets/target.png");
    private final Image imgBox    = load("/sokoban/UI/resources/assets/box.png");
    private final Image imgFloor  = load("/sokoban/UI/resources/assets/floor.png");
    private final Image imgPortal  = load("/sokoban/UI/resources/assets/portal.png");

    // position visuelle lissee du joueur en pixels
    private double visualX = 0;
    private double visualY = 0;

    private AnimationTimer timer;

    public GameRenderer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
    }



    public void draw(Grid grid, Position playerPos) {
        if (grid == null) return;

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double tileSize = computeTileSize(grid);
        double startX   = (canvas.getWidth()  - grid.getWidth()  * tileSize) / 2;
        double startY   = (canvas.getHeight() - grid.getLength() * tileSize) / 2;

        drawTiles(grid, tileSize, startX, startY);

        gc.drawImage(imgPlayer,
                startX + visualX,
                startY + visualY,
                tileSize, tileSize);
    }


     // Demarre l animation de lissage vers la nouvelle position du joueur.
     // Appeler apres chaque action qui deplace le joueur.


    public void startSmoothMove(Grid grid, Position playerPos) {
        stopTimer();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double tileSize = computeTileSize(grid);
                double targetX = playerPos.getX() * tileSize;
                double targetY = playerPos.getY() * tileSize;

                visualX += (targetX - visualX) * SMOOTH_FACTOR;
                visualY += (targetY - visualY) * SMOOTH_FACTOR;

                draw(grid, playerPos);
            }
        };
        timer.start();
    }


    public void snapToPosition(Grid grid, Position playerPos) {
        double tileSize = computeTileSize(grid);
        visualX = playerPos.getX() * tileSize;
        visualY = playerPos.getY() * tileSize;
    }

    public void stopTimer() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }



    private void drawTiles(Grid grid, double tileSize, double startX, double startY) {
        for (int row = 0; row < grid.getLength(); row++) {
            for (int col = 0; col < grid.getWidth(); col++) {
                char cell = grid.getElement(row, col);
                double x  = startX + col * tileSize;
                double y  = startY + row * tileSize;

                gc.drawImage(imgFloor, x, y, tileSize, tileSize);

                if      (cell == '#') gc.drawImage(imgWall,   x, y, tileSize, tileSize);
                else if (cell == 'O') gc.drawImage(imgBox,    x, y, tileSize, tileSize);
                else if (cell == 'x') gc.drawImage(imgTarget, x, y, tileSize, tileSize);
                else if (cell == 'P') gc.drawImage(imgPortal, x, y, tileSize, tileSize);

            }
        }
    }

    private double computeTileSize(Grid grid) {
        double availW = Math.max(canvas.getWidth()  - SIDE_PADDING, 100);
        double availH = Math.max(canvas.getHeight() - SIDE_PADDING, 100);
        return Math.min(availW / grid.getWidth(), availH / grid.getLength());
    }

    private Image load(String path) {
        return new Image(Objects.requireNonNull(
                getClass().getResourceAsStream(path)));
    }

    // expose tileSize pour que le controller puisse convertir un clic souris vers Position
    public double getTileSize(Grid grid) {
        return computeTileSize(grid);
    }
}