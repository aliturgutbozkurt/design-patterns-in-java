# Ödev 02 — Ortanca Sıralı (In-Order) Ağaç Yineleyicisi ve Özel bir Gatherer

> Modül: m06-behavioral-algorithms · Zorluk: ★★★ · Tahmini süre: 3 saat

## Amaç

Bir ikili arama ağacı **ortanca sırayla** (in-order) dolaşıldığında anahtarlarını sıralı verir. Ders kitabındaki
çözüm özyinelemelidir (recursive) ve derin bir ağaçta çağrı yığınını taşırır. Bu ödevde **Iterator (Yineleyici)**
sürümünü yazıyorsunuz: tembeldir (lazy) ve özyineleme olmadan çalışır. Ardından onu doğru bir `Stream` olarak
sunuyor ve ardışık öğeleri gruplara (run) ayıran, durum tutan **özel bir `Gatherer`** yazıyorsunuz; örneğin
`1,2,3,7,8,10` anahtarları `[1,2,3] [7,8] [10]` aralıklarına ayrılır.

## Size verilenler

- `exercises/ex02/BinaryTree.java`: `empty()`, `leaf(value)` ve `branch(left, value, right)` yardımcılarıyla
  `sealed interface BinaryTree<T> permits Empty, Branch`. **Değiştirmeyin.**
- `exercises/ex02/Empty.java`, `Branch.java`: ağaç record'ları (`Branch` değerleri asla `null` değildir).
  **Değiştirmeyin.**
- `exercises/ex02/TreeTools.java`: `inOrder`, `stream`, `runs`. **Değiştirmeyin.**
- `exercises/ex02/InOrderIterator.java`, `RunsGatherer.java`, `DefaultTreeTools.java`: kodunuzu buraya yazın
  (`TODO(ex02)` işaretleri).

## Görevler

1. `InOrderIterator<T>`: sol alt ağaç, değer, sağ alt ağaç; **özyineleme olmadan**. Değeri henüz verilmemiş
   dalların açık bir yığınını (`Deque`) tutun; bu yığın her seviyede en fazla bir dal içerir (O(yükseklik) bellek).
   Sondan sonra `next()` `NoSuchElementException` fırlatır; `remove()` desteklenmez (varsayılan `Iterator.remove`
   zaten fırlatır). Aynı ağaç üzerindeki iki yineleyici birbirinden bağımsızdır.
2. `DefaultTreeTools.inOrder(tree)` yineleyicinizi döndürür; `stream(tree)` aynı değerlerin, spliterator'ı
   `ORDERED | NONNULL` bildiren **sıralı (sequential)** bir akışını döndürür.
3. `RunsGatherer.runs(sameRun)`: sıralı bir `Gatherer` (başlatıcı, bütünleştirici, bitirici). `sameRun.test(önceki,
   şimdiki)` doğru olduğu sürece **ardışık** öğeleri gruplar ve her grubu **değiştirilemez** bir liste olarak
   yayar. Bitirici son grubu yayar; boş bir akış hiç grup vermez. Bütünleştirici `downstream.push(...)`'ın sonucunu
   döndürmelidir; böylece `limit` uygulanmış sonsuz bir akış durur.
4. `null` argümanlar `NullPointerException` fırlatır.

## Kabul kriterleri

- [ ] `inOrderVisitsBinarySearchTreeInSortedOrder`
- [ ] `emptyTreeHasNoElements`
- [ ] `nextAfterEndThrowsNoSuchElement`
- [ ] `removeIsUnsupported`
- [ ] `independentIteratorsDoNotInterfere`
- [ ] `deepTreeDoesNotOverflowTheStack` (100 000 derinlikte bir sol omurga)
- [ ] `streamMatchesIterator`
- [ ] `streamReportsOrderedAndNonNull`
- [ ] `runsGroupsConsecutiveElements`
- [ ] `runsEmitsTheLastRun`
- [ ] `runsOfEmptyStreamIsEmpty`
- [ ] `runsListsAreUnmodifiable`
- [ ] `runsStopsEarlyOnInfiniteStream`
- [ ] `treeStreamGatheredIntoRuns`
- [ ] `rejectsNullArguments`

## Testleri çalıştırın

```bash
./mvnw -pl modules/m06-behavioral-algorithms test -Pexercises -Dtest='Ex02*'
```

Tüm testler yeşil = ödev bitti. `solutions/ex02/` ile ancak kendiniz denedikten **sonra** karşılaştırın.

## İpuçları

<details><summary>İpucu 1 — özyinelemesiz ortanca sıralı dolaşma</summary>

"Sol omurgayı yığına koy": bir düğümden başlayıp sola giderken her `Branch`'i yığına koyun, `Empty`'ye ulaşana kadar.
Kök için bunu kurucuda yapın. `next()` bir dalı yığından alır, onun **sağ** alt ağacının sol omurgasını yığına koyar
ve alınan değeri döndürür. Mühürlü ağaç üzerinde bir `switch` (`case Empty<T> _ ->`, `case Branch<T> b ->`)
`default` gerektirmez. Derin bir ağaçta `toString`, `equals` ya da `hashCode` çağırmayın: record sürümleri
özyinelemelidir.

</details>

<details><summary>İpucu 2 — yineleyiciden akış</summary>

Elle bir `Spliterator` yazmanıza gerek yok. Yineleyicinizi sarmalayın:

```java
// snippet
StreamSupport.stream(Spliterators.spliteratorUnknownSize(inOrder(tree), Spliterator.ORDERED | Spliterator.NONNULL), false)
```

Bu ödevin asıl işi yineleyici ve gatherer'dır; akış iki satırdır.

</details>

<details><summary>İpucu 3 — gatherer'ın durumu</summary>

Durum, `runs` içinde tanımlanan ve bir `List<T>` alanı olan küçük bir yerel sınıftır. Bütünleştiricide: liste boş
değilse ve `sameRun` yeni öğenin bu gruba ait olmadığını söylüyorsa, biten grubu (değiştirilemez bir liste olarak)
aşağı akışa gönderin ve yeni bir liste başlatın; sonra öğeyi ekleyin. Gönderdiyseniz `downstream.push(...)`'ın
sonucunu, göndermediyseniz `true` döndürün. Bitirici kalanı gönderir.

</details>

## Ek hedefler (isteğe bağlı, notlandırılmaz)

- `preOrder` ve `postOrder` yineleyicileri ekleyin. Hangisini özyineleme olmadan yazmak en zordur?
- Ağaç için `trySplit` yapabilen (sol alt ağacı devreden) bir `Spliterator` yazın ve paralel toplamı sıralı toplamla
  karşılaştırın.
