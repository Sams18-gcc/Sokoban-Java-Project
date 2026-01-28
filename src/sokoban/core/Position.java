package sokoban.core;

public class Position {
  private int x;
  private int y;
  
  
    public Position(int x, int y)
    {
      this.x = x;
      this.y = y;
    }
    
    public int getY()
    {
      return y;
    }
   
   
   public int getX()
   {
     return x;
   }

   public void translate(Direction d)
   {
      if(d == null) throw new NullPointerException();
      this.x += d.dx;
      this.y += d.dy;
   }

}
