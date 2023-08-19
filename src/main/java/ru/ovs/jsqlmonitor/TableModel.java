package ru.ovs.jsqlmonitor;

import java.util.Arrays;
import javax.swing.table.AbstractTableModel;

class TableDataModel extends AbstractTableModel {
    private final Table table;
    
    TableDataModel(Table aTable) {
        table=aTable;
    }
       
    @Override
    public int getRowCount() {
        return table.getRowCount();
    }
    
    @Override
    public int getColumnCount() {
        return table.getColumnCount();
    }
    
    @Override
    public Class<?> getColumnClass(int column) {
        return table.getColumnType(column);
    }
    
    @Override
    public String getColumnName(int column) {
        return table.getColumnName(column);
    }

    @Override
    public Object getValueAt(int row, int column)
    {
        return table.getVal(row,column);
    }
    
    public String[] getSortedColmnsList() {
        java.util.HashSet<String> lcList=new java.util.HashSet<String>();
        for (int i=0;i<getColumnCount();i++) lcList.add(getColumnName(i));
        
        String[] result=new String[lcList.size()];
        result=lcList.toArray(result);
        Arrays.sort(result);
        return result;
    }
    
    public int getColumnByName(String name) {
        int result=-1;
        for (int i=0;i<getColumnCount();i++) if (getColumnName(i).equals(name)) {
            result=i;
            break;
        }
        return result;
    }
    
    public Integer getColor(int row) {
        return table.getColor(row);
    }
}
