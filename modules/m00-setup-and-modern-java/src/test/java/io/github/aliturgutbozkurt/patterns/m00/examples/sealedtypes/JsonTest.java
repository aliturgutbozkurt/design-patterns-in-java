package io.github.aliturgutbozkurt.patterns.m00.examples.sealedtypes;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m00.support.Console;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class JsonTest {

    @Test
    void rendersScalars() {
        assertThat(Json.render(JsonNull.INSTANCE)).isEqualTo("null");
        assertThat(Json.render(new JsonBool(true))).isEqualTo("true");
        assertThat(Json.render(new JsonNumber(3))).isEqualTo("3");
        assertThat(Json.render(new JsonNumber(4.5))).isEqualTo("4.5");
    }

    @Test
    void escapesStrings() {
        assertThat(Json.render(new JsonString("say \"hi\"\\\n"))).isEqualTo("\"say \\\"hi\\\"\\\\\\n\"");
    }

    @Test
    void rendersNestedValuesInInsertionOrder() {
        Map<String, Json> members = new LinkedHashMap<>();
        members.put("name", new JsonString("PatternShop"));
        members.put("tags", new JsonArray(List.of(new JsonString("java"), new JsonString("patterns"))));
        members.put("owner", JsonNull.INSTANCE);
        assertThat(Json.render(new JsonObject(members)))
                .isEqualTo("{\"name\":\"PatternShop\",\"tags\":[\"java\",\"patterns\"],\"owner\":null}");
    }

    @Test
    void containersAreImmutableCopies() {
        List<Json> items = new java.util.ArrayList<>(List.of(new JsonBool(false)));
        JsonArray array = new JsonArray(items);
        items.add(new JsonBool(true));
        assertThat(array.items()).hasSize(1);
    }

    @Test
    void findsAStringMemberWithNestedPatterns() {
        Json config = new JsonObject(Map.of("env", new JsonString("prod"), "port", new JsonNumber(8080)));
        assertThat(Json.stringAt(config, "env")).contains("prod");
        assertThat(Json.stringAt(config, "port")).isEmpty();
        assertThat(Json.stringAt(new JsonString("env"), "env")).isEmpty();
    }

    @Test
    void demoPrintsExpectedLines() {
        assertThat(Console.capture(() -> JsonDemo.main(new String[0]))).isEqualTo("""
                {"shop":"PatternShop","open":true,"rating":4.5,"branches":["İzmir","Berlin"],"owner":null}
                shop = PatternShop
                rating is not a string
                """);
    }
}
