package ru.ovs.jsqlmonitor;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

@FunctionalInterface
interface ShortDocumentListener extends DocumentListener {
    void onTextChange();

    @Override
    default void insertUpdate(DocumentEvent e) {
        onTextChange();
    }
    
    @Override
    default void removeUpdate(DocumentEvent e) {
        onTextChange();
    }
    
    @Override
    default void changedUpdate(DocumentEvent e) {
        onTextChange();
    }
}
