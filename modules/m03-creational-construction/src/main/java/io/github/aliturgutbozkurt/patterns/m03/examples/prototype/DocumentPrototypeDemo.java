package io.github.aliturgutbozkurt.patterns.m03.examples.prototype;

import io.github.aliturgutbozkurt.patterns.m03.examples.prototype.documents.DocumentTemplate;
import java.util.List;
import java.util.Map;

/** Run: {@code java modules/m03-creational-construction/src/main/java/io/github/aliturgutbozkurt/patterns/m03/examples/prototype/DocumentPrototypeDemo.java} */
public final class DocumentPrototypeDemo {

    private DocumentPrototypeDemo() {}

    public static void main(String[] args) {
        DocumentTemplate template = invoiceTemplate();
        System.out.println("template: " + template);

        System.out.println("== clone() then edit the copy ==");
        DocumentTemplate shallow = template.clone();
        edit(shallow);
        System.out.println("copy:     " + shallow);
        System.out.println("template: " + template + "   <- changed too!");

        System.out.println("== copy constructor then edit the copy ==");
        DocumentTemplate fresh = invoiceTemplate();
        DocumentTemplate deep = new DocumentTemplate(fresh);
        edit(deep);
        System.out.println("copy:     " + deep);
        System.out.println("template: " + fresh);
    }

    private static DocumentTemplate invoiceTemplate() {
        return new DocumentTemplate("Invoice", List.of("Header", "Lines", "Totals"), Map.of("lang", "en"));
    }

    private static void edit(DocumentTemplate copy) {
        copy.setTitle("Receipt");
        copy.addSection("Signature");
        copy.putMetadata("lang", "tr");
    }
}
