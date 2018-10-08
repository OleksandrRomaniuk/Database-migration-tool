package romaniuk.migrationtool;

import java.sql.Connection;

public class DTBtype {
    private static DTBtype instance = null;

    private DTBtype() {
    }

    ;

    public static DTBtype getInstance() {
        if (instance == null)
            instance = new DTBtype();

        return instance;
    }

    public String getDTBtype(Connection connection) {

        if (connection.getClass().getCanonicalName().indexOf("oracle") > -1)
            return "oracle";

        else if (connection.getClass().getCanonicalName().indexOf("sqlserver") > -1)
            return "sqlserver";

        else if (connection.getClass().getCanonicalName().indexOf("mysql") > -1)
            return "mysql";

        else if (connection.getClass().getCanonicalName().indexOf("hsqldb") > -1)
            return "hsqldb";

        else if (connection.getClass().getCanonicalName().indexOf("postgresql") > -1)
            return "postgresql";

        else
            return "not identified";

    }
}
