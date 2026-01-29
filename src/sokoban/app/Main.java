package sokoban.app;

import sokoban.core.Direction;
import sokoban.core.Monde;
import sokoban.entity.Joueur;

import java.io.DataInput;

public class Main {
    public static void main(String[] args) {
        Monde monde = new Monde(10, 10);
        monde.afficherMonde();

    }
}