# Sokoban — Parabox-like Game

Projet universitaire — Licence 2 Informatique, Université Sorbonne Paris Nord  
Un jeu de type Sokoban en Java avec interface graphique JavaFX.

---

## Prérequis

- **Java 21** installé sur votre machine

| Système | Commande / Lien |
|---|---|
| Linux | `sudo apt install openjdk-21-jdk` |
| Windows | [https://adoptium.net](https://adoptium.net) |
| Mac | `brew install openjdk@21` |

---

## Lancer le jeu

### Windows
Double-cliquez sur `run.bat`  
Ou depuis un terminal :
```
run.bat
```

### Linux
```bash
./run.sh
```
Ou clic droit sur `run.sh` → *Exécuter comme un programme*

Pour activer le double-clic sur le JAR :
```bash
./install.sh
```
Puis double-cliquez sur `sokoban-1.0-SNAPSHOT-jar-with-dependencies.jar`

### Mac
```bash
./run.sh
```

---

## Fonctionnalités

- 7 niveaux en mode Story
- Mode Free Play avec niveaux personnalisés
- Éditeur de niveaux intégré
- Pathfinding automatique (clic souris)
- Solveur automatique par algorithme A* (touche `H`)
- Sauvegarde / Chargement / Undo
- Interface graphique responsive en plein écran

---

## Contrôles

| Touche | Action |
|---|---|
| `W` / `↑` | Monter |
| `S` / `↓` | Descendre |
| `A` / `←` | Gauche |
| `D` / `→` | Droite |
| `H` | Résolution automatique |
| `V` | Sauvegarder |
| `L` | Charger |
| `R` | Recommencer |
| `Z` / `U` | Annuler |
| Clic souris | Déplacement automatique vers la case cliquée |

---

## Structure du projet

```
Sokoban-Java-Project/
├── src/sokoban/          # Code source Java
│   ├── app/              # Logique principale (Level, LevelState)
│   ├── core/             # Modèle (World, Grid, Position...)
│   ├── entity/           # Entités (Box, Player, Cell...)
│   ├── logic/            # Règles du jeu (GameLogic)
│   ├── saving/           # Persistance (StateManager)
│   ├── pathfinding/      # Recherche de chemin (BFS)
│   ├── autosolver/       # Résolution automatique (A*)
│   ├── editor/           # Éditeur de niveaux
│   └── UI/               # Interface graphique JavaFX
├── levels/               # Niveaux du jeu
├── saves/                # Sauvegardes
└── pom.xml               # Configuration Maven
```

---

## Compilation depuis les sources

```bash
mvn clean javafx:run
```

---

## Équipe

| Membre | Rôle |
|---|---|
| Sami | Coordinateur |
| Ryma | Modèle & Récursivité |
| Yanis | Interface Graphique |
| Amar | Persistance |
| Aimen | Chemins (BFS) |
| Koceila | Solveur automatique (A*) |
| Manil | Éditeur de niveaux |
| Amayas | Installation & Déploiement |
