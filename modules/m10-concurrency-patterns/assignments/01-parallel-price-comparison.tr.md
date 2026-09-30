# Ödev 01 — Paralel Fiyat Karşılaştırma

> Modül: m10-concurrency-patterns · Zorluk: ★★★ · Tahmini süre: 3 saat

## Amaç

Bir mağaza, bir ürünün fiyatını birkaç tedarikçide karşılaştırıyor. Her tedarikçi yavaş, bloklayan bir uzak çağrıdır;
hata verebilir ya da hiç yanıt vermeyebilir. Onlara sırayla sormak çok yavaştır, en yavaşını beklemek de kabul
edilemez. **Tüm sağlayıcılara aynı anda** soran, karşılaştırmanın tamamına bir **son süre (deadline)** veren, artık
ihtiyaç duymadığı her sağlayıcıyı **iptal eden** ve bir **hata politikası** uygulayan bir karşılaştırıcı yazın. Bu
ödev, görev başına iş parçacığı (thread-per-task) ile "elle kurulmuş yapılandırılmış eşzamanlılıktır"; yalnızca
kesinleşmiş (final) `java.util.concurrent` API'leri kullanılır (önizleme özelliği yok).

## Size verilenler

- `exercises/ex01/Quote.java` — record `Quote(String provider, long priceCents)` — **değiştirmeyin**
- `exercises/ex01/PriceProvider.java` — `String name()`, `Quote quote(String sku) throws Exception` (bloklayan) —
  **değiştirmeyin**
- `exercises/ex01/ProviderResult.java` — sealed: `Priced(Quote)`, `Failed(String provider, String reason)`,
  `TimedOut(String provider)` — **değiştirmeyin**
- `exercises/ex01/Comparison.java` — record `Comparison(String sku, List<ProviderResult> results)` ve
  `Optional<Quote> cheapest()` — **değiştirmeyin**
- `exercises/ex01/FailurePolicy.java` — `BEST_EFFORT`, `FAIL_FAST` — **değiştirmeyin**
- `exercises/ex01/ComparisonFailedException.java` — denetlenmeyen (unchecked) istisna, `provider()` hata veren
  sağlayıcıyı adlandırır — **değiştirmeyin**
- `exercises/ex01/PriceComparator.java` — `Comparison compare(String sku) throws InterruptedException` —
  **değiştirmeyin**
- `exercises/ex01/ParallelPriceComparator.java` — kodunuzu buraya yazın (`TODO(ex01)` işaretleri); kurucu
  `ParallelPriceComparator(List<PriceProvider> providers, Duration deadline, FailurePolicy policy, ExecutorService
  executor)`

## Görevler

1. `null` ya da boş bir sku'yu `IllegalArgumentException` ile reddedin.
2. **Her sağlayıcıyı eşzamanlı** çağırın; her biri, enjekte edilen `ExecutorService` üzerinde kendi görevinde
   çalışsın. Bu executor'ı asla kapatmayın: ömrüne sahibi karar verir.
3. **Sağlayıcı başına tam olarak bir sonuç, sağlayıcı sırasıyla** döndürün (bitiş sırasıyla değil).
4. Son süre `compare` başladığı andan itibaren ölçülür. Süre dolduğunda henüz yanıt vermemiş bir sağlayıcı
   `TimedOut(provider)` olur ve **iptal edilmelidir**; yani iş parçacığı kesilir (interrupt). Bir sağlayıcı hiç
   bitmese bile `compare` son süreden kısa süre sonra döner.
5. `BEST_EFFORT`: sağlayıcının istisnası `Failed(provider, message)` olur, diğer sonuçlar korunur.
6. `FAIL_FAST`: ilk sağlayıcı istisnası hâlâ çalışan tüm sağlayıcıları iptal eder ve `compare`, o sağlayıcıyı
   adlandıran bir `ComparisonFailedException` fırlatır (istisnanın nedeni sağlayıcının istisnasıdır); **son süreyi
   beklemeden**.
7. `cheapest()` (verilmiştir) en düşük `Priced` teklifi döndürür; eşitlikte önceki sağlayıcı kazanır.

## Kabul kriterleri

