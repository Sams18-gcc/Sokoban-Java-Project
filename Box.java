public class Box extends Position
{
     
    boolean isInTarget;
    public Box(int x, int y)
    {
     super();
     isInTarget = false;
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
   
   
   public int getY()
   {
     return y;
   }
   
   
   public int getX()
   {
     return x;
   }
   
   
}
