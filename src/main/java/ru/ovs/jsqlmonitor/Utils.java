package ru.ovs.jsqlmonitor;

import static ru.ovs.jsqlmonitor.JSQLMonitor.*;

enum ModalResult {OK,Cancel}

class Utils {    
    static boolean drvExists(String aDrvName) {
        for (Driver lcDrv:drvList) if (lcDrv.drvName.equals(aDrvName)) return true;
        return false;
    }
    
    static Driver findDrv(String aDrvName) {
        for (Driver lcDrv:drvList) if (lcDrv.drvName.equals(aDrvName)) return lcDrv;
        return null;
    }
    
    static String noNull(String aStr) {
        return aStr == null ? "" : aStr;
    }
    
    static boolean isBlank(String aStr) {
        return (noNull(aStr).trim().length()==0);
    }
    
    static void wipeCharArray(char[] aCharArray) {
        if (aCharArray!=null) java.util.Arrays.fill(aCharArray,'\u0000');
    }
    
    static boolean getLoginParam(LoginParam aLoginParam) {
        LoginForm loginForm=new LoginForm(aLoginParam);
        loginForm.setLocationRelativeTo(mainForm);
        loginForm.setVisible(true);
        return loginForm.modalResult==ModalResult.OK;
    }
    
    static java.awt.Window getActiveWindow() {
        java.awt.Window window=javax.swing.FocusManager.getCurrentManager().getActiveWindow();
        if (window instanceof LoginForm) window=window.getOwner();
        return window==null ? mainForm : window;
    }
    
    static String getStringFromFile(String filePath) throws java.io.IOException {
        return new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filePath).toAbsolutePath()),java.nio.charset.StandardCharsets.UTF_8);
    }
    
    static void ShowError(String message) {javax.swing.JOptionPane.showMessageDialog(getActiveWindow(),message,"Error",javax.swing.JOptionPane.ERROR_MESSAGE);}   
    static void ShowWarning(String message) {javax.swing.JOptionPane.showMessageDialog(getActiveWindow(),message,"Warning",javax.swing.JOptionPane.WARNING_MESSAGE);}   
    static void ShowMessage(String message) {javax.swing.JOptionPane.showMessageDialog(getActiveWindow(),message,"Information",javax.swing.JOptionPane.INFORMATION_MESSAGE);}
}

