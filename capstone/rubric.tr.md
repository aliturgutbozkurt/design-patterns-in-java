# PatternShop — Bitirme Projesi Değerlendirme Rubriği

> 100 puan · Proje tanımı: [spec.tr.md](spec.tr.md) · English: [rubric.en.md](rubric.en.md)

## Değerlendirme nasıl yapılır

- Rubrikte **üç grupta 10 ölçüt** vardır. Gruplar, bitirme projesinin ders notundaki %40'lık payının izlencedeki
  dağılımına karşılık gelir: spesifikasyon ve tasarım 25 puan (%10), uygulama ve testler 50 puan (%20), rapor ve
  savunma 25 puan (%10).
- Her ölçütün **dört düzeyi** vardır: Mükemmel, İyi, Yeterli, Yetersiz. Her düzey, değerlendiricinin teslimde
  denetleyebileceği şeylerle tanımlanır — bir sayı, bir komut, bir dosya, bir bölüm. Değerlendirici koşullarının
  **tamamı** sağlanan en yüksek düzeyi seçer ve tam olarak o düzeyin puanını verir.
- Yetersiz düzeyi puanını yalnızca teslim edilecek şey mevcutsa verir; eksik bir teslim 0 alır.
- Değerlendirilen durum, öğrencinin çatalındaki (fork) `capstone-final` etiketli commit'tir (14. hafta, sunum
  saatinden önce). `SPEC.md` ayrıca 10. hafta kilometre taşında teslim edilen sürümüyle de değerlendirilir (C1).
- Değerlendiriciler JDK 27 ile, depo kökünden şunları çalıştırır:
  `./mvnw -q -pl capstone/starter verify`, `./mvnw -q -pl capstone/starter test -Pexercises` ve
  `./mvnw -q -pl capstone/starter verify -Pcoverage`.

## Otomatik başarısızlık koşulları

Etiketli commit'te aşağıdakilerden **herhangi biri** geçerliyse uygulama bölümü başarısız olur: **C3–C8 ölçütleri 0
alır** (50 puan). C1, C2, C9 ve C10 yine değerlendirilir.

| Koşul | Tanım | Nasıl denetlenir |
|---|---|---|
| G1 | Derleme JDK 27'de başarısız: derleme hatası, bir `-Xlint:all -Werror` uyarısı ya da öğrencinin kendi testlerinden birinin kırmızı olması | `./mvnw -q -pl capstone/starter verify` |
| G2 | Verilen 83 kabul testinden herhangi biri kırmızı (başarısız, hatalı ya da çalışmamış) | `./mvnw -q -pl capstone/starter test -Pexercises` |
| G3 | Verilen 7 mimari kuraldan herhangi biri kırmızı | `verify` çalıştırmasındaki `ShopArchitectureTest` |
| G4 | Verilen bir test, test sahtesi (fixture), test kaynağı, mimari kural ya da VERİLEN API tipi değiştirilmiş, silinmiş, devre dışı bırakılmış (`@Disabled`, kaldırılmış etiket, Surefire dışlaması) veya zayıflatılmış | `capstone/starter/src/test/**/acceptance/**`, `*ExerciseTest` ve `ShopArchitectureTest` bağlamaları, `src/test/resources/acceptance/**` ve `…/capstone/api/**` için yayımlanmış başlangıç koduna karşı `git diff` |

İzin verilen tek derleme değişikliği E10 genişletmesinin gerektirdiğidir (m10'daki gibi yalıtılmış
`--enable-preview`); verilen tüm testler önizleme olmadan da geçmelidir. Akademik dürüstlük ihlalleri ayrıca ele
alınır (proje tanımı §12).

## Ölçütler ve ağırlıklar

| Grup | Kimlik | Ölçüt | Puan |
|---|---|---|---|
| Spesifikasyon ve tasarım | C1 | Öğrencinin `SPEC.md` dosyası | 15 |
| | C2 | Kalıp planı ve gerekçe tablosu | 10 |
| Uygulama ve testler | C3 | Kapının ötesinde doğruluk (genişletme özellikleri) | 10 |
| | C4 | Kalıp kullanımı | 14 |
| | C5 | Modern Java ve eşzamanlılık | 8 |
| | C6 | Mimari | 6 |
| | C7 | Test kalitesi | 7 |
| | C8 | Kod kalitesi ve süreç | 5 |
| Rapor ve savunma | C9 | Rapor | 12 |
| | C10 | Sunum ve savunma | 13 |
| | | **Toplam** | **100** |

## Düzey tanımları

### C1 — Öğrencinin SPEC.md dosyası (15)

