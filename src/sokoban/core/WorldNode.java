package sokoban.core;


import sokoban.entity.PortalBox;

import java.util.ArrayList;

    public class WorldNode {

        // monde contenu dans ce noeud
        private final World worldact;

        // parent du noeud courant
        private WorldNode parent;

        // liste des mondes enfants
        private final ArrayList<WorldNode> children;

        public WorldNode(World world) {
            if (world == null) {
                throw new NullPointerException();
            }

            this.worldact = world;
            this.parent = null;
            this.children = new ArrayList<WorldNode>();
        }

        // renvoie le monde contenu dans ce noeud
        public World getWorldact() {
            return worldact;
        }

        // renvoie le parent du noeud
        public WorldNode getParent() {
            return parent;
        }

        // renvoie la liste des enfants
        public ArrayList<WorldNode> getChildren() {
            return children;
        }

        // ajoute un enfant et relie la PortalBox a ce monde enfant
        public void addChild(WorldNode child, PortalBox portalBox) {
            if (child == null || portalBox == null) {
                throw new NullPointerException();
            }

            child.parent = this;
            children.add(child);
            portalBox.linkTo(child);
        }

        // verifie si le noeud est la racine
        public boolean isRoot() {
            return parent == null;
        }

        // verifie si le noeud est une feuille
        public boolean isLeaf() {
            return children.isEmpty();
        }
    }

