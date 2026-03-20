/*package sokoban.IG.java;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import sokoban.core.Direction;
import sokoban.core.Grid;
import sokoban.core.World;
import sokoban.entity.Player;
import sokoban.logic.GameLogic;
import sokoban.logic.LogicKey;

import java.util.Objects;


public class GAMEController {


    private double player_X;
    private double player_Y;


    private final double smooth = 0.25;


    private GameLogic logic;

    private World world;
    @FXML
    private AnchorPane SCENE;
    @FXML
    private Canvas canva;

    private Grid mygrille;

    private final Image Pion = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/essaypion.gif")));
    private final Image Mur = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/mur.png")));
    private final Image Target = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/target.png")));

    private final Image Box = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/vrai_box.png")));

    private final Image Ground = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/ground.png")));


    private GraphicsContext GD;


    private Player pion;





    public void initialize() {


        world = new World(10, 10, 1);
        world.loadWorld();

        mygrille = world.getGrid();


        GD = canva.getGraphicsContext2D();

        dessinerworld();


        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // On place le visuel immédiatement sur la case de départ du joueur
                double targetX = world.getPlayerPosition().getX() * 60;
                double targetY = world.getPlayerPosition().getY() * 60;// On réduit l espace entre la postion actuelle et la prochaine
                player_X += (targetX - player_X) * smooth;
                player_Y += (targetY - player_Y) * smooth;


                // cette methode est appele automatiquement 60 fois par seconde pour redessine le level a chaque fois
                dessinerworld(); //
            }
        };
        timer.start();









        // Lancer la boucle pour refraicher

    }




    public void dessinerworld() {
        GD.clearRect(0, 0, canva.getWidth(), canva.getHeight());
        for (int i = 0; i < mygrille.getLength(); i++) {
            for (int j = 0; j < mygrille.getWidth(); j++) {
                char typecell = mygrille.getElement(i, j);
                int taille_case = 60;
                double x = j * taille_case;
                double y = i * taille_case;


                GD.drawImage(Ground, x, y, taille_case, taille_case);

                if (typecell == '#') {
                    GD.drawImage(Mur, x, y, taille_case, taille_case);

                } else if (typecell == 'O') {
                    GD.drawImage(Box, x, y, taille_case, taille_case);
                } else if (typecell == 'x') {
                    GD.drawImage(Target, x, y, taille_case, taille_case);
                }

            }


        }
        GD.drawImage(Pion, player_X - 18, player_Y - 18, 95, 95);


    }



    private boolean fin_partie=false;
    @FXML
    public void MOVE(KeyEvent event) {

       if(fin_partie)
        {
            return;
        }


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
        }

        if (k != null) {
            //logic.movePlayer(d, world);

            GameLogic.logic.executeUserAction(k,world);
            mygrille = world.getGrid();


            dessinerworld();
            if (world.allBoxesInTarget()) {

                Gagner();

                System.out.println("gagner");
                fin_partie=true;



            }


        }

    }





    @FXML
  private Label Winner_label;

    @FXML
    private ImageView Winner_image;
    @FXML
    private Text Winner_text;


    @FXML
    public void Gagner() {
        Image img =new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/Winner_pion.gif")));
        Winner_image.setImage(img);
        Winner_image.setVisible(true);
        Winner_text.setVisible(true);
        Winner_label.setVisible(true);


    }
}*/
