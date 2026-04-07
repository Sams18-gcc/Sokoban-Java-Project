package sokoban.entity;

import sokoban.core.WorldNode;

// ajout rec — boîte spéciale qui fait office de portail entre deux mondes.
// Hérite de Box. Quand elle est posée sur une target, elle s'ouvre (open = true)
// et permet de traverser vers le monde enfant lié (nextWorld).
public class PortalBox extends Box {

    /*--------------------------------------------------
                        ATTRIBUTS
    --------------------------------------------------*/
    private boolean   open;
    private WorldNode nextWorld;   // ajout rec — noeud enfant lié à cette boîte
    private int       portalId;    // ajout rec — identifiant unique du portail dans le niveau

    /*--------------------------------------------------
                        CONSTRUCTEUR
    --------------------------------------------------*/
    public PortalBox(int y, int x) {
        super(y, x);
        this.open      = false;
        this.nextWorld = null;
        this.portalId  = -1;
    }

    /*--------------------------------------------------
                     SETTERS AND GETTERS
    --------------------------------------------------*/
    public boolean   isOpen()      { return open;      }
    public WorldNode getNextWorld() { return nextWorld; }
    public int       getPortalId()  { return portalId;  }

    public void setPortalId(int portalId) { this.portalId = portalId; }

    /*--------------------------------------------------
                        METHODES
    --------------------------------------------------*/

    // ajout rec — lie cette boîte à son noeud enfant dans l'arbre
    public void linkTo(WorldNode node) {
        if (node == null) throw new NullPointerException();
        this.nextWorld = node;
    }

    // ajout rec — traverse le portail dans le bon sens selon le noeud courant.
    // Si le joueur est dans l'enfant (currentNode == nextWorld) → remonte vers le parent.
    // Sinon (joueur dans le parent) → descend vers l'enfant.
    // Retourne null si le portail est fermé ou non lié.
    public WorldNode traverse(WorldNode currentNode) {
        if (currentNode == null) throw new NullPointerException();
        if (!open || nextWorld == null) return null;

        if (currentNode.equals(nextWorld))
            return nextWorld.getParent();   // remontée

        return nextWorld;                   // descente
    }

    // ouvre le portail quand la boîte arrive sur une target
    @Override
    public void setInTarget() {
        super.setInTarget();
        this.open = true;
    }

    // ferme le portail quand la boîte quitte une target
    @Override
    public void setOutOfTarget() {
        super.setOutOfTarget();
        this.open = false;
    }
}
