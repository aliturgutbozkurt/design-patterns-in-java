# Ödev 01 — Command Tabanlı Geri Alma/Yinelemeli Metin Editörü

> Modül: m06-behavioral-algorithms · Zorluk: ★★☆ · Tahmini süre: 3 saat

## Amaç

Her ciddi editörde geri alma (undo) ve yineleme (redo) vardır. Bu ödevde bunları **Command (Komut)** kalıbıyla
yazıyorsunuz. Metindeki her değişiklik, kendini çalıştırabilen ve geri alabilen bir komut nesnesine dönüşür. Editör
(çağırıcı, invoker) bu komutlardan oluşan bir geri alma yığını ve bir yineleme yığını tutar. İki ek özellik işi
gerçekçi kılar: tek adımda geri alınan bir düzenleme **grubu** (bir makro komut, örneğin "tümünü bul ve değiştir")
ve en eski adımları unutan **sınırlı bir geçmiş**.

## Size verilenler

- `exercises/ex01/Edit.java`: `sealed interface Edit permits Insert, Delete, Replace`. **Değiştirmeyin.**
- `exercises/ex01/Insert.java`, `Delete.java`, `Replace.java`: record olarak düzenlemeler. **Değiştirmeyin.**
- `exercises/ex01/Editor.java`: `text`, `apply`, `undo`, `redo`, `canUndo`, `canRedo`, `group`, `undoHistory`.
  **Değiştirmeyin.**
- `exercises/ex01/CommandEditor.java`: kodunuzu buraya yazın (`TODO(ex01)` işaretleri). Komut sınıflarınızı da aynı
  pakete ekleyin.

## Görevler

1. `CommandEditor(String initialText, int maxHistory)`: `null` metin `NullPointerException` fırlatır;
   `maxHistory < 1` ise `IllegalArgumentException` fırlatır.
2. Her düzenleme türü için bir komut sınıfı yazın (`InsertCommand`, `DeleteCommand`, `ReplaceCommand`); her birinde
   `execute` ve `undo` olsun. Bir `Delete` ya da `Replace` sildiği metni hatırlamalıdır; böylece geri alma **tam
   olarak** o metni geri koyar.
3. `apply(edit)`: önce konumu ve uzunluğu mevcut metne göre kontrol edin. `Insert` için
   `0 <= position <= length` geçerlidir; `Delete`/`Replace` için `position >= 0`, `length >= 0` ve
   `position + length <= metin uzunluğu` gerekir. Geçersiz bir düzenleme `IndexOutOfBoundsException` fırlatır ve
   hiçbir şeyi değiştirmez. Aksi hâlde komutu çalıştırın ve geri alma yığınına koyun. Yeni bir adım yineleme yığınını
   temizler.
4. `undo()` / `redo()` iki yığın arasında bir adım taşır; yapılacak bir şey yoksa `false` döndürür.
   `canUndo()` / `canRedo()` bir şey yapıp yapmayacaklarını söyler.
5. `group(edits)`: consumer'ı bu editörle çalıştırın. Uyguladığı her düzenleme **tek** bir geri alma adımı olur (bir
   grup ya da makro komut). Grubun içindeki bir grup, dıştaki gruba katılır. Consumer bir istisna fırlatırsa, o grubun
   yaptığı düzenlemeleri ters sırayla geri alın ve aynı istisnayı yeniden fırlatın.
6. En fazla `maxHistory` geri alma adımı tutun; fazlası olursa en eskisini atın.
7. `undoHistory()` geri alınabilecek düzenlemeleri en yeniden başlayarak listeler (bir grubun düzenlemeleri de
   listelenir).
8. `apply` ve `group`'a verilen `null` argümanlar `NullPointerException` fırlatır.

## Kabul kriterleri

- [ ] `appliesInsertDeleteAndReplace`
- [ ] `undoRestoresPreviousText`
- [ ] `undoRestoresExactDeletedText`
- [ ] `redoReappliesUndoneEdit`
- [ ] `newEditClearsRedo`
- [ ] `undoAndRedoOnEmptyHistoryReturnFalse`
- [ ] `invalidEditThrowsAndLeavesTextUnchanged`
- [ ] `groupUndoesAsOneStep`
- [ ] `nestedGroupsJoinTheOuterGroup`
- [ ] `failedGroupIsRolledBackAndRethrown`
- [ ] `historyIsBoundedToMaxSteps`
- [ ] `undoHistoryListsMostRecentFirst`
- [ ] `rejectsNullArguments`

## Testleri çalıştırın

```bash
./mvnw -pl modules/m06-behavioral-algorithms test -Pexercises -Dtest='Ex01*'
```

Tüm testler yeşil = ödev bitti. `solutions/ex01/` ile ancak kendiniz denedikten **sonra** karşılaştırın.

## İpuçları

<details><summary>İpucu 1 — bir komut neyi hatırlamalı</summary>

Bir `InsertCommand` kendini yalnızca düzenlemeye bakarak geri alabilir: `position` konumunda `text.length()` karakter
siler. Bir `DeleteCommand` bunu yapamaz: `execute`'tan sonra karakterler gitmiştir. Silmeden önce, **`execute`
içinde**, `text.substring(position, position + length)` değerini bir alanda saklayın. `StringBuilder`'da `insert`,
`delete` ve `replace` vardır.

</details>

<details><summary>İpucu 2 — yığınlar ve sınırlı geçmiş</summary>

`ArrayDeque` iyi bir yığındır: `push`, `poll` (boşsa `null` döndürür) ve `size() > maxHistory` olduğunda en eski
kaydı atmak için `removeLast`. `Objects.checkIndex` ve `Objects.checkFromIndexSize` tam da ihtiyacınız olan
`IndexOutOfBoundsException`'ı fırlatır.

</details>

<details><summary>İpucu 3 — gruplar</summary>

Bir grup açıkken komutları yığına koymak yerine bir listede toplayın. En dıştaki grup bittiğinde, listeyi sırayla
çalıştıran ve ters sırayla geri alan tek bir `GroupCommand(list)` koyun. İç içe gruplar ve geri sarma (rollback) için,
bir grup başlarken listenin boyutunu hatırlayın. Consumer bir istisna fırlatırsa, bu işaretten sonraki her şeyi geri
alın ve listeden çıkarın.

</details>

## Ek hedefler (isteğe bağlı, notlandırılmaz)

- Gerçek editörlerin yazmayı birleştirdiği gibi, art arda gelen tek karakterlik eklemeleri tek bir geri alma adımında
  birleştirin.
- Komut sınıflarının yerine, *ters* düzenlemeyi hesaplayan eksiksiz (exhaustive) bir `switch` kullanın (dersteki
  hesap tablosu örneğine bakın). Ne kazanırsınız, ne kaybedersiniz?
