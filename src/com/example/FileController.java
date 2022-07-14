package com.example;

import org.apache.commons.io.FilenameUtils;

import javax.swing.event.SwingPropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class FileController {
    private File[] files;
    private File currentFolder;
    private Set<Integer> selectedIndexes = new HashSet<>();
    private final SwingPropertyChangeSupport support;

    public FileController() {
        support = new SwingPropertyChangeSupport(this, true);
    }

    public void addChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public String[] getFileNames() {
        String[] fileNames = new String[files.length];
        int i = 0;
        for(File file: files) {
            fileNames[i++] = file.getName();
        }
        return fileNames;
    }

    public File[] getFiles() {
        return files;
    }

    public void deleteAllIndexes() {
        updateFiles();
        support.firePropertyChange("file-controller", selectedIndexes.size(), 0);
        selectedIndexes.clear();
    }

    private void updateFiles() {
        files = currentFolder.listFiles();
    }

    public void setSelectedIndexes(Set<Integer> selectedIndexes) {
        this.selectedIndexes = selectedIndexes;
    }

    public void setSelectedIndex(int index) {
        selectedIndexes.clear();
        selectedIndexes.add(index);
    }

    public String getFileNameWithoutExtension(int index) {
        String name = getFiles()[index].getName();
        return FilenameUtils.removeExtension(name);
    }

    public Set<Integer> getSelectedIndexes() {
        return selectedIndexes;
    }

    public void setFiles(File file) {
        currentFolder = file;
        this.files = file.listFiles();
    }

}
