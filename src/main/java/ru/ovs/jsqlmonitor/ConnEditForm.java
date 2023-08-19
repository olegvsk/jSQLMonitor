package ru.ovs.jsqlmonitor;

import static ru.ovs.jsqlmonitor.JSQLMonitor.*;
import java.util.Arrays;
import javax.swing.JFileChooser;
import java.io.File;

public class ConnEditForm extends javax.swing.JDialog {
    private Server server;
    private int maxHeight;
    
    void updateServer() {
        Utils.wipeCharArray(server.password);
        server.name=txConnName.getText();
        server.user=txUser.getText();
        server.sqlScript=txSQLScript.getText();
        server.connString=txConnectionString.getText();
        server.password=chSavePassw.isSelected() ? txPassword.getPassword() : null;
        server.readOnly=chReadOnly.isSelected();
        server.kerberos=chKerberos.isSelected();
        mainForm.srvListModel.update(mainForm.getCurrentIndex());
        XMLProc.saveConnections();
    }
    
    private void setOKEnabled() {
        boolean changed = !txConnName.getText().equals(server.name)               |
                          !txUser.getText().equals(server.user)                   |
                          !txSQLScript.getText().equals(server.sqlScript)         |
                          !txConnectionString.getText().equals(server.connString) |
                          !(chSavePassw.isSelected() == server.passwSaved())      |
                          !(chReadOnly.isSelected() == server.readOnly)           |
                          !(chKerberos.isSelected() == server.kerberos)           |
                          !Arrays.equals(txPassword.getPassword(),server.password) & chSavePassw.isSelected();
        
        changed=changed & !Utils.isBlank(txConnName.getText());
        
        btnOK.setEnabled(changed);
    }

    public ConnEditForm(Server aServer) {
        super(mainForm,"Edit connection",true);
        initComponents();
                       
        server=aServer;
        txDriver.setText(server.drvName);
        if(server.driver!=null) txClassName.setText(server.driver.drvClass);
        txSQLScript.setText(server.sqlScript);
        txConnName.setText(server.name);
        txUser.setText(server.user);
        chReadOnly.setSelected(server.readOnly);
        chKerberos.setSelected(server.kerberos);
        txConnectionString.setText(server.connString);
        if (server.passwSaved()) {
            txPassword.setText(String.valueOf(server.password));
            chSavePassw.setSelected(true);
        }
        
        chKerberos.setEnabled(server.driver!=null && server.driver.canKerberosAuth());
        chKerberosActionPerformed(null);

        txConnName.getDocument().addDocumentListener((ShortDocumentListener)         ()->setOKEnabled());
        txUser.getDocument().addDocumentListener((ShortDocumentListener)             ()->setOKEnabled());
        txPassword.getDocument().addDocumentListener((ShortDocumentListener)         ()->setOKEnabled());
        txSQLScript.getDocument().addDocumentListener((ShortDocumentListener)        ()->setOKEnabled());
        txConnectionString.getDocument().addDocumentListener((ShortDocumentListener) ()->setOKEnabled());
        
        getRootPane().registerKeyboardAction(evt->dispose(),javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE,0),javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        getRootPane().setDefaultButton(btnOK);
        txConnectionString.moveCaretPosition(0);
        txConnName.requestFocusInWindow();

        setMinimumSize(new java.awt.Dimension(getWidth(),getHeight()));
        maxHeight=getHeight();
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txDriver = new javax.swing.JTextField();
        txClassName = new javax.swing.JTextField();
        txConnName = new javax.swing.JTextField();
        txUser = new javax.swing.JTextField();
        txConnectionString = new javax.swing.JTextField();
        chSavePassw = new javax.swing.JCheckBox();
        btnCancel = new javax.swing.JToggleButton();
        btnOK = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txPassword = new javax.swing.JPasswordField();
        chKerberos = new javax.swing.JCheckBox();
        chReadOnly = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        txSQLScript = new javax.swing.JTextField();
        btnOpen = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        jLabel1.setText("JDBC driver:");

        jLabel2.setText("Class name:");

        jLabel3.setText("Name:");

        jLabel5.setText("Password:");

        jLabel6.setText("Connection string");

        txDriver.setEditable(false);

        txClassName.setEditable(false);

        chSavePassw.setText("Save");
        chSavePassw.setToolTipText("The password will be saved in plain text");
        chSavePassw.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chSavePasswStateChanged(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnOK.setText("OK");
        btnOK.setEnabled(false);
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });

        jLabel7.setText("User name:");

        chKerberos.setText("Integrated security");
        chKerberos.setToolTipText("Only for MS SQL Server");
        chKerberos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chKerberosActionPerformed(evt);
            }
        });

        chReadOnly.setText("Set read-only connection");
        chReadOnly.setToolTipText("If the jdbc driver supports this mode");
        chReadOnly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chReadOnlyActionPerformed(evt);
            }
        });

        jLabel8.setText("SQL script");

        btnOpen.setText("...");
        btnOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(chReadOnly))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel8)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txConnectionString)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txClassName)
                                    .addComponent(txConnName)
                                    .addComponent(txDriver, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(txPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                                            .addComponent(txUser))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(chKerberos)
                                            .addComponent(chSavePassw)))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnOK)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCancel))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(txSQLScript)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnOpen, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCancel, btnOK});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txDriver, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txClassName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txConnName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chKerberos))
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chSavePassw)))
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txConnectionString, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chReadOnly)
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txSQLScript, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOpen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnOK))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        updateServer();
        dispose();
    }//GEN-LAST:event_btnOKActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        if (evt.getComponent().getHeight()>maxHeight) setSize(evt.getComponent().getWidth(),maxHeight);
    }//GEN-LAST:event_formComponentResized

    private void chSavePasswStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chSavePasswStateChanged
        setOKEnabled();
    }//GEN-LAST:event_chSavePasswStateChanged

    private void chKerberosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chKerberosActionPerformed
        txUser.setEnabled(!chKerberos.isSelected());
        txPassword.setEnabled(!chKerberos.isSelected());
        chSavePassw.setEnabled(!chKerberos.isSelected());
        setOKEnabled();
    }//GEN-LAST:event_chKerberosActionPerformed

    private void chReadOnlyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chReadOnlyActionPerformed
        setOKEnabled();
    }//GEN-LAST:event_chReadOnlyActionPerformed

    private void btnOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenActionPerformed
        JFileChooser foDialog=new JFileChooser();
        foDialog.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("SQL Files","sql"));
        foDialog.setCurrentDirectory(new File(txSQLScript.getText()).getParentFile());
        if(foDialog.showDialog(this,"Select File")==JFileChooser.APPROVE_OPTION) txSQLScript.setText(foDialog.getSelectedFile().getAbsolutePath());
    }//GEN-LAST:event_btnOpenActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JButton btnOpen;
    private javax.swing.JCheckBox chKerberos;
    private javax.swing.JCheckBox chReadOnly;
    private javax.swing.JCheckBox chSavePassw;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JTextField txClassName;
    private javax.swing.JTextField txConnName;
    private javax.swing.JTextField txConnectionString;
    private javax.swing.JTextField txDriver;
    private javax.swing.JPasswordField txPassword;
    private javax.swing.JTextField txSQLScript;
    private javax.swing.JTextField txUser;
    // End of variables declaration//GEN-END:variables
}
