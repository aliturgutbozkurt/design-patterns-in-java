package io.github.aliturgutbozkurt.patterns.m05.exercises.ex01;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

/** Assignment 01 — restaurant menu Composite. Each test is one acceptance criterion of the brief. */
public abstract class Ex01Contract {

    protected abstract MenuQueries queries();

    private static final MenuItem SOUP = new MenuItem("Soup", 450, true);
    private static final MenuItem CALAMARI = new MenuItem("Calamari", 700, false);
    private static final MenuItem STEAK = new MenuItem("Steak", 1850, false);
    private static final MenuItem RISOTTO = new MenuItem("Risotto", 1200, true);
    private static final MenuItem TIRAMISU = new MenuItem("Tiramisu", 600, true);
    private static final MenuItem WATER = new MenuItem("Water", 150, true);

    private static final Menu DINNER = Menu.of("Dinner",
            Menu.of("Starters", SOUP, CALAMARI),
            Menu.of("Mains", STEAK, RISOTTO),
            Menu.of("Desserts", TIRAMISU),
            WATER);

    @Test
    void singleItemIsItsOwnTree() {
        MenuQueries queries = queries();
        assertThat(queries.itemCount(SOUP)).isEqualTo(1);
        assertThat(queries.totalCents(SOUP)).isEqualTo(450);
        assertThat(queries.itemNames(SOUP)).containsExactly("Soup");
        assertThat(queries.vegetarian(SOUP)).containsExactly(SOUP);
        assertThat(queries.vegetarian(STEAK)).isEmpty();
        assertThat(queries.pathTo(SOUP, "Soup")).contains("Soup");
        assertThat(queries.render(SOUP)).isEqualTo("- Soup 4.50 (v)\n");
    }

    @Test
    void emptyMenuHasNoItemsAndZeroTotal() {
        MenuQueries queries = queries();
        Menu empty = Menu.of("Closed");
        assertThat(queries.itemCount(empty)).isZero();
        assertThat(queries.totalCents(empty)).isZero();
        assertThat(queries.itemNames(empty)).isEmpty();
        assertThat(queries.render(empty)).isEqualTo("Closed\n");
    }

    @Test
    void countsItemsInNestedMenus() {
        assertThat(queries().itemCount(DINNER)).isEqualTo(6);
        assertThat(queries().itemCount(Menu.of("Outer", Menu.of("Inner", Menu.of("Core", SOUP))))).isEqualTo(1);
    }

    @Test
    void totalsPricesAcrossAllLevels() {
        assertThat(queries().totalCents(DINNER)).isEqualTo(450 + 700 + 1850 + 1200 + 600 + 150);
    }

    @Test
    void listsItemNamesDepthFirstInMenuOrder() {
        assertThat(queries().itemNames(DINNER))
                .containsExactly("Soup", "Calamari", "Steak", "Risotto", "Tiramisu", "Water");
    }

    @Test
    void filtersVegetarianItemsInOrder() {
        assertThat(queries().vegetarian(DINNER)).containsExactly(SOUP, RISOTTO, TIRAMISU, WATER);
    }

    @Test
    void findsPathToNestedItem() {
        assertThat(queries().pathTo(DINNER, "Tiramisu")).contains("Dinner > Desserts > Tiramisu");
        assertThat(queries().pathTo(DINNER, "Water")).contains("Dinner > Water");
    }

    @Test
    void pathToUnknownItemIsEmpty() {
        assertThat(queries().pathTo(DINNER, "Pizza")).isEmpty();
        assertThat(queries().pathTo(DINNER, "Mains")).as("menus are not items").isEmpty();
    }

    @Test
    void rendersIndentedMenu() {
        assertThat(queries().render(DINNER)).isEqualTo("""
                Dinner
                  Starters
                    - Soup 4.50 (v)
                    - Calamari 7.00
                  Mains
                    - Steak 18.50
                    - Risotto 12.00 (v)
                  Desserts
                    - Tiramisu 6.00 (v)
                  - Water 1.50 (v)
                """);
    }

    @Test
    void rejectsNullArguments() {
        MenuQueries queries = queries();
        assertThatNullPointerException().isThrownBy(() -> queries.itemCount(null));
        assertThatNullPointerException().isThrownBy(() -> queries.totalCents(null));
        assertThatNullPointerException().isThrownBy(() -> queries.itemNames(null));
        assertThatNullPointerException().isThrownBy(() -> queries.vegetarian(null));
        assertThatNullPointerException().isThrownBy(() -> queries.pathTo(null, "Soup"));
        assertThatNullPointerException().isThrownBy(() -> queries.pathTo(DINNER, null));
        assertThatNullPointerException().isThrownBy(() -> queries.render(null));
    }
}
