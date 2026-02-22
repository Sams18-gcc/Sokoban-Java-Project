package com.example.sokoban;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import sokoban.app.Level;
import sokoban.core.Position;
import sokoban.core.World;
import sokoban.entity.Box;
import sokoban.logic.GameLogic;
import sokoban.logic.GameLogic;
import sokoban.core.Direction;
import sokoban.core.Grid;
import sokoban.entity.Player;

import javafx.scene.input.KeyEvent;





public class GAMEController {

    private char[][] map = {
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '@', ' ', ' ', ' ', '.', ' ', ' ', ' ', '#'},
            {'#', ' ', '#', '#', ' ', ' ', ' ', ' ', ' ', '#'},
            {'#', ' ', 'O', ' ', ' ', ' ', ' ', ' ', ' ', '#'},
            {'#', ' ', ' ', ' ', ' ', '.', ' ', ' ', ' ', '#'},
            {'#', ' ', ' ', ' ', 'O', ' ', ' ', ' ', ' ', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#'}
    };
    private GameLogic logic;

    private World world;
    @FXML
    private GridPane Plateau;
    @FXML
    private AnchorPane SCENE;
    @FXML
    private GridPane GRILLE;

    public void initialize() {
            world=new World(10,10,1);
            world.loadWorld();

            logic=new GameLogic();
            dessinerworld();
    }
    public void dessinerworld()
    {

        for(int i=0;i<map.length;i++)
        {
            for (int j=0;j<map[i].length;j++)
            {
                  char typecell =map[i][j];

                  Image img=getImage(typecell);
                ImageView view = new ImageView(img);
                view.setFitWidth(55);  // Taille des cases
                view.setFitHeight(55);

                GRILLE.add(view, i, j); // Ajout dynamique


            }


        }

    }


    private Image getImage(char symbol) {
        String path = switch (symbol) {
            case '#' -> "/mur.png";
            case '@' -> "/pion.png";
            case 'O' -> "/target.png";
            case '.' -> "/vrai_box.png";
            default  -> "/ground.png";
        };
        return new Image(getClass().getResourceAsStream(path));
    }




    @FXML
    public void MOVE(KeyEvent event) {
        Direction d = null;
        KeyCode code = event.getCode(); // On utilise getCode() et non getKeyCode()

        // On utilise KeyCode.NOM_DE_LA_TOUCHE
        if (code == KeyCode.Z || code == KeyCode.UP) {
            d = Direction.UP;
        } else if (code == KeyCode.S || code == KeyCode.DOWN) {
            d = Direction.DOWN;
        } else if (code == KeyCode.Q || code == KeyCode.LEFT) {
            d = Direction.LEFT;
        } else if (code == KeyCode.D || code == KeyCode.RIGHT) {
            d = Direction.RIGHT;
        }

        if (d != null && world.checkMove(d)) {
            logic.movePlayer(d, world);
            dessinerworld(); // Ta méthode de rafraîchissement

            if (world.allBoxesInTarget()) {
                System.out.println("Gagné !");
            }
        }
    }
}
