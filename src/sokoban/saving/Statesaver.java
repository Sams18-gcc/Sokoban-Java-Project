package persistance;
import sokoban.core.World;
import sokoban.core.Position;
import sokoban.core.Box;
import java.io.*;
import java.util.ArrayList;

//basically this has to be called everytime we make a move ,so this saves the move in like a text file or idk yet 
//thats gonna look like  BOX x y 
//                       PLAYER x y 
// 
//etc and each move is seperated by ---


public class StateSaver {
    private static int moveCounter = 1;

    public static void saveState(Position playerpos,ArrayList<Box> boxes, String path) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path, true))) { //true c'est pour ne pas écraser avec de nouvelles valeurs
            pw.println("STATE " + moveCounter);

            pw.println("PLAYER " + playerpos.getY() + " " + playerpos.getX());

            for (Box b : boxes) {
                Position bp = b.getPosition();
                pw.println("BOX " + bp.getY() + " " + bp.getX());
            }

            pw.println("---"); 
            moveCounter++;
        }
    }
}
