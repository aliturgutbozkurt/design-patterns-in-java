package io.github.aliturgutbozkurt.patterns.m09.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.Ex02Contract;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.SignupPipeline;
import io.github.aliturgutbozkurt.patterns.m09.exercises.ex02.UserRegistry;

class Ex02SolutionTest extends Ex02Contract {

    @Override
    protected SignupPipeline newPipeline(UserRegistry registry) {
        return new DefaultSignupPipeline(registry);
    }
}
