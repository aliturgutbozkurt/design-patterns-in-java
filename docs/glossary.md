# Glossary · Sözlük (EN ↔ TR)

Rules for Turkish text (CLAUDE.md §7): **pattern names stay in English**; on first use in a lesson write the Turkish
term in parentheses — e.g. "Strategy (Strateji)". Code identifiers are never translated. Use the terms below; if a term
is missing, add it here in the same PR instead of inventing a new translation inline.

Türkçe metin kuralları: **kalıp adları İngilizce kalır**; derste ilk geçtiği yerde Türkçesi parantez içinde yazılır.
Kod tanımlayıcıları çevrilmez. Listede olmayan bir terim gerekiyorsa aynı PR'da buraya ekleyin.

## Design patterns · Tasarım kalıpları

| English | Türkçe | Note |
|---|---|---|
| design pattern | tasarım kalıbı | "tasarım deseni" de yaygındır; bu derste *kalıp* kullanılır |
| creational / structural / behavioral pattern | yaratımsal / yapısal / davranışsal kalıp | |
| Singleton | Singleton (Tekil Nesne) | |
| Factory Method | Factory Method (Fabrika Metodu) | |
| Static Factory Method | Static Factory Method (Statik Fabrika Metodu) | |
| Abstract Factory | Abstract Factory (Soyut Fabrika) | |
| Builder | Builder (İnşacı) | |
| Prototype | Prototype (Prototip) | |
| Object Pool | Object Pool (Nesne Havuzu) | |
| Adapter | Adapter (Adaptör) | |
| Bridge | Bridge (Köprü) | |
| Composite | Composite (Bileşik) | |
| Decorator | Decorator (Dekoratör) | |
| Facade | Facade (Cephe) | |
| Flyweight | Flyweight (Sinek Siklet) | |
| Proxy | Proxy (Vekil) | |
| Chain of Responsibility | Chain of Responsibility (Sorumluluk Zinciri) | |
| Command | Command (Komut) | |
| Interpreter | Interpreter (Yorumlayıcı) | |
| Iterator | Iterator (Yineleyici) | |
| Mediator | Mediator (Arabulucu) | |
| Memento | Memento (Hatıra) | |
| Observer | Observer (Gözlemci) | |
| State | State (Durum) | |
| Strategy | Strategy (Strateji) | |
| Template Method | Template Method (Şablon Metot) | |
| Visitor | Visitor (Ziyaretçi) | |
| Producer–Consumer | Producer–Consumer (Üretici–Tüketici) | |
| Repository | Repository (Depo) | |
| Specification | Specification (Belirtim) | sorgu nesnesi; `and` / `or` / `not` ile birleşir |
| optimistic / pessimistic locking | iyimser / kötümser kilitleme | sürüm (version) karşılaştırması |
| aggregate / aggregate root | aggregate (küme) / küme kökü (aggregate root) | DDD |
| unit of work | iş birimi (unit of work) | önce commit, sonra olay dağıtımı |
| domain event | alan olayı (domain event) | geçmiş zamanla adlandırılır: `OrderPlaced` |
| transactional outbox | işlemsel giden kutusu (transactional outbox) | |
| at-least-once delivery / idempotent consumer | en az bir kez teslim / idempotent tüketici | |
| Dependency Injection | Dependency Injection (Bağımlılık Enjeksiyonu) | kısaltma: DI |
| composition root | bileşim kökü (composition root) | nesne grafiğinin kurulduğu tek yer |
| lifetime: application / per-request / transient | yaşam süresi: uygulama / istek başına / geçici | |
| DI container | DI kapsayıcısı (container) | Spring, Guice, CDI |
| Service Locator | Service Locator (Servis Bulucu) | anti-kalıp olarak |
| Ports and Adapters / Hexagonal Architecture | Ports and Adapters (Portlar ve Adaptörler) / Altıgen Mimari | |
| inbound / outbound port | giriş portu (inbound) / çıkış portu (outbound) | |
| application service / use case | uygulama servisi / kullanım senaryosu (use case) | |
| layered / onion architecture | katmanlı / soğan (onion) mimari | |
| architecture erosion | mimari aşınma | |
| anti-pattern | anti-kalıp | |
| god class | tanrı sınıf (god class) | |
| anaemic / rich domain model | kansız (anaemic) / zengin alan modeli | |
| speculative generality ("patternitis") | spekülatif genellik ("patternitis", kalıp hastalığı) | |

## Object-oriented design · Nesne yönelimli tasarım

| English | Türkçe |
|---|---|
| encapsulation | kapsülleme |
| inheritance | kalıtım |
| polymorphism | çok biçimlilik |
| abstraction | soyutlama |
| composition over inheritance | kalıtım yerine bileşim |
| coupling / cohesion | bağlaşım / uyum |
| loose coupling | gevşek bağlaşım |
| Single Responsibility Principle | Tek Sorumluluk İlkesi |
| Open/Closed Principle | Açık/Kapalı İlkesi |
| Liskov Substitution Principle | Liskov Yerine Geçme İlkesi |
| Interface Segregation Principle | Arayüz Ayrımı İlkesi |
| Dependency Inversion Principle | Bağımlılığın Tersine Çevrilmesi İlkesi |
| interface | arayüz |
| abstract class | soyut sınıf |
| concrete class | somut sınıf |
| client (code) | istemci (kod) |
| delegation | yetki devri (delegasyon) |
| double dispatch | çift yönlendirme (double dispatch) |
| invariant | değişmez (invariant) |
| immutable / immutability | değişmez / değişmezlik |
| side effect | yan etki |
| class diagram / sequence diagram | sınıf diyagramı / sıralama diyagramı |

## Java language · Java dili

| English | Türkçe |
|---|---|
| record | record (kayıt) |
| sealed interface / class | sealed (mühürlü) arayüz / sınıf |
| pattern matching | desen eşleme (pattern matching) |
| record pattern | record deseni |
| exhaustive switch | eksiksiz (exhaustive) switch |
| compact constructor | kompakt kurucu |
| constructor | kurucu (constructor) |
| lambda expression / method reference | lambda ifadesi / metot referansı |
| functional interface | fonksiyonel arayüz |
| higher-order function | yüksek mertebeden fonksiyon |
| generic type | jenerik tip |
| virtual thread | sanal iş parçacığı (virtual thread) |
| platform thread | platform iş parçacığı |
| structured concurrency | yapılandırılmış eşzamanlılık |
| scoped value | kapsamlı değer (scoped value) |
| thread-safe | iş parçacığı güvenli |
| race condition / deadlock | yarış durumu / kilitlenme |
| preview feature | önizleme özelliği (preview) |
| source-code launcher | kaynak kod başlatıcı |
| data-oriented programming | veri odaklı programlama |

## Testing & tooling · Test ve araçlar

| English | Türkçe |
|---|---|
| unit test | birim testi |
| test double (stub, fake, spy, mock) | test ikizi (stub, fake, spy, mock) |
| dummy | dummy (yer tutucu ikiz) |
| state / interaction verification | durum / etkileşim doğrulaması |
| characterization test | karakterizasyon testi |
| architecture rule / test | mimari kural / mimari test |
| contract test | sözleşme testi |
| starter code | başlangıç kodu |
| reference solution | referans çözüm |
| assignment | ödev |
| capstone project | bitirme projesi |
| acceptance criteria | kabul kriterleri |
| refactoring | yeniden düzenleme (refactoring) |
| build | derleme (build) |
| spec-driven development | spesifikasyon güdümlü geliştirme (SDD) |
