# m11 — Architecture & Enterprise Patterns · Mimari ve Kurumsal Kalıplar

> Week 13 · 13. Hafta — [Spec](../../specs/SPEC-m11-architecture-enterprise.md)

| | English | Türkçe |
|---|---|---|
| Lesson · Ders | [Markdown](lesson/lesson.en.md) · [PDF](lesson/lesson.en.pdf) | [Markdown](lesson/lesson.tr.md) · [PDF](lesson/lesson.tr.pdf) |
| Assignment 01 · Ödev 01 — PatternShop checkout as Ports & Adapters (Repository, hexagonal service, ArchUnit) | [EN](assignments/01-checkout-hexagon.en.md) | [TR](assignments/01-checkout-hexagon.tr.md) |
| Assignment 02 · Ödev 02 — Order lifecycle with domain events after commit | [EN](assignments/02-order-lifecycle-events.en.md) | [TR](assignments/02-order-lifecycle-events.tr.md) |

## Examples · Örnekler

Run any example without a build · Her örneği derlemeden çalıştırın:
`java modules/m11-architecture-enterprise/src/main/java/io/github/aliturgutbozkurt/patterns/m11/examples/<path>`

| Topic · Konu | Example · Örnek | Run · Çalıştır (`<path>`) |
|---|---|---|
| Dependency Injection | Lifetimes in a composition root · Bileşim kökünde yaşam süreleri | `di/LifetimesDemo.java` |
| Dependency Injection | Constructor vs. setter vs. hidden dependencies · Enjeksiyon biçimleri | `di/InjectionStylesDemo.java` |
| Dependency Injection | A 100-line reflective container · 100 satırlık kapsayıcı | `di/MiniContainerDemo.java` |
| Repository | Catalogue with Specifications, memory + file · Belirtimli katalog | `repository/CatalogRepositoryDemo.java` |
| Repository | Optimistic versioning · İyimser sürümleme | `repository/OptimisticLockingDemo.java` |
| Ports & Adapters | Money transfer hexagon · Para transferi altıgeni | `hexagonal/transfer/config/TransferDemo.java` |
| Ports & Adapters | PatternShop on memory and files · Bellek ve dosyada PatternShop | `hexagonal/shop/config/ShopDemo.java` |
| Domain events | Commit, then dispatch · Önce commit, sonra dağıtım | `events/DomainEventsDemo.java` |
| Domain events | Transactional outbox, idempotent consumer · İşlemsel giden kutusu | `events/OutboxDemo.java` |
| Anti-pattern · Anti-kalıp | God class → Chain, Strategy, Repository, Facade · Tanrı sınıf | `antipatterns/GodClassDemo.java` |
| Anti-pattern · Anti-kalıp | Anaemic vs. rich domain model · Kansız ve zengin model | `antipatterns/AnaemicDomainDemo.java` |
| Anti-pattern · Anti-kalıp | Singleton / Service Locator state leak · Global durum sızıntısı | `antipatterns/GlobalStateDemo.java` |
| Anti-pattern · Anti-kalıp | Patternitis · Spekülatif genellik | `antipatterns/PatternitisDemo.java` |
| Refactoring · Yeniden düzenleme | Type code → sealed type · Tip kodu → mühürlü tip | `refactoring/ShippingRefactoringDemo.java` |
| Refactoring · Yeniden düzenleme | Hard-wired calls → events · Sıkı bağlı çağrılar → olaylar | `refactoring/NotificationsRefactoringDemo.java` |
| Test doubles · Test ikizleri | Dummy, stub, fake, spy, mock by hand · Elle yazılmış ikizler | `testdoubles/TestDoublesDemo.java` |
| Test doubles · Test ikizleri | Fake clock, session expiry · Sahte saat | `testdoubles/SessionExpiryDemo.java` |
| Architecture rules · Mimari kurallar | Erosion the compiler cannot see · Derleyicinin görmediği aşınma | `erosion/ErosionDemo.java` |
| Architecture rules · Mimari kurallar | Class-File API dependency scanner · Class-File API tarayıcı | `archcheck/ArchCheckDemo.java` |

Architecture tests · Mimari testler (ArchUnit, test scope): `src/test/java/.../m11/architecture/` —
`HexagonalShopArchitectureTest`, `ErosionRulesTest`, `CourseConventionsArchTest`.

## Build & test · Derleme ve test

```bash
./mvnw -q -pl modules/m11-architecture-enterprise verify           # examples, architecture tests, solutions
./mvnw -pl modules/m11-architecture-enterprise test -Pexercises     # your assignments · ödevleriniz
```
