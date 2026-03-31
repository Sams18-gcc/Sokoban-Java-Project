package sokoban.app;

import sokoban.core.Direction;
import sokoban.core.World;
import sokoban.logic.GameLogic;
import sokoban.terminalUi.TerminalUi;
import sokoban.saving.StateManager;
import java.security.DigestException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        


        StateManager sm = new StateManager(); //
        Level level = new Level(2, "levels/storyMode" ,sm);
        level.init();
        TerminalUi t = TerminalUi.game;       //
        t.play(level,sm);                     //instance statemanager pour chaque level 

    }
}