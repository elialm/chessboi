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

import org.efac.chess.Chessboard;
import org.efac.chess.ChessPiece;
import org.efac.chess.BoardLocation;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class ChessboardBuilder {
    public static void setupChessboard(Chessboard chessboard, Scene scene) {
        setupChessboard(chessboard, (GridPane)scene.lookup("#chessboard"));
    }

    public static void setupChessboard(Chessboard chessboard, GridPane chessboardPane) {
        chessboardPane.getColumnConstraints().clear();
        chessboardPane.getRowConstraints().clear();
        chessboardPane.getChildren().clear();

        double widthPercentage = 100.0 / ((double)chessboard.getXSize());
        double heightPercentage = 100.0 / ((double)chessboard.getYSize());

        double widthCell = chessboardPane.getPrefWidth() / ((double)chessboard.getXSize());
        double heightCell = chessboardPane.getPrefHeight() / ((double)chessboard.getYSize());

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

        for (int x = 0; x < chessboard.getXSize(); x++) {
            for (int y = 0; y < chessboard.getYSize(); y++) {
                BorderPane pane = new BorderPane();
                pane.setPrefSize(widthCell, heightCell);
                pane.setMinSize(widthCell, heightCell);
                pane.setMaxSize(widthCell, heightCell);
                
                if (x % 2 == 0 && y % 2 == 1 || x % 2 == 1 && y % 2 == 0) {
                    pane.setStyle("-fx-background-color: #793e30;");   
                } else {
                    pane.setStyle("-fx-background-color: #fdf3ae;");
                }

                chessboardPane.add(pane, x, y);
            }
        }
    }

    public static void updateChessboard(Chessboard chessboard, Scene scene) {
        updateChessboard(chessboard, (GridPane)scene.lookup("#chessboard"));
    }

    public static void updateChessboard(Chessboard chessboard, GridPane chessboardPane) {
        for (javafx.scene.Node node : chessboardPane.getChildren()) {
            BorderPane pane = (BorderPane)node;
            pane.setCenter(null);

            int x = GridPane.getColumnIndex(pane);
            int y = GridPane.getRowIndex(pane);
            BoardLocation location = chessboard.getLocation(x, y);

            if (!location.isFree()) {
                ImageView view = new ImageView(getChessPieceImage(location.getPiece()));
                view.fitWidthProperty().bind(pane.widthProperty());
                view.fitHeightProperty().bind(pane.heightProperty());
                pane.setCenter(view);
            }
        }
    }

    private static Image getChessPieceImage(ChessPiece piece) {
        String typeString = piece.getType().toString().toLowerCase();
        String colorString = piece.getColor().toString().toLowerCase();

        return new Image(ChessboardBuilder.class.getResourceAsStream("/img/" + colorString + "_" + typeString + ".png"));
    }
}
