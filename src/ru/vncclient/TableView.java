package ru.vncclient;

import ru.vncclient.Clients.ClientList;
import ru.vncclient.Inferface.InterfaceParam;
import ru.vncclient.Inferface.ImageRender;
import ru.vncclient.Inferface.Listners.TableClickListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import static ru.vncclient.Inferface.InterfaceParam.COLUMN_LIMIT;

public class TableView {
    private static JTable table;

    private static int row = 0;
    private static int column = 0;

    public TableView() {
        table = createTable();
        table.addMouseListener(new TableClickListener(table));
        clearTable();
    }

    /**
     * Создание таблицы
     *
     * @return JTable
     */
    private JTable createTable() {
        JTable jTable = new JTable();
        jTable.setModel(createModel());
        jTable.setDefaultRenderer(Object.class, new ImageRender());
        int height = (800 / InterfaceParam.COLUMN_LIMIT) + (10 + InterfaceParam.COLUMN_LIMIT);
        jTable.setRowHeight(height);
        jTable.setShowGrid(false);
        jTable.setCellSelectionEnabled(false);
        jTable.setTableHeader(null);

        return jTable;
    }

    /**
     * Очистить таблицу
     */
    public static void clearTable() {
        row = 0;
        column = 0;
        table.setModel(createModel());
        ClientList.clearMap();
    }

    public static DefaultTableModel createModel() {
        DefaultTableModel model = new DefaultTableModel(1,
                InterfaceParam.COLUMN_LIMIT) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        return model;
    }

    /**
     * Проверка текущей колонки
     * Если равно или больше, добавляет новый ряд
     */
    public static void checkColumn() {
        if (column >= COLUMN_LIMIT) {
            row += 1;
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.insertRow(row, new Object[COLUMN_LIMIT]);
            column = 0;
        }
    }

    /**
     * Добавить картинку в таблицу
     * @param image картинка
     * @param rowIndex строка
     * @param colIndex столбец
     */
    public static void setView(Image image, int rowIndex, int colIndex) {
        table.setValueAt(image, rowIndex, colIndex);
    }

    public static JTable getTable() {
        return table;
    }
}