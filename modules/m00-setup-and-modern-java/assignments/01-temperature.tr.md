# Ödev 01 — Sıcaklık Değer Nesnesi

> Modül: m00-setup-and-modern-java · Zorluk: ★☆☆ · Tahmini süre: 1 saat

## Amaç

Java `record`'u olarak küçük bir **değer nesnesi** (value object) yazın: oluşturulurken kendini doğrulayan, birimler
arasında dönüşüm yapan ve yalnızca değeri ve birimi eşit olduğunda başka bir sıcaklığa eşit sayılan bir sıcaklık.
Record'ları, kompakt kurucuları ve switch ifadelerini pekiştirirsiniz.

## Size verilenler

- `exercises/ex01/Unit.java` — sembolleriyle birlikte `CELSIUS`, `FAHRENHEIT`, `KELVIN` — **değiştirmeyin**
- `exercises/ex01/Temperature.java` — record'unuzun uygulayacağı arayüz — **değiştirmeyin**
- `exercises/ex01/TemperatureReading.java` — kodunuzu buraya yazın (`TODO(ex01)` işaretleri)

(Yollar `modules/m00-setup-and-modern-java/src/main/java/io/github/aliturgutbozkurt/patterns/m00/` klasörüne görelidir.)

## Görevler

1. `TemperatureReading`'in kompakt kurucusunda geçersiz durumları reddedin:
   `null` birim (`NullPointerException`), `NaN` ve mutlak sıfırdan soğuk her değer (`IllegalArgumentException`).
   Mutlak sıfır 0 K = −273,15 °C = −459,67 °F'dir.
2. `to(Unit target)` metodunu yazın. Dönüşümü Kelvin üzerinden yapın:
   - K = °C + 273,15
   - K = (°F + 459,67) × 5 ⁄ 9
3. Bir sıcaklığı zaten sahip olduğu birime dönüştürmenin **eşit** bir değer döndürdüğünden emin olun.

## Kabul kriterleri

- [ ] `isARecord` — `TemperatureReading` bir record'dur
- [ ] `convertsCelsiusToFahrenheit` — 100 °C = 212 °F ve −40 °C = −40 °F
- [ ] `convertsFahrenheitToKelvin` — 32 °F = 273,15 K
- [ ] `convertingToSameUnitReturnsEqualValue`
- [ ] `rejectsTemperaturesBelowAbsoluteZero` — ama −273,15 °C'nin kendisine izin verilir
- [ ] `rejectsNaN`
- [ ] `rejectsNullUnit`
- [ ] `equalWhenValueAndUnitEqual` — ve 20 °C, 68 °F'ye **eşit değildir**

## Testleri çalıştırın

```bash
./mvnw -pl modules/m00-setup-and-modern-java test -Pexercises -Dtest='Ex01*'
```

Tüm testler yeşil = ödev bitti. `solutions/ex01/` ile ancak kendiniz denedikten **sonra** karşılaştırın.

## İpuçları

<details><summary>İpucu 1 — doğrulama nereye yazılır?</summary>

Kompakt kurucunun parametre listesi yoktur: `public TemperatureReading { … }`. `value` ve `unit` bileşenleri
kapsamdadır; alanlar, sizin kodunuz çalıştıktan sonra otomatik olarak atanır.

</details>

<details><summary>İpucu 2 — aynı birim</summary>

Kayan noktalı sayılarda `21.5 + 273.15 - 273.15` tam olarak `21.5` etmez. `target == unit` ise onun yerine ne
döndürebilirsiniz?

</details>

## İleri hedefler (isteğe bağlı, notlanmaz)

- `"21.5 °C"` biçimini kabul eden bir `static TemperatureReading parse(String text)` ekleyin.
- 20 °C ile 68 °F aynı sıcaklık olduğu hâlde neden `equals` değildir? Bunun bir değer nesnesi için ne zaman doğru,
  ne zaman yanlış davranış olduğunu yazın.
