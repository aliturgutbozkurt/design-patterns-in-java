# Capstone — PatternShop · Bitirme Projesi

> Weeks 9–14 · 9.–14. Haftalar — [Spec](../specs/SPEC-capstone.md)

| | English | Türkçe |
|---|---|---|
| Project brief · Proje tanımı | [Markdown](spec.en.md) · [PDF](spec.en.pdf) | [Markdown](spec.tr.md) · [PDF](spec.tr.pdf) |
| Grading rubric · Değerlendirme rubriği | [Markdown](rubric.en.md) · [PDF](rubric.en.pdf) | [Markdown](rubric.tr.md) · [PDF](rubric.tr.pdf) |
| Walkthrough guide · Çözüm rehberi | [Markdown](guide.en.md) · [PDF](guide.en.pdf) | [Markdown](guide.tr.md) · [PDF](guide.tr.pdf) |

## Code · Kod

| Folder · Klasör | What it is · Nedir |
|---|---|
| [`starter/`](starter/) | Your project: GIVEN API (`…capstone.api`, do not modify), skeleton (`…capstone.shop`), 83 acceptance tests in 11 suites, 7 architecture rules, [`SPEC.md`](starter/SPEC.md) and [`REPORT.md`](starter/REPORT.md) templates · Sizin projeniz: VERİLEN API (değiştirmeyin), iskelet, 11 takımda 83 kabul testi, 7 mimari kural, `SPEC.md` ve `REPORT.md` şablonları |
| [`starter/src/test/resources/acceptance/`](starter/src/test/resources/acceptance/) | Expected CLI transcript and report texts · Beklenen CLI dökümü ve rapor metinleri |
| [`reference/`](reference/) | Reference solution (13 counted patterns), explained in the guide — read it, do not copy it (brief §12) · Referans çözüm (13 sayılan kalıp), rehberde açıklanır — okuyun, kopyalamayın (proje tanımı §12) |

## Build & test · Derleme ve test

Run from the repository root on JDK 27 · Depo kökünden JDK 27 ile çalıştırın:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 27)            # macOS; on Linux/Windows point JAVA_HOME at JDK 27

./mvnw -q -pl capstone/starter verify                        # your code + architecture rules · kodunuz + mimari kurallar
./mvnw -q -pl capstone/starter test -Pexercises              # the 83 acceptance tests · 83 kabul testi
./mvnw -q -pl capstone/starter test -Pexercises -Dtest='CartExerciseTest' -Dsurefire.failIfNoSpecifiedTests=false
./mvnw -q -pl capstone/starter verify -Pcoverage             # JaCoCo report · kapsam raporu
java -cp capstone/starter/target/classes io.github.aliturgutbozkurt.patterns.capstone.shop.config.Main --demo

./mvnw -q -pl capstone/reference -am verify                  # reference: 83 acceptance + 7 rules + unit tests
java -cp capstone/starter/target/classes:capstone/reference/target/classes \
     io.github.aliturgutbozkurt.patterns.capstone.reference.config.Main --demo
```
