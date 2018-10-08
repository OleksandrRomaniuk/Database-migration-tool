package romaniuk.migrationtool;

import java.sql.Connection;

public class Data {

    private static Data instance;

    private Data() {
    }

    public static Data getInstance() {
        if (instance == null)
            instance = new Data();

        return instance;
    }

    public void copyData(Connection sourceConnection, Connection targetConnection, String DTBtypeSource, String DTBtypeTarget, String[] tablesSource, String[] tablesTarget) {
        for (String tableNameSource : tablesSource) {
            boolean tableExists = false;

            for (String tableNameTarget : tablesTarget) {
                if (tableNameTarget.equals(tableNameSource)) {
                    tableExists = true;
                    break;
                }
            }

            if (tableExists)
                Table.getInstance().copyTable(tableNameSource, sourceConnection, targetConnection, DTBtypeSource);
        }
    }
}