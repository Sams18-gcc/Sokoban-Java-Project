package sokoban.saving;

import java.io.*;
import java.util.*;
import sokoban.core.*;
import sokoban.entity.*;
import sokoban.saving.*;
import sokoban.app.*;

public class StateManager {

    private final String savesFolder = "saves";
    private String saveFileName = "save.txt";
    //comme on aura besoin dundostack pour chaque monde dans un level alors je fais une map ,chaque stack a sa reference monde
    private final Map<Integer, Stack<GameState>> undoStacks = new HashMap<>();

  
    public void save(Level level) {
        saveFileName = "save_level" + level.getNumLevel() + ".txt";//chauqe level aura le droit de sauvegarde 
        File folder = new File(savesFolder);
        if (!folder.exists()) folder.mkdirs();

        File file = new File(folder, saveFileName);

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {

            ArrayList<World> worlds = level.getWorlds();

            
            writer.println(level.getNumLevel() + " " + level.getActWorldRef());

            
            writer.println(worlds.size());

            for (World w : worlds) {

                writer.println(w.getWorldRef());

               
                Position p = w.getPlayerPosition();
                writer.println(p.getY() + " " + p.getX());

                List<Box> boxes = w.getBoxes();
                writer.println(boxes.size());

                
                for (Box b : boxes) {
                    writer.println(b.getPosition().getY() + " " + b.getPosition().getX());
                }

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

            // line 1 numLevel et actWorld
            String[] header = reader.readLine().split(" ");
            int savedNumLevel = Integer.parseInt(header[0]);
            int savedActWorld = Integer.parseInt(header[1]);

            if (savedNumLevel != level.getNumLevel()) {
                return false;
            }

            int nbWorlds = Integer.parseInt(reader.readLine());//on restore level

            ArrayList<World> worlds = level.getWorlds();

            for (int i = 0; i < nbWorlds; i++) {//on restore les worlds un par un 

                int worldRef = Integer.parseInt(reader.readLine());
                reader.readLine(); 

                
                int boxCount = Integer.parseInt(reader.readLine());
                for (int b = 0; b < boxCount; b++) {
                    reader.readLine(); 
                }

                // grid
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

                World w = worlds.get(worldRef);
                w.loadWorld(grid);
            }

            level.changeActWorld(savedActWorld);
            undoStacks.clear();//c'est une partie sauvbegardé donc si on jouait avant je vide la stack pour maintenant 
            return true;       //save les nouveaux mouvements 

        } catch (IOException | NumberFormatException e) {
            return false;
        }
    }

   
    public void saveUndoSnapshot(World w) {
        int ref = w.getWorldRef();
        undoStacks.computeIfAbsent(ref, k -> new Stack<>());//si le monde existe dans la map

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
        Stack<GameState> stack = undoStacks.get(ref);//chercher quelle stack a depiler

        if (stack == null || stack.isEmpty()) {
            return;
        }

        GameState snapshot = stack.pop();
        w.loadWorld(snapshot.getGridSnapshot());
        w.displayWorld();
    }

    public void clearUndoStack(int worldRef) {
        undoStacks.remove(worldRef);
    }

   public void loadFresh(Level level) {
        level.getWorlds().clear();  
        level.init();              
        undoStacks.clear();        
    }

}