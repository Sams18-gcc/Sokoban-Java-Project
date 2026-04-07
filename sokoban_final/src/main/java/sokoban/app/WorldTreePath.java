package sokoban.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// ajout rec — représente un chemin entre deux noeuds dans l'arbre de mondes.
// Contient la liste des worldRefs visités et les étapes (Step) qui les relient.
public class WorldTreePath {

    /*--------------------------------------------------
                        TYPES INTERNES
    --------------------------------------------------*/

    // ajout rec — sens d'une étape : vers un enfant (via portail) ou vers le parent
    public enum StepType {
        TO_CHILD,
        TO_PARENT
    }

    // ajout rec — une étape individuelle dans le chemin arborescent
    public static class Step {

        /*--------------------------------------------------
                            ATTRIBUTS
        --------------------------------------------------*/
        private final int      fromWorldRef;
        private final int      toWorldRef;
        private final StepType type;
        private final Integer  portalId;

        /*--------------------------------------------------
                            CONSTRUCTEUR
        --------------------------------------------------*/
        public Step(int fromWorldRef, int toWorldRef, StepType type, Integer portalId) {
            if (type == null) throw new NullPointerException();
            this.fromWorldRef = fromWorldRef;
            this.toWorldRef   = toWorldRef;
            this.type         = type;
            this.portalId     = portalId;
        }

        /*--------------------------------------------------
                            GETTERS
        --------------------------------------------------*/
        public int      getFromWorldRef() { return fromWorldRef; }
        public int      getToWorldRef()   { return toWorldRef;   }
        public StepType getType()         { return type;         }
        public Integer  getPortalId()     { return portalId;     }

        /*--------------------------------------------------
                            METHODES
        --------------------------------------------------*/

        // retourne une représentation lisible de l'étape
        @Override
        public String toString() {
            if (type == StepType.TO_CHILD)
                return "world" + fromWorldRef + " --portal#" + portalId + "--> world" + toWorldRef;
            return "world" + fromWorldRef + " --parent--> world" + toWorldRef;
        }
    }

    /*--------------------------------------------------
                        ATTRIBUTS
    --------------------------------------------------*/
    private final List<Integer>          worldRefs;
    private final List<Step>             steps;

    /*--------------------------------------------------
                        CONSTRUCTEUR
    --------------------------------------------------*/
    public WorldTreePath(List<Integer> worldRefs, List<Step> steps) {
        if (worldRefs == null || steps == null) throw new NullPointerException();
        this.worldRefs = new ArrayList<>(worldRefs);
        this.steps     = new ArrayList<>(steps);
    }

    /*--------------------------------------------------
                        GETTERS
    --------------------------------------------------*/
    public List<Integer> getWorldRefs() { return Collections.unmodifiableList(worldRefs); }
    public List<Step>    getSteps()     { return Collections.unmodifiableList(steps);     }

    /*--------------------------------------------------
                        METHODES
    --------------------------------------------------*/

    // retourne le nombre de mondes dans ce chemin
    public int worldCount() { return worldRefs.size(); }

    // retourne le nombre d'arêtes (transitions) dans ce chemin
    public int edgeCount()  { return steps.size();     }

    // retourne le worldRef de départ, ou -1 si vide
    public int getStartWorldRef() {
        if (worldRefs.isEmpty()) return -1;
        return worldRefs.get(0);
    }

    // retourne le worldRef d'arrivée, ou -1 si vide
    public int getEndWorldRef() {
        if (worldRefs.isEmpty()) return -1;
        return worldRefs.get(worldRefs.size() - 1);
    }

    // retourne une chaîne lisible représentant le chemin complet
    public String toDisplayString() {
        if (worldRefs.isEmpty()) return "(vide)";

        StringBuilder sb = new StringBuilder();
        sb.append("world").append(worldRefs.get(0));

        for (Step step : steps) {
            if (step.getType() == StepType.TO_CHILD)
                sb.append(" --portal#").append(step.getPortalId())
                  .append("--> world").append(step.getToWorldRef());
            else
                sb.append(" --parent--> world").append(step.getToWorldRef());
        }
        return sb.toString();
    }
}
