package sokoban.saving;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LoadGame {
    public static final LoadGame gameLoader = new LoadGame();
    private File levelsFolder = new File("levels/storyMode");
    private ArrayList<char[][]> grids = null ;

    private LoadGame(){}
    public boolean loadGrids(int numLevel)
    {
        grids = new ArrayList<char[][]>();
        int nbWorlds = getnbWorlds(numLevel);
         if(nbWorlds == -1)
             return false;
        // verifier si le dossier existe
        if(!levelsFolder.exists() || !levelsFolder.isDirectory())
            return false;
        File worldsFolder = new File(levelsFolder, "level" + numLevel );
        // verifier si le level existe
        if(!worldsFolder.exists() || !worldsFolder.isDirectory())
            return false;

        // indexer les mondes du level
        int worldIndex = 0;
        // grille  en cours de lecture
        File currWorldGrid = null;
        // ligne lu depuis le fichier
        String line;
        // une liste de toutes les lignes lues depuis le fichier
        // represente la matrice
        ArrayList<String> lines;

        // lire toutes les grilles (chaque grille represente un monde)
        while(worldIndex < nbWorlds) {
            lines = new ArrayList<String>();
            // recuperer le fichier contenant la grille actuelle
            currWorldGrid = new File(worldsFolder, "world" + worldIndex +".txt");
            if(!currWorldGrid.exists() || !currWorldGrid.isFile())
                return false;

            // initialiser le lecteur du fichier
            try (BufferedReader reader =
                         new BufferedReader(
                                 new FileReader(currWorldGrid)
                         );

                    ){

                // tant qu'on a pas fini de lire le fichier

                while((line=reader.readLine())!=null)
                {
                    lines.add(line);
                }
            }catch(IOException e)
                {
                    e.printStackTrace();
                    return false;
                }
            if (lines.isEmpty()) {
                return false;
            }
            try{
                char[][] actGrid = convertLinesToGrid(lines);
                grids.add(worldIndex, actGrid);
            }catch(IllegalArgumentException e)
            {
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




