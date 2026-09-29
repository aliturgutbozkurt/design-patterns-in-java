# Ödev 01 — Restoran Menüsü Composite

> Modül: m05-structural-composition · Zorluk: ★★☆ · Tahmini süre: 2 saat

## Amaç

Bir restoran menüsü bir ağaçtır: akşam yemeği menüsü alt menüleri (başlangıçlar, ana yemekler, tatlılar), alt
menüler de yemekleri içerir — ama bir menü doğrudan bir yemek de içerebilir ve tek bir yemek de kendi başına geçerli
bir "menü"dür. Ağaç, iki **record** gerçekleştirmesi olan **sealed** (mühürlü) bir arayüz olarak verilmiştir. Sizin
işiniz modern Composite (Bileşik) deseninin diğer yarısı: ağaç işlemlerini (sayımlar, toplamlar, süzülmüş listeler,
yol döndüren bir arama, girintili bir çıktı) sealed tip üzerinde **özyinelemeli, eksiksiz (exhaustive) `switch`
ifadeleri** olarak yazmak.

## Size verilenler

- `exercises/ex01/MenuComponent.java` — `sealed interface MenuComponent permits MenuItem, Menu` — **değiştirmeyin**
- `exercises/ex01/MenuItem.java` — `record MenuItem(String name, int priceCents, boolean vegetarian)`; ad boş
  olamaz, fiyat ≥ 0 — **değiştirmeyin**
- `exercises/ex01/Menu.java` — `record Menu(String name, List<MenuComponent> children)`; çocuklar `List.copyOf` ile
  kopyalanır, aynı adlı çocuklar reddedilir; `Menu.of(name, children...)` — **değiştirmeyin**
- `exercises/ex01/MenuQueries.java` — gerçekleştireceğiniz altı işlem (Javadoc'a bakın) — **değiştirmeyin**
- `exercises/ex01/MenuReport.java` — kodunuzu buraya yazın (`TODO(ex01)` işaretleri)

## Görevler

1. `itemCount` ve `totalCents`: bir yemek 1 sayılır ve kendi fiyatını katar; bir menü çocuklarının sonuçlarını
   toplar. Boş bir menü 0 verir.
2. `itemNames` ve `vegetarian`: tüm yemekler **menü sırasıyla, önce derinlik (depth-first)** gezilir (bir alt menü,
   bir sonraki kardeşe geçilmeden bitirilir); `vegetarian` yalnızca vejetaryen işaretli yemekleri tutar.
3. `pathTo(root, itemName)`: kökten o adı taşıyan ilk **yemeğe** kadar olan adlar, `" > "` ile birleştirilmiş, ör.
   `Dinner > Desserts > Tiramisu`; yoksa `Optional.empty()` (o adı taşıyan bir *menü* sayılmaz).
4. `render(root)`: her düğüm için bir satır, her seviye için iki boşluk girinti, her satır `\n` ile biter. Bir menü
   kendi adıdır; bir yemek `- <ad> <avro>.<sent>` biçimindedir, vejetaryense sonuna ` (v)` eklenir:

   ```text
   Dinner
     Starters
       - Soup 4.50 (v)
       - Calamari 7.00
     - Water 1.50 (v)
   ```

5. Her metot `null` bir argüman için `NullPointerException` fırlatır (`Objects.requireNonNull`).

`MenuComponent` üzerinde record desenleriyle bir `switch` kullanın — `instanceof` zincirleri ve `default` dalı
olmasın. Testler yalnızca davranışı denetler, ama alıştırmanın asıl amacı kodun biçimidir.

## Kabul kriterleri

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

## Testleri çalıştırın

```bash
./mvnw -pl modules/m05-structural-composition test -Pexercises -Dtest='Ex01*'
```

Tüm testler yeşil = ödev bitti. `solutions/ex01/` ile ancak kendiniz denedikten **sonra** karşılaştırın.

## İpuçları

<details><summary>İpucu 1 — her işlemin biçimi</summary>

```text
return switch (root) {
    case MenuItem(var name, var price, var veg) -> …   // yaprak durumu
    case Menu(var name, var children) -> …             // çocukların sonuçlarını birleştir
};
```

İhtiyacınız olmayan record bileşenleri için `_` kullanın, ör. `case MenuItem(var _, var price, var _) -> price`.

</details>

<details><summary>İpucu 2 — yollar ve çıktı</summary>

`pathTo` için her çocuğa *kendi* yolunu sorun ve bulunan ilk yolun önüne menünün adını ekleyin (`Optional.map`,
`Optional::stream` + `findFirst`). `render` için o anki derinliği özyineleme boyunca aşağı taşıyın ve fiyatı
`String.format(Locale.ROOT, "%d.%02d", cents / 100, cents % 100)` ile biçimlendirin.

</details>

## Ek hedefler (isteğe bağlı, notlandırılmaz)

- Üçüncü bir düğüm tipi ekleyin: `Combo(String name, List<MenuItem> items, int discountCents)`. Hangi metotlarınız
  derlenmez hâle gelir — ve bu neden *iyi* bir şeydir? Yeni bir işlem eklemenin her düğüm sınıfını değiştirmek
  anlamına geldiği klasik Composite ile karşılaştırın.
- Tek bir `switch` ile `Optional<MenuItem>` döndüren `cheapest(MenuComponent)` metodunu yazın.
