package io.github.aliturgutbozkurt.patterns.m07.examples.chain;

import io.github.aliturgutbozkurt.patterns.m07.examples.chain.validation.SignUp;
import io.github.aliturgutbozkurt.patterns.m07.examples.chain.validation.SignUpRules;
import java.util.List;

/** Run: {@code java modules/m07-behavioral-communication/src/main/java/io/github/aliturgutbozkurt/patterns/m07/examples/chain/ValidationChainDemo.java} */
public final class ValidationChainDemo {

    private ValidationChainDemo() {}

    public static void main(String[] args) {
        var collectAll = SignUpRules.collectAll();
        var failFast = SignUpRules.failFast();
        for (SignUp input : List.of(new SignUp("ada.example.com", "short", 16),
                new SignUp("ada@example.com", "s3cret-pass", 36))) {
            System.out.println(input);
            System.out.println("  collect-all: " + collectAll.validate(input));
            System.out.println("  fail-fast:   " + failFast.validate(input));
        }
    }
}
