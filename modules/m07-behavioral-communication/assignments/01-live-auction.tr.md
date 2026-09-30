# Ödev 01 — Canlı Açık Artırma Bildirimleri

> Modül: m07-behavioral-communication · Zorluk: ★★☆ · Tahmini süre: 2–3 saat

## Amaç

Bir çevrim içi açık artırma evi her teklifi canlı gösterir: teklif verenlerin ekranları, açık artırmacının paneli ve bir
dolandırıcılık izleyicisi aynı olaylara tepki verir, ama açık artırma bunların hiçbirini tanımamalıdır. Bir Observer
(Gözlemci) kalıbının **öznesini (subject)** yazın: dinleyiciler tüm olaylara ya da tek bir olay tipine abone olur, bir
abonelikten çıkma tutamacı (handle) alır ve iyi tanımlanmış kurallarla bildirim alır — abonelik sırasıyla, bir teslimat
sırasında abone olmaya/abonelikten çıkmaya ve hata fırlatan dinleyicilere karşı güvenli biçimde.

## Size verilenler

- `exercises/ex01/Bid.java` — record `Bid(String bidder, long amountCents)` — **değiştirmeyin**
- `exercises/ex01/AuctionEvent.java` — sealed: `BidPlaced`, `BidRejected`, `Sold`, `Unsold` (record'lar) — **değiştirmeyin**
- `exercises/ex01/Subscription.java` — `void close()`, try-with-resources ile kullanılabilir — **değiştirmeyin**
- `exercises/ex01/Auction.java` — `subscribe(listener)`, `subscribe(type, listener)`, `placeBid`, `close`,
  `highestBid` — **değiştirmeyin**
- `exercises/ex01/LiveAuction.java` — kodunuzu buraya yazın (`TODO(ex01)` işaretleri)

## Görevler

1. Kurucu `LiveAuction(long startingPriceCents, long minIncrementCents, Consumer<RuntimeException> errorHandler)`;
   `null` bir hata işleyici `NullPointerException` fırlatır.
2. `placeBid`: kabul edilen ilk teklif başlangıç fiyatına ≥, sonraki her teklif en yüksek + asgari artışa ≥ olmalıdır.
   Aksi hâlde `"below starting price"` / `"below minimum increment"` gerekçeli `BidRejected` yayımlayın. `close()`'dan
   sonra her teklif `"auction closed"` ile reddedilir. Kabul edilen bir teklif `BidPlaced` yayımlar ve `highestBid()`
   olur.
3. `close()`, `Sold(highest)` ya da `Unsold()` olayını tam bir kez yayımlar; ikinci bir `close()` hiçbir şey yapmaz.
4. `subscribe(listener)` her olayı, `subscribe(type, listener)` yalnızca o tipteki olayları teslim eder. Dinleyiciler
   abonelik sırasıyla bilgilendirilir. `Subscription.close()` yalnızca o kaydı kaldırır ve idempotenttir — aynı dinleyici
   iki kez abone olmuş olsa bile.
5. Bir teslimat *sırasında* abone olmak ya da abonelikten çıkmak bir sonraki olaydan itibaren geçerli olur.
6. Hata fırlatan bir dinleyici diğerlerini durdurmaz; istisnası hata işleyiciye gider.
7. `null` teklif, dinleyici ya da tip `NullPointerException` fırlatır.

## Kabul kriterleri

- [ ] `acceptsFirstBidAtStartingPrice`
- [ ] `rejectsBidBelowStartingPrice`
- [ ] `rejectsBidBelowMinimumIncrement`
- [ ] `highestBidReflectsAcceptedBidsOnly`
- [ ] `listenersNotifiedInSubscriptionOrder`
- [ ] `typedSubscriptionReceivesOnlyItsEventType`
- [ ] `closedSubscriptionReceivesNothing`
- [ ] `closingASubscriptionTwiceIsHarmless`
- [ ] `unsubscribingDuringDeliveryTakesEffectFromTheNextEvent`
- [ ] `subscribingDuringDeliveryTakesEffectFromTheNextEvent`
- [ ] `failingListenerDoesNotStopOthers`
- [ ] `closeWithBidsPublishesSold`
- [ ] `closeWithoutBidsPublishesUnsold`
- [ ] `closeIsPublishedOnlyOnce`
- [ ] `bidsAfterCloseAreRejected`
- [ ] `rejectsNullArguments`

## Testleri çalıştırın

```bash
./mvnw -pl modules/m07-behavioral-communication test -Pexercises -Dtest='Ex01*'
```

Tüm testler yeşil = ödev bitti. `solutions/ex01/` ile ancak kendiniz denedikten **sonra** karşılaştırın.

## İpuçları

<details><summary>İpucu 1 — hangi liste?</summary>

`CopyOnWriteArrayList` bir anlık görüntü (snapshot) üzerinde dolaşır: döngü sürerken eklenen ya da çıkarılan
dinleyicileri yalnızca bir sonraki döngü görür. Bu, ek kod yazmadan tam olarak 5. kuraldır. (Sıradan bir `ArrayList` ve
döngüden önce `List.copyOf(...)` da işe yarar.)

</details>

<details><summary>İpucu 2 — doğru kaydı kaldırmak</summary>

`list.remove(listener)` *eşit olan ilk* elemanı kaldırır — aynı lambda iki kez abone olduysa yanlış olanı. Her aboneliği
kimliğiyle (identity) karşılaştırılan küçük, özel bir nesneye sarın ve onun yerine o nesneyi kaldırın.

</details>

<details><summary>İpucu 3 — tipli abonelikler</summary>

Tipli bir abonelik, süzgeç uygulayan bir dinleyiciden ibarettir: `event -> { if (type.isInstance(event)) listener.accept(type.cast(event)); }`.

</details>

## Ek hedefler (isteğe bağlı, notlandırılmaz)

- `LiveAuction`'ı birden çok iş parçacığından kullanılabilecek şekilde güvenli yapın. Hangi durumun korunması gerekir,
  yavaş dinleyiciler ne olacak?
- Dinleyici listesini bir `SubmissionPublisher<AuctionEvent>` ile değiştirin. Ne kazanırsınız (geri basınç, eşzamansız
  teslimat) ve ne kaybedersiniz (sıralama garantileri, hata işleyici)?
