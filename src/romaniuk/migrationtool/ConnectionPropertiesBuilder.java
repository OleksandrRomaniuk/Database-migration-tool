package romaniuk.migrationtool;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionPropertiesBuilder {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    private static ConnectionPropertiesBuilder instance = null;
    Document doc = null;

    ;

    private ConnectionPropertiesBuilder() {
        setFile();
    }

    public static ConnectionPropertiesBuilder getInstance() {
        if (instance == null)
            instance = new ConnectionPropertiesBuilder();

        return instance;
    }

    public void addElement(String dbName, String dbType, String username, String password) {
        if (!checkIfExist(dbName))
            if (dbName != null)
                addElements(dbName, dbType, username, password);
            else
                logger.log(Level.WARNING, "Can not add a new connection property. Please choose the db.");
        else {
            logger.log(Level.WARNING, "Can not add a new connection property. The database with such name already exists.");
        }
    }

    public void deleteElement(String dbName) {
        if (checkIfExist(dbName)) {

            File file = new File("DBproperties.xml");

            DocumentBuilder documentBuilder = null;
            Document document = null;

            try {
                documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                document = documentBuilder.parse(file);

            } catch (ParserConfigurationException | SAXException | IOException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }

            NodeList connections = document.getElementsByTagName("Connection");

            for (int i = 0; i < connections.getLength(); i++) {
                Node connection = connections.item(i);
                String con = connection.getAttributes().getNamedItem("ConnectionName").getNodeValue();
                if (con.equals(dbName)) {
                    connection.getParentNode().removeChild(connection);
                    //document.getDocumentElement().removeChild(connection);
                    break;
                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = null;

            try {
                transformer = transformerFactory.newTransformer();
            } catch (TransformerConfigurationException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(file);

            try {
                transformer.transform(source, result);
            } catch (TransformerException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }
        } else {
            logger.log(Level.WARNING, "The DB property does not exist.");
        }
    }

    private void addElements(String dbName, String dbType, String username, String password) {
        String urls = null;
        String drivers = null;
        String usernames = null;
        String passwords = null;

        if (dbType == "mysql") {
            urls = "jdbc:mysql://localhost:3306/" + dbName + "?allowPublicKeyRetrieval=true&useSSL=false";
            drivers = "com.mysql.cj.jdbc.Driver";
            usernames = username;
            passwords = password;
            addNewElement(dbName, dbType, urls, drivers, usernames, passwords);
        } else if (dbType == "postgresql") {
            urls = "jdbc:postgresql://localhost:5432/" + dbName + "?allowPublicKeyRetrieval=true&useSSL=false";
            drivers = "org.postgresql.Driver";
            usernames = username;
            passwords = password;
            addNewElement(dbName, dbType, urls, drivers, usernames, passwords);
        } else {
            logger.log(Level.WARNING, "Can not add a new connection property. The type of the DTB is not supported.");
        }
    }

    private void addNewElement(String dbName, String dbType, String urls, String drivers, String usernames, String passwords) {
        String filepath = "DBproperties.xml";
        File file = new File("DBproperties.xml");
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        Document doc = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();

            if (file.length() != 0) {
                doc = docBuilder.parse(filepath);
            } else
                doc = docBuilder.newDocument();

        } catch (SAXException | IOException | ParserConfigurationException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }

        Node connections = doc.getDocumentElement();

        Element connection = doc.createElement("Connection");
        connections.appendChild(connection);
        Attr conName = doc.createAttribute("ConnectionName");
        conName.setValue(dbName);
        connection.setAttributeNode(conName);

        Element url = doc.createElement("url");
        url.appendChild(doc.createTextNode(urls));
        connection.appendChild(url);
        Attr urlAtr = doc.createAttribute("url");
        urlAtr.setValue("url");
        url.setAttributeNode(urlAtr);

        Element driver = doc.createElement("driver");
        driver.appendChild(doc.createTextNode(drivers));
        connection.appendChild(driver);
        Attr dtbType = doc.createAttribute("DatabaseType");
        dtbType.setValue(dbType);
        driver.setAttributeNode(dtbType);

        Element password = doc.createElement("password");
        password.appendChild(doc.createTextNode(passwords));
        connection.appendChild(password);
        Attr passAtr = doc.createAttribute("password");
        passAtr.setValue("password");
        password.setAttributeNode(passAtr);

        Element username = doc.createElement("username");
        username.appendChild(doc.createTextNode(usernames));
        connection.appendChild(username);
        Attr usernameAtr = doc.createAttribute("username");
        usernameAtr.setValue("username");
        username.setAttributeNode(usernameAtr);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;

        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filepath));

        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    private void setFile() {
        File file = new File("DBproperties.xml");
        if (!file.exists()) {
            setNewFile();
        }
    }

    private void setNewFile() {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;

        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        Document doc = builder.newDocument();

        Element connections = doc.createElement("Connections");
        doc.appendChild(connections);

        DOMSource domSource = new DOMSource(doc);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = tf.newTransformer();
        } catch (TransformerConfigurationException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
                "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        File file = new File("DBproperties.xml");
        StreamResult result = new StreamResult(file);

        try {
            transformer.transform(domSource, result);
        } catch (TransformerException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    public boolean checkIfExist(String connectionName) {
        ArrayList<String> conList = getConnectionsList();
        for (String conName : conList) {
            if (conName.equals(connectionName))
                return true;
        }
        return false;

    }

    public ArrayList<String> getConnectionsList() {
        ArrayList<String> conList = new ArrayList();
        File file = new File("DBproperties.xml");

        DocumentBuilder documentBuilder = null;
        Document document = null;

        if (file.exists()) {
            try {
                documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                document = documentBuilder.parse(file);

            } catch (ParserConfigurationException | SAXException | IOException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }


            Node root = document.getDocumentElement();

            NodeList connections = root.getChildNodes();

            for (int i = 0; i < connections.getLength(); i++) {
                Node connection = connections.item(i);
                String con = connection.getAttributes().getNamedItem("ConnectionName").getNodeValue();
                conList.add(con);
            }
        }

        return conList;
    }
}