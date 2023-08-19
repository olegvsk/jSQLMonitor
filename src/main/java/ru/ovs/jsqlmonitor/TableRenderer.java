package ru.ovs.jsqlmonitor;

import java.time.format.DateTimeFormatter;

class TableRenderer extends javax.swing.table.DefaultTableCellRenderer {
    static TableRenderer renderer=new TableRenderer();
    private final DateTimeFormatter formatter=DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private final javax.swing.border.Border border1=javax.swing.BorderFactory.createMatteBorder(2,0,2,0,java.awt.Color.BLUE);
    private final javax.swing.border.Border border2=javax.swing.BorderFactory.createMatteBorder(2,1,2,1,java.awt.Color.BLUE);
    private int dtFormat=0;
    
    void setDTFormat(int AdtFormat) {
        dtFormat=AdtFormat;
        if (dtFormat<0 | dtFormat>1) dtFormat=0;
    }
    
    int getDTFormat() {
        return dtFormat;
    }
    
    @Override
    public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column) {
     
        javax.swing.JLabel lb=(javax.swing.JLabel)super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
        
        Integer lcIntColor=((TableDataModel)table.getModel()).getColor(table.convertRowIndexToModel(row));
        java.awt.Color lcColor=lcIntColor==null ? table.getBackground() : new java.awt.Color(lcIntColor);

        if (isSelected) if (column==table.getSelectedColumn()) lb.setBorder(border2); else lb.setBorder(border1);
            
        if (isSelected && column==table.getSelectedColumn()) {
            lb.setForeground(lcColor.brighter().brighter()); 
            lb.setBackground(lcColor.darker().darker());
        }
        else {
            lb.setForeground(table.getForeground());
            lb.setBackground(lcColor);
        }
                
        if (value instanceof java.sql.Timestamp && dtFormat!=0 && value!=null) 
            lb.setText(((java.sql.Timestamp)value).toLocalDateTime().format(formatter));
        
        if (value instanceof java.math.BigDecimal ||
            value instanceof Byte                 ||    
            value instanceof Short                ||    
            value instanceof Integer              ||    
            value instanceof Long                 ||    
            value instanceof Float                ||    
            value instanceof Double) 
            lb.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        else
        if (value instanceof Boolean            ||
            value instanceof java.sql.Timestamp ||
            value instanceof java.sql.Date      ||
            value instanceof java.sql.Time) 
            lb.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        else
            lb.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        
        return lb;
    }
}
