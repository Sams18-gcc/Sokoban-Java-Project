package sokoban.IG.java;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.Event;
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
import sokoban.app.Level;
import sokoban.core.Direction;
import sokoban.core.Grid;
import sokoban.core.Position;
import sokoban.core.World;
import sokoban.entity.Player;
import sokoban.logic.GameLogic;
import sokoban.logic.LogicKey;
import sokoban.pathfinding.PathSeek;
import sokoban.saving.LoadGame;
import sokoban.pathfinding.PathSeek;
import sokoban.saving.StateManager;

import java.io.IOException;
import java.util.List;
import java.util.Objects;



/// A revoir:
/// - ne pas piloter le jeu directement avec World + GameLogic
/// - passer par Level pour rester coherent avec l'architecture actuelle
/// - separer plus clairement l'affichage JavaFX et le deroulement du jeu
/// - reprendre une logique proche de TerminalUi pour les actions utilisateur
public class GAMEController {
    int taille_case = 60;


    List<Direction> LEPATH;



    Stage stage;



    private double player_X;
    private double player_Y;


    private final double smooth = 0.33;




    private World world;
    @FXML
    private Canvas canva;

    private Grid mygrille;

    private final Image Pion = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/essaypion.gif")));
    private final Image Mur = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/stone.jpg")));
    private final Image Target = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/target.png")));

    private final Image Box = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/vrai_box.png")));

    private final Image Ground = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/ground.png")));


    private GraphicsContext GD;

    AnimationTimer timer;


    StateManager stat=new StateManager();

    Level lvl;


    public void initialize(int level_number) {


        lvl=new Level(level_number,stat);
        lvl.init();

           Boolean succeseed=lvl.getLoader().loadGrids(level_number);

        RELOAD_BUTTON.setFocusTraversable(false);
        BACK_BUTTON.setFocusTraversable(false);
        SAVE_BUTTON.setFocusTraversable(false);
        PAUSE_BUTTON.setFocusTraversable(false);
        UNDO_BUTTON.setFocusTraversable(false);


        if (succeseed) {



            world = lvl.getCurrentWorld();
             mygrille=world.getGrid();


        } else {
            System.out.println("levels " + level_number+" introuvable ");
            return;
        }



        GD = canva.getGraphicsContext2D();

        dessinerworld();




         timer = new AnimationTimer() {
            @Override
            public void handle(long now) {

                double targetX = world.getPlayerPosition().getX() * 60;
                double targetY = world.getPlayerPosition().getY() * 60;// On réduit l espace entre la postion actuelle et la prochaine
                player_X += (targetX - player_X) * smooth;
                player_Y += (targetY - player_Y) * smooth;


                // cette methode est appele automatiquement 60 fois par seconde pour redessine le level a chaque fois
                dessinerworld(); //
            }
        };
        timer.start();

       // canva.setFocusTraversable(true);
        //canva.requestFocus();




        // lancer la boucle pour refraicher

    }