- [ ] `returnsOneResultPerProviderInProviderOrder`
- [ ] `providersAreCalledConcurrently`
- [ ] `cheapestPicksLowestPricedQuote`
- [ ] `cheapestTieGoesToEarlierProvider`
- [ ] `cheapestIsEmptyWhenNoQuoteSucceeded`
- [ ] `bestEffortReportsFailedProviderAndKeepsOthers`
- [ ] `slowProviderIsReportedAsTimedOut`
- [ ] `slowProviderIsInterruptedAfterDeadline`
- [ ] `failFastThrowsNamingTheFailingProvider`
- [ ] `failFastInterruptsRemainingProviders`
- [ ] `failFastDoesNotWaitForTheDeadline`
- [ ] `emptyProviderListGivesEmptyComparison`
- [ ] `resultsListIsImmutable`
- [ ] `doesNotShutDownTheInjectedExecutor`
- [ ] `rejectsNullOrBlankSku`

## Testleri çalıştırın

```bash
./mvnw -pl modules/m10-concurrency-patterns test -Pexercises -Dtest='Ex01*'
```

Tüm testler yeşil = ödev bitti. `solutions/ex01/` ile ancak kendiniz denedikten **sonra** karşılaştırın.

Testler hiç uyumaz (sleep kullanmaz). "Hiç yanıt vermeyen" bir sağlayıcı, ancak test bittikten sonra açılan bir
mandalı (latch) bekler; bu yüzden bir test ancak kodunuz onu gerçekten iptal ederse ya da onu beklemeyi gerçekten
bırakırsa geçebilir.

## İpuçları

<details><summary>İpucu 1 — invokeAll ile en iyi çaba</summary>

`ExecutorService.invokeAll(tasks, timeout, unit)` tüm görevleri çalıştırır, en fazla `timeout` kadar bekler, bitmemiş
her görevi **kesme (interrupt) ile iptal eder** ve future'ları **görev sırasıyla** döndürür. Ardından
`future.state()` size `SUCCESS`, `FAILED` ya da `CANCELLED` bilgisini verir; `resultNow()` / `exceptionNow()` sonucu
bloklamadan okur. Her sağlayıcı çağrısını, sağlayıcının istisnasını veriye çeviren bir göreve sarın; böylece hangi
sağlayıcının hata verdiğini hâlâ bilirsiniz.

</details>

<details><summary>İpucu 2 — completion service ile ilk hatada dur</summary>

`invokeAll` her zaman tüm görevleri (ya da zaman aşımını) bekler, bu yüzden ilk hatada duramaz. Bir
`ExecutorCompletionService` future'ları **bitiş sırasıyla** verir: `poll(timeLeft, NANOSECONDS)` bir sonraki biten
görevi, son süre geçtiyse `null` döndürür. Her görevin indeksini saklayın ki sonucunu doğru yere koyabilesiniz ve
`finally` bloğunda her future'ı iptal edin (`cancel(true)`). Tek başına bir `CompletableFuture` yetmez:
`CompletableFuture.cancel(true)` çalışan görevi **kesmez** (derse bakın); executor'ın asıl `Future`'ını iptal etmeniz
gerekir.

</details>

<details><summary>İpucu 3 — kesmeler sözleşmenin parçasıdır</summary>

`InterruptedException` yakalayan bir görev onu yutmamalıdır. Ya yeniden fırlatın ya da bayrağı
`Thread.currentThread().interrupt()` ile geri yükleyin. İptal edilen bir sağlayıcı `Failed` olarak değil, `TimedOut`
olarak raporlanır.

</details>

## Ek hedefler (isteğe bağlı, notlandırılmaz)

- Herhangi bir sağlayıcı yanıt verir vermez dönen ve diğerlerini iptal eden üçüncü bir politika, `FIRST_QUOTE`,
  ekleyin.
- Structured Concurrency (JEP 533, JDK 27'de bir **önizleme** API'si) kesinleştiğinde bu ödevin tamamı birkaç satıra
  iner. Aşağıdaki taslak **notlandırılmaz** ve çözümünüze girmez; ödevler yalnızca kesinleşmiş API'ler kullanır:

```java
// snippet — preview API (JEP 533), compile and run with --enable-preview; not part of the assignment
try (var scope = StructuredTaskScope.open(Joiner.<Quote>allSuccessfulOrThrow(),
        config -> config.withTimeout(deadline))) {
    providers.forEach(provider -> scope.fork(() -> provider.quote(sku)));
    List<Quote> quotes = scope.join();   // fail fast: the first failure cancels (interrupts) the rest
}
```
