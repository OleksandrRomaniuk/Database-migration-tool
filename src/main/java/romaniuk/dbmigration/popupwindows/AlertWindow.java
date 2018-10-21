package romaniuk.dbmigration.popupwindows;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import romaniuk.dbmigration.controllers.AlertWindowController;
import romaniuk.dbmigration.Main;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AlertWindow {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    private static AlertWindow instance = null;

    private AlertWindow() {
    }

    public static AlertWindow getInstance() {
        if (instance == null) {
            AlertWindow instance = new AlertWindow();
            return instance;
        } else
            return instance;
    }

    public void launchAlertWindow(String title, String message) {
        try {
            Stage st = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/AlertWindow.fxml"));

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
}