Denetim listesi (a–g için 10. hafta sürümü, h için son sürüm):
(a) şablonun on bölümünün hepsi var;
(b) F1–F11 zorunlu özelliklerinin her biri kendi kabul takımıyla eşleştirilmiş;
(c) her genişletme özelliğinin en az üç Given / When / Then kabul kriteri ve adlandırılmış test sınıfları var;
(d) bir Mermaid alan sınıf diyagramı ve bir altıgen diyagramı;
(e) en az on satırlık bir kalıp planı;
(f) proje tanımının sessiz kaldığı yerlerde en az üç sınır ya da varsayım;
(g) 10.–13. haftalar için hafta hafta bir plan;
(h) son `SPEC.md` kodla örtüşür — planlanan her kalıp ve genişletme vardır ya da değişiklik günlüğü neden değiştiğini
söyler.

| Düzey | Puan | Tanım |
|---|---|---|
| Mükemmel | 15 | 10. hafta son tarihine kadar teslim; a–h'nin hepsi |
| İyi | 11 | 10. hafta son tarihine kadar teslim; a–h'den altı ya da yedisi |
| Yeterli | 7 | a–h'den dört ya da beşi, ya da en çok bir hafta geç teslim |
| Yetersiz | 3 | a–h'den üç ya da daha azı, ya da bir haftadan fazla geç teslim |

### C2 — Kalıp planı ve gerekçe tablosu (10)

Bir satır şunları adlandırıyorsa **eksiksizdir**: (1) PatternShop'a özgü bir kuvvet (force) ya da sorun, (2) kodda
var olan katılımcı tipler, (3) değerlendirilen bir alternatif ve neden seçilmediği, (4) kalıbın çalıştığını gösteren
bir test. Tablo şablonu: bölüm 5.

| Düzey | Puan | Tanım |
|---|---|---|
| Mükemmel | 10 | En az 10 eksiksiz satır ve gerekçesiyle bilerek *kullanılmayan* en az bir kalıp |
| İyi | 7 | En az 10 satır, bunların en az 8'i eksiksiz |
| Yeterli | 5 | En az 5'i eksiksiz en az 10 satır, ya da hepsi eksiksiz 8–9 satır |
| Yetersiz | 2 | 8'den az eksiksiz satır |

### C3 — Kapının ötesinde doğruluk (10)

Kapı (G2) verilen tüm kabul testlerinin geçmesini zaten gerektirir. C3 **genişletme özelliklerini** değerlendirir
(iki; ikililerde üç). Bir genişletme, `SPEC.md` içinde onun için yazılmış her kabul kriterinin onu adlandıran (test
adında ya da `@DisplayName` içinde) en az bir yeşil testi varsa ve özelliğe CLI'dan ulaşılabiliyorsa
**tamamlanmıştır**.

| Düzey | Puan | Tanım |
|---|---|---|
| Mükemmel | 10 | Tüm genişletmeler tamamlanmış |
| İyi | 7 | Tüm genişletmeler gerçekleştirilmiş; kriterlerinin en az %75'inin yeşil testi var |
| Yeterli | 5 | Bir genişletme tamamlanmış, ya da tüm genişletmelerin kriterlerinin en az %50'si yeşil testli |
| Yetersiz | 2 | Hiçbir genişletme %50'ye ulaşmıyor |

### C4 — Kalıp kullanımı (14)

`@PatternRole` ile bildirilen ve sayılan (yaratımsal, yapısal, davranışsal, eşzamanlılık) her kalıp için
değerlendirici şunu denetler: işaretli tipler, modül dersinin o kalıp için verdiği rolleri oynar ve kalıp, yeşil bir
kabul ya da birim testinin kapsadığı bir yolda çalışır. İkisini de sağlayan kalıp **geçerlidir**. **Karışım** en az
2 yaratımsal, 2 yapısal, 3 davranışsal ve 1 eşzamanlılık kalıbıdır (Immutable Object değil).

| Düzey | Puan | Tanım |
|---|---|---|
| Mükemmel | 14 | En az 12 geçerli kalıp, karışım geçerli kalıplarla sağlanmış ve bildirilen hiçbir kalıp geçersiz değil |
| İyi | 10 | 10 ya da 11 geçerli kalıp, karışım geçerli kalıplarla sağlanmış |
| Yeterli | 7 | 8 ya da 9 geçerli kalıp, ya da karışım olmadan en az 10 geçerli kalıp |
| Yetersiz | 3 | 7 ya da daha az geçerli kalıp |

### C5 — Modern Java ve eşzamanlılık (8)

