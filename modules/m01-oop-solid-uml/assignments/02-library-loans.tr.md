# Ödev 02 — Kütüphane Ödünç İşlemleri: DIP + LSP

> Modül: m01-oop-solid-uml · Zorluk: ★★☆ · Tahmini süre: 2 saat

## Amaç

Küçük bir kütüphanenin ödünç verme kurallarını, **yalnızca soyutlamalara** bağlı olacak şekilde yazın: ödünçlerin
nerede saklandığı, üyelere nasıl haber verildiği ve hatta *bugünün hangi gün olduğu* kurucu üzerinden gelir
(Bağımlılığın Tersine Çevrilmesi İlkesi, DIP). Bu sayede testler kurallarınızı sahte nesnelerle çalıştırabilir —
mesajları kaydeden bir bildirici, sessiz bir lambda, elle ileri alınan bir saat — ve bir arayüzün her uygulaması bir
diğerinin yerine geçebilmelidir (Liskov Yerine Geçme İlkesi, LSP).

## Size verilenler

- `exercises/ex02/Book.java`, `Member.java`, `Loan.java` — record'lar — **değiştirmeyin**
- `exercises/ex02/LoanStore.java`, `Notifier.java`, `LoanService.java` — arayüzler — **değiştirmeyin**
- `exercises/ex02/InMemoryLoanStore.java`, `LibraryLoanService.java` — kodunuzu buraya yazın (`TODO(ex02)` işaretleri)

## Görevler

1. `InMemoryLoanStore implements LoanStore`: ödünçleri kaydedildikleri sırayla tutun; bilinmeyen bir ödüncü `remove`
   etmek hiçbir şey yapmaz; `activeLoansOf` yalnızca o üyenin ödünçlerini döndürür.
2. `LibraryLoanService(LoanStore, Notifier, Clock)`, şu kurallarla:

   | Kural | Ayrıntı |
   |---|---|
   | Ödünç süresi | iade tarihi = bugün (verilen `Clock`'a göre) + 14 gün |
   | Sınır | üye başına en fazla 3 aktif ödünç; 4. istek `IllegalStateException` fırlatır |
   | İade | yeri boşaltır |
   | Hatırlatmalar | `remindOverdue()` gecikmiş her ödünç için **bir** mesaj gönderir ve kaç mesaj gönderdiğini döndürür |
   | Gecikmiş | iade tarihi bugünden **önce**; iade tarihi bugün olan ödünç henüz gecikmiş sayılmaz |
   | Mesaj | `"Overdue: <title> (due <yyyy-MM-dd>)"` |

3. Servis kendi iş birlikçilerini oluşturmamalıdır: `new InMemoryLoanStore()` yok, somut bir bildirici yok, saat
   olmadan `LocalDate.now()` yok (`LocalDate.now(clock)` kullanın).
4. `null` kurucu argümanlarını, üyeleri ve kitapları `NullPointerException` ile reddedin.

## Kabul kriterleri

- [ ] `dueDateIsFourteenDaysAfterClockDate`
- [ ] `refusesFourthActiveLoan`
- [ ] `givingBackFreesASlot`
- [ ] `remindsOnlyOverdueLoans`
- [ ] `loanDueTodayIsNotOverdue`
- [ ] `reminderMessageFormat`
- [ ] `worksWithAnyNotifier` — mesajları kaydeden bir bildirici ve hiçbir şey yapmayan bir lambda (LSP)
- [ ] `storeContractHolds`
- [ ] `constructorDependsOnlyOnAbstractions` — kurucunun her parametresi bir arayüz ya da `Clock`
- [ ] `rejectsNullArguments`

## Testleri çalıştırın

```bash
./mvnw -pl modules/m01-oop-solid-uml test -Pexercises -Dtest='Ex02*'
```

Tüm testler yeşil = ödev bitti. `solutions/ex02/` ile ancak kendiniz denedikten **sonra** karşılaştırın.

## İpuçları

<details><summary>İpucu 1 — bugünün tarihi</summary>

`LocalDate.now(clock)` tarihi verilen saate sorar. Gerçek uygulamada `main` `Clock.systemDefaultZone()` verir; testler
ise ileri alabildikleri bir saat verir.

</details>

<details><summary>İpucu 2 — "bugünden önce"</summary>

İki tarih eşitken `dueDate.isBefore(today)` `false` döner — tam da ihtiyacınız olan kural.

</details>

## Ek hedefler (isteğe bağlı, notlandırılmaz)

- Bileşim kökü (composition root) görevi gören bir `main` yazın: bir depo, ekrana yazan bir bildirici, sistem saati
  ve servisi oluşturun.
- `remindOverdue()` için bir Mermaid sıralama diyagramı çizin.
