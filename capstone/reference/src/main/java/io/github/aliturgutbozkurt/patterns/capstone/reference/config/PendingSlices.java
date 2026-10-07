package io.github.aliturgutbozkurt.patterns.capstone.reference.config;

import io.github.aliturgutbozkurt.patterns.capstone.api.cli.CommandLine;
import io.github.aliturgutbozkurt.patterns.capstone.api.fulfilment.FulfilmentUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.Report;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.ReportFormat;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.ReportRequest;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.ReportUseCase;

/** Ports of the slice that is not built yet (C6); each call says so. Removed in C6. */
final class PendingSlices {

    private PendingSlices() {
    }

    private static UnsupportedOperationException pending(String what) {
        return new UnsupportedOperationException(what + " arrives in C6");
    }

    static FulfilmentUseCase fulfilment() {
        return () -> {
            throw pending("fulfilment");
        };
    }

    static ReportUseCase reports() {
        return new ReportUseCase() {
            @Override
            public Report run(ReportRequest request) {
                throw pending("reports");
            }

            @Override
            public String render(Report report, ReportFormat format) {
                throw pending("reports");
            }
        };
    }

    static CommandLine cli() {
        return _ -> {
            throw pending("the CLI");
        };
    }
}
