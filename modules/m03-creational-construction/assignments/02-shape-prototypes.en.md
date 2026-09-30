# Assignment 02 — Shape Editor with Prototypes

> Module: m03-creational-construction · Difficulty: ★★☆ · Estimated time: 2 h

## Goal

A drawing editor lets users save a shape — or a whole group of shapes — as a **template** and stamp out copies of
it. Shapes are mutable (they can be moved), so every copy must be **deep**: moving a copy must never move the
template, even when groups contain other groups. Implement the shapes, their `copy()` methods and a prototype
registry.

## What you are given

- `exercises/ex02/Point.java` — immutable record with `moved(dx, dy)` — **do not modify**
- `exercises/ex02/Shape.java` — `position()`, `moveBy(dx, dy)`, `copy()`, `describe()` — **do not modify**
- `exercises/ex02/ShapeRegistry.java` — `register`, `create`, `names` — **do not modify**
- `exercises/ex02/Circle.java`, `Rect.java`, `Group.java`, `TemplateRegistry.java` — your code goes here
  (`TODO(ex02)` markers)

## Tasks

1. `Circle(Point centre, int radius)` and `Rect(Point corner, int width, int height)`: mutable position, positive
   sizes. `describe()` gives `circle r=5 at (1,2)` and `rect 3x4 at (0,0)`.
2. `Group(List<Shape>)`: at least one child; `position()` is the first child's; `moveBy` moves every child;
   `describe()` gives `group[<child>, <child>]`; `copy()` copies **every child**.
3. `TemplateRegistry`: `register` stores a **copy** of the template, `create` returns a **fresh copy** each time,
   an unknown name throws `IllegalArgumentException`, `names()` is alphabetical.

## Acceptance criteria

- [ ] `copyDescribesTheSameShape`
- [ ] `copyIsANewObject`
- [ ] `movingACopyLeavesTheOriginal`
- [ ] `groupCopyIsDeep`
- [ ] `nestedGroupsAreCopiedDeeply`
- [ ] `registryReturnsFreshCopies`
- [ ] `registeredTemplateIsNotAffectedByChangesToCreatedShapes`
- [ ] `registeringCopiesTheTemplate`
- [ ] `unknownTemplateRejected`
- [ ] `namesAreSorted`

## Run the tests

```bash
./mvnw -pl modules/m03-creational-construction test -Pexercises -Dtest='Ex02*'
```

All tests green = done. Compare with `solutions/ex02/` only **after** you have tried.

## Hints

<details><summary>Hint 1 — what to copy</summary>

`Point` is immutable, so a copy may share it; the *field* that holds it is what must be separate. A `List<Shape>`
of mutable shapes is different: copy the list **and** each shape in it.

</details>

<details><summary>Hint 2 — deep copy of a group</summary>

`children.stream().map(Shape::copy).toList()` — and because `Group.copy()` calls `copy()` on its children, nested
groups are copied deeply automatically.

</details>

## Stretch goals (optional, not graded)

- Implement `Circle` with `Cloneable`/`clone()` instead of a copy constructor. What goes wrong if a field is `final`,
  or if it refers to a mutable object?
- Make `Circle` and `Rect` immutable records whose `moveBy` returns a new shape. Would you still need `copy()`?
