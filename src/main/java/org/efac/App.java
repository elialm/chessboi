package org.efac;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
// import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // var javaVersion = SystemInfo.javaVersion();
        // var javafxVersion = SystemInfo.javafxVersion();

        var loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/main.fxml"));

        BorderPane pane = null;
        try {
            pane = loader.<BorderPane>load();
        } 
        catch (Exception ex) {
            System.out.println(ex);
            throw ex;
        }

        var scene = new Scene(pane, 900, 600);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        //GridPane chessboard = (GridPane)scene.lookup("#chessboard");

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}