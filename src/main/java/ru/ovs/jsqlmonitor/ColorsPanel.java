package ru.ovs.jsqlmonitor;

import java.awt.Color;

    
class ColorsPanel extends javax.swing.JToolBar {
    void updateColors(java.util.ArrayList<ColorInfo> colors) {
        this.removeAll();
        
        for (ColorInfo color:colors) {
            javax.swing.JTextField tb=new javax.swing.JTextField(String.valueOf(color.getNum()));
            tb.setEditable(false);
            tb.setMaximumSize(new java.awt.Dimension(100,22));
            tb.setPreferredSize(new java.awt.Dimension(100,22));
            tb.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
            tb.setBackground(color.getColor());
            tb.setFont(new java.awt.Font(tb.getFont().getName(),java.awt.Font.BOLD,tb.getFont().getSize()));
            tb.setFocusable(false);
            tb.setBorder(javax.swing.BorderFactory.createLineBorder(tb.getBackground().darker()));
            add(tb);
        }
        
        javax.swing.SwingUtilities.updateComponentTreeUI(this);
    }
    
    ColorsPanel() {
        setOrientation(javax.swing.SwingConstants.VERTICAL);
        setPreferredSize(new java.awt.Dimension(50,0));
        setFloatable(false);
        setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
    }
}
