package com.dev.androidfirebaseml;

public class Item {
    private String filename;
    private String reader;
    private String result;

    public Item(String filename, String reader, String result) {
        this.filename = filename;
        this.reader = reader;
        this.result = result;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getReader() {
        return reader;
    }

    public void setReader(String reader) {
        this.reader = reader;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
