package ru.ovs.jsqlmonitor;

import static ru.ovs.jsqlmonitor.JSQLMonitor.*;
import static javax.swing.SwingUtilities.invokeLater;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class Worker extends Thread {
    private boolean terminated;
    private PreparedStatement statement;
    private PreparedStatement NewStatement;
    private int period;
    private Table buffer;
    private boolean needUpdate;
    private boolean flReconnect;
    private boolean flError;
    private Server currentServer;
    private boolean pause;
    boolean waiting;
    
    public final Object sync=new Object();
    public final Object syncPause=new Object();
    
    Worker() {
        buffer=new Table();
    }
    
    private void delay() {try {sleep(IDLE_TIME);}catch (InterruptedException exc) {}}
        
    @Override
    public void run() {
        long curTime;
        
        while (!terminated) {
            if (pause) {
                waiting=true;
                synchronized(syncPause) {try {syncPause.wait();}catch(InterruptedException exc) {}}
            }
            waiting=false;
            
            getData();                   
            needUpdate=false;
            curTime=System.currentTimeMillis();
            do {
                if (terminated || needUpdate || pause) break;
                delay();
            }
            while (System.currentTimeMillis()-curTime<period); 
        }
    }
    
    public Table getBuffer() {
        return buffer;
    }
    
    public void terminate() {
        terminated=true;
    }
    
    public void forceUpdate() {
        needUpdate=true;
        if (pause) synchronized(syncPause) {syncPause.notify();}
    }
    
    public void setPause(boolean Apause) {
        pause=Apause;
        if (!pause) synchronized(syncPause) {syncPause.notify();}
    }
    
    void setStatement(PreparedStatement aStatement,Server aServer) {
        NewStatement=aStatement;
        currentServer=aServer;
        flError=false;
    }
    
    void setPeriod(int aPeriod) {
        period=aPeriod;
    }    

    private void getData() {        
        flReconnect=statement!=NewStatement;
        if (flReconnect) flError=false;
        if (flError) return;
        
        statement=NewStatement;
        
        if (statement==null & flReconnect) {
            synchronized(sync) {buffer.clearAll();}
            invokeLater(()->mainForm.updateMonitor(true,false,currentServer));
        }
        else 
        if (statement!=null) {
            try {
                if (!statement.execute()) throw new Exception("No data set was received from the server.");

                ResultSet rs=statement.getResultSet();

                ResultSetMetaData md=rs.getMetaData();

                synchronized(sync) {
                    if (flReconnect) {
                        buffer.clearHeader();
                        for (int i=1;i<=md.getColumnCount();i++) if (!"Color".equalsIgnoreCase(md.getColumnName(i))) buffer.addColumn(md.getColumnName(i),md.getColumnClassName(i));
                    }

                    buffer.clearData();
                    while (rs.next()) {
                        Table.Row row=new Table.Row();
                        for (int i=1;i<=md.getColumnCount();i++) {
                            if ("Color".equalsIgnoreCase(md.getColumnName(i)))
                                try {row.color=(Integer)rs.getObject(i);} catch(Exception exc) {}
                            else
                                row.add(rs.getObject(i));
                        }
                        buffer.addRow(row);
                    }
                }
        
                invokeLater(()->mainForm.updateMonitor(flReconnect,false,currentServer));
            }
            catch (Exception exc) {
                flError=true;
                synchronized(sync) {
                    buffer.clearAll();
                    buffer.addColumn("ERROR","String");
                    Table.Row row=new Table.Row();
                    row.add(exc.getMessage());                    
                    buffer.addRow(row);
                }
                invokeLater(()->mainForm.updateMonitor(true,true,currentServer));
            }
        }
    }
}
