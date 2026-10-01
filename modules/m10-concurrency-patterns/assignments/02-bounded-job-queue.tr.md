# Ödev 02 — Sınırlı İş Kuyruğu

> Modül: m10-concurrency-patterns · Zorluk: ★★★ · Tahmini süre: 3 saat

## Amaç

Bir arka ofis sistemi işleri (e-posta gönder, fatura oluştur) kuyruğa alır ve sabit sayıda çalışan (worker) iş
parçacığında çalıştırır. Bu kuyruğu kendiniz yazın: engelleme davranışı bir dizi **Guarded Suspension (Korumalı
Bekletme)** ve **Balking (Vazgeçme)** kuralından oluşan bir **Producer–Consumer (Üretici–Tüketici)** iş kuyruğu.
Kuyruk doluyken geri itmeli, kapatılırken kuyruktakileri bitirerek kapanmalı, istenirse hemen durmalı ve bekleyen hiçbir
iş parçacığını asılı bırakmamalıdır.

## Size verilenler

- `exercises/ex02/Job.java` — record `Job(long id, String payload)` — **değiştirmeyin**
- `exercises/ex02/JobHandler.java` — `String handle(Job job) throws Exception` (bloklayabilir, istisna fırlatabilir)
  — **değiştirmeyin**
- `exercises/ex02/JobOutcome.java` — sealed: `Completed(long jobId, String result)`, `Failed(long jobId, String
  error)` — **değiştirmeyin**
