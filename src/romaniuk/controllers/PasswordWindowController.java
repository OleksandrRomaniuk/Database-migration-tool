package romaniuk.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import romaniuk.popupwindows.AlertWindow;
import romaniuk.popupwindows.PasswordAndLogin;
import romaniuk.popupwindows.PasswordWindow;


public class PasswordWindowController {
    @FXML
    private Button btnSubmit;
    @FXML
    private Button btnCancelPasswordWindow;
    @FXML
    private TextArea txtLogin;
    @FXML
    private PasswordField fieldPassword;
    @FXML
    private Label lblAlert;

    private PasswordAndLogin passwordAndLoginInstance;
    private PasswordWindow passwordWindowInstance;
    private String login;
    private String password;

    @FXML
    private void btnSubmitSetOnMouseClicked() {
        if (txtLogin.getText() != null && !txtLogin.getText().equals("")) {
            login = txtLogin.getText();
            if (fieldPassword.getText() != null && !fieldPassword.getText().equals("")) {

                password = fieldPassword.getText();
                Stage stage = (Stage) btnSubmit.getScene().getWindow();
                stage.close();
                if (passwordWindowInstance.checkPasswordAndLogin(login, password)) {
                    passwordAndLoginInstance.setLoginAndPassword(login, password);
                    passwordAndLoginInstance.handle();
                }
            } else {
                AlertWindow.getInstance().launchAlertWindow("Enter the password", "Please enter the password");
            }
        } else {
            AlertWindow.getInstance().launchAlertWindow("Enter the login", "Please enter the login");
        }
    }

    public void setObject(PasswordAndLogin passwordAndLoginInstance, PasswordWindow passwordWindowInstance) {
        this.passwordAndLoginInstance = passwordAndLoginInstance;
        this.passwordWindowInstance = passwordWindowInstance;
    }

    public void setLabel(String label) {
        lblAlert.setText(label);
    }

    @FXML
    private void btnCancelSetOnMouseClicked(MouseEvent mouseEvent) {
        Stage stage = (Stage) btnCancelPasswordWindow.getScene().getWindow();
        stage.close();
    }
}