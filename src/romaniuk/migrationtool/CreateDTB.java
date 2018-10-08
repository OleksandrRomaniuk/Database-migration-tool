package romaniuk.migrationtool;

import romaniuk.popupwindows.AlertWindow;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateDTB {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    private static CreateDTB instance;

    private CreateDTB() {
    }

    public static CreateDTB getInstance() {
        if (instance == null)
            instance = new CreateDTB();
        return instance;
    }

    public void createNewDTB(String dbName, Connection connection, String dbType, String username, String password) {


        if (dbType.equals("mysql") || dbType.equals("postgresql")) {

            if (!checkIfDTBExist(dbName, dbType)) {
                if (creatingNewDTB(dbName, ConnectToDTB.getInstance().getDefaultConnection(dbType), dbType, username, password))
                    AlertWindow.getInstance().launchAlertWindow("DB created", "The database has been successfully created.");
                else
                    AlertWindow.getInstance().launchAlertWindow("DB created", "The database was not created.");
            } else {
                AlertWindow.getInstance().launchAlertWindow("DB exists", "The database with such name already exists");
            }
        } else {
            logger.log(Level.WARNING, "We have not created a solution to create a DTB for this type. We appologize for that.");
        }
    }

    private boolean creatingNewDTB(String dbName, Connection connection, String DTBtype, String username, String password) {
        Boolean result = false;
        Statement stmt = null;

        try {
            stmt = connection.createStatement();
            stmt.executeUpdate("CREATE DATABASE " + dbName);
            ConnectionPropertiesBuilder.getInstance().addElement(dbName, DTBtype, username, password);
            result = true;
        } catch (SQLException e) {
            result = false;
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }
        }

        return result;
    }

    public void removeDB(String dbName, Connection connection, String dbType) {
        if (dbType.equals("mysql") || dbType.equals("postgresql")) {
            if (checkIfDTBExist(dbName, dbType)) {
                if (removeDTB(dbName, connection, dbType))
                    AlertWindow.getInstance().launchAlertWindow("DB removed", "The database has been successfully removed.");
                else
                    AlertWindow.getInstance().launchAlertWindow("DB removed", "The database was not removed.");
            } else {
                AlertWindow.getInstance().launchAlertWindow("DB does not exist", "The database with such name does not exist");
            }
        } else {
            logger.log(Level.SEVERE, "We have not created a solution to create a DTB for this type. We appologize for that.");
        }
    }

    private boolean removeDTB(String dbName, Connection connection, String DTBtype) {
        boolean result = false;
        Statement stmt = null;

        try {
            stmt = connection.createStatement();
            stmt.executeUpdate("DROP DATABASE " + dbName);
            ConnectionPropertiesBuilder.getInstance().deleteElement(dbName);
            result = true;
        } catch (SQLException e) {
            result = false;
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            try {
                if (stmt != null)
                    stmt.close();

            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }
        }
        return result;
    }

    public boolean checkIfDTBExist(String dbName, String dbType) {
        boolean result = false;

        ArrayList<String> dbList = ConnectToDTB.getInstance().getListOfDBs(dbType);

        for (String name : dbList) {
            if (name.equals(dbName)) {
                result = true;
                break;
            }
        }
        return result;
    }
}