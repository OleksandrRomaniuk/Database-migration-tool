package romaniuk.dbmigration.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AlertWindowController {
    @FXML
    private Label lblAlert;
    @FXML
    private Button btnAlert;

    @FXML
    private void btnAlertSetOnMouseClicked() {
        Stage stage = (Stage) btnAlert.getScene().getWindow();
        stage.close();
    }

    public void initData(String message) {
        lblAlert.setText(message);
    }
}
