package sokoban.saving;

import sokoban.entity.Box;
import sokoban.core.Position;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Statesaver {

    public static void saveState(Position playerPos, ArrayList<Box> boxes,String path) throws IOException {

        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println("PLAYER " + playerPos.getY() + " " + playerPos.getX());

            for (Box b : boxes) {
                Position p = b.getPosition();
                pw.println("BOX " + p.getY() + " " + p.getX());
            }
        }
    }
}