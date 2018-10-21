package romaniuk.dbmigration.tool;


import romaniuk.dbmigration.Main;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Table {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    private static Table instance;

    private Table() {
    }

    public static Table getInstance() {
        if (instance == null)
            instance = new Table();

        return instance;
    }

    public void copyTable(String tableName, Connection sourceConnection, Connection targetConnection, String DTBtype) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        PreparedStatement inst = null;

        try {
            if (!DTBtype.equals("mysql")) {
                logger.log(Level.SEVERE, new Exception("No solution for this type of DTB").getMessage());
                throw new Exception("No solution for this type of DTB");
            } else
                tableName = tableName.toLowerCase();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());

        }

        String sql = "select * from " + tableName;

        try {
            ps = sourceConnection.prepareStatement(sql);

            rs = ps.executeQuery();

            ResultSetMetaData metadata = ps.getMetaData();
            int colCount = metadata.getColumnCount();

            StringBuilder sqlInsert = new StringBuilder("INSERT INTO " + tableName + "(");

            for (int c = 1; c <= colCount; c++) {
                sqlInsert.append(((c == 1 ? "" : ",") + metadata.getColumnName(c).toLowerCase()));
            }

            sqlInsert.append(") VALUES (");

            for (int c = 1; c <= colCount; c++) {
                sqlInsert.append(((c == 1 ? "" : ",") + "?"));
            }

            sqlInsert.append(")");

            //int row = 0;
            Object[] line = new Object[colCount];
            inst = targetConnection.prepareStatement(sqlInsert.toString());
            while (rs.next()) {
                for (int c = 1; c <= colCount; c++) {
                    //Object ceilvalue = rs.getObject(c);
                    line[c - 1] = rs.getObject(c);
                }
                insertRow(inst, line);


            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            try {
                inst.close();
                rs.close();
                ps.close();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }
        }
    }

    private void insertRow(PreparedStatement ps, Object[] line) {
        try {
            for (int n = 1; n <= line.length; n++) {

                ps.setObject(n, line[n - 1]);
            }

            ps.execute();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }
}
