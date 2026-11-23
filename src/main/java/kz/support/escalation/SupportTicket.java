package kz.support.escalation;

import java.util.Objects;

/** A support request with a severity that determines the minimum handling tier. */
public record SupportTicket(String id, String subject, String description, Severity severity) {
    public SupportTicket {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(subject, "subject");
        Objects.requireNonNull(description, "description");
        Objects.requireNonNull(severity, "severity");
    }

    public enum Severity {
        LOW, MEDIUM, HIGH, CRITICAL
    }
}
