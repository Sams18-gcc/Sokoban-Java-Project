package sokoban.app;
import sokoban.saving.*;
import java.util.Stack;
import java.io.IOException;
import sokoban.core.*;
import sokoban.entity.*;
import java.util.ArrayList;
import java.security.DigestException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException{
        World world = new World(10, 10);
        Stack<Snapshot> undoStack = new Stack<>();//pour le undo je sauvegarderai les pos dans une pile

        world.loadWorld();
        world.displayWorld();

        if (world.getPlayer() != null) {
            undoStack.push(new Snapshot(world.getPlayer().getPosition(), world.getBoxes()));
         } else {
              System.out.println("Error: player not initialized!");
          }

        System.out.println("Left(q) | Right(d) | Up (z) | Down(s)|save(p)|load existing game(l)|undo(u)");
        Scanner sc = new Scanner(System.in);
          while (true) {
            String input = sc.nextLine();
            Direction d = null;

              if (input.equals("p")) { // save
                Statesaver.saveState(world.getPlayer().getPosition(), world.getBoxes(), "save.txt");
                System.out.println("Game saved.");
                continue;
            } else if (input.equals("l")) { // load
                Snapshot snap = Stateloader.loadState("save.txt");
                world.loadWorld(); 

                
                Position curr = world.getPlayer().getPosition();
                Position target = snap.player;
                while (!curr.equals(target)) {
                    Direction dx = (curr.getX() < target.getX()) ? Direction.RIGHT :
                                   (curr.getX() > target.getX()) ? Direction.LEFT : null;
                    Direction dy = (curr.getY() < target.getY()) ? Direction.DOWN :
                                   (curr.getY() > target.getY()) ? Direction.UP : null;

                    if (dx != null) world.movePlayer(dx);
                    else if (dy != null) world.movePlayer(dy);

                    curr = world.getPlayer().getPosition();
                }

                // restore boxes
                ArrayList<Box> boxes = world.getBoxes();
                int i = 0;
                for (Position p : snap.boxes) {
                    if (i >= boxes.size()) break;
                    Box b = boxes.get(i++);
                    Position bp = b.getPosition();
                    while (!bp.equals(p)) {
                        Direction dx = (bp.getX() < p.getX()) ? Direction.RIGHT :
                                       (bp.getX() > p.getX()) ? Direction.LEFT : null;
                        Direction dy = (bp.getY() < p.getY()) ? Direction.DOWN :
                                       (bp.getY() > p.getY()) ? Direction.UP : null;

                        if (dx != null) b.move(dx);
                        else if (dy != null) b.move(dy);

                        bp = b.getPosition();
                    }
                }

                undoStack.clear();
                undoStack.push(new Snapshot(world.getPlayer().getPosition(), world.getBoxes()));
                world.displayWorld();
                System.out.println("Game loaded.");
                continue;
            }
            if (input.equals("z")) {
                 d = Direction.UP;
            } else if (input.equals("s")) {
                 d = Direction.DOWN;
            } else if (input.equals("q")) {
                 d = Direction.LEFT;
            } else if (input.equals("d")) {
                 d = Direction.RIGHT;
            }
           
            if(d == null)
                continue;
            if (world.checkMove(d))
                undoStack.push(new Snapshot(world.getPlayer().getPosition(), world.getBoxes()));//sauvegarder chaque movement dans la stack
               if(world.movePlayer(d))
                   if(world.allBoxesInTarget())
                   {
                        break;
                   }
            world.displayWorld();
            System.out.println("Left(q) | Right(d) | Up (z) | Down(s)|save(p)|load existing game(l)|undo(u)");
        }
        world.displayWorld();
        System.out.println("Vous avez gagner!!");

    }
}