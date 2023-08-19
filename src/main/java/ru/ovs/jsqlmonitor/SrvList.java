package ru.ovs.jsqlmonitor;

public class SrvList<E> extends javax.swing.JList {
    @Override
    public int locationToIndex(java.awt.Point location) {
        java.awt.Rectangle bounds=this.getCellBounds(0,this.getModel().getSize()-1);
        if (bounds==null || bounds.height<location.y) return -1;
        
        javax.swing.plaf.ListUI ui = getUI();
        return (ui != null) ? ui.locationToIndex(this, location) : -1;
    }    
}
