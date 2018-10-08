package romaniuk.controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import romaniuk.migrationtool.*;
import romaniuk.popupwindows.AlertWindow;
import romaniuk.popupwindows.PasswordAndLogin;
import romaniuk.popupwindows.PasswordWindow;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainUIController implements Initializable {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    private static HashMap<ListView<String>, String> viewList = new HashMap();
    @FXML
    private ComboBox<String> cbxTreeTab;
    @FXML
    private TreeView<String> treeTreeTab;
    @FXML
    private TextArea textAreaSelectTreeTab;
    @FXML
    private TextArea textAreaPrintChildren;
    @FXML
    private TextArea infoText;
    @FXML
    private ListView<String> mysqlDBList;
    @FXML
    private ListView<String> dbPropertiesList;
    @FXML
    private ListView<String> postgresqlDBList;

    public static void refreshMigrListViews() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(() ->
                {
                    for (Map.Entry<ListView<String>, String> entry : viewList.entrySet()) {
                        ListView<String> key = entry.getKey();
                        String value = entry.getValue();
                        if (value.equals("mysql")) {
                            key.setItems(FXCollections.observableArrayList(ConnectToDTB.getInstance().getListOfDBs("mysql")));
                        } else if (value.equals("postgresql")) {
                            key.setItems(FXCollections.observableArrayList(ConnectToDTB.getInstance().getListOfDBs("postgresql")));
                        } else
                            key.setItems(FXCollections.observableArrayList(ConnectionPropertiesBuilder.getInstance().getConnectionsList()));
                    }
                    ;
                });
            }
        }).start();
    }

    @FXML
    private void btnPropmysqlSetOnMouseClicked() {
        new PasswordWindow(new AddNewPropMainControllerPasswordAndLogin(mysqlDBList.getSelectionModel().getSelectedItem().toLowerCase(), "mysql"), "mysql");
        dbPropertiesList.setItems(FXCollections.observableArrayList(ConnectionPropertiesBuilder.getInstance().getConnectionsList()));
    }

    @FXML
    private void btnProppostrgesqlSetOnMouseClicked() {
        new PasswordWindow(new AddNewPropMainControllerPasswordAndLogin(mysqlDBList.getSelectionModel().getSelectedItem().toLowerCase(), "postgresql"), "postgresql");
        dbPropertiesList.setItems(FXCollections.observableArrayList(ConnectionPropertiesBuilder.getInstance().getConnectionsList()));
    }

    @FXML
    private void btnDelmysqlSetOnMouseClicked() {
        CreateDTB.getInstance().removeDB(mysqlDBList.getSelectionModel().getSelectedItem(), ConnectToDTB.getInstance().getDefaultConnection("mysql"), "mysql");
        mysqlDBList.setItems(FXCollections.observableArrayList(FXCollections.observableArrayList(ConnectToDTB.getInstance().getListOfDBs("mysql"))));
        dbPropertiesList.setItems(FXCollections.observableArrayList(ConnectionPropertiesBuilder.getInstance().getConnectionsList()));
        refreshMigrListViews();
    }

    @FXML
    private void btnDelpostgresqlSetOnMouseClicked() {
        CreateDTB.getInstance().removeDB(postgresqlDBList.getSelectionModel().getSelectedItem(), ConnectToDTB.getInstance().getDefaultConnection("postgresql"), "postgresql");
        postgresqlDBList.setItems(FXCollections.observableArrayList(FXCollections.observableArrayList(ConnectToDTB.getInstance().getListOfDBs("postgresql"))));
        dbPropertiesList.setItems(FXCollections.observableArrayList(ConnectionPropertiesBuilder.getInstance().getConnectionsList()));
        refreshMigrListViews();
    }

    @FXML
    private void btnDelpropSetOnMouseClicked() {
        ConnectionPropertiesBuilder.getInstance().deleteElement(dbPropertiesList.getSelectionModel().getSelectedItem());
        dbPropertiesList.setItems(FXCollections.observableArrayList(ConnectionPropertiesBuilder.getInstance().getConnectionsList()));
        refreshMigrListViews();
    }

    @FXML
    private void setOnMouseClickedBtnDownloadToXMLMigrTab() {
        Stage window = new Stage();

        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/romaniuk/ui/DownloadToXML.fxml"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        window.setScene(new Scene(root, 500, 200));
        window.setTitle("Migration tool");
        window.show();
    }

    @FXML
    private void setOnMouseClickedBtnMigrToolMigrTab() {
        Stage window = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/romaniuk/ui/MigrationTool.fxml"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        window.setScene(new Scene(root, 500, 200));
        window.setTitle("Migration tool");
        window.show();
    }

    @FXML
    private void setOnMouseClickedBtnReplicationOfTablesMigrTab() {
        Stage window = new Stage();

        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/romaniuk/ui/ReplicationOfTables.fxml"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        window.setScene(new Scene(root, 500, 200));
        window.setTitle("Replication of tables");
        window.show();
    }

    @FXML
    private void setOnMouseClickedBtnCreateDBMigrTab() {
        try {
            Stage window = new Stage();
            Parent root = null;
            root = FXMLLoader.load(getClass().getResource("/romaniuk/ui/CreateNewDB.fxml"));
            window.setScene(new Scene(root, 500, 200));
            window.setTitle("Set up a new DB");
            window.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    @FXML
    private void setOnMouseClickedCbxTreeTab() {
        cbxTreeTab.setItems(FXCollections.observableArrayList(ConnectionPropertiesBuilder.getInstance().getConnectionsList()));
    }

    private void setOnActionTextAreaSelectTreeTab() {
        textAreaPrintChildren.clear();

        printChildren(
                Trees.getInstance().findItems(textAreaSelectTreeTab.getText(),
                        Trees.getInstance().getRootItem()).toString());
    }

    private void printChildren(String item) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(() ->
                {
                    textAreaPrintChildren.clear();
                    textAreaPrintChildren.appendText(item + "\n");
                });
            }
        }).start();
    }

    @FXML
    private void showTree() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Platform.runLater(() ->
                {
                    treeTreeTab.setRoot(Trees.getInstance().tree(ConnectToDTB.getInstance().getConnection(cbxTreeTab.valueProperty().getValue())));

                });
            }
        }).start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        cbxTreeTab.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null && !newValue.equals("")) {
                    if (newValue.equals("defaultconnectionmysql") || newValue.equals("defaultconnectionpostgresql")) {
                        AlertWindow.getInstance().launchAlertWindow("Default connection tree", "Can not work out the tree for the default connection.");
                    } else {
                        showTree();
                    }
                }
            }
        });

        textAreaSelectTreeTab.textProperty().addListener(
                Event ->
                {
                    if (cbxTreeTab.valueProperty().getValue() != null && !textAreaSelectTreeTab.getText().equals("")) {

                        setOnActionTextAreaSelectTreeTab();
                    } else {
                        textAreaPrintChildren.clear();
                    }
                });

        viewList.put(mysqlDBList, "mysql");
        viewList.put(postgresqlDBList, "postgresql");
        viewList.put(dbPropertiesList, "dbproperties");
        dbPropertiesList.setItems(FXCollections.observableArrayList(ConnectionPropertiesBuilder.getInstance().getConnectionsList()));

        if (ConnectionPropertiesBuilder.getInstance().checkIfExist("defaultconnectionmysql")) {
            mysqlDBList.setItems(FXCollections.observableArrayList(ConnectToDTB.getInstance().getListOfDBs("mysql")));

            if (ConnectionPropertiesBuilder.getInstance().checkIfExist("defaultconnectionpostgresql")) {
                postgresqlDBList.setItems(FXCollections.observableArrayList(ConnectToDTB.getInstance().getListOfDBs("postgresql")));
            } else
                new PasswordWindow(new CreateNewDBControllerPasswordAndLogin("defaultconnectionpostgresql", "postgresql"), "postgresql");
        } else
            new PasswordWindow(new CreateNewDBControllerPasswordAndLogin("defaultconnectionmysql", "mysql"), "mysql");

    }

    public void textUpdate(String text) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                infoText.appendText(text);
            }
        }).start();
    }

    private class CreateNewDBControllerPasswordAndLogin implements
            PasswordAndLogin {
        private String login;
        private String password;
        private String DBtype;
        private String DBName;

        public CreateNewDBControllerPasswordAndLogin(String DBName, String DBtype) {
            this.DBtype = DBtype;
            this.DBName = DBName;
        }

        @Override
        public void setLoginAndPassword(String login, String password) {
            this.login = login;
            this.password = password;
        }

        @Override
        public void handle() {
            ConnectionPropertiesBuilder.getInstance().addElement(DBName, DBtype, login, password);
            refreshMigrListViews();

        }
    }

    private class AddNewPropMainControllerPasswordAndLogin implements
            PasswordAndLogin {
        private String login;
        private String password;
        private String DBtype;
        private String DBname;

        public AddNewPropMainControllerPasswordAndLogin(String DBname, String DBtype) {
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
            refreshMigrListViews();
        }
    }
}
