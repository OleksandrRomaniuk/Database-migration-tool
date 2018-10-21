package romaniuk.dbmigration.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import romaniuk.dbmigration.tool.ConnectToDTB;
import romaniuk.dbmigration.tool.CreateDTB;
import romaniuk.dbmigration.popupwindows.AlertWindow;
import romaniuk.dbmigration.popupwindows.PasswordAndLogin;
import romaniuk.dbmigration.popupwindows.PasswordWindow;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateNewDBController implements Initializable {
    @FXML
    private ComboBox cbxTypeCrDB;
    @FXML
    private TextArea tfNameCrDB;
    @FXML
    private Button btnCancelCrDR;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbxTypeCrDB.setItems(FXCollections.observableArrayList("mysql", "postgresql"));
    }

    @FXML
    private void btnSubmitCrDBsetOnMouseClicked() {
        if (tfNameCrDB.getText().equals("")) {
            AlertWindow.getInstance().launchAlertWindow("Name of DB", "Please enter the name of DB.");
        } else if (cbxTypeCrDB.valueProperty().getValue() == null)
            AlertWindow.getInstance().launchAlertWindow("Type of DB", "Please choose the type of DB.");
        else if (CreateDTB.getInstance().checkIfDTBExist(tfNameCrDB.getText().toLowerCase(), cbxTypeCrDB.valueProperty().getValue().toString()))
            AlertWindow.getInstance().launchAlertWindow("Name of DB", "The DB with this name already exists.");
        else {
            if (cbxTypeCrDB.valueProperty().getValue().toString().equals("mysql")) {
                new PasswordWindow(new CreateNewDBControllerPasswordAndLogin("mysql"), "mysql");
            } else if (cbxTypeCrDB.valueProperty().getValue().toString().equals("postgresql")) {
                new PasswordWindow(new CreateNewDBControllerPasswordAndLogin("postgresql"), "postgresql");
            } else
                AlertWindow.getInstance().launchAlertWindow("Type of DB", "The type of DB is not supported.");

        }
    }

    @FXML
    private void btnCancelCrDRSetOnMouseClicked() {
        Stage stage = (Stage) btnCancelCrDR.getScene().getWindow();
        stage.close();
    }

    private class CreateNewDBControllerPasswordAndLogin implements
            PasswordAndLogin {
        private String login;
        private String password;
        private String DBtype;

        public CreateNewDBControllerPasswordAndLogin(String DBtype) {
            this.DBtype = DBtype;
        }

        @Override
        public void setLoginAndPassword(String login, String password) {
            this.login = login;
            this.password = password;
        }

        @Override
        public void handle() {
            CreateDTB.getInstance().createNewDTB(
                    tfNameCrDB.getText().toLowerCase(),
                    ConnectToDTB.getInstance().getDefaultConnection(DBtype), DBtype, login, password);
            MainUIController.refreshMigrListViews();

        }
    }
}