# Assignment 02 — Bounded Job Queue

> Module: m10-concurrency-patterns · Difficulty: ★★★ · Estimated time: 3 h

## Goal

A back-office system queues jobs (send an e-mail, render an invoice) and runs them on a fixed number of worker
threads. Build that queue yourself: a **Producer–Consumer** work queue whose blocking behaviour is a set of
**Guarded Suspension** and **Balking** rules. It must push back when it is full, shut down by draining what is queued,
stop immediately when asked to, and never leave a waiting thread hanging.

## What you are given

- `exercises/ex02/Job.java` — record `Job(long id, String payload)` — **do not modify**
- `exercises/ex02/JobHandler.java` — `String handle(Job job) throws Exception` (may block, may throw) — **do not
  modify**
- `exercises/ex02/JobOutcome.java` — sealed: `Completed(long jobId, String result)`, `Failed(long jobId, String
  error)` — **do not modify**
- `exercises/ex02/JobQueue.java` — `submit`, `trySubmit`, `shutdown`, `shutdownNow`, `awaitTermination`, `outcomes`,
  `close` (see the Javadoc) — **do not modify**
- `exercises/ex02/WorkerPoolQueue.java` — your code goes here (`TODO(ex02)` markers); constructor
  `WorkerPoolQueue(int capacity, int workers, JobHandler handler, ThreadFactory threadFactory)`

## Tasks

1. Reject `capacity < 1` or `workers < 1` with `IllegalArgumentException`. Create **exactly** `workers` threads,
   all from the injected `ThreadFactory`, and start them.
2. At most `capacity` jobs wait in the queue, and at most `workers` jobs run at once.
3. `submit` **blocks** while the queue is full (Guarded Suspension). `trySubmit` **balks**: it returns `false` at
   once when the queue is full or shut down.
4. A handler result becomes `Completed(id, result)`; a handler exception becomes `Failed(id, message)`, and the worker
   carries on with the next job.
5. `shutdown()` stops accepting jobs, lets the workers finish every job that is already queued, and returns
   **without blocking**, even when the queue is full. A thread blocked in `submit` is released with
   `IllegalStateException`; `submit` after shutdown throws `IllegalStateException`.
6. `shutdownNow()` stops accepting jobs, interrupts the running handlers and returns the queued jobs that never
   started, **in submission order**.
7. `awaitTermination(timeout)` returns `true` once every worker has exited, `false` if the timeout elapses first.
8. `outcomes()` is an immutable snapshot **sorted by job id**. `shutdown()` and `shutdownNow()` are idempotent.
   `close()` = `shutdown()` + wait for termination.

## Acceptance criteria

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

## Run the tests

```bash
./mvnw -pl modules/m10-concurrency-patterns test -Pexercises -Dtest='Ex02*'
```

All tests green = done. Compare with `solutions/ex02/` only **after** you have tried.

The tests never sleep. A handler that has to "keep running" waits at a gate that the test opens, and "the submitter
is blocked" is checked by polling the submitter thread's state until it is `WAITING`.

## Hints

<details><summary>Hint 1 — one lock, three conditions</summary>

Guard the queue (an `ArrayDeque`), a `shutdown` flag and a count of live workers with one `ReentrantLock`, and name
what threads wait for: `notFull` (submitters), `notEmpty` (workers) and `terminated` (`awaitTermination`). Every
guard is a `while` loop, and every guard also checks `shutdown`: `while (queue is full && !shutdown) notFull.await();`.
`shutdown()` sets the flag and calls `signalAll()` on `notFull` and `notEmpty`; it never waits for anything.

</details>

<details><summary>Hint 2 — why not ArrayBlockingQueue + poison pills?</summary>

The obvious design, an `ArrayBlockingQueue` with one poison pill per worker, cannot meet rule 5. Putting the pill
into a full queue blocks `shutdown()`, and a thread that is already blocked in `put` stays blocked until there is
space. Then it slips its job in *after* the shutdown. The condition-based queue can wake those threads and let them
see the flag.

</details>

<details><summary>Hint 3 — a worker's life</summary>

A worker loops: take the next job under the lock (wait while the queue is empty and not shut down; return when it is
shut down and empty), run the handler **outside** the lock, record the outcome. When it leaves the loop, it decrements
the live-worker count and signals `terminated` when it reaches 0. Keep the worker threads so that `shutdownNow()` can
interrupt them, and do not swallow the interrupt.

</details>

## Stretch goals (optional, not graded)

- Add `int queuedCount()` and `int runningCount()` and show the invariants `queued ≤ capacity` and
  `running ≤ workers` in a stress test with many submitters.
- Compare your queue with `ThreadPoolExecutor(workers, workers, 0, SECONDS, new ArrayBlockingQueue<>(capacity))`.
  What does its `RejectedExecutionHandler` do when the queue is full, and which of the rules above does it break?
