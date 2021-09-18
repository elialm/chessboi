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
import javafx.scene.layout.BorderPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import org.efac.chess.Chessboard;
import org.efac.chess.ChessPiece.Color;
import org.efac.chess.piece.Bishop;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("chessboi");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/white_queen.png")));

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
        chessboard.getLocation(3, 4).setPiece(new Bishop(Color.WHITE));
        ChessboardBuilder.setupChessboard(chessboard, scene);
        ChessboardBuilder.updateChessboard(chessboard, scene);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    
}