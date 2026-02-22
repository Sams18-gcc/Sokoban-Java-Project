package sokoban.saving;

import sokoban.core.*;
import sokoban.entity.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class StateManager {
    private final Stack<GameState> history = new Stack<>();
    private final String saveFolder = "saves/";

    public StateManager() {
        File folder = new File(saveFolder);
        if (!folder.exists()) folder.mkdirs();
    }

    public void saveState(World world) {
        history.push(new GameState(
                world.getPlayer().getPosition().getY(),
                world.getPlayer().getPosition().getX(),
                world.getBoxes(),
                world.getGridArray() // new getter in World
        ));
    }


    public void saveToFile(World world, String filename) throws IOException {
        GameState state = new GameState(
                world.getPlayer().getPosition().getY(),
                world.getPlayer().getPosition().getX(),
                world.getBoxes(),
                world.getGridArray()
        );
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(saveFolder + filename))) {
            out.writeObject(state);
        }
    }

    public void loadFromFile(World world, String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(saveFolder + filename))) {
            GameState state = (GameState) in.readObject();
            restore(world, state);
        }
    }

    private void restore(World world, GameState state) {
        
        world.setGridArray(state.getGridSnapshot());
        world.setPlayerAt(state.getPlayerY(), state.getPlayerX());
        List<Box> boxes = world.getBoxes();
        List<int[]> pos = state.getBoxPositions();
        for (int i = 0; i < boxes.size(); i++) {
            boxes.set(i, new Box(pos.get(i)[0], pos.get(i)[1]));
        }

        world.displayWorld();
    }
}