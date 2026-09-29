# Ödev 01 — Seyahat Rezervasyonu Builder'ı

> Modül: m03-creational-construction · Zorluk: ★★☆ · Tahmini süre: 1,5 saat

## Amaç

Bir uçuş rezervasyonunun dört zorunlu ve birkaç isteğe bağlı parçası vardır. Mantıklı varsayılanları dolduran,
`build()` içinde her kuralı denetleyen ve ilk sorunda duran bir builder'ın aksine ihlal edilen **tüm** kuralları tek
bir istisnada bildiren bir **Builder (İnşacı)** yazın; böylece kullanıcı bir formu tek seferde düzeltebilir. Ortaya
çıkan `Booking` değişmez ve builder'dan bağımsız olmalıdır.

## Size verilenler

- `exercises/ex01/Booking.java` — record `Booking(traveller, from, to, departure, returnDate, passengers, cabin,
  extras)`; tek yön yolculukta `returnDate` `null`'dır, onu `returnTrip()` ile okuyun — **değiştirmeyin**
- `exercises/ex01/CabinClass.java` — `ECONOMY`, `BUSINESS`, `FIRST` — **değiştirmeyin**
- `exercises/ex01/BookingBuilder.java` — builder arayüzü — **değiştirmeyin**
- `exercises/ex01/DefaultBookingBuilder.java` — kodunuzu buraya yazın (`TODO(ex01)` işaretleri)

## Görevler

1. Setter'lar değerleri saklar ve `this` döndürür; `null` bir argüman hemen `NullPointerException` fırlatır.
2. Varsayılanlar: tek yön (dönüş tarihi yok), 1 yolcu, `ECONOMY`, ek hizmet yok.
3. `build()` her kuralı denetler ve sorunları toplar:
   - yolcu adı (traveller), kalkış (from), varış (to) ve gidiş tarihi (departure) zorunludur;
   - `from` ve `to` farklı olmalıdır (büyük/küçük harf fark etmez);
   - dönüş tarihi varsa gidiş tarihinden **sonra** olmalıdır;
   - yolcu sayısı 1–9 arasında olmalıdır.
4. En az bir sorun varsa, mesajı hepsini listeleyen tek bir `IllegalStateException` fırlatın.
5. Rezervasyonun `extras` alanı **değişmez bir kopya** olmalıdır: builder'a sonradan ek hizmet eklemek, önceden
   oluşturulmuş bir rezervasyonu değiştirmemelidir. Aynı builder birkaç rezervasyon oluşturabilir.

## Kabul kriterleri

- [ ] `buildsWithDefaults`
- [ ] `buildsAReturnTrip`
- [ ] `missingRequiredFieldsAreAllReported` — tek bir mesaj traveller, from, to, departure *ve* passengers'ı anar
- [ ] `rejectsSameOriginAndDestination`
- [ ] `rejectsReturnBeforeDeparture` — aynı gün dönmek de "sonra değil" sayılır
- [ ] `rejectsPassengersOutsideOneToNine`
- [ ] `extrasAreImmutableAndIndependentOfTheBuilder`
- [ ] `builderCanBuildSeveralIndependentBookings`
- [ ] `rejectsNullArguments`

## Testleri çalıştırın

```bash
./mvnw -pl modules/m03-creational-construction test -Pexercises -Dtest='Ex01*'
```

Tüm testler yeşil = ödev bitti. `solutions/ex01/` ile ancak kendiniz denedikten **sonra** karşılaştırın.

## İpuçları

<details><summary>İpucu 1 — sorunları toplamak</summary>

Bir `List<String> problems` tutun, ihlal edilen her kural için bir cümle ekleyin ve sonunda liste boş değilse
`new IllegalStateException("invalid booking: " + String.join("; ", problems))` fırlatın.

</details>

<details><summary>İpucu 2 — savunmacı kopya</summary>

`Set.copyOf(extras)`, builder'ın kümesinin değişmez bir anlık görüntüsünü oluşturur.

</details>

## Ek hedefler (isteğe bağlı, notlandırılmaz)

- Builder'ı, `build()` yalnızca dört zorunlu parçadan sonra kullanılabilecek şekilde bir **step builder**'a
  dönüştürün. Ne kazanırsınız, ne zorlaşır (ipucu: eksik parçaların *hepsini* bildirmek)?
- `returnDate` neden bir `Optional` bileşeni değil de, `Optional` döndüren bir metodu olan null olabilir bir
  bileşendir?
