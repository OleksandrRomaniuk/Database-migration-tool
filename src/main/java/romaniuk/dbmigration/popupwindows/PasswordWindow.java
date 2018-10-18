package romaniuk.dbmigration.popupwindows;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import romaniuk.dbmigration.controllers.PasswordWindowController;
import romaniuk.dbmigration.tool.ConnectToDTB;
import romaniuk.dbmigration.Main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PasswordWindow {

    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private final String DBtype;
    private PasswordAndLogin instance;

    public PasswordWindow(PasswordAndLogin instance, String DBtype) {
        this.instance = instance;
        this.DBtype = DBtype;
        getWindow();
    }

    private void getWindow() {
        Stage st = null;
        try {
            st = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/PasswordWindow.fxml"));
            Parent sceneMain = loader.load();
            Scene scene = new Scene(sceneMain);
            PasswordWindowController controller = loader.<PasswordWindowController>getController();
            controller.setObject(instance, this);
            st.setScene(scene);
            st.setTitle("Login&password " + DBtype + " DB");
            st.show();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }
    }

    private void getWindow(String label) {
        try {
            Stage st = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/PasswordWindow.fxml"));
            Parent sceneMain = loader.load();
            Scene scene = new Scene(sceneMain);
            PasswordWindowController controller = loader.<PasswordWindowController>getController();
            controller.setObject(instance, this);
            controller.setLabel(label);
            st.setScene(scene);
            st.setTitle("Login&password " + DBtype + " DB");
            st.show();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }
    }

    public boolean checkPasswordAndLogin(String login, String password) {
        boolean result = false;

        if (login != null && login != null) {

            try {
                ConnectToDTB.getInstance().checkLoginAndPassword(login, password, DBtype);
                result = true;
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Login or password are incorrect.");
                getWindow("Login or password are incorrect.");
            }

        } else {
            logger.log(Level.SEVERE, "Login or password have not been entered.");
            getWindow("Login or password have not been entered.");
        }
        return result;
    }
}