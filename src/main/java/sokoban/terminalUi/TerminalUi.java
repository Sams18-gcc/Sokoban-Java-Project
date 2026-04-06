package sokoban.terminalUi;

import sokoban.app.Level;
import sokoban.app.LevelState;
import sokoban.app.VictoryCondition;
import sokoban.app.WorldTreePath;
import sokoban.core.World;
import sokoban.logic.Action;
import sokoban.logic.LogicKey;
import sokoban.pathfinding.VictoryPathFinder;
import sokoban.pathfinding.VictoryPathFinder.PathObjective;
import sokoban.saving.StateManager;

import java.util.List;
import java.util.Scanner;

// interface terminale du jeu — singleton.
// Gère le menu de démarrage, la boucle de jeu et l'affichage des commandes.
// ajout rec — supporte la navigation par portail (tz/ts/tq/td, mz/ms/mq/md)
//             et le guide de victoire (pv) via VictoryPathFinder.
public class TerminalUi {

    /*--------------------------------------------------
                        SINGLETON
    --------------------------------------------------*/
    public static final TerminalUi game = new TerminalUi();

    private final Scanner sc = new Scanner(System.in);

    private TerminalUi() {}

    /*--------------------------------------------------
                        METHODES — DEMARRAGE
    --------------------------------------------------*/

    // point d'entrée : affiche le menu et lance une partie
    public void start() {
        System.out.println("======================");
        System.out.println("       SOKOBAN        ");
        System.out.println("======================");
        startGame();
    }

    // demande le numéro de niveau au joueur
    private int chooseLevel() {
        System.out.println("Choisissez un niveau (1-5) :");
        System.out.print("Niveau : ");
        return Integer.parseInt(sc.nextLine().trim());
    }

    // ajout rec — demande le mode de victoire au joueur
    private VictoryCondition chooseVictoryCondition() {
        System.out.println("======================");
        System.out.println("Mode de victoire :");
        System.out.println("1. Tous les mondes    (DFS)");
        System.out.println("2. Premiere feuille   (LEAVES)");
        System.out.println("3. Monde surface seul (ROOT)");
        System.out.print("Choix : ");
        switch (Integer.parseInt(sc.nextLine().trim())) {
            case 1:  return VictoryCondition.ALL_WORLDS;
            case 2:  return VictoryCondition.LEAVES_ONLY;
            case 3:  return VictoryCondition.ROOT_ONLY;
            default:
                System.out.println("Choix invalide, mode DFS par défaut.");
                return VictoryCondition.ALL_WORLDS;
        }
    }

    // initialise le niveau, définit le mode de victoire et lance la boucle
    private void startGame() {
        int numLevel = chooseLevel();
        VictoryCondition vc = chooseVictoryCondition();

        StateManager sm    = new StateManager();
        Level        level = new Level(numLevel, "levels/storyMode", sm);
        level.setVictoryCondition(vc);
        level.init();

        if (level.getWorlds().isEmpty()) {
            System.out.println("Niveau " + numLevel + " introuvable.");
            start();
            return;
        }

        System.out.println("Niveau " + numLevel + " chargé — "
                + level.getWorlds().size() + " monde(s).");
        System.out.println("Mode de victoire : " + vc);

        play(level, sm);
        start();
    }

    /*--------------------------------------------------
                        METHODES — SAISIE
    --------------------------------------------------*/

    // traduit la saisie clavier en LogicKey.
    // ajout rec — commandes de traversée portail (tz/ts/tq/td, mz/ms/mq/md) et guide (pv)
    public LogicKey getUserAction() {
        String input = sc.nextLine().trim();
        switch (input) {
            case "z":   return LogicKey.MOVE_UP;
            case "s":   return LogicKey.MOVE_DOWN;
            case "q":   return LogicKey.MOVE_LEFT;
            case "d":   return LogicKey.MOVE_RIGHT;
            case "pv":  return LogicKey.FIND_PATH_MODE;     // ajout rec — guide victoire
            case "esc": return LogicKey.ESCAPE;
            case "u":   return LogicKey.UNDO;
            case "sv":  return LogicKey.SAVE;
            case "ld":  return LogicKey.LOAD;
            case "r":   return LogicKey.RELOAD;
            case "tz":  return LogicKey.TRAVERSE_CHILD_UP;     // ajout rec
            case "ts":  return LogicKey.TRAVERSE_CHILD_DOWN;   // ajout rec
            case "tq":  return LogicKey.TRAVERSE_CHILD_LEFT;   // ajout rec
            case "td":  return LogicKey.TRAVERSE_CHILD_RIGHT;  // ajout rec
            case "mz":  return LogicKey.TRAVERSE_PARENT_UP;    // ajout rec
            case "ms":  return LogicKey.TRAVERSE_PARENT_DOWN;  // ajout rec
            case "mq":  return LogicKey.TRAVERSE_PARENT_LEFT;  // ajout rec
            case "md":  return LogicKey.TRAVERSE_PARENT_RIGHT; // ajout rec
            default:    return null;
        }
    }

