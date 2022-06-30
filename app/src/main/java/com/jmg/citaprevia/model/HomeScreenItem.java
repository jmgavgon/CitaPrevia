package com.jmg.citaprevia.model;

import java.io.Serializable;

public class HomeScreenItem implements Serializable {
    private String text;
    private int icon;

    public HomeScreenItem(String text, int icon){
        this.text = text;
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
