package ru.vncclient.Inferface.Listners;

import ru.vncclient.Clients.Client;
import ru.vncclient.Clients.ClientList;
import ru.vncclient.Inferface.UserInterface;
import ru.vncclient.Viewer;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TableClickListener implements MouseListener {
    private final JTable table;

    public TableClickListener(JTable table) {
        this.table = table;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    /**
     * Нажатия на таблицу по активным вирт. машинам
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        int colIndex = table.columnAtPoint(e.getPoint());
        int rowIndex = table.rowAtPoint(e.getPoint());

        Client client = ClientList.getClient(rowIndex, colIndex);
        if (client != null) {
            switch (e.getButton()) {
                case MouseEvent.BUTTON1:
                    new Viewer(client);
                    break;
                case MouseEvent.BUTTON3:
                    UserInterface.createPopup(e, colIndex, rowIndex);
                    break;
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}