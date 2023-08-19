package ru.ovs.jsqlmonitor;

import static ru.ovs.jsqlmonitor.JSQLMonitor.*;
import static javax.swing.Action.SMALL_ICON;
import static javax.swing.Action.ACCELERATOR_KEY;
import static javax.swing.Action.SHORT_DESCRIPTION;
import static javax.swing.KeyStroke.getKeyStroke;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

class ActSrvNew extends AbstractAction {
    public ActSrvNew() {
        putValue(SMALL_ICON,getIcon("srvnew.png"));
        putValue(NAME,"New connection");
        putValue(SHORT_DESCRIPTION,"New connection");
        putValue(ACCELERATOR_KEY,getKeyStroke(java.awt.event.KeyEvent.VK_D,java.awt.event.KeyEvent.CTRL_DOWN_MASK));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       ConnNewForm connNewForm=new ConnNewForm();
       connNewForm.setLocationRelativeTo(mainForm);
       connNewForm.setVisible(true);
    }
 }

class ActSrvEdit extends AbstractAction {
    public ActSrvEdit() {
        putValue(SMALL_ICON,getIcon("srvedit.png"));
        putValue(NAME,"Edit connection");
        putValue(SHORT_DESCRIPTION,"Edit connection");
        putValue(ACCELERATOR_KEY,getKeyStroke(java.awt.event.KeyEvent.VK_ENTER,java.awt.event.KeyEvent.ALT_DOWN_MASK));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       Server server=mainForm.getCurrentServer();
       if (server==null) return;
       ConnEditForm connEditForm=new ConnEditForm(server);
       connEditForm.setLocationRelativeTo(mainForm);
       connEditForm.setVisible(true);
    }
 }

class ActSrvDelete extends AbstractAction {
    public ActSrvDelete() {
        putValue(SMALL_ICON,getIcon("srvdelete.png"));
        putValue(NAME,"Delete connection");
        putValue(SHORT_DESCRIPTION,"Delete connection");
        putValue(ACCELERATOR_KEY,getKeyStroke(java.awt.event.KeyEvent.VK_DELETE,0));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Server server=mainForm.getCurrentServer();
        String[] options={"Yes","No"};
        if (server!=null && javax.swing.JOptionPane.showOptionDialog(mainForm,String.format("Delete connection \"%s\"?",server.name),"Delete connection",javax.swing.JOptionPane.YES_NO_OPTION,javax.swing.JOptionPane.QUESTION_MESSAGE,null,options,"Yes")==javax.swing.JOptionPane.YES_OPTION) {
            mainForm.srvListModel.removeElement(server);
            mainForm.setFirstSelected();
            XMLProc.saveConnections();
        }
    }
 }

class ActConnect extends AbstractAction {
    public ActConnect() {
        putValue(SMALL_ICON,getIcon("connect.png"));
        putValue(NAME,"Connect");
        putValue(SHORT_DESCRIPTION,"Connect");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       Server server=mainForm.getCurrentServer();
       if (server==null) return;
       server.prepare();
       mainForm.srvListModel.update(mainForm.getCurrentIndex());
       mainForm.monitoring(server);
    }
 }

class ActDisconnect extends AbstractAction {
    public ActDisconnect() {
        putValue(SMALL_ICON,getIcon("disconnect.png"));
        putValue(NAME,"Disconnect");
        putValue(SHORT_DESCRIPTION,"Disconnect");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       Server server=mainForm.getCurrentServer();
       if (server==null) return;
       server.disconnect();
       mainForm.srvListModel.update(mainForm.getCurrentIndex());
       mainForm.monitoring(server);
    }
 }

