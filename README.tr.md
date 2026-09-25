# Java ile Tasarım Kalıpları (Java 27)

[![CI](https://github.com/aliturgutbozkurt/design-patterns-in-java/actions/workflows/ci.yml/badge.svg)](https://github.com/aliturgutbozkurt/design-patterns-in-java/actions/workflows/ci.yml)
[![Kod: MIT](https://img.shields.io/badge/kod-MIT-blue.svg)](LICENSE)
[![Metin: CC BY 4.0](https://img.shields.io/badge/metin-CC%20BY%204.0-lightgrey.svg)](LICENSE-CONTENT.txt)

🇬🇧 [English README](README.md)

2026 Güz dönemi için **modern Java (JDK 27) ile tasarım kalıpları** üzerine açık ve iki dilli (İngilizce + Türkçe) bir
ders: 12 modül ve bir bitirme projesi. Her modülde ders notları (Markdown + PDF), çok sayıda çalıştırılabilir örnek,
testli ödevler ve referans çözümler bulunur.

> 🚧 Ders, spesifikasyon güdümlü geliştirme (SDD) ile modül modül hazırlanıyor —
> [ilerleme](https://github.com/aliturgutbozkurt/design-patterns-in-java/milestones).

## Neler öğreneceksiniz

- Klasik GoF kalıpları — her birini **ne zaman** kullanmalı, ne zaman kullanmamalı
- Modern Java'nın (record, sealed tipler, desen eşleme, lambda, sanal iş parçacıkları) bu kalıpları nasıl sadeleştirdiği ya da gereksiz kıldığı
- SOLID tasarım, fonksiyonel ve veri odaklı tasarım, eşzamanlılık kalıpları ve temiz mimari
- Gereksinimleri bir spesifikasyona, spesifikasyonu da test edilmiş koda dönüştürmek (bitirme projesi)

## Ders haritası

| Hafta | Modül | Konular |
|---|---|---|
| 1 | m00 Kurulum ve Modern Java | JDK 27, derlemeden kod çalıştırma, record, sealed tipler, desen eşleme |
| 2 | m01 OOP, SOLID ve UML | SOLID, kalıtım yerine bileşim, Mermaid ile UML |
| 3 | m02 Yaratımsal I | Singleton, Static Factory, Factory Method, Abstract Factory |
| 4 | m03 Yaratımsal II | Builder, Prototype, Object Pool |
| 5 | m04 Yapısal I | Adapter, Decorator, Proxy |
| 6 | m05 Yapısal II | Composite, Bridge, Facade, Flyweight |
| 7 | — | Ara sınav |
| 8 | m06 Davranışsal I | Strategy, Template Method, Command, Iterator |
| 9 | m07 Davranışsal II | Observer, Mediator, Chain of Responsibility, Memento |
| 10 | m08 Davranışsal III | State, Visitor, Interpreter |
| 11 | m09 Fonksiyonel ve Veri Odaklı | Record + sealed tipler, Result tipleri, fonksiyon olarak kalıplar |
| 12 | m10 Eşzamanlılık Kalıpları | Sanal iş parçacıkları, Producer–Consumer, Structured Concurrency, Scoped Values |
| 13 | m11 Mimari | Dependency Injection, Repository, Altıgen mimari, anti-kalıplar |
| 9–14 | Bitirme projesi | PatternShop — en az 10 kalıp kullanan bir sipariş işleme sistemi |

Ayrıntılı plan: [müfredat](docs/syllabus.tr.md).

## Başlarken

### 1. JDK 27'yi kurun

| İşletim sistemi | Komut |
|---|---|
| macOS | `brew install --cask temurin` (en güncel Temurin'i, yani 27'yi kurar) |
| Windows | [adoptium.net](https://adoptium.net/temurin/releases/?version=27) adresinden JDK 27 `.msi` yükleyicisini indirin ve "Set JAVA_HOME" seçeneğini işaretleyin |
| Linux / diğer | [SDKMAN!](https://sdkman.io): `sdk list java`, ardından `sdk install java <27.x-tem kimliği>` — ya da [adoptium.net](https://adoptium.net/temurin/releases/?version=27) arşivini kullanın |

Kontrol: `java -version` çıktısında `27` görünmeli. Birden fazla JDK kuruluysa `JAVA_HOME`'u 27'ye yönlendirin
(macOS: `export JAVA_HOME=$(/usr/libexec/java_home -v 27)`).

### 2. Depoyu klonlayın

```bash
git clone https://github.com/aliturgutbozkurt/design-patterns-in-java.git
cd design-patterns-in-java
```

Maven kurmanıza **gerek yok** — depo Maven Wrapper ile gelir (`./mvnw`, Windows'ta `mvnw.cmd`).

### 3. Bir örneği çalıştırın — derleme gerekmez

Her örneğin bir `main` metodu vardır ve doğrudan kaynak koddan çalışır:

```bash
java modules/<modül>/src/main/java/io/github/aliturgutbozkurt/patterns/<mNN>/examples/<kalıp>/<Demo>.java
```

Ya da projeyi IntelliJ IDEA / VS Code / Eclipse ile açıp `main` metodunu çalıştırın.

### 4. Derleyin ve test edin

```bash
./mvnw verify                            # her şey
./mvnw -pl modules/<modül> verify        # tek bir modül
```

### 5. Bir ödev yapın

1. `modules/<modül>/assignments/` klasöründeki ödev metnini okuyun (İngilizce `.en.md`, Türkçe `.tr.md`).
2. Kodunuzu `src/main/java/.../exercises/exNN/` içindeki `TODO(exNN)` işaretli yerlere yazın.
3. Testleriniz yeşil olana kadar çalıştırın:
   ```bash
   ./mvnw -pl modules/<modül> test -Pexercises
   ```
4. Ancak bundan sonra `.../solutions/exNN/` altındaki referans çözümle karşılaştırın.

## Eğitmenler için

- Ders PDF'leri (İngilizce ve Türkçe) her modülün `lesson/` klasöründedir ve her
  [sürüme](https://github.com/aliturgutbozkurt/design-patterns-in-java/releases) eklenir.
- Önerilen haftalık plan ve değerlendirme ağırlıkları: [müfredat](docs/syllabus.tr.md).
- Ödevler sözleşme testleriyle gelir; aynı testler hem öğrenci kodunu hem referans çözümü değerlendirir.

## Depo rehberi

| Yol | İçerik |
|---|---|
| `modules/<modül>/` | ders (MD + PDF), örnekler, ödevler, çözümler, testler |
| `capstone/` | bitirme projesi tanımı, değerlendirme ölçütleri, başlangıç kodu, referans çözüm |
| `docs/` | müfredat, sözlük (EN ↔ TR), Java 27 özellik notları, şablonlar |
| `SPEC.md`, `tasks/` | dersin spesifikasyonu, planı ve görev listesi (SDD) |

## Katkıda bulunma

Düzeltme ve önerilere açığız — lütfen [CONTRIBUTING.md](CONTRIBUTING.md) dosyasını okuyun ve bir
[issue](https://github.com/aliturgutbozkurt/design-patterns-in-java/issues/new/choose) açın.

## Lisans

Kod: [MIT](LICENSE) · Ders metinleri, ödev metinleri, diyagramlar ve PDF'ler: [CC BY 4.0](LICENSE-CONTENT.txt).
