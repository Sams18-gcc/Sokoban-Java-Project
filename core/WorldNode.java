package sokoban.core;

import sokoban.entity.PortalBox;
import java.util.ArrayList;

public class WorldNode {

    /*--------------------------------------------------
                        ATTRIBUTS
    --------------------------------------------------*/
    private final World worldact;
    private WorldNode parent;
    private final ArrayList<WorldNode> children;







    private final String nodeRef;

    /*--------------------------------------------------
                   SETTERS AND GETTERS
   --------------------------------------------------*/
    public World getWorldact()                  {
        return worldact; }
    public WorldNode getParent()                {
        return parent; }
    public ArrayList<WorldNode> getChildren()   {
        return children; }

    public String getNodeRef()                   {
        return nodeRef; }
    /*--------------------------------------------------
                        CONSTRUCTEURS
    --------------------------------------------------*/

    // constructeur racine
    public WorldNode(World world) {
        if (world == null) throw new NullPointerException();
        this.worldact  = world;
        this.parent    = null;
        this.children  = new ArrayList<>();
        this.nodeRef = String.valueOf(world.getWorldRef());
    }

    /*--------------------------------------------------
                        METHODES
    --------------------------------------------------*/

    /**
     * Ajoute un monde enfant.
     * Crée automatiquement le lien de retour vers ce noeud parent.
     * Lie aussi la PortalBox de ce monde à son noeud enfant.
     */
    public void addChild(WorldNode child, PortalBox portalBox) {
        if (child == null || portalBox == null) throw new NullPointerException();

        child.parent   = this;
        children.add(child);

        // lie la portalBox à son monde enfant
        portalBox.linkTo(child);
    }




    public boolean isRoot() { return parent == null; }
    public boolean isLeaf() { return children.isEmpty(); }


}