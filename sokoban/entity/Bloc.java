package sokoban.entity;

import sokoban.core.BlocType;
import sokoban.core.Position;

public class Bloc extends Position
{
  private BlocType bloc;


  public Bloc(int x, int y, BlocType bloc)
  {
    super(x,y);
    this.bloc = bloc;
  }
}