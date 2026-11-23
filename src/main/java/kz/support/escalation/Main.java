package kz.support.escalation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static final Color NAVY = new Color(22, 35, 58);
    private static final Color BLUE = new Color(54, 112, 220);
    private static final Color PALE = new Color(244, 247, 251);
    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final SupportHandler chain;
    private final AtomicInteger nextId = new AtomicInteger(105);
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Тема обращения", "Серьёзность", "Назначено в"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTextArea activity = new JTextArea();
    private JLabel countValue;
    private JLabel criticalValue;
    private int criticalCount;

    private Main() {
        SupportHandler first = new Tier1Handler();
        SupportHandler second = new Tier2Handler();
        first.setNext(second).setNext(new Tier3Handler());
        chain = first;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) { }
            new Main().showWindow();
        });
    }

    private void showWindow() {
        JFrame frame = new JFrame("SupportFlow | Эскалация обращений");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(900, 600));
        frame.setSize(1120, 700);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(buildContent());
        frame.setVisible(true);
        addWelcomeMessage();
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(PALE);
        root.setBorder(new EmptyBorder(24, 28, 24, 28));
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildWorkspace(), BorderLayout.CENTER);
        return root;
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(18, 0));
        header.setOpaque(false);
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        JLabel eyebrow = new JLabel("SUPPORT OPERATIONS  /  CHAIN OF RESPONSIBILITY");
        eyebrow.setFont(new Font("SansSerif", Font.BOLD, 11));
        eyebrow.setForeground(BLUE);
        JLabel title = new JLabel("Эскалация обращений");
        title.setFont(new Font("SansSerif", Font.BOLD, 27));
        title.setForeground(NAVY);
        JLabel subtitle = new JLabel("Заявки автоматически направляются команде нужного уровня.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(104, 116, 135));
        titleBox.add(eyebrow); titleBox.add(Box.createVerticalStrut(6));
        titleBox.add(title); titleBox.add(Box.createVerticalStrut(4)); titleBox.add(subtitle);
        header.add(titleBox, BorderLayout.WEST);

        JPanel stats = new JPanel(new GridLayout(1, 2, 10, 0));
        stats.setOpaque(false);
        countValue = addStat(stats, "ОБРАЩЕНИЯ", "0");
        criticalValue = addStat(stats, "КРИТИЧЕСКИЕ", "0");
        header.add(stats, BorderLayout.EAST);
        return header;
    }

    private JLabel addStat(JPanel parent, String label, String initial) {
        JPanel card = new JPanel(new BorderLayout(12, 2));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 234, 241)), new EmptyBorder(11, 15, 11, 15)));
        JLabel name = new JLabel(label);
        name.setFont(new Font("SansSerif", Font.BOLD, 10));
        name.setForeground(new Color(120, 132, 150));
        JLabel value = new JLabel(initial);
        value.setFont(new Font("SansSerif", Font.BOLD, 22));
        value.setForeground(NAVY);
        card.add(name, BorderLayout.NORTH); card.add(value, BorderLayout.CENTER);
        parent.add(card);
        return value;
    }

    private JPanel buildWorkspace() {
        JPanel workspace = new JPanel(new BorderLayout(16, 0));
        workspace.setOpaque(false);
        workspace.add(buildTicketsPanel(), BorderLayout.CENTER);
        workspace.add(buildActivityPanel(), BorderLayout.EAST);
        return workspace;
    }

    private JPanel buildTicketsPanel() {
        JPanel panel = cardPanel();
        panel.setLayout(new BorderLayout(0, 14));
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel heading = new JLabel("Очередь обращений");
        heading.setFont(new Font("SansSerif", Font.BOLD, 17));
        heading.setForeground(NAVY);
        top.add(heading, BorderLayout.WEST);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        JButton sample = button("Загрузить примеры", false);
        sample.addActionListener(e -> loadExamples());
        JButton add = button("+ Новое обращение", true);
        add.addActionListener(e -> showNewTicketDialog());
        buttons.add(sample); buttons.add(add);
        top.add(buttons, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        JTable table = new JTable(tableModel);
        table.setRowHeight(38);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setForeground(new Color(47, 59, 78));
        table.setSelectionBackground(new Color(229, 238, 253));
        table.setGridColor(new Color(238, 241, 246));
        table.setShowVerticalLines(false);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        table.getTableHeader().setForeground(new Color(106, 119, 139));
        table.getTableHeader().setBackground(new Color(249, 250, 252));
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
        table.getColumnModel().getColumn(0).setPreferredWidth(85);
        table.getColumnModel().getColumn(1).setPreferredWidth(240);
        table.getColumnModel().getColumn(2).setPreferredWidth(115);
        table.getColumnModel().getColumn(3).setPreferredWidth(210);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(235, 239, 245)));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildActivityPanel() {
        JPanel panel = cardPanel();
        panel.setPreferredSize(new Dimension(305, 0));
        panel.setLayout(new BorderLayout(0, 12));
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        JLabel title = new JLabel("Журнал эскалации");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(NAVY);
        JButton clear = new JButton("Очистить");
        clear.setFont(new Font("SansSerif", Font.PLAIN, 11));
        clear.setForeground(BLUE);
        clear.setBorderPainted(false); clear.setContentAreaFilled(false); clear.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clear.addActionListener(e -> activity.setText(""));
        titleRow.add(title, BorderLayout.WEST); titleRow.add(clear, BorderLayout.EAST);
        panel.add(titleRow, BorderLayout.NORTH);

        activity.setEditable(false);
        activity.setLineWrap(true);
        activity.setWrapStyleWord(true);
        activity.setFont(new Font("Monospaced", Font.PLAIN, 12));
        activity.setForeground(new Color(62, 75, 96));
        activity.setBackground(new Color(249, 250, 252));
        activity.setBorder(new EmptyBorder(12, 12, 12, 12));
        JScrollPane logScroll = new JScrollPane(activity);
        logScroll.setBorder(BorderFactory.createLineBorder(new Color(235, 239, 245)));
        panel.add(logScroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout(0, 5));
        footer.setOpaque(false);
        JLabel chainLabel = new JLabel("МАРШРУТ ОБРАБОТКИ");
        chainLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        chainLabel.setForeground(new Color(120, 132, 150));
        JLabel chainValue = new JLabel("L1  →  L2  →  L3");
        chainValue.setFont(new Font("SansSerif", Font.BOLD, 14));
        chainValue.setForeground(BLUE);
        footer.add(chainLabel, BorderLayout.NORTH); footer.add(chainValue, BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel cardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 234, 241)), new EmptyBorder(17, 17, 17, 17)));
        return panel;
    }

    private JButton button(String text, boolean primary) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 14, 10, 14));
        if (primary) {
            button.setForeground(Color.WHITE); button.setBackground(BLUE); button.setOpaque(true);
            button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BLUE), new EmptyBorder(9, 13, 9, 13)));
        } else {
            button.setForeground(new Color(70, 86, 109)); button.setBackground(new Color(246, 248, 251)); button.setOpaque(true);
            button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(229, 234, 241)), new EmptyBorder(9, 13, 9, 13)));
        }
        return button;
    }

    private void showNewTicketDialog() {
        JTextField subject = new JTextField(24);
        JTextArea description = new JTextArea(4, 24);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        JComboBox<SupportTicket.Severity> severity = new JComboBox<>(SupportTicket.Severity.values());
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 5, 6, 5); c.anchor = GridBagConstraints.WEST;
        c.gridx = 0; c.gridy = 0; form.add(new JLabel("Тема:"), c);
        c.gridx = 1; form.add(subject, c);
        c.gridx = 0; c.gridy = 1; form.add(new JLabel("Описание:"), c);
        c.gridx = 1; form.add(new JScrollPane(description), c);
        c.gridx = 0; c.gridy = 2; form.add(new JLabel("Серьёзность:"), c);
        c.gridx = 1; form.add(severity, c);

        int result = JOptionPane.showConfirmDialog(null, form, "Новое обращение",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;
        String title = subject.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Введите тему обращения.", "Проверка данных", JOptionPane.WARNING_MESSAGE);
            return;
        }
        SupportTicket ticket = new SupportTicket("INC-" + nextId.getAndIncrement(), title,
                description.getText().trim(), (SupportTicket.Severity) severity.getSelectedItem());
        route(ticket);
    }

    private void loadExamples() {
        List<SupportTicket> examples = List.of(
                new SupportTicket("INC-101", "Сброс пароля", "Пользователь не может войти", SupportTicket.Severity.LOW),
                new SupportTicket("INC-102", "Не синхронизируется почта", "Проблема с почтовым ящиком", SupportTicket.Severity.MEDIUM),
                new SupportTicket("INC-103", "Ошибка платёжного сервиса", "Транзакции не проходят", SupportTicket.Severity.HIGH),
                new SupportTicket("INC-104", "Сервис недоступен", "Затронуты все клиенты", SupportTicket.Severity.CRITICAL)
        );
        examples.forEach(this::route);
    }

    private void route(SupportTicket ticket) {
        StringBuilder steps = new StringBuilder();
        String tier = chain.handle(ticket, message -> steps.append("  • ").append(message).append('\n'));
        tableModel.addRow(new Object[]{ticket.id(), ticket.subject(), severityLabel(ticket.severity()), tier});
        if (ticket.severity() == SupportTicket.Severity.CRITICAL) criticalCount++;
        countValue.setText(String.valueOf(tableModel.getRowCount()));
        criticalValue.setText(String.valueOf(criticalCount));
        activity.append("[" + LocalTime.now().format(CLOCK) + "] " + ticket.id() + " — " + ticket.subject() + "\n");
        activity.append(steps.toString() + "\n");
        activity.setCaretPosition(activity.getDocument().getLength());
    }

    private String severityLabel(SupportTicket.Severity severity) {
        return switch (severity) {
            case LOW -> "Низкая";
            case MEDIUM -> "Средняя";
            case HIGH -> "Высокая";
            case CRITICAL -> "Критическая";
        };
    }

    private void addWelcomeMessage() {
        activity.append("Приложение готово к работе.\nДобавьте обращение или загрузите демонстрационные примеры.\n\n");
    }
}
