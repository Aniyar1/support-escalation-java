package kz.support.escalation;

import java.util.function.Consumer;

final class Tier2Handler extends SupportHandler {
    @Override protected boolean canHandle(SupportTicket ticket) {
        return ticket.severity() == SupportTicket.Severity.MEDIUM;
    }
    @Override protected void resolve(SupportTicket ticket, Consumer<String> log) {
        log.accept("L2: технический специалист решил обращение.");
    }
    @Override protected String tierName() { return "L2 — Технический специалист"; }
}
