package io.github.aliturgutbozkurt.patterns.m07.examples.memento;

import io.github.aliturgutbozkurt.patterns.m07.examples.memento.editor.Editor;
import io.github.aliturgutbozkurt.patterns.m07.examples.memento.editor.EditorSnapshot;

/**
 * Run: {@code java modules/m07-behavioral-communication/src/main/java/io/github/aliturgutbozkurt/patterns/m07/examples/memento/EditorUndoDemo.java}
 *
 * @see "m07 lesson, section Memento"
 */
public final class EditorUndoDemo {

    private EditorUndoDemo() {}

    public static void main(String[] args) {
        var editor = new Editor(3);
        editor.type("Hello");
        editor.type(" world");
        editor.select(6, 11);
        show("select", editor);
        editor.type("Java");
        show("type", editor);
        show("undo " + editor.undo(), editor);
        show("undo " + editor.undo(), editor);
        show("redo " + editor.redo(), editor);
        editor.type("there");
        show("type", editor);
        show("redo " + editor.redo(), editor);
        System.out.println("history (newest first, capacity 3): "
                + editor.history().stream().map(EditorSnapshot::render).toList());
    }

    private static void show(String step, Editor editor) {
        System.out.println(step + " -> " + editor.render());
    }
}
