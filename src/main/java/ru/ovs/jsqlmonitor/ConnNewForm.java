package ru.ovs.jsqlmonitor;
        
import static ru.ovs.jsqlmonitor.JSQLMonitor.*;
import static ru.ovs.jsqlmonitor.Utils.*;
import javax.swing.JFileChooser;
import java.io.File;

public class ConnNewForm extends javax.swing.JDialog {
    private int maxHeight;
    
    private javax.swing.text.NumberFormatter getPortFormatter() {
        java.text.NumberFormat numberFormat=java.text.NumberFormat.getInstance();
        numberFormat.setGroupingUsed(false);
        javax.swing.text.NumberFormatter portFormatter=new javax.swing.text.NumberFormatter(numberFormat);
        portFormatter.setValueClass(Integer.class);
        portFormatter.setMaximum(65535);
        portFormatter.setAllowsInvalid(false);
        portFormatter.setCommitsOnValidEdit(true);
        return portFormatter;
    }
    
    private String getDrvName() {
        Driver driver=(Driver) cbDriver.getSelectedItem();
        return driver==null ? "" : driver.drvName;
    }
    
    private String getDrvScript() {
        Driver driver=(Driver) cbDriver.getSelectedItem();
        return driver==null ? "" : driver.sqlScript;
    }
    
    private boolean needShowKerberos() {
        Driver driver=(Driver) cbDriver.getSelectedItem();
        return driver!=null && driver.canKerberosAuth();
    }
    
    private char[] getPassword() {
        return chSavePassw.isSelected() ? txPassword.getPassword() : null;
    }

    private void updateConnString() {                              
        Driver driver=(Driver)cbDriver.getSelectedItem();
        txConnectionString.setText(driver==null ? "" : driver.getConnectionString(txHost.getText(),txPort.getText(),txDatabase.getText()));
        txConnectionString.setCaretPosition(0);
    }     
    
    private void updateConnName() {
        setOKEnabled();
    }
    
    private void addServer() {
        mainForm.srvListModel.add(mainForm.srvListModel.getSize(),new Server(getDrvName(),txConnName.getText(),txUser.getText(),getPassword(),chKerberos.isSelected(),txConnectionString.getText(),chReadOnly.isSelected(),txSQLScript.getText()));
        mainForm.setLastSelected();
        XMLProc.saveConnections();
    }
    
    private void setOKEnabled() {
        btnOK.setEnabled(cbDriver.getSelectedItem()!=null & !Utils.isBlank(txConnName.getText()));
    }
    
   public ConnNewForm() {
        super(mainForm,"New connection",true);
        initComponents();
        
        cbDriver.setModel(new javax.swing.DefaultComboBoxModel<Driver>(drvList.toArray(new Driver[0])));
        
        txHost.getDocument().addDocumentListener((ShortDocumentListener)     ()->updateConnString());
        txPort.getDocument().addDocumentListener((ShortDocumentListener)     ()->updateConnString());
        txDatabase.getDocument().addDocumentListener((ShortDocumentListener) ()->updateConnString());
        txConnName.getDocument().addDocumentListener((ShortDocumentListener) ()->updateConnName());
        
        getRootPane().registerKeyboardAction(evt->dispose(),javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE,0),javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        getRootPane().setDefaultButton(btnOK);
        cbDriver.setSelectedIndex(-1);
        
        setMinimumSize(new java.awt.Dimension(getWidth(),getHeight()));
        maxHeight=getHeight();
    }
  
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        cbDriver = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        txHost = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txDatabase = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txUser = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txPassword = new javax.swing.JPasswordField();
        btnCancel = new javax.swing.JButton();
        btnOK = new javax.swing.JButton();
        txPort = new javax.swing.JFormattedTextField(getPortFormatter());
        jLabel7 = new javax.swing.JLabel();
        txConnectionString = new javax.swing.JTextField();
        chSavePassw = new javax.swing.JCheckBox();
        txConnName = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        chKerberos = new javax.swing.JCheckBox();
        btnTest = new javax.swing.JButton();
        chReadOnly = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        txSQLScript = new javax.swing.JTextField();
        btnOpen = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(new java.awt.Dimension(0, 0));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        jLabel1.setText("JDBC driver:");