    public void dessinerworld() {
        GD.clearRect(0, 0, canva.getWidth(), canva.getHeight());


        // calculer la taille total de la grille
        double gridWidth = mygrille.getWidth() * taille_case;
        double gridHeight = mygrille.getLength() * taille_case;

        //pour centrer le dessin
        double startX = (canva.getWidth() - gridWidth) / 2;
        double startY = (canva.getHeight() - gridHeight) / 2;



        for (int i = 0; i < mygrille.getLength(); i++) {
            for (int j = 0; j < mygrille.getWidth(); j++) {
                char typecell = mygrille.getElement(i, j);
                int taille_case = 60;


                double x = startX +(j* taille_case);
                double y = startY +(i* taille_case);




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

        double p_pixelX = startX + player_X;
        double p_pixelY = startY + player_Y ;

        GD.drawImage(Pion, p_pixelX - 18, p_pixelY - 18, 95, 95);


    }



    private boolean fin_partie=false;

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



        lvl.executeUserAction(k);
            lvl.saveMove();
        mygrille = world.getGrid();


        dessinerworld();
        if (world.allBoxesInTarget()) {
            Gagner();
            fin_partie = true;


        }
    }





    @FXML
  private Label Winner_label;

    @FXML
    private ImageView Winner_image;
    @FXML
    private Text Winner_text;
    @FXML
    private Button Winner_next;


    @FXML
    public void Gagner() {
        Image img =new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sokoban/IG/resources/photo/Winner_pion.gif")));
        Winner_image.setImage(img);
        Winner_image.setVisible(true);
        Winner_text.setVisible(true);
        Winner_label.setVisible(true);
        Winner_next.setVisible(true);


    }
    public void PATH(MouseEvent event)
    {

        // calculer la taille total de la grille
        double gridWidth = mygrille.getWidth() * taille_case;
        double gridHeight = mygrille.getLength() * taille_case;

        //pour centrer le dessin
        double startX = (canva.getWidth() - gridWidth) / 2;
        double startY = (canva.getHeight() - gridHeight) / 2;


            //parceque j ai utiliser canvas qui a ces coordonnee comme des doubles
            //donc je dois divisier sur la taille d une seule case
        int colonne = (int) ((event.getX()-startX)/ taille_case);
        int row = (int) ((event.getY() -startY)/ taille_case);


        Position pos_target=new Position(row,colonne);


       LEPATH =lvl.executePathFinding(pos_target);
        if (LEPATH != null && !LEPATH.isEmpty()) {
            MOVE_AVEC_PATH(LEPATH);
        } else {
            System.out.println("destination impossible");
        }





    }

    public void MOVE_AVEC_PATH(List<Direction> LEPATH)
    {
        if (fin_partie || LEPATH == null || LEPATH.isEmpty()) {
            return;
        }
        LogicKey k = null;


            Direction d = LEPATH.remove(0);
            switch (d) {
                case UP:
                    k = LogicKey.MOVE_UP;
                    break;
                case RIGHT:
                    k = LogicKey.MOVE_RIGHT;
                    break;
                case LEFT:
                    k = LogicKey.MOVE_LEFT;
                    break;
                case DOWN:
                    k = LogicKey.MOVE_DOWN;
                    break;

                default:
                    k=null;


            }

            if(k!=null) {
                lvl.executeUserAction(k);
                lvl.saveMove();
                mygrille = world.getGrid();
                dessinerworld();
                // je peux pas utilsier celle de Level pasceque t as parcourir toutes les worlds
                if (world.allBoxesInTarget()) {

                    Gagner();


                    LEPATH.clear();
                    fin_partie = true;



                }

                // pour le delais
                PauseTransition pause = new PauseTransition(Duration.millis(400));
                pause.setOnFinished(event -> {
                    // on rappelle la methode pour faire le pas suivant
                    MOVE_AVEC_PATH(LEPATH);
                });

                pause.play();
            }
        }

@FXML
    public void NEXT(ActionEvent event) throws IOException {


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
    public void PAUSE()
    {
        // pas encore fini


    }
    public void RELOAD()
    {
        // pas encore fini


        if (timer != null) {
            timer.stop();
        }
       // stat.loadFresh(lvl);
        initialize(lvl.getNumLevel());




    }
    @FXML
    public void UNDO()
    {



        lvl.undo();
        world=lvl.getCurrentWorld();
        mygrille=world.getGrid();




        canva.requestFocus();



    }

    public void SAVE()
    {


        // pas encore fini
    stat.save(lvl);
    }
    public void BACK(ActionEvent event) throws IOException {

        FXMLLoader GAME = new FXMLLoader(getClass().getResource("/sokoban/IG/resources/designe/START.fxml"));
        Scene sceneSTART=new Scene(GAME.load(),660, 660);
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();


        stage.setTitle("LEVEL1");
        sceneSTART.getStylesheets().add(getClass().getResource("/sokoban/IG/resources/designe/START.css").toExternalForm());
        stage.setScene(sceneSTART);
        stage.setResizable(false);

        stage.show();

    }








}


