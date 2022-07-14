package com.example;

import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;

public class ProgressFrame extends JPanel {
    private JProgressBar progressBar;
    private JTextArea label;
    private final SwingWorker<?, ?> worker;

    public ProgressFrame(ReplacementWorker worker) {
        this.worker = worker;
        setLayout(new BorderLayout());
        setVisible(true);
        createUI();
        addListeners();
        worker.setLabel(label);
        worker.execute();
    }

    private void createUI() {
        progressBar = new JProgressBar();
        progressBar.setUI(new BasicProgressBarUI() {
            protected Color getSelectionForeground() {
                return Color.WHITE;
            }

            @Override
            protected Color getSelectionBackground() {
                return Color.black;
            }
        });
        progressBar.setSize(progressBar.getWidth(), 30);
        progressBar.setForeground(Color.decode("#1E2F97"));
        progressBar.setStringPainted(true);
        label = new JTextArea(1, progressBar.getWidth());
        label.setLineWrap(true);
        label.setWrapStyleWord(true);
        add(label, BorderLayout.NORTH);
        add(progressBar);
    }

    private void addListeners() {
        worker.addPropertyChangeListener((e) -> {

            if(!"progress".equals(e.getPropertyName())) return;

            Integer val = (Integer) e.getNewValue();
            progressBar.setValue(val);

            if(val == 100) label.setText("DONE...");

        });
    }

}
