package ru.arseny.Inferface.Listners;

import ru.arseny.Clients.Client;
import ru.arseny.Clients.ClientConnection;
import ru.arseny.Inferface.UserInterface;
import ru.arseny.Viewer;

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

    @Override
    public void mouseReleased(MouseEvent e) {
        int colIndex = table.columnAtPoint(e.getPoint());
        int rowIndex = table.rowAtPoint(e.getPoint());

        Client client = ClientConnection.getClient(rowIndex, colIndex);
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