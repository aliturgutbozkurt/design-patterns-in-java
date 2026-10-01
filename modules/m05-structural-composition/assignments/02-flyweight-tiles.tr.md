# Ödev 02 — Flyweight ile Harita Kareleri

> Modül: m05-structural-composition · Zorluk: ★★☆ · Tahmini süre: 2 saat

## Amaç

Bir strateji oyunu dünyasını karelerden (tile) oluşan bir ızgara üzerine çizer. Her kare tipi "ağır" veri taşır —
8 × 8 bir sprite, bir harita sembolü, bir hareket maliyeti — ve verilen `NaiveTileMap` her hücre için **yeni** bir
`TileType` oluşturur: yalnızca beş çeşit arazi olmasına rağmen 256 × 256 bir harita bunlardan 65 536 tane tutar.
**Flyweight (Sinek Siklet)** kalıbını uygulayın: arazi başına tek bir `TileType` paylaştıran (*içsel* durum)
iş parçacığı güvenli bir kayıt (registry) ve hücreleri yalnızca *hangi* paylaşılan tipin *hangi* konumda olduğunu
hatırlayan (*dışsal* durum) bir harita yazın. Sonra kazancı, öncesi ve sonrası için farklı nesne sayısını sayarak
ölçün.

## Size verilenler

- `exercises/ex02/Terrain.java` — `symbol()`, `movementCost()` ve `walkable()` metotlarıyla `GRASS`, `SAND`,
  `FOREST`, `WATER`, `MOUNTAIN` (aşağıdaki tablo) — **değiştirmeyin**
- `exercises/ex02/TileType.java` — `record TileType(Terrain terrain, char symbol, int movementCost, boolean walkable,
  List<String> sprite)`; `TileType.of(terrain)` yeni bir tane oluşturur — **değiştirmeyin**
- `exercises/ex02/Sprites.java` — `Sprites.forTerrain(terrain)`, 8 × 8 sprite — **değiştirmeyin**
- `exercises/ex02/Point.java` — `record Point(int x, int y)` — **değiştirmeyin**
- `exercises/ex02/TileTypeRegistry.java` — `typeOf(Terrain)`, `createdCount()` — **değiştirmeyin**
- `exercises/ex02/TileMap.java` — `width()`, `height()`, `typeAt(x, y)`, `paint(x, y, terrain)`,
  `movementCost(path)`, `render()` — **değiştirmeyin**
- `exercises/ex02/NaiveTileMap.java` — doğru ama savurgan "önce" hâli — **değiştirmeyin**
- `exercises/ex02/ObjectCounter.java` — `distinctInstances(map)`, tüm hücreler üzerinde kimliğe (identity) dayalı
  sayım — **değiştirmeyin**
- `exercises/ex02/CachingTileTypeRegistry.java`, `SharedTileMap.java` — kodunuzu buraya yazın (`TODO(ex02)`
  işaretleri)

| Arazi | Sembol | Hareket maliyeti | Yürünebilir |
|---|---|---|---|
| `GRASS` | `.` | 1 | evet |
| `SAND` | `:` | 2 | evet |
| `FOREST` | `T` | 3 | evet |
| `WATER` | `~` | — | hayır |
| `MOUNTAIN` | `^` | — | hayır |

## Görevler

1. `CachingTileTypeRegistry`: `typeOf(terrain)` aynı arazi için her çağrıda **aynı nesneyi** döndürür ve onu
   `TileType.of` ile **en fazla bir kez** oluşturur — 1 000 sanal iş parçacığı (virtual thread) aynı anda sorsa
   bile. `createdCount()` kaç tip oluşturulduğunu bildirir (yeni bir kayıt için 0).
2. `SharedTileMap(width, height, registry)`: yeni bir harita tamamen `GRASS`'tır; `paint` tam olarak bir hücreyi
   değiştirir; her hücre kayıttan gelen bir tipe işaret eder, bu yüzden aynı kayıt üzerine kurulan iki harita
   tiplerini paylaşır.
3. `movementCost(path)` yol üzerindeki karelerin maliyetlerini toplar (boş bir yolun maliyeti 0'dır) ve yol
   üzerindeki bir kare yürünebilir değilse `IllegalArgumentException` fırlatır.
4. `render()` her satır için bir sembol satırı döndürür, en üst satır önce gelir, her satır `\n` ile biter.
5. Harita dışındaki koordinatlar `IndexOutOfBoundsException` fırlatır (`typeAt`, `paint` ve `movementCost` içinde).

## Kabul kriterleri

- [ ] `registryReturnsTheSameInstanceForTheSameTerrain`
- [ ] `registryCreatesEachTypeAtMostOnce`
- [ ] `registryIsSafeUnderManyVirtualThreads`
- [ ] `newMapIsAllGrass`
- [ ] `paintChangesOnlyOneCell`
- [ ] `sharedMapHoldsOneInstancePerTerrainUsed` — üç araziyle boyanmış 256 × 256 bir haritada naif haritada
  65 536, sizinkinde tam 3 farklı `TileType` nesnesi vardır
- [ ] `mapsShareTypesThroughTheRegistry`
- [ ] `movementCostSumsThePath`
- [ ] `pathThroughWaterIsRejected`
- [ ] `rendersRowsOfSymbols`
- [ ] `rejectsCoordinatesOutsideTheMap`

## Testleri çalıştırın

```bash
./mvnw -pl modules/m05-structural-composition test -Pexercises -Dtest='Ex02*'
```

Tüm testler yeşil = ödev bitti. `solutions/ex02/` ile ancak kendiniz denedikten **sonra** karşılaştırın.

## İpuçları

<details><summary>İpucu 1 — tek satırda iş parçacığı güvenli bir fabrika</summary>

`ConcurrentHashMap.computeIfAbsent(key, function)`, `function`'ı anahtar başına en fazla bir kez ve atomik olarak
çalıştırır. `HashMap` artı "önce kontrol et, sonra koy" bir yarış durumudur: iki iş parçacığı da "yok" görüp ikisi de
bir tip oluşturabilir. Oluşturmaları `typeOf` içinde değil, fonksiyonun içinde sayın (bir `AtomicInteger`).

</details>

<details><summary>İpucu 2 — bir hücre ne saklar</summary>

Bir hücrenin paylaşılan bir `TileType`'a referanstan başka hiçbir şeye ihtiyacı yoktur; konumu dizideki indistir
(`y * width + x`). Yeni bir harita için `Arrays.fill(cells, registry.typeOf(Terrain.GRASS))`. Sınır kontrolü için
tek bir özel `index(x, y)` metodunu yeniden kullanın.

</details>

## Ek hedefler (isteğe bağlı, notlandırılmaz)

- Ölçün: küçük bir `main` içinde iki 256 × 256 haritayı da oluşturun, canlı tutun ve
  `jcmd <pid> GC.class_histogram` çalıştırın. Her durumda kaç `TileType` ve `ArrayList`/`ImmutableCollections$ListN`
  nesnesi görüyorsunuz?
- `Terrain` bir enum'dur — JDK sabit başına tek bir nesneyi zaten garanti eder. `TileType` da doğrudan bir enum
  olabilir miydi? Neyi kaybederdiniz?
