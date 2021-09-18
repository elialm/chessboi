package org.efac;

/**
 *  MIT License

    Copyright (c) 2021 Elijah Almeida Coimbra

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.

 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import java.io.FileInputStream;

import org.efac.chess.Chessboard;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // var javaVersion = SystemInfo.javaVersion();
        // var javafxVersion = SystemInfo.javafxVersion();

        stage.setTitle("chessboi");

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

        Chessboard chessboard = new Chessboard(8, 8);
        setupChessboard(scene, chessboard);

        GridPane chessboardPane = (GridPane)scene.lookup("#chessboard");
        chessboardPane.add(new Button(), 0, 0);
        chessboardPane.add(new Button(), 7, 7);
        
        BorderPane block = new BorderPane();
        block.setStyle("-fx-background-color: rgb(84, 82, 151);");
        chessboardPane.add(block, 4, 4);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private void setupChessboard(Scene scene, Chessboard chessboard) {
        GridPane chessboardPane = (GridPane)scene.lookup("#chessboard");

        chessboardPane.getColumnConstraints().clear();
        chessboardPane.getRowConstraints().clear();

        double widthPercentage = 100.0 / ((double)chessboard.getXSize());
        double heightPercentage = 100.0 / ((double)chessboard.getYSize());

        for (int i = 0; i < chessboard.getXSize(); i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(widthPercentage);

            chessboardPane.getColumnConstraints().add(column);
        }

        for (int i = 0; i < chessboard.getYSize(); i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(heightPercentage);
            
            chessboardPane.getRowConstraints().add(row);
        }
    }
}