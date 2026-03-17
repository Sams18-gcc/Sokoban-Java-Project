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

    private TerminalUi(){}

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
        } else if (input.equals("p")) {
            return LogicKey.PATHFINDING;
        } else if (input.equals("esc")) {
            return LogicKey.ESCAPE;
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
        System.out.println("Left(q) | Right(d) | Up (z) | Down(s) | Auto (p)");
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
    public void play(Level level) {
        while (level.getState() == LevelState.RUNNING) {
            // on reaffiche le monde avant chaque nouvelle action
            displayWorldTerminal(level.getCurrentWorld());
            showOptionsTerminal();

            // lecture de l'action utilisateur au clavier
            LogicKey lk = getUserAction();

            // si l'entree n'est pas reconnue, on recommence simplement
            if (lk == null)
                continue;

            // cas special : le pathfinding demande d'abord une destination,
            // puis execute le chemin trouve et reaffiche le monde a chaque etape
            if (lk == LogicKey.PATHFINDING) {
                int countDisplay = 0;
                Position dest = setPathTargetPosition();
                List<Direction> path = level.executePathFinding(dest);

                // aucun chemin possible vers la destination demandee
                if (path == null) {
                    System.out.println("Aucun chemin trouve.");
                    continue;
                }

                // on joue le chemin et on affiche le deplacement progressivement
                for (Direction d : path) {
                    countDisplay++;
                    level.executeMove(d);

                    // on reaffiche entre les deplacements pour voir le parcours
                    if (countDisplay < path.size()) {
                        displayWorldTerminal(level.getCurrentWorld());
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }

            } else {
                // pour les autres actions, on passe directement par Level
                Action result = level.executeUserAction(lk);

                // si une boite vient d'entrer dans une target,
                // on verifie si ca a termine le niveau
                if (result == Action.BOX_IN_TARGET) {
                    if (level.getState() == LevelState.WON) {
                        displayWorldTerminal(level.getCurrentWorld());
                        displayVictoryMessage();
                    }

                    // si l'utilisateur demande une pause, on stoppe la boucle terminale
                } else if (result == Action.PAUSE) {
                    level.stop();
                }
            }
        }
    }
}