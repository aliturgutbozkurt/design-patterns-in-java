# Ödev 01 — Visitor'dan Veri Odaklı Bordroya

> Modül: m09-functional-data-oriented · Zorluk: ★★☆ · Tahmini süre: 2–3 saat

## Amaç

Eski (legacy) bir bordro sistemi çalışanları bir sınıf hiyerarşisiyle modelliyor ve her şeyi **Visitor (Ziyaretçi)**
ile hesaplıyor. Bunu **veri odaklı programlama** ile değiştirin: eski nesneleri sınırda (boundary) bir kez sealed
(mühürlü) record'lara dönüştürün, sonra her işlemi düz bir fonksiyon olarak yazın — record desenleri kullanan,
`default` içermeyen eksiksiz (exhaustive) bir `switch`. Buna, ziyaretçi olarak yazması zahmetli olan değişmez bir
güncelleme (`withRaise`) ve bir toplama işlemi (`summarize`) de dahildir. Verilen eski ziyaretçiler test kâhini
(oracle) olarak kalır: sizin sayılarınız onlarınkiyle aynı olmalıdır.

## Size verilenler

- `exercises/ex01/legacy/LegacyEmployee.java` ve `LegacySalaried`, `LegacyHourly`, `LegacyContractor`,
  `LegacyIntern` — getter'ları ve `accept` metodu olan eski sınıflar — **değiştirmeyin**
- `exercises/ex01/legacy/EmployeeVisitor.java`, `MonthlyPayVisitor.java`, `BenefitsVisitor.java` — çalışan eski
  kurallar (kâhin) — **değiştirmeyin**
- `exercises/ex01/Employee.java` — `sealed interface Employee permits Salaried, Hourly, Contractor, Intern` ve
  record'ları: `Salaried(id, name, annualSalaryCents)`, `Hourly(id, name, hourlyRateCents, hoursThisMonth)`,
  `Contractor(id, name, invoiceCents, vatRegistered)`, `Intern(id, name, stipendCents, universityFunded)`; kompakt
  kurucular boş id/isim ve negatif tutarları reddeder — **değiştirmeyin**
- `exercises/ex01/Kind.java` — `SALARIED`, `HOURLY`, `CONTRACTOR`, `INTERN` — **değiştirmeyin**
- `exercises/ex01/PayrollSummary.java` — record `PayrollSummary(long totalCents, Map<Kind, Long> totalByKind,
  List<String> highestPaidIds)` — **değiştirmeyin**
- `exercises/ex01/Payroll.java` — altı işlem — **değiştirmeyin**
- `exercises/ex01/DataOrientedPayroll.java` — kodunuz buraya (`TODO(ex01)` işaretleri)

## Görevler

1. `fromLegacy`: her eski alt sınıfı karşılık gelen record'a eşleyin. Eski nesne üzerinde tip desenleri ve
   getter'ları kullanın; `EmployeeVisitor` arayüzünü **uygulamayın** ve `accept` metodunu **çağırmayın** (nedeni için
   İpucu 1).
2. `monthlyPayCents` (tüm tutarlar kuruş/cent cinsinden, tamsayı aritmetiği):
   maaşlı `annual / 12`; saatlik `rate × hours`, ancak 160 saatin üstündeki her saat `rate * 3 / 2` ile ödenir;
   sözleşmeli `invoice`, KDV'ye kayıtlıysa `invoice * 120 / 100`; stajyer `stipend`, üniversite ödüyorsa `0`.
3. `benefits`: maaşlı `"health, pension"`; saatlik, saat ≥ 80 ise `"health"`, değilse `"none"`; sözleşmeli `"none"`;
   stajyer `"mentoring"`.
4. `kindOf`: her record'un `Kind` değeri.
5. `withRaise(employee, percent)`: `percent` 0..100 olmalıdır (aksi halde `IllegalArgumentException`). Maaşı, saat
   ücreti, faturası ya da bursu `(100 + percent) / 100` ile çarpılmış **yeni** bir record döndürün (tamsayı
   aritmetiği: `amount * (100 + percent) / 100`); diğer bileşenler aynı kalır, orijinal değişmez.
