package ru.vncclient.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageRender extends DefaultTableCellRenderer {
    /**
     * Отрисовка изображения в таблице
     * @param table таблица
     * @param value значение в виде картинки
     * @param isSelected
     * @param hasFocus
     * @param row строка
     * @param column столбец
     * @return
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value instanceof BufferedImage) {
            Image image = (BufferedImage) value;
            ImageIcon icon = new ImageIcon(image.getScaledInstance(
                    table.getColumnModel().getColumn(column).getWidth(),
                    table.getRowHeight(), Image.SCALE_AREA_AVERAGING));

            setIcon(icon);
            setText(null);
            return this;
        }
        return null;
    }
}