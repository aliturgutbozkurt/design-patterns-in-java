# Ödev 02 — Küçük İfade Dili

> Modül: m08-behavioral-state-structure · Zorluk: ★★★ · Tahmini süre: 3 saat

## Amaç

Sayılar, boolean'lar, karşılaştırmalar, `and`/`or`/`not` ve `if … then … else …` içeren küçük, tipli bir dil için bir
**Interpreter (Yorumlayıcı)** kalıbının işlemlerini yazın. Sealed soyut sözdizimi ağacı (AST) ve eksiksiz bir
ayrıştırıcı (parser) size verilmiştir. Ağaç üzerinde üç özyinelemeli fonksiyon yazarsınız: çalışma zamanı tip
denetimi ve kısa devre yapan bir değerlendirici, çıktısı aynı ağaca geri ayrıştırılan bir yazıcı ve değişkenleri
listeleyen bir sorgu. Bu, modern "ziyaretçisiz Visitor"dır: her işlem için tek bir eksiksiz (exhaustive) `switch`.

## Size verilenler

- `exercises/ex02/Expr.java` — sealed: `Num(long)`, `Bool(boolean)`, `Var(String)`, `Unary(UnaryOp, Expr)`,
  `Binary(BinaryOp, Expr, Expr)`, `If(Expr condition, Expr then, Expr otherwise)` — **değiştirmeyin**
- `exercises/ex02/UnaryOp.java` — `NEG` (`-`), `NOT` (`not`); her birinde `symbol()` ve `precedence()` —
  **değiştirmeyin**
- `exercises/ex02/BinaryOp.java` — `OR, AND, EQ, LT, LE, ADD, SUB, MUL, DIV`; `symbol()`, `precedence()` ve
  `isComparison()` ile — **değiştirmeyin**
- `exercises/ex02/Value.java` — sealed: `NumValue(long)`, `BoolValue(boolean)`; `EvalException.java` —
  **değiştirmeyin**
