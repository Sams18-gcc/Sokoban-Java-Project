package sokoban.saving;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

// singleton responsable du chargement des grilles depuis le système de fichiers.
// Lit les fichiers worldN.txt d'un dossier de niveau et les convertit en char[][].
public class LoadGame {

    /*--------------------------------------------------
                        SINGLETON
    --------------------------------------------------*/
    public static final LoadGame gameLoader = new LoadGame();

    private LoadGame() {}

    /*--------------------------------------------------
                        ATTRIBUTS
    --------------------------------------------------*/
    private File               levelsFolder;
    private ArrayList<char[][]> grids = null;

    /*--------------------------------------------------
                        SETTERS
    --------------------------------------------------*/
    public void setLevelsFolder(String levelsFolder) {
        this.levelsFolder = new File(levelsFolder);
    }

    /*--------------------------------------------------
                        METHODES
    --------------------------------------------------*/

    // charge toutes les grilles du niveau numLevel depuis le dossier levelsFolder.
    // Retourne false si le niveau est introuvable ou invalide.
    public boolean loadGrids(int numLevel) {
        grids = new ArrayList<>();
        int nbWorlds = getnbWorlds(numLevel);
        if (nbWorlds == -1) return false;

        if (!levelsFolder.exists() || !levelsFolder.isDirectory()) return false;

        File worldsFolder = new File(levelsFolder, "level" + numLevel);
        if (!worldsFolder.exists() || !worldsFolder.isDirectory()) return false;

        int            worldIndex   = 0;
        String         line;
        ArrayList<String> lines;

        while (worldIndex < nbWorlds) {
            lines = new ArrayList<>();
            File currWorldGrid = new File(worldsFolder, "world" + worldIndex + ".txt");
            if (!currWorldGrid.exists() || !currWorldGrid.isFile()) return false;

            try (BufferedReader reader = new BufferedReader(new FileReader(currWorldGrid))) {
                while ((line = reader.readLine()) != null)
                    lines.add(line);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            if (lines.isEmpty()) return false;

            try {
                grids.add(worldIndex, convertLinesToGrid(lines));
            } catch (IllegalArgumentException e) {
                return false;
            }

            worldIndex++;
        }
        return true;
    }

    // convertit une liste de lignes en tableau de char 2D.
    // Lance IllegalArgumentException si les lignes n'ont pas toutes la même longueur.
    private char[][] convertLinesToGrid(ArrayList<String> lines) {
        int rows = lines.size();
        int cols = lines.get(0).length();

        for (String l : lines)
            if (l.length() != cols) throw new IllegalArgumentException();

        char[][] grid = new char[rows][cols];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                grid[i][j] = lines.get(i).charAt(j);

        return grid;
    }

    // retourne une copie des grilles chargées, ou une liste vide si aucun chargement
    public ArrayList<char[][]> getGrids() {
        if (grids == null) return new ArrayList<>();
        return new ArrayList<>(grids);
    }

    // lit le nombre de mondes dans nbWorlds.txt du niveau donné.
    // Retourne -1 en cas d'erreur.
    public int getnbWorlds(int numLevel) {
        if (!levelsFolder.exists() || !levelsFolder.isDirectory()) return -1;
        File levelFile    = new File(levelsFolder, "level" + numLevel);
        if (!levelFile.exists() || !levelFile.isDirectory()) return -1;
        File nbWorldsFile = new File(levelFile, "nbWorlds.txt");
        if (!nbWorldsFile.exists() || !nbWorldsFile.isFile()) return -1;

        try (BufferedReader reader = new BufferedReader(new FileReader(nbWorldsFile))) {
            String line = reader.readLine();
            if (line == null) return -1;
            return Integer.parseInt(line);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