6. `summarize(employees)`: `totalCents` tüm aylık ödemelerin toplamıdır; `totalByKind` **her** `Kind` değerini içerir
   (olmayan türler için 0) ve değiştirilemez; `highestPaidIds` en yüksek aylık ödemeyi alan herkesin id'sini girdi
   sırasıyla, değiştirilemez bir liste olarak verir. Boş liste toplam 0, tüm türler 0 ve id'siz bir özet verir.
7. Her metot `null` argümanda `NullPointerException` fırlatır.

`Employee` üzerindeki dört işlemi record desenli (`case Hourly(_, _, var rate, var hours) -> …`) ve **`default`
içermeyen** `switch` ifadeleriyle yazın; böylece beşinci bir çalışan türü, her işlem onu ele alana kadar derlenmez.

## Kabul kriterleri

- [ ] `convertsEveryLegacyKind`
- [ ] `monthlyPayMatchesLegacyVisitorForAllSamples`
- [ ] `benefitsMatchLegacyVisitorForAllSamples`
- [ ] `hourlyOvertimeIsPaidAtTimeAndAHalf`
- [ ] `vatAddedOnlyForRegisteredContractors`
- [ ] `universityFundedInternCostsNothing`
- [ ] `kindOfClassifiesEveryVariant`
- [ ] `withRaiseReturnsNewRecordAndLeavesOriginalUnchanged`
- [ ] `withRaiseRejectsPercentOutOfRange`
- [ ] `summaryTotalsByKindIncludeAbsentKinds`
- [ ] `summaryListsAllHighestPaidInInputOrder`
- [ ] `summaryOfEmptyListIsZero`
- [ ] `summaryMapIsUnmodifiable`
- [ ] `rejectsNullArguments`

## Testleri çalıştırın

```bash
./mvnw -pl modules/m09-functional-data-oriented test -Pexercises -Dtest='Ex01*'
```

Tüm testler yeşil = bitti. `solutions/ex01/` ile ancak kendiniz denedikten **sonra** karşılaştırın.

## İpuçları

<details><summary>İpucu 1 — neden ziyaretçi yok?</summary>

Bir ziyaretçi *işlem* kümesini tek bir arayüzün metotları olarak sabitler; her yeni işlem, durumu alanlarda taşıyan
yeni bir sınıftır. `withRaise` bir record'u yeniden kurmalı, `summarize` bir liste üzerinde katlama (fold) yapmalıdır;
ikisi de veri üzerinde basit fonksiyonlardır ama ziyaretçi olarak hantaldır. Sealed record'larda derleyici tüm
durumları zaten bilir; eksiksiz bir `switch`, `accept` tesisatı olmadan çift yönlendirmenin (double dispatch)
sağladığı güvenliği verir.

</details>

<details><summary>İpucu 2 — sınırdaki switch'in default'a ihtiyacı var</summary>

`LegacyEmployee` sıradan bir soyut sınıftır, sealed değildir; bu yüzden onun üzerindeki bir `switch` eksiksiz olamaz ve
`default -> throw new IllegalArgumentException(...)` gerektirir. Tam da bu nedenle sınırda bir kez dönüştürürsünüz:
çekirdekteki her `switch` sealed `Employee` üzerindedir ve `default` içermez.

</details>

<details><summary>İpucu 3 — her tür için bir toplam</summary>

Her türü `0L` olan bir `EnumMap<Kind, Long>` ile başlayın, her çalışanın ödemesini `merge` ile ekleyin ve sonucun
değiştirilemez olması için `Map.copyOf(...)` döndürün. Önce en büyük değeri bulun, sonra girdi sırasını korumak için
listeyi bir kez daha süzün.

</details>

## Ek hedefler (isteğe bağlı, notlandırılmaz)

- Modelin bir kopyasına beşinci bir tür, `Commissioned(id, name, baseCents, salesCents, percent)` ekleyin ve
  derleyicinin değişmesi gereken her `switch`'i göstermesini izleyin. Aynı değişiklik Visitor tasarımında neye mal
  olurdu?
- `summarize`'ı `Collectors.teeing` ile tek bir akış (stream) hattı olarak yazın.