- `exercises/ex02/Parser.java` — `Parser.parse(String)`, dilbilgisinin tamamı (Javadoc'una bakın) — **değiştirmeyin**
- `exercises/ex02/Language.java` — `evaluate(Expr, Map<String, Value>)`, `print(Expr)`, `freeVariables(Expr)` —
  **değiştirmeyin**
- `exercises/ex02/MiniLanguage.java` — kodunuz buraya (`TODO(ex02)` işaretleri)

Operatör önceliği, en gevşekten başlayarak: `if` < `or` < `and` < `not` < karşılaştırmalar (`=`, `<`, `<=`) < `+ -` <
`* /` < tekli `-`. İkili operatörler soldan birleşir. Karşılaştırmalar zincirlenmez (`a < b < c` ayrıştırılamaz).

## Görevler

1. **Değerlendirme.**
   - Aritmetik `Math.addExact`/`subtractExact`/`multiplyExact` kullanır, `-x` için de `negateExact`. Taşma
     `ArithmeticException` fırlatır. `DIV` sıfıra doğru keser; sıfıra bölme `EvalException("division by zero")`
     fırlatır.
   - Yanlış operand tipi `EvalException("type error: <OP> expects <NUM|BOOL> but got <NUM|BOOL>")` fırlatır. `<OP>`
     enum adıdır (`ADD`, `NOT`, …); boolean olmayan bir koşulda `IF` yazılır. `EQ` iki sayı ya da iki boolean alır;
     karışık tiplerde beklenen tip sol operandın tipidir.
   - `and`/`or` kısa devre yapar ve `if` yalnızca seçilen dalı değerlendirir: `false and 1 / 0 = 1` sonucu `false`
     olur.
   - Bilinmeyen bir değişken `EvalException("unknown variable: <name>")` fırlatır.
2. **Yazdırma.** İkili operatörlerin iki yanında tek boşluk kullanın; `-x`, `not x` ve `if c then a else b` biçimlerini
   kullanın. Parantezi **yalnızca** öncelik ya da birleşme yönü gerektirdiğinde ekleyin. `Parser.parse(print(e))`,
   `e`'ye eşit olmalıdır.
3. **Serbest değişkenler.** Ağaçtaki her değişken adını döndürün (dilde bağlayıcı yoktur).
4. `null` argümanlar `NullPointerException` fırlatır.

## Kabul kriterleri

- [ ] `evaluatesIntegerArithmetic`
- [ ] `respectsPrecedenceAndLeftAssociativity`
- [ ] `integerDivisionTruncatesTowardZero`
- [ ] `divisionByZeroIsAnEvalError`
- [ ] `overflowThrowsArithmeticException`
- [ ] `comparesNumbersAndBooleans`
- [ ] `mixedTypeEqualityIsATypeError`
- [ ] `arithmeticOnBooleansIsATypeError`
- [ ] `ifRequiresABooleanCondition`
- [ ] `andOrShortCircuit`
- [ ] `ifEvaluatesOnlyTheChosenBranch`
- [ ] `looksUpVariablesInTheEnvironment`
- [ ] `unknownVariableIsAnEvalError`
- [ ] `printsWithMinimalParentheses`
- [ ] `printsNestedIfAndUnaryOperators`
- [ ] `printedTextParsesBackToTheSameTree` (23 ağaç)
- [ ] `collectsFreeVariables`
- [ ] `rejectsNullArguments`

## Testleri çalıştırın

```bash
./mvnw -pl modules/m08-behavioral-state-structure test -Pexercises -Dtest='Ex02*'
```

Tüm testler yeşil = bitti. `solutions/ex02/` ile ancak kendiniz denedikten **sonra** karşılaştırın.

## İpuçları

<details><summary>İpucu 1 — tip denetimini iki küçük yardımcı taşır</summary>

`long number(String op, Value v)` ve `boolean bool(String op, Value v)` sealed `Value` üzerinde switch yapar. İkisi de
ya içerdiği değeri döndürür ya da tip hatasını fırlatır. Böylece her operatör tek satır olur. `bool(...) && bool(...)`
de kendiliğinden kısa devre yapar, çünkü Java'nın `&&` operatörü öyle çalışır.

</details>

<details><summary>İpucu 2 — bir çift üzerinde eşitlik</summary>

İki değeri özel bir `record Operands(Value left, Value right)` içine koyup onun üzerinde switch yapın:
`case Operands(NumValue(var a), NumValue(var b)) -> a == b`. İki karışık durum tek bir etiketi paylaşabilir:
`case Operands(NumValue _, BoolValue _), Operands(BoolValue _, NumValue _) -> …`. `default` gerekmez.

</details>

<details><summary>İpucu 3 — yazıcı kuralı</summary>

Her düğüme bir öncelik verin (`if` = 0, operatörler kendi önceliğini alır, sabitler ve değişkenler en yükseği). Bir
çocuğun önceliği ebeveyninkinden **düşükse** onu paranteze alın. İkili bir operatörün sağ operandını öncelik
**eşit** olduğunda da paranteze alın (soldan birleşme). Bir karşılaştırmanın sol operandını da eşitlikte paranteze
alın (karşılaştırmalar zincirlenmez). Operand olarak gelen `if` her zaman paranteze alınır.
`examples/interpreter/calc` içindeki hesap makinesinin `Printer`'ı aynı kuralı kullanır.

</details>

## Ek hedefler (isteğe bağlı, notlandırılmaz)

- `Expr`'e dokunmadan dördüncü bir fonksiyon olarak bir `Simplifier` ekleyin (`x + 0`, `x * 1`, `not not b`, sabit
  katlama).
- `1 + true` ifadesini değerlendirmeden *önce* reddeden statik bir tip denetleyicisi `typeOf(Expr, Map<String, Type>)`
  ekleyin. Hangi çalışma zamanı denetimleri artık hiç başarısız olamaz?
