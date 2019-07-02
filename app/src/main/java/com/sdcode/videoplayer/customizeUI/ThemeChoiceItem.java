package com.sdcode.videoplayer.customizeUI;

public class ThemeChoiceItem {
    int color;
    int id;

    public ThemeChoiceItem(int color,int id){
        this.color = color;
        this.id = id;
    }
    public int getColor(){
        return color;
    }
    public int getId(){
        return id;
    }
    public void setColor(int value){
        this.color = value;
    }
    public void setId(int value){
        this.id = value;
    }
}
