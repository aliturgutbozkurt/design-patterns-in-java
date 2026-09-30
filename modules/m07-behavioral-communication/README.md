# m07 — Behavioral Patterns II: Communication · Davranışsal Kalıplar II: İletişim

> Week 9 · 9. Hafta — [Spec](../../specs/SPEC-m07-behavioral-communication.md)

| | English | Türkçe |
|---|---|---|
| Lesson · Ders | [Markdown](lesson/lesson.en.md) · [PDF](lesson/lesson.en.pdf) | [Markdown](lesson/lesson.tr.md) · [PDF](lesson/lesson.tr.pdf) |
| Assignment 01 · Ödev 01 — Live auction notifications (Observer) | [EN](assignments/01-live-auction.en.md) | [TR](assignments/01-live-auction.tr.md) |
| Assignment 02 · Ödev 02 — Expense approval chain (Chain of Responsibility) | [EN](assignments/02-expense-approval.en.md) | [TR](assignments/02-expense-approval.tr.md) |

## Examples · Örnekler

Run any example without a build · Her örneği derlemeden çalıştırın:
`java modules/m07-behavioral-communication/src/main/java/io/github/aliturgutbozkurt/patterns/m07/examples/<path>`

| Pattern · Kalıp | Example · Örnek | Run · Çalıştır (`<path>`) |
|---|---|---|
| Observer | Stock ticker (classic GoF) · Borsa göstergesi (klasik) | `observer/StockTickerDemo.java` |
| Observer | Functional listeners + `Subscription` · Fonksiyonel dinleyiciler | `observer/ModernTickerDemo.java` |
| Observer | `PropertyChangeSupport` thermostat · Termostat | `observer/ThermostatDemo.java` |
| Observer | Typed PatternShop event bus · Tipli olay veri yolu | `observer/EventBusDemo.java` |
| Observer (`Flow`) | Back-pressure, drops, processor · Geri basınç, düşürme, işlemci | `observer/FlowDemo.java` |
| Mediator | Chat room · Sohbet odası | `mediator/ChatRoomDemo.java` |
| Mediator | Air traffic control, sealed requests · Hava trafik kontrolü | `mediator/AirTrafficDemo.java` |
| Mediator | Headless sign-up form · Arayüzsüz kayıt formu | `mediator/SignUpFormDemo.java` |
| Chain of Responsibility | Support escalation, linked vs. functions · Destek yükseltme | `chain/SupportEscalationDemo.java` |
| Chain of Responsibility | HTTP-style middleware pipeline · Middleware hattı | `chain/MiddlewareDemo.java` |
| Chain of Responsibility | Collect-all vs. fail-fast validation · Doğrulama zincirleri | `chain/ValidationChainDemo.java` |
| Memento | Opaque classic memento · Opak klasik memento | `memento/ClassicMementoDemo.java` |
| Memento | Editor undo/redo with record snapshots · Geri al/yinele | `memento/EditorUndoDemo.java` |
| Memento | Game save slots (`SequencedMap`) · Oyun kayıt yuvaları | `memento/GameSaveDemo.java` |

## Build & test · Derleme ve test

```bash
./mvnw -q -pl modules/m07-behavioral-communication verify           # examples + solutions
./mvnw -pl modules/m07-behavioral-communication test -Pexercises     # your assignments · ödevleriniz
```
