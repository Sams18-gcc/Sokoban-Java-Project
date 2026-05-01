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

    
    private static final String BG_ROOT       = "#120606"; // fond general - noir profond teinte rouge
    private static final String BG_CANVAS     = "#0a0303"; // fond canvas - presque noir
    private static final String BG_SURFACE    = "#1f1010"; // barres et palette
    private static final String BG_BTN        = "#2d2020"; // boutons style "carte" sombre
    private static final String BG_BTN_HOVER  = "#3d2828"; // hover boutons
    private static final String BG_STATUS     = "#180a0a"; // barre de statut
    private static final String BORDER_COL    = "#4a1818"; // bordures rouge sombre
    private static final String TEXT_MAIN     = "#f0e0d8"; // blanc casse chaud
    private static final String TEXT_DIM      = "#a08585"; // rouge delave (texte secondaire)
    private static final String TITLE_RED     = "#dc143c"; // rouge sang vif (titre)
    private static final String ACCENT_RED    = "#a01818"; // accent principal (sauvegarder/valider)
    private static final String ACCENT_HOVER  = "#c42020"; // hover accent
    private static final String ACCENT_SEL    = "#dc143c"; // bordure outil selectionne

    private LevelEditor editor;
    private Canvas canvas;
    private char currentTool = '#';
    private Label statusLabel;
    private ToggleGroup toolGroup;

    @Override
    public void start(Stage primaryStage) {
        editor = new LevelEditor(10, 10, 'A');

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_ROOT + ";");

       
        VBox topZone = new VBox();
        topZone.getChildren().addAll(createTitleBar(), createTopBar(primaryStage));
        root.setTop(topZone);

        VBox toolPalette = createToolPalette();
        root.setLeft(toolPalette);

        canvas = new Canvas(editor.getCols() * CELL_SIZE, editor.getRows() * CELL_SIZE);
        StackPane canvasContainer = new StackPane(canvas);
        canvasContainer.setStyle(
                "-fx-background-color: " + BG_CANVAS + ";" +
                "-fx-border-color: " + BORDER_COL + ";" +
                "-fx-border-width: 1;"
        );
        canvasContainer.setPadding(new Insets(16));

        ScrollPane scrollPane = new ScrollPane(canvasContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle(
                "-fx-background: " + BG_CANVAS + ";" +
                "-fx-background-color: " + BG_CANVAS + ";" +
                "-fx-border-color: transparent;"
        );
        root.setCenter(scrollPane);

        
        statusLabel = new Label("PRET.");
        statusLabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 11));
        statusLabel.setTextFill(Color.web(TEXT_DIM));

        Label statusDot = new Label("●");
        statusDot.setTextFill(Color.web(ACCENT_SEL));
        statusDot.setFont(Font.font("System", 10));

        HBox statusBar = new HBox(8, statusDot, statusLabel);
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setPadding(new Insets(8, 14, 8, 14));
        statusBar.setStyle(
                "-fx-background-color: " + BG_STATUS + ";" +
                "-fx-border-color: " + BORDER_COL + " transparent transparent transparent;" +
                "-fx-border-width: 1 0 0 0;"
        );
        root.setBottom(statusBar);

        canvas.setOnMousePressed(e -> onMouseClick(e.getX(), e.getY(), e.getButton()));
        canvas.setOnMouseDragged(e -> onMouseClick(e.getX(), e.getY(), e.getButton()));

        redrawGrid();

        Scene scene = new Scene(root, 980, 720);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> onKeyPress(e));

        primaryStage.setTitle("Sokoban — Editeur de Plateau");
        primaryStage.setScene(scene);
        primaryStage.show();
        canvas.requestFocus();
    }

    
    private HBox createTitleBar() {
        HBox titleBar = new HBox();
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setPadding(new Insets(14, 22, 12, 22));
        titleBar.setStyle(
                "-fx-background-color: " + BG_ROOT + ";" +
                "-fx-border-color: transparent transparent " + ACCENT_RED + " transparent;" +
                "-fx-border-width: 0 0 2 0;"
        );

        Label title = new Label("ÉDITEUR");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 30));
        title.setTextFill(Color.web(TITLE_RED));
        title.setStyle("-fx-letter-spacing: 4px;");

        Label subtitle = new Label("DE NIVEAUX");
        subtitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 18));
        subtitle.setTextFill(Color.web(TEXT_DIM));
        subtitle.setStyle("-fx-letter-spacing: 3px;");
        subtitle.setPadding(new Insets(8, 0, 0, 12));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label deco = new Label("[ MODE LIBRE ]");
        deco.setFont(Font.font("Monospaced", FontWeight.BOLD, 12));
        deco.setTextFill(Color.web(ACCENT_RED));
        deco.setStyle("-fx-letter-spacing: 2px;");

        titleBar.getChildren().addAll(title, subtitle, spacer, deco);
        return titleBar;
    }

    private HBox createTopBar(Stage stage) {
        HBox bar = new HBox(6);
        bar.setPadding(new Insets(10, 14, 10, 14));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle(
                "-fx-background-color: " + BG_SURFACE + ";" +
                "-fx-border-color: transparent transparent " + BORDER_COL + " transparent;" +
                "-fx-border-width: 0 0 1 0;"
        );

        Button btnNew    = makeButton("NOUVEAU", false);
        btnNew.setOnAction(e -> dialogNouveau());

        Button btnSave   = makeButton("SAUVEGARDER", true);
        btnSave.setOnAction(e -> dialogSave());

        Button btnLoad   = makeButton("CHARGER", false);
        btnLoad.setOnAction(e -> dialogLoad());

        Button btnImport = makeButton("IMPORTER (.TXT)", false);
        btnImport.setOnAction(e -> importerFichier(stage));

        Button btnValid  = makeButton("VALIDER", true);
        btnValid.setOnAction(e -> montrerValidation());

        Button btnBordure = makeButton("MURS BORDURE", false);
        btnBordure.setOnAction(e -> {
            remplirBordures();
            redrawGrid();
            statusLabel.setText("BORDURES REMPLIES.");
        });

        bar.getChildren().addAll(
                btnNew, btnSave, btnLoad,
                separateurVertical(),
                btnImport,
                separateurVertical(),
                btnValid, btnBordure
        );
        return bar;
    }

    private VBox createToolPalette() {
        VBox palette = new VBox(8);
        palette.setPadding(new Insets(18, 12, 16, 12));
        palette.setAlignment(Pos.TOP_CENTER);
        palette.setPrefWidth(155);
        palette.setStyle(
                "-fx-background-color: " + BG_SURFACE + ";" +
                "-fx-border-color: transparent " + BORDER_COL + " transparent transparent;" +
                "-fx-border-width: 0 1 0 0;"
        );

        Label title = new Label("OUTILS");
        title.setFont(Font.font("Monospaced", FontWeight.BOLD, 12));
        title.setTextFill(Color.web(TITLE_RED));
        title.setStyle("-fx-letter-spacing: 3px;");
        title.setPadding(new Insets(0, 0, 6, 0));

        toolGroup = new ToggleGroup();

        ToggleButton btnMur    = creerBoutonOutil("MUR",    '#', Color.SADDLEBROWN);
        ToggleButton btnJoueur = creerBoutonOutil("JOUEUR", '@', Color.DODGERBLUE);
        ToggleButton btnBoite  = creerBoutonOutil("BOITE",  'O', Color.ORANGE);
        ToggleButton btnCible  = creerBoutonOutil("CIBLE",  'x', Color.LIMEGREEN);
        ToggleButton btnSortie = creerBoutonOutil("SORTIE", 'e', Color.MEDIUMPURPLE);
        ToggleButton btnGomme  = creerBoutonOutil("GOMME",  ' ', Color.GRAY);

        btnMur.setSelected(true);
        appliquerStyleOutil(btnMur, true);

        Region sep = new Region();
        sep.setPrefHeight(1);
        sep.setMaxWidth(Double.MAX_VALUE);
        sep.setStyle("-fx-background-color: " + BORDER_COL + ";");
        VBox.setMargin(sep, new Insets(10, 4, 10, 4));

        Label aide = new Label("CLIC GAUCHE : PLACER\nCLIC DROIT : EFFACER\nWASD : CURSEUR");
        aide.setTextFill(Color.web(TEXT_DIM));
        aide.setFont(Font.font("Monospaced", 9.5));
        aide.setWrapText(true);
        aide.setStyle("-fx-line-spacing: 3px;");

        palette.getChildren().addAll(
                title,
                btnMur, btnJoueur, btnBoite, btnCible, btnSortie, btnGomme,
                sep,
                aide
        );
        return palette;
    }

    private ToggleButton creerBoutonOutil(String text, char tool, Color couleur) {
        ToggleButton btn = new ToggleButton(text);
        btn.setToggleGroup(toolGroup);
        btn.setPrefWidth(130);
        btn.setPrefHeight(38);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setFont(Font.font("Monospaced", FontWeight.BOLD, 11));
        appliquerStyleOutil(btn, false);

        Region indicator = new Region();
        indicator.setPrefSize(18, 18);
        indicator.setMinSize(18, 18);
        indicator.setStyle(
                "-fx-background-color: " + colorToHex(couleur) + ";" +
                "-fx-background-radius: 2;" +
                "-fx-border-color: rgba(255,255,255,0.2);" +
                "-fx-border-radius: 2;" +
                "-fx-border-width: 1;"
        );
        btn.setGraphic(indicator);
        btn.setGraphicTextGap(10);
        btn.setPadding(new Insets(0, 12, 0, 12));

        btn.setOnAction(e -> {
            currentTool = tool;
            statusLabel.setText("OUTIL : " + text);
            for (Toggle t : toolGroup.getToggles()) {
                if (t instanceof ToggleButton tb) appliquerStyleOutil(tb, tb.isSelected());
            }
        });

        btn.setOnMouseEntered(e -> { if (!btn.isSelected()) appliquerStyleOutil(btn, false, true); });
        btn.setOnMouseExited(e -> appliquerStyleOutil(btn, btn.isSelected(), false));

        return btn;
    }

    private void appliquerStyleOutil(ToggleButton btn, boolean selected) {
        appliquerStyleOutil(btn, selected, false);
    }
    private void appliquerStyleOutil(ToggleButton btn, boolean selected, boolean hover) {
        String bg = selected ? "#3a1818" : (hover ? BG_BTN_HOVER : BG_BTN);
        String border = selected ? ACCENT_SEL : BORDER_COL;
        double bWidth = selected ? 2 : 1;
        btn.setStyle(
                "-fx-background-color: " + bg + ";" +
                "-fx-text-fill: " + TEXT_MAIN + ";" +
                "-fx-background-radius: 2;" +
                "-fx-border-color: " + border + ";" +
                "-fx-border-width: " + bWidth + ";" +
                "-fx-border-radius: 2;" +
                "-fx-cursor: hand;"
        );
    }

    private void onMouseClick(double mx, double my, MouseButton button) {
        int col = (int) (mx / CELL_SIZE);
        int row = (int) (my / CELL_SIZE);
        if (row < 0 || row >= editor.getRows() || col < 0 || col >= editor.getCols())
            return;

        if (button == MouseButton.SECONDARY)
            editor.setCell(row, col, ' ');
        else
            editor.setCell(row, col, currentTool);

        editor.setCursor(row, col);
        redrawGrid();
        statusLabel.setText("(" + row + ", " + col + ") -> '" + editor.getCell(row, col) + "'");
    }

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
            statusLabel.setText("CURSEUR : (" + nr + ", " + nc + ")  |  '" + editor.getCell(nr, nc) + "'");
        }
        e.consume();
    }

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

                gc.setFill(couleurCase(cell));
                gc.fillRect(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2);

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

                gc.setStroke(Color.rgb(60, 25, 25, 0.6));
                gc.setLineWidth(0.5);
                gc.strokeRect(x, y, CELL_SIZE, CELL_SIZE);
            }
        }

        
        int cr = editor.getCursorRow();
        int cc = editor.getCursorCol();
        gc.setStroke(Color.rgb(255, 48, 48));
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
            default:  return Color.rgb(45, 45, 45);
        }
    }

    private void dialogNouveau() {
        Dialog<int[]> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle grille");
        dialog.setHeaderText("Creer une grille vide");

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
                statusLabel.setText("GRILLE " + res[0] + "x" + res[1] + " CREEE.");
            } catch (IllegalArgumentException ex) {
                erreur("Taille minimale : 5x5");
            }
        });
    }

    private void dialogSave() {
        ArrayList<String> warnings = editor.validate();
        if (!warnings.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String w : warnings) sb.append("- ").append(w).append("\n");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Sauvegarde impossible");
            alert.setHeaderText("Niveau pas valide");
            alert.setContentText(sb.toString() + "\nCorrigez avant de sauvegarder.");
            alert.showAndWait();
            return;
        }

        if (!editor.borduresIntactes()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Sauvegarde impossible");
            alert.setHeaderText("Bordures cassees");
            alert.setContentText("Utilisez 'Murs bordure' pour corriger.");
            alert.showAndWait();
            return;
        }

        Dialog<int[]> dialog = new Dialog<>();
        dialog.setTitle("Sauvegarder");
        dialog.setHeaderText("Sauvegarder dans levels/personalized/levelN/worldN.txt\n(nbWorlds.txt et state.txt crees auto)");

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
            if (editor.saveToProjectFile(res[0], res[1], 0))
                statusLabel.setText("SAUVE : levels/personalized/level" + res[0] + "/world" + res[1] + ".txt");
            else
                erreur("Sauvegarde echouee.");
        });
    }

    private void dialogLoad() {
        Dialog<int[]> dialog = new Dialog<>();
        dialog.setTitle("Charger");
        dialog.setHeaderText("Charger depuis levels/personalized/levelN/worldN.txt");

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
                statusLabel.setText("CHARGE LEVEL" + res[0] + "/WORLD" + res[1]);
            } else {
                erreur("Fichier introuvable.");
            }
        });
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
                if (firstLine.matches("[A-Za-z]\\s+\\d+"))
                    editor.importLevel(content);
                else
                    editor.importProjectFormat(content);

                redrawGrid();
                statusLabel.setText("IMPORTE : " + file.getName());
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
            alert.setContentText("Joueur, boites et cibles OK.");
        } else {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation");
            alert.setHeaderText("Problemes");
            StringBuilder sb = new StringBuilder();
            for (String w : warnings) sb.append("- ").append(w).append("\n");
            alert.setContentText(sb.toString());
        }
        alert.showAndWait();
    }

    private void remplirBordures() {
        for (int i = 0; i < editor.getRows(); i++)
            for (int j = 0; j < editor.getCols(); j++)
                if (i == 0 || j == 0 || i == editor.getRows()-1 || j == editor.getCols()-1)
                    editor.setCell(i, j, '#');
    }

    private void erreur(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Erreur");
        a.setContentText(msg);
        a.showAndWait();
    }

  
    private Button makeButton(String text, boolean accent) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Monospaced", FontWeight.BOLD, 11));

        String bgRepos = accent ? ACCENT_RED : BG_BTN;
        String bgHover = accent ? ACCENT_HOVER : BG_BTN_HOVER;
        String border  = accent ? ACCENT_HOVER : BORDER_COL;
        String texte   = accent ? "#ffffff" : TEXT_MAIN;

        String styleRepos =
                "-fx-background-color: " + bgRepos + ";" +
                "-fx-text-fill: " + texte + ";" +
                "-fx-background-radius: 2;" +
                "-fx-border-color: " + border + ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 2;" +
                "-fx-padding: 8 14 8 14;" +
                "-fx-cursor: hand;";
        String styleHover =
                "-fx-background-color: " + bgHover + ";" +
                "-fx-text-fill: " + texte + ";" +
                "-fx-background-radius: 2;" +
                "-fx-border-color: " + ACCENT_SEL + ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 2;" +
                "-fx-padding: 8 14 8 14;" +
                "-fx-cursor: hand;";

        btn.setStyle(styleRepos);
        btn.setOnMouseEntered(e -> btn.setStyle(styleHover));
        btn.setOnMouseExited(e -> btn.setStyle(styleRepos));
        return btn;
    }

    private Region separateurVertical() {
        Region r = new Region();
        r.setPrefWidth(1);
        r.setMinHeight(22);
        r.setMaxHeight(22);
        r.setStyle("-fx-background-color: " + BORDER_COL + ";");
        HBox.setMargin(r, new Insets(0, 6, 0, 6));
        return r;
    }

    private String colorToHex(Color c) {
        return String.format("#%02x%02x%02x",
                (int)(c.getRed()*255), (int)(c.getGreen()*255), (int)(c.getBlue()*255));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
