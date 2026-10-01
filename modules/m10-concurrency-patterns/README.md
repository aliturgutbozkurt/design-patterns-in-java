# m10 — Concurrency Patterns · Eşzamanlılık Kalıpları

> Week 12 · 12. Hafta — [Spec](../../specs/SPEC-m10-concurrency-patterns.md)

| | English | Türkçe |
|---|---|---|
| Lesson · Ders | [Markdown](lesson/lesson.en.md) · [PDF](lesson/lesson.en.pdf) | [Markdown](lesson/lesson.tr.md) · [PDF](lesson/lesson.tr.pdf) |
| Assignment 01 · Ödev 01 — Parallel price comparison (thread-per-task, deadlines, cancellation) | [EN](assignments/01-parallel-price-comparison.en.md) | [TR](assignments/01-parallel-price-comparison.tr.md) |
| Assignment 02 · Ödev 02 — Bounded job queue (Producer–Consumer, Guarded Suspension, Balking) | [EN](assignments/02-bounded-job-queue.en.md) | [TR](assignments/02-bounded-job-queue.tr.md) |

## Examples · Örnekler

Run any example without a build · Her örneği derlemeden çalıştırın:
`java modules/m10-concurrency-patterns/src/main/java/io/github/aliturgutbozkurt/patterns/m10/examples/<path>`

⚠️ The `structured/` demos use a **preview** API (JEP 533) · `structured/` demoları bir **önizleme** API'si kullanır:
`java --enable-preview --source 27 modules/m10-concurrency-patterns/src/main/java/io/github/aliturgutbozkurt/patterns/m10/examples/<path>`

| Pattern · Kalıp | Example · Örnek | Run · Çalıştır (`<path>`) |
|---|---|---|
| Thread-per-task | Web crawler on virtual threads · Sanal iş parçacıklarıyla web tarayıcı | `threadpertask/CrawlerDemo.java` |
| Thread-per-task | Fixed pool vs. one virtual thread per task · Sabit havuz ve görev başına sanal iş parçacığı | `threadpertask/ThreadPerTaskDemo.java` |
| Producer–Consumer | Log ingestion, sealed poison pill · Günlük alımı, sealed zehirli hap | `producerconsumer/LogIngestionDemo.java` |
| Producer–Consumer | Fulfilment pipeline, back-pressure · Sipariş hattı, geri basınç | `producerconsumer/FulfilmentDemo.java` |
| Guarded Suspension | Bounded buffer, lock/condition and monitor · Sınırlı tampon | `guarded/BoundedBufferDemo.java` |
| Guarded Suspension | Readiness gate for a warming service · Isınan servis için hazır olma kapısı | `guarded/ReadinessGateDemo.java` |
| Balking | Auto-saving document · Otomatik kaydedilen belge | `guarded/BalkingDemo.java` |
| Immutable Object | Order records with withers vs. mutable bean · Wither'lı sipariş record'ları | `immutable/ImmutableOrderDemo.java` |
| Immutable Object | Copy-on-write live config · Yazarken kopyalanan canlı yapılandırma | `immutable/LiveConfigDemo.java` |
| Scoped Values | Request context with nested rebinding · İç içe yeniden bağlamalı istek bağlamı | `scopedvalue/ScopedRequestDemo.java` |
| Scoped Values | `ThreadLocal` leak and inheritance vs. `ScopedValue` · Sızıntı ve aktarım | `scopedvalue/ThreadLocalVsScopedValueDemo.java` |
| `CompletableFuture` | Checkout pipeline · Ödeme hattı | `future/CheckoutPipelineDemo.java` |
| `CompletableFuture` | Quote fan-out, deadlines, cancellation facts · Teklif dağıtma, son süreler, iptal | `future/QuoteFanOutDemo.java` |
| Structured Concurrency ⚠️ | Trip planner, failure and deadline · Seyahat planlayıcı | `structured/TripPlannerDemo.java` |
| Structured Concurrency ⚠️ | Joiners: mirrors, scatter-gather, best effort · Joiner'lar | `structured/MirrorDownloadDemo.java` |
| Structured Concurrency ⚠️ | Scoped values in subtasks, owner rules · Alt görevlerde scoped value'lar | `structured/StructuredContextDemo.java` |

## Build & test · Derleme ve test

```bash
./mvnw -q -pl modules/m10-concurrency-patterns verify           # examples + solutions (compiled with --enable-preview)
./mvnw -pl modules/m10-concurrency-patterns test -Pexercises     # your assignments · ödevleriniz
```

The module's POM enables preview for compilation and tests; `PreviewIsolationTest` keeps preview code inside
`examples.structured`. · Modülün POM'u derleme ve testler için önizlemeyi açar; `PreviewIsolationTest` önizleme kodunu
`examples.structured` içinde tutar.
