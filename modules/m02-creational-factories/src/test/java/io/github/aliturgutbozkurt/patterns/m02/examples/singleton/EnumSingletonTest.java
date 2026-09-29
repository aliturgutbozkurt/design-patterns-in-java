package io.github.aliturgutbozkurt.patterns.m02.examples.singleton;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m02.examples.singleton.enumsingleton.AppSettings;
import io.github.aliturgutbozkurt.patterns.m02.support.Console;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.Test;

class EnumSingletonTest {

    @Test
    void isTheSameInstanceEverywhere() {
        assertThat(AppSettings.INSTANCE).isSameAs(AppSettings.valueOf("INSTANCE"));
        assertThat(AppSettings.values()).containsExactly(AppSettings.INSTANCE);
    }

    @Test
    void readsSettings() {
        assertThat(AppSettings.INSTANCE.get("app.name")).isEqualTo("PatternShop");
        assertThat(AppSettings.INSTANCE.pageSize()).isEqualTo(20);
        assertThatIllegalArgumentException().isThrownBy(() -> AppSettings.INSTANCE.get("nope"))
                .withMessage("unknown setting: nope");
    }

    @Test
    void serializationRoundTripReturnsTheSameInstance() throws Exception {
        var bytes = new ByteArrayOutputStream();
        try (var out = new ObjectOutputStream(bytes)) {
            out.writeObject(AppSettings.INSTANCE);
        }
        try (var in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            assertThat(in.readObject()).isSameAs(AppSettings.INSTANCE);
        }
    }

    @Test
    void reflectionCannotCreateASecondInstance() {
        Constructor<?> constructor = AppSettings.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        assertThatThrownBy(() -> constructor.newInstance("SECOND", 1))
                .isInstanceOf(IllegalArgumentException.class)
                .isNotInstanceOf(InvocationTargetException.class)
                .hasMessageContaining("enum");
    }

    @Test
    void demoPrintsTheSettingsAndIdentity() {
        assertThat(Console.capture(() -> EnumSingletonDemo.main(new String[0]))).isEqualTo("""
                app.name = PatternShop
                page size = 20
                same instance via valueOf? true
                same instance after serialization? true
                """);
    }
}
