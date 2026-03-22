package sokoban.editor;

public class EditorMain {
    public static void main(String[] args) {
        System.out.println("=== Éditeur de plateau Sokoban (Terminal) ===");
        System.out.println("Grille par défaut : 10x10, monde 'A'");
        System.out.println("Tapez 'n' pour une autre taille.");
        System.out.println();

        LevelEditor editor = new LevelEditor(10, 10, 'A');
        editor.run();
    }
}