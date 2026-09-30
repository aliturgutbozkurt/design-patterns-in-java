# m04 — Structural Patterns I: Wrappers · Yapısal Kalıplar I: Sarmalayıcılar

> Week 5 · 5. Hafta — [Spec](../../specs/SPEC-m04-structural-wrappers.md)

| | English | Türkçe |
|---|---|---|
| Lesson · Ders | [Markdown](lesson/lesson.en.md) · [PDF](lesson/lesson.en.pdf) | [Markdown](lesson/lesson.tr.md) · [PDF](lesson/lesson.tr.pdf) |
| Assignment 01 · Ödev 01 — Data-source decorators | [EN](assignments/01-data-source-decorators.en.md) | [TR](assignments/01-data-source-decorators.tr.md) |
| Assignment 02 · Ödev 02 — Caching proxy with TTL | [EN](assignments/02-weather-cache.en.md) | [TR](assignments/02-weather-cache.tr.md) |

## Examples · Örnekler

Run any example without a build · Her örneği derlemeden çalıştırın:
`java modules/m04-structural-wrappers/src/main/java/io/github/aliturgutbozkurt/patterns/m04/examples/<path>`

| Pattern · Kalıp | Example · Örnek | Run · Çalıştır (`<path>`) |
|---|---|---|
| Adapter | Thermometer: class and lambda · Termometre: sınıf ve lambda | `adapter/thermometer/ThermometerAdapterDemo.java` |
| Adapter | Legacy payment: object vs class adapter · Eski ödeme: nesne ve sınıf adaptörü | `adapter/payment/PaymentAdapterDemo.java` |
| Adapter | JDK adapters · JDK adaptörleri | `adapter/jdk/JdkAdaptersDemo.java` |
| Decorator | Coffee (classic) · Kahve (klasik) | `decorator/coffee/classic/CoffeeDemo.java` |
| Decorator | Coffee (records) · Kahve (record'lar) | `decorator/coffee/modern/CoffeeDemo.java` |
| Decorator | Logging + retry, order matters · Günlükleme + yeniden deneme, sıra önemli | `decorator/resilience/ResilienceDemo.java` |
| Decorator | `java.io` stack, `CountingInputStream` · `java.io` yığını | `decorator/jdk/JdkDecoratorsDemo.java` |
| Decorator | Functional (`andThen`/`compose`) · Fonksiyonel | `decorator/functional/FunctionalDecoratorDemo.java` |
| Proxy | Virtual proxy, lazy image · Sanal vekil, tembel resim | `proxy/virtual/LazyImageDemo.java` |
| Proxy | Protection proxy · Koruma vekili | `proxy/protection/ProtectionProxyDemo.java` |
| Proxy | Caching proxy with TTL · TTL'li önbellek vekili | `proxy/caching/CachingProxyDemo.java` |
| Proxy | Dynamic proxy (`java.lang.reflect.Proxy`) · Dinamik vekil | `proxy/dynamic/DynamicProxyDemo.java` |

## Build & test · Derleme ve test

```bash
./mvnw -q -pl modules/m04-structural-wrappers verify           # examples + solutions
./mvnw -pl modules/m04-structural-wrappers test -Pexercises     # your assignments · ödevleriniz
```
