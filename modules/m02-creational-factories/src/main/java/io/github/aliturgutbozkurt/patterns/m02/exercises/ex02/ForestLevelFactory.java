package io.github.aliturgutbozkurt.patterns.m02.exercises.ex02;

/** Assignment 02 — the forest family. See assignments/02-game-levels.en.md (Türkçe: 02-game-levels.tr.md). */
public class ForestLevelFactory implements LevelFactory {

    @Override
    public Enemy enemy() {
        // TODO(ex02): see the table in the brief; every product reports Biome.FOREST.
        throw new UnsupportedOperationException("TODO(ex02): implement enemy()");
    }

    @Override
    public Obstacle obstacle() {
        // TODO(ex02)
        throw new UnsupportedOperationException("TODO(ex02): implement obstacle()");
    }

    @Override
    public Reward reward() {
        // TODO(ex02)
        throw new UnsupportedOperationException("TODO(ex02): implement reward()");
    }
}
