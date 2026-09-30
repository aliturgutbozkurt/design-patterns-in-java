# Ödev 02 — Masraf Onay Zinciri

> Modül: m07-behavioral-communication · Zorluk: ★★☆ · Tahmini süre: 2 saat

## Amaç

Çalışanlar masraf talepleri gönderir; bir talebi kimin onaylayabileceği tutarına ve kategorisine bağlıdır. İç içe
`if`'lerle dolu tek bir metot yerine bir **Chain of Responsibility (Sorumluluk Zinciri)** kurun: her onaylayıcı ya karar
verir ya da masrafı bir sonrakine iletir, zincir ilk kararda durur ve sonuç, masrafın geçtiği her onaylayıcıyı kaydeder.

## Size verilenler

- `exercises/ex02/Category.java` — `TRAVEL`, `MEALS`, `EQUIPMENT`, `TRAINING` — **değiştirmeyin**
- `exercises/ex02/Expense.java` — record `Expense(String id, String employee, Category category, long amountCents)` —
  **değiştirmeyin**
- `exercises/ex02/Decision.java` — sealed: `Approved(String approver)`, `Rejected(String approver, String reason)` —
  **değiştirmeyin**
- `exercises/ex02/ApprovalResult.java` — record `ApprovalResult(String expenseId, Decision decision, List<String> trail)`
  — **değiştirmeyin**
- `exercises/ex02/Approver.java` — `String name()`, `Optional<Decision> review(Expense)` (boş = ilet) —
  **değiştirmeyin**
- `exercises/ex02/ApprovalChain.java` — `ApprovalResult submit(Expense)` — **değiştirmeyin**
- `exercises/ex02/PolicyCheck.java`, `TeamLead.java`, `Manager.java`, `Director.java`, `ApprovalChains.java` — kodunuzu
  buraya yazın (`TODO(ex02)` işaretleri)

## Görevler

1. `PolicyCheck` (`"policy check"`): pozitif olmayan tutarları (`"amount must be positive"`) ve 100.00'ün üzerindeki
   `MEALS` masraflarını (`"meals above 100.00"`) reddeder; aksi hâlde masrafı iletir.
2. `TeamLead` (`"team lead"`) `EQUIPMENT` dışında 500.00'e kadar onaylar; `Manager` (`"manager"`) 5 000.00'e kadar,
   `Director` (`"director"`) 20 000.00'e kadar onaylar. Tüm sınırlar **dahildir**.
3. `ApprovalChains.of(List<Approver>)`: onaylayıcılara sırayla sorar, ilk kararda durur ve masrafı inceleyen her
   onaylayıcının adını `trail` içine kaydeder. Kimse karar vermezse karar `Rejected("chain", "no approver could decide")`
   olur. `null` bir masraf `NullPointerException` fırlatır.
4. `ApprovalChains.standard()`: policy check → team lead → manager → director.

## Kabul kriterleri

- [ ] `smallTravelExpenseApprovedByTeamLead`
- [ ] `equipmentSkipsTeamLead`
- [ ] `mediumExpenseEscalatesToManager`
- [ ] `largeExpenseEscalatesToDirector`
- [ ] `tooLargeExpenseRejectedAtEndOfChain`
- [ ] `policyCheckRejectsExpensiveMealsBeforeAnyApprover`
- [ ] `policyCheckRejectsNonPositiveAmounts`
- [ ] `boundaryAmountsBelongToTheLowerApprover`
- [ ] `trailListsReviewersInOrder`
- [ ] `chainStopsAtFirstDecision`
- [ ] `customChainUsesOnlyGivenApprovers`
- [ ] `emptyChainRejects`
- [ ] `trailIsImmutable`
- [ ] `rejectsNullExpense`

## Testleri çalıştırın

```bash
./mvnw -pl modules/m07-behavioral-communication test -Pexercises -Dtest='Ex02*'
```

Tüm testler yeşil = ödev bitti. `solutions/ex02/` ile ancak kendiniz denedikten **sonra** karşılaştırın.

## İpuçları

<details><summary>İpucu 1 — kuruş cinsinden tutarlar</summary>

500.00, `500_00` kuruştur; 20 000.00 ise `20_000_00`. `long` kuruş kullanmak, sınırlardaki tüm kayan nokta
sürprizlerini önler.

</details>

<details><summary>İpucu 2 — zincir bir döngüdür</summary>

Onaylayıcıların bir `next` alanına ihtiyacı yoktur: `of(...)` listeyi saklayıp üzerinde dolaşabilir; `review`'u
çağırmadan önce her adı iz listesine (trail) ekler ve bir karar gelir gelmez döner. Dersteki bağlı `SupportHandler` ile
karşılaştırın — ikisi de Chain of Responsibility'dir.

</details>

## Ek hedefler (isteğe bağlı, notlandırılmaz)

- `ApprovalChains.of`'u stream'lerle (`map`, `flatMap(Optional::stream)`, `findFirst`) yazın. İz listesini yan etkisiz
  nasıl oluşturursunuz?
- Aynı kimliğe sahip iki talebi reddeden bir `FraudCheck` ekleyin — hangi duruma ihtiyacı var ve zincirde nereye
  konmalı?
