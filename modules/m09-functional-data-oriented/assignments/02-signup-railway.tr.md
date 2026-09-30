# Ödev 02 — Sealed `Result` ile Demiryolu Tarzı Kayıt Doğrulama

> Modül: m09-functional-data-oriented · Zorluk: ★★★ · Tahmini süre: 3 saat

## Amaç

Bir PatternShop kayıt formunu tek bir istisna (exception) fırlatmadan doğrulayın. Her alan ayrıştırıcısı bir
`Result<X, SignupError>` döndürür. **1. aşama** tüm ayrıştırıcıları çalıştırır ve alan hatalarının **hepsini toplar**
(collect-all); müşteri hepsini aynı anda görür. **2. aşama** yalnızca 1. aşama başarılıysa çalışır: dış
`UserRegistry` servisine **ilk hatada duran** (fail-fast) bir `flatMap` zinciriyle ("demiryolu", railway) sorar;
böylece servis hiçbir zaman geçersiz girdiyle çağrılmaz.

## Size verilenler

- `exercises/ex02/Result.java` — `map`, `mapError`, `flatMap`, `fold`, `orElse`, `orElseGet`, `toOptional`, `attempt`
  metotlarıyla `sealed interface Result<T, E> permits Ok, Err`. `examples.result.core.Result`'ın aynı API'ye sahip bir
  kopyasıdır; böylece ödev yalnızca kendi paketine bağımlıdır — **değiştirmeyin**
- `exercises/ex02/RawSignup.java` — record `RawSignup(username, email, age, country, referralCode)`, hepsi yazıldığı
  gibi metin; herhangi biri `null` olabilir — **değiştirmeyin**
- `exercises/ex02/Country.java` — `TR`, `DE`, `NL`, `US` — **değiştirmeyin**
- `exercises/ex02/Referral.java` — sealed: `NoReferral()`, `ReferredBy(String code)` — **değiştirmeyin**
- `exercises/ex02/Signup.java` — record `Signup(String username, String email, int age, Country country, Referral
  referral)` — **değiştirmeyin**
- `exercises/ex02/SignupError.java` — sealed: `Missing(field)`, `TooShort(field, minLength)`, `InvalidFormat(field)`,
  `OutOfRange(field, min, max)`, `UnknownCountry(value)`, `UsernameTaken(username)`, `InvalidReferral(code)` —
  **değiştirmeyin**
- `exercises/ex02/UserRegistry.java` — `boolean isTaken(String username)`, `boolean isValidReferral(String code)` —
  **değiştirmeyin**
- `exercises/ex02/SignupPipeline.java` — `Result<Signup, List<SignupError>> validate(RawSignup raw)` —
  **değiştirmeyin**
- `exercises/ex02/DefaultSignupPipeline.java` — kodunuz buraya (`TODO(ex02)` işaretleri); dilediğiniz private alan
  ayrıştırıcılarını ekleyin

## Görevler

1. **1. aşama — hepsini topla**, alan sırası username → email → age → country → referral. Her alan önce kırpılır
   (trim). `null` ya da boş username, email, age veya country → `Missing(field)`.
   - username: `Locale.ROOT` ile küçük harfe çevrilir; 3'ten kısa → `TooShort("username", 3)`; 20'den uzun ya da
     `[a-z0-9_]` dışında bir karakter → `InvalidFormat("username")`.
   - email: `yerel@alan.uzantı` biçiminde olmalı (boşluk yok, tek bir `@`, ondan sonra bir nokta), aksi halde
     `InvalidFormat("email")`; `Locale.ROOT` ile küçük harfe çevrilir.
   - age: 10 tabanında bir tamsayı (değilse `InvalidFormat("age")`), 13..120 dahil aralığında (değilse
     `OutOfRange("age", 13, 120)`).
   - country: büyük/küçük harf fark etmeksizin bir `Country` adı; değilse `UnknownCountry(<kırpılmış değer>)`.
   - referral: `null`/boş → `NoReferral`, aksi halde `ReferredBy(<Locale.ROOT ile büyük harfe çevrilmiş kod>)`.

   Herhangi bir alan hatalıysa 1. aşamanın **tüm** hatalarını alan sırasıyla içeren bir `Err` döndürün — ve kayıt
   servisini **çağırmayın**.
