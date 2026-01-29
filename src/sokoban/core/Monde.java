package sokoban.core;

public class Monde {
    private final Carte carte;

    public Monde(int longueur, int largeur)
    {
         if(largeur < 5 || longueur < 5) throw new IllegalArgumentException();
         carte = new Carte(longueur,largeur);
         carte.initCarte();
    }

    public void afficherMonde()
    {
         carte.afficherCarte();
    }
}