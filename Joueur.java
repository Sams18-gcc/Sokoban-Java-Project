public class Joueur extends Position
{
  
  
    public Joueur(int x, int y)
    {
     super();
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
