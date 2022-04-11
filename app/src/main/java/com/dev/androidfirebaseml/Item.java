package com.dev.androidfirebaseml;

public class Item {
    private String filename;
    private String reader;
    private String result;
    private String id;
    private String imageUrl;

    public Item(String filename, String reader, String result) {
        this.filename = filename;
        this.reader = reader;
        this.result = result;
    }

    public Item(String filename, String reader, String result, String id, String imageUrl) {
        this.filename = filename;
        this.reader = reader;
        this.result = result;
        this.id = id;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
