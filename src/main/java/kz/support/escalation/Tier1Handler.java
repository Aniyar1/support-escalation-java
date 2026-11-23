package kz.support.escalation;

import java.util.function.Consumer;

final class Tier1Handler extends SupportHandler {
    @Override protected boolean canHandle(SupportTicket ticket) {
        return ticket.severity() == SupportTicket.Severity.LOW;
    }
    @Override protected void resolve(SupportTicket ticket, Consumer<String> log) {
        log.accept("L1: оператор первой линии решил обращение.");
    }
    @Override protected String tierName() { return "L1 — Первая линия"; }
}
