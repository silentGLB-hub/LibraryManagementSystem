package com.library.model;

public class StatItem {

    private String label;
    private int value;
    private int percent;

    public StatItem() {
    }

    public StatItem(String label, int value, int percent) {
        this.label = label;
        this.value = value;
        this.percent = percent;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }
}
