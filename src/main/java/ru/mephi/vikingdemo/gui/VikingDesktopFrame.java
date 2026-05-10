package ru.mephi.vikingdemo.gui;

import ru.mephi.vikingdemo.model.BeardStyle;
import ru.mephi.vikingdemo.model.HairColor;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.LambdaStatisticsService;
import ru.mephi.vikingdemo.service.VikingService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VikingDesktopFrame extends JFrame {

    private final VikingService vikingService;
    private final LambdaStatisticsService statsService;
    private VikingTableModel tableModel;
    private JTable vikingTable;

    public VikingDesktopFrame(VikingService vikingService, LambdaStatisticsService statsService) {
        this.vikingService = vikingService;
        this.statsService = statsService;

        setTitle("Viking Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1200, 500));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel header = new JLabel("Viking Demo", SwingConstants.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 18f));
        header.setBorder(new EmptyBorder(10, 0, 10, 0));
        add(header, BorderLayout.NORTH);

        refreshTable(vikingService.findAll());
        vikingTable = new JTable(tableModel);
        vikingTable.setRowHeight(30);

        vikingTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = vikingTable.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        deleteViking(row);
                    }
                }
            }
        });

        add(new JScrollPane(vikingTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton createRandomButton = new JButton("Create random viking");
        createRandomButton.addActionListener(e -> onCreateRandomViking());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshTable(vikingService.findAll()));

        JButton deleteSelectedButton = new JButton("Delete Selected");
        deleteSelectedButton.addActionListener(e -> deleteSelectedViking());

        JButton updateSelectedButton = new JButton("Update Selected");
        updateSelectedButton.addActionListener(e -> updateSelectedViking());

        // новые
        JButton statisticsButton = new JButton("Статистика");
        statisticsButton.addActionListener(e -> showStatisticsDialog());

        JButton massGenerateButton = new JButton("Массовая генерация");
        massGenerateButton.addActionListener(e -> massGenerate());

        buttonPanel.add(createRandomButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(deleteSelectedButton);
        buttonPanel.add(updateSelectedButton);
        buttonPanel.add(statisticsButton);
        buttonPanel.add(massGenerateButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void onCreateRandomViking() {
        vikingService.createRandomViking(); // автоматически добавляет в сервис
        refreshTable(vikingService.findAll());
    }

    private void deleteSelectedViking() {
        int selectedRow = vikingTable.getSelectedRow();
        if (selectedRow >= 0) {
            deleteViking(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a viking to delete");
        }
    }

    private void deleteViking(int index) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete " + tableModel.getVikingAt(index).name() + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            vikingService.deleteViking(index);
            refreshTable(vikingService.findAll());
        }
    }

    private void updateSelectedViking() {
        int selectedRow = vikingTable.getSelectedRow();
        if (selectedRow >= 0) {
            Viking oldViking = tableModel.getVikingAt(selectedRow);
            showUpdateDialog(selectedRow, oldViking);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a viking to update");
        }
    }

    private void showUpdateDialog(int index, Viking oldViking) {
        JTextField nameField = new JTextField(oldViking.name(), 15);
        JTextField ageField = new JTextField(String.valueOf(oldViking.age()), 5);
        JTextField heightField = new JTextField(String.valueOf(oldViking.heightCm()), 5);
        JComboBox<HairColor> hairBox = new JComboBox<>(HairColor.values());
        JComboBox<BeardStyle> beardBox = new JComboBox<>(BeardStyle.values());

        hairBox.setSelectedItem(oldViking.hairColor());
        beardBox.setSelectedItem(oldViking.beardStyle());

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Age:"));
        panel.add(ageField);
        panel.add(new JLabel("Height (cm):"));
        panel.add(heightField);
        panel.add(new JLabel("Hair Color:"));
        panel.add(hairBox);
        panel.add(new JLabel("Beard Style:"));
        panel.add(beardBox);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Update Viking", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Viking updatedViking = new Viking(
                        nameField.getText(),
                        Integer.parseInt(ageField.getText()),
                        Integer.parseInt(heightField.getText()),
                        (HairColor) hairBox.getSelectedItem(),
                        (BeardStyle) beardBox.getSelectedItem(),
                        oldViking.equipment() // сохраняем старое снаряжение
                );
                vikingService.updateViking(index, updatedViking);
                refreshTable(vikingService.findAll());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid number format!");
            }
        }
    }

    private void showStatisticsDialog() {
        new StatisticsDialog(this, statsService, vikingService).setVisible(true);
    }

    private void massGenerate() {
        String input = JOptionPane.showInputDialog(this, "Введите количество викингов для генерации:");
        if (input != null) {
            try {
                int count = Integer.parseInt(input);
                if (count > 0) {
                    vikingService.generateMultipleVikings(count);
                    refreshTable(vikingService.findAll());
                    JOptionPane.showMessageDialog(this, "Сгенерировано " + count + " викингов.");
                } else {
                    JOptionPane.showMessageDialog(this, "Число должно быть больше нуля.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Некорректное число.");
            }
        }
    }

    public void refreshTable(java.util.List<Viking> vikings) {
        if (tableModel == null) {
            tableModel = new VikingTableModel(vikings);
        } else {
            tableModel.setData(vikings);
        }
        if (vikingTable != null) {
            vikingTable.setModel(tableModel);
        }
    }

    public void addNewViking(Viking viking) {
        refreshTable(vikingService.findAll());
    }
}