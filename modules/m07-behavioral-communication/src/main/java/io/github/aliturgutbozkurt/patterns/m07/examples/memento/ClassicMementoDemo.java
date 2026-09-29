package io.github.aliturgutbozkurt.patterns.m07.examples.memento;

import io.github.aliturgutbozkurt.patterns.m07.examples.memento.classic.History;
import io.github.aliturgutbozkurt.patterns.m07.examples.memento.classic.TextDocument;

/** Run: {@code java modules/m07-behavioral-communication/src/main/java/io/github/aliturgutbozkurt/patterns/m07/examples/memento/ClassicMementoDemo.java} */
public final class ClassicMementoDemo {

    private ClassicMementoDemo() {}

    public static void main(String[] args) {
        var document = new TextDocument();
        var history = new History();

        document.type("Hello");
        history.push(document.save());
        System.out.println("saved:  " + document.render());
        document.type(", world");
        history.push(document.save());
        System.out.println("saved:  " + document.render());
        document.moveCursor(0);
        document.type(">> ");
        System.out.println("edited: " + document.render());

        for (int i = 0; i < 3; i++) {
            history.pop().ifPresentOrElse(memento -> {
                document.restore(memento);
                System.out.println("undo:   " + document.render());
            }, () -> System.out.println("nothing to undo"));
        }
    }
}
