package ru.ovs.jsqlmonitor;

class PeriodComboBox extends javax.swing.JComboBox {
    static final Integer[] values = { 0,      100,     200,     500,     1000,   2000,   5000,   10000,   30000,   60000,  120000, 300000, 600000,  1800000, 3600000};
    static final String[]  labels = {"nonstop","100 ms","200 ms","500 ms","1 sec","2 sec","5 sec","10 sec","30 sec","1 min","2 min","5 min","10 min","30 min","1 hour"};
    
    int getValue() {
        int idx=getSelectedIndex();
        return idx>=0 ? values[idx] : 0;
    }
    
    PeriodComboBox() {
        super(labels);
        setSelectedIndex(7);
        setMaximumRowCount(20);
    }
}

class PeriodRenderer extends javax.swing.JLabel implements javax.swing.ListCellRenderer<String> {
    static String PeriodToString(int period) {
        if (period==0) return "0 sec";
        String result="";
        if (period/3600!=0) result=String.valueOf(period/3600)+ " hour";
        if ((period%3600)/60!=0) result+=" "+String.valueOf((period%3600)/60)+ " min";
        if (period%60!=0) result+=" "+String.valueOf(period%60)+ " sec";
        return result.strip();
    }

    @Override
    public java.awt.Component getListCellRendererComponent(javax.swing.JList aList,String aPeriod,int index,boolean isSelected,boolean cellHasFocus) {
        setText(aPeriod);

        if (isSelected) {
            setBackground(aList.getSelectionBackground());
            setForeground(aList.getSelectionForeground());
        } 
        else {
            setBackground(aList.getBackground());
            setForeground(aList.getForeground());
        }
        setOpaque(true);
        return this;
    }
}
    

