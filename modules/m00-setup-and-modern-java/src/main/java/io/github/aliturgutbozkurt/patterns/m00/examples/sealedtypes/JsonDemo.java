package io.github.aliturgutbozkurt.patterns.m00.examples.sealedtypes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Run: {@code java modules/m00-setup-and-modern-java/src/main/java/io/github/aliturgutbozkurt/patterns/m00/examples/sealedtypes/JsonDemo.java} */
public final class JsonDemo {

    private JsonDemo() {}

    public static void main(String[] args) {
        Map<String, Json> members = new LinkedHashMap<>();
        members.put("shop", new JsonString("PatternShop"));
        members.put("open", new JsonBool(true));
        members.put("rating", new JsonNumber(4.5));
        members.put("branches", new JsonArray(List.of(new JsonString("İzmir"), new JsonString("Berlin"))));
        members.put("owner", JsonNull.INSTANCE);
        Json config = new JsonObject(members);

        System.out.println(Json.render(config));
        Json.stringAt(config, "shop").ifPresent(shop -> System.out.println("shop = " + shop));
        System.out.println(Json.stringAt(config, "rating").isPresent() ? "rating is a string" : "rating is not a string");
    }
}
