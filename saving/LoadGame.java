package sokoban.saving;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LoadGame {
    public static final LoadGame gameLoader = new LoadGame();
    private File levelsFolder = new File("levels");
    private ArrayList<char[][]> grids = null ;

    private LoadGame(){}
    public boolean loadGrids(int numLevel) {
        grids = new ArrayList<>();
        int nbWorlds = getnbWorlds(numLevel);
        System.out.println("nbWorlds: " + nbWorlds);

        if (nbWorlds == -1) { System.out.println("ECHEC: getnbWorlds -1"); return false; }
        if (!levelsFolder.exists() || !levelsFolder.isDirectory()) {
            System.out.println("ECHEC: dossier levels introuvable à " + levelsFolder.getAbsolutePath());
            return false;
        }

        File worldsFolder = new File(levelsFolder, "level" + numLevel);
        System.out.println("Cherche: " + worldsFolder.getAbsolutePath());
        if (!worldsFolder.exists() || !worldsFolder.isDirectory()) {
            System.out.println("ECHEC: dossier level" + numLevel + " introuvable");
            return false;
        }

        int worldIndex = 0;
        File currWorldGrid = null;
        String line;
        ArrayList<String> lines;

        while (worldIndex < nbWorlds) {
            lines = new ArrayList<>();
            currWorldGrid = new File(worldsFolder, "world" + worldIndex + ".txt");
            System.out.println("Lecture: " + currWorldGrid.getAbsolutePath());
            if (!currWorldGrid.exists() || !currWorldGrid.isFile()) {
                System.out.println("ECHEC: world" + worldIndex + ".txt introuvable");
                return false;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(currWorldGrid))) {
                while ((line = reader.readLine()) != null) { lines.add(line); }
            } catch (IOException e) { e.printStackTrace(); return false; }

            if (lines.isEmpty()) { System.out.println("ECHEC: fichier vide"); return false; }

            try {
                char[][] actGrid = convertLinesToGrid(lines);
                grids.add(worldIndex, actGrid);
            } catch (IllegalArgumentException e) {
                System.out.println("ECHEC: lignes de longueurs différentes");
                return false;
            }

            worldIndex++;
        }
        return true;
    }
    private char[][] convertLinesToGrid(ArrayList<String> lines) {
        int rows = lines.size();
        int cols = lines.get(0).length();

        for (String line : lines) {
            if (line.length() != cols) {
                throw new IllegalArgumentException();
            }
        }

        char[][] grid = new char[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = lines.get(i).charAt(j);
            }
        }

        return grid;
    }

    public ArrayList<char[][]> getGrids()
    {
        if (grids == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(grids);
    }

    public int getnbWorlds(int numLevel)
    {
        if(!levelsFolder.exists() || !levelsFolder.isDirectory())
            return -1;
        File levelFile = new File(levelsFolder, "level" + numLevel);
        if(!levelFile.exists() || !levelFile.isDirectory())
            return -1;
        File nbWorldsFile = new File(levelFile, "nbWorlds.txt");
        if(!nbWorldsFile.exists() || !nbWorldsFile.isFile())
            return -1;
        try(
                BufferedReader reader =
                        new BufferedReader(
                                new FileReader(nbWorldsFile
                                ));
                ){
            String line = reader.readLine();
            if(line == null)
                return -1;
            return Integer.parseInt(line);

        }catch(IOException e)
        {
            e.printStackTrace();
            return -1;
        }

    }


    }




