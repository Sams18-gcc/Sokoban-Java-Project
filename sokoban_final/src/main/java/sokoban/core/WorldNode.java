package sokoban.core;

import sokoban.entity.PortalBox;

import java.util.ArrayList;

// ajout rec — noeud de l'arbre récursif de mondes.
// Chaque WorldNode encapsule un World et connaît son parent, ses enfants,
// et les liens portail (PortalLink) qui relient ce noeud à ses enfants.
public class WorldNode {

    /*--------------------------------------------------
                        ATTRIBUTS
    --------------------------------------------------*/
    private final World              worldact;
    private       WorldNode          parent;
    private final ArrayList<WorldNode>  children;
    private final ArrayList<PortalLink> portalLinks;
    private final String             nodeRef;

    /*--------------------------------------------------
                   SETTERS AND GETTERS
    --------------------------------------------------*/
    public World                    getWorldact()    { return worldact;    }
    public WorldNode                getParent()      { return parent;      }
    public ArrayList<WorldNode>     getChildren()    { return children;    }
    public ArrayList<PortalLink>    getPortalLinks() { return portalLinks; }
    public String                   getNodeRef()     { return nodeRef;     }

    /*--------------------------------------------------
                        CONSTRUCTEURS
    --------------------------------------------------*/

    // constructeur racine — parent null, enfants/liens vides
    public WorldNode(World world) {
        if (world == null) throw new NullPointerException();
        this.worldact    = world;
        this.parent      = null;
        this.children    = new ArrayList<>();
        this.portalLinks = new ArrayList<>();
        this.nodeRef     = String.valueOf(world.getWorldRef());
    }

    /*--------------------------------------------------
                        METHODES
    --------------------------------------------------*/

    // ajout rec — ajoute un monde enfant, crée le lien retour parent,
    // et associe la PortalBox à son noeud enfant via linkTo()
    public void addChild(WorldNode child, PortalBox portalBox) {
        if (child == null || portalBox == null) throw new NullPointerException();

        child.parent = this;
        children.add(child);

        // lie la portalBox à son monde enfant
        portalBox.linkTo(child);
        portalLinks.add(new PortalLink(portalBox, child));
    }

    // retourne vrai si ce noeud est la racine (aucun parent)
    public boolean isRoot() { return parent == null;      }

    // retourne vrai si ce noeud est une feuille (aucun enfant)
    public boolean isLeaf() { return children.isEmpty(); }
}
