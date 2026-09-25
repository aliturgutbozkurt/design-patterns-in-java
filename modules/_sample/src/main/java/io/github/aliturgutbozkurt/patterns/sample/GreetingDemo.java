package io.github.aliturgutbozkurt.patterns.sample;

/** Runs without a build: {@code java modules/_sample/src/main/java/.../GreetingDemo.java}. */
public final class GreetingDemo {

    private GreetingDemo() {}

    public static void main(String[] args) {
        for (Language language : Language.values()) {
            System.out.println(new Greeting("Ada", language).text());
        }
    }
}
