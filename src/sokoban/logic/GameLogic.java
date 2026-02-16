package sokoban.logic;

import sokoban.core.CellType;
import sokoban.core.Direction;
import sokoban.core.Position;
import sokoban.core.World;
import sokoban.entity.Box;

public class GameLogic{





    public final boolean movePlayer(Direction d, World world) {
        if (d == null) throw new NullPointerException();
        boolean boxInTarget = false;
        Position actualPos = world.getPlayerPosition();
        Position nextPos = world.getPlayerPosition();
        nextPos.translate(d);
        if (world.isBox(nextPos)) {
            boxInTarget = moveBox(d, nextPos, world);
        }
        world.updateCells(actualPos, nextPos);
        world.updateWorldData(actualPos, nextPos, world.getCellatPosition(actualPos).getCellType());
        world.changePlayerPosition(d);
        return boxInTarget;
    }


         public final boolean moveBox(Direction d, Position pos , World world) {
         if (d == null || pos == null || world == null || world == null) throw new NullPointerException();
         Position actualPos = new Position(pos.getY(), pos.getX());
         Position nextPos = new Position(pos.getY(), pos.getX());
         nextPos.translate(d);
         world.updateWorldData(actualPos, nextPos, world.getCellatPosition(actualPos).getCellType());
         Box box = world.getBoxatPosition(pos);
         if (box == null)
             throw new IllegalStateException();
         else box.move(d);
         world.updateCells(actualPos, nextPos);
         if (world.getCellatPosition(nextPos).getCellType() == CellType.TARGET) {
             box.setInTarget();
             return true;

         } else {
             box.setOutOfTarget();
             return false;

         }

     }



}