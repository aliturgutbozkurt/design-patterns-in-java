# Ödev 02 — Abstract Factory ile Oyun Seviyeleri

> Modül: m02-creational-factories · Zorluk: ★★☆ · Tahmini süre: 2 saat

## Amaç

Bir oyunun farklı biyomlarda seviyeleri var. Bir seviyenin düşmanları, engelleri ve ödülleri **aynı biyomdan**
gelmelidir — orman kurdu olan bir çöl seviyesi bir hatadır. Bu tutarlılığı yapısı gereği garanti etmek için bir
**Abstract Factory (Soyut Fabrika)** kullanın, *herhangi bir* fabrikayla çalışan bir seviye üreteci yazın ve aileyi
seçen statik fabrikalar ekleyin.

## Size verilenler

- `exercises/ex02/Biome.java` — `enum Biome { FOREST, DESERT }` — **değiştirmeyin**
- `exercises/ex02/Enemy.java`, `Obstacle.java`, `Reward.java` — soyut ürünler — **değiştirmeyin**
- `exercises/ex02/LevelFactory.java` — soyut fabrika — **değiştirmeyin**
- `exercises/ex02/Level.java` — record `Level(enemies, obstacles, reward)` — **değiştirmeyin**
- `exercises/ex02/ForestLevelFactory.java`, `DesertLevelFactory.java`, `LevelGenerator.java`,
  `LevelFactories.java` — kodunuzu buraya yazın (`TODO(ex02)` işaretleri)

## Görevler

1. İki aileyi yazın. Her ürün kendi ailesinin `Biome` değerini bildirir:

   | Aile | Düşman (can puanı) | Engel (hasar) | Ödül (puan) |
   |---|---|---|---|
   | Orman | Wolf (30) | Fallen log (5) | Mushroom (10) |
   | Çöl | Scorpion (20) | Quicksand (12) | Water flask (15) |

2. `LevelGenerator(LevelFactory)`: `generate(difficulty)`, hepsi kendi fabrikasıyla oluşturulmuş `difficulty` düşman,
   `difficulty` engel ve bir ödül içeren bir `Level` döndürür. Zorluk 1–10 arasında olmalıdır, değilse
   `IllegalArgumentException`. Üreteç hiçbir somut aileyi ya da ürünü adıyla anmamalıdır.
3. `LevelFactories.forBiome(Biome)` — `default` olmadan eksiksiz bir `switch`.
4. `LevelFactories.forName(String)` — büyük/küçük harf duyarsız (`"forest"`, `"Desert"`); bilinmeyen adlar
   `IllegalArgumentException` fırlatır.

## Kabul kriterleri

- [ ] `forestFamilyIsConsistent`
- [ ] `desertFamilyIsConsistent`
- [ ] `productStatsMatchTheTable`
- [ ] `generatorUsesOnlyItsFactory`
- [ ] `difficultyScalesEnemiesAndObstacles`
- [ ] `generatorWorksWithAnyFactory` — test, yalnızca testte var olan bir aile verir
- [ ] `forBiomeReturnsTheMatchingFamily`
- [ ] `forNameIsCaseInsensitive`
- [ ] `unknownNameRejected`
- [ ] `rejectsDifficultyOutsideOneToTen`

## Testleri çalıştırın

```bash
./mvnw -pl modules/m02-creational-factories test -Pexercises -Dtest='Ex02*'
```

Tüm testler yeşil = ödev bitti. `solutions/ex02/` ile ancak kendiniz denedikten **sonra** karşılaştırın.

## İpuçları

<details><summary>İpucu 1 — tek satırda ürünler</summary>

Bileşenleri `name`, `hitPoints` ve `biome` olan bir record, `Enemy`'nin istediği erişim metotlarına zaten sahiptir:
`record SimpleEnemy(String name, int hitPoints, Biome biome) implements Enemy {}`.

</details>

<details><summary>İpucu 2 — bir şeyden n tane</summary>

`Stream.generate(factory::enemy).limit(n).toList()` fabrikayı `n` kez çağırır.

</details>

## Ek hedefler (isteğe bağlı, notlandırılmaz)

- Bir `SNOW` biyomu ekleyin. Derleyici hangi dosyaları değiştirmenizi istedi ve bu neden iyi bir şey?
- Factory Method (Fabrika Metodu) tasarımıyla karşılaştırın: her biyom için bir üreteç alt sınıfı. Ne zorlaşır?
