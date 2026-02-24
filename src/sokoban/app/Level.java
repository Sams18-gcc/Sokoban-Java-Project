package sokoban.app;

import sokoban.core.Direction;
import sokoban.core.World;
import sokoban.logic.GameLogic;
import sokoban.saving.*;
import java.io.*;
import java.util.*;

public class Level {
    private ArrayList<World> worlds;
    private GameLogic logic;
    private int actWorld;
    private StateManager stateManager;//added this cuz i need it 
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
        
    }

    public void run()
    {  
        
        World world = worlds.get(actWorld);
        System.out.println("Left(q) | Right(d) | Up (z) | Down(s)");
        Scanner sc = new Scanner(System.in);
        while (true) {

            String input = sc.nextLine();
            // ------------------------------
            if (input.equals("save")) {
                stateManager.save(world);
                continue;
            } else if (input.equals("load")) {
                stateManager.load(world);
                continue;
            }else if (input.equals("u")) {
             stateManager.undo(world);
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
                stateManager.saveUndoSnapshot(world);//j'ai ajouté ça aussi pour snapper chaque mouvement pour le undo
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
