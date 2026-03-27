package sokoban.entity;

import sokoban.core.World;
import sokoban.core.WorldNode;

public class PortalBox extends Box {

    /*--------------------------------------------------
                        ATTRIBUTS
     ---------------------------------------------------*/

    //private final World worldact;
    private boolean open;
    private WorldNode nextWorld;  // noeud enfant lié à cette boîte

    /*--------------------------------------------------
                        CONSTRUCTEUR
    --------------------------------------------------*/

    public PortalBox(int y, int x) {
        super(y, x);

        this.open = false;
        this.nextWorld = null;
    }

    /*--------------------------------------------------
                     SETTERS AND GETTERS
     --------------------------------------------------*/
    public boolean isOpen() {
        return open;
    }

    public WorldNode getNextWorld() {
        return nextWorld;
    }
    /*--------------------------------------------------
                        METHODES
    --------------------------------------------------*/

    // lie cette boîte à son noeud enfant dans l'arbre
    public void linkTo(WorldNode node) {
        if (node == null) throw new NullPointerException();
        this.nextWorld = node;
    }

    /*
     * Traverse le portail dans le bon sens.
     * Level passe currentNode pour que PortalBox
     * sache si le joueur est dans l'enfant ou le parent.
     *
     * currentNode == nextWorld → joueur est dans l'enfant
     *                          → on remonte vers le parent
     * sinon                   → joueur est dans le parent
     *                          → on descend vers l'enfant
     */
    public WorldNode traverse(WorldNode currentNode) {
        if (currentNode == null) throw new NullPointerException();
        if (!open || nextWorld == null) return null;

        // joueur est DANS le monde enfant → on remonte
        if (currentNode.equals(nextWorld)) {
            return nextWorld.getParent();
        }

        // joueur est dans le monde parent → on descend
        return nextWorld;
    }

    @Override
    public void setInTarget() {
        super.setInTarget();
        this.open = true;
    }

    @Override
    public void setOutOfTarget() {
        super.setOutOfTarget();
        this.open = false;
    }
}

