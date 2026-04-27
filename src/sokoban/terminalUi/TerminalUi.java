package sokoban.terminalUi;

import sokoban.app.Level;
import sokoban.app.LevelState;
import sokoban.core.Direction;
import sokoban.core.Position;
import sokoban.core.World;
import sokoban.logic.ResultOfAction;
import sokoban.logic.LogicKey;
import sokoban.saving.StateManager;

import java.util.List;
import java.util.Scanner;

/*
 * Interface terminale du jeu.
 * Cette classe recupere les actions de l'utilisateur,
 * les traduit en actions logiques, appelle Level,
 * puis affiche le resultat dans le terminal.
 *
 * Elle sert aussi d'exemple pour montrer comment
 * une autre interface (ex: graphique) peut s'integrer
 * au noyau du projet.
 */
public class TerminalUi {

    private final Scanner sc = new Scanner(System.in);

    public static final TerminalUi game = new TerminalUi();

    private TerminalUi() {
    }

    /*
     * Lit l'entree clavier de l'utilisateur
     * et la traduit en LogicKey.
     */
    public LogicKey getUserAction() {
        String input = sc.nextLine();

        if (input.equals("z")) {
            return LogicKey.MOVE_UP;
        } else if (input.equals("s")) {
            return LogicKey.MOVE_DOWN;
        } else if (input.equals("q")) {
            return LogicKey.MOVE_LEFT;
        } else if (input.equals("d")) {
            return LogicKey.MOVE_RIGHT;

            // traverser vers un monde enfant
        } else if (input.equals("ez")) {
            return LogicKey.TRAVERSE_CHILD_UP;
        } else if (input.equals("es")) {
            return LogicKey.TRAVERSE_CHILD_DOWN;
        } else if (input.equals("eq")) {
            return LogicKey.TRAVERSE_CHILD_LEFT;
        } else if (input.equals("ed")) {
            return LogicKey.TRAVERSE_CHILD_RIGHT;

            // traverser vers le monde parent
        } else if (input.equals("pz")) {
            return LogicKey.TRAVERSE_PARENT_UP;
        } else if (input.equals("ps")) {
            return LogicKey.TRAVERSE_PARENT_DOWN;
        } else if (input.equals("pq")) {
            return LogicKey.TRAVERSE_PARENT_LEFT;
        } else if (input.equals("pd")) {
            return LogicKey.TRAVERSE_PARENT_RIGHT;

        } else if (input.equals("p")) {
            return LogicKey.FIND_PATH;
        } else if (input.equals("esc")) {
            return LogicKey.ESCAPE;
        } else if (input.equals("u")) {
            return LogicKey.UNDO;
        } else if (input.equals("sv")) {
            return LogicKey.SAVE;
        } else if (input.equals("ld")) {
            return LogicKey.LOAD;
        } else if (input.equals("r")) {
            return LogicKey.RELOAD;
        } else {
            return null;
        }
    }

    // affiche le monde courant dans le terminal
    public void displayWorldTerminal(World world) {
        if (world == null)
            throw new NullPointerException();

        world.getGrid().drawGrid();
    }

    // affiche les commandes disponibles
    public void showOptionsTerminal() {
        System.out.println("Move: z/s/q/d | Child portal: ez/es/eq/ed | Parent portal: pz/ps/pq/pd | Auto(p) | Undo(u) | Save(sv) | Load(ld) | Reload(r)");
    }

    /*
     * Demande a l'utilisateur la position cible
     * pour le pathfinding.
     */
    public Position setPathTargetPosition() {
        int x, y;

        System.out.println("Please enter the target position for pathfinding:");

        System.out.print("x = ");
        while (!sc.hasNextInt()) {
            System.out.println("Please enter a valid integer.");
            sc.next();
            System.out.print("x = ");
        }
        x = sc.nextInt();

        System.out.print("y = ");
        while (!sc.hasNextInt()) {
            System.out.println("Please enter a valid integer.");
            sc.next();
            System.out.print("y = ");
        }
        y = sc.nextInt();

        // consomme le retour a la ligne restant apres nextInt()
        sc.nextLine();

        return new Position(y, x);
    }

    public void displayVictoryMessage() {
        System.out.println("YOU WON!!!!");
    }

    /*
     * Boucle principale de la version terminale.
     *
     * Tant que le niveau est en cours :
     * - on affiche le monde courant
     * - on lit l'action de l'utilisateur
     * - on appelle Level pour executer cette action
     * - puis on affiche le resultat si besoin
     */
    public void play(Level level, StateManager sm) {

        while (level.getState() == LevelState.RUNNING) {
            displayWorldTerminal(level.getCurrentWorld());
            showOptionsTerminal();

            LogicKey lk = getUserAction();

            if (lk == null)
                continue;

            // cas pathfinding : demande une destination, puis joue le chemin
            if (lk == LogicKey.FIND_PATH) {
                Position dest = setPathTargetPosition();
                List<Direction> path = level.executePathFinding(dest);

                if (path == null) {
                    System.out.println("Aucun chemin trouve.");
                    continue;
                }

                int countDisplay = 0;
                for (Direction d : path) {
                    countDisplay++;

                    LogicKey key = level.directionToLogicKey(d);
                    ResultOfAction result = level.handleUserAction(key);


                    if (countDisplay < path.size()) {
                        displayWorldTerminal(level.getCurrentWorld());
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }



                continue;
            }

            // tous les autres cas passent par handleUserAction
            ResultOfAction result = level.handleUserAction(lk);

            switch (result) {
                case WON:
                    displayWorldTerminal(level.getCurrentWorld());
                    displayVictoryMessage();
                    break;

                case PAUSED:
                    level.stop();
                    break;

                default:
                    break;
            }
        }
    }
}