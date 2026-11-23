package kz.support.escalation;

import java.util.function.Consumer;

/** Common link in the Chain of Responsibility. */
public abstract class SupportHandler {
    private SupportHandler next;

    public SupportHandler setNext(SupportHandler next) {
        this.next = next;
        return next;
    }

    public final String handle(SupportTicket ticket, Consumer<String> log) {
        if (canHandle(ticket)) {
            resolve(ticket, log);
            return tierName();
        }
        if (next != null) {
            log.accept(tierName() + " → эскалация в " + next.tierName());
            return next.handle(ticket, log);
        }
        log.accept(tierName() + ": нет следующего уровня обработки");
        return "Не обработана";
    }

    protected abstract boolean canHandle(SupportTicket ticket);
    protected abstract void resolve(SupportTicket ticket, Consumer<String> log);
    protected abstract String tierName();
}
