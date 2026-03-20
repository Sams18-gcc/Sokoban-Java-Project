package sokoban.saving;

import java.io.*;
import java.util.*;
import sokoban.core.*;
import sokoban.entity.*;
import sokoban.app.*;

public class StateManager {

    private final String savesFolder = "saves";
    private String saveFileName = "save.txt";

    // une stack undo pour chaque monde
    private final Map<Integer, Stack<GameState>> undoStacks = new HashMap<>();


    public void save(Level level) {
        saveFileName = "save_level" + level.getNumLevel() + ".txt";

        File folder = new File(savesFolder);
        if (!folder.exists()) folder.mkdirs();

        File file = new File(folder, saveFileName);

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {

            ArrayList<World> worlds = level.getWorlds();

            // on ecrit dabord le num du level et le monde actif
            writer.println(level.getNumLevel() + " " + level.getActWorldRef());

            // nb de mondes dans le level
            writer.println(worlds.size());

            for (World w : worlds) {

                writer.println(w.getWorldRef());

                // position du joueur
                Position p = w.getPlayerPosition();
                writer.println(p.getY() + " " + p.getX());

                // les boites
                List<Box> boxes = w.getBoxes();
                writer.println(boxes.size());

                for (Box b : boxes) {
                    writer.println(b.getPosition().getY() + " " + b.getPosition().getX());
                }

                // la grille affichee
                char[][] grid = w.getGridArray();
                writer.println(grid.length + " " + grid[0].length);
                for (char[] row : grid) {
                    writer.println(new String(row));
                }
            }

        } catch (IOException e) {
        }
    }

    public boolean load(Level level) {
        saveFileName = "save_level" + level.getNumLevel() + ".txt";
        File file = new File(savesFolder, saveFileName);

        if (!file.exists()) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            // 1ere ligne : num level + monde actif
            String[] header = reader.readLine().split(" ");
            int savedNumLevel = Integer.parseInt(header[0]);
            int savedActWorld = Integer.parseInt(header[1]);

            if (savedNumLevel != level.getNumLevel()) {
                return false;
            }

            int nbWorlds = Integer.parseInt(reader.readLine());

            LoadGame gameLoader = LoadGame.gameLoader;
            gameLoader.loadGrids(level.getNumLevel());
            ArrayList<char[][]> grids = gameLoader.getGrids();

            for (int i = 0; i < nbWorlds; i++) {

                int worldRef = Integer.parseInt(reader.readLine());

                // joueur
                header = reader.readLine().split(" ");
                int playerY = Integer.parseInt(header[0]);
                int playerX = Integer.parseInt(header[1]);
                Position playerPos = new Position(playerY, playerX);

                // boites
                int boxCount = Integer.parseInt(reader.readLine());
                ArrayList<Position> boxPositions = new ArrayList<>();

                for (int b = 0; b < boxCount; b++) {
                    header = reader.readLine().split(" ");
                    int boxY = Integer.parseInt(header[0]);
                    int boxX = Integer.parseInt(header[1]);
                    boxPositions.add(new Position(boxY, boxX));
                }

                // grille sauvegardee
                String[] dims = reader.readLine().split(" ");
                int rows = Integer.parseInt(dims[0]);
                int cols = Integer.parseInt(dims[1]);

                char[][] grid = new char[rows][cols];
                for (int r = 0; r < rows; r++) {
                    String line = reader.readLine();
                    for (int c = 0; c < cols; c++) {
                        grid[r][c] = line.charAt(c);
                    }
                }

                // loadingWorld garde les vraies cells du monde de base
                World loadingWorld = new World(rows, cols, worldRef);

                // temp sert juste a lire les etats free/occupied depuis la grille savee
                World temp = new World(rows, cols, worldRef);

                loadingWorld.loadWorld(grids.get(worldRef));
                temp.loadWorld(grid);

                // on recopie juste letat des cells
                copyCellsState(temp, loadingWorld);

                // ensuite on remet la vraie grille visible + joueur + boites
                loadingWorld.setGridArray(grid);
                loadingWorld.setPlayerAt(playerPos);
                loadingWorld.setBoxesFromPositions(boxPositions);

                level.replaceWorld(worldRef, loadingWorld);
            }

            level.changeActWorld(savedActWorld);

            // on vide les anciennes stacks undo apres un load
            undoStacks.clear();
            return true;

        } catch (IOException | NumberFormatException e) {
            return false;
        }
    }


    public void saveUndoSnapshot(World w) {
        int ref = w.getWorldRef();

        // si aucune stack existe pour ce monde on la cree
        undoStacks.computeIfAbsent(ref, k -> new Stack<>());

        GameState snapshot = new GameState(
                w.getPlayerPosition().getY(),
                w.getPlayerPosition().getX(),
                w.getBoxes(),
                w.getGridArray()
        );

        undoStacks.get(ref).push(snapshot);
    }

    public void undo(World w) {
        int ref = w.getWorldRef();
        Stack<GameState> stack = undoStacks.get(ref);

        if (stack == null || stack.isEmpty()) {
            return;
        }

        GameState snapshot = stack.pop();

        // on remet boites et joueur
        w.setBoxesFromPositions(snapshot.getBoxPositions());
        w.setPlayerAt(new Position(snapshot.getPlayerY(), snapshot.getPlayerX()));

        // temp sert juste a reconstruire letat free/occupied des cells
        World temp = new World(w.getGrid().getLength(), w.getGrid().getWidth(), ref);
        temp.loadWorld(snapshot.getGridSnapshot());

        // on recopie letat des cells depuis temp vers le vrai monde
        copyCellsState(temp, w);

        // puis on remet la grille affichee
        w.setGridArray(snapshot.getGridSnapshot());
    }

    public void clearUndoStack(int worldRef) {
        undoStacks.remove(worldRef);
    }

    public void loadFresh(Level level) {
        level.getWorlds().clear();
        level.init();
        undoStacks.clear();
    }

    // methode commune entre load et undo
    // elle ne change pas le type de la cellule, elle recopie juste son etat free/occupied
    private void copyCellsState(World source, World target) {
        int rows = source.getGrid().getLength();
        int cols = source.getGrid().getWidth();

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                Position pos = new Position(y, x);

                if (source.getCellatPosition(pos).isFree()) {
                    target.getCellatPosition(pos).setFree();
                } else {
                    target.getCellatPosition(pos).setOccupied();
                }
            }
        }
    }
}