2. **2. aşama — ilk hatada dur**, yalnızca 1. aşama başarılıysa: `isTaken(username)` → `Err([UsernameTaken(username)])`
   ve referans kodu artık kontrol edilmez; aksi halde, yalnızca `ReferredBy` için, `isValidReferral(code)` false →
   `Err([InvalidReferral(code)])`. Başarı → `Ok(Signup)`.
3. Her hata listesi değiştirilemezdir. Hattın kendisi hatalı girdide asla istisna fırlatmaz; `null` bir `RawSignup` →
   `NullPointerException`.

## Kabul kriterleri

- [ ] `validSignupProducesNormalisedValue`
- [ ] `missingFieldsAreReported`
- [ ] `allFieldErrorsAreCollectedInFieldOrder`
- [ ] `usernameTooShortAndInvalidCharacters`
- [ ] `emailIsTrimmedAndLowerCased`
- [ ] `ageMustBeANumberInRange`
- [ ] `ageBoundariesAreInclusive`
- [ ] `countryIsCaseInsensitive`
- [ ] `unknownCountryIsReported`
- [ ] `blankReferralMeansNoReferral`
- [ ] `registryNotCalledWhenFieldsInvalid`
- [ ] `takenUsernameFailsFast`
- [ ] `invalidReferralIsReported`
- [ ] `errorListIsUnmodifiable`
- [ ] `neverThrowsForBadInput`
- [ ] `rejectsNullInput`

## Testleri çalıştırın

```bash
./mvnw -pl modules/m09-functional-data-oriented test -Pexercises -Dtest='Ex02*'
```

Tüm testler yeşil = bitti. `solutions/ex02/` ile ancak kendiniz denedikten **sonra** karşılaştırın.

## İpuçları

<details><summary>İpucu 1 — her alan için bir ayrıştırıcı</summary>

`private static Result<String, SignupError> username(String text)`, `email(...)`, `Result<Integer, …> age(...)` gibi
metotlar yazın. `Result.attempt(() -> Integer.parseInt(text, 10), _ -> new InvalidFormat("age"))`,
`NumberFormatException`'ı bir `Err`'e çevirir; ardından gelen bir `flatMap` aralığı kontrol eder.

</details>

<details><summary>İpucu 2 — hataları toplamak</summary>

`fold`, her alan sonucunu sıfır ya da bir hatalık bir akışa çevirir:
`r.fold(_ -> Stream.<SignupError>empty(), Stream::of)`. `Stream.of(u, e, a, c, r).flatMap(...).toList()` tüm hataları
alan sırasıyla ve zaten değiştirilemez olarak verir.

</details>

<details><summary>İpucu 3 — beş Ok'u birleştirmek, sonra demiryolu</summary>

Hata listesi boşsa her sonuç bir `Ok`'tur; iç içe `flatMap`'ler
(`u.flatMap(x -> e.flatMap(y -> …map(… -> new Signup(…))))`) `Signup`'ı kurar. `mapError(List::of)` `SignupError`'ı
`List<SignupError>`'a yükseltir; iki `flatMap` daha kayıt servisine sorar: ilki `Err` döndürdüyse ikincisi hiç
çalışmaz.

</details>

## Ek hedefler (isteğe bağlı, notlandırılmaz)

- 1. aşamayı, iki sonucu birleştirip hatalarını art arda ekleyen küçük bir `Validated` yardımcısına genelleştirin.
  `Result.flatMap` tek başına bunu neden yapamaz?
- Üç bağımsız kuralı olan bir `password` alanı ekleyin ve yalnızca ilkini değil, bozulan her kuralı raporlayın.
