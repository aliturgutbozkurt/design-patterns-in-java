# Ödev 01 — Static Factory'lerle Renk Değerleri

> Modül: m02-creational-factories · Zorluk: ★★☆ · Tahmini süre: 1,5 saat

## Amaç

Çağıranların yalnızca **statik fabrika metotlarıyla** — `rgb(…)`, `hex(…)` ve `named(…)` — oluşturabileceği,
değişmez bir renk sınıfı yazın. Bu sırada bir fabrikanın yapabildiği ama bir kurucunun yapamadığı üç şeyi
çalışacaksınız: anlamlı bir ada sahip olmak, **önbellekteki** bir nesneyi döndürmek ve neyi döndüreceğine karar
vermeden önce girdiyi ayrıştırıp doğrulamak.

## Size verilenler

- `exercises/ex01/Color.java` — uygulanacak arayüz — **değiştirmeyin**
- `exercises/ex01/RgbColor.java` — kodunuzu buraya yazın (`TODO(ex01)` işaretleri)

## Görevler

1. `RgbColor`'un kurucusunu **private** tutun; üç bileşeni saklayın.
2. `rgb(red, green, blue)`: her bileşen 0–255 arasında olmalı, değilse `IllegalArgumentException`.
3. `hex(text)`: `#RRGGBB` biçimini ve kısa `#RGB` biçimini (`#F80` = `#FF8800`) büyük ya da küçük harfle kabul edin.
   Başka her şey `IllegalArgumentException` fırlatır.
4. `named(name)`: 8 temel renk — `black`, `white`, `red`, `green`, `blue`, `yellow`, `cyan`, `magenta` —
   büyük/küçük harf duyarsız. Her birini **bir kez** oluşturun ve hep o nesneyi döndürün. Bilinmeyen bir ad,
   mesajında bilinen adları listeleyen bir `IllegalArgumentException` fırlatır.
5. Değer adlandırılmış renklerden biriyse `rgb(…)` (dolayısıyla `hex(…)`) önbellekteki nesneyi döndürür.
6. `toHex()` büyük harfli `#RRGGBB` döndürür; `equals`/`hashCode` bileşenleri karşılaştırır.

## Kabul kriterleri

- [ ] `rgbRejectsComponentsOutsideZeroTo255`
- [ ] `hexParsesLongForm` — `#FF8800` → 255, 136, 0
- [ ] `hexParsesShortForm` — `#F80` → 255, 136, 0
- [ ] `hexIsCaseInsensitive`
- [ ] `hexRejectsMalformedInput` — `FF8800`, `#GG0000`, `#12345`, boş dizge
- [ ] `namedColoursAreCached` — `named("red")`, `named("RED")` ile *aynı nesnedir*
- [ ] `rgbReturnsCachedInstanceForNamedColour` — `rgb(255, 0, 0)`, `named("red")` ile aynı nesnedir
- [ ] `unknownNameListsKnownNames`
- [ ] `toHexIsUppercaseSixDigits` — `rgb(10, 11, 12)` → `#0A0B0C`
- [ ] `equalByValue`
- [ ] `hasNoPublicConstructor`

## Testleri çalıştırın

```bash
./mvnw -pl modules/m02-creational-factories test -Pexercises -Dtest='Ex01*'
```

Tüm testler yeşil = ödev bitti. `solutions/ex01/` ile ancak kendiniz denedikten **sonra** karşılaştırın.

## İpuçları

<details><summary>İpucu 1 — önbelleği kurmak</summary>

Adlandırılmış renkleri bir `static` başlatıcıda oluşturun ve addan renge bir haritada tutun. Paketlenmiş değerden
(`(red << 16) | (green << 8) | blue`) renge ikinci bir harita, `rgb(…)`'nin önbellekteki nesneyi hızla bulmasını
sağlar.

</details>

<details><summary>İpucu 2 — hex ayrıştırma</summary>

`#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})` gibi bir düzenli ifade girdiyi doğrular; `Integer.parseInt(digits, 16)` onu
sayıya çevirir. `"%02X".formatted(value)` iki büyük harfli onaltılık basamak yazar.

</details>

## Ek hedefler (isteğe bağlı, notlandırılmaz)

- Bir `record` neden `hasNoPublicConstructor` koşulunu sağlayamaz? Yine de record kullansaydınız ne kaybeder, ne
  kazanırdınız?
- Hem bir adı hem bir hex dizgesini kabul eden `RgbColor.parse(String)` ekleyin. Tek bir "akıllı" fabrika, iki açık
  fabrikadan daha mı iyidir?
