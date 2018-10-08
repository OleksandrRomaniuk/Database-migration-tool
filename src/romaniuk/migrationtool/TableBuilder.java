package romaniuk.migrationtool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TableBuilder {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    private static TableBuilder instance = null;

    private TableBuilder() {
    }

    ;

    public static TableBuilder getInstance() {
        if (instance == null)
            instance = new TableBuilder();

        return instance;
    }

    public boolean replicateTables(Connection sourceConnection, Connection targetConnection) {
        boolean result = false;
        try {
            String sourceConnectionType = DTBtype.getInstance().getDTBtype(sourceConnection);
            String targetConnectionType = DTBtype.getInstance().getDTBtype(targetConnection);
            String[] tableListSource = TableList.getInstance().getTableList(sourceConnection, sourceConnectionType);
            String[] tableListTarget = TableList.getInstance().getTableList(targetConnection, targetConnectionType);

            ReplicateTables.getInstance().replicate(sourceConnection, targetConnection);
            ForeignKeys.getInstance().setPrimaryKeys(tableListSource, sourceConnection, targetConnection);
            ForeignKeys.getInstance().setForeignKeyConstraints(tableListSource, tableListTarget, sourceConnection, targetConnection);
            result = true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            try {
                if (targetConnection != null && !targetConnection.isClosed()) {
                    targetConnection.close();
                }

            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }

            try {
                if (sourceConnection != null && !sourceConnection.isClosed()) {
                    sourceConnection.close();
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }
        }
        return result;
    }
}