    /*--------------------------------------------------
                        METHODES — AFFICHAGE
    --------------------------------------------------*/

    // affiche la grille du monde dans le terminal
    public void displayWorldTerminal(World world) {
        if (world == null) throw new NullPointerException();
        world.getGrid().drawGrid();
    }

    // affiche les raccourcis clavier disponibles
    public void showOptionsTerminal() {
        System.out.println("Left(q) | Right(d) | Up(z) | Down(s) | Undo(u) | Save(sv) | Load(ld) | Reload(r)");
        System.out.println("Guide(pv) | Portail enfant : tz/ts/tq/td | Portail parent : mz/ms/mq/md | Pause(esc)");
    }

    // affiche le message de victoire
    public void displayVictoryMessage() {
        System.out.println("╔══════════════════════╗");
        System.out.println("║     YOU WON !!!!     ║");
        System.out.println("╚══════════════════════╝");
    }

    // ajout rec — affiche les chemins racine→feuille en mode LEAVES_ONLY
    private void displayLeavesOnlyRoutes(Level level) {
        if (level.getCurrentNode() == null) return;

        System.out.println("Monde courant : world" + level.getCurrentWorld().getWorldRef());

        WorldTreePath shortest = level.getShortestPathFromCurrentNodeToLeaf();
        if (shortest != null)
            System.out.println("Chemin le plus court vers feuille : "
                    + shortest.toDisplayString()
                    + " (" + shortest.edgeCount() + " transition(s))");

        List<WorldTreePath> allPaths = level.getLeafPathsLongestToShortest();
        if (!allPaths.isEmpty()) {
            System.out.println("Tous les chemins racine→feuille :");
            int idx = 1;
            for (WorldTreePath path : allPaths) {
                System.out.println("  " + idx + ". " + path.toDisplayString()
                        + " (" + path.worldCount() + " monde(s))");
                idx++;
            }
        }
    }

    // ajout rec — affiche l'objectif calculé par VictoryPathFinder
    private void showVictoryGuide(Level level) {
        PathObjective objective = VictoryPathFinder.getNextObjective(level);
        if (objective == null) {
            System.out.println("[pv] Impossible de calculer un objectif.");
            return;
        }
        System.out.println(objective.getMessage());
        if (!objective.isReachableNow())
            System.out.println("→ Utilisez les commandes de traversée (tz/ts/tq/td ou mz/ms/mq/md).");
    }

    /*--------------------------------------------------
                        METHODES — BOUCLE DE JEU
    --------------------------------------------------*/

    // boucle principale : affiche, lit l'entrée, exécute et réagit au résultat
    public void play(Level level, StateManager sm) {
        while (level.getState() == LevelState.RUNNING) {
            displayWorldTerminal(level.getCurrentWorld());

            // ajout rec — affiche les chemins en mode LEAVES_ONLY
            if (level.getVictoryCondition() == VictoryCondition.LEAVES_ONLY)
                displayLeavesOnlyRoutes(level);

            showOptionsTerminal();

            LogicKey lk = getUserAction();
            if (lk == null) {
                System.out.println("Commande inconnue.");
                continue;
            }

            // ajout rec — guide de victoire traité à part (pas délégué à Level)
            if (lk == LogicKey.FIND_PATH_MODE) {
                showVictoryGuide(level);
                continue;
            }

            Action result = level.handleUserAction(lk);

            switch (result) {
                case TRAVERSE:  // ajout rec
                    System.out.println("─── Monde "
                            + level.getCurrentWorld().getWorldRef() + " ───");
                    break;
                case WON:
                    displayWorldTerminal(level.getCurrentWorld());
                    displayVictoryMessage();
                    break;
                case PAUSE:
                    level.stop();
                    break;
                case BLOCKED:
                    System.out.println("Mouvement bloqué.");
                    break;
                case SAVED:
                    System.out.println("Partie sauvegardée.");
                    break;
                case LOADED:
                    System.out.println("Partie chargée.");
                    break;
                case RELOADED:
                    System.out.println("Niveau rechargé.");
                    break;
                case UNDONE:
                    System.out.println("Action annulée.");
                    break;
                default:
                    break;
            }
        }
    }
}
