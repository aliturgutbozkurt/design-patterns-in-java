// Multi-file source-code program (JEP 458): the launcher compiles Greeter.java from the same directory on demand.
// Run:   java modules/m00-setup-and-modern-java/first-steps/multifile/Main.java
public class Main {
    public static void main(String[] args) {
        System.out.println(new Greeter("Ada").greet());
    }
}
