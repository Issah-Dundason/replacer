package com.example;

import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

public class ReplacementWorker extends SwingWorker<Void, String> {
    private final FileController controller;
    private final String key;
    private final String toBeReplaced;
    private JTextArea label;

    public ReplacementWorker(FileController controller, String key,
                             String toReplace) {
        this.controller = controller;
        this.key = key;
        this.toBeReplaced = toReplace;
    }

    @Override
    protected Void doInBackground() throws Exception {

        File[] files = controller.getFiles();
        Set<Integer> indexes = controller.getSelectedIndexes();

        int i = 0;

        for (Integer index : indexes) {
            File file = files[index];
            String fileName = FilenameUtils.removeExtension(file.getName());
            String extension = FilenameUtils.getExtension(file.getName());
            String name = fileName.replaceFirst(String.format("(?i)%s", toBeReplaced), key);
            Path path = Paths.get(file.toURI());
            publish(name + "." + extension);
            try {
                Files.move(path, path.resolveSibling(name + "." + extension));
                setProgress(((++i) / indexes.size()) * 100);
            } catch (Exception ignored){}
        }

        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        String fileName = chunks.get(chunks.size() - 1);
        label.setText("Renaming " + fileName);
        super.process(chunks);
    }

    public void setLabel(JTextArea label) {
        this.label = label;
    }
}
