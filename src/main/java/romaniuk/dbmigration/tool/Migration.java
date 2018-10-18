package romaniuk.dbmigration.tool;


import romaniuk.dbmigration.Main;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Migration {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    private static Migration instance;

    private Migration() {
    }

    public static Migration getInstance() {
        if (instance == null)
            instance = new Migration();

        return instance;
    }

    public boolean migrate(Connection SourceConnection, Connection TargetConnection) {
        boolean result = false;
        try {
            Locale.setDefault(Locale.ENGLISH);

            String DTBtypeTarget = DTBtype.getInstance().getDTBtype(TargetConnection);
            String DTBtypeSource = DTBtype.getInstance().getDTBtype(SourceConnection);

            String[] tableListSource = TableList.getInstance().getTableList(SourceConnection, DTBtypeSource);
            String[] tableListTarget = TableList.getInstance().getTableList(TargetConnection, DTBtypeTarget);

            ForeignKeys.getInstance().enableForeignKeys(TargetConnection, false, DTBtypeTarget, tableListTarget);
            Data.getInstance().copyData(SourceConnection, TargetConnection, DTBtypeSource, DTBtypeTarget, tableListSource, tableListTarget);
            ForeignKeys.getInstance().enableForeignKeys(TargetConnection, true, DTBtypeTarget, tableListTarget);
            result = true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        try {
            SourceConnection.close();
            TargetConnection.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }

        return result;
    }
}
