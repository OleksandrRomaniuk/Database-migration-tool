package romaniuk.dbmigration.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import romaniuk.dbmigration.controllers.MainUIController;
import romaniuk.dbmigration.popupwindows.PasswordAndLogin;
import romaniuk.dbmigration.popupwindows.PasswordWindow;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ConnectToDTB {

    private static final Logger logger = LoggerFactory.getLogger(ConnectToDTB.class);
    private static ConnectToDTB instance;
    private List<Connection> conList = new ArrayList<>();

    private ConnectToDTB() {
    }

    public static ConnectToDTB getInstance() {
        if (instance == null)
            instance = new ConnectToDTB();

        return instance;
    }

    public void closeAll() {
        for (Connection con : conList) {
            try {
                if (con != null || !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
    }

    public Connection getDefaultConnection(String DBtype) {

        Connection connection = null;
        switch (DBtype) {
            case "mysql":
                if (ConnectionPropertiesBuilder.getInstance().checkIfExist("defaultconnectionmysql")) {
                    connection = setDefaultConnection("mysql", "defaultconnectionmysql");
                    conList.add(connection);
                } else {
                    new PasswordWindow(new ConnectDefConnectionPasswordAndLogin("mysql", "defaultconnectionmysql"), "Login&password default mysql DB");
                }
                break;
            case "postgresql":
                if (ConnectionPropertiesBuilder.getInstance().checkIfExist("defaultconnectionpostgresql")) {
                    connection = setDefaultConnection("postgresql", "defaultconnectionpostgresql");
                    conList.add(connection);
                } else {
                    new PasswordWindow(new ConnectDefConnectionPasswordAndLogin("postgresql", "defaultconnectionpostgresql"), "Login&password default postgresql DB");
                }
                break;
            default:
                logger.warn("The default connection does not exist");
                break;
        }


        return connection;
    }

    public Connection getConnection(String DBname) {
        Connection connection = null;
        switch (DBname) {
            case "defaultconnectionmysql":
                connection = setDefaultConnection("mysql", "defaultconnectionmysql");
                conList.add(connection);
                break;
            case "defaultconnectionpostgresql":
                connection = setDefaultConnection("postgresql", "defaultconnectionpostgresql");
                conList.add(connection);
                break;
            default:
                connection = setConnection(DBname);
                conList.add(connection);
                break;
        }

        return connection;
    }

    private Connection setConnection(String DBname) {
        String url = null;
        String username = null;
        String password = null;
        String driver = null;

        File file = new File("DBproperties.xml");

        DocumentBuilder documentBuilder;
        Document document = null;

        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = documentBuilder.parse(file);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.error(e.getMessage());
        }

        Node root = document.getDocumentElement();

        NodeList connections = root.getChildNodes();

        for (int i = 0; i < connections.getLength(); i++) {
            Node connection = connections.item(i);

            if (connection.getAttributes().getNamedItem("ConnectionName").getNodeValue().equals(DBname)) {
                if (connection.getNodeType() != Node.TEXT_NODE) {
                    NodeList attributes = connection.getChildNodes();

                    for (int j = 0; j < attributes.getLength(); j++) {
                        Node attribute = attributes.item(j);
                        if (attribute.getNodeType() != Node.TEXT_NODE) {
                            String attributeName = attribute.toString();
                            if (attributeName.contains("url")) url = attribute.getTextContent();
                            else if (attributeName.contains("username")) username = attribute.getTextContent();
                            else if (attributeName.contains("password")) password = attribute.getTextContent();
                            else if (attributeName.contains("driver")) driver = attribute.getTextContent();
                        }
                    }
                }
            }
        }

        if (driver != null) {
            try {
                Class.forName(driver).newInstance();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                logger.error("Can not load the driver: " + e.getMessage());
            }
        } else {
            logger.error("Can not load the driver");
        }

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return connection;
    }

    private void showDrivers() {
        Enumeration<Driver> en = DriverManager.getDrivers();
        Driver dr = null;
        while (en.hasMoreElements()) {
            dr = en.nextElement();
        }
    }

    private Connection setDefaultConnection(String DBtype, String DBname) {
        String url = null;
        String username = null;
        String password = null;
        String driver = null;

        switch (DBtype) {
            case "mysql":
                url = "jdbc:mysql://localhost:3306/?allowPublicKeyRetrieval=true&useSSL=false";
                driver = "com.mysql.cj.jdbc.Driver";
                break;
            case "postgresql":
                url = "jdbc:postgresql://localhost:5432/?allowPublicKeyRetrieval=true&useSSL=false";
                driver = "org.postgresql.Driver";
                break;
            default:
                logger.warn("Can not set up the connection. The connection type is not supported.");
                break;
        }

        File file = new File("DBproperties.xml");

        DocumentBuilder documentBuilder = null;
        Document document = null;

        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = documentBuilder.parse(file);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.error(e.getMessage());
        }

        Node root = document.getDocumentElement();

        NodeList connections = root.getChildNodes();

        for (int i = 0; i < connections.getLength(); i++) {
            Node connection = connections.item(i);

            if (connection.getAttributes().getNamedItem("ConnectionName").getNodeValue().equals(DBname)) {
                if (connection.getNodeType() != Node.TEXT_NODE) {
                    NodeList attributes = connection.getChildNodes();

                    for (int j = 0; j < attributes.getLength(); j++) {
                        Node attribute = attributes.item(j);
                        if (attribute.getNodeType() != Node.TEXT_NODE) {
                            String attributeName = attribute.toString();
                            if (attributeName.contains("username")) username = attribute.getTextContent();
                            else if (attributeName.contains("password")) password = attribute.getTextContent();
                        }
                    }
                }
            }
        }

        if (driver != null) {
            try {
                Class.forName(driver).newInstance();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                logger.error("Can not load the driver: " + e.getMessage());
            }
        } else {
            logger.error("Can not load the driver");
        }

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return connection;
    }

    /*
    private Connection setDefaultConnection(String DBtype) {
        String url = null;
        String driver = null;

        if (DBtype.equals("mysql")) {
            url = "jdbc:mysql://localhost:3306/?allowPublicKeyRetrieval=true&useSSL=false";
            driver = "com.mysql.cj.jdbc.Driver";
        } else if (DBtype.equals("postgresql")) {
            url = "jdbc:postgresql://localhost:5432/?allowPublicKeyRetrieval=true&useSSL=false";
            driver = "org.postgresql.Driver";
        } else {
            logger.warn("Can not set up the connection. The connection type is not supported.");
        }

        if (driver != null) {
            try {
                Class.forName(driver).newInstance();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                logger.error("Can not load the driver: " + e.getMessage());
            }
        } else {
            logger.error("Can not load the driver");
        }

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return connection;
    }
    */

    public List<String> getListOfDBs(String DBtype) {
        List<String> dbList = new ArrayList<>();
        Connection connection = getDefaultConnection(DBtype);
        ResultSet rs = null;
        DatabaseMetaData meta = null;

        if (DBtype.equals("mysql")) {
            try {
                meta = connection.getMetaData();
                rs = meta.getCatalogs();

                while (rs.next()) {
                    dbList.add(rs.getString(1));
                }
            } catch (SQLException e) {
                logger.error(e.getMessage());
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        if (DBtype.equals("postgresql")) {
            PreparedStatement pstmt = null;
            try {
                pstmt = connection.prepareStatement("SELECT datname FROM pg_database");
            } catch (SQLException e) {
                logger.error(e.getMessage());
            } finally {
                try {
                    rs = pstmt.executeQuery();
                    while (rs.next()) {
                        dbList.add(rs.getString(1));
                    }

                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return dbList;
    }

    public void checkLoginAndPassword(String login, String password, String DBtype) throws SQLException {
        if (DBtype.equals("mysql")) {
            Connection connection = null;
            try {
                Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            } catch (InstantiationException e) {
                logger.error(e.getMessage());
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage());
            } catch (ClassNotFoundException e) {
                logger.error(e.getMessage());
            }
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/?allowPublicKeyRetrieval=true&useSSL=false", login, password);
        } else if (DBtype.equals("postgresql")) {
            Connection connection = null;
            try {
                Class.forName("org.postgresql.Driver").newInstance();
            } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
                logger.error(e.getMessage());
            }
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/?allowPublicKeyRetrieval=true&useSSL=false", login, password);
        } else {
            logger.warn("The type of DB is incorrect");
        }

    }

    private class ConnectDefConnectionPasswordAndLogin implements
            PasswordAndLogin {
        private String login;
        private String password;
        private String DBtype;
        private String DBname;

        public ConnectDefConnectionPasswordAndLogin(String DBtype, String DBname) {
            this.DBtype = DBtype;
            this.DBname = DBname;
        }

        @Override
        public void setLoginAndPassword(String login, String password) {
            this.login = login;
            this.password = password;
        }

        @Override
        public void handle() {
            ConnectionPropertiesBuilder.getInstance().addElement(DBname, DBtype, login, password);
            MainUIController.refreshMigrListViews();
        }
    }


}