        cbDriver.setMaximumRowCount(20);
        cbDriver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbDriverActionPerformed(evt);
            }
        });

        jLabel2.setText("Host:");

        jLabel3.setText("Port:");

        jLabel4.setText("Database:");

        jLabel5.setText("User name:");

        jLabel6.setText("Password:");

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

        txPort.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel7.setText("Connection string");

        chSavePassw.setText("Save");
        chSavePassw.setToolTipText("The password will be saved in plain text");

        jLabel9.setText("Name:");

        chKerberos.setText("Integrated security");
        chKerberos.setToolTipText("Only for MS SQL Server");
        chKerberos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chKerberosActionPerformed(evt);
            }
        });

        btnTest.setText("Test connection");
        btnTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTestActionPerformed(evt);
            }
        });

        chReadOnly.setSelected(true);
        chReadOnly.setText("Set read-only connection");
        chReadOnly.setToolTipText("If the jdbc driver supports this mode");

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
                        .addGap(8, 8, 8)
                        .addComponent(chReadOnly)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnTest))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel8)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txConnectionString)
                        .addGap(12, 12, 12))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txConnName, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txHost)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txPort, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(txDatabase, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(cbDriver, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(txPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                                            .addComponent(txUser))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(chKerberos)
                                            .addComponent(chSavePassw)))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnOK)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCancel))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(txSQLScript)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnOpen, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCancel, btnOK});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbDriver, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txConnName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chKerberos))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chSavePassw))
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txConnectionString, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chReadOnly)
                    .addComponent(btnTest))
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnOpen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txSQLScript, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOK)
                    .addComponent(btnCancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        addServer();        
        dispose();
    }//GEN-LAST:event_btnOKActionPerformed

    private void cbDriverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbDriverActionPerformed
        updateConnString();
        txSQLScript.setText(getDrvScript());
        txConnName.setText(getDrvName());
        txConnName.selectAll();
        txConnName.requestFocus();
        chKerberos.setSelected(false);
        chKerberos.setEnabled(needShowKerberos());
        chKerberosActionPerformed(null);
        btnTest.setEnabled(cbDriver.getSelectedItem()!=null);
        setOKEnabled();
    }//GEN-LAST:event_cbDriverActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        if (evt.getComponent().getHeight()>maxHeight) setSize(evt.getComponent().getWidth(),maxHeight);
    }//GEN-LAST:event_formComponentResized

    private void chKerberosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chKerberosActionPerformed
        txUser.setEnabled(!chKerberos.isSelected());
        txPassword.setEnabled(!chKerberos.isSelected());
        chSavePassw.setEnabled(!chKerberos.isSelected());
    }//GEN-LAST:event_chKerberosActionPerformed

    private void btnTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTestActionPerformed
        Server lcServer=new Server(getDrvName(),getDrvName(),txUser.getText(),txPassword.getPassword(),chKerberos.isSelected(),txConnectionString.getText(),chReadOnly.isSelected(),"");
        if (lcServer.state==ServerState.NoDriver)
            ShowWarning("No class name found for the selected driver.");
        else
        if (lcServer.connect()) {
            ShowMessage("Connection successfull.");
            lcServer.disconnect();
        }
    }//GEN-LAST:event_btnTestActionPerformed

    private void btnOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenActionPerformed
        JFileChooser foDialog=new JFileChooser();
        foDialog.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("SQL Files","sql"));
        foDialog.setCurrentDirectory(new File(txSQLScript.getText()).getParentFile());
        if(foDialog.showDialog(this,"Select File")==JFileChooser.APPROVE_OPTION) txSQLScript.setText(foDialog.getSelectedFile().getAbsolutePath());
    }//GEN-LAST:event_btnOpenActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JButton btnOpen;
    private javax.swing.JButton btnTest;
    private javax.swing.JComboBox<Driver> cbDriver;
    private javax.swing.JCheckBox chKerberos;
    private javax.swing.JCheckBox chReadOnly;
    private javax.swing.JCheckBox chSavePassw;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTextField txConnName;
    private javax.swing.JTextField txConnectionString;
    private javax.swing.JTextField txDatabase;
    private javax.swing.JTextField txHost;
    private javax.swing.JPasswordField txPassword;
    private javax.swing.JFormattedTextField txPort;
    private javax.swing.JTextField txSQLScript;
    private javax.swing.JTextField txUser;
    // End of variables declaration//GEN-END:variables
}
