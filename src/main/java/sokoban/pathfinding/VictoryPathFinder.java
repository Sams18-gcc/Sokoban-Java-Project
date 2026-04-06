package sokoban.pathfinding;

import sokoban.app.Level;
import sokoban.core.Direction;
import sokoban.core.PortalLink;
import sokoban.core.Position;
import sokoban.core.World;
import sokoban.core.WorldNode;
import sokoban.entity.Box;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

// ajout rec — guide le joueur vers son prochain objectif selon le VictoryCondition actif.
// Retourne un PathObjective contenant le chemin (directions), un message explicatif,
// et un flag reachableNow indiquant si le déplacement automatique est possible.
//
// ── Modes supportés ────────────────────────────────────────────────────────
//  ALL_WORLDS  : DFS pré-order, guide vers la boîte la plus proche dans le
//                monde courant ; si résolu, calcule le prochain monde optimal
//                via LCA (ancêtre commun le plus bas).
//  LEAVES_ONLY : si dans une feuille, guide vers la boîte ; sinon, BFS sur
//                l'arbre pour trouver la feuille non résolue la plus proche.
//  ROOT_ONLY   : si hors racine, demande de remonter ; si dans la racine,
//                guide vers la boîte non placée.
public class VictoryPathFinder {

    /*--------------------------------------------------
                    RESULTAT RETOURNE
    --------------------------------------------------*/

    // ajout rec — objet résultat du calcul d'objectif
    public static class PathObjective {

        /*--------------------------------------------------
                            ATTRIBUTS
        --------------------------------------------------*/
        private final int             targetWorldRef;
        private final Position        targetPos;
        private final List<Direction> path;
        private final String          message;
        private final boolean         reachableNow;

        /*--------------------------------------------------
                            CONSTRUCTEUR
        --------------------------------------------------*/
        public PathObjective(int targetWorldRef, Position targetPos,
                             List<Direction> path, String message, boolean reachableNow) {
            this.targetWorldRef = targetWorldRef;
            this.targetPos      = targetPos;
            this.path           = path;
            this.message        = message;
            this.reachableNow   = reachableNow;
        }

        /*--------------------------------------------------
                            GETTERS
        --------------------------------------------------*/
        public int             getTargetWorldRef() { return targetWorldRef; }
        public Position        getTargetPos()      { return targetPos;      }
        public List<Direction> getPath()           { return path;           }
        public String          getMessage()        { return message;        }
        public boolean         isReachableNow()    { return reachableNow;   }
    }

    /*--------------------------------------------------
                    POINT D'ENTREE
    --------------------------------------------------*/

    // ajout rec — dispatch selon le mode de victoire du niveau
    public static PathObjective getNextObjective(Level level) {
        if (level == null) throw new NullPointerException();
        switch (level.getVictoryCondition()) {
            case ALL_WORLDS:  return objectiveDFS(level);
            case LEAVES_ONLY: return objectiveLeaves(level);
            case ROOT_ONLY:   return objectiveRoot(level);
            default:          return null;
        }
    }

    /*--------------------------------------------------
            MODE DFS — tous les mondes
    --------------------------------------------------*/

    // ajout rec — guide vers la boîte la plus proche dans le monde courant,
    //             ou calcule le prochain monde optimal si le monde est résolu
    private static PathObjective objectiveDFS(Level level) {
        World     world   = level.getCurrentWorld();
        WorldNode current = level.getCurrentNode();
        int       ref     = world.getWorldRef();

        if (!world.allBoxesInTarget()) {
            Position pushPos = nearestPushPosition(world);
            if (pushPos != null) {
                List<Direction> path = PathSeek.findShortestPath(
                        world, world.getPlayerPosition(), pushPos);
                if (path != null && !path.isEmpty()) {
                    String hint = nextWorldHint(current);
                    return new PathObjective(ref, pushPos, path,
                            "[DFS] Monde " + ref
                            + (hint.isEmpty() ? "" : " | Ensuite : " + hint),
                            true);
                }
            }
            return new PathObjective(ref, null, null,
                    "[DFS] Monde " + ref + " — chemin bloqué. Repositionnez-vous.", false);
        }

        // monde courant résolu → trouver le prochain monde optimal
        WorldNode next = optimalNextNode(level.getRoot(), current);
        if (next == null)
            return new PathObjective(-1, null, null,
                    "[DFS] Tous les mondes résolus — victoire !", false);

        int nextRef = next.getWorldact().getWorldRef();
        String route = traversalRoute(current, next);
        return new PathObjective(nextRef, null, null,
                "[DFS] Monde " + ref + " résolu. Prochain : monde " + nextRef
                + ". Chemin : " + route,
                false);
    }

