package io.github.aliturgutbozkurt.patterns.m02.solutions.ex01;

import io.github.aliturgutbozkurt.patterns.m02.exercises.ex01.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * Reference solution for assignment 01: a private constructor and three named static factories. The 8 basic colours
 * are created once and handed out again by {@link #named} and by {@link #rgb} whenever the value matches.
 *
 * @see "m02 lesson, section Static Factory Method"
 */
public final class RgbColor implements Color {

    private static final Pattern HEX = Pattern.compile("#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})");

    private static final SortedMap<String, RgbColor> NAMED;
    private static final Map<Integer, RgbColor> NAMED_BY_VALUE;

    static {
        SortedMap<String, RgbColor> named = new TreeMap<>();
        named.put("black", new RgbColor(0, 0, 0));
        named.put("white", new RgbColor(255, 255, 255));
        named.put("red", new RgbColor(255, 0, 0));
        named.put("green", new RgbColor(0, 255, 0));
        named.put("blue", new RgbColor(0, 0, 255));
        named.put("yellow", new RgbColor(255, 255, 0));
        named.put("cyan", new RgbColor(0, 255, 255));
        named.put("magenta", new RgbColor(255, 0, 255));
        NAMED = Collections.unmodifiableSortedMap(named);
        Map<Integer, RgbColor> byValue = new HashMap<>();
        named.values().forEach(color -> byValue.put(color.packed(), color));
        NAMED_BY_VALUE = Map.copyOf(byValue);
    }

    private final int red;
    private final int green;
    private final int blue;

    private RgbColor(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public static RgbColor rgb(int red, int green, int blue) {
        requireComponent("red", red);
        requireComponent("green", green);
        requireComponent("blue", blue);
        int packed = (red << 16) | (green << 8) | blue;
        RgbColor cached = NAMED_BY_VALUE.get(packed);
        return cached != null ? cached : new RgbColor(red, green, blue);
    }

    public static RgbColor hex(String text) {
        if (!HEX.matcher(text).matches()) {
            throw new IllegalArgumentException("not a colour: '" + text + "' (expected #RRGGBB or #RGB)");
        }
        String digits = text.substring(1);
        if (digits.length() == 3) {
            digits = "" + digits.charAt(0) + digits.charAt(0) + digits.charAt(1) + digits.charAt(1)
                    + digits.charAt(2) + digits.charAt(2);
        }
        int value = Integer.parseInt(digits, 16);
        return rgb(value >> 16, (value >> 8) & 0xFF, value & 0xFF);
    }

    public static RgbColor named(String name) {
        RgbColor color = NAMED.get(name.toLowerCase(Locale.ROOT));
        if (color == null) {
            throw new IllegalArgumentException("unknown colour: " + name + " (known: "
                    + String.join(", ", NAMED.keySet()) + ")");
        }
        return color;
    }

    private static void requireComponent(String name, int value) {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException(name + " must be in 0..255: " + value);
        }
    }

    private int packed() {
        return (red << 16) | (green << 8) | blue;
    }

    @Override
    public int red() {
        return red;
    }

    @Override
    public int green() {
        return green;
    }

    @Override
    public int blue() {
        return blue;
    }

    @Override
    public String toHex() {
        return "#%02X%02X%02X".formatted(red, green, blue);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof RgbColor that && packed() == that.packed();
    }

    @Override
    public int hashCode() {
        return packed();
    }

    @Override
    public String toString() {
        return toHex();
    }
}
