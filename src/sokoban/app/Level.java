package sokoban.app;

import sokoban.core.Direction;
import sokoban.core.World;
import sokoban.logic.GameLogic;
import sokoban.saving.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class Level {
    private ArrayList<World> worlds;
    private GameLogic logic;
    private int actWorld;
    private StateManager stateManager;
    public Level(Collection<World> c)
    {
         if(c == null || c.contains(null)) throw new NullPointerException();
         worlds = new ArrayList<World>(c);
         logic = new GameLogic();
         actWorld = 0;
      stateManager = new StateManager();   
    }

    public void init()
    {
        World world = worlds.get(actWorld);
        world.loadWorld();
        world.displayWorld();
       stateManager.saveState(world);
    }

    public void run()
    {
        World world = worlds.get(actWorld);
        System.out.println("Left(q) | Right(d) | Up (z) | Down(s)");
        Scanner sc = new Scanner(System.in);
        while (true) {

            String input = sc.nextLine();
            // ------------------------------

            if(input.equals("save")) { // save to file
                try {
                    stateManager.saveToFile(world, "prot.sok");
                    System.out.println("Game saved");
                } catch(Exception e) {
                    System.out.println("Failed to save: " + e.getMessage());
                }
                continue;
            }
            else if(input.equals("load")) { // load from file
                try {
                    stateManager.loadFromFile(world, "prot.sok");
                    System.out.println("Game loaded");
                } catch(Exception e) {
                    System.out.println("Failed to load: " + e.getMessage());
                }
                continue;
            }
            // ------------------------------
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
                if(logic.movePlayer(d, world))
                    
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
