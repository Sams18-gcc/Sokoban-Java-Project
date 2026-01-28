package sokoban.entity;

import sokoban.core.Direction;
import sokoban.core.Position;

public class Joueur
{
    private Position pos;
  
    public Joueur(int x, int y)
    {
     pos = new Position(x,y);
    }



    public void move(Direction d)
   {
     if(d == null) throw new NullPointerException();
     switch(d)
     {
       case LEFT:
         this.x -= 1;
       case RIGHT:
         this.x += 1;
       case UP:
         this.y -= 1;
       case DOWN:
         this.y += 1; 
     }
   }
   
   
}
