package ru.ovs.jsqlmonitor;

import java.util.ArrayList;

public class Table {
    
    static public class Row extends ArrayList<Object> {
        Integer color;
    }
    
    private class Field {
        private String name;
        private Class type;
        
        private Field(String aName,Class aType) {
            name=aName;
            type=aType;
        }
    }
    
    private final ArrayList<Field> header;
    private final ArrayList<Row> data;    
    
    public Table() {
        header=new ArrayList();
        data=new ArrayList();
    }
    
    private Field getColumn(int Column) {
        return header.get(Column);
    }
    
    void updateTable(Table source,boolean updateHeader) {
        if (updateHeader) {
            header.clear();
            for (int i=0;i<source.getColumnCount();i++) header.add(source.getColumn(i));
            data.clear();
        }
        else {
            for (int i=getRowCount();i>source.getRowCount();i--) data.remove(i-1);
        }
        
        for (int i=0;i<source.getRowCount();i++) {
            Row row;
            
            if (getRowCount()-1<i) {
                row=new Row();
                data.add(row);
                for (int j=0;j<source.getColumnCount();j++) row.add(source.getVal(i,j));
            }
            else {
                row=data.get(i);
                for (int j=0;j<source.getColumnCount();j++) row.set(j,source.getVal(i,j));
            }
            
            row.color=source.getColor(i);
        }
    }
    
    public boolean headerIsEmpty() {
        return header.isEmpty();
    }
    
    public void clearHeader() {
        header.clear();
    }
    
    public void clearData() {
        data.clear();
    }
    
    public void clearAll() {
        header.clear();
        data.clear();
    }
    
    public void addColumn(String aName,String aClassName) {
        Class lcClass;
        try {
            lcClass=Class.forName(aClassName);
        }
        catch(ClassNotFoundException exc) {
            lcClass=Object.class;
        }
            
        header.add(new Field(aName,lcClass));
    }
    
    public void addRow(Row aRow) {
        data.add(aRow);
    }
    
    public int getRowCount() {
        return data.size();
    }
    
    public int getColumnCount() {
        return header.size();
    }
    
    public String getColumnName(int Column) {
        return header.get(Column).name;
    }
    
    public Class getColumnType(int Column) {
        return header.get(Column).type;
    }
    
    public Object getVal(int rec,int col) {
        return data.get(rec).get(col);
    }
    
    public ArrayList<Field> getHeader() {
        return header;
    }
    
    public Integer getColor(int rec) {
        return data.get(rec).color;
    }
}
