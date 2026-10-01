# Ödev 01 — Ports and Adapters ile PatternShop Ödeme Akışı

> Modül: m11-architecture-enterprise · Zorluk: ★★★ · Tahmini süre: 3–4 saat

## Amaç

Bitirme projesinin çekirdeğini kurun: bir sepeti **yalnızca portları** (çekirdeğin sahip olduğu arayüzleri)
kullanarak ödemeye dönüştüren bir uygulama servisi ve bir dış (outbound) adaptör — siparişler için bellek içi bir
**Repository (Depo)**. Servis hiçbir adaptör sınıfının adını anmadığı için aynı akış her katalog, ödeme sağlayıcısı,
depo ya da olay yoluyla çalışır; sözleşmedeki bir ArchUnit kuralı bunu kanıtlar. İş kaynaklı retler mühürlü (sealed)
bir sonuç tipinin değerleridir, altyapı hataları istisna olarak kalır ve sipariş, olay yayımlanmadan **önce**
kaydedilir.

## Size verilenler

- `exercises/ex01/Sku.java`, `Money.java` (negatif olmayan kuruş; `plus`, `times`), `OrderId.java`, `CartItem.java`,
  `Product.java` (`price` ve `stock` ile), `OrderLine.java`, `Order.java` (toplamı satırların toplamına eşit
  olmalıdır), `OrderPlaced.java` — record'lar — **değiştirmeyin**
- `exercises/ex01/CheckoutUseCase.java` — iç (inbound) port
  `CheckoutResult checkout(String customer, List<CartItem> cart)` — **değiştirmeyin**
- `exercises/ex01/CheckoutResult.java` — sealed: `Confirmed(Order)`, `Rejected(String reason)` — **değiştirmeyin**
- `exercises/ex01/ProductCatalog.java`, `PaymentPort.java`, `OrderRepository.java`, `EventPublisher.java`,
  `OrderIdGenerator.java` — dış portlar — **değiştirmeyin**
- `exercises/ex01/CheckoutService.java` — uygulama servisiniz (`TODO(ex01)` işaretleri)
- `exercises/ex01/adapter/InMemoryOrderRepository.java` — dış adaptörünüz (`TODO(ex01)` işaretleri)

## Görevler

1. `CheckoutService(ProductCatalog, PaymentPort, OrderRepository, EventPublisher, OrderIdGenerator)` `null`
   işbirlikçileri `NullPointerException` ile reddeder; `checkout` da `null` müşteri ya da sepeti aynı şekilde reddeder.
2. Şu sırayla doğrulayın — ilk sorun karar verir: boş sepet → `"empty cart"`; ardından sepet sırasıyla kalem kalem,
   miktar ≤ 0 → `"invalid quantity: <sku>"` ve bilinmeyen SKU → `"unknown product: <sku>"`.
3. Aynı SKU'ları ilk göründükleri konumda tek satırda birleştirin (birim fiyat katalogdan), sonra stoğu kontrol edin:
   stok < birleşik miktar → `"insufficient stock: <sku>"`.
4. Toplamı (Σ miktar × birim fiyat) **tam bir kez** tahsil edin. Reddedilirse → `"payment declined"`.
5. Yalnızca onaylanan sipariş için: sıradaki kimliği alın, siparişi kaydedin, ardından tam bir `OrderPlaced`
   yayımlayın — kaydetme kesinlikle yayımlamadan önce. `save` istisna fırlatırsa hiçbir şey yayımlanmaz ve istisna
   yukarı iletilir.
6. `Confirmed(order)` döndürün. Reddedilen bir ödeme hiçbir şey tahsil etmez (reddedilen deneme hariç), kaydetmez,
   yayımlamaz ve kimlik tüketmez.
7. `InMemoryOrderRepository`: aynı kimlikle `save` siparişi değiştirir ama konumunu korur; `findByCustomer` müşterinin
   siparişlerini ilk kayıt sırasıyla döndürür; dönen listeler değiştirilemez anlık görüntülerdir; `count`.
8. `CheckoutService` hiçbir `adapter` paketindeki sınıfa bağımlı olmamalıdır.

## Kabul kriterleri

- [ ] `confirmsValidCartAndReturnsTheOrder`
- [ ] `totalIsSumOfLinePrices`
- [ ] `chargesTotalExactlyOnce`
- [ ] `savesBeforePublishing`
- [ ] `publishesExactlyOneOrderPlaced`
- [ ] `rejectsEmptyCart`
- [ ] `rejectsNonPositiveQuantity`
- [ ] `rejectsUnknownProduct`
- [ ] `rejectsInsufficientStock`
- [ ] `firstInvalidItemDecidesTheReason`
- [ ] `mergesDuplicateSkus`
- [ ] `validationFailureNeverCharges`
- [ ] `declinedPaymentSavesAndPublishesNothing`
- [ ] `idsAreConsumedOnlyByConfirmedOrders`
- [ ] `saveFailurePublishesNothingAndPropagates`
- [ ] `repositoryFindsSavedOrderById`
- [ ] `repositorySaveWithSameIdReplacesInPlace`
- [ ] `repositoryFindsByCustomerInSaveOrder`
- [ ] `repositoryListsAreImmutable`
- [ ] `serviceDoesNotDependOnAdapters`
- [ ] `rejectsNullArguments`

Sözleşme, dersteki `testdoubles.checkout` örneğindeki gibi elle yazılmış test ikizleri kullanır: bir stub katalog, bir
mock ödeme portu (tutar ve çağrı sayısı), ortak bir çağrı günlüğüne yazan bir spy depo ve bir spy yayıncı, ve sıralı
kimlik üreteci.

## Testleri çalıştırın

```bash
./mvnw -pl modules/m11-architecture-enterprise test -Pexercises -Dtest='Ex01*'
```

Tüm testler yeşil = bitti. `solutions/ex01/` ile ancak kendiniz denedikten **sonra** karşılaştırın.

## İpuçları

<details><summary>İpucu 1 — sırayı koruyarak birleştirmek</summary>

`merge(sku, quantity, Integer::sum)` ile kullanılan bir `LinkedHashMap<Sku, Integer>` her SKU'nun ilk konumunu korur
ve miktarları toplar. Miktar ve katalog kontrolünü aynı döngüde, birleştirme önemli hale gelmeden yapın.

</details>

<details><summary>İpucu 2 — "yerinde değiştirme"</summary>

Var olan bir anahtar için `LinkedHashMap.put` değeri değiştirir ve anahtarın ilk konumunu korur — tam olarak depo
kuralı. `stream().filter(...).toList()` değiştirilemez bir liste döndürür.

</details>

<details><summary>İpucu 3 — mimari test geçiyor, diğerleri kalıyor mu?</summary>

Başlangıç kodu için bu beklenen durumdur: başlangıç kodu zaten yalnızca portlara bağlıdır. Öyle kalsın —
`CheckoutService` `adapter.InMemoryOrderRepository`'yi içe aktardığı anda (örneğin onu kendisi oluşturmak için) kural
kalır. Hangi adaptörün kullanılacağına servis değil, composition root (bileşim kökü) karar verir.

</details>

## Ek hedefler (isteğe bağlı, notlandırılmaz)

- Bir `FileOrderRepository` adaptörü ekleyin ve aynı depo testlerini onun için de çalıştırın (iki uygulamanın
  paylaştığı sözleşme için örneklerdeki `repository.catalog` paketine bakın).
- Olayı doğrudan yayımlamak yerine bir outbox'a (giden kutusu) yazan bir `EventPublisher` adaptörü ekleyin (ders:
  transactional outbox).
