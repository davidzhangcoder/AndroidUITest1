package com.test1.model;

import java.io.Serializable;

public class SizeBean implements Serializable
{

    private String size;

    private boolean isSelected;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}
