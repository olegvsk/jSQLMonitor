package ru.ovs.jsqlmonitor;

import static ru.ovs.jsqlmonitor.JSQLMonitor.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

class XMLProc {
    
    static void loadDriverList() {
        drvList.clear();
            
        try {
            DocumentBuilder Builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document xml=Builder.parse(DRIVERS_FILE_NAME);
            NodeList drivers=xml.getDocumentElement().getElementsByTagName("driver");
            for (int i=0;i<drivers.getLength();i++) {
                Node node=drivers.item(i);            
                NamedNodeMap attrs=node.getAttributes();
                
                String lcDrvName="";
                if (attrs.getNamedItem("name")!=null)  lcDrvName=attrs.getNamedItem("name").getNodeValue();
                if (Utils.isBlank(lcDrvName)) continue;
                
                if (Utils.drvExists(lcDrvName)) continue;

                Driver drv=new Driver();
                drv.drvName=lcDrvName;
                if (attrs.getNamedItem("class")!=null) drv.drvClass = attrs.getNamedItem("class").getNodeValue();
                if (attrs.getNamedItem("jar")!=null)   drv.drvJAR   = attrs.getNamedItem("jar").getNodeValue();
                
                NodeList nodeChilds=node.getChildNodes();
                for (int j=0;j<nodeChilds.getLength();j++) {
                    node=nodeChilds.item(j);            
                    if ("sql".equals(node.getNodeName())) {
                        attrs=node.getAttributes();
                        if (attrs.getNamedItem("script")!=null) drv.sqlScript=attrs.getNamedItem("script").getNodeValue();
                    }
                    if ("connString".equals(node.getNodeName())) {
                        drv.connString=node.getTextContent();
                    }
                }
                
                drvList.add(drv);
            }
        }
        catch(Exception exc) {
            Utils.ShowWarning("Unable to load the list of jdbc drivers. Error description: \n"+exc.getMessage());
        }
                
    }
    
    static void saveConnections() {
        if (mainForm.srvListModel.getSize()==0) return;
        
        try {
            DocumentBuilder Builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document xml=Builder.newDocument();

            Node root=xml.createElement("connections");
            for (int i=0;i<mainForm.srvListModel.getSize();i++) {
                Server server=mainForm.srvListModel.getElementAt(i);

                Element elem;            
                Element connection=xml.createElement("connection");
                connection.setAttribute("name",server.name);
                connection.setAttribute("driver",server.drvName);
                connection.setAttribute("readOnly",Boolean.toString(server.readOnly));

                elem=xml.createElement("user");
                elem.setTextContent(server.user);
                connection.appendChild(elem);

                if (server.password!=null) {
                    elem=xml.createElement("password");
                    elem.setTextContent(String.valueOf(server.password));    
                    connection.appendChild(elem);
                }

                if (server.kerberos) {
                    elem=xml.createElement("integratedSecurity");
                    elem.setTextContent("true");    
                    connection.appendChild(elem);
                }

                elem=xml.createElement("connString");
                elem.setTextContent(server.connString);
                connection.appendChild(elem);
                
                elem=xml.createElement("sql");
                elem.setAttribute("script",server.sqlScript);
                connection.appendChild(elem);

                if (server.columns.size()!=0) {
                    elem=xml.createElement("columns");
                    if (server.sortColumn!=0) elem.setAttribute("sort",Integer.toString(server.sortColumn));
                    elem.setTextContent(server.columnsToString());
                    connection.appendChild(elem);
                }

                root.appendChild(connection);
            }            
            xml.appendChild(root);

            Transformer transformer=TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            DOMSource source=new DOMSource(xml);
            StreamResult result = new StreamResult(new java.io.File(CONNECTIONS_FILE_NAME));
            transformer.transform(source,result);
        }
        catch(Exception exc) {
            Utils.ShowWarning("Unable to save the connection list. Error description: \n"+exc.getMessage());
        }
    }

    static void loadConnections() {
        if (!new java.io.File(CONNECTIONS_FILE_NAME).exists()) return;
        
        try {
            DocumentBuilder Builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document xml=Builder.parse(CONNECTIONS_FILE_NAME);
            NodeList connections=xml.getDocumentElement().getElementsByTagName("connection");
            for (int i=0;i<connections.getLength();i++) {                
                String lcDrvName="",lcName="",lcUser="",lcSQLScript="",lcConnString="",lcColumns="",lcSort="";
                boolean lcReadOnly=false;
                boolean lcKerberos=false;
                char[] lcPassword=null;

                Node node=connections.item(i);            
                NamedNodeMap attrs=node.getAttributes();
                
                if (attrs.getNamedItem("driver")!=null)                    lcDrvName    = attrs.getNamedItem("driver").getNodeValue();
                if (attrs.getNamedItem("name")!=null)                      lcName       = attrs.getNamedItem("name").getNodeValue();
                if (attrs.getNamedItem("readOnly")!=null)                  lcReadOnly   = attrs.getNamedItem("readOnly").getNodeValue().toLowerCase().equals("true") ? true : false;
                
                NodeList nodeChilds=node.getChildNodes();
                for (int j=0;j<nodeChilds.getLength();j++) {
                    node=nodeChilds.item(j);               
                    if ("user".equals(node.getNodeName()))                 lcUser       = node.getTextContent();
                    if ("password".equals(node.getNodeName()))             lcPassword   = node.getTextContent().toCharArray();
                    if ("integratedSecurity".equals(node.getNodeName()))   lcKerberos   = node.getTextContent().toLowerCase().equals("true");
                    if ("connString".equals(node.getNodeName()))           lcConnString = node.getTextContent();

                    if ("sql".equals(node.getNodeName())) {
                        attrs=node.getAttributes();
                        if (attrs.getNamedItem("script")!=null)            lcSQLScript  = attrs.getNamedItem("script").getNodeValue();
                    }

                    if ("columns".equals(node.getNodeName())) {
                        attrs=node.getAttributes();
                        if (attrs.getNamedItem("sort")!=null)              lcSort       = attrs.getNamedItem("sort").getNodeValue();
                                                                           lcColumns    = node.getTextContent();
                    }
                }
                
                Server server=new Server(lcDrvName,lcName,lcUser,lcPassword,lcKerberos,lcConnString,lcReadOnly,lcSQLScript);
                mainForm.srvListModel.add(mainForm.srvListModel.getSize(),server);
                if (lcSort.length()!=0) try{server.sortColumn=Integer.valueOf(lcSort);}catch(Exception exc){}
                if (lcColumns.length()!=0) server.stringToColumns(lcColumns);
            }
        }
        catch(Exception exc) {
            Utils.ShowWarning("Unable to load connections. Error description: \n"+exc.getMessage());
        }
        mainForm.srvListModel.update();
    }
}

