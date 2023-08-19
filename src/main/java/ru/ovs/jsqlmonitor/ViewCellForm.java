package ru.ovs.jsqlmonitor;

import static ru.ovs.jsqlmonitor.JSQLMonitor.mainForm;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class ViewCellForm extends javax.swing.JDialog {
    
    ViewCellForm(java.awt.Frame owner,String title,String text) {
        super(owner,title,true);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowDeactivated(java.awt.event.WindowEvent evt) {
                mainForm.settings.vcwidth=getWidth();
                mainForm.settings.vcheight=getHeight();
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getRootPane().registerKeyboardAction(evt->dispose(),javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE,0),javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(evt->dispose(),javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER,0),javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);

        JTextArea editor=new JTextArea(text);
        editor.setFont(new java.awt.Font("Monospaced",java.awt.Font.BOLD,14));
        editor.setEditable(false);
        JScrollPane scrollPane=new JScrollPane(editor);
        add(scrollPane);
        setSize(new java.awt.Dimension(mainForm.settings.vcwidth,mainForm.settings.vcheight));
    }    
}
