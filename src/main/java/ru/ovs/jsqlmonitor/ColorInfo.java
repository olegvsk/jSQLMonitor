package ru.ovs.jsqlmonitor;

import java.awt.Color;

class ColorInfo {
    final private Color color;
    private int num;

    void inc() {
        num++;
    }
       
    Color getColor() {
        return color;
    }
    
    int getNum() {
        return num;
    }

    ColorInfo(int RGB) {
        color=new Color(RGB);
        num=1;
    }
}
