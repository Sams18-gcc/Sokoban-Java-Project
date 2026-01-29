package sokoban.core;

public class Carte {
    private final char[][] carte;
    private final int largeur;
    private final int longueur;

    public Carte(int longueur, int largeur) {
        if(largeur < 5 || longueur < 5) throw new IllegalArgumentException();
        this.longueur = longueur;
        this.largeur = largeur;
        carte = new char[longueur][largeur];

    }

   public void initCarte()
   {
      this.carte[1][1] ='@';
      this.carte[3][2] = 'O';
      this.carte[longueur - 2][largeur - 2] = 'x';

      for(int i=0 ; i<longueur ; i++)
      {
           for(int j=0; j<largeur; j++)
           {
                if(i==0 || j==0 || i==longueur-1 || j==largeur-1)
                {
                     carte[i][j] = '#';
                }
           }
      }

   }

   public void afficherCarte()
   {
       for(int i=0 ; i<longueur ; i++)
       {
           for(int j=0; j<largeur; j++)
           {
               System.out.printf("%c ",carte[i][j]);
           }
           System.out.println();
       }
   }

   public int getLargeur()
   {
        return largeur;
   }
    public int getLongueur()
    {
        return longueur;
    }
}