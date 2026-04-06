package sokoban.editor;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;

// éditeur de plateaux Sokoban avec interface JavaFX.
// Permet de dessiner des grilles, les valider, les sauvegarder et les charger.
// Peut être ouvert depuis InterfaceController ou lancé en standalone via EditorLauncher.
public class EditorGUI extends Application {

    /*--------------------------------------------------
                        CONSTANTES
    --------------------------------------------------*/
    private static final int    CELL_SIZE = 48;
    private static final String ASSETS    = "/sokoban/UI/resources/assets/";

    /*--------------------------------------------------
                        ATTRIBUTS
    --------------------------------------------------*/
    // images du jeu réutilisées dans la grille
    private Image imgFloor;
    private Image imgWall;
    private Image imgBox;
    private Image imgTarget;
    private Image imgPlayer;
    private Image imgPortal;

    private Stage       mainStage;      // stage principale du jeu (pour le bouton RETOUR)
    private LevelEditor editor;
    private Canvas      canvas;
    private char        currentTool = '#';
    private Label       statusLabel;
    private ToggleGroup toolGroup;

    /*--------------------------------------------------
                        INIT JAVAFX
    --------------------------------------------------*/

    // point d'entrée JavaFX : construit l'UI complète de l'éditeur
    @Override
    public void start(Stage primaryStage) {
        loadImages();
        editor = new LevelEditor(10, 10, 'A');

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1b1b1b, #2e2e2e);");

        root.setTop(createTopBar(primaryStage));
        root.setLeft(createToolPalette());

        // canvas centré dans un ScrollPane
        canvas = new Canvas(editor.getCols() * CELL_SIZE, editor.getRows() * CELL_SIZE);
        StackPane canvasContainer = new StackPane(canvas);
        canvasContainer.setStyle("-fx-background-color: #1e1e1e;");
        canvasContainer.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(canvasContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: #1e1e1e; -fx-background-color: #1e1e1e;");
        root.setCenter(scrollPane);

        // barre de statut en bas
        statusLabel = new Label("Prêt. Choisissez un outil et cliquez.");
        statusLabel.setFont(Font.font("Monospaced", 13));
        statusLabel.setTextFill(Color.web("#D8B35A"));
        statusLabel.setPadding(new Insets(8, 14, 8, 14));
        HBox statusBar = new HBox(statusLabel);
        statusBar.setStyle("-fx-background-color: #111111;");
        root.setBottom(statusBar);

        canvas.setOnMousePressed(e -> onMouseClick(e.getX(), e.getY(), e.getButton()));
        canvas.setOnMouseDragged(e -> onMouseClick(e.getX(), e.getY(), e.getButton()));

        redrawGrid();

        Scene scene = new Scene(root, 900, 650);
        scene.getStylesheets().add(
                getClass().getResource("/sokoban/UI/resources/style/Game.css").toExternalForm());
        scene.addEventFilter(KeyEvent.KEY_PRESSED, this::onKeyPress);

        primaryStage.setTitle("Éditeur de Plateau Sokoban");
        primaryStage.setScene(scene);
        primaryStage.show();
        canvas.requestFocus();
    }

    // appelé par InterfaceController pour passer la Stage principale du jeu
    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    /*--------------------------------------------------
                METHODES — CHARGEMENT IMAGES
    --------------------------------------------------*/

    // charge toutes les images du jeu utilisées dans l'éditeur
    private void loadImages() {
        imgFloor  = loadImg("floor.png");
        imgWall   = loadImg("wall.png");
        imgBox    = loadImg("box.png");
        imgTarget = loadImg("target.png");
        imgPlayer = loadImg("player.gif");
        imgPortal = loadImg("portal.png");
    }

    // charge une image depuis le dossier ASSETS, retourne null en cas d'échec
    private Image loadImg(String name) {
        try { return new Image(getClass().getResourceAsStream(ASSETS + name)); }
        catch (Exception e) { return null; }
    }

    /*--------------------------------------------------
                METHODES — CONSTRUCTION UI
    --------------------------------------------------*/

    // construit la barre d'outils supérieure avec tous les boutons d'action
    private HBox createTopBar(Stage stage) {
        HBox bar = new HBox(8);
        bar.setPadding(new Insets(10, 14, 10, 14));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: #111111;");

        Button btnBack       = makeButton("◀ MENU");
        Label  title         = new Label("ÉDITEUR");
        Button btnNew        = makeButton("NOUVEAU");
        Button btnSave       = makeButton("SAUVER");
        Button btnLoad       = makeButton("CHARGER");
        Button btnExport     = makeButton("EXPORTER");
        Button btnImport     = makeButton("IMPORTER");
        Button btnValid      = makeButton("VALIDER MONDE");
        Button btnValidLevel = makeButton("VALIDER LEVEL");
        Button btnReport     = makeButton("ARBRE LEVEL");
        Button btnBordure    = makeButton("BORDURES");

        title.setFont(Font.font("Impact", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#D8B35A"));
        title.setPadding(new Insets(0, 12, 0, 0));

        // bouton valider monde en vert pour le distinguer
        btnValid.setStyle(
            "-fx-background-color: linear-gradient(to bottom,#1a4a2a,#0a2a10);" +
            "-fx-text-fill: white; -fx-font-family: Impact; -fx-font-size: 13px;" +
            "-fx-border-color: #4a7c59; -fx-border-width: 2;" +
            "-fx-background-radius: 12; -fx-border-radius: 12; -fx-padding: 8 14 8 14;");

        btnBack.setOnAction(e       -> retournerAuJeu(stage));
        btnNew.setOnAction(e        -> dialogNouveau());
        btnSave.setOnAction(e       -> dialogSave());
        btnLoad.setOnAction(e       -> dialogLoad());
        btnExport.setOnAction(e     -> exporterFichier(stage));
        btnImport.setOnAction(e     -> importerFichier(stage));
        btnValid.setOnAction(e      -> montrerValidation());
        btnValidLevel.setOnAction(e -> dialogValidationLevel());
        btnReport.setOnAction(e     -> dialogRapportLevel());
        btnBordure.setOnAction(e    -> { remplirBordures(); redrawGrid(); statusLabel.setText("Bordures remplies."); });

        bar.getChildren().addAll(
            btnBack, new Separator(), title,
            btnNew, btnSave, btnLoad,
            new Separator(),
            btnExport, btnImport,
            new Separator(),
            btnValid, btnValidLevel, btnReport, btnBordure
        );
        return bar;
    }

    // construit la palette d'outils latérale
    private VBox createToolPalette() {
        VBox palette = new VBox(6);
        palette.setPadding(new Insets(12));
        palette.setAlignment(Pos.TOP_CENTER);
        palette.setStyle("-fx-background-color: #111111;");
        palette.setPrefWidth(140);

        Label title = new Label("OUTILS");
        title.setFont(Font.font("Impact", 16));
        title.setTextFill(Color.web("#D8B35A"));

        toolGroup = new ToggleGroup();

        ToggleButton btnMur    = creerBoutonOutil("# Mur",     '#', Color.SADDLEBROWN, imgWall);
        ToggleButton btnJoueur = creerBoutonOutil("@ Joueur",  '@', Color.DODGERBLUE,  imgPlayer);
        ToggleButton btnBoite  = creerBoutonOutil("O Boîte",   'O', Color.ORANGE,      imgBox);
        ToggleButton btnPortal = creerBoutonOutil("P Portail", 'P', Color.GOLD,        imgPortal);
        ToggleButton btnCible  = creerBoutonOutil("x Cible",   'x', Color.LIMEGREEN,   imgTarget);
        ToggleButton btnSortie = creerBoutonOutil("e Sortie",  'e', Color.MEDIUMPURPLE, null);
        ToggleButton btnGomme  = creerBoutonOutil("  Gomme",   ' ', Color.GRAY,        null);
        btnMur.setSelected(true);

        Label info = new Label("Clic gauche : placer\nClic droit : effacer\nWASD/flèches : curseur");
        info.setTextFill(Color.LIGHTGRAY);
        info.setFont(Font.font("Monospaced", 10));
        info.setWrapText(true);
        info.setPadding(new Insets(8, 0, 0, 0));

        palette.getChildren().addAll(
            title, new Separator(),
            btnMur, btnJoueur, btnBoite, btnPortal, btnCible, btnSortie, btnGomme,
            new Separator(), info
        );
        return palette;
    }

    // crée un bouton toggle pour la palette d'outils avec image ou indicateur coloré
    private ToggleButton creerBoutonOutil(String text, char tool, Color couleur, Image img) {
        ToggleButton btn = new ToggleButton(text);
        btn.setToggleGroup(toolGroup);
        btn.setPrefWidth(120);
        btn.setFont(Font.font("Impact", 13));

        String baseStyle =
            "-fx-background-color: linear-gradient(to bottom,#400000,#1a0000);" +
            "-fx-text-fill: white; -fx-border-color: #aa0000; -fx-border-width: 2;" +
            "-fx-background-radius: 8; -fx-border-radius: 8; -fx-padding: 6 8 6 8;";
        String selectedStyle =
            "-fx-background-color: linear-gradient(to bottom,#770000,#330000);" +
            "-fx-text-fill: #ffdddd; -fx-border-color: #ff4444; -fx-border-width: 2;" +
            "-fx-background-radius: 8; -fx-border-radius: 8; -fx-padding: 6 8 6 8;";

        btn.setStyle(baseStyle);
        btn.selectedProperty().addListener((obs, was, is) ->
            btn.setStyle(is ? selectedStyle : baseStyle));

        if (img != null) {
            ImageView iv = new ImageView(img);
            iv.setFitWidth(20);
            iv.setFitHeight(20);
            iv.setPreserveRatio(true);
            btn.setGraphic(iv);
        } else {
            Region indicator = new Region();
            indicator.setPrefSize(12, 12);
            indicator.setStyle("-fx-background-color: " + colorToHex(couleur) + "; -fx-background-radius: 2;");
            btn.setGraphic(indicator);
        }

        btn.setOnAction(e -> {
            currentTool = tool;
            statusLabel.setText("Outil : " + text.trim());
        });
        return btn;
    }

    /*--------------------------------------------------
                METHODES — INTERACTIONS
    --------------------------------------------------*/

    // gère un clic ou glissement souris sur le canvas
    private void onMouseClick(double mx, double my, MouseButton button) {
        int col = (int) (mx / CELL_SIZE);
        int row = (int) (my / CELL_SIZE);
        if (row < 0 || row >= editor.getRows() || col < 0 || col >= editor.getCols()) return;

        editor.setCell(row, col, button == MouseButton.SECONDARY ? ' ' : currentTool);
        editor.setCursor(row, col);
        redrawGrid();
        statusLabel.setText("(" + row + ", " + col + ") -> '" + editor.getCell(row, col) + "'");
    }

    // gère les touches clavier (déplacement curseur, espace/suppr)
    private void onKeyPress(KeyEvent e) {
        int dr = 0, dc = 0;
        switch (e.getCode()) {
            case W: case UP:    dr = -1; break;
            case S: case DOWN:  dr =  1; break;
            case A: case LEFT:  dc = -1; break;
            case D: case RIGHT: dc =  1; break;
            case SPACE:
                editor.setCell(editor.getCursorRow(), editor.getCursorCol(), currentTool);
                redrawGrid(); return;
            case DELETE: case BACK_SPACE:
                editor.setCell(editor.getCursorRow(), editor.getCursorCol(), ' ');
                redrawGrid(); return;
            default: return;
        }
        int nr = editor.getCursorRow() + dr;
        int nc = editor.getCursorCol() + dc;
        if (nr >= 0 && nr < editor.getRows() && nc >= 0 && nc < editor.getCols()) {
            editor.setCursor(nr, nc);
            redrawGrid();
            statusLabel.setText("Curseur : (" + nr + ", " + nc + ")  |  '" + editor.getCell(nr, nc) + "'");
        }
        e.consume();
    }

    /*--------------------------------------------------
                METHODES — DESSIN CANVAS
    --------------------------------------------------*/

    // redessine toute la grille sur le canvas
    private void redrawGrid() {
        int rows = editor.getRows();
        int cols = editor.getCols();
        canvas.setWidth(cols  * CELL_SIZE);
        canvas.setHeight(rows * CELL_SIZE);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double x    = j * CELL_SIZE;
                double y    = i * CELL_SIZE;
                char   cell = editor.getCell(i, j);

                if (imgFloor != null) gc.drawImage(imgFloor, x, y, CELL_SIZE, CELL_SIZE);
                else { gc.setFill(Color.rgb(45, 45, 45)); gc.fillRect(x, y, CELL_SIZE, CELL_SIZE); }

                drawCell(gc, cell, x, y);

                gc.setStroke(Color.rgb(60, 60, 60));
                gc.setLineWidth(0.5);
                gc.strokeRect(x, y, CELL_SIZE, CELL_SIZE);
            }
        }

        // curseur doré
        int cr = editor.getCursorRow();
        int cc = editor.getCursorCol();
        gc.setStroke(Color.web("#D8B35A"));
        gc.setLineWidth(3);
        gc.strokeRect(cc * CELL_SIZE + 2, cr * CELL_SIZE + 2, CELL_SIZE - 4, CELL_SIZE - 4);
    }

    // dessine l'image ou le fallback coloré pour un caractère de cellule
    private void drawCell(GraphicsContext gc, char c, double x, double y) {
        Image img = null;
        switch (c) {
            case '#': img = imgWall;   break;
            case '@': img = imgPlayer; break;
            case 'O': img = imgBox;    break;
            case 'x': img = imgTarget; break;
            case 'P': img = imgPortal; break;
            case 'e':
                gc.setFill(Color.rgb(130, 80, 200));
                gc.fillRect(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4);
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 20));
                gc.fillText("e", x + CELL_SIZE / 2.0 - 6, y + CELL_SIZE / 2.0 + 7);
                return;
            default: return;
        }
        if (img != null) gc.drawImage(img, x, y, CELL_SIZE, CELL_SIZE);
        else {
            gc.setFill(couleurCase(c));
            gc.fillRect(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 20));
            gc.fillText(String.valueOf(c), x + CELL_SIZE / 2.0 - 6, y + CELL_SIZE / 2.0 + 7);
        }
    }

    // retourne la couleur de fallback pour un caractère de cellule
    private Color couleurCase(char c) {
        switch (c) {
            case '#': return Color.rgb(101, 67,  33);
            case '@': return Color.rgb(30,  100, 200);
            case 'O': return Color.rgb(200, 130, 30);
            case 'P': return Color.rgb(220, 180, 40);
            case 'x': return Color.rgb(40,  160, 60);
            case 'e': return Color.rgb(130, 80,  200);
            default:  return Color.rgb(45,  45,  45);
        }
    }

    /*--------------------------------------------------
                METHODES — DIALOGUES
    --------------------------------------------------*/

    // dialogue pour créer une nouvelle grille (taille + nom du monde)
    private void dialogNouveau() {
        Dialog<int[]> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle grille");
        dialog.setHeaderText("Créer une grille vide");

        TextField rowsF = new TextField("10");
        TextField colsF = new TextField("10");
        TextField nameF = new TextField("A");

        GridPane gp = new GridPane();
        gp.setHgap(10); gp.setVgap(10);
        gp.add(new Label("Lignes :"),   0, 0); gp.add(rowsF, 1, 0);
        gp.add(new Label("Colonnes :"), 0, 1); gp.add(colsF, 1, 1);
        gp.add(new Label("Monde :"),    0, 2); gp.add(nameF, 1, 2);
        dialog.getDialogPane().setContent(gp);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    return new int[]{
                        Integer.parseInt(rowsF.getText().trim()),
                        Integer.parseInt(colsF.getText().trim()),
                        nameF.getText().trim().charAt(0)
                    };
                } catch (Exception ex) { return null; }
            }
            return null;
        });
        dialog.showAndWait().ifPresent(res -> {
            try {
                editor.resize(res[0], res[1], (char) res[2]);
                redrawGrid();
                statusLabel.setText("Grille " + res[0] + "x" + res[1] + " créée.");
            } catch (IllegalArgumentException ex) { erreur("Taille minimale : 5x5"); }
        });
    }

    // dialogue pour sauvegarder dans levels/personnalized/levelN/worldN.txt
    private void dialogSave() {
        Dialog<int[]> dialog = new Dialog<>();
        dialog.setTitle("Sauvegarder");
        dialog.setHeaderText("Sauvegarder dans levels/personnalized/levelN/worldN.txt");

        TextField lvlF = new TextField("1");
        TextField wF   = new TextField("0");
        TextField totF = new TextField("1");

        GridPane gp = new GridPane();
        gp.setHgap(10); gp.setVgap(10);
        gp.add(new Label("N° level :"),     0, 0); gp.add(lvlF, 1, 0);
        gp.add(new Label("Index monde :"),  0, 1); gp.add(wF,   1, 1);
        gp.add(new Label("Total mondes :"), 0, 2); gp.add(totF, 1, 2);
        dialog.getDialogPane().setContent(gp);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    return new int[]{
                        Integer.parseInt(lvlF.getText().trim()),
                        Integer.parseInt(wF.getText().trim()),
                        Integer.parseInt(totF.getText().trim())
                    };
                } catch (Exception ex) { return null; }
            }
            return null;
        });
        dialog.showAndWait().ifPresent(res -> {
            editor.saveToProjectFile(res[0], res[1], res[2]);
            ArrayList<String> warnings = LevelProjectValidator.validateProjectLevel(
                    new File("levels/personnalized/level" + res[0]));
            if (warnings.isEmpty())
                statusLabel.setText("Sauvé et level valide : level" + res[0] + "/world" + res[1]);
            else
                statusLabel.setText("Sauvé avec " + warnings.size() + " avertissement(s) level.");
        });
    }

    // dialogue pour charger depuis levels/personnalized/levelN/worldN.txt
    private void dialogLoad() {
        Dialog<int[]> dialog = new Dialog<>();
        dialog.setTitle("Charger");
        dialog.setHeaderText("Charger depuis levels/personnalized/levelN/worldN.txt");

        TextField lvlF = new TextField("1");
        TextField wF   = new TextField("0");

        GridPane gp = new GridPane();
        gp.setHgap(10); gp.setVgap(10);
        gp.add(new Label("N° level :"),    0, 0); gp.add(lvlF, 1, 0);
        gp.add(new Label("Index monde :"), 0, 1); gp.add(wF,   1, 1);
        dialog.getDialogPane().setContent(gp);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    return new int[]{
                        Integer.parseInt(lvlF.getText().trim()),
                        Integer.parseInt(wF.getText().trim())
                    };
                } catch (Exception ex) { return null; }
            }
            return null;
        });
        dialog.showAndWait().ifPresent(res -> {
            if (editor.loadFromProjectFile(res[0], res[1])) {
                redrawGrid();
                statusLabel.setText("Chargé level" + res[0] + "/world" + res[1]);
            } else {
                erreur("Fichier introuvable.");
            }
        });
    }

    // exporte la grille dans un fichier .txt via FileChooser
    private void exporterFichier(Stage stage) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Exporter");
        fc.setInitialFileName("world.txt");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texte", "*.txt"));
        File file = fc.showSaveDialog(stage);
        if (file != null) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                pw.print(editor.exportProjectFormat());
                statusLabel.setText("Exporté : " + file.getName());
            } catch (IOException ex) { erreur("Export impossible : " + ex.getMessage()); }
        }
    }

    // importe une grille depuis un fichier .txt via FileChooser
    private void importerFichier(Stage stage) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Importer");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texte", "*.txt"));
        File file = fc.showOpenDialog(stage);
        if (file != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                StringBuilder sb = new StringBuilder();
                String        line;
                while ((line = br.readLine()) != null) sb.append(line).append("\n");
                String content   = sb.toString();
                String firstLine = content.split("\\r?\\n")[0].trim();
                if (firstLine.matches("[A-Za-z]\\s+\\d+"))
                    editor.importLevel(content);
                else
                    editor.importProjectFormat(content);
                redrawGrid();
                statusLabel.setText("Importé : " + file.getName());
            } catch (IOException ex) { erreur("Import impossible : " + ex.getMessage()); }
        }
    }

    // affiche le résultat de la validation du monde courant
    private void montrerValidation() {
        ArrayList<String> warnings = editor.validate();
        if (warnings.isEmpty()) {
            showScrollableAlert(Alert.AlertType.INFORMATION,
                "Validation monde", "Monde valide !",
                "Joueur présent, boîtes et cibles en nombre égal.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (String w : warnings) sb.append("• ").append(w).append("\n");
            showScrollableAlert(Alert.AlertType.WARNING,
                "Validation monde", "Problèmes trouvés dans ce monde :", sb.toString());
        }
    }

    // dialogue pour valider un niveau complet (tous ses mondes)
    private void dialogValidationLevel() {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Valider un level");
        dialog.setHeaderText("Vérifier la cohérence globale du niveau");
        TextField lvlF = new TextField("1");
        GridPane  gp   = new GridPane();
        gp.setHgap(10); gp.setVgap(10);
        gp.add(new Label("N° level :"), 0, 0); gp.add(lvlF, 1, 0);
        dialog.getDialogPane().setContent(gp);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(btn ->
            btn == ButtonType.OK ? Integer.parseInt(lvlF.getText().trim()) : null);

        dialog.showAndWait().ifPresent(levelNum -> {
            ArrayList<String> warnings = LevelProjectValidator.validateProjectLevel(
                    new File("levels/personnalized/level" + levelNum));
            if (warnings.isEmpty()) {
                showScrollableAlert(Alert.AlertType.INFORMATION,
                    "Validation level", "Level " + levelNum + " valide !",
                    "Distribution des mondes par PortalBox et targets cohérente.");
            } else {
                StringBuilder sb = new StringBuilder();
                for (String w : warnings) sb.append("• ").append(w).append("\n");
                showScrollableAlert(Alert.AlertType.WARNING,
                    "Validation level", "Problèmes trouvés dans level " + levelNum + " :", sb.toString());
            }
        });
    }

    // dialogue pour afficher l'arbre de distribution DFS d'un niveau
    private void dialogRapportLevel() {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Arbre du level");
        dialog.setHeaderText("Afficher la distribution DFS des mondes");
        TextField lvlF = new TextField("1");
        GridPane  gp   = new GridPane();
        gp.setHgap(10); gp.setVgap(10);
        gp.add(new Label("N° level :"), 0, 0); gp.add(lvlF, 1, 0);
        dialog.getDialogPane().setContent(gp);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(btn ->
            btn == ButtonType.OK ? Integer.parseInt(lvlF.getText().trim()) : null);

        dialog.showAndWait().ifPresent(levelNum -> {
            String   report = LevelProjectValidator.buildDistributionReport(
                    new File("levels/personnalized/level" + levelNum));
            Alert    alert  = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Arbre du level");
            alert.setHeaderText("Distribution des mondes");
            TextArea area = new TextArea(report);
            area.setEditable(false);
            area.setWrapText(false);
            area.setPrefColumnCount(40);
            area.setPrefRowCount(20);
            alert.getDialogPane().setContent(area);
            alert.showAndWait();
        });
    }

    /*--------------------------------------------------
                METHODES — UTILITAIRES
    --------------------------------------------------*/

    // remplit toute la bordure de la grille avec des murs
    private void remplirBordures() {
        for (int i = 0; i < editor.getRows(); i++)
            for (int j = 0; j < editor.getCols(); j++)
                if (i == 0 || j == 0 || i == editor.getRows() - 1 || j == editor.getCols() - 1)
                    editor.setCell(i, j, '#');
    }

    // affiche un message d'erreur dans une Alert scrollable
    private void erreur(String msg) {
        showScrollableAlert(Alert.AlertType.ERROR, "Erreur", null, msg);
    }

    // affiche une Alert avec un TextArea scrollable pour les messages longs
    private void showScrollableAlert(Alert.AlertType type, String title, String header, String body) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.getDialogPane().setPrefSize(580, 380);

        TextArea area = new TextArea(body);
        area.setEditable(false);
        area.setWrapText(true);
        area.setMaxWidth(Double.MAX_VALUE);
        area.setMaxHeight(Double.MAX_VALUE);
        area.setStyle("-fx-font-family: Monospaced; -fx-font-size: 12px;");

        GridPane.setVgrow(area, javafx.scene.layout.Priority.ALWAYS);
        GridPane.setHgrow(area, javafx.scene.layout.Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(area, 0, 0);
        alert.getDialogPane().setContent(expContent);
        alert.showAndWait();
    }

    // ferme l'éditeur et restaure la scène principale du jeu
    private void retournerAuJeu(Stage editorStage) {
        if (mainStage != null) {
            mainStage.setScene(sokoban.UI.app.SokobanApp.sceneInterface);
            mainStage.setTitle("Interface");
            mainStage.setResizable(true);
            mainStage.show();
        }
        editorStage.close();
    }

    // crée un bouton standard avec la classe CSS du jeu
    private Button makeButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("button");
        return btn;
    }

    // convertit une Color JavaFX en chaîne hexadécimale CSS
    private String colorToHex(Color c) {
        return String.format("#%02x%02x%02x",
                (int)(c.getRed()*255), (int)(c.getGreen()*255), (int)(c.getBlue()*255));
    }

    public static void main(String[] args) { launch(args); }
}