    // ajout rec — compte les mondes non résolus dans le sous-arbre de node
    private static int unsolvedCount(WorldNode node) {
        if (node == null) return 0;
        int n = node.getWorldact().allBoxesInTarget() ? 0 : 1;
        for (WorldNode c : node.getChildren()) n += unsolvedCount(c);
        return n;
    }

    // ajout rec — construit la séquence de visite optimale (DFS pré-order, enfants
    //             triés par nombre de mondes non résolus décroissant).
    //             Retourne le premier nœud non résolu qui suit `current` dans cette séquence.
    private static WorldNode optimalNextNode(WorldNode root, WorldNode current) {
        List<WorldNode> seq = new ArrayList<>();
        buildSeq(root, seq);
        int pos = seq.indexOf(current);
        if (pos < 0) pos = 0;
        for (int i = 1; i <= seq.size(); i++) {
            WorldNode n = seq.get((pos + i) % seq.size());
            if (!n.getWorldact().allBoxesInTarget()) return n;
        }
        return null;
    }

    // ajout rec — remplit `seq` en DFS pré-order avec enfants triés par poids décroissant
    private static void buildSeq(WorldNode node, List<WorldNode> seq) {
        if (node == null) return;
        seq.add(node);
        List<WorldNode> children = new ArrayList<>(node.getChildren());
        children.sort(Comparator.comparingInt(VictoryPathFinder::unsolvedCount).reversed());
        for (WorldNode c : children) buildSeq(c, seq);
    }

    /*--------------------------------------------------
            CHEMIN DE TRAVERSEE VIA LCA
    --------------------------------------------------*/

    // ajout rec — calcule le chemin dans l'arbre de `from` vers `target` via leur LCA
    //             et retourne une description lisible des portails à emprunter
    private static String traversalRoute(WorldNode from, WorldNode target) {
        if (from == null || target == null || from.equals(target))
            return "vous y êtes déjà";

        List<WorldNode> chainFrom   = ancestorChain(from);
        List<WorldNode> chainTarget = ancestorChain(target);
        WorldNode lca = lca(chainFrom, chainTarget);
        if (lca == null) return "chemin introuvable";

        StringBuilder sb    = new StringBuilder();
        boolean       first = true;

        // phase 1 : remonter de `from` jusqu'au LCA
        WorldNode cursor = from;
        while (!cursor.equals(lca)) {
            WorldNode parent = cursor.getParent();
            Integer pid = portalId(parent, cursor);
            if (!first) sb.append(" -> ");
            sb.append(pid != null ? "portail " + pid : "portail ?");
            sb.append(" -> monde parent ").append(parent.getWorldact().getWorldRef());
            cursor = parent;
            first  = false;
        }

        // phase 2 : descendre du LCA jusqu'à `target`
        List<WorldNode> descent = descentPath(lca, chainTarget);
        for (int i = 0; i < descent.size() - 1; i++) {
            WorldNode a   = descent.get(i);
            WorldNode b   = descent.get(i + 1);
            Integer   pid = portalId(a, b);
            if (!first) sb.append(" -> ");
            sb.append(pid != null ? "portail " + pid : "portail ?");
            sb.append(" -> monde enfant ").append(b.getWorldact().getWorldRef());
            first = false;
        }

        return sb.toString();
    }

