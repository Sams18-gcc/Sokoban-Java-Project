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
        System.out.println("Choisir une Direction :");
        System.out.println("Left(q) | Right(d) | Up (z) | Down(s)");
        Scanner sc = new Scanner(System.in);
        while(true)
        {

          String input = sc.nextLine();
            if (input.equals("z")) {
                if(world.checkMove(Direction.UP))
                    world.movePlayer(Direction.UP);
            }
            else if (input.equals("s")) {
                if(world.checkMove(Direction.DOWN))
                    world.movePlayer(Direction.DOWN);
            }
            else if (input.equals("q")) {
                if(world.checkMove(Direction.LEFT))
                    world.movePlayer(Direction.LEFT);
            }
            else if (input.equals("d")) {
                if (world.checkMove(Direction.RIGHT))
                    world.movePlayer(Direction.RIGHT);
            }
            world.displayWorld();
        }



    }
}