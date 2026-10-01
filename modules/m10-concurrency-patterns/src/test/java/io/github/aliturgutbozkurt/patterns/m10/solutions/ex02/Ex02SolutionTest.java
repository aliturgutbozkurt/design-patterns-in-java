package io.github.aliturgutbozkurt.patterns.m10.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m10.exercises.ex02.Ex02Contract;
import io.github.aliturgutbozkurt.patterns.m10.exercises.ex02.JobHandler;
import io.github.aliturgutbozkurt.patterns.m10.exercises.ex02.JobQueue;
import java.util.concurrent.ThreadFactory;

class Ex02SolutionTest extends Ex02Contract {

    @Override
    protected JobQueue newQueue(int capacity, int workers, JobHandler handler, ThreadFactory threadFactory) {
        return new WorkerPoolQueue(capacity, workers, handler, threadFactory);
    }
}
