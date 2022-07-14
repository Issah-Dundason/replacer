package com.example;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;

public class FileNameSearcher extends SwingWorker<Set<Integer>, Void> {
    private final App app;
    private final FileController controller;
    private final String key;

    public FileNameSearcher(App app,
                            FileController controller,
                            String key) {
        this.app = app;
        this.controller = controller;
        this.key = key;
    }

    @Override
    protected Set<Integer> doInBackground() throws Exception {
        int i = 0;
        Set<Integer> indexes = new HashSet<>();
        for(String name: controller.getFileNames()) {
           if(name.matches(String.format("(?i).*%s.*", key))) {
               indexes.add(i);
           }
           i++;
        }
        return indexes;
    }

    @Override
    protected void done() {
        try {
          Set<Integer> indexes =  get();
          controller.setSelectedIndexes(indexes);
          int[] i = indexes.stream().mapToInt(Integer::intValue).toArray();
          app.setSelectedFiles(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
