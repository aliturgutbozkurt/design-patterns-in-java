package io.github.aliturgutbozkurt.patterns.m07.examples.memento;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import io.github.aliturgutbozkurt.patterns.m07.examples.memento.classic.History;
import io.github.aliturgutbozkurt.patterns.m07.examples.memento.classic.TextDocument;
import io.github.aliturgutbozkurt.patterns.m07.support.Console;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class ClassicMementoTest {

    private final TextDocument document = new TextDocument();

    @Test
    void restoreBringsBackExactTextAndCursor() {
        document.type("Hello world");
        document.moveCursor(5);
        var memento = document.save();
        document.type(",");
        document.moveCursor(0);
        document.restore(memento);
        assertThat(document.text()).isEqualTo("Hello world");
        assertThat(document.cursor()).isEqualTo(5);
    }

    @Test
    void savedMementoIsUnaffectedByLaterEdits() {
        document.type("v1");
        var memento = document.save();
        document.type(" and more");
        document.restore(memento);
        document.type("!");
        document.restore(memento);
        assertThat(document.render()).isEqualTo("v1|");
    }

    @Test
    void mementoExposesNothingBeyondObject() {
        Class<?> memento = TextDocument.Memento.class;
        assertThat(Arrays.stream(memento.getDeclaredMethods()).filter(m -> Modifier.isPublic(m.getModifiers())))
                .isEmpty();
        assertThat(memento.getConstructors()).isEmpty();
        assertThat(Arrays.stream(memento.getDeclaredFields()).map(Field::getModifiers))
                .allMatch(modifiers -> Modifier.isPrivate(modifiers) && Modifier.isFinal(modifiers));
    }

    @Test
    void historyPopsNewestFirstAndIsEmptyAtTheEnd() {
        var history = new History();
        document.type("a");
        history.push(document.save());
        document.type("b");
        history.push(document.save());
        document.restore(history.pop().orElseThrow());
        assertThat(document.text()).isEqualTo("ab");
        document.restore(history.pop().orElseThrow());
        assertThat(document.text()).isEqualTo("a");
        assertThat(history.pop()).isEmpty();
    }

    @Test
    void cursorMustStayInsideTheText() {
        document.type("abc");
        assertThatExceptionOfType(IndexOutOfBoundsException.class).isThrownBy(() -> document.moveCursor(4));
    }

    @Test
    void demoPrintsSavesEditsAndUndos() {
        assertThat(Console.capture(() -> ClassicMementoDemo.main(new String[0]))).isEqualTo("""
                saved:  Hello|
                saved:  Hello, world|
                edited: >> |Hello, world
                undo:   Hello, world|
                undo:   Hello|
                nothing to undo
                """);
    }
}
