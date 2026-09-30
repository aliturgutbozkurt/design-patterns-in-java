# Ödev 02 — Prototiplerle Şekil Editörü

> Modül: m03-creational-construction · Zorluk: ★★☆ · Tahmini süre: 2 saat

## Amaç

Bir çizim editörü, kullanıcıların bir şekli — ya da bütün bir şekil grubunu — **şablon** olarak kaydetmesine ve
ondan kopyalar çıkarmasına izin verir. Şekiller değiştirilebilirdir (taşınabilirler), bu yüzden her kopya **derin**
olmalıdır: bir kopyayı taşımak, gruplar başka grupları içerse bile şablonu asla taşımamalıdır. Şekilleri, `copy()`
metotlarını ve bir prototip kaydını (registry) yazın.

## Size verilenler

- `exercises/ex02/Point.java` — `moved(dx, dy)` metodu olan değişmez record — **değiştirmeyin**
- `exercises/ex02/Shape.java` — `position()`, `moveBy(dx, dy)`, `copy()`, `describe()` — **değiştirmeyin**
- `exercises/ex02/ShapeRegistry.java` — `register`, `create`, `names` — **değiştirmeyin**
- `exercises/ex02/Circle.java`, `Rect.java`, `Group.java`, `TemplateRegistry.java` — kodunuzu buraya yazın
  (`TODO(ex02)` işaretleri)

## Görevler

1. `Circle(Point centre, int radius)` ve `Rect(Point corner, int width, int height)`: değiştirilebilir konum, pozitif
   boyutlar. `describe()` `circle r=5 at (1,2)` ve `rect 3x4 at (0,0)` verir.
2. `Group(List<Shape>)`: en az bir çocuk; `position()` ilk çocuğunkidir; `moveBy` her çocuğu taşır; `describe()`
   `group[<çocuk>, <çocuk>]` verir; `copy()` **her çocuğu** kopyalar.
3. `TemplateRegistry`: `register` şablonun bir **kopyasını** saklar, `create` her seferinde **yeni bir kopya**
   döndürür, bilinmeyen bir ad `IllegalArgumentException` fırlatır, `names()` alfabetiktir.

## Kabul kriterleri

- [ ] `copyDescribesTheSameShape`
- [ ] `copyIsANewObject`
- [ ] `movingACopyLeavesTheOriginal`
- [ ] `groupCopyIsDeep`
- [ ] `nestedGroupsAreCopiedDeeply`
- [ ] `registryReturnsFreshCopies`
- [ ] `registeredTemplateIsNotAffectedByChangesToCreatedShapes`
- [ ] `registeringCopiesTheTemplate`
- [ ] `unknownTemplateRejected`
- [ ] `namesAreSorted`

## Testleri çalıştırın

```bash
./mvnw -pl modules/m03-creational-construction test -Pexercises -Dtest='Ex02*'
```

Tüm testler yeşil = ödev bitti. `solutions/ex02/` ile ancak kendiniz denedikten **sonra** karşılaştırın.

## İpuçları

<details><summary>İpucu 1 — neyi kopyalamalı</summary>

`Point` değişmezdir, bu yüzden bir kopya onu paylaşabilir; ayrı olması gereken, onu tutan *alandır*. Değiştirilebilir
şekillerden oluşan bir `List<Shape>` ise farklıdır: listeyi **ve** içindeki her şekli kopyalayın.

</details>

<details><summary>İpucu 2 — bir grubun derin kopyası</summary>

`children.stream().map(Shape::copy).toList()` — ve `Group.copy()` çocuklarında `copy()` çağırdığı için iç içe gruplar
kendiliğinden derin kopyalanır.

</details>

## Ek hedefler (isteğe bağlı, notlandırılmaz)

- `Circle`'ı kopya kurucusu yerine `Cloneable`/`clone()` ile yazın. Bir alan `final` ise ya da değiştirilebilir bir
  nesneye işaret ediyorsa ne ters gider?
- `Circle` ve `Rect`'i, `moveBy`'ı yeni bir şekil döndüren değişmez record'lar yapın. Yine de `copy()`'ye ihtiyacınız
  olur muydu?
