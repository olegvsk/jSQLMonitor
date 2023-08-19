package ru.ovs.jsqlmonitor;

import static ru.ovs.jsqlmonitor.JSQLMonitor.*;
import java.awt.Frame;
import java.util.prefs.Preferences;

class Settings {
    private Preferences store;
    
    public int     width;
    public int     height;
    public int     vcwidth;
    public int     vcheight;
    public String  theme;
    public boolean fbold;
    public int     dtformat;
    
    void save() {
        if ((mainForm.getExtendedState() & java.awt.Frame.MAXIMIZED_BOTH) != java.awt.Frame.MAXIMIZED_BOTH) {
            store.putInt("width",mainForm.getWidth());
            store.putInt("height",mainForm.getHeight());
        }
        store.putInt("vcwidth",vcwidth);
        store.putInt("vcheight",vcheight);
        store.put("theme",mainForm.getCurrentLaFName());        
        store.putBoolean("fbold",(boolean)javax.swing.UIManager.get("swing.boldMetal"));
        store.putInt("dtformat",TableRenderer.renderer.getDTFormat());
    }
    
    Settings() {
        store=Preferences.userRoot().node(SETTINGS_NODE_NAME);
        
        width=store.getInt("width",0);
        height=store.getInt("height",0);
        vcwidth=store.getInt("vcwidth",300);
        vcheight=store.getInt("vcheight",100);
        theme=store.get("theme","");
        fbold=store.getBoolean("fbold",true);
        dtformat=store.getInt("dtformat",0);
    }    
}
