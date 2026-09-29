# m02 — Creational Patterns I: Factories · Yaratımsal Kalıplar I: Fabrikalar

> Week 3 · 3. Hafta — [Spec](../../specs/SPEC-m02-creational-factories.md)

| | English | Türkçe |
|---|---|---|
| Lesson · Ders | [Markdown](lesson/lesson.en.md) · [PDF](lesson/lesson.en.pdf) | [Markdown](lesson/lesson.tr.md) · [PDF](lesson/lesson.tr.pdf) |
| Assignment 01 · Ödev 01 — Colour static factories | [EN](assignments/01-colour-factories.en.md) | [TR](assignments/01-colour-factories.tr.md) |
| Assignment 02 · Ödev 02 — Game levels (Abstract Factory) | [EN](assignments/02-game-levels.en.md) | [TR](assignments/02-game-levels.tr.md) |

## Examples · Örnekler

Run any example without a build · Her örneği derlemeden çalıştırın:
`java modules/m02-creational-factories/src/main/java/io/github/aliturgutbozkurt/patterns/m02/examples/<path>`

| Pattern · Kalıp | Example · Örnek | Run · Çalıştır (`<path>`) |
|---|---|---|
| Singleton | `enum` singleton | `singleton/EnumSingletonDemo.java` |
| Singleton | Lazy holder idiom · Tembel tutucu yöntemi | `singleton/HolderSingletonDemo.java` |
| Singleton → DI | Testability before/after · Test edilebilirlik önce/sonra | `singleton/SingletonTestabilityDemo.java` |
| Static Factory Method | Names, caching, subtypes · Adlar, önbellek, alt tipler | `staticfactory/StaticFactoryDemo.java` |
| Factory Method | Exporters, classic + modern · Dışa aktarıcılar | `factorymethod/ExportDemo.java` |
| Factory Method | Logistics · Lojistik | `factorymethod/LogisticsDemo.java` |
| Abstract Factory | UI widget families · Arayüz bileşeni aileleri | `abstractfactory/WidgetDemo.java` |
| Abstract Factory | Cloud providers · Bulut sağlayıcılar | `abstractfactory/CloudDemo.java` |
| ServiceLoader | Export plugins · Dışa aktarma eklentileri | `serviceloader/PluginDemo.java` |

## Build & test · Derleme ve test

```bash
./mvnw -q -pl modules/m02-creational-factories verify           # examples + solutions
./mvnw -pl modules/m02-creational-factories test -Pexercises     # your assignments · ödevleriniz
```
