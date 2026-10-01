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
| abstraction / implementor (Bridge) | soyutlama / uygulayıcı (implementor) | |
| Composite | Composite (Bileşik) | |
| part–whole hierarchy | parça–bütün hiyerarşisi | |
| transparency vs. safety (Composite) | şeffaflık / güvenlik | `add`/`remove` bileşende mi, yalnızca bileşikte mi |
| Decorator | Decorator (Dekoratör) | |
| Facade | Facade (Cephe) | |
| subsystem | alt sistem | |
| compensation (undoing earlier steps) | telafi (compensation) | |
| Flyweight | Flyweight (Sinek Siklet) | |
| intrinsic / extrinsic state | içsel / dışsal durum | |
| Proxy | Proxy (Vekil) | |
| Chain of Responsibility | Chain of Responsibility (Sorumluluk Zinciri) | |
| middleware | ara katman yazılımı (middleware) | Chain of Responsibility'nin "önce ve sonra" biçimi |
| fail-fast / collect-all (validation) | ilk hatada dur (fail-fast) / tüm hataları topla (collect-all) | |
| Command | Command (Komut) | |
| invoker / receiver (Command) | çağırıcı / alıcı | |
| macro command | makro komut | |
| undo / redo | geri alma / yineleme (undo/redo) | |
| Interpreter | Interpreter (Yorumlayıcı) | |
| Iterator | Iterator (Yineleyici) | |
| external / internal iteration | dış / iç yineleme | |
| fail-fast iterator | hızlı-başarısız (fail-fast) yineleyici | |
| spliterator | spliterator (bölünebilir yineleyici) | |
| stream gatherer; initializer / integrator / finisher | gatherer (akış toplayıcısı); başlatıcı / bütünleştirici / bitirici | |
| in-order / pre-order / level-order traversal | ortanca sıralı / önce kök / seviye sıralı dolaşma | |
| hook method | kanca metot (hook) | Template Method |
| Mediator | Mediator (Arabulucu) | |
| colleague (Mediator role) | iş arkadaşı (colleague) | |
| Memento | Memento (Hatıra) | |
| originator / caretaker (Memento roles) | kaynak (originator) / bekçi (caretaker) | |
| snapshot | anlık görüntü (snapshot) | |
| Observer | Observer (Gözlemci) | |
| subject / listener / subscription | özne / dinleyici / abonelik | |
| lapsed listener | unutulmuş dinleyici (lapsed listener) | bellek sızıntısı |
| event bus / dead event | olay veri yolu (event bus) / ölü olay (dead event) | |
| back-pressure / demand | geri basınç (back-pressure) / talep (demand) | `java.util.concurrent.Flow` |
| State | State (Durum) | |
| Strategy | Strategy (Strateji) | |
| Template Method | Template Method (Şablon Metot) | |
| Visitor | Visitor (Ziyaretçi) | |
| Producer–Consumer | Producer–Consumer (Üretici–Tüketici) | |
| Guarded Suspension | Guarded Suspension (Korumalı Bekletme) | koşul sağlanana dek bekle |
| Balking | Balking (Vazgeçme) | koşul yoksa hemen dön |
| Immutable Object | Immutable Object (Değişmez Nesne) | |
| Repository | Repository (Depo) | |
| Dependency Injection | Dependency Injection (Bağımlılık Enjeksiyonu) | kısaltma: DI |
| Ports and Adapters / Hexagonal Architecture | Ports and Adapters (Portlar ve Adaptörler) / Altıgen Mimari | |
| anti-pattern | anti-kalıp | |
| god object | tanrı nesnesi (god object) | |

## Object-oriented design · Nesne yönelimli tasarım

| English | Türkçe |
|---|---|
| encapsulation | kapsülleme |
| inheritance | kalıtım |
| polymorphism | çok biçimlilik |
| expression problem | ifade problemi (expression problem) |
| abstraction | soyutlama |
| composition over inheritance | kalıtım yerine bileşim |
| coupling / cohesion | bağlaşım / uyum |
| loose coupling | gevşek bağlaşım |
| cache / cache hit / cache miss | önbellek / isabet (hit) / ıska (miss) |
| time-to-live (TTL) | yaşam süresi (TTL) |
| least recently used (LRU) eviction | en uzun süredir kullanılmayanı (LRU) çıkarma |
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
| wrapper | sarmalayıcı |
| object adapter / class adapter | nesne adaptörü / sınıf adaptörü |
| target / adaptee (Adapter) | hedef (target) / uyarlanan (adaptee) |
| virtual / protection / caching / remote proxy | sanal / koruma / önbellek / uzak vekil |
| view (vs. copy) | görünüm (kopyaya karşı) |
| memoization | bellekleme (memoization) |
| double dispatch | çift yönlendirme (double dispatch) |
| state machine / state transition | durum makinesi / durum geçişi |
| invariant | değişmez (invariant) |
| immutable / immutability | değişmez / değişmezlik |
| value object | değer nesnesi |
| defensive copy / unmodifiable view | savunmacı kopya / değiştirilemez görünüm (view) |
| wither | wither (`withX` metodu, yeni değer döndürür) |
| side effect | yan etki |
| class diagram / sequence diagram | sınıf diyagramı / sıralama diyagramı |
| god class | her işi yapan sınıf (god class) |
| role interface | rol arayüzü |
| fragile base class | kırılgan taban sınıf |
| composition root | bileşim kökü (composition root) |
| characterization test | karakterizasyon testi |
| optional operation | isteğe bağlı işlem (optional operation) |

