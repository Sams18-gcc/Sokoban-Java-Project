package sokoban.editor;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;

public class EditorGUI extends Application {

    private static final int CELL_SIZE = 48;

    private LevelEditor editor;
    private Canvas canvas;
    private char currentTool = '#'; // outil par defaut = mur
    private Label statusLabel;
    private ToggleGroup toolGroup;

    @Override
    public void start(Stage primaryStage) {
        editor = new LevelEditor(10, 10, 'A');

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #2b2b2b;");

        // barre en haut
        HBox topBar = createTopBar(primaryStage);
        root.setTop(topBar);

        // palette outils a gauche
        VBox toolPalette = createToolPalette();
        root.setLeft(toolPalette);

        // canvas au centre
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
        statusLabel.setTextFill(Color.LIGHTGRAY);
        statusLabel.setPadding(new Insets(8));
        HBox statusBar = new HBox(statusLabel);
        statusBar.setStyle("-fx-background-color: #333333;");
        root.setBottom(statusBar);

        // events souris
        canvas.setOnMousePressed(e -> onMouseClick(e.getX(), e.getY(), e.getButton()));
        canvas.setOnMouseDragged(e -> onMouseClick(e.getX(), e.getY(), e.getButton()));

        redrawGrid();

        Scene scene = new Scene(root, 900, 650);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> onKeyPress(e));