    // retourne la chaîne d'ancêtres depuis node jusqu'à la racine
    private static List<WorldNode> ancestorChain(WorldNode node) {
        List<WorldNode> chain = new ArrayList<>();
        for (WorldNode c = node; c != null; c = c.getParent()) chain.add(c);
        return chain;
    }

    // retourne le LCA des deux noeuds à partir de leurs chaînes d'ancêtres
    private static WorldNode lca(List<WorldNode> chainA, List<WorldNode> chainB) {
        List<WorldNode> rA = new ArrayList<>(chainA), rB = new ArrayList<>(chainB);
        Collections.reverse(rA);
        Collections.reverse(rB);
        WorldNode lca = null;
        int len = Math.min(rA.size(), rB.size());
        for (int i = 0; i < len; i++) {
            if (rA.get(i).equals(rB.get(i))) lca = rA.get(i); else break;
        }
        return lca;
    }

    // reconstruit le chemin lca→target à partir de la chaîne d'ancêtres de target
    private static List<WorldNode> descentPath(WorldNode lca, List<WorldNode> targetChain) {
        List<WorldNode> d = new ArrayList<>();
        for (WorldNode n : targetChain) { d.add(n); if (n.equals(lca)) break; }
        Collections.reverse(d);
        return d;
    }

    // retourne l'ID du portail qui lie `parent` à `child`, ou null si non trouvé
    private static Integer portalId(WorldNode parent, WorldNode child) {
        if (parent == null || child == null) return null;
        for (PortalLink link : parent.getPortalLinks())
            if (link.getChild().equals(child)) return link.getPortalBox().getPortalId();
        return null;
    }

    // retourne une indication courte sur le prochain monde à atteindre depuis `current`
    private static String nextWorldHint(WorldNode current) {
        if (current == null) return "";
        for (WorldNode child : current.getChildren()) {
            if (unsolvedCount(child) > 0) {
                Integer pid = portalId(current, child);
                return "portail " + (pid != null ? pid : "?")
                        + " -> monde enfant " + child.getWorldact().getWorldRef()
                        + " (" + unsolvedCount(child) + " monde(s))";
            }
        }
        if (current.getParent() != null) {
            int sibUnsolved = current.getParent().getChildren().stream()
                    .filter(s -> !s.equals(current))
                    .mapToInt(VictoryPathFinder::unsolvedCount).sum();
            if (sibUnsolved > 0) return "remonter au parent";
        }
        return "";
    }

    /*--------------------------------------------------
            MODE LEAVES_ONLY — première feuille résolue
    --------------------------------------------------*/

    // ajout rec — si dans une feuille, guide vers la boîte ;
    //             sinon, cherche la feuille non résolue la plus proche (BFS arbre)
    private static PathObjective objectiveLeaves(Level level) {
        WorldNode current = level.getCurrentNode();
        World     world   = level.getCurrentWorld();
        int       ref     = world.getWorldRef();

        if (current != null && current.isLeaf()) {
            Position box = nearestUnplacedBox(world);
            if (box != null) {
                List<Direction> path = PathSeek.findShortestPath(
                        world, world.getPlayerPosition(), box);
                if (path != null && !path.isEmpty())
                    return new PathObjective(ref, box, path,
                            "[LEAVES] Feuille monde " + ref + ". Boîte -> " + posStr(box), true);
            }
            return new PathObjective(-1, null, null,
                    "[LEAVES] Feuille résolue — victoire !", false);
        }

        WorldNode leaf = nearestUnsolvedLeaf(level.getRoot());
        if (leaf == null)
            return new PathObjective(-1, null, null,
                    "[LEAVES] Toutes les feuilles résolues — victoire !", false);

        int leafRef = leaf.getWorldact().getWorldRef();

        // résoudre d'abord une éventuelle boîte dans le monde courant
        Position box = nearestUnplacedBox(world);
        if (box != null) {
            List<Direction> path = PathSeek.findShortestPath(
                    world, world.getPlayerPosition(), box);
            if (path != null && !path.isEmpty())
                return new PathObjective(ref, box, path,
                        "[LEAVES] Cible = feuille monde " + leafRef
                        + ". D'abord boîte dans monde " + ref + " -> " + posStr(box), true);
        }

        String route = traversalRoute(current, leaf);
        return new PathObjective(leafRef, null, null,
                "[LEAVES] Feuille cible = monde " + leafRef + ". Chemin : " + route, false);
    }

