package ru.ovs.jsqlmonitor;

public class JSQLMonitor {
    final static String DRIVERS_FILE_NAME     = "drivers.xml";
    final static String CONNECTIONS_FILE_NAME = "connections.xml";
    final static String APPLICATION_NAME      = "jSQLMonitor";
    final static String SETTINGS_NODE_NAME    = "jsqlmonitor";
    final static int    MAX_COL_WIDTH         = 500;
    final static long   IDLE_TIME             = 10;
    
    static MainForm mainForm;
    static java.util.ArrayList<Driver> drvList;
    
    static javax.swing.ImageIcon getIcon(String iconFile) {
        javax.swing.ImageIcon result=null;
        
        try {
            result=new javax.swing.ImageIcon(JSQLMonitor.class.getResource("resources/"+iconFile));
        }
        catch(Exception exc){}
        
        return result;
    }
    
    public static void main(String[] args) { 
        drvList=new java.util.ArrayList<>();
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() { 
                mainForm=new MainForm();
                mainForm.setVisible(true);        
                XMLProc.loadDriverList();        
                XMLProc.loadConnections();
                mainForm.setFirstSelected();
            } 
        });
    }
}