Denetim listesi:
(a) record desenli ve `default` içermeyen eksiksiz bir `switch` ile işlenen en az bir sealed record hiyerarşisi;
(b) öğrencinin kendi kodunda en az iki sealed hiyerarşi ya da eksiksiz switch daha;
(c) tüm değer tipleri record (değiştirilebilir değer sınıfı yok);
(d) durum gerektirmeyen tek metotlu stratejiler ya da komutlar lambda veya metot referansı;
(e) karşılama, paralel sipariş sayısı sınırlı sanal iş parçacıklarında çalışır ve `SPEC.md` §6 paylaşılan durumun
neden iş parçacığı güvenli olduğunu savunur;
(f) `Optional` yalnızca dönüş tipi; hiçbir public metot `null` döndürmez.

| Düzey | Puan | Tanım |
|---|---|---|
| Mükemmel | 8 | a–f'nin hepsi |
| İyi | 6 | a–f'den beşi |
| Yeterli | 4 | (a) ve (e) dahil a–f'den üç ya da dördü |
| Yetersiz | 2 | Diğer durumlar |

### C6 — Mimari (6)

Kapı (G3) verilen kuralların yeşil olmasını zaten gerektirir. Denetim listesi:
(a) adaptörler yalnızca bileşim kökünde (`config`) oluşturulur;
(b) çıkış portları `application` katmanının sahip olduğu, teknolojiye değil rolüne göre adlandırılmış arayüzlerdir;
(c) iş sonuçları sealed sonuçlar, altyapı hataları istisnalardır;
(d) öğrencinin kendi yazdığı, yeşil ve anlamlı en az bir ek ArchUnit kuralı (ör. portlar arayüzdür, adaptör
adlandırması);
(e) `SPEC.md` içindeki altıgen diyagramı gerçek paketlerle örtüşür.

| Düzey | Puan | Tanım |
|---|---|---|
| Mükemmel | 6 | a–e'nin hepsi |
| İyi | 4 | a–e'den dördü |
| Yeterli | 3 | a–e'den üçü |
| Yetersiz | 1 | a–e'den iki ya da daha azı |

### C7 — Test kalitesi (7)

Denetim listesi:
(a) her geçerli kalıbın (C4) davranışını gösteren en az bir kendi birim testi var;
(b) test ikizleri elle yazılmış; mocking kütüphanesi yok;
(c) test adları davranışı anlatır (`test1` değil, `appliesCouponAfterThresholdDiscount`);
(d) testler belirlenimci: eşzamanlama için `Thread.sleep` yok, zaman enjekte edilen saatten gelir;
(e) `domain` ve `application` satır kapsamı en az %80 (`-Pcoverage` JaCoCo raporu);
(f) her genişletme testi `SPEC.md` içinde kendi kabul kriterinin yanında listelenmiş.

| Düzey | Puan | Tanım |
|---|---|---|
| Mükemmel | 7 | a–f'nin hepsi |
| İyi | 5 | a–f'den beşi |
| Yeterli | 3 | a–f'den üç ya da dördü |
| Yetersiz | 1 | a–f'den iki ya da daha azı |

### C8 — Kod kalitesi ve süreç (5)

Denetim listesi:
(a) nedenini açıklayan bir yorumu olmayan `@SuppressWarnings` yok;
(b) `domain` ve `application` içindeki her public tipin tek satırlık amaç Javadoc'u var;
(c) hiçbir sınıfın 10'dan fazla public metodu yok ve hiçbir metot 30 satırdan uzun değil;
(d) 9.–13. haftaların her birinde, Conventional Commits mesajlı en az bir commit;
(e) kalmış `TODO(capstone)` işareti, yoruma alınmış kod ve kullanılmayan sınıf yok.

| Düzey | Puan | Tanım |
|---|---|---|
| Mükemmel | 5 | a–e'nin hepsi |
| İyi | 4 | a–e'den dördü |
| Yeterli | 2 | a–e'den üçü |
| Yetersiz | 1 | a–e'den iki ya da daha azı |

### C9 — Rapor (12)

`REPORT.md` için denetim listesi:
(a) altıgen diyagramıyla bir tasarım özeti;
(b) eksiksiz kalıp gerekçe tablosu (`SPEC.md` içindekinin güncellenmiş hali olabilir);
(c) klasik alternatifle karşılaştırılarak açıklanmış en az iki modern Java kararı;
(d) iş parçacığı güvenliği savunmasıyla birlikte eşzamanlılık tasarımı;
(e) bir değerlendirme: değiştireceğiniz en az iki şey ve bilerek kullanmadığınız bir kalıp ile nedeni;
(f) bir yapay zekâ kullanım beyanı (ne için, hangi bölümlerde);
(g) diğer dilde bir sayfalık özet (İngilizce ↔ Türkçe);
(h) 6–10 sayfa, terimler `docs/glossary.md`'deki gibi, kodla ilgili her iddia bir dosya yolunu gösterir.

