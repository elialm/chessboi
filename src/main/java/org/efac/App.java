package org.efac;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.URL;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // var javaVersion = SystemInfo.javaVersion();
        // var javafxVersion = SystemInfo.javafxVersion();

        var loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("fxml/main.fxml"));
        var vbox = loader.<VBox>load();

        var scene = new Scene(vbox, 600, 400); 
        stage.setScene(scene);
        stage.show();

        // System.out.println(getClass().getResource("resources/pedobear.png"));
    }

    public static void main(String[] args) {

        launch();
    }

}