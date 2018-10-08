package romaniuk.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import romaniuk.migrationtool.ConnectToDTB;
import romaniuk.migrationtool.ConnectionPropertiesBuilder;
import romaniuk.migrationtool.ToXML;
import romaniuk.popupwindows.AlertWindow;

import java.net.URL;
import java.util.ResourceBundle;

public class DownloadToXMLController implements Initializable {
    @FXML
    private ComboBox cbxDownloadToXML;
    @FXML
    private TextArea txtDownloadToXML;
    @FXML
    private Button btnSubmitDownLoadToXML;
    @FXML
    private Button btnCancelDownloadToXML;

    @FXML
    private void btnCancelDownloadToXMLSetOnMouseClicked() {
        Stage stage = (Stage) btnCancelDownloadToXML.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void btnSubmitDownLoadToXMLSetOnMouseClicked() {
        if (cbxDownloadToXML.valueProperty().getValue() == null)
            AlertWindow.getInstance().launchAlertWindow("Choose DB", "Please choose the DB.");
        else if (txtDownloadToXML.getText() == null || txtDownloadToXML.getText().equals("")) {
            AlertWindow.getInstance().launchAlertWindow("Name of the DB", "Please enter the name of DB.");
        } else {
            ToXML.getInstance().fromTableToXML(ConnectToDTB.getInstance().getConnection(cbxDownloadToXML.valueProperty().getValue().toString()), txtDownloadToXML.getText());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbxDownloadToXML.setItems(FXCollections.observableArrayList(ConnectionPropertiesBuilder.getInstance().getConnectionsList()));
    }
}
