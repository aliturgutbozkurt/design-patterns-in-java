# Assignment 02 — Game Levels with an Abstract Factory

> Module: m02-creational-factories · Difficulty: ★★☆ · Estimated time: 2 h

## Goal

A game has levels in different biomes. The enemies, obstacles and rewards of a level must all come from the **same
biome** — a desert level with a forest wolf is a bug. Use an **Abstract Factory** so that consistency is guaranteed
by construction, write a level generator that works with *any* factory, and add static factories that pick the
family.

## What you are given

- `exercises/ex02/Biome.java` — `enum Biome { FOREST, DESERT }` — **do not modify**
- `exercises/ex02/Enemy.java`, `Obstacle.java`, `Reward.java` — the abstract products — **do not modify**
- `exercises/ex02/LevelFactory.java` — the abstract factory — **do not modify**
- `exercises/ex02/Level.java` — record `Level(enemies, obstacles, reward)` — **do not modify**
- `exercises/ex02/ForestLevelFactory.java`, `DesertLevelFactory.java`, `LevelGenerator.java`,
  `LevelFactories.java` — your code goes here (`TODO(ex02)` markers)

## Tasks

1. Implement the two families. Every product reports its family's `Biome`:

   | Family | Enemy (hit points) | Obstacle (damage) | Reward (points) |
   |---|---|---|---|
   | Forest | Wolf (30) | Fallen log (5) | Mushroom (10) |
   | Desert | Scorpion (20) | Quicksand (12) | Water flask (15) |

2. `LevelGenerator(LevelFactory)`: `generate(difficulty)` returns a `Level` with `difficulty` enemies, `difficulty`
   obstacles and one reward, all created by its factory. Difficulty must be 1–10, otherwise
   `IllegalArgumentException`. The generator must not name any concrete family or product.
3. `LevelFactories.forBiome(Biome)` — an exhaustive `switch`, no `default`.
4. `LevelFactories.forName(String)` — case-insensitive (`"forest"`, `"Desert"`); unknown names throw
   `IllegalArgumentException`.

## Acceptance criteria

- [ ] `forestFamilyIsConsistent`
- [ ] `desertFamilyIsConsistent`
- [ ] `productStatsMatchTheTable`
- [ ] `generatorUsesOnlyItsFactory`
- [ ] `difficultyScalesEnemiesAndObstacles`
- [ ] `generatorWorksWithAnyFactory` — the test passes a family that exists only in the test
- [ ] `forBiomeReturnsTheMatchingFamily`
- [ ] `forNameIsCaseInsensitive`
- [ ] `unknownNameRejected`
- [ ] `rejectsDifficultyOutsideOneToTen`

## Run the tests

```bash
./mvnw -pl modules/m02-creational-factories test -Pexercises -Dtest='Ex02*'
```

All tests green = done. Compare with `solutions/ex02/` only **after** you have tried.

## Hints

<details><summary>Hint 1 — products in one line</summary>

A record whose components are `name`, `hitPoints` and `biome` already has the accessor methods `Enemy` asks for:
`record SimpleEnemy(String name, int hitPoints, Biome biome) implements Enemy {}`.

</details>

<details><summary>Hint 2 — n of something</summary>

`Stream.generate(factory::enemy).limit(n).toList()` calls the factory `n` times.

</details>

## Stretch goals (optional, not graded)

- Add a `SNOW` biome. Which files did the compiler make you change, and why is that good?
- Compare with a Factory Method design: one generator subclass per biome. What gets harder?
