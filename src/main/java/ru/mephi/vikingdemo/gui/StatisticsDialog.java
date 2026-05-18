package ru.mephi.vikingdemo.gui;

import ru.mephi.vikingdemo.model.BeardStyle;
import ru.mephi.vikingdemo.model.HairColor;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.LambdaStatisticsService;
import ru.mephi.vikingdemo.service.VikingService;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StatisticsDialog extends JDialog {
    private final LambdaStatisticsService statsService;
    private final VikingService vikingService;
    private JTextArea resultArea;

    public StatisticsDialog(JFrame parent, LambdaStatisticsService statsService, VikingService vikingService) {
        super(parent, "Статистика и операции", true);
        this.statsService = statsService;
        this.vikingService = vikingService;
        setSize(700, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Кнопки для оценки выборки
        buttonPanel.add(createButton("Подсчёт старше 30", e -> countByAgeGreater(30)));
        buttonPanel.add(createButton("Подсчёт младше 20", e -> countByAgeLess(20)));
        buttonPanel.add(createButton("Подсчёт возраст 20-35", e -> countByAgeBetween(20, 35)));
        buttonPanel.add(createButton("Подсчёт вне 20-35", e -> countByAgeOutside(20, 35)));
        buttonPanel.add(createButton("Борода LONG + волосы Brown", e -> countByBeardAndHair(BeardStyle.LONG, HairColor.Brown)));
        buttonPanel.add(createButton("Имеют 1 или 2 топора", e -> countVikingsWithOneOrTwoAxes()));
        buttonPanel.add(createButton("Случайный викинг >180 см", e -> showRandomTall()));
        buttonPanel.add(createButton("Все с легендарным снаряжением", e -> showLegendary()));
        buttonPanel.add(createButton("Рыжебородые по возрасту", e -> showRedBeardedSorted()));
        buttonPanel.add(createButton("Max ID (последний индекс)", e -> showMaxId()));
        buttonPanel.add(createButton("Все чётные ID", e -> showEvenIds()));
        buttonPanel.add(createButton("Очистить", e -> resultArea.setText("")));

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, java.awt.event.ActionListener listener) {
        JButton btn = new JButton(text);
        btn.addActionListener(listener);
        return btn;
    }

    private void appendResult(String title, Object data) {
        resultArea.append(title + ":\n" + data + "\n\n");
    }

    private void countByAgeGreater(int age) {
        long count = statsService.countByAgeGreaterThan(age);
        appendResult("Викингов старше " + age, count);
    }

    private void countByAgeLess(int age) {
        long count = statsService.countByAgeLessThan(age);
        appendResult("Викингов младше " + age, count);
    }

    private void countByAgeBetween(int min, int max) {
        long count = statsService.countByAgeBetween(min, max);
        appendResult("Викингов с возрастом от " + min + " до " + max, count);
    }

    private void countByAgeOutside(int min, int max) {
        long count = statsService.countByAgeOutside(min, max);
        appendResult("Викингов с возрастом вне диапазона " + min + "-" + max, count);
    }

    private void countByBeardAndHair(BeardStyle beard, HairColor hair) {
        long count = statsService.countByBeardStyleAndHairColor(beard, hair);
        appendResult("Викингов с бородой " + beard + " и волосами " + hair, count);
    }

    private void countVikingsWithOneOrTwoAxes() {
        long count = statsService.countVikingsWithOneOrTwoAxes();
        appendResult("Викингов, имеющих 1 или 2 топора", count);
    }

    private void showRandomTall() {
        var opt = statsService.getRandomTallViking(180);
        appendResult("Случайный викинг ростом >180 см",
                opt.map(v -> v.name() + ", возраст " + v.age() + ", рост " + v.heightCm())
                        .orElse("Нет таких викингов"));
    }

    private void showLegendary() {
        List<Viking> list = statsService.getVikingsWithLegendaryEquipment();
        String result = list.isEmpty() ? "Нет" : list.stream()
                .map(v -> v.name() + " [" + v.age() + "]")
                .collect(Collectors.joining(", "));
        appendResult("Викинги с легендарным снаряжением", result);
    }

    private void showRedBeardedSorted() {
        List<Viking> list = statsService.getRedBeardedVikingsSortedByAge();
        if (list.isEmpty()) {
            appendResult("Рыжебородые  по возрасту", "Нет таких викингов");
        } else {
            String result = list.stream()
                    .map(v -> v.name() + " (" + v.age() + " лет, борода: " + v.beardStyle() + ", волосы: " + v.hairColor() + ")")
                    .collect(Collectors.joining("\n"));
            appendResult("Рыжебородые  по возрасту", result);
        }
    }

    private void showMaxId() {
        int total = vikingService.findAll().size();
        Integer[] ids = new Integer[total];
        for (int i = 0; i < total; i++) {
            ids[i] = i;
        }
        Integer maxId = statsService.getMaxId(ids);
        appendResult("Максимальный ID ", maxId == -1 ? "Нет викингов" : maxId);
    }

    private void showEvenIds() {
        int total = vikingService.findAll().size();
        Integer[] ids = new Integer[total];
        for (int i = 0; i < total; i++) {
            ids[i] = i;
        }
        List<Integer> evenIds = statsService.getEvenIds(ids);
        appendResult("Чётные ID", evenIds.isEmpty() ? "Нет" : evenIds.toString());
    }
}