package romaniuk.migrationtool;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import romaniuk.logger.LoggerHandler;

import java.util.logging.Logger;


public class Main extends Application {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        (new LoggerHandler()).loggerHandler();

        Application.launch(Main.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/romaniuk/ui/MainUI.fxml"));
        primaryStage.setTitle("Data migration");
        primaryStage.setScene(new Scene(root, 1300, 800));
        primaryStage.show();
    }
}

