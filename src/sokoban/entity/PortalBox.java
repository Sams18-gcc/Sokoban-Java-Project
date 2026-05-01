package sokoban.entity;
import sokoban.core.WorldNode;
import sokoban.entity.Box;

public class PortalBox extends Box {

    /*--------------------------------------------------
                        ATTRIBUTS
    --------------------------------------------------*/
    // indique si le portail est ouvert
    private boolean open;

    // monde enfant lie a cette PortalBox
    private WorldNode nextWorld;


    /*--------------------------------------------------
                        CONSTRUCTEUR
    --------------------------------------------------*/

    public PortalBox(int y, int x) {
        super(y, x);
        open = false;
        nextWorld = null;

    }
    /*--------------------------------------------------
                        SETTERS AND GETTERS
       --------------------------------------------------*/
    // quand la boite est sur une target, le portail est ouvert
    @Override
    public void setInTarget() {
        super.setInTarget();
        open = true;
    }

    // quand la boite sort d'une target, le portail est ferme
    @Override
    public void setOutOfTarget() {
        super.setOutOfTarget();
        open = false;
    }
    // renvoie le monde enfant lie au portail
    public WorldNode getNextWorld() {
        return nextWorld;
    }
    /*--------------------------------------------------
                    METHODES
    --------------------------------------------------*/
    // verifie si le portail est ouvert
    public boolean isOpen() {
        return open;
    }
    // lie le portail a un monde enfant
    public void linkTo(WorldNode node) {
        if (node == null) {
            throw new NullPointerException();
        }

        nextWorld = node;
    }


}