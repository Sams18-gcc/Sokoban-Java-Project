package sokoban.saving;

import sokoban.app.Level;
import sokoban.core.Position;
import sokoban.core.World;
import sokoban.entity.Box;

import java.io.*;
import java.util.*;

// gère la sauvegarde sur disque, le chargement et la pile undo pour chaque monde.
// Une stack undo indépendante est tenue par worldRef.
public class StateManager {

    /*--------------------------------------------------
                        ATTRIBUTS
    --------------------------------------------------*/
    private final String savesFolder = "saves";
    private       String saveFileName = "save.txt";

    // pile undo séparée par monde (clé = worldRef)
    private final Map<Integer, Stack<GameState>> undoStacks = new HashMap<>();

    /*--------------------------------------------------
                        METHODES — SAUVEGARDE SUR DISQUE
    --------------------------------------------------*/

    // sauvegarde l'état complet du niveau dans saves/save_levelN.txt
    public void save(Level level) {
        saveFileName = "save_level" + level.getNumLevel() + ".txt";
        File folder = new File(savesFolder);
        if (!folder.exists()) folder.mkdirs();

        try (PrintWriter writer = new PrintWriter(new FileWriter(new File(folder, saveFileName)))) {
            ArrayList<World> worlds = level.getWorlds();
            writer.println(level.getNumLevel() + " " + level.getActWorldRef());
            writer.println(worlds.size());

            for (World w : worlds) {
                writer.println(w.getWorldRef());

                Position p = w.getPlayerPosition();
                writer.println(p.getY() + " " + p.getX());

                List<Box> boxes = w.getBoxes();
                writer.println(boxes.size());
                for (Box b : boxes)
                    writer.println(b.getPosition().getY() + " " + b.getPosition().getX());

                char[][] grid = w.getGridArray();
                writer.println(grid.length + " " + grid[0].length);
                for (char[] row : grid)
                    writer.println(new String(row));
            }
        } catch (IOException e) {
            // silencieux par compatibilité avec l'original
        }
    }

    // charge une sauvegarde depuis saves/save_levelN.txt et reconstruit l'état du niveau.
    // Retourne false si le fichier est absent ou invalide.
    public boolean load(Level level) {
        saveFileName = "save_level" + level.getNumLevel() + ".txt";
        File file = new File(savesFolder, saveFileName);
        if (!file.exists()) return false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String[] header = reader.readLine().split(" ");
            int savedNumLevel = Integer.parseInt(header[0]);
            int savedActWorld = Integer.parseInt(header[1]);

            if (savedNumLevel != level.getNumLevel()) return false;

            int nbWorlds = Integer.parseInt(reader.readLine());

            LoadGame gameLoader = LoadGame.gameLoader;
            gameLoader.loadGrids(level.getNumLevel());
            ArrayList<char[][]> grids = gameLoader.getGrids();

            for (int i = 0; i < nbWorlds; i++) {
                int worldRef = Integer.parseInt(reader.readLine());

                header = reader.readLine().split(" ");
                Position playerPos = new Position(
                        Integer.parseInt(header[0]),
                        Integer.parseInt(header[1]));

                int boxCount = Integer.parseInt(reader.readLine());
                ArrayList<Position> boxPositions = new ArrayList<>();
                for (int b = 0; b < boxCount; b++) {
                    header = reader.readLine().split(" ");
                    boxPositions.add(new Position(
                            Integer.parseInt(header[0]),
                            Integer.parseInt(header[1])));
                }

                String[] dims = reader.readLine().split(" ");
                int rows = Integer.parseInt(dims[0]);
                int cols = Integer.parseInt(dims[1]);
                char[][] grid = new char[rows][cols];
                for (int r = 0; r < rows; r++) {
                    String line = reader.readLine();
                    for (int c = 0; c < cols; c++)
                        grid[r][c] = line.charAt(c);
                }

                World loadingWorld = new World(rows, cols, worldRef);
                World temp         = new World(rows, cols, worldRef);
                loadingWorld.loadWorld(grids.get(worldRef));
                temp.loadWorld(grid);

                copyCellsState(temp, loadingWorld);
                loadingWorld.setGridArray(grid);
                loadingWorld.setPlayerAt(playerPos);
                loadingWorld.setBoxesFromPositions(boxPositions);

                level.replaceWorld(worldRef, loadingWorld);
            }

            level.changeActWorld(savedActWorld);
            undoStacks.clear();
            return true;

        } catch (IOException | NumberFormatException e) {
            return false;
        }
    }

    /*--------------------------------------------------
                        METHODES — UNDO
    --------------------------------------------------*/

    // sauvegarde un snapshot du monde courant avant chaque action (pour undo)
    public void saveUndoSnapshot(World w) {
        int ref = w.getWorldRef();
        undoStacks.computeIfAbsent(ref, k -> new Stack<>());
        undoStacks.get(ref).push(new GameState(
                w.getPlayerPosition().getY(),
                w.getPlayerPosition().getX(),
                w.getBoxes(),
                w.getGridArray()));
    }

    // annule la dernière action sur le monde donné en restaurant le dernier snapshot
    public void undo(World w) {
        int ref = w.getWorldRef();
        Stack<GameState> stack = undoStacks.get(ref);
        if (stack == null || stack.isEmpty()) return;

        GameState snapshot = stack.pop();
        w.setBoxesFromPositions(snapshot.getBoxPositions());
        w.setPlayerAt(new Position(snapshot.getPlayerY(), snapshot.getPlayerX()));

        World temp = new World(w.getGrid().getLength(), w.getGrid().getWidth(), ref);
        temp.loadWorld(snapshot.getGridSnapshot());
        copyCellsState(temp, w);
        w.setGridArray(snapshot.getGridSnapshot());
    }

    // vide la pile undo d'un monde donné
    public void clearUndoStack(int worldRef) { undoStacks.remove(worldRef); }

    /*--------------------------------------------------
                        METHODES — RELOAD
    --------------------------------------------------*/

    // recharge le niveau depuis les fichiers originaux et vide les piles undo
    public void loadFresh(Level level) {
        level.getWorlds().clear();
        level.init();
        undoStacks.clear();
    }

    /*--------------------------------------------------
                        METHODES INTERNES
    --------------------------------------------------*/

    // recopie l'état free/occupied des cellules de source vers target,
    // sans changer le type de cellule — commun entre load et undo
    private void copyCellsState(World source, World target) {
        int rows = source.getGrid().getLength();
        int cols = source.getGrid().getWidth();
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                Position pos = new Position(y, x);
                if (source.getCellatPosition(pos).isFree())
                    target.getCellatPosition(pos).setFree();
                else
                    target.getCellatPosition(pos).setOccupied();
            }
        }
    }
}
