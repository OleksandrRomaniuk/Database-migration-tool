package romaniuk.dbmigration.tool;


import romaniuk.dbmigration.Main;

import java.sql.*;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ForeignKeys {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static ForeignKeys instance;

    private ForeignKeys() {
    }

    public static ForeignKeys getInstance() {
        if (instance == null)
            instance = new ForeignKeys();

        return instance;
    }

    public void enableForeignKeys(Connection targetConnection, boolean mode, String DTBtype, String[] tableListTarget) {
        HashMap<String, String> constraints = null;
        PreparedStatement ps = null;
        String sql = null;

        try {
            if (DTBtype.equals("mysql")) {
                sql = "SET FOREIGN_KEY_CHECKS=" + (mode ? "1" : "0");
                ps = targetConnection.prepareStatement(sql);
                ps.execute();
            } else if (DTBtype.equals("hsqldb")) {
                sql = "SET REFERENTIAL_INTEGRITY " + (mode ? "TRUE" : "FALSE");
                ps = targetConnection.prepareStatement(sql);
                ps.execute();
            } else if (DTBtype.equals("postgresql")) {
                for (String tableName : tableListTarget) {
                    sql = "alter table " + tableName + " " + (mode ? "enable" : "disable") + " trigger all";
                    ps = targetConnection.prepareStatement(sql);
                    ps.execute();
                }
            } else {
                constraints = getForeignKeyConstraints(targetConnection, DTBtype);

                for (String constraintName : constraints.keySet()) {
                    String tableName = constraints.get(constraintName);

                    if (DTBtype.equals("oracle")) {
                        sql = "alter table " + tableName + " " + (mode ? "enable" : "disable") + " constraint " + constraintName;
                        ps = targetConnection.prepareStatement(sql);
                        ps.execute();
                    } else if (DTBtype.equals("sqlserver")) {
                        sql = "alter table " + tableName + " " + (mode ? "check" : "nocheck") + " constraint " + constraintName;
                        ps = targetConnection.prepareStatement(sql);
                        ps.execute();
                    } else {
                        logger.log(Level.SEVERE, "Foreign heys has not been disabled because the DTB type is not supported");
                    }
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            try {
                if (ps != null)
                    ps.close();

            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }
        }
    }

    public HashMap<String, String> getForeignKeyConstraints(Connection targetConnection, String DTBtype) {
        HashMap<String, String> constraints = new HashMap<String, String>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = null;

        if (DTBtype.equals("oracle")) {
            sql = "select table_name,constraint_name from user_constraints where constraint_type='R'";
        } else if (DTBtype.equals("sqlserver")) {
            sql = "select table_name,constraint_name from INFORMATION_SCHEMA.TABLE_CONSTRAINTS "
                    + "where constraint_type='FOREIGN KEY'";
        } else if (DTBtype.equals("mysql")) {
            sql = "select table_name,constraint_name from INFORMATION_SCHEMA.TABLE_CONSTRAINTS "
                    + "where constraint_type='FOREIGN KEY' AND constraint_schema=database()";
        } else if (DTBtype.equals("hsqldb")) {
            sql = "SELECT TABLE_NAME,CONSTRAINT_NAME FROM INFORMATION_SCHEMA.SYSTEM_TABLE_CONSTRAINTS "
                    + "where constraint_type='FOREIGN KEY'";
        }

        try {
            ps = targetConnection.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                constraints.put(rs.getString(2), rs.getString(1));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }

        }
        return constraints;
    }

    public String setForeignKeyConstraints(String[] tablesListSource, String[] tablesListTarget, Connection sourceConnection, Connection targetConnection) {
        PreparedStatement psmt = null;
        try {
            DatabaseMetaData metaSource = sourceConnection.getMetaData();
            DatabaseMetaData metaTarget = targetConnection.getMetaData();

            for (int i = 0; i < tablesListSource.length; i++) {
                boolean constraintExists = false;
                boolean tableExists = false;
                ResultSet foreignKeysSource = metaSource.getImportedKeys(sourceConnection.getCatalog(), null, tablesListSource[i].toString());

                for (String tableNameTarget : tablesListTarget) {
                    if (tableNameTarget.equals(tablesListSource[i].toString())) ;
                    tableExists = true;
                    break;
                }

                if (tableExists) {
                    ResultSet foreignKeysTarget = metaTarget.getImportedKeys(targetConnection.getCatalog(), null, tablesListSource[i].toString());

                    while (foreignKeysSource.next()) {


                        while (foreignKeysTarget.next()) {
                            if (foreignKeysTarget.equals(foreignKeysSource)) {
                                constraintExists = true;
                                break;
                            }
                        }
                    }

                    if (constraintExists) {
                        String fkTableName = foreignKeysSource.getString("FKTABLE_NAME");
                        String fkColumnName = foreignKeysSource.getString("FKCOLUMN_NAME");
                        String pkTableName = foreignKeysSource.getString("PKTABLE_NAME");
                        String pkColumnName = foreignKeysSource.getString("PKCOLUMN_NAME");
                        StringBuilder constraint_name = new StringBuilder(fkTableName + "_" + fkColumnName + "_" + pkTableName + "_" + pkColumnName);

                        StringBuilder sqlQuery = new StringBuilder("ALTER TABLE " + fkTableName + " \r\n" +
                                "ADD CONSTRAINT " + constraint_name + " FOREIGN KEY (" + fkColumnName + ") REFERENCES " + pkTableName + " (" + pkColumnName + ");");

                        psmt = targetConnection.prepareStatement(sqlQuery.toString());
                        psmt.execute();
                    }
                }
            }


        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }

        return null;

    }

    public void setPrimaryKeys(String[] tableList, Connection sourceConnection, Connection targetConnection) {

        PreparedStatement psmt = null;

        try {
            DatabaseMetaData meta = sourceConnection.getMetaData();
            ResultSet foreignKeys = null;

            for (int i = 0; i < tableList.length; i++) {
                foreignKeys = meta.getPrimaryKeys(sourceConnection.getCatalog(), null, tableList[i].toString());

                StringBuilder sqlQuery = new StringBuilder("ALTER TABLE " + tableList[i].toString() + " \r\n" +
                        "ADD PRIMARY KEY (");

                int j = 0;

                while (foreignKeys.next()) {
                    if (checkPrimaryKeys(tableList, foreignKeys.getString(4), targetConnection)) {
                        j++;
                        if (j > 1) sqlQuery.append(", ");
                        String pkColumnName = foreignKeys.getString(4);
                        sqlQuery.append(pkColumnName);
                    }
                }

                sqlQuery.append(")");

                if (j > 0) {
                    psmt = targetConnection.prepareStatement(sqlQuery.toString());
                    psmt.execute();
                } else {
                    logger.log(Level.WARNING, "No new primary keys to add.");
                }
            }


        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    private boolean checkPrimaryKeys(String[] tableList, String constraint, Connection targetConnection) {
        DatabaseMetaData meta = null;
        boolean result = false;

        try {
            meta = targetConnection.getMetaData();

            ResultSet foreignKeys = null;


            for (int i = 0; i < tableList.length; i++) {
                foreignKeys = meta.getPrimaryKeys(targetConnection.getCatalog(), null, tableList[i].toString());

                while (foreignKeys.next()) {
                    if (foreignKeys.getString(4).equals(constraint)) {
                        result = true;
                    } else continue;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return result;
    }
}