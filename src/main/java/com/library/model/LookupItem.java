package com.library.model;

public class LookupItem {

    private int id;
    private String name;
    private String infoUrl;

    public LookupItem() {
    }

    public LookupItem(int id, String name) {
        this(id, name, null);
    }

    public LookupItem(int id, String name, String infoUrl) {
        this.id = id;
        this.name = name;
        this.infoUrl = infoUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfoUrl() {
        return infoUrl;
    }

    public void setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
    }
}