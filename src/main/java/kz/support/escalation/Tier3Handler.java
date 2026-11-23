package kz.support.escalation;

import java.util.function.Consumer;

final class Tier3Handler extends SupportHandler {
    @Override protected boolean canHandle(SupportTicket ticket) {
        return ticket.severity() == SupportTicket.Severity.HIGH
                || ticket.severity() == SupportTicket.Severity.CRITICAL;
    }
    @Override protected void resolve(SupportTicket ticket, Consumer<String> log) {
        log.accept("L3: старший инженер принял обращение; запущена реакция на инцидент.");
    }
    @Override protected String tierName() { return "L3 — Старший инженер"; }
}
