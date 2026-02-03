package sokoban.app;

import sokoban.core.Direction;
import sokoban.core.World;

import java.security.DigestException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        World world = new World(10, 10);
        world.loadWorld();
        world.displayWorld();
        System.out.println("Left(q) | Right(d) | Up (z) | Down(s)");
        Scanner sc = new Scanner(System.in);
        while (true) {

            String input = sc.nextLine();
            Direction d = null;

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
               if(world.movePlayer(d))
                   if(world.allBoxesInTarget())
                   {
                        break;
                   }
            world.displayWorld();
            System.out.println("Left(q) | Right(d) | Up (z) | Down(s)");
        }
        world.displayWorld();
        System.out.println("Vous avez gagner!!");

    }
}