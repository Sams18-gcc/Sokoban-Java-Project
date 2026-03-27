package sokoban.terminalUi;

import sokoban.app.Level;
import sokoban.app.LevelState;
import sokoban.app.VictoryCondition;
import sokoban.core.Direction;
import sokoban.core.Position;
import sokoban.core.World;
import sokoban.logic.Action;
import sokoban.logic.LogicKey;
import sokoban.saving.StateManager;

import java.util.List;
import java.util.Scanner;

/*
 * Interface terminale du jeu.
 * modifiee pour la recursivite :
 *   - TRAVERSE ne declenche pas le undo
 *   - ajout du menu principal
 *   - ajout du choix de mode de victoire
 */
public class TerminalUi {

    /*--------------------------------------------------
                        SINGLETON
    --------------------------------------------------*/

    public static final TerminalUi game = new TerminalUi();

    /*--------------------------------------------------
                        ATTRIBUTS
    --------------------------------------------------*/

    private final Scanner sc = new Scanner(System.in);

    /*--------------------------------------------------
                        CONSTRUCTEUR
    --------------------------------------------------*/

    private TerminalUi() {}

    /*--------------------------------------------------
                        MENU
    --------------------------------------------------*/

    // lance le menu principal
    public void start() {
        System.out.println("======================");
        System.out.println("       SOKOBAN        ");
        System.out.println("======================");
        System.out.println("1. Jouer");
        System.out.println("2. Quitter");
        System.out.print("Choix : ");

        int choix = Integer.parseInt(sc.nextLine());

        switch (choix) {
            case 1: startGame(); break;
            case 2: System.out.println("Au revoir !"); break;
            default:
                System.out.println("Choix invalide.");
                start();
        }
    }

    // choix du niveau
    private int chooseLevel() {
        System.out.println("======================");
        System.out.println("Choisissez un niveau  :");
        System.out.print("Niveau : ");
        return Integer.parseInt(sc.nextLine());
    }

    // choix du mode de victoire
    private VictoryCondition chooseVictoryCondition() {
        System.out.println("======================");
        System.out.println("Mode de victoire :");
        System.out.println("1. Tous les mondes    (DFS)");
        System.out.println("2. Premiere feuille   (LEAVES)");
        System.out.println("3. Tous par niveaux   (BFS)");
        System.out.println("4. Monde surface seul (ROOT)");
        System.out.print("Choix : ");

        int choix = Integer.parseInt(sc.nextLine());

        switch (choix) {
            case 1:
                return VictoryCondition.ALL_WORLDS;
            case 2:
                return VictoryCondition.LEAVES_ONLY;
            case 3:
                return VictoryCondition.BFS_ALL;
            case 4:
                return VictoryCondition.ROOT_ONLY;
            default:
                System.out.println("Choix invalide, mode DFS par defaut.");
                return VictoryCondition.ALL_WORLDS;
        }
    }

    // lance une partie complete
    private void startGame() {
        int numLevel        = chooseLevel();
        VictoryCondition vc = chooseVictoryCondition();

        StateManager sm = new StateManager();
        Level level     = new Level(numLevel, sm);
        level.init();
        level.setVictoryCondition(vc);

        if (level.getWorlds().isEmpty()) {
            System.out.println("Niveau " + numLevel + " introuvable.");
            start();
            return;
        }

        System.out.println("Niveau " + numLevel + " charge — "
                + level.getWorlds().size() + " monde(s).");
        System.out.println("Mode de victoire : " + vc);

        play(level, sm);

        // retour au menu apres la partie
        start();
    }

    /*--------------------------------------------------
                        METHODES
    --------------------------------------------------*/

    // lit l'entree clavier et la traduit en LogicKey
    public LogicKey getUserAction() {
        String input = sc.nextLine();

        if (input.equals("z"))        return LogicKey.MOVE_UP;
        else if (input.equals("s"))   return LogicKey.MOVE_DOWN;
        else if (input.equals("q"))   return LogicKey.MOVE_LEFT;
        else if (input.equals("d"))   return LogicKey.MOVE_RIGHT;
        else if (input.equals("p"))   return LogicKey.PATHFINDING;
        else if (input.equals("esc")) return LogicKey.ESCAPE;
        else if (input.equals("u"))   return LogicKey.UNDO;
        else if (input.equals("sv"))  return LogicKey.SAVE;
        else if (input.equals("ld"))  return LogicKey.LOAD;
        else if (input.equals("r"))   return LogicKey.RELOAD;
        else                          return null;
    }

    // affiche le monde courant dans le terminal
    public void displayWorldTerminal(World world) {
        if (world == null) throw new NullPointerException();
        world.getGrid().drawGrid();
    }

    // affiche les commandes disponibles
    public void showOptionsTerminal() {
        System.out.println("Left(q) | Right(d) | Up(z) | Down(s) | Auto(p) | Undo(u) | Save(sv) | Load(ld) | Reload(r)");
    }

    // demande la position cible pour le pathfinding
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

        sc.nextLine();

        return new Position(y, x);
    }

    public void displayVictoryMessage() {
        System.out.println("YOU WON!!!!");
    }

    /*
     * Boucle principale de la version terminale.
     * modifiee pour la recursivite :
     *   - TRAVERSE est un mouvement valide, pas de undo
     *   - affiche le monde courant apres une traversee
     */
    public void play(Level level, StateManager sm) {

        while (level.getState() == LevelState.RUNNING) {
            displayWorldTerminal(level.getCurrentWorld());
            showOptionsTerminal();

            LogicKey lk = getUserAction();
            if (lk == null) continue;

            if (lk == LogicKey.UNDO) {
                sm.undo(level.getCurrentWorld());
                continue;
            }

            if (lk == LogicKey.SAVE) {
                sm.save(level);
                continue;
            }

            if (lk == LogicKey.LOAD) {
                sm.load(level);
                continue;
            }

            if (lk == LogicKey.RELOAD) {
                sm.loadFresh(level);
                continue;
            }

            if (lk == LogicKey.PATHFINDING) {
                int countDisplay = 0;
                Position dest = setPathTargetPosition();
                List<Direction> path = level.executePathFinding(dest);

                if (path == null) {
                    System.out.println("Aucun chemin trouve.");
                    continue;
                }

                for (Direction d : path) {
                    countDisplay++;
                    sm.saveUndoSnapshot(level.getCurrentWorld());
                    level.executeMove(d);

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
                level.saveMove();
                Action result = level.executeUserAction(lk);

                // AJOUT rec : TRAVERSE est valide -> pas de undo, affichage du nouveau monde
                if (result == Action.TRAVERSE) {
                    System.out.println("--- Monde "
                            + level.getCurrentWorld().getWorldRef() + " ---");
                    continue;
                }

                // mouvement invalide -> on annule
                if (result != Action.MOVED && result != Action.BOX_IN_TARGET)
                    level.undo();

                if (result == Action.BOX_IN_TARGET) {
                    if (level.getState() == LevelState.WON) {
                        displayWorldTerminal(level.getCurrentWorld());
                        displayVictoryMessage();
                    }
                } else if (result == Action.PAUSE) {
                    level.stop();
                }
            }
        }
    }
}