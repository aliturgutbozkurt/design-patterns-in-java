# Ödev 01 — Veri Kaynağı Dekoratörleri (Sıkıştırma + Base64)

> Modül: m04-structural-wrappers · Zorluk: ★★☆ · Tahmini süre: 2 saat

## Amaç

Bir uygulama belgeleri bir `DataSource` içinde saklar — burada bellekteki bir "dosya". Bazı kurulumlar verinin
**sıkıştırılmasını**, bazıları **Base64 ile kodlanmasını** (çünkü depo yalnızca metin kabul ediyor), bazıları ikisini
birden ister. Her kombinasyon için bir sınıf yazmak yerine üst üste takılabilen iki **decorator (dekoratör)** yazın:
her biri veriyi içeri girerken dönüştürür, dışarı çıkarken dönüşümü geri alır. Üst üste takma sırasının depoya ne
yazıldığını değiştirdiğini — ama istemcinin geri okuduğunu asla değiştirmediğini — göreceksiniz.

## Size verilenler

- `exercises/ex01/DataSource.java` — `write(byte[])`, `read()` — **değiştirmeyin**
- `exercises/ex01/InMemoryDataSource.java` — "dosya deposu": son yazılanın savunmacı bir kopyasını tutar; hiç
  yazılmadan çağrılan `read()` boş dizi döndürür — **değiştirmeyin**
- `exercises/ex01/DataSourceDecorator.java`, `CompressionDecorator.java`, `Base64Decorator.java` — kodunuzu buraya
  yazın (`TODO(ex01)` işaretleri)

## Görevler

1. `DataSourceDecorator` (soyut): sarmaladığı `DataSource`'u tutar (null → `NullPointerException`) ve işi ona devreder.
   **Kendine ait verisi yoktur**; bu yüzden her dekoratör her `DataSource`'u sarmalayabilir — aynı türden başka bir
   dekoratörü de.
2. `CompressionDecorator`: `write` GZIP ile sıkıştırır (`java.util.zip`) ve sonucu sarmalanan kaynağa verir; `read`
   sarmalanan kaynaktan okur ve açar.
3. `Base64Decorator`: `write` standart Base64 metnini US-ASCII baytları olarak saklar; `read` onu çözer.
4. İkisi için de: boş veri iki yönde de boş kalır (sarmalanan kaynaktan okunan boş dizi çözülmeden boş döndürülür);
   `read` sırasında bozuk veri, özgün istisnayı **cause** olarak taşıyan bir `IllegalStateException` fırlatır;
   `write(null)` `NullPointerException` fırlatır.

## Kabul kriterleri

- [ ] `roundTripsThroughCompression`
- [ ] `roundTripsThroughBase64`
- [ ] `roundTripsThroughBothInEitherOrder`
- [ ] `compressionShrinksRepetitiveData`
- [ ] `base64StoresOnlyBase64Characters`
- [ ] `outermostDecoratorTransformsFirst` — `compression(base64(store))` depoda Base64 metni bırakır;
  `base64(compression(store))` GZIP baytları bırakır (sihirli sayı `0x1f 0x8b` ile başlarlar)
- [ ] `sameDecoratorCanBeStackedTwice`
- [ ] `emptyDataRoundTrips`
- [ ] `readingANeverWrittenSourceReturnsEmpty`
- [ ] `corruptDataIsReportedAsIllegalState`
- [ ] `decoratorsWorkWithAnyDataSource`
- [ ] `rejectsNullArguments`

## Testleri çalıştırın

```bash
./mvnw -pl modules/m04-structural-wrappers test -Pexercises -Dtest='Ex01*'
```

Tüm testler yeşil = ödev bitti. `solutions/ex01/` ile ancak kendiniz denedikten **sonra** karşılaştırın.

## İpuçları

<details><summary>İpucu 1 — ortak kısmı taban sınıfa koyun</summary>

İki dekoratör de farklı bir dönüşümün etrafında aynı işi yapar: "kodla, sonra yaz" ve "oku, sonra çöz".
`DataSourceDecorator` `write`/`read`'i bir kez yazsın ve alt sınıfların sağladığı iki soyut metodu, `encode(byte[])`
ve `decode(byte[])`'i çağırsın. (Bu, bir Decorator içindeki küçük bir Template Method'dur.) Boş dizi kuralı da böylece
tek bir yerde yaşar.

</details>

<details><summary>İpucu 2 — JDK'da GZIP ve Base64</summary>

try-with-resources içinde `new GZIPOutputStream(byteArrayOutputStream)` ile sıkıştırın — GZIP'in son eki ancak akış
**kapatıldığında** yazılır. `new GZIPInputStream(new ByteArrayInputStream(bytes)).readAllBytes()` ile açın. Anlamsız
veri `ZipException`, yarım kalmış bir akış `EOFException` fırlatır — ikisi de `IOException`'dır.
`Base64.getEncoder().encode(bytes)` zaten ASCII baytları döndürür; `Base64.getDecoder().decode(bytes)` geçersiz
girdide `IllegalArgumentException` fırlatır.

</details>

<details><summary>İpucu 3 — boş neden boş kalmalı?</summary>

Boş bir dizinin GZIP'i 20 bayttır ve *boş* bir akışı `GZIPInputStream` ile okumak `EOFException` fırlatır. Bu yüzden
hiç yazılmamış (boş) bir depo "açılmamalı", hiçbir şey yazmamak da depoya hiçbir şey koymamalıdır.

</details>

## Ek hedefler (isteğe bağlı, notlandırılmaz)

- Bir `EncryptionDecorator` (`javax.crypto`'dan AES) ekleyin ve yığındaki yerine karar verin: sıkıştırmadan önce mi,
  sonra mı? (İpucu: şifrelenmiş veri sıkışmaz.)
- Aynı iki dönüşümü `UnaryOperator<byte[]>` olarak yazıp `andThen` ile birleştirin. `DataSource`'u uygulayan
  dekoratörlere göre neyi kaybedersiniz?
