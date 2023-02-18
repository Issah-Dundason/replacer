package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.FIRST_LINE_START;


public class App extends JFrame {
    private final JPanel firstPanel;
    private final JPanel secondPanel;
    private final FileController controller;
    private JButton replaceBtn;
    private JButton folderBtn;
    private JList<File> filesList;
    private JTextArea toBeReplacedTxt;
    private JTextArea replacementTxt;

    public App() {
        Container pane = getContentPane();
        pane.setLayout(new GridBagLayout());
        firstPanel = new JPanel();
        GridBagConstraints c = new GridBagConstraints();
        c.weighty = 1;
        c.weightx = 0.6;
        c.fill = BOTH;
        firstPanel.setLayout(new BorderLayout());
        secondPanel = new JPanel();
        secondPanel.setLayout(new GridBagLayout());
        pane.add(firstPanel, c);
        c.gridx = 1;
        c.weightx = 0.1;
        pane.add(secondPanel, c);
        buildUI();
        addListeners();
        setSize(500, 500);
        setResizable(false);
        controller = new FileController();
        controller.addChangeListener((e) -> {
            toBeReplacedTxt.setText("");
            replacementTxt.setText("");
            filesList.setListData(controller.getFiles());
            filesList.setSelectedIndices(new int[]{});
        });
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void addListeners() {
        folderBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int option = chooser.showOpenDialog(this);

            if (option == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                controller.setFiles(file);
                filesList.requestFocus();
                filesList.setListData(controller.getFiles());
            }
        });
        toBeReplacedTxt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String key = toBeReplacedTxt.getText();
                if (!key.isEmpty()) {
                    FileNameSearcher searcher = new FileNameSearcher(App.this, controller, key);
                    searcher.execute();
                } else {
                    setSelectedFiles(new int[]{});
                }
            }
        });
        filesList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JList<?> target = (JList<?>) e.getSource();
                int index = target.locationToIndex(e.getPoint());
                controller.setSelectedIndex(index);
                SwingUtilities.invokeLater(() -> {
                    String file = controller.getFileNameWithoutExtension(index);
                    toBeReplacedTxt.setText(file);
                });
            }


        });
        replaceBtn.addActionListener((e) -> {

            if(controller.getSelectedIndexes().size() == 0) return;

            int option = JOptionPane.showOptionDialog(
                    this,
                    "Should rename",
                    "Renaming",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, new Object[]{"Yes", "No"}, "Yes");


            if (option == 0) {
                ReplacementWorker worker = new ReplacementWorker(this.controller, replacementTxt.getText(),
                        toBeReplacedTxt.getText());
                option = JOptionPane.showOptionDialog(this, new ProgressFrame(worker), "Progress",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                        new Object[]{"Close"}, "Close");

                if (option == -1) {
                    if (worker.isDone()) controller.deleteAllIndexes();
                    else if (!worker.isDone()) {
                        worker.cancel(true);
                    }
                } else if (option == 0) controller.deleteAllIndexes();
            }
        });
    }

    private void buildUI() {
        buildChooseBtn();
        buildToBeReplacedTxt();
        buildFilesContainer();
        buildReplacementTxt();
        buildReplaceBtn();
    }

    private void buildChooseBtn() {
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = FIRST_LINE_START;
        c.weightx = 0.5;
        c.insets = new Insets(10, 10, 10, 10);
        folderBtn = new JButton("Open Folder");
        secondPanel.add(folderBtn, c);
    }

    private void buildToBeReplacedTxt() {
        GridBagConstraints c2 = new GridBagConstraints();
        c2.anchor = FIRST_LINE_START;
        c2.gridy = 1;
        c2.insets = new Insets(10, 10, 0, 0);
        JLabel label = new JLabel("Replace:");
        secondPanel.add(label, c2);
        GridBagConstraints c1 = new GridBagConstraints();
        c1.gridy = 2;
        c1.ipady = 10;
        c1.anchor = FIRST_LINE_START;
        c1.weightx = 0.5;
        c1.insets = new Insets(5, 10, 0, 0);
        toBeReplacedTxt = new JTextArea(1, 10);
        toBeReplacedTxt.setWrapStyleWord(true);
        toBeReplacedTxt.setLineWrap(true);
        secondPanel.add(toBeReplacedTxt, c1);
    }

    private void buildReplaceBtn() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 5;
        c.anchor = FIRST_LINE_START;
        c.insets = new Insets(4, 10, 0, 0);
        c.weighty = 0.5;
        replaceBtn = new JButton("Replace");
        secondPanel.add(replaceBtn, c);
    }

    private void buildReplacementTxt() {
        GridBagConstraints c2 = new GridBagConstraints();
        c2.anchor = FIRST_LINE_START;
        c2.gridy = 3;
        c2.insets = new Insets(5, 10, 0, 0);
        JLabel label = new JLabel("With:");
        secondPanel.add(label, c2);
        GridBagConstraints c = new GridBagConstraints();
        replacementTxt = new JTextArea(1, 10);
        c.gridy = 4;
        c.ipady = 10;
        replacementTxt.setLineWrap(true);
        replacementTxt.setWrapStyleWord(true);
        c.insets = new Insets(5, 10, 0, 0);
        c.anchor = FIRST_LINE_START;
        c.weightx = 0.5;
        secondPanel.add(replacementTxt, c);
    }

    private void buildFilesContainer() {
        JScrollPane scroll = new JScrollPane();
        filesList = new JList<>();
        scroll.setViewportView(filesList);
        filesList.setCellRenderer(new ListItemRenderer());
        scroll.setEnabled(true);
        firstPanel.add(scroll);
    }

    public void setSelectedFiles(int[] indexes) {
        SwingUtilities.invokeLater(() -> {
            filesList.setSelectedIndices(indexes);
            if (indexes.length > 0) {
                filesList.ensureIndexIsVisible(indexes[0]);
            }
        });
    }

}