## Java language · Java dili

| English | Türkçe |
|---|---|
| record | record (kayıt) |
| sealed interface / class | sealed (mühürlü) arayüz / sınıf |
| pattern matching | desen eşleme (pattern matching) |
| record pattern | record deseni |
| exhaustive switch | eksiksiz (exhaustive) switch |
| guard (`case … when …`) | koşul (guard, `when`) |
| compact constructor | kompakt kurucu |
| telescoping constructor | teleskopik kurucu |
| copy constructor | kopya kurucu (copy constructor) |
| shallow copy / deep copy | yüzeysel kopya / derin kopya |
| step builder | step builder (adım adım builder) |
| constructor | kurucu (constructor) |
| lambda expression / method reference | lambda ifadesi / metot referansı |
| functional interface | fonksiyonel arayüz |
| higher-order function | yüksek mertebeden fonksiyon |
| function composition | fonksiyon bileşimi |
| currying / partial application | currying / kısmi uygulama |
| lazy evaluation | tembel değerlendirme |
| errors as values; railway (fail-fast chain) | değer olarak hatalar; demiryolu (railway) zinciri |
| "parse, don't validate" | doğrulama değil, ayrıştırma |
| generic type | jenerik tip |
| dynamic proxy / invocation handler | dinamik vekil / çağrı işleyici (InvocationHandler) |
| double-checked locking | çift denetimli kilitleme (double-checked locking) |
| virtual thread | sanal iş parçacığı (virtual thread) |
| platform thread | platform iş parçacığı |
| thread-per-task | görev başına iş parçacığı (thread-per-task) |
| carrier thread / pinning | taşıyıcı iş parçacığı / sabitlenme (pinning) |
| bounded queue / poison pill | sınırlı kuyruk / zehirli hap (poison pill) |
| guard / spurious wake-up | koruma koşulu (guard) / sahte uyanma (spurious wake-up) |
| monitor / condition (variable) | monitör / koşul (değişkeni) |
| copy-on-write / safe publication | yazarken kopyalama (copy-on-write) / güvenli yayınlama |
| fan-out / fan-in | dağıtma / toplama (fan-out / fan-in) |
| deadline / timeout / cancellation | son süre (deadline) / zaman aşımı (timeout) / iptal |
| subtask / owner thread / joiner | alt görev / sahip iş parçacığı / birleştirici (joiner) |
| structured concurrency | yapılandırılmış eşzamanlılık |
| scoped value | kapsamlı değer (scoped value) |
| thread-safe | iş parçacığı güvenli |
| race condition / deadlock | yarış durumu / kilitlenme |
| preview feature | önizleme özelliği (preview) |
| class loader | sınıf yükleyici (class loader) |
| lazy holder idiom | lazy holder (tembel tutucu) yöntemi |
| service provider interface (SPI) | servis sağlayıcı arayüzü (SPI) |
| source-code launcher | kaynak kod başlatıcı |
| data-oriented programming | veri odaklı programlama |
| value-based class | değer tabanlı sınıf (value-based class) |
| compact object headers (JEP 534) | sıkıştırılmış nesne başlıkları |
| abstract syntax tree (AST) | soyut sözdizimi ağacı (AST) |
| lexer / parser | sözcük çözümleyici (lexer) / ayrıştırıcı (parser) |
| recursive descent | özyinelemeli iniş (recursive descent) |
| operator precedence / associativity | operatör önceliği / birleşme yönü (associativity) |

## Testing & tooling · Test ve araçlar

| English | Türkçe |
|---|---|
| unit test | birim testi |
| test double (stub, fake, spy, mock) | test ikizi (stub, fake, spy, mock) |
| contract test | sözleşme testi |
| starter code | başlangıç kodu |
| reference solution | referans çözüm |
| assignment | ödev |
| capstone project | bitirme projesi |
| acceptance criteria | kabul kriterleri |
| refactoring | yeniden düzenleme (refactoring) |
| build | derleme (build) |
| spec-driven development | spesifikasyon güdümlü geliştirme (SDD) |
