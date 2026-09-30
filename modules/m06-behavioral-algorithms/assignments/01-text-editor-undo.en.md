# Assignment 01 — Text Editor with Command-Based Undo/Redo

> Module: m06-behavioral-algorithms · Difficulty: ★★☆ · Estimated time: 3 h

## Goal

Every serious editor has undo and redo. Here you build them with the **Command** pattern. Each change to the text
becomes a command object that can execute itself and undo itself. The editor (the invoker) keeps an undo stack and a
redo stack of those commands. Two extras make it realistic: a **group** of edits that undoes as one step (a macro
command, e.g. "find and replace all"), and a **bounded history** that forgets the oldest steps.

## What you are given

- `exercises/ex01/Edit.java`: `sealed interface Edit permits Insert, Delete, Replace`. **Do not modify.**
- `exercises/ex01/Insert.java`, `Delete.java`, `Replace.java`: the edits as records. **Do not modify.**
- `exercises/ex01/Editor.java`: `text`, `apply`, `undo`, `redo`, `canUndo`, `canRedo`, `group`, `undoHistory`.
  **Do not modify.**
- `exercises/ex01/CommandEditor.java`: your code goes here (`TODO(ex01)` markers). Add your command classes to the
  same package.

## Tasks

1. `CommandEditor(String initialText, int maxHistory)`: `null` text throws `NullPointerException`; `maxHistory < 1`
   throws `IllegalArgumentException`.
2. Write one command class per edit type (`InsertCommand`, `DeleteCommand`, `ReplaceCommand`) with `execute` and
   `undo`. A `Delete` or `Replace` must remember the text it removed, so that undo puts back **exactly** that text.
3. `apply(edit)`: first check the position and length against the current text. `Insert` allows
   `0 <= position <= length`; `Delete`/`Replace` need `position >= 0`, `length >= 0` and
   `position + length <= text length`. An invalid edit throws `IndexOutOfBoundsException` and changes nothing.
   Otherwise execute the command and push it on the undo stack. A new step clears the redo stack.
4. `undo()` / `redo()` move one step between the two stacks and return `false` when there is nothing to do.
   `canUndo()` / `canRedo()` tell whether they would do something.
5. `group(edits)`: run the consumer with this editor. Every edit it applies becomes **one** undo step (a group or
   macro command). A group inside a group joins the outer group. If the consumer throws, undo the edits that group
   already made, in reverse order, and rethrow the same exception.
6. Keep at most `maxHistory` undo steps; when there are more, drop the oldest.
7. `undoHistory()` lists the edits that can be undone, most recent first (the edits of a group are listed too).
8. `null` arguments to `apply` and `group` throw `NullPointerException`.

## Acceptance criteria

- [ ] `appliesInsertDeleteAndReplace`
- [ ] `undoRestoresPreviousText`
- [ ] `undoRestoresExactDeletedText`
- [ ] `redoReappliesUndoneEdit`
- [ ] `newEditClearsRedo`
- [ ] `undoAndRedoOnEmptyHistoryReturnFalse`
- [ ] `invalidEditThrowsAndLeavesTextUnchanged`
- [ ] `groupUndoesAsOneStep`
- [ ] `nestedGroupsJoinTheOuterGroup`
- [ ] `failedGroupIsRolledBackAndRethrown`
- [ ] `historyIsBoundedToMaxSteps`
- [ ] `undoHistoryListsMostRecentFirst`
- [ ] `rejectsNullArguments`

## Run the tests

```bash
./mvnw -pl modules/m06-behavioral-algorithms test -Pexercises -Dtest='Ex01*'
```

All tests green = done. Compare with `solutions/ex01/` only **after** you have tried.

## Hints

<details><summary>Hint 1 — what a command must remember</summary>

An `InsertCommand` can undo itself from the edit alone: delete `text.length()` characters at `position`. A
`DeleteCommand` cannot: the characters are gone after `execute`. Store `text.substring(position, position + length)`
in a field **inside `execute`**, before deleting. `StringBuilder` has `insert`, `delete` and `replace`.

</details>

<details><summary>Hint 2 — stacks and the bounded history</summary>

`ArrayDeque` is a good stack: `push`, `poll` (returns `null` when empty) and `removeLast` to drop the oldest entry
when `size() > maxHistory`. `Objects.checkIndex` and `Objects.checkFromIndexSize` throw exactly the
`IndexOutOfBoundsException` you need.

</details>

<details><summary>Hint 3 — groups</summary>

While a group is open, collect the commands in a list instead of pushing them. When the outermost group ends, push
one `GroupCommand(list)` that executes the list in order and undoes it in reverse order. For nesting and rollback,
remember the list size when a group starts. If the consumer throws, undo and remove everything after that mark.

</details>

## Stretch goals (optional, not graded)

- Merge consecutive single-character inserts into one undo step, the way real editors merge typing.
- Replace the command classes with an exhaustive `switch` that computes the *inverse* edit (see the spreadsheet
  example in the lesson). What do you gain and what do you lose?
