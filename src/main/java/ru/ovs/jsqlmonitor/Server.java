package ru.ovs.jsqlmonitor;

import static ru.ovs.jsqlmonitor.JSQLMonitor.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.awt.Cursor;
import java.util.Properties;
import java.util.ArrayList;

enum ServerState {Disconnected,Connected,Error,NoDriver}

public class Server {
    String drvName;
    String name;
    String user;
    String connString;
    String sqlScript;
    char[] password;
    boolean kerberos;
    boolean readOnly;    
    Driver driver;
    ServerState state;
    final ArrayList<ArrayList<Integer>> columns;
    int sortColumn;
    int fltCol;
    String fltText;
    
    private Connection connection;
    private PreparedStatement statement;
    private String curUser;
            
    boolean passwSaved() {return password!=null;}
    boolean connected() {return state==ServerState.Connected;}
    
    String columnsToString() {
        try {
            java.io.ByteArrayOutputStream stream=new java.io.ByteArrayOutputStream();        
            new java.io.ObjectOutputStream(stream).writeObject(columns);
            return java.util.Base64.getEncoder().encodeToString(stream.toByteArray());
        } 
        catch (Exception exc) {
            return "";
        }        
    }
    
    void stringToColumns(String base64) {
        try {
            java.io.ByteArrayInputStream stream=new java.io.ByteArrayInputStream(java.util.Base64.getDecoder().decode(base64));        
            columns.addAll((ArrayList<ArrayList<Integer>>)new java.io.ObjectInputStream(stream).readObject());
        } 
        catch (Exception exc) {}        
    }
    
    void prepare() {
        if (connect()) {
            try {
                statement=connection.prepareStatement(Utils.getStringFromFile(sqlScript));
            }
            catch(Exception exc) {
                Utils.ShowError("Unable to load query text.\nError: "+exc.getMessage());
                disconnect();
                state=ServerState.Error;
            }        
        }
    }
    
    boolean connect() {        
        if (state==ServerState.Connected) return true;
        
        Properties prop=new Properties();
        prop.put("ApplicationName",APPLICATION_NAME);
        
        if (kerberos) {
            prop.put("integratedSecurity","true");
            curUser=System.getProperty("user.name");
        }
        else {
            LoginParam loginParam=new LoginParam(user,password);        
            if (!passwSaved() && !Utils.getLoginParam(loginParam)) return false;
            prop.put("user",loginParam.getUser());
            prop.put("password",loginParam.getStrPassword());
            loginParam.setPassword(null);
            curUser=loginParam.getUser();
        }

        java.awt.Window window=Utils.getActiveWindow();
        window.setCursor(new Cursor(Cursor.WAIT_CURSOR));
               
        try {
            connection=driver.getSQLDriver().connect(connString,prop);
            if (connection==null) throw new Exception("Invalid connection string.");
            
            state=ServerState.Connected;
            if (readOnly) setReadOnly(); 
            
            return true;
        } 
        catch (Exception exc) {
            state=ServerState.Error;
            Utils.ShowError("An error occurred while connecting to "+name+".\nException: "+exc.getClass().getTypeName()+"\nMessage: "+exc.getMessage());
        }
        catch (java.lang.Error exc) {
            state=ServerState.Error;
            Utils.ShowError("Virtual Machine Error: "+exc.getClass().getTypeName()+"\nMessage: "+exc.getMessage());
        }
        finally {
            window.setCursor(null);
        }
        return false;
    }
    
    void disconnect() {
        if (state!=ServerState.Connected) return;
        
        try {
            connection.close();
            state=ServerState.Disconnected;
        } 
        catch (SQLException exc) {
            state=ServerState.Error;
            Utils.ShowError("Unable to close connection.\nError: "+exc.getMessage());
        }
        connection=null;
    }
    
    private void setReadOnly() {
        try {
            connection.setReadOnly(true);
        }
        catch(SQLException exc) {
            Utils.ShowWarning("Unable to set read-only connection.\nError: "+exc.getMessage());
        }        
    }
    
    PreparedStatement getStatement() {return statement;}
    
    String getConnectionInfo() {
        String result="";
            
        try {
            if (connection!=null) {
                String catalog=connection.getCatalog();
                String schema=connection.getSchema();
                if ("".equals(catalog)) catalog="<empty>";
                if ("".equals(schema)) schema="<empty>";

                result=curUser+"@"+catalog+'.'+schema;
            }
        } 
        catch (SQLException ex) {}
        
        return result;
    }
    
    Server(String aDrvName,String aName,String aUser,char[] aPassword,boolean aKerberos,String aConnString,boolean aReadOnly,String aSqlScript) {
        drvName    = Utils.noNull(aDrvName);
        name       = Utils.noNull(aName);
        user       = Utils.noNull(aUser);
        connString = Utils.noNull(aConnString);
        password   = aPassword;
        kerberos   = aKerberos;
        readOnly   = aReadOnly;
        sqlScript  = Utils.noNull(aSqlScript);

        driver=Utils.findDrv(drvName);
        
        if (driver==null)
            state=ServerState.NoDriver;
        else
        if (Utils.isBlank(driver.drvClass))
            state=ServerState.NoDriver;
        else
            state=ServerState.Disconnected;    
        
        columns=new ArrayList();
        sortColumn=0;
        fltCol=0;
        fltText="";
    }    
}

class ServerTransferable implements Transferable {
    final static DataFlavor SERVER_FLAVOR=new DataFlavor(Server.class,Server.class.getSimpleName());
    private Server server;

    public ServerTransferable(Server aServer) {
        server=aServer;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {SERVER_FLAVOR};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return SERVER_FLAVOR.equals(flavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws java.awt.datatransfer.UnsupportedFlavorException,java.io.IOException {
        if (flavor.equals(SERVER_FLAVOR))
            return server;
        else
            return null;
    }
}
