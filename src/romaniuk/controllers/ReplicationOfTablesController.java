package romaniuk.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import romaniuk.migrationtool.ConnectToDTB;
import romaniuk.migrationtool.ConnectionPropertiesBuilder;
import romaniuk.migrationtool.Main;
import romaniuk.migrationtool.TableBuilder;
import romaniuk.popupwindows.AlertWindow;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class ReplicationOfTablesController implements Initializable {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    @FXML
    private ComboBox cbxSourceReplication;
    @FXML
    private ComboBox cbxTargetReplication;
    @FXML
    private Button btnSubmitReplication;
    @FXML
    private Button btnCancelReplication;
    private ObservableList<String> connections = null;

    @FXML
    private void cbxSourceReplicationSetOnMouseClicked() {
        connections = FXCollections.observableArrayList(ConnectionPropertiesBuilder.getInstance().getConnectionsList());
        cbxSourceReplication.setItems(connections);
    }

    @FXML
    private void cbxTargetReplicationSetOnMouseClicked() {
        connections = FXCollections.observableArrayList(ConnectionPropertiesBuilder.getInstance().getConnectionsList());
        cbxTargetReplication.setItems(connections);
    }

    @FXML
    private void btnSubmitReplicationSetOnClicked() {
        if (cbxSourceReplication.getValue() == null || cbxSourceReplication.getValue().equals(""))
            AlertWindow.getInstance().launchAlertWindow("Source DB", "Please choose the source connection");
        else if (cbxTargetReplication.getValue() == null || cbxTargetReplication.getValue().equals(""))
            AlertWindow.getInstance().launchAlertWindow("Target DB", "Please choose the target connection");
        else {
            if (TableBuilder.getInstance().replicateTables(
                    ConnectToDTB.getInstance().getConnection(cbxSourceReplication.getValue().toString()),
                    ConnectToDTB.getInstance().getConnection(cbxTargetReplication.getValue().toString())
            )) {
                AlertWindow.getInstance().launchAlertWindow("Replication of tables", "Replication of tables has been completed.");
            }
            ;

        }
    }

    @FXML
    private void btnCancelReplicationSetOnClicked() {
        Stage stage = (Stage) btnCancelReplication.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connections = FXCollections.observableArrayList(ConnectionPropertiesBuilder.getInstance().getConnectionsList());
        cbxSourceReplication.setItems(connections);
        cbxTargetReplication.setItems(connections);
    }

    /*
    private void launchAlertWindow(String title, String message) {
        try {
            Stage st = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AlertWindow.fxml"));

            Parent sceneMain = loader.load();

            AlertWindowController controller = loader.<AlertWindowController>getController();
            controller.initData(message);

            Scene scene = new Scene(sceneMain);
            st.setScene(scene);
            st.setTitle(title);
            st.show();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }
    }
    */
}
