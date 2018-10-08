package romaniuk.logger;

import romaniuk.migrationtool.Main;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.*;

public class LoggerHandler {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public void loggerHandler() {
        FileHandler fh = null;
        SimpleDateFormat format = new SimpleDateFormat("M-d_HHmmss");

        try {
            fh = new FileHandler("MyLogger.log", 1000000000, 1, true);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }

        fh.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                SimpleDateFormat logTime = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                Calendar cal = new GregorianCalendar();
                cal.setTimeInMillis(record.getMillis());
                return record.getLevel() + " "
                        + logTime.format(cal.getTime())
                        + " || "
                        + record.getSourceClassName().substring(
                        record.getSourceClassName().lastIndexOf(".") + 1,
                        record.getSourceClassName().length())
                        + "."
                        + record.getSourceMethodName()
                        + "() : "
                        + record.getMessage() + "\n";
            }
        });

        fh.setFilter((Filter) new Filter() {

            @Override
            public boolean isLoggable(LogRecord record) {
                /*
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/romaniuk/ui/MainUI.fxml"));
                MainUIController controller = loader.<MainUIController>getController();
                controller.textUpdate(record.getMessage());
                */
                return record.getLevel().intValue() >= Level.WARNING.intValue();
            }

        });
        logger.addHandler(fh);
    }
}