    // ajout rec — BFS sur l'arbre pour trouver la feuille non résolue la plus proche
    private static WorldNode nearestUnsolvedLeaf(WorldNode root) {
        if (root == null) return null;
        Queue<WorldNode> q = new LinkedList<>();
        q.add(root);
        while (!q.isEmpty()) {
            WorldNode n = q.poll();
            if (n.isLeaf() && !n.getWorldact().allBoxesInTarget()) return n;
            q.addAll(n.getChildren());
        }
        return null;
    }

    /*--------------------------------------------------
            MODE ROOT_ONLY — seule la racine compte
    --------------------------------------------------*/

    // ajout rec — si hors racine, demande de remonter ; sinon guide vers la boîte
    private static PathObjective objectiveRoot(Level level) {
        World world = level.getCurrentWorld();
        int   ref   = world.getWorldRef();
        World root  = level.getWorlds().get(0);

        if (ref != 0)
            return new PathObjective(0, null, null,
                    "[ROOT] Objectif = monde 0 uniquement. Vous êtes dans monde "
                    + ref + ". Remontez au parent.", false);

        Position box = nearestUnplacedBox(root);
        if (box == null)
            return new PathObjective(-1, null, null,
                    "[ROOT] Monde racine résolu — victoire !", false);

        List<Direction> path = PathSeek.findShortestPath(root, root.getPlayerPosition(), box);
        if (path != null && !path.isEmpty())
            return new PathObjective(0, box, path,
                    "[ROOT] Boîte non placée -> " + posStr(box), true);

        return new PathObjective(0, box, null,
                "[ROOT] Boîte en " + posStr(box) + " inaccessible. Repositionnez-vous.", true);
    }

    /*--------------------------------------------------
                    UTILITAIRES GRILLE
    --------------------------------------------------*/

    // retourne la case adjacente à la boîte non placée la plus proche d'où
    // le joueur peut la pousser (case libre, pas mur, pas boîte)
    private static Position nearestPushPosition(World world) {
        Position player  = world.getPlayerPosition();
        Position best    = null;
        int      minDist = Integer.MAX_VALUE;

        for (Box box : world.getBoxes()) {
            if (box.isInTarget()) continue;
            Position bp     = box.getPosition();
            int[][]  adj    = {{-1,0},{1,0},{0,-1},{0,1}};
            for (int[] a : adj) {
                Position c = new Position(bp.getY() + a[0], bp.getX() + a[1]);
                try {
                    if (world.getCellatPosition(c).getCellType() == sokoban.core.CellType.WALL) continue;
                    if (world.getBoxatPosition(c) != null) continue;
                } catch (Exception e) { continue; }
                int d = manhattan(player, c);
                if (d < minDist) { minDist = d; best = c; }
            }
        }
        return best;
    }

    // retourne la position de la boîte non placée la plus proche du joueur (Manhattan)
    private static Position nearestUnplacedBox(World world) {
        Position player  = world.getPlayerPosition();
        Position best    = null;
        int      minDist = Integer.MAX_VALUE;
        for (Box box : world.getBoxes()) {
            if (box.isInTarget()) continue;
            int d = manhattan(player, box.getPosition());
            if (d < minDist) { minDist = d; best = box.getPosition(); }
        }
        return best;
    }

    // calcule la distance de Manhattan entre deux positions
    private static int manhattan(Position a, Position b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

    // retourne une représentation compacte d'une position
    private static String posStr(Position p) {
        return "(" + p.getX() + "," + p.getY() + ")";
    }
}
