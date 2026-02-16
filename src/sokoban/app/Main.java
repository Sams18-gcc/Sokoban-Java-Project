package sokoban.app;

import sokoban.core.Direction;
import sokoban.core.World;
import sokoban.logic.GameLogic;

import java.security.DigestException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        World world = new World(10, 10, 0);
        ArrayList<World> worlds = new ArrayList<>();
        worlds.add(world);

        Level level = new Level(worlds);
        level.init();
        level.run();

    }
}