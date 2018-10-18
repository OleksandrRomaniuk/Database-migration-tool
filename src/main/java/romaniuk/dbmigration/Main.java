package romaniuk.dbmigration;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class Main extends Application {

    public static final String DB_PROPERTIES_XML = "DBproperties.xml";

    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(
                Objects.requireNonNull(
                        Thread.currentThread().getContextClassLoader().getResource("ui/MainUI.fxml")));

        primaryStage.setTitle("Data migration");
        primaryStage.setScene(new Scene(root, 1300, 800));
        primaryStage.show();

        LoggerFactory.getLogger(Main.class).info("Started DB Migration Tool");
    }
}

