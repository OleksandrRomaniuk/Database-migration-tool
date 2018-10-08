package romaniuk.migrationtool;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import romaniuk.popupwindows.AlertWindow;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ToXML {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    private static ToXML instance = null;
    Document doc = null;

    private ToXML() {
    }

    ;

    public static ToXML getInstance() {
        if (instance == null)
            instance = new ToXML();

        return instance;
    }

    public void fromTableToXML(Connection connection, String fileName) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        DOMSource domSource = null;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }

        Document doc = builder.newDocument();

        Element tables = doc.createElement("Tables");
        doc.appendChild(tables);

        String[] tableList = TableList.getInstance().getTableList(connection, DTBtype.getInstance().getDTBtype(connection));

        for (int k = 0; k < tableList.length; k++) {
            Element table = doc.createElement("Table");
            tables.appendChild(table);

            Element tableName = null;
            int colCount = 0;
            ResultSetMetaData rsmd = null;

            try {
                pstmt = connection.prepareStatement("SELECT * FROM " + tableList[k]);
                rs = pstmt.executeQuery();
                rsmd = rs.getMetaData();// to retrieve table names, columns and other
                colCount = rsmd.getColumnCount();
                tableName = doc.createElement("TableName");
                tableName.appendChild(doc.createTextNode(rsmd.getTableName(1)));


                table.appendChild(tableName);

                Element structure = doc.createElement("TableStructure");
                table.appendChild(structure);

                Element col = null;

                for (int i = 1; i <= colCount; i++) {
                    col = doc.createElement("Column" + i);
                    table.appendChild(col);
                    Element columnNode = doc.createElement("ColumnName");

                    columnNode.appendChild(doc.createTextNode(rsmd.getColumnName(i)));
                    col.appendChild(columnNode);

                    Element typeNode = doc.createElement("ColumnType");
                    typeNode.appendChild(doc.createTextNode(String.valueOf((rsmd
                            .getColumnTypeName(i)))));
                    col.appendChild(typeNode);
                    Element lengthNode = doc.createElement("Length");
                    lengthNode.appendChild(doc.createTextNode(String.valueOf((rsmd
                            .getPrecision(i)))));
                    col.appendChild(lengthNode);

                    structure.appendChild(col);
                }

                Element tableData = doc.createElement("TableData");
                table.appendChild(tableData);

                int j = 0;
                while (rs.next()) {
                    Element row = doc.createElement("Item" + (++j));
                    table.appendChild(row);
                    for (int i = 1; i <= colCount; i++) {
                        String columnName = rsmd.getColumnName(i);
                        Object value = rs.getObject(i);
                        Element node = doc.createElement(columnName);
                        node.appendChild(doc.createTextNode((value != null) ? value
                                .toString() : ""));
                        row.appendChild(node);
                    }
                    tableData.appendChild(row);
                }

            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }
        }

        domSource = new DOMSource(doc);
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

        File file = new File(fileName + ".xml");
        StreamResult result = new StreamResult(file);

        try {
            transformer.transform(domSource, result);
            AlertWindow.getInstance().launchAlertWindow("Download to XML", "The download to XML has been completed.");
        } catch (TransformerException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }
}