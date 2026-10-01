# Ödev 02 — Commit Sonrası Yayımlanan Domain Event'lerle Sipariş Yaşam Döngüsü

> Modül: m11-architecture-enterprise · Zorluk: ★★★ · Tahmini süre: 3–4 saat

## Amaç

Bir aggregate'in (küme kökü) dinleyicileri çağırmak yerine olanları **kaydetmesini** sağlayın, durumunu bir port
üzerinden commit edin ve kaydedilen **domain event'leri** (alan olayları) yalnızca commit başarılı olduktan sonra
dağıtın. Üç parça yazacaksınız: `Order` aggregate'i (geçişler ve olaylar), süreç içi bir `EventDispatcher` (tipli
abonelikler, hata işleyici, dağıtım sırasında doğan olaylar için bir kuyruk) ve bunları birleştiren
`OrderLifecycleService` kullanım senaryosu: yükle → aggregate metodu → commit → dağıt.

## Size verilenler

- `exercises/ex02/OrderId.java` — record — **değiştirmeyin**
- `exercises/ex02/OrderStatus.java` — `PLACED`, `PAID`, `SHIPPED`, `CANCELLED` — **değiştirmeyin**
- `exercises/ex02/OrderEvent.java` — sealed: `OrderPlaced(id, totalCents)`, `OrderPaid(id)`, `OrderShipped(id)`,
  `OrderCancelled(id, reason)` (record'lar) — **değiştirmeyin**
- `exercises/ex02/OrderSnapshot.java` — record `(id, status, totalCents)`: deponun sakladığı — **değiştirmeyin**
- `exercises/ex02/OrderStore.java` — dış port `load(OrderId)`, `commit(OrderSnapshot)` (istisna fırlatabilir) —
  **değiştirmeyin**
- `exercises/ex02/Subscription.java` ve `exercises/ex02/OrderLifecycle.java` — **değiştirmeyin**
- `exercises/ex02/Order.java`, `EventDispatcher.java`, `OrderLifecycleService.java` — kodunuz (`TODO(ex02)`
  işaretleri; `Order` ve `EventDispatcher` değiştirebileceğiniz önerilen bir iskelettir)

## Görevler

1. Geçişler: `PLACED → PAID → SHIPPED` ve `PLACED | PAID → CANCELLED`. Başka her şey (iki kez ödeme, ödenmemiş
   siparişi kargolama, kargolanmış siparişi iptal, …) `IllegalStateException` fırlatır — hiçbir şey commit edilmez ve
   dağıtılmaz.
2. `place(totalCents)`: `totalCents ≤ 0` → `IllegalArgumentException`; aksi halde tedarikçiden bir kimlik alın,
   `PLACED` durumunu commit edin, `OrderPlaced` dağıtın, kimliği döndürün. Bilinmeyen kimlik →
   `NoSuchElementException` (`pay`, `ship`, `cancel`, `status`).
3. Her komut **yükle → aggregate metodu (olayı kaydeder) → commit → dağıt** şeklindedir. `commit` istisna fırlatırsa
   istisna yukarı iletilir, hiçbir olay dağıtılmaz ve `status` değişmez.
4. İşleyiciler kendi tiplerindeki olayları alır; `OrderEvent.class` için yazılmış bir işleyici hepsini alır.
   İşleyiciler abonelik sırasıyla çalışır. İstisna fırlatan bir işleyici hata işleyiciye gider; diğer işleyiciler yine
   çalışır ve commit geri alınmaz.
5. Bir işleyicinin içinden verilen komut hemen yürütülür (ve commit edilir), ancak olayları mevcut olayın işleyicileri
   bittikten sonra dağıtılır. Her olay tam bir kez dağıtılır.
6. `Subscription.close()` teslimatı durdurur ve idempotent'tir (tekrar çağrılması zararsızdır).
7. `null` argümanlar (kimlikler, nedenler, tipler, işleyiciler, kurucu argümanları) → `NullPointerException`.

## Kabul kriterleri

- [ ] `placeCommitsThenDispatchesOrderPlaced`
- [ ] `eventsAreDispatchedOnlyAfterCommit`
- [ ] `failedCommitDispatchesNothingAndKeepsState`
- [ ] `payThenShipFollowsLifecycle`
- [ ] `cannotPayTwice`
- [ ] `cannotShipUnpaidOrder`
- [ ] `cannotCancelShippedOrder`
- [ ] `illegalTransitionCommitsAndDispatchesNothing`
- [ ] `cancelCarriesReason`
- [ ] `unknownOrderIsRejected`
- [ ] `rejectsNonPositiveTotal`
- [ ] `typedSubscriberReceivesOnlyItsEventType`
- [ ] `supertypeSubscriberReceivesAllEvents`
- [ ] `handlersRunInSubscriptionOrder`
- [ ] `failingHandlerDoesNotStopOthersOrUndoCommit`
- [ ] `commandFromHandlerIsDispatchedAfterCurrentEvent`
- [ ] `closedSubscriptionReceivesNothing`
- [ ] `eachEventIsDispatchedOnce`
- [ ] `rejectsNullArguments`

Sözleşmedeki sahte (fake) `OrderStore`, test işleyicilerinin yazdığı günlüğe `"commit <id> <status>"` yazar; böylece
testler bir işleyicinin commit'ten önce mi sonra mı çalıştığını görebilir.

## Testleri çalıştırın

```bash
./mvnw -pl modules/m11-architecture-enterprise test -Pexercises -Dtest='Ex02*'
```

Tüm testler yeşil = bitti. `solutions/ex02/` ile ancak kendiniz denedikten **sonra** karşılaştırın.

## İpuçları

<details><summary>İpucu 1 — olaylar "oldu" ile "dağıtıldı" arasında nerede durur?</summary>

Aggregate'in içinde: `pay()`, `ship()`, … metotlarının eklediği özel bir liste ve bir kopya döndürüp listeyi
temizleyen `pullEvents()`. Servis bunu yalnızca `commit` normal döndükten sonra çağırır — fırlatılan bir istisna bu
adımı atlar.

</details>

<details><summary>İpucu 2 — yeniden giren komut</summary>

İşleyici `orders.ship(...)` çağırır; bu commit eder ve ilk dağıtım hâlâ döngüdeyken `dispatch`'i yeniden çağırır.
Olayları bir `ArrayDeque`'e koyun; bir dağıtım zaten çalışıyorsa yalnızca kuyruğa ekleyip dönün — dıştaki döngü
onları sıradaki olarak teslim eder (m07'deki olay yolu ile aynı hile).

</details>

<details><summary>İpucu 3 — tam olarak bir aboneliği kapatmak</summary>

Her kaydı küçük, özel bir record'a sarın ve onu kimliğe göre kaldırın (`h == registration`). Zaten kaldırılmış bir
nesneyi kaldırmak hiçbir şey yapmaz — idempotentlik bedavaya gelir.

</details>

## Ek hedefler (isteğe bağlı, notlandırılmaz)

- Olayları `commit` içinde bir outbox'a (durum ve olaylar birlikte) yazın ve bir aktarıcıdan (relay) dağıtın.
- `OrderStatus` kontrollerini durum üzerinde eksiksiz (exhaustive) bir `switch` ile değiştirin (m08 State).
