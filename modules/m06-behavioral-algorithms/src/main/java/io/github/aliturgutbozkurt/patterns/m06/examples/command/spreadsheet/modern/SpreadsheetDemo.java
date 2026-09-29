package io.github.aliturgutbozkurt.patterns.m06.examples.command.spreadsheet.modern;

import io.github.aliturgutbozkurt.patterns.m06.examples.command.spreadsheet.classic.Sheet;
import io.github.aliturgutbozkurt.patterns.m06.examples.command.spreadsheet.modern.SheetEdit.Batch;
import io.github.aliturgutbozkurt.patterns.m06.examples.command.spreadsheet.modern.SheetEdit.ClearCell;
import io.github.aliturgutbozkurt.patterns.m06.examples.command.spreadsheet.modern.SheetEdit.SetCell;
import java.util.List;

/** Run: {@code java modules/m06-behavioral-algorithms/src/main/java/io/github/aliturgutbozkurt/patterns/m06/examples/command/spreadsheet/modern/SpreadsheetDemo.java} */
public final class SpreadsheetDemo {

    private SpreadsheetDemo() {}

    public static void main(String[] args) {
        var sheet = new Sheet();
        var editor = new SheetEditor(sheet);

        SheetEdit first = new SetCell("A1", "10");
        editor.perform(first);
        System.out.println("perform " + first + " -> " + sheet);

        SheetEdit batch = new Batch(List.of(new SetCell("B1", "20"), new SetCell("C1", "30"), new ClearCell("A1")));
        SheetEdit inverse = editor.perform(batch);
        System.out.println("perform " + batch + " -> " + sheet);
        System.out.println("inverse " + inverse);

        editor.undo();  // the whole batch is undone in one step
        System.out.println("undo -> " + sheet);

        List<SheetEdit> log = editor.log();
        System.out.println("log has " + log.size() + " edits; replayed on an empty sheet: " + SheetEditor.replay(log));
    }
}
