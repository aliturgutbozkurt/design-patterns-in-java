# Ödev 02 — Sealed Hiyerarşi ile Ödeme Ücretleri

> Modül: m00-setup-and-modern-java · Zorluk: ★★☆ · Tahmini süre: 1,5 saat

## Amaç

Bir mağaza üç tür ödeme kabul ediyor; bunlar üç record'lu **sealed** (mühürlü) bir arayüzle modellenmiş. Record
desenleri ve koşullar (guard) kullanan **tek bir `switch`** ile her ödemenin işlem ücretini hesaplayın — `default`
dalı olmadan; böylece ileride yeni bir ödeme türü eklenirse derleyici sizi uyarır.

## Size verilenler

- `exercises/ex02/Payment.java` — `sealed interface Payment permits CardPayment, BankTransfer, WalletPayment` — **değiştirmeyin**
- `exercises/ex02/CardPayment.java`, `BankTransfer.java`, `WalletPayment.java` — record'lar — **değiştirmeyin**
- `exercises/ex02/FeeCalculator.java` — uygulanacak arayüz — **değiştirmeyin**
- `exercises/ex02/PaymentFees.java` — kodunuzu buraya yazın (`TODO(ex02)` işareti)

## Görevler

`PaymentFees.feeFor(Payment)` metodunu şu kurallarla yazın (tutarlar avro cinsinden):

| Ödeme | Ücret |
|---|---|
| Kart, yurt içi | tutarın %2,9'u + 0,30 |
| Kart, yurt dışı | tutarın %3,9'u + 0,30 (%1 ek ücret) |
| Banka havalesi | sabit 0,50; tutar 1000 veya üzerindeyse **ücretsiz** |
| Cüzdan | tutarın %1,5'i, **en fazla** 5,00 |

Ölçeği (scale) 2 olan, `RoundingMode.HALF_EVEN` ("bankacı yuvarlaması") ile yuvarlanmış bir `BigDecimal` döndürün.
`null` ödeme `NullPointerException` fırlatır.

## Kabul kriterleri

- [ ] `domesticCardFee` — 100.00 → 3.20
- [ ] `internationalCardFee` — 100.00 → 4.20
- [ ] `bankTransferFlatFee` — 999.99 → 0.50
- [ ] `bankTransferFreeFromThousand` — 1000.00 → 0.00
- [ ] `walletFeeBelowCap` — 200.00 → 3.00
- [ ] `walletFeeCapped` — 1000.00 → 5.00
- [ ] `feesUseBankersRounding` — kart 5.00 → 0.445 → 0.44
- [ ] `rejectsNull`

## Testleri çalıştırın

```bash
./mvnw -pl modules/m00-setup-and-modern-java test -Pexercises -Dtest='Ex02*'
```

Tüm testler yeşil = ödev bitti. `solutions/ex02/` ile ancak kendiniz denedikten **sonra** karşılaştırın.

## İpuçları

<details><summary>İpucu 1 — switch'in yapısı</summary>

`case CardPayment(BigDecimal amount, boolean international) when international -> …` durumu, genel `CardPayment`
durumundan **önce** gelir. İhtiyacınız olmayan bileşenler için `_` kullanın.

</details>

<details><summary>İpucu 2 — üst sınır</summary>

`BigDecimal.min(other)` iki değerden küçük olanı döndürür.

</details>

## İleri hedefler (isteğe bağlı, notlanmaz)

- Karalama amaçlı bir kopyada `Payment`'a dördüncü bir record (`CryptoPayment`) ekleyin ve hangi kodun derlenmeyi
  bıraktığını izleyin. Bu neden bir avantajdır?
- Hesaplayıcıyı her record üzerinde soyut bir `fee()` metodu olarak yeniden yazın. İki tasarımı karşılaştırın: hangisi
  yeni bir *ödeme türü* eklemeyi, hangisi yeni bir *işlem* (ör. `refundFee`) eklemeyi kolaylaştırır?
