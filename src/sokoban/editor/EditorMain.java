package sokoban.editor;


public class EditorMain {
    public static void main(String[] args) {
        System.out.println("=== Éditeur de plateau Sokoban ===");
        System.out.println("Grille par défaut : 10x10, monde 'A'");
        System.out.println("Utilisez la commande 'n' pour créer une grille de taille différente.");
        System.out.println();

        LevelEditor editor = new LevelEditor(10, 10, 'A');
        editor.run();
    }
}
