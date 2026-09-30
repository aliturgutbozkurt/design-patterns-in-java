# Ödev 01 — Belge Onay İş Akışı

> Modül: m08-behavioral-state-structure · Zorluk: ★★☆ · Tahmini süre: 2–3 saat

## Amaç

Bir belge taslaktan incelemeye geçer, onay toplar, değişiklik için geri gönderilebilir, yayımlanır ve istendiği an
arşivlenebilir. Bunu bir **State (Durum)** makinesi olarak kurun. Belgenin ne yapabileceği o anki durumuna bağlıdır.
Bazı durumlar veri taşır (şimdiye kadar onay veren inceleyiciler). Geçersiz bir olay istisna fırlatmaz; bir **gerekçeyle
reddedilir** ve hiçbir şeyi değiştirmez. Böylece geçmiş denetlenebilir kalır.

## Size verilenler

- `exercises/ex01/DocumentStatus.java` — `DRAFT, IN_REVIEW, CHANGES_REQUESTED, APPROVED, PUBLISHED, ARCHIVED` —
  **değiştirmeyin**
- `exercises/ex01/WorkflowEvent.java` — sealed: `Submit(by)`, `Approve(by)`, `RequestChanges(by, comment)`,
  `Revise(by)`, `Publish(by)`, `Archive(by)`. Tüm adlar ve yorum boş olamaz. **Değiştirmeyin**
- `exercises/ex01/Outcome.java` — sealed: `Accepted(DocumentStatus status)`, `Refused(String reason)` —
  **değiştirmeyin**
- `exercises/ex01/HistoryEntry.java` — record `HistoryEntry(DocumentStatus from, WorkflowEvent event,
  DocumentStatus to)` — **değiştirmeyin**
- `exercises/ex01/DocumentWorkflow.java` — `status()`, `Outcome handle(WorkflowEvent)`, `Set<String> approvals()`,
  `List<HistoryEntry> history()` — **değiştirmeyin**
- `exercises/ex01/ReviewWorkflow.java` — kodunuz buraya (`TODO(ex01)` işaretleri), kurucu
  `ReviewWorkflow(String author, int requiredApprovals)`

## Görevler

1. Kurucu: yazar sabittir. Boş bir yazar ya da `requiredApprovals < 1` `IllegalArgumentException` fırlatır. Yeni bir
   belge `DRAFT` durumundadır; onayı yoktur ve geçmişi boştur.
2. Durum geçişleri (diğer her çift reddedilir):

   | Durum | Olay | Sonuç |
   |---|---|---|
   | `DRAFT` | yazarın `Submit`'i | `IN_REVIEW` |
   | `IN_REVIEW` | `Approve` | inceleyiciyi kaydeder; `requiredApprovals` kadar farklı inceleyici onaylayınca `APPROVED`, aksi hâlde `IN_REVIEW` kalır |
   | `IN_REVIEW` | `RequestChanges` | `CHANGES_REQUESTED`; onaylar temizlenir |
   | `CHANGES_REQUESTED` | yazarın `Revise`'ı | `IN_REVIEW` |
   | `APPROVED` | `Publish` | `PUBLISHED` |
   | `ARCHIVED` dışında her durum | `Archive` | `ARCHIVED` |

3. Ret gerekçeleri, birebir:
   - `"only the author can submit"` / `"only the author can revise"`
   - `"author cannot approve own document"`
   - `"already approved by <name>"`
   - `ARCHIVED` durumunda **her** olay için `"document is archived"`
   - diğer durumlarda `"<EventName> not allowed in <STATUS>"`, ör. `"Publish not allowed in IN_REVIEW"` (olayın record
     adı).
4. Reddedilen bir olay hiçbir şeyi değiştirmez: durum, onaylar ve geçmiş olduğu gibi kalır. Kabul edilen her olay bir
   `HistoryEntry` ekler; durumu `IN_REVIEW` bırakan bir onay da buna dahildir.
5. `approvals()` ve `history()` sıralı, salt okunur görünümler döndürür. `null` bir olay `NullPointerException`
   fırlatır.

## Kabul kriterleri

- [ ] `newDocumentIsDraft`
- [ ] `authorSubmitsDraftForReview`
- [ ] `onlyAuthorCanSubmit`
- [ ] `singleApprovalApprovesWhenOneRequired`
- [ ] `staysInReviewUntilEnoughDistinctApprovals`
- [ ] `sameReviewerCannotApproveTwice`
- [ ] `authorCannotApproveOwnDocument`
- [ ] `requestChangesClearsApprovals`
- [ ] `onlyAuthorCanRevise`
- [ ] `approvedDocumentCanBePublished`
- [ ] `publishBeforeApprovalIsRefused`
- [ ] `archiveIsAllowedFromEveryOtherState`
- [ ] `everyEventIsRefusedWhenArchived`
- [ ] `refusedEventChangesNothing`
- [ ] `historyRecordsAcceptedTransitionsInOrder`
- [ ] `historyAndApprovalsAreUnmodifiable`
- [ ] `everyStatusEventPairHasADefinedOutcome`
- [ ] `rejectsInvalidConstructorArgumentsAndNullEvents`

## Testleri çalıştırın

```bash
./mvnw -pl modules/m08-behavioral-state-structure test -Pexercises -Dtest='Ex01*'
```

Tüm testler yeşil = bitti. `solutions/ex01/` ile ancak kendiniz denedikten **sonra** karşılaştırın.

## İpuçları

<details><summary>İpucu 1 — tek switch, default yok</summary>

**Olay** üzerinde record desenleriyle switch yapın ve durumu bir `when` koşuluna koyun:
`case Submit(var by) when status == DRAFT -> …`. Önce `ARCHIVED` durumunu, sonra `Archive` olayını ele alın. En sona
`default` yerine kalan olay tiplerini adıyla sayan bir case koyun (`case Submit _, Approve _, … ->`). Böylece yeni bir
olay tipi sessiz bir ret değil, derleme hatası olur. Nedenini dersin "State — Modern Java 27" bölümü gösteriyor.

</details>

<details><summary>İpucu 2 — reddedilen, dokunulmamış demektir</summary>

Önce karar verin, sonra değiştirin. Onaylara bir şey eklemeden *önce* yazarı ve tekrar eden inceleyiciyi kontrol edin.
Geçmişe yalnızca `Accepted` döndüren kod yolunda ekleyin. Kaydı ekleyen, durumu ayarlayan ve `new Accepted(next)`
döndüren küçük bir `move(event, next)` yardımcısı bu işi tek yerde toplar.

</details>

<details><summary>İpucu 3 — salt okunur görünümler</summary>

`List.copyOf(history)` zaten salt okunurdur. Onaylarda ekleme sırası gerekir: bir `LinkedHashSet` tutun ve
`Collections.unmodifiableSet(new LinkedHashSet<>(approvals))` döndürün.

</details>

## Ek hedefler (isteğe bağlı, notlandırılmaz)

- İç durumu sealed record'larla yeniden yazın (`Draft`, `InReview(Set<String> approvals)`, …). Böylece onaylar yalnızca
  belge incelemedeyken var olabilir. Diğer durumlarda `approvals()` ne döndürmeli?
- Verilen tiplerin bir kopyasına bir `Withdraw(by)` olayı ekleyin (yalnızca yazar, `IN_REVIEW`'dan `DRAFT`'a geri).
  Derleyicinin size dokundurduğu yerleri sayın.
