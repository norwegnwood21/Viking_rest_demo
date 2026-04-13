
package ru.mephi.vikingdemo.gui;

import ru.mephi.vikingdemo.model.EquipmentItem;
import ru.mephi.vikingdemo.model.Viking;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VikingTableModel extends AbstractTableModel {

    private final String[] columns = {"Index", "Name", "Age", "Height (cm)", "Hair color", "Beard style", "Equipment"};
    private List<Viking> data = new ArrayList<>();

    public VikingTableModel() {
        this.data = new ArrayList<>();
    }

    public VikingTableModel(List<Viking> data) {
        this.data = new ArrayList<>(data);
    }

    public void addViking(Viking viking) {
        data.add(viking);
        fireTableRowsInserted(data.size() - 1, data.size() - 1);
    }

    public void setData(List<Viking> newData) {
        this.data = new ArrayList<>(newData);
        fireTableDataChanged();
    }

    public Viking getVikingAt(int row) {
        return data.get(row);
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Viking viking = data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> rowIndex;
            case 1 -> viking.name();
            case 2 -> viking.age();
            case 3 -> viking.heightCm();
            case 4 -> viking.hairColor();
            case 5 -> viking.beardStyle();
            case 6 -> formatEquipment(viking.equipment());
            default -> "";
        };
    }

    private String formatEquipment(List<EquipmentItem> equipment) {
        return equipment.stream()
                .map(item -> item.name() + " [" + item.quality() + "]")
                .collect(Collectors.joining(", "));
    }
}