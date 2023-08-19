package ru.ovs.jsqlmonitor;

import static ru.ovs.jsqlmonitor.JSQLMonitor.mainForm;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.BoxLayout;

class JColCheckBox extends javax.swing.JCheckBox {
    int col;
    
    JColCheckBox(String text,int acol) {
        super(text);
        col=acol;
    }
}

class ColumnsForm extends javax.swing.JDialog {

    ColumnsForm() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowDeactivated(java.awt.event.WindowEvent evt) {
                dispose();
            }
        });

        getRootPane().registerKeyboardAction(evt->dispose(),javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE,0),javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);

        JPanel panel=new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        
        for (int i=0;i<mainForm.getColumnCount();i++) {
            JColCheckBox ch=new JColCheckBox(mainForm.getColumnName(i),i);
            ch.setSelected(mainForm.isVisibleColumn(i));
            
            ch.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mainForm.showColumn(((JColCheckBox)evt.getSource()).col,((JColCheckBox)evt.getSource()).isSelected());
                }
            });
            
            panel.add(ch);
        }

        JScrollPane scrollPane=new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane);
        panel.setPreferredSize(new java.awt.Dimension(panel.getPreferredSize().width+scrollPane.getVerticalScrollBar().getPreferredSize().width+4,panel.getPreferredSize().height));
    }
}
