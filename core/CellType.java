package sokoban.core;

public enum CellType {
    TARGET('x'),
    FLOOR(' '),
    WALL('#'),
    EXIT('e');

    private final char symbole;

     CellType(char symbole)
     {
          this.symbole = symbole;
     }

     public char getSymbole()
     {
          return this.symbole;
     }
}

