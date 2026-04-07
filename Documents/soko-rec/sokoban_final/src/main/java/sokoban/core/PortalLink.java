package sokoban.core;

import sokoban.entity.PortalBox;

// ajout rec — associe une PortalBox à son WorldNode enfant dans l'arbre.
// Stocké dans WorldNode pour retrouver rapidement quel portail mène à quel sous-monde.
public class PortalLink {

    /*--------------------------------------------------
                        ATTRIBUTS
    --------------------------------------------------*/
    private final PortalBox portalBox;
    private final WorldNode child;

    /*--------------------------------------------------
                        CONSTRUCTEUR
    --------------------------------------------------*/
    public PortalLink(PortalBox portalBox, WorldNode child) {
        if (portalBox == null || child == null) throw new NullPointerException();
        this.portalBox = portalBox;
        this.child     = child;
    }

    /*--------------------------------------------------
                        GETTERS
    --------------------------------------------------*/
    public PortalBox getPortalBox() { return portalBox; }
    public WorldNode getChild()     { return child;     }
}
