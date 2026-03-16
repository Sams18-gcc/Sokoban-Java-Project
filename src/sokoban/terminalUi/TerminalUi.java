package sokoban.terminalUi;


import sokoban.app.Level;
import sokoban.app.LevelState;
import sokoban.core.Direction;
import sokoban.core.Position;
import sokoban.core.World;
import sokoban.logic.Action;
import sokoban.logic.LogicKey;

import java.util.List;
import java.util.Scanner;

public class TerminalUi {

    private final Scanner sc = new Scanner(System.in);
    public static final TerminalUi game = new TerminalUi();
    private TerminalUi(){};


    public LogicKey getUserAction() {

        String input;
        input = sc.nextLine();
        if (input.equals("z")) {
            return LogicKey.MOVE_UP;
        } else if (input.equals("s")) {
            return LogicKey.MOVE_DOWN;
        } else if (input.equals("q")) {
            return LogicKey.MOVE_LEFT;
        } else if (input.equals("d")) {
            return LogicKey.MOVE_RIGHT;
        } else if (input.equals("p")) {
            return LogicKey.PATHFINDING;
        } else if (input.equals("esc")) {
            return LogicKey.ESCAPE;
        }else return null;
    }

    public void displayWorldTerminal(World world) {
        if (world == null)
            throw new NullPointerException();
        world.getGrid().drawGrid();
    }

    public void showOptionsTerminal() {
        System.out.println("Left(q) | Right(d) | Up (z) | Down(s) | Auto (p)");
    }

    public Position setPathTargetPosition() {

        int x, y;
        System.out.println("Please enter the target position for pathfinding:");
        System.out.print("x = ");
        while (!sc.hasNextInt()) {
            System.out.println("Please enter a valid integer.");
            sc.next(); // consomme l'entrée invalide
            System.out.print("x = ");
        }
        x = sc.nextInt();
        System.out.print("y = ");
        while (!sc.hasNextInt()) {
            System.out.println("Please enter a valid integer.");
            sc.next(); // consomme l'entrée invalide
            System.out.print("y = ");
        }
        y = sc.nextInt();
        sc.nextLine();

        return new Position(y, x);


    }
    public void displayVictoryMessage()
    {
        System.out.println("YOU WON!!!!");
    }

    public void play(Level level)
    {
        while(level.getState() == LevelState.RUNNING)
        {
            displayWorldTerminal(level.getCurrentWorld());
            showOptionsTerminal();
            LogicKey lk = getUserAction();
            if(lk == null)
                continue;
            if(lk == LogicKey.PATHFINDING)
            {
                int countDisplay = 0;
                Position dest = setPathTargetPosition();
                List<Direction> path = level.executePathFinding(dest);

                if (path == null) {
                    System.out.println("Aucun chemin trouvé.");
                    continue;
                }
                for(Direction d : path)
                {
                    countDisplay++;
                    level.executeMove(d);
                    if(countDisplay < path.size() ) {
                        displayWorldTerminal(level.getCurrentWorld());
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }else{
                Action result = level.executeUserAction(lk);
                if(result == Action.BOX_IN_TARGET) {
                    if (level.getState() == LevelState.WON) {
                        displayWorldTerminal(level.getCurrentWorld());
                        displayVictoryMessage();
                    }
                } else if(result == Action.PAUSE)
                    level.stop();

            }
        }


    }
}

