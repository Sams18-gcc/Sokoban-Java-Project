package persistance;
import sokoban.core.*;
import java.io.*;
import java.util.*;
import java.util.ArrayList;


//this is where i put the stack history that is gonna save all of the moves throughout the playing 
//and it is gonna read it from the statesaver ,then it saves it using snapshots
//so move 1 he gets it from the statesaver file ,he gets the positions then pushes them on the stack as a form of snapshot 
//then when we're gonna do "void undo()"" in main or... world idk yet we'll just pop from here the last snapshot and voilà.

public class StateLoader {

    public static Stack<Snapshot> loadHistory(String path) throws IOException {
        Stack<Snapshot> history = new Stack<>();
        BufferedReader br = new BufferedReader(new FileReader(path));

        String line;
        Position player = null;
        ArrayList<Box> boxes = new ArrayList<>();

        while ((line = br.readLine()) != null) {//lire ligne par ligne depuis le file in which we saved with statesaver
            if (line.startsWith("PLAYER")) {
                String[] parts = line.split(" ");
                player = new Position(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
            } else if (line.startsWith("BOX")) {
                String[] parts = line.split(" ");
                boxes.add(new Box(Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
            } else if (line.equals("---")) {
                if (player != null) {
                    history.push(new Snapshot(player, boxes));//on fait une capture et on met dans stack puis on passe au prochain ,aprés ---
                }
                player = null;
                boxes = new ArrayList<>();
            }
        }

        br.close();
        return history;
    }
}
