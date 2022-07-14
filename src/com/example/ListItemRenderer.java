package com.example;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;

import static java.awt.Image.SCALE_SMOOTH;

public class ListItemRenderer implements ListCellRenderer<File> {


    @Override
    public Component getListCellRendererComponent(JList<? extends File> list,
                                                  File value,
                                                  int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setSize(new Dimension(50, 40 + (value.getName().length())));

        panel.setForeground(Color.WHITE);
        panel.setBackground(isSelected ? Color.decode("#1E3F66") :
                UIManager.getColor("List:\"List.cellRenderer\"[Disabled].background"));
        ImageIcon icon = (ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(value);
        Image image = icon.getImage();

        icon = new ImageIcon(image.getScaledInstance(30, 30, SCALE_SMOOTH));
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setForeground(Color.white);
        panel.add(iconLabel);

        JLabel text = new JLabel();
        text.setText("<html><div WIDTH=90>" + value.getName() + "</div><html>");
        panel.add(text);

        return panel;
    }
}
