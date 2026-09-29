package io.github.aliturgutbozkurt.patterns.m06.examples.command.jobs;

import java.util.Objects;

/**
 * A background job as data: it can be queued, logged, sent over the network and retried. {@link JobRunner} decides
 * how each kind of job is performed.
 *
 * @see "m06 lesson, section Command"
 */
public sealed interface Job permits Job.SendEmail, Job.ResizeImage, Job.GenerateReport {

    /**
     * Send one e-mail.
     *
     * @see "m06 lesson, section Command"
     */
    record SendEmail(String to, String subject) implements Job {
        public SendEmail {
            Objects.requireNonNull(to, "to");
            Objects.requireNonNull(subject, "subject");
        }
    }

    /**
     * Resize an image; fails at run time when the width is not positive.
     *
     * @see "m06 lesson, section Command"
     */
    record ResizeImage(String file, int width) implements Job {
        public ResizeImage {
            Objects.requireNonNull(file, "file");
        }
    }

    /**
     * Build a named report.
     *
     * @see "m06 lesson, section Command"
     */
    record GenerateReport(String name) implements Job {
        public GenerateReport {
            Objects.requireNonNull(name, "name");
        }
    }
}