        primaryStage.setTitle("Éditeur de Plateau Sokoban");
        primaryStage.setScene(scene);
        primaryStage.show();
        canvas.requestFocus();
    }

    // === barre du haut ===

    private HBox createTopBar(Stage stage) {
        HBox bar = new HBox(8);
        bar.setPadding(new Insets(8));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: #3c3f41;");

        Button btnNew = makeButton("Nouveau");
        btnNew.setOnAction(e -> dialogNouveau());

        Button btnSave = makeButton("Sauvegarder");
        btnSave.setOnAction(e -> dialogSave());

        Button btnLoad = makeButton("Charger");
        btnLoad.setOnAction(e -> dialogLoad());

        Button btnExport = makeButton("Exporter (.txt)");
        btnExport.setOnAction(e -> exporterFichier(stage));

        Button btnImport = makeButton("Importer (.txt)");
        btnImport.setOnAction(e -> importerFichier(stage));

        Button btnValid = makeButton("Valider");
        btnValid.setStyle(btnValid.getStyle() + "-fx-background-color: #4a7c59;");
        btnValid.setOnAction(e -> montrerValidation());

        Button btnBordure = makeButton("Murs bordure");
        btnBordure.setOnAction(e -> {
            remplirBordures();
            redrawGrid();
            statusLabel.setText("Bordures remplies.");
        });

        bar.getChildren().addAll(btnNew, btnSave, btnLoad,
                new Separator(), btnExport, btnImport,
                new Separator(), btnValid, btnBordure);
        return bar;
    }

    // === palette outils ===

    private VBox createToolPalette() {
        VBox palette = new VBox(6);
        palette.setPadding(new Insets(10));
        palette.setAlignment(Pos.TOP_CENTER);
        palette.setStyle("-fx-background-color: #3c3f41;");
        palette.setPrefWidth(130);

        Label title = new Label("Outils");
        title.setFont(Font.font("System", FontWeight.BOLD, 14));
        title.setTextFill(Color.WHITE);

        toolGroup = new ToggleGroup();

        ToggleButton btnMur    = creerBoutonOutil("# Mur",    '#', Color.SADDLEBROWN);
        ToggleButton btnJoueur = creerBoutonOutil("@ Joueur", '@', Color.DODGERBLUE);
        ToggleButton btnBoite  = creerBoutonOutil("O Boîte",  'O', Color.ORANGE);
        ToggleButton btnCible  = creerBoutonOutil("x Cible",  'x', Color.LIMEGREEN);
        ToggleButton btnSortie = creerBoutonOutil("e Sortie", 'e', Color.MEDIUMPURPLE);
        ToggleButton btnGomme  = creerBoutonOutil("  Gomme",  ' ', Color.GRAY);

        btnMur.setSelected(true);

        palette.getChildren().addAll(title, new Separator(),
                btnMur, btnJoueur, btnBoite, btnCible, btnSortie, btnGomme,
                new Separator());

        Label info = new Label("Clic gauche : placer\nClic droit : effacer\nWASD/fleches : curseur");
        info.setTextFill(Color.LIGHTGRAY);
        info.setFont(Font.font("Monospaced", 10));
        info.setWrapText(true);
        palette.getChildren().add(info);

        return palette;
    }

    private ToggleButton creerBoutonOutil(String text, char tool, Color couleur) {
        ToggleButton btn = new ToggleButton(text);
        btn.setToggleGroup(toolGroup);
        btn.setPrefWidth(110);
        btn.setFont(Font.font("Monospaced", FontWeight.BOLD, 12));
        btn.setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-background-radius: 4;");
        btn.setOnAction(e -> {
            currentTool = tool;
            statusLabel.setText("Outil : " + text.trim());
        });

        Region indicator = new Region();
        indicator.setPrefSize(12, 12);
        indicator.setStyle("-fx-background-color: " + colorToHex(couleur) + "; -fx-background-radius: 2;");
        btn.setGraphic(indicator);
        return btn;
    }

    // gestion clic souris sur le canvas
    private void onMouseClick(double mx, double my, MouseButton button) {
        int col = (int) (mx / CELL_SIZE);
        int row = (int) (my / CELL_SIZE);
        if (row < 0 || row >= editor.getRows() || col < 0 || col >= editor.getCols())
            return;

        if (button == MouseButton.SECONDARY)
            editor.setCell(row, col, ' '); // clic droit = effacer
        else
            editor.setCell(row, col, currentTool);

        editor.setCursor(row, col);
        redrawGrid();
        statusLabel.setText("(" + row + ", " + col + ") -> '" + editor.getCell(row, col) + "'");
    }

    // gestion clavier
    private void onKeyPress(KeyEvent e) {
        int dr = 0, dc = 0;
        switch (e.getCode()) {
            case W: case UP:    dr = -1; break;
            case S: case DOWN:  dr = 1; break;
            case A: case LEFT:  dc = -1; break;
            case D: case RIGHT: dc = 1; break;
            case SPACE:
                editor.setCell(editor.getCursorRow(), editor.getCursorCol(), currentTool);
                redrawGrid();
                return;
            case DELETE: case BACK_SPACE:
                editor.setCell(editor.getCursorRow(), editor.getCursorCol(), ' ');
                redrawGrid();
                return;
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

    // dessine toute la grille
    private void redrawGrid() {
        int rows = editor.getRows();
        int cols = editor.getCols();

        canvas.setWidth(cols * CELL_SIZE);
        canvas.setHeight(rows * CELL_SIZE);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double x = j * CELL_SIZE;
                double y = i * CELL_SIZE;
                char cell = editor.getCell(i, j);

                // couleur de fond
                gc.setFill(couleurCase(cell));
                gc.fillRect(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2);

                // symbole
                if (cell != ' ') {
                    gc.setFill(Color.WHITE);
                    gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 20));
                    String sym = "";
                    switch(cell) {
                        case '#': sym = "#"; break;
                        case '@': sym = "@"; break;
                        case 'O': sym = "O"; break;
                        case 'x': sym = "x"; break;
                        case 'e': sym = "e"; break;
                    }
                    gc.fillText(sym, x + CELL_SIZE / 2.0 - 6, y + CELL_SIZE / 2.0 + 7);
                }

                // lignes de la grille
                gc.setStroke(Color.rgb(60, 60, 60));
                gc.setLineWidth(0.5);
                gc.strokeRect(x, y, CELL_SIZE, CELL_SIZE);
            }
        }

        // curseur jaune
        int cr = editor.getCursorRow();
        int cc = editor.getCursorCol();
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(3);
        gc.strokeRect(cc * CELL_SIZE + 2, cr * CELL_SIZE + 2, CELL_SIZE - 4, CELL_SIZE - 4);
    }

    private Color couleurCase(char c) {
        switch (c) {
            case '#': return Color.rgb(101, 67, 33);
            case '@': return Color.rgb(30, 100, 200);
            case 'O': return Color.rgb(200, 130, 30);
            case 'x': return Color.rgb(40, 160, 60);
            case 'e': return Color.rgb(130, 80, 200);
            default: return Color.rgb(45, 45, 45);
        }
    }

    // === dialogues ===

    private void dialogNouveau() {
        Dialog<int[]> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle grille");
        dialog.setHeaderText("Créer une grille vide");

        TextField rowsF = new TextField("10");
        TextField colsF = new TextField("10");
        TextField nameF = new TextField("A");

        GridPane gp = new GridPane();
        gp.setHgap(10); gp.setVgap(10);
        gp.add(new Label("Lignes :"), 0, 0); gp.add(rowsF, 1, 0);
        gp.add(new Label("Colonnes :"), 0, 1); gp.add(colsF, 1, 1);
        gp.add(new Label("Monde :"), 0, 2); gp.add(nameF, 1, 2);
        dialog.getDialogPane().setContent(gp);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    int r = Integer.parseInt(rowsF.getText().trim());
                    int c = Integer.parseInt(colsF.getText().trim());
                    return new int[]{r, c, nameF.getText().trim().charAt(0)};
                } catch (Exception ex) { return null; }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(res -> {
            try {
                editor.resize(res[0], res[1], (char) res[2]);
                redrawGrid();
                statusLabel.setText("Grille " + res[0] + "x" + res[1] + " créée.");
            } catch (IllegalArgumentException ex) {
                erreur("Taille minimale : 5x5");
            }
        });
    }

    private void dialogSave() {
        Dialog<int[]> dialog = new Dialog<>();
        dialog.setTitle("Sauvegarder");
        dialog.setHeaderText("Sauvegarder dans levels/levelN/worldN.txt");

        TextField lvlF = new TextField("1");
        TextField wF = new TextField("0");
        TextField totF = new TextField("1");

        GridPane gp = new GridPane();
        gp.setHgap(10); gp.setVgap(10);
        gp.add(new Label("N° level :"), 0, 0); gp.add(lvlF, 1, 0);
        gp.add(new Label("Index monde :"), 0, 1); gp.add(wF, 1, 1);
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
            statusLabel.setText("Sauvé dans levels/level" + res[0] + "/world" + res[1] + ".txt");
        });
    }

    private void dialogLoad() {
        Dialog<int[]> dialog = new Dialog<>();
        dialog.setTitle("Charger");
        dialog.setHeaderText("Charger depuis levels/levelN/worldN.txt");

        TextField lvlF = new TextField("1");
        TextField wF = new TextField("0");

        GridPane gp = new GridPane();
        gp.setHgap(10); gp.setVgap(10);
        gp.add(new Label("N° level :"), 0, 0); gp.add(lvlF, 1, 0);
        gp.add(new Label("Index monde :"), 0, 1); gp.add(wF, 1, 1);
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
            } catch (IOException ex) {
                erreur("Export impossible : " + ex.getMessage());
            }
        }
    }

    private void importerFichier(Stage stage) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Importer");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texte", "*.txt"));
        File file = fc.showOpenDialog(stage);
        if (file != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line).append("\n");

                String content = sb.toString();
                String firstLine = content.split("\\r?\\n")[0].trim();
                // detecter si cest le format standard (avec header lettre + taille) ou pas
                if (firstLine.matches("[A-Za-z]\\s+\\d+"))
                    editor.importLevel(content);
                else
                    editor.importProjectFormat(content);

                redrawGrid();
                statusLabel.setText("Importé : " + file.getName());
            } catch (IOException ex) {
                erreur("Import impossible : " + ex.getMessage());
            }
        }
    }

    private void montrerValidation() {
        ArrayList<String> warnings = editor.validate();
        Alert alert;
        if (warnings.isEmpty()) {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Validation");
            alert.setHeaderText("Niveau valide !");
            alert.setContentText("Joueur present, boîtes et cibles en nombre egal.");
        } else {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation");
            alert.setHeaderText("Problèmes trouvés");
            StringBuilder sb = new StringBuilder();
            for (String w : warnings) sb.append("- ").append(w).append("\n");
            alert.setContentText(sb.toString());
        }
        alert.showAndWait();
    }

    // utils

    private void remplirBordures() {
        for (int i = 0; i < editor.getRows(); i++)
            for (int j = 0; j < editor.getCols(); j++)
                if (i == 0 || j == 0 || i == editor.getRows() - 1 || j == editor.getCols() - 1)
                    editor.setCell(i, j, '#');
    }

    private void erreur(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Erreur");
        a.setContentText(msg);
        a.showAndWait();
    }

    private Button makeButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-background-radius: 4; -fx-padding: 6 12;");
        btn.setFont(Font.font("System", 12));
        return btn;
    }

    private String colorToHex(Color c) {
        return String.format("#%02x%02x%02x",
                (int)(c.getRed()*255), (int)(c.getGreen()*255), (int)(c.getBlue()*255));
    }

    public static void main(String[] args) {
        launch(args);
    }
}