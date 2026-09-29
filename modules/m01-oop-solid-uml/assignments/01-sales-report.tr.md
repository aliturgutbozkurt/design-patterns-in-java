# Ödev 01 — Satış Raporu: SRP + OCP ile Yeniden Düzenleme

> Modül: m01-oop-solid-uml · Zorluk: ★★☆ · Tahmini süre: 2 saat

## Amaç

`LegacyReport` her işi tek başına yapan bir sınıf (god class): tek bir metotta satışları bölgelere göre topluyor,
bölgeleri sıralıyor ve sonucu biçimlendiriyor; biçimi de bir `String` bayrağıyla seçiyor. Onu, her birinin
**değişmek için tek bir nedeni** olan sınıflara ayırın (Tek Sorumluluk İlkesi, SRP) ve yeni bir rapor biçiminin
servisi **değiştirmeden** eklenebilmesini sağlayın (Açık/Kapalı İlkesi, OCP). Eski çıktı sizin güvenlik ağınızdır:
yeni sürümünüz birebir aynı metni üretmelidir.

## Size verilenler

- `exercises/ex01/Sale.java`, `SalesSummary.java` — record'lar — **değiştirmeyin**
- `exercises/ex01/SalesSummarizer.java`, `ReportFormat.java`, `ReportGenerator.java` — arayüzler — **değiştirmeyin**
- `exercises/ex01/LegacyReport.java` — god class; çıktısı referanstır — **değiştirmeyin**
- `exercises/ex01/RegionSummarizer.java`, `TextReportFormat.java`, `CsvReportFormat.java`, `ReportService.java` —
  kodunuzu buraya yazın (`TODO(ex01)` işaretleri)

## Görevler

1. `LegacyReport`'u okuyun ve sorumluluklarını listeleyin (toplama, yerleşim, biçim seçimi).
2. `RegionSummarizer`: tutarları bölge bazında ve toplamda hesaplayın. Bölgeler alfabetik sıralıdır; tüm tutarların
   ölçeği (scale) 2'dir; satış yoksa harita boştur ve genel toplam `0.00` olur.
3. `TextReportFormat` ve `CsvReportFormat`: yalnızca yerleşim — her biri `LegacyReport`'un `"text"` ve `"csv"`
   çıktısıyla karakteri karakterine aynı olmalıdır.
4. `ReportService(SalesSummarizer, ReportFormat)`: önce özetleyin, sonra biçimlendirin. Servisin içinde biçime göre
   `if`/`switch` olmasın — biçim, size hangi nesne verildiyse odur.
5. `null` kurucu argümanlarını ve `null` satış listesini `NullPointerException` ile reddedin.

## Kabul kriterleri

- [ ] `summarizesTotalsByRegionInAlphabeticalOrder`
- [ ] `grandTotalIsSumOfRegions` — örnek veri → 5630.75
- [ ] `emptySalesGiveZeroTotal`
- [ ] `textFormatMatchesLegacyOutput`
- [ ] `csvFormatMatchesLegacyOutput`
- [ ] `newFormatPlugsInWithoutChangingService` — test bir lambda `ReportFormat` verir
- [ ] `amountsUseScaleTwo`
- [ ] `rejectsNullArguments`

## Testleri çalıştırın

```bash
./mvnw -pl modules/m01-oop-solid-uml test -Pexercises -Dtest='Ex01*'
```

Tüm testler yeşil = ödev bitti. `solutions/ex01/` ile ancak kendiniz denedikten **sonra** karşılaştırın.

## İpuçları

<details><summary>İpucu 1 — anahtara göre toplama</summary>

`Map.merge(region, amount, BigDecimal::add)` var olan toplama ekler ya da yeni bir toplam başlatır. `TreeMap`
anahtarlarını sıralı tutar.

</details>

<details><summary>İpucu 2 — metin yerleşimi</summary>

Biçim dizgesini `LegacyReport`'tan kopyalayın: `"%-12s%10s"` etiketi 12 karaktere (sola yaslı), tutarı 10 karaktere
(sağa yaslı) tamamlar.

</details>

## Ek hedefler (isteğe bağlı, notlandırılmaz)

- Markdown tablosu üreten bir `MarkdownReportFormat` ekleyin. Hangi sınıflara dokunmanız gerekti?
- Önceki ve sonraki tasarımı Mermaid sınıf diyagramı olarak çizin.
