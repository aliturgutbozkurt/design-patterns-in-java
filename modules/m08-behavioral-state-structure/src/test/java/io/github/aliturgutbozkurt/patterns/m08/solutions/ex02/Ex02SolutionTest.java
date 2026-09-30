package io.github.aliturgutbozkurt.patterns.m08.solutions.ex02;

import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.Ex02Contract;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.Language;

class Ex02SolutionTest extends Ex02Contract {

    @Override
    protected Language newLanguage() {
        return new MiniLanguage();
    }
}
