package romaniuk.migrationtool;

import javafx.scene.control.TreeItem;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Trees {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    private static Trees trees = null;
    private TreeItem<String> rootItem = null;


    private Trees() {
    }

    public static Trees getInstance() {
        if (trees == null) {
            trees = new Trees();
            return trees;
        } else return trees;
    }

    public TreeItem<String> getRootItem() {
        return rootItem;
    }

    public TreeItem<String> tree(Connection connection) {
        DatabaseMetaData dbMeta = null;
        ResultSet rsTables = null;
        ResultSet rsCol = null;

        try {
            dbMeta = connection.getMetaData();
            String[] types = {"TABLE"};
            rsTables = dbMeta.getTables(connection.getCatalog(), null, "%", types);

            rootItem = new TreeItem<String>("Schema");
            rootItem.setExpanded(true);

            while (rsTables.next()) {
                TreeItem<String> itemTable = new TreeItem<String>(rsTables.getString(3));
                rootItem.getChildren().add(itemTable);
                rsCol = dbMeta.getColumns(connection.getCatalog(), null, rsTables.getString(3), null);

                while (rsCol.next()) {
                    TreeItem<String> itemCol = new TreeItem<String>(rsCol.getString(4));
                    itemTable.getChildren().add(itemCol);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }

        return rootItem;
    }

    public StringBuilder findItems(String value, TreeItem<String> rootItem) {
        StringBuilder sb = new StringBuilder();
        sb.delete(0, sb.length());
        findItem(value, rootItem, sb);
        return sb;
    }

    private StringBuilder findItem(String value, TreeItem<String> rootItem, StringBuilder sb) {


        if (value == null || value.trim().equals("")) {
            logger.log(Level.SEVERE, "Item cannot be empty.");
        } else if (rootItem.getChildren().size() > 0) {
            for (TreeItem<String> child : rootItem.getChildren()) {
                if (child.getValue().toString().contains(value))
                    sb.append(child.getValue().toString() + "\n");

                if (child.getChildren().size() > 0) {
                    findItem(value, child, sb);
                }
            }
        }
        return sb;
    }
}