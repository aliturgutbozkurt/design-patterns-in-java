# Müfredat — Java ile Tasarım Kalıpları (Java 27), 2026 Güz

## Ders bilgileri

| | |
|---|---|
| Düzey | Lisans (2.–4. sınıf) veya yeni başlayan geliştiriciler |
| Ön koşullar | Temel Java: sınıflar, arayüzler, koleksiyonlar, istisnalar, jenerikler (okuma düzeyinde) |
| Biçim | 14 hafta · haftada 3 saat ders + 2 saat laboratuvar (önerilen) |
| Dil | Aynı içerikte İngilizce ve Türkçe materyaller |
| Araçlar | JDK 27, herhangi bir Java IDE'si, Git; Maven depodaki wrapper ile gelir |

## Dersin öğrenme çıktıları

Dersin sonunda öğrenciler:

1. Bir tasarım kalıbının çözdüğü problemi **tanımlar**, gerekçeli ödünleşimlerle bir kalıp — ya da hiçbirini — **seçer**.
2. GoF yaratımsal, yapısal ve davranışsal kalıplarını deyimsel Java 27 ile **uygular**.
3. Mevcut kodu, davranışı testlerle sabit tutarak SOLID tasarıma ve kalıplara doğru **yeniden düzenler**.
4. Record, sealed tipler, desen eşleme ve sanal iş parçacıklarıyla fonksiyonel, veri odaklı ve eşzamanlılık kalıplarını **uygular**.
5. Yazılı bir spesifikasyondan başlayarak temiz mimariye sahip küçük bir sistem **tasarlar** ve tasarımını **savunur**.

## Haftalık plan

| Hafta | Modül | Konular | Laboratuvar / teslim |
|---|---|---|---|
| 1 | m00 Kurulum ve Modern Java | JDK 27, kaynak kod başlatıcı, record, sealed tipler, desen eşleme, lambda | m00 ödevleri |
| 2 | m01 OOP, SOLID ve UML | SOLID, kalıtım yerine bileşim, UML sınıf ve sıralama diyagramları | m01 ödevleri |
| 3 | m02 Yaratımsal I | Singleton, Static Factory Method, Factory Method, Abstract Factory, `ServiceLoader` | m02 ödevleri |
| 4 | m03 Yaratımsal II | Builder, Prototype, Object Pool, composition root | m03 ödevleri |
| 5 | m04 Yapısal I | Adapter, Decorator, Proxy (dinamik proxy dahil) | m04 ödevleri |
| 6 | m05 Yapısal II | Composite, Bridge, Facade, Flyweight | m05 ödevleri |
| 7 | — | **Ara sınav** (m00–m05) | — |
| 8 | m06 Davranışsal I | Strategy, Template Method, Command, Iterator, Stream Gatherers | m06 ödevleri |
| 9 | m07 Davranışsal II | Observer, Mediator, Chain of Responsibility, Memento | m07 ödevleri · **bitirme projesi başlar** (spesifikasyon) |
| 10 | m08 Davranışsal III | State, Visitor ve desen eşleme, Interpreter | m08 ödevleri · bitirme spesifikasyonu teslimi |
| 11 | m09 Fonksiyonel ve Veri Odaklı | Veri odaklı programlama, Result tipleri, fonksiyon olarak kalıplar | m09 ödevleri |
| 12 | m10 Eşzamanlılık Kalıpları | Sanal iş parçacıkları, Producer–Consumer, Structured Concurrency (önizleme), Scoped Values | m10 ödevleri |
| 13 | m11 Mimari ve Kurumsal | Dependency Injection, Repository, Portlar ve Adaptörler, anti-kalıplar, ArchUnit | m11 ödevleri |
| 14 | Bitirme projesi | Sunumlar ve tasarım savunması | bitirme kodu + raporu teslimi |

## Değerlendirme (önerilen)

| Bileşen | Ağırlık | Notlar |
|---|---|---|
| Ödevler | %30 | Sözleşme testleri ve kısa bir kod kalitesi incelemesiyle notlanır |
| Ara sınav | %20 | 1–6. haftalar |
| Bitirme projesi | %40 | Spesifikasyon %10 · uygulama ve testler %20 · rapor ve savunma %10 — bitirme değerlendirme ölçütlerine bakın |
| Katılım / kısa sınavlar | %10 | Her modülün sonundaki sınav soruları |

## Kurallar

### Yapay zekâ asistanları

Yapay zekâ kodlama asistanları 2026'da profesyonel pratiğin parçasıdır ve üç kuralla kullanılabilir:
1. Teslimatınızda bunları ne için kullandığınızı **belirtin**.
2. Teslim ettiğiniz her satırı **açıklayabilin** — kodunuzu adım adım anlatmanız istenebilir.
3. Bitirme projesinde **spesifikasyon ve tasarım kararları size aittir**: yalnızca kod değil; yazılı spesifikasyon,
   kalıp seçimleri ve gerekçeleri de notlanır.

### Akademik dürüstlük

Ödev metninde aksi belirtilmedikçe ödevler bireysel yapılır. Ciddi bir denemeden önce referans çözümü okumak ödevin
amacını boşa çıkarır — ne zaman bittiğini testler söyler.