| Düzey | Puan | Tanım |
|---|---|---|
| Mükemmel | 12 | a–h'nin hepsi |
| İyi | 9 | a–h'den altı ya da yedisi |
| Yeterli | 6 | a–h'den dört ya da beşi |
| Yetersiz | 2 | a–h'den üç ya da daha azı |

### C10 — Sunum ve savunma (13)

Denetim listesi:
(a) konuşma 9–11 dakika sürer;
(b) ödeme adımı, karşılama ve bir raporun canlı CLI gösterimi çalışır;
(c) üç kalıp kuvvetiyle, yapısıyla ve öğrencinin koduyla açıklanır;
(d) bir ödünleşim (trade-off), reddedilen alternatifle birlikte sunulur.
Savunma: değerlendirici öğrencinin kendi kodu hakkında dört soru sorar — adı verilen bir kalıbın katılımcılarını
bulmak, adı verilen bir testin neyi kanıtladığını açıklamak, verilen bir değişiklik isteğinin nasıl
gerçekleştirileceğini taslak olarak anlatmak, karşılamanın neden iş parçacığı güvenli olduğunu açıklamak. İkililer:
her üye bir bölümü sunar ve soruların en az ikisini yanıtlar.

| Düzey | Puan | Tanım |
|---|---|---|
| Mükemmel | 13 | a–d'nin hepsi ve dört sorunun dördü doğru yanıtlanmış |
| İyi | 10 | (b) dahil a–d'den en az üçü ve dört sorunun üçü doğru yanıtlanmış |
| Yeterli | 6 | a–d'den en az ikisi ve dört sorunun ikisi doğru yanıtlanmış |
| Yetersiz | 3 | Diğer durumlar (sunum yapılmış) |

## Kalıp gerekçe tablosu şablonu

Bu tabloyu `SPEC.md` (§5) ve `REPORT.md` içine kopyalayın. Sayılan her kalıp için bir satır; isterseniz mimari
kalıplar için de satır ekleyin (en az on kalıba dahil edilmezler). İlk satır beklenen derinliğe bir örnektir.

| # | Kalıp (aile) | PatternShop'taki kuvvet / sorun | Katılımcılar (tipler ve `@PatternRole` rolleri) | Değerlendirilen alternatif ve neden seçilmediği | Modern Java biçimi | Onu gösteren test(ler) | Ders modülü |
|-|:----|:------|:-------|:------|:----|:-----------|:--|
| 1 | Strategy (Strateji) (davranışsal) | Dört promosyon türü aynı biçimde fiyatlandırılmalı ve fiyatlandırma hattına dokunmadan yeni türler eklenecek | `PromotionRule` (strateji), `BuyXGetYFreeRule`, `CategoryPercentOffRule`, … (somut stratejiler), `PricingPipeline` (bağlam) | Hattın içinde promosyon tanımı üzerinde `switch`: bugün daha kısa, ama her yeni tür hattı ve testlerini değiştirir | record'lardan oluşan sealed arayüz; her kural veri taşıdığı için lambda kullanılmadı | `PricingAcceptance.categoryPercentOffRoundsHalfEvenPerLine`, `PromotionRuleTest.buyXGetYCountsWholeGroupsOnly` | m06 |
| 2 | | | | | | | |
| … | | | | | | | |

**Değerlendirilip reddedilen kalıplar** (C2'de Mükemmel düzeyi için en az bir tane):

| Kalıp | Nerede cazip geldi | Neden kullanılmadı |
|---|---|---|
| | | |

## Değerlendirme formu

| Kimlik | Ölçüt | En çok | Düzey | Puan | Kanıt (dosyalar, komutlar, notlar) |
|---|---|---|---|---|---|
| G1–G4 | Koşullar geçildi mi? (evet / hayır — hayırsa C3–C8 = 0) | — | | | |
| C1 | Öğrencinin `SPEC.md` dosyası | 15 | | | |
| C2 | Kalıp planı ve gerekçe tablosu | 10 | | | |
| C3 | Kapının ötesinde doğruluk | 10 | | | |
| C4 | Kalıp kullanımı | 14 | | | |
| C5 | Modern Java ve eşzamanlılık | 8 | | | |
| C6 | Mimari | 6 | | | |
| C7 | Test kalitesi | 7 | | | |
| C8 | Kod kalitesi ve süreç | 5 | | | |
| C9 | Rapor | 12 | | | |
| C10 | Sunum ve savunma | 13 | | | |
| | **Toplam** | **100** | | | |
