package com.example.recovermessages.models;

import java.io.File;

public class CacheFiles {
    private File file;
    private String filename;
    private boolean selected;
    private String size;
    private String type;
    private Long timeStamp;

    public CacheFiles(String filename, String type, String size, File file, boolean selected, Long timeStamp) {
        this.filename = filename;
        this.type = type;
        this.size = size;
        this.file = file;
        this.selected = selected;
        this.timeStamp = timeStamp;
    }


    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String str) {
        this.filename = str;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String str) {
        this.type = str;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String str) {
        this.size = str;
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean z) {
        this.selected = z;
    }
}
