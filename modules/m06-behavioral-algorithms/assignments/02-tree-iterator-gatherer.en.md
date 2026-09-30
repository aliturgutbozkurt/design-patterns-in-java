# Assignment 02 — In-Order Tree Iterator and a Custom Gatherer

> Module: m06-behavioral-algorithms · Difficulty: ★★★ · Estimated time: 3 h

## Goal

A binary search tree gives its keys in sorted order when you walk it **in order**. The textbook solution is
recursive, and it overflows the call stack on a deep tree. Here you write the **Iterator** version: it is lazy and
works without recursion. Then you expose it as a correct `Stream` and write a stateful **custom `Gatherer`** that
groups consecutive elements into runs, e.g. the keys `1,2,3,7,8,10` into the ranges `[1,2,3] [7,8] [10]`.

## What you are given

- `exercises/ex02/BinaryTree.java`: `sealed interface BinaryTree<T> permits Empty, Branch` with the helpers
  `empty()`, `leaf(value)` and `branch(left, value, right)`. **Do not modify.**
- `exercises/ex02/Empty.java`, `Branch.java`: the tree records (`Branch` values are never `null`). **Do not modify.**
- `exercises/ex02/TreeTools.java`: `inOrder`, `stream`, `runs`. **Do not modify.**
- `exercises/ex02/InOrderIterator.java`, `RunsGatherer.java`, `DefaultTreeTools.java`: your code goes here
  (`TODO(ex02)` markers).

## Tasks

1. `InOrderIterator<T>`: left subtree, value, right subtree, **without recursion**. Keep an explicit stack
   (`Deque`) of the branches whose value is still to come; it never holds more than one branch per level (O(height)
   memory). `next()` past the end throws `NoSuchElementException`; `remove()` stays unsupported (the default
   `Iterator.remove` already throws). Two iterators over the same tree are independent.
2. `DefaultTreeTools.inOrder(tree)` returns your iterator; `stream(tree)` returns a **sequential** stream of the same
   values whose spliterator reports `ORDERED | NONNULL`.
3. `RunsGatherer.runs(sameRun)`: a sequential `Gatherer` (initializer, integrator, finisher). It groups
   **consecutive** elements while `sameRun.test(previous, current)` is true, and emits each run as an
   **unmodifiable** list. The finisher emits the last run; an empty stream gives no runs. The integrator must return
   the result of `downstream.push(...)`, so an infinite stream with `limit` stops.
4. `null` arguments throw `NullPointerException`.

## Acceptance criteria

- [ ] `inOrderVisitsBinarySearchTreeInSortedOrder`
- [ ] `emptyTreeHasNoElements`
- [ ] `nextAfterEndThrowsNoSuchElement`
- [ ] `removeIsUnsupported`
- [ ] `independentIteratorsDoNotInterfere`
- [ ] `deepTreeDoesNotOverflowTheStack` (a 100 000-deep left spine)
- [ ] `streamMatchesIterator`
- [ ] `streamReportsOrderedAndNonNull`
- [ ] `runsGroupsConsecutiveElements`
- [ ] `runsEmitsTheLastRun`
- [ ] `runsOfEmptyStreamIsEmpty`
- [ ] `runsListsAreUnmodifiable`
- [ ] `runsStopsEarlyOnInfiniteStream`
- [ ] `treeStreamGatheredIntoRuns`
- [ ] `rejectsNullArguments`

## Run the tests

```bash
./mvnw -pl modules/m06-behavioral-algorithms test -Pexercises -Dtest='Ex02*'
```

All tests green = done. Compare with `solutions/ex02/` only **after** you have tried.

## Hints

<details><summary>Hint 1 — the iterative in-order walk</summary>

"Push the left spine": starting at a node, push every `Branch` while you go left, until you reach `Empty`. Do this
in the constructor for the root. `next()` pops a branch, pushes the left spine of its **right** subtree, and returns
the popped value. A `switch` over the sealed tree (`case Empty<T> _ ->`, `case Branch<T> b ->`) needs no `default`.
Do not call `toString`, `equals` or `hashCode` on a deep tree: the record versions recurse.

</details>

<details><summary>Hint 2 — the stream from the iterator</summary>

You do not need to write a `Spliterator` by hand. Wrap your iterator:

```java
// snippet
StreamSupport.stream(Spliterators.spliteratorUnknownSize(inOrder(tree), Spliterator.ORDERED | Spliterator.NONNULL), false)
```

The main work of this assignment is the iterator and the gatherer; the stream is two lines.

</details>

<details><summary>Hint 3 — the gatherer's state</summary>

A small local class (declared inside `runs`) with a `List<T>` field is the state. In the integrator: if the list is
not empty and `sameRun` says the new element does not belong, push the finished run (an unmodifiable list) and start
a new list; then add the element. Return `downstream.push(...)`'s result when you pushed, otherwise `true`. The
finisher pushes what is left.

</details>

## Stretch goals (optional, not graded)

- Add `preOrder` and `postOrder` iterators. Which one is the hardest to write without recursion?
- Write a `Spliterator` for the tree that can `trySplit` (hand off the left subtree) and compare a parallel sum with
  the sequential one.
