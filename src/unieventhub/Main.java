package unieventhub;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import unieventhub.util.EmailSender;
import unieventhub.util.ReminderScheduler;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
    	
    	// Start background reminder check using queue-based ReminderScheduler
        ReminderScheduler scheduler = new ReminderScheduler();
        scheduler.start();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/login.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
