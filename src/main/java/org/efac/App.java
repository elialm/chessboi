package org.efac;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;

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
        // vbox.setId("label1");

        var label = new Label("Other label");
        label.setStyle("-fx-background-color: rgb(84, 82, 151);");
        vbox.getChildren().add(label);

        var scene = new Scene(vbox, 600, 400);
        scene.getStylesheets().add(getClass().getResource("css/style.css").toExternalForm());

        stage.setScene(scene);
        stage.show();

        // System.out.println(getClass().getResource("resources/pedobear.png"));
    }

    public static void main(String[] args) {

        launch();
    }

}