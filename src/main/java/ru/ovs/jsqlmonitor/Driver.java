package ru.ovs.jsqlmonitor;

import static ru.ovs.jsqlmonitor.Utils.*;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

public class Driver {
    String drvName    = "",
           drvClass   = "",
           drvJAR     = "",
           sqlScript  = "",
           connString = "";
    
    private java.sql.Driver sqlDriver;

    @Override 
    public String toString() {
        return drvName;
    }
    
    public boolean canKerberosAuth() {
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver".equals(drvClass);
    }
    
    public java.sql.Driver getSQLDriver() throws MalformedURLException,
                                                 ClassNotFoundException,
                                                 NoSuchMethodException,
                                                 InstantiationException,
                                                 IllegalAccessException,
                                                 IllegalArgumentException,
                                                 InvocationTargetException,
                                                 FileNotFoundException {
        
        if (sqlDriver!=null) return sqlDriver;
        
        File jar=new File(drvJAR);
        if (!jar.isFile()) throw new FileNotFoundException(drvJAR);
        URLClassLoader loader=new URLClassLoader(new URL[] {jar.toURI().toURL()});
        sqlDriver=(java.sql.Driver)Class.forName(drvClass,true,loader).getDeclaredConstructor().newInstance();

        return sqlDriver;
    }
    
    public String getConnectionString(String aHost,String aPort,String aDB) {
        return connString.replace("[host]",noNull(aHost).trim()).replace("[:port]",Utils.isBlank(aPort) ? "" : ":"+aPort.trim()).replace("[database]",noNull(aDB).trim());
    }
}
