package romaniuk.dbmigration.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import romaniuk.dbmigration.tool.ConnectToDTB;
import romaniuk.dbmigration.tool.ConnectionPropertiesBuilder;
import romaniuk.dbmigration.tool.Migration;
import romaniuk.dbmigration.popupwindows.AlertWindow;

import java.net.URL;
import java.util.ResourceBundle;

public class MigrationToolController implements Initializable {
    @FXML
    private ComboBox<String> cbxSourceMigration;
    @FXML
    private ComboBox<String> cbxTargetMigration;
    @FXML
    private Button btnCancelMigration;
    @FXML
    private Button btnSubmitMigration;
    private ObservableList<String> connections = null;

    @FXML
    private void btnSubmitMigrationSetOnMouseClicked() {
        if (cbxSourceMigration.getValue() == null || cbxSourceMigration.getValue().equals(""))
            AlertWindow.getInstance().launchAlertWindow("Source DB", "Please choose the source connection");
        else if (cbxTargetMigration.getValue() == null || cbxTargetMigration.getValue().equals(""))
            AlertWindow.getInstance().launchAlertWindow("Target DB", "Please choose the target connection");
        else {
            if (Migration.getInstance().migrate(
                    ConnectToDTB.getInstance().getConnection(cbxSourceMigration.getValue().toString()),
                    ConnectToDTB.getInstance().getConnection(cbxTargetMigration.getValue().toString())
            )) {
                AlertWindow.getInstance().launchAlertWindow("Migration of data", "Migration has been successfully completed.");
            }
        }
    }

    @FXML
    private void btnCancelMigrationSetOnMouseClicked() {
        Stage stage = (Stage) btnCancelMigration.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cbxSourceMigrationSetOnMouseClicked() {
        connections = FXCollections.observableArrayList(ConnectionPropertiesBuilder.getInstance().getConnectionsList());
        cbxSourceMigration.setItems(connections);
    }

    @FXML
    private void cbxTargetMigrationSetOnMouseClicked() {
        connections = FXCollections.observableArrayList(ConnectionPropertiesBuilder.getInstance().getConnectionsList());
        cbxTargetMigration.setItems(connections);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connections = FXCollections.observableArrayList(ConnectionPropertiesBuilder.getInstance().getConnectionsList());
        cbxSourceMigration.setItems(connections);
        cbxTargetMigration.setItems(connections);
    }
}
