package ru.ovs.jsqlmonitor;

import javax.swing.UIManager;
import java.util.ArrayList;

class LaFList {
    private class LaF {
        private String name;
        private UIManager.LookAndFeelInfo lafInfo;
        private String theme;
        
        private LaF(String Aname,UIManager.LookAndFeelInfo AlafInfo,String Atheme) {
            name=Aname;
            lafInfo=AlafInfo;
            theme=Atheme;
        }        
    }
    
    private ArrayList<LaF> lafs;
    
    public int getLaFsCount() {
        return lafs.size();
    }
    
    public String getLaFName(int idx) {
        return idx>=0 & idx<getLaFsCount() ? lafs.get(idx).name : "";
    }
    
    public String getLaFClassName(int idx) {
        return idx>=0 & idx<getLaFsCount() ? lafs.get(idx).lafInfo.getClassName() : "";
    }
    
    public String getLaFTheme(int idx) {
        return idx>=0 & idx<getLaFsCount() ? lafs.get(idx).theme : "";
    }
    
    public int getLafIdx(String Aname) {
        int result=-1;
        for (int i=0;i<lafs.size();i++) if (lafs.get(i).name.equals(Aname)) {
            result=i;
            break;
        }
        return result;
    }
    
    LaFList() {
        lafs=new ArrayList();
     
        for (UIManager.LookAndFeelInfo li:UIManager.getInstalledLookAndFeels()) {
            if(li.getName().equals("Metal")) {
                lafs.add(0,new LaF(li.getName()+" Ocean",li,"Ocean"));
                lafs.add(1,new LaF(li.getName()+" Steel",li,"Steel"));
            }
            else {
                lafs.add(new LaF(li.getName(),li,null));
            }
        }
    }
}