- `exercises/ex02/JobQueue.java` — `submit`, `trySubmit`, `shutdown`, `shutdownNow`, `awaitTermination`, `outcomes`,
  `close` (Javadoc'a bakın) — **değiştirmeyin**
- `exercises/ex02/WorkerPoolQueue.java` — kodunuzu buraya yazın (`TODO(ex02)` işaretleri); kurucu
  `WorkerPoolQueue(int capacity, int workers, JobHandler handler, ThreadFactory threadFactory)`

## Görevler

1. `capacity < 1` ya da `workers < 1` değerlerini `IllegalArgumentException` ile reddedin. Enjekte edilen
   `ThreadFactory` ile **tam olarak** `workers` kadar iş parçacığı oluşturun ve başlatın.
2. Kuyrukta en fazla `capacity` iş bekler, aynı anda en fazla `workers` iş çalışır.
3. `submit`, kuyruk doluyken **bekler** (Guarded Suspension). `trySubmit` **vazgeçer**: kuyruk doluysa ya da
   kapatıldıysa hemen `false` döner.
4. İşleyicinin sonucu `Completed(id, result)` olur; işleyicinin istisnası `Failed(id, message)` olur ve çalışan bir
   sonraki işe devam eder.
5. `shutdown()` yeni iş kabul etmeyi bırakır, çalışanların kuyruktaki her işi bitirmesine izin verir ve kuyruk dolu
   olsa bile **beklemeden** döner. `submit` içinde bekleyen bir iş parçacığı `IllegalStateException` ile serbest
   bırakılır; kapatmadan sonra `submit` `IllegalStateException` fırlatır.
6. `shutdownNow()` yeni iş kabul etmeyi bırakır, çalışan işleyicileri keser (interrupt) ve hiç başlamamış kuyruktaki
   işleri **gönderilme sırasıyla** döndürür.
7. `awaitTermination(timeout)`, tüm çalışanlar çıktığında `true`, önce zaman aşımı dolarsa `false` döner.
8. `outcomes()`, **iş kimliğine göre sıralı**, değiştirilemez bir anlık görüntüdür (snapshot). `shutdown()` ve
   `shutdownNow()` idempotenttir (tekrar çağrılabilir). `close()` = `shutdown()` + sonlanmayı bekle.

## Kabul kriterleri

- [ ] `processesEverySubmittedJobExactlyOnce`
- [ ] `outcomesAreSortedByJobId`
- [ ] `failingJobBecomesFailedOutcomeAndWorkerContinues`
- [ ] `createsExactlyTheConfiguredNumberOfWorkers`
- [ ] `runsAtMostWorkersJobsConcurrently`
- [ ] `trySubmitBalksWhenQueueIsFull`
- [ ] `submitBlocksWhileQueueIsFullAndResumesWhenSpaceFrees`
- [ ] `shutdownDrainsQueuedJobsBeforeTermination`
- [ ] `shutdownDoesNotBlockWhenQueueIsFull`
- [ ] `blockedSubmitterIsReleasedByShutdown`
- [ ] `submitAfterShutdownIsRejected`
- [ ] `trySubmitAfterShutdownReturnsFalse`
- [ ] `shutdownNowReturnsJobsThatNeverStartedInSubmissionOrder`
- [ ] `shutdownNowInterruptsRunningJobs`
- [ ] `awaitTerminationReturnsFalseWhileJobsStillRun`
- [ ] `shutdownIsIdempotent`
- [ ] `closeShutsDownAndAwaitsTermination`
- [ ] `rejectsInvalidConstructorArguments`

## Testleri çalıştırın

```bash
./mvnw -pl modules/m10-concurrency-patterns test -Pexercises -Dtest='Ex02*'
```

Tüm testler yeşil = ödev bitti. `solutions/ex02/` ile ancak kendiniz denedikten **sonra** karşılaştırın.

Testler hiç uyumaz (sleep kullanmaz). "Çalışmaya devam etmesi" gereken bir işleyici, testin açtığı bir kapıda bekler;
"gönderen bekliyor" durumu da gönderen iş parçacığının durumu `WAITING` olana dek yoklanarak denetlenir.

## İpuçları

<details><summary>İpucu 1 — bir kilit, üç koşul</summary>

Kuyruğu (bir `ArrayDeque`), bir `shutdown` bayrağını ve yaşayan çalışan sayısını tek bir `ReentrantLock` ile koruyun
ve iş parçacıklarının neyi beklediğini adlandırın: `notFull` (gönderenler), `notEmpty` (çalışanlar) ve `terminated`
(`awaitTermination`). Her koruma koşulu (guard) bir `while` döngüsüdür ve her biri `shutdown` bayrağını da denetler:
`while (kuyruk dolu && !shutdown) notFull.await();`. `shutdown()` bayrağı ayarlar, `notFull` ve `notEmpty` üzerinde
`signalAll()` çağırır; hiçbir şeyi beklemez.

</details>

<details><summary>İpucu 2 — neden ArrayBlockingQueue + zehirli hap değil?</summary>

Akla ilk gelen tasarım, çalışan başına bir zehirli hap (poison pill) içeren bir `ArrayBlockingQueue`, 5. kuralı
karşılayamaz. Hapı dolu bir kuyruğa koymak `shutdown()` çağrısını bekletir; `put` içinde zaten bekleyen bir iş
parçacığı da yer açılana dek bekler ve işini kapatmadan *sonra* kuyruğa sokar. Koşul tabanlı kuyruk ise bu iş
parçacıklarını uyandırıp bayrağı görmelerini sağlayabilir.

</details>

<details><summary>İpucu 3 — bir çalışanın hayatı</summary>

Bir çalışan döngü hâlinde şunu yapar: kilit altında sıradaki işi alır (kuyruk boşken ve kapatılmamışken bekler;
kapatılmış ve boşsa döner), işleyiciyi kilidin **dışında** çalıştırır, sonucu kaydeder. Döngüden çıkarken yaşayan
çalışan sayısını azaltır ve sayı 0'a ulaşınca `terminated` koşuluna sinyal verir. `shutdownNow()` onları kesebilsin
diye çalışan iş parçacıklarını saklayın ve kesmeyi (interrupt) yutmayın.

</details>

## Ek hedefler (isteğe bağlı, notlandırılmaz)

- `int queuedCount()` ve `int runningCount()` ekleyin ve çok sayıda gönderenle bir stres testinde
  `queued ≤ capacity` ve `running ≤ workers` değişmezlerini (invariant) gösterin.
- Kuyruğunuzu `ThreadPoolExecutor(workers, workers, 0, SECONDS, new ArrayBlockingQueue<>(capacity))` ile
  karşılaştırın. Kuyruk doluyken `RejectedExecutionHandler` ne yapar ve yukarıdaki kurallardan hangisini çiğner?
