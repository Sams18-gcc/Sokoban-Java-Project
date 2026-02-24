package sokoban.saving;

import sokoban.core.*;
import sokoban.entity.*;
import java.io.*;
import java.util.*;

public class StateManager {

    private final String saveFile = "prot.sok";     
    private final Stack<GameState> undoStack = new Stack<>();    

    
    public void save(World world) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(saveFile))) {

            Position p = world.getPlayerPosition();
            writer.println(p.getY() + " " + p.getX());

            List<Box> boxes = world.getBoxes();
            writer.println(boxes.size());
            for (Box b : boxes) {
                writer.println(b.getPosition().getY() + " " + b.getPosition().getX());
            }

            Grid grid = world.getGrid();
            for (int i = 0; i < grid.getLength(); i++) {
                for (int j = 0; j < grid.getWidth(); j++) {
                    writer.print(grid.getElement(i, j));
                }
                writer.println();
            }

            System.out.println("Game saved.");

        } catch (IOException e) {
            System.out.println("Failed to save: " + e.getMessage());
        }
    }

    public void load(World world) {
        try (BufferedReader reader = new BufferedReader(new FileReader(saveFile))) {

            String[] playerPos = reader.readLine().split(" ");
            world.setPlayerAt(Integer.parseInt(playerPos[0]), Integer.parseInt(playerPos[1]));

            int boxCount = Integer.parseInt(reader.readLine());
            for (int i = 0; i < boxCount; i++) {
                String[] boxPos = reader.readLine().split(" ");
                world.getBoxes().set(i, new Box(
                        Integer.parseInt(boxPos[0]),
                        Integer.parseInt(boxPos[1])
                ));
            }

            Grid grid = world.getGrid();
            for (int i = 0; i < grid.getLength(); i++) {
                String line = reader.readLine();
                for (int j = 0; j < grid.getWidth(); j++) {
                    grid.setElement(i, j, line.charAt(j));
                }
            }

            world.displayWorld();
            System.out.println("Game loaded.");

        } catch (IOException e) {
            System.out.println("Failed to load: " + e.getMessage());
        }
    }

    
    public void saveUndoSnapshot(World world) {
        GameState snapshot = new GameState(
            world.getPlayerPosition().getY(),
            world.getPlayerPosition().getX(),
            world.getBoxes(),
            world.getGridArray()
        );
        undoStack.push(snapshot);
    }

    
    public void undo(World world) {
        if (undoStack.isEmpty()) {
            System.out.println("Nothing to undo.");
            return;
        }
        GameState snapshot = undoStack.pop();

        world.setGridArray(snapshot.getGridSnapshot());
        world.setPlayerAt(snapshot.getPlayerY(), snapshot.getPlayerX());

        List<Box> boxes = world.getBoxes();
        List<int[]> pos = snapshot.getBoxPositions();
        for (int i = 0; i < boxes.size(); i++) {
            boxes.set(i, new Box(pos.get(i)[0], pos.get(i)[1]));
        }

        world.displayWorld();
        
    }
}
