# Assignment 02 — Flyweight Map Tiles

> Module: m05-structural-composition · Difficulty: ★★☆ · Estimated time: 2 h

## Goal

A strategy game draws its world on a grid of tiles. Every tile type carries "heavy" data — an 8 × 8 sprite, a map
symbol, a movement cost — and the given `NaiveTileMap` creates a **new** `TileType` for every cell: a 256 × 256 map
holds 65 536 of them although there are only five kinds of terrain. Apply the **Flyweight** pattern: build a
thread-safe registry that shares one `TileType` per terrain (the *intrinsic* state), and a map whose cells only
remember *which* shared type sits at *which* position (the *extrinsic* state). Then measure the saving by counting
distinct instances before and after.

## What you are given

- `exercises/ex02/Terrain.java` — `GRASS`, `SAND`, `FOREST`, `WATER`, `MOUNTAIN` with `symbol()`,
  `movementCost()` and `walkable()` (the table below) — **do not modify**
- `exercises/ex02/TileType.java` — `record TileType(Terrain terrain, char symbol, int movementCost, boolean walkable,
  List<String> sprite)`; `TileType.of(terrain)` builds a new one — **do not modify**
- `exercises/ex02/Sprites.java` — `Sprites.forTerrain(terrain)`, the 8 × 8 sprite — **do not modify**
- `exercises/ex02/Point.java` — `record Point(int x, int y)` — **do not modify**
- `exercises/ex02/TileTypeRegistry.java` — `typeOf(Terrain)`, `createdCount()` — **do not modify**
- `exercises/ex02/TileMap.java` — `width()`, `height()`, `typeAt(x, y)`, `paint(x, y, terrain)`,
  `movementCost(path)`, `render()` — **do not modify**
- `exercises/ex02/NaiveTileMap.java` — the correct but wasteful "before" — **do not modify**
- `exercises/ex02/ObjectCounter.java` — `distinctInstances(map)`, an identity-based count over all cells —
  **do not modify**
- `exercises/ex02/CachingTileTypeRegistry.java`, `SharedTileMap.java` — your code goes here (`TODO(ex02)` markers)

| Terrain | Symbol | Movement cost | Walkable |
|---|---|---|---|
| `GRASS` | `.` | 1 | yes |
| `SAND` | `:` | 2 | yes |
| `FOREST` | `T` | 3 | yes |
| `WATER` | `~` | — | no |
| `MOUNTAIN` | `^` | — | no |

## Tasks

1. `CachingTileTypeRegistry`: `typeOf(terrain)` returns the **same instance** for the same terrain on every call,
   creating it with `TileType.of` **at most once** — even when 1 000 virtual threads ask at the same time.
   `createdCount()` reports how many types were created (0 for a new registry).
2. `SharedTileMap(width, height, registry)`: a new map is all `GRASS`; `paint` changes exactly one cell; every cell
   refers to a type from the registry, so two maps built on one registry share their types.
3. `movementCost(path)` sums the costs of the tiles on the path (an empty path costs 0) and throws
   `IllegalArgumentException` when a tile on it is not walkable.
4. `render()` returns one line of symbols per row, top row first, each line ending in `\n`.
5. Coordinates outside the map throw `IndexOutOfBoundsException` (in `typeAt`, `paint` and `movementCost`).

## Acceptance criteria

- [ ] `registryReturnsTheSameInstanceForTheSameTerrain`
- [ ] `registryCreatesEachTypeAtMostOnce`
- [ ] `registryIsSafeUnderManyVirtualThreads`
- [ ] `newMapIsAllGrass`
- [ ] `paintChangesOnlyOneCell`
- [ ] `sharedMapHoldsOneInstancePerTerrainUsed` — on a 256 × 256 map painted with three terrains, the naive map
  has 65 536 distinct `TileType` instances and yours exactly 3
- [ ] `mapsShareTypesThroughTheRegistry`
- [ ] `movementCostSumsThePath`
- [ ] `pathThroughWaterIsRejected`
- [ ] `rendersRowsOfSymbols`
- [ ] `rejectsCoordinatesOutsideTheMap`

## Run the tests

```bash
./mvnw -pl modules/m05-structural-composition test -Pexercises -Dtest='Ex02*'
```

All tests green = done. Compare with `solutions/ex02/` only **after** you have tried.

## Hints

<details><summary>Hint 1 — a thread-safe factory in one line</summary>

`ConcurrentHashMap.computeIfAbsent(key, function)` runs `function` at most once per key, atomically. A
`HashMap` plus "check, then put" is a race: two threads can both see "absent" and both create a type. Count
creations inside the function (an `AtomicInteger`), not in `typeOf`.

</details>

<details><summary>Hint 2 — what a cell stores</summary>

A cell needs nothing but a reference to a shared `TileType`; its position is the array index (`y * width + x`).
`Arrays.fill(cells, registry.typeOf(Terrain.GRASS))` makes a new map. Reuse one private `index(x, y)` method for
the bounds check.

</details>

## Stretch goals (optional, not graded)

- Measure it: build both 256 × 256 maps in a small `main`, keep them alive, and run
  `jcmd <pid> GC.class_histogram`. How many `TileType` and `ArrayList`/`ImmutableCollections$ListN` instances do
  you see in each case?
- `Terrain` is an enum — the JDK already guarantees one instance per constant. Could `TileType` simply be an enum
  too? What would you lose?
