package sokoban.saving;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LoadGame {
    public static final LoadGame gameLoader = new LoadGame();
    private File levelsFolder;
    private ArrayList<char[][]> grids = null ;

    private LoadGame(){}

    public void setLevelsFolder(String levelsFolder)
    {
        this.levelsFolder = new File(levelsFolder);

    }
    public boolean loadGrids(int numLevel)
    {
        grids = new ArrayList<char[][]>();
        int nbWorlds = getnbWorlds(numLevel);
        System.out.println("DEBUG nbWorlds=" + nbWorlds + " pour level=" + numLevel);
        if(nbWorlds == -1) {
            System.out.println("FAIL: getnbWorlds a retourne -1");
            return false;
        }
        if(!levelsFolder.exists() || !levelsFolder.isDirectory()) {
            System.out.println("FAIL: levelsFolder invalide: " + levelsFolder.getAbsolutePath());
            return false;
        }
        File worldsFolder = new File(levelsFolder, "level" + numLevel);
        if(!worldsFolder.exists() || !worldsFolder.isDirectory()) {
            System.out.println("FAIL: worldsFolder introuvable: " + worldsFolder.getAbsolutePath());
            return false;
        }

        int worldIndex = 0;
        File currWorldGrid = null;
        String line;
        ArrayList<String> lines;

        while(worldIndex < nbWorlds) {
            lines = new ArrayList<String>();
            currWorldGrid = new File(worldsFolder, "world" + worldIndex + ".txt");
            System.out.println("DEBUG lecture: " + currWorldGrid.getAbsolutePath());
            if(!currWorldGrid.exists() || !currWorldGrid.isFile()) {
                System.out.println("FAIL: fichier introuvable: " + currWorldGrid.getAbsolutePath());
                return false;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(currWorldGrid))) {
                while((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            } catch(IOException e) {
                e.printStackTrace();
                return false;
            }

            if(lines.isEmpty()) {
                System.out.println("FAIL: fichier vide: " + currWorldGrid.getAbsolutePath());
                return false;
            }

            System.out.println("DEBUG " + lines.size() + " lignes lues, longueurs:");
            for(int i = 0; i < lines.size(); i++) {
                System.out.println("  ligne " + i + " longueur=" + lines.get(i).length() + " [" + lines.get(i) + "]");
            }

            try {
                char[][] actGrid = convertLinesToGrid(lines);
                grids.add(worldIndex, actGrid);
            } catch(IllegalArgumentException e) {
                System.out.println("FAIL: grille invalide (lignes de longueurs differentes) dans " + currWorldGrid.getAbsolutePath());
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




