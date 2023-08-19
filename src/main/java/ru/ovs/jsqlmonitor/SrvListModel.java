package ru.ovs.jsqlmonitor;

import static ru.ovs.jsqlmonitor.JSQLMonitor.*;
import javax.swing.DefaultListModel;

class SrvListModel<T> extends DefaultListModel<T> {
    public void update(int index) {
        fireContentsChanged(this,index,index);
        mainForm.tuneActions();
    }
    
    public void update() {
        fireContentsChanged(this,0,getSize());
    }
}
