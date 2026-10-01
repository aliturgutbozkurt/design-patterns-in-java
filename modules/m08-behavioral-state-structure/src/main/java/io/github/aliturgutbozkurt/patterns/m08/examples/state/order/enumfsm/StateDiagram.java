package io.github.aliturgutbozkurt.patterns.m08.examples.state.order.enumfsm;

/**
 * Generates the Mermaid {@code stateDiagram-v2} of {@link OrderStatus} from the transition table itself, so the
 * diagram in the lesson can never drift from the code.
 *
 * @see "m08 lesson, section State — Modern Java 27"
 */
public final class StateDiagram {

    private StateDiagram() {}

    public static String mermaid() {
        var text = new StringBuilder("stateDiagram-v2\n");
        text.append("    [*] --> ").append(OrderStatus.NEW).append('\n');
        for (OrderStatus from : OrderStatus.values()) {
            for (OrderStatus to : from.next()) {
                text.append("    ").append(from).append(" --> ").append(to).append('\n');
            }
        }
        for (OrderStatus status : OrderStatus.values()) {
            if (status.isTerminal()) {
                text.append("    ").append(status).append(" --> [*]\n");
            }
        }
        return text.toString();
    }
}
