package io.github.aliturgutbozkurt.patterns.sample;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;

class GreetingTest {

    @Test
    void greetsByNameInEachLanguage() {
        assertThat(new Greeting("Ada", Language.EN).text()).isEqualTo("Hello, Ada!");
        assertThat(new Greeting("Ada", Language.TR).text()).isEqualTo("Merhaba, Ada!");
    }

    @Test
    void rejectsBlankName() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Greeting(" ", Language.EN));
    }
}
