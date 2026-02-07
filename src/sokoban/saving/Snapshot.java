package persistance;
import sokoban.core.Position;
import sokoban.core.Box;
import java.util.*;

//litteraly in the title ,it's a snapshot of positions of both player and boxes


public class Snapshot {
    public Position player;
    public Set<Position> boxes;//set pour ne pas avoir des box dans une mm position 

    public Snapshot(Position p, ArrayList<Box> boxList) {
        player = new Position(p.getY(), p.getX());
        boxes = new HashSet<>();
        for (Box b : boxList) {
            boxes.add(new Position(b.getPosition().getY(), b.getPosition().getX()));
        }
    }
}