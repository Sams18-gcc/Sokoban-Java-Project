package sokoban.saving;

import sokoban.entity.Box;
import sokoban.core.Position;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Snapshot {
    public Position player;
    public Set<Position> boxes;

    public Snapshot(Position p, ArrayList<Box> boxList) {
        player = new Position(p.getY(), p.getX());
        boxes = new HashSet<>();
        for (Box b : boxList) {
            Position bp = b.getPosition();
            boxes.add(new Position(bp.getY(), bp.getX()));
        }
    }
}

