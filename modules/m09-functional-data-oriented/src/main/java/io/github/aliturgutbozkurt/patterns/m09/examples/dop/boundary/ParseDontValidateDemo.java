package io.github.aliturgutbozkurt.patterns.m09.examples.dop.boundary;

import io.github.aliturgutbozkurt.patterns.m09.examples.dop.boundary.ImportedRow.Accepted;
import io.github.aliturgutbozkurt.patterns.m09.examples.dop.boundary.ImportedRow.Rejected;
import java.util.List;

/** Run: {@code java modules/m09-functional-data-oriented/src/main/java/io/github/aliturgutbozkurt/patterns/m09/examples/dop/boundary/ParseDontValidateDemo.java} */
public final class ParseDontValidateDemo {

    /** A bulk order as a customer uploaded it: two good rows and four broken ones. */
    static final String CSV = """
            MUG-0001,2,1250
            TEE-0002,1,2000
            mug-3,1,500
            BAG-0004,0,3000
            CAP-0005,two,800
            PEN-0006,3
            """;

    private ParseDontValidateDemo() {}

    public static void main(String[] args) {
        System.out.println("-- the boundary: text in, typed rows out");
        List<ImportedRow> rows = new CsvOrderImporter().parse(CSV);
        for (ImportedRow row : rows) {
            String text = switch (row) {
                case Accepted(var no, OrderLine(var sku, var qty, var price)) ->
                        "line " + no + ": accepted " + sku + " x " + qty + " @ " + price;
                case Rejected(var no, var reason) -> "line " + no + ": rejected (" + reason + ")";
            };
            System.out.println(text);
        }

        System.out.println("-- the core: only valid values, no checks left");
        List<OrderLine> lines = CsvOrderImporter.acceptedLines(rows);
        System.out.println("accepted lines: " + lines.size() + ", total " + OrderLines.totalCents(lines) + " cents");

        System.out.println("-- values are normalised once, when they are built");
        System.out.println("new Email(\"  Ali@Example.COM \") = " + new Email("  Ali@Example.COM "));
    }
}
