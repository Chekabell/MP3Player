package player;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Parent;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class Main extends Application {

    @Override
    public void start (Stage stage) throws IOException, URISyntaxException {
        Parent root = new FXMLLoader(new File("src/main/resources/Menu.fxml").getAbsoluteFile().toURL()).load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(windowEvent -> {
            Platform.exit();
            System.exit(0);
        });
    }
    public static void main(String[] args){
        launch();
    }
}
