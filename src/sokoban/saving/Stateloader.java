package sokoban.saving;

import sokoban.entity.Box;
import sokoban.core.Position;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Stateloader {

    public static Snapshot loadState(String path) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;

        Position player = null;
        ArrayList<Box> boxes = new ArrayList<>();

        while ((line = br.readLine()) != null) {
            String[] parts = line.split(" ");

            if (parts[0].equals("PLAYER")) {
                player = new Position(
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2])
                );
            } else if (parts[0].equals("BOX")) {
                boxes.add(new Box(
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2])
                ));
            }
        }

        br.close();
        return new Snapshot(player, boxes);
    }
}
