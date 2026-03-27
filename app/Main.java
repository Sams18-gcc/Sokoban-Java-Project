package sokoban.app;

import sokoban.terminalUi.TerminalUi;

public class Main {

    public static void main(String[] args) {
        System.out.println("Répertoire courant : " + System.getProperty("user.dir"));
        TerminalUi.game.start();
    }
}