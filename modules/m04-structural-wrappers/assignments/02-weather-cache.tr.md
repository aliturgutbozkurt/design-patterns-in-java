# Ödev 02 — Yavaş Bir Hava Durumu Servisi için TTL'li Önbellek Vekili

> Modül: m04-structural-wrappers · Zorluk: ★★★ · Tahmini süre: 3 saat

## Amaç

Bir hava durumu servisi yavaştır ve istek başına ücret alır; oysa tahminler ancak yarım saatte bir değişir. Önüne
bir **caching proxy (önbellek vekili)** koyun: istemciler `forecast(city)`'yi eskisi gibi çağırmaya devam eder, vekil
ise kayıt tazeyken cevabı önbellekten verir. Dersteki döviz kuru örneğinin ötesine geçin: büyük/küçük harf duyarsız
anahtarlar, önbelleğe *alınmayan* hatalar, geçersiz kılma, isabet/ıska istatistikleri ve en uzun süredir
kullanılmayan kaydı çıkaran bir boyut sınırı.

## Size verilenler

- `exercises/ex02/Forecast.java` — record `(city, temperatureCelsius, summary)` — **değiştirmeyin**
- `exercises/ex02/WeatherService.java` — `Forecast forecast(String city)` — **değiştirmeyin**
- `exercises/ex02/CachingWeatherService.java` — `WeatherService`'i `invalidate(city)` ve `stats()` ile genişletir —
  **değiştirmeyin**
- `exercises/ex02/CacheStats.java` — record `(hits, misses)` — **değiştirmeyin**
- `exercises/ex02/TtlCachingWeatherService.java` — kodunuzu buraya yazın (`TODO(ex02)` işaretleri)

## Görevler

1. Kurucu `TtlCachingWeatherService(WeatherService target, Duration ttl, int maxEntries, Clock clock)`: `ttl` pozitif,
   `maxEntries` ≥ 1 olmalıdır (aksi halde `IllegalArgumentException`); null iş birlikçiler `NullPointerException`
   fırlatır. Zamanı **yalnızca** `clock`'tan okuyun.
2. Önbellek anahtarı: şehir adının baştaki/sondaki boşlukları atılmış ve `Locale.ROOT` ile küçük harfe çevrilmiş hâli.
   Boş (blank) bir şehir, hedef çağrılmadan `IllegalArgumentException` fırlatır.
3. Bir kayıt, `clock.instant()` `storedAt + ttl`'den **önce** olduğu sürece tazedir; tam `storedAt + ttl` anında
   bayattır ve yeniden getirilir.
4. *İsabet (hit)*, önbellekten cevaplanan çağrıdır; *ıska (miss)*, başarılı da olsa başarısız da olsa hedefe
   devredilen çağrıdır. Hedefin istisnaları değişmeden yayılır ve önbelleğe **alınmaz**.
5. `invalidate(city)` tek bir şehri siler (bilinmeyen şehir: hiçbir şey yapmaz).
6. Yeni bir kayıt `maxEntries`'i aşacaksa **en uzun süredir kullanılmayan (LRU)** kaydı çıkarın (isabet de saklama da
   kullanım sayılır).

## Kabul kriterleri

- [ ] `firstCallGoesToTheService`
- [ ] `repeatWithinTtlIsServedFromCache`
- [ ] `entryExpiresExactlyAtTtl`
- [ ] `cityKeysIgnoreCaseAndSurroundingSpaces`
- [ ] `differentCitiesAreCachedSeparately`
- [ ] `failuresAreNotCached`
- [ ] `invalidateForcesARefresh`
- [ ] `statsCountHitsAndMisses`
- [ ] `leastRecentlyUsedEntryIsEvictedWhenFull`
- [ ] `rejectsInvalidConfiguration`
- [ ] `rejectsBlankCityWithoutCallingTheService`
- [ ] `usableWhereAWeatherServiceIsExpected`

## Testleri çalıştırın

```bash
./mvnw -pl modules/m04-structural-wrappers test -Pexercises -Dtest='Ex02*'
```

Tüm testler yeşil = ödev bitti. `solutions/ex02/` ile ancak kendiniz denedikten **sonra** karşılaştırın.

## İpuçları

<details><summary>İpucu 1 — tek satırda bir LRU haritası</summary>

`new LinkedHashMap<>(16, 0.75f, true)` kayıtlarını **erişim sırasında** tutar: `get` ve `put` bir kaydı sona taşır,
bu yüzden ilk kayıt her zaman en uzun süredir kullanılmayandır. `removeEldestEntry`'yi `size() > maxEntries`
döndürecek şekilde ezin; harita kendiliğinden çıkarır. (Anonim sınıf yerine küçük, statik iç içe bir alt sınıf
kullanın.)

</details>

<details><summary>İpucu 2 — ne saklanmalı</summary>

Tahmini **ve** saklandığı anı saklayın, ör. özel bir `record Entry(Forecast forecast, Instant storedAt)`. Taze demek
`now.isBefore(entry.storedAt().plus(ttl))` demektir. Önce hedefi çağırın, yalnızca bir değer döndüyse saklayın —
böylece bir hata asla önbelleğe alınmaz.

</details>

<details><summary>İpucu 3 — Türkçe I tuzağı</summary>

Neden `Locale.ROOT`? Varsayılan yerel ayar Türkçe olduğunda `"ISTANBUL".toLowerCase()` `"ıstanbul"` (noktasız ı)
verir; `"İSTANBUL".toLowerCase(Locale.ROOT)` ise `"istanbul"`'a eşit olmayan 9 karakter (`i` + birleşen bir nokta)
verir. `Locale.ROOT` anahtarı her makinede aynı yapar; kural net olsun diye testler ASCII şehir adları kullanır.

</details>

## Ek hedefler (isteğe bağlı, notlandırılmaz)

- Önbelleği, yavaş uzak çağrı sırasında kilit tutmadan eşzamanlı çağıranlar (sanal iş parçacıkları!) için güvenli
  yapın. İki iş parçacığının aynı şehri aynı anda getirmesini nasıl engellersiniz? (Eşzamanlılık m10'un konusudur.)
- Hedef başarısız olduğunda *bayat* bir kaydı sunun ("stale-if-error") ve istatistiklerde ayrıca sayın.
