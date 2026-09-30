package io.github.aliturgutbozkurt.patterns.m02.exercises.ex02;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/** Assignment 02 — game levels with an Abstract Factory. Each test is one acceptance criterion of the brief. */
public abstract class Ex02Contract {

    protected abstract LevelFactory forestFactory();

    protected abstract LevelFactory desertFactory();

    /** Generates a level with the student's (or solution's) generator over {@code factory}. */
    protected abstract Level generate(LevelFactory factory, int difficulty);

    protected abstract LevelFactory forBiome(Biome biome);

    protected abstract LevelFactory forName(String name);

    record RobotEnemy(String name, int hitPoints, Biome biome) implements Enemy {}

    record LaserObstacle(String name, int damage, Biome biome) implements Obstacle {}

    record BatteryReward(String name, int points, Biome biome) implements Reward {}

    /** A family that exists only in this test — the generator must accept it unchanged. */
    static final class TestFactory implements LevelFactory {
        @Override public Enemy enemy() { return new RobotEnemy("Robot", 99, Biome.DESERT); }
        @Override public Obstacle obstacle() { return new LaserObstacle("Laser", 50, Biome.DESERT); }
        @Override public Reward reward() { return new BatteryReward("Battery", 1, Biome.DESERT); }
    }

    private static void assertFamily(LevelFactory factory, Biome biome) {
        assertThat(Stream.of(factory.enemy().biome(), factory.obstacle().biome(), factory.reward().biome()))
                .containsOnly(biome);
    }

    @Test
    void forestFamilyIsConsistent() {
        assertFamily(forestFactory(), Biome.FOREST);
    }

    @Test
    void desertFamilyIsConsistent() {
        assertFamily(desertFactory(), Biome.DESERT);
    }

    @Test
    void productStatsMatchTheTable() {
        var forest = forestFactory();
        assertThat(forest.enemy().name()).isEqualTo("Wolf");
        assertThat(forest.enemy().hitPoints()).isEqualTo(30);
        assertThat(forest.obstacle().name()).isEqualTo("Fallen log");
        assertThat(forest.obstacle().damage()).isEqualTo(5);
        assertThat(forest.reward().name()).isEqualTo("Mushroom");
        assertThat(forest.reward().points()).isEqualTo(10);
        var desert = desertFactory();
        assertThat(desert.enemy().name()).isEqualTo("Scorpion");
        assertThat(desert.enemy().hitPoints()).isEqualTo(20);
        assertThat(desert.obstacle().name()).isEqualTo("Quicksand");
        assertThat(desert.obstacle().damage()).isEqualTo(12);
        assertThat(desert.reward().name()).isEqualTo("Water flask");
        assertThat(desert.reward().points()).isEqualTo(15);
    }

    @Test
    void generatorUsesOnlyItsFactory() {
        Level level = generate(forestFactory(), 3);
        assertThat(level.enemies()).extracting(Enemy::biome).containsOnly(Biome.FOREST);
        assertThat(level.obstacles()).extracting(Obstacle::biome).containsOnly(Biome.FOREST);
        assertThat(level.reward().biome()).isEqualTo(Biome.FOREST);
    }

    @Test
    void difficultyScalesEnemiesAndObstacles() {
        assertThat(generate(desertFactory(), 1).enemies()).hasSize(1);
        Level hard = generate(desertFactory(), 7);
        assertThat(hard.enemies()).hasSize(7);
        assertThat(hard.obstacles()).hasSize(7);
        assertThat(hard.reward()).isNotNull();
    }

    @Test
    void generatorWorksWithAnyFactory() {
        Level level = generate(new TestFactory(), 2);
        assertThat(level.enemies()).extracting(Enemy::name).containsExactly("Robot", "Robot");
        assertThat(level.reward().name()).isEqualTo("Battery");
    }

    @Test
    void forBiomeReturnsTheMatchingFamily() {
        assertFamily(forBiome(Biome.FOREST), Biome.FOREST);
        assertFamily(forBiome(Biome.DESERT), Biome.DESERT);
    }

    @Test
    void forNameIsCaseInsensitive() {
        assertFamily(forName("desert"), Biome.DESERT);
        assertFamily(forName("Forest"), Biome.FOREST);
        assertFamily(forName("DESERT"), Biome.DESERT);
    }

    @Test
    void unknownNameRejected() {
        assertThatIllegalArgumentException().isThrownBy(() -> forName("ocean"));
    }

    @Test
    void rejectsDifficultyOutsideOneToTen() {
        assertThatIllegalArgumentException().isThrownBy(() -> generate(forestFactory(), 0));
        assertThatIllegalArgumentException().isThrownBy(() -> generate(forestFactory(), 11));
        assertThat(generate(forestFactory(), 10).enemies()).hasSize(10);
    }
}
