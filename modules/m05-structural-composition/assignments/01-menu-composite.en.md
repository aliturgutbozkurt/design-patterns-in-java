# Assignment 01 — Restaurant Menu Composite

> Module: m05-structural-composition · Difficulty: ★★☆ · Estimated time: 2 h

## Goal

A restaurant menu is a tree: the dinner menu holds submenus (starters, mains, desserts), and submenus hold dishes —
but a menu may also hold a dish directly, and a single dish is a valid "menu" on its own. The tree is given as a
**sealed** interface with two **record** implementations. Your job is the other half of the modern Composite: write
the tree operations (counts, totals, filtered lists, a search that returns a path, an indented rendering) as
**recursive, exhaustive `switch` expressions** over the sealed type.

## What you are given

- `exercises/ex01/MenuComponent.java` — `sealed interface MenuComponent permits MenuItem, Menu` — **do not modify**
- `exercises/ex01/MenuItem.java` — `record MenuItem(String name, int priceCents, boolean vegetarian)`; name not
  blank, price ≥ 0 — **do not modify**
- `exercises/ex01/Menu.java` — `record Menu(String name, List<MenuComponent> children)`; children copied with
  `List.copyOf`, duplicate child names rejected; `Menu.of(name, children...)` — **do not modify**
- `exercises/ex01/MenuQueries.java` — the six operations you implement (see Javadoc) — **do not modify**
- `exercises/ex01/MenuReport.java` — your code goes here (`TODO(ex01)` markers)

## Tasks

1. `itemCount` and `totalCents`: an item counts 1 and contributes its price; a menu adds up its children. An empty
   menu gives 0.
2. `itemNames` and `vegetarian`: all items **depth-first in menu order** (a child menu is finished before the next
   sibling starts); `vegetarian` keeps only items marked vegetarian.
3. `pathTo(root, itemName)`: the names from the root down to the first **item** with that name, joined with
   `" > "`, e.g. `Dinner > Desserts > Tiramisu`; `Optional.empty()` when there is none (a *menu* with that name does
   not count).
4. `render(root)`: one line per node, two spaces of indent per level, every line ending in `\n`. A menu is its name;
   an item is `- <name> <euros>.<cents>`, followed by ` (v)` when vegetarian:

   ```text
   Dinner
     Starters
       - Soup 4.50 (v)
       - Calamari 7.00
     - Water 1.50 (v)
   ```

5. Every method throws `NullPointerException` for a `null` argument (`Objects.requireNonNull`).

Use a `switch` over `MenuComponent` with record patterns — no `instanceof` chains and no `default` branch. The tests
check behaviour only, but the point of the exercise is the shape of the code.

## Acceptance criteria

- [ ] `singleItemIsItsOwnTree`
- [ ] `emptyMenuHasNoItemsAndZeroTotal`
- [ ] `countsItemsInNestedMenus`
- [ ] `totalsPricesAcrossAllLevels`
- [ ] `listsItemNamesDepthFirstInMenuOrder`
- [ ] `filtersVegetarianItemsInOrder`
- [ ] `findsPathToNestedItem`
- [ ] `pathToUnknownItemIsEmpty`
- [ ] `rendersIndentedMenu`
- [ ] `rejectsNullArguments`

## Run the tests

```bash
./mvnw -pl modules/m05-structural-composition test -Pexercises -Dtest='Ex01*'
```

All tests green = done. Compare with `solutions/ex01/` only **after** you have tried.

## Hints

<details><summary>Hint 1 — the shape of every operation</summary>

```text
return switch (root) {
    case MenuItem(var name, var price, var veg) -> …   // the leaf case
    case Menu(var name, var children) -> …             // combine the results of the children
};
```

Use `_` for record components you do not need, e.g. `case MenuItem(var _, var price, var _) -> price`.

</details>

<details><summary>Hint 2 — paths and rendering</summary>

For `pathTo`, ask each child for *its* path and prefix the menu name to the first one found
(`Optional.map`, `Optional::stream` + `findFirst`). For `render`, pass the current depth down the recursion and
format the price with `String.format(Locale.ROOT, "%d.%02d", cents / 100, cents % 100)`.

</details>

## Stretch goals (optional, not graded)

- Add a third node type, `Combo(String name, List<MenuItem> items, int discountCents)`. Which of your methods stop
  compiling — and why is that a *good* thing? Compare with the classic Composite, where adding an operation would
  have meant changing every node class.
- Write `cheapest(MenuComponent)` returning `Optional<MenuItem>` with a single `switch`.
