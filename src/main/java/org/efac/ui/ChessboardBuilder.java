package org.efac.ui;

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

import java.util.ArrayList;

import org.efac.chess.BoardLocation;

import javafx.scene.Scene;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class ChessboardBuilder {
    private static double chessboardPaneMaxSize = 550.0;
    private static String generalBoardLocationStyle = "-fx-border-color: #1e1e1e;-fx-border-radius: 6;-fx-border-width: 4;-fx-background-insets: 4;";
    private static String darkBoardLocationStyle = "-fx-background-color: #793e30;" + generalBoardLocationStyle;
    private static String lightBoardLocationStyle = "-fx-background-color: #fdf3ae;" + generalBoardLocationStyle;

    private EventHandler<MouseEvent> chessboardLocationMouseEventHandler;
    private Chessboard chessboard;
    private GridPane chessboardPane;

    public ChessboardBuilder(Chessboard chessboard, GridPane chessboardPane, EventHandler<MouseEvent> chessboardLocationMouseEventHandler) {
        this.chessboard = chessboard;
        this.chessboardPane = chessboardPane;
        this.chessboardLocationMouseEventHandler = chessboardLocationMouseEventHandler;
    }

    public void setupChessboard() {
        chessboardPane.getColumnConstraints().clear();
        chessboardPane.getRowConstraints().clear();
        chessboardPane.getChildren().clear();

        double widthPercentage = 100.0 / ((double)chessboard.getXSize());
        double heightPercentage = 100.0 / ((double)chessboard.getYSize());

        if (chessboard.getXSize() != chessboard.getYSize()) {
            resizeChessboardPane();
        } else {
            setChessboardPaneSize(chessboardPaneMaxSize, chessboardPaneMaxSize);
        }

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
                pane.addEventHandler(MouseEvent.MOUSE_RELEASED, chessboardLocationMouseEventHandler);
                
                if (x % 2 == 0 && y % 2 == 1 || x % 2 == 1 && y % 2 == 0) {
                    pane.setStyle(darkBoardLocationStyle);   
                } else {
                    pane.setStyle(lightBoardLocationStyle);
                }

                chessboardPane.add(pane, x, y);
            }
        }
    }

    public void updateChessboard() {
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
            } else {
                pane.setCenter(null);
            }
        }

        updatePossibleMoves();
    }

    private void updatePossibleMoves() {
        for (BoardLocation filledLocation : chessboard.getFilledLocations()) {
            ArrayList<BoardLocation> possibleMoves = filledLocation.getPossibleMoves();

            for (BoardLocation possibleMove : possibleMoves) {
                if (possibleMove.isFree()) {
                    BorderPane cell = getCellByBoardLocation(possibleMove);

                    if (cell.getCenter() == null) {
                        ImageView view = new ImageView(new Image(ChessboardBuilder.class.getResourceAsStream("/img/under_attack_3.png")));
                        view.fitWidthProperty().bind(cell.widthProperty());
                        view.fitHeightProperty().bind(cell.heightProperty());
                        cell.setCenter(view);
                    }
                }
            }
        }
    }

    private BorderPane getCellByBoardLocation(BoardLocation location) {
        for (Node node : chessboardPane.getChildren()) {
            if (GridPane.getRowIndex(node) == location.getYLocation() && GridPane.getColumnIndex(node) == location.getXLocation()) {
                return (BorderPane)node;
            }
        }

        return null;
    }

    private Image getChessPieceImage(ChessPiece piece) {
        String typeString = piece.getType().toString().toLowerCase();
        String colorString = piece.getColor().toString().toLowerCase();

        return new Image(ChessboardBuilder.class.getResourceAsStream("/img/" + colorString + "_" + typeString + ".png"));
    }

    private void resizeChessboardPane() {
        double smallerDimension = Math.min(chessboard.getXSize(), chessboard.getYSize());
        int biggerDimension = Math.max(chessboard.getXSize(), chessboard.getYSize());

        smallerDimension *= chessboardPaneMaxSize / (double)biggerDimension;

        if (biggerDimension == chessboard.getXSize()) {
            setChessboardPaneSize(chessboardPaneMaxSize, smallerDimension);
        } else {
            setChessboardPaneSize(smallerDimension, chessboardPaneMaxSize);
        }
    }

    private void setChessboardPaneSize(double xSize, double ySize) {
        chessboardPane.setMinSize(xSize, ySize);
        chessboardPane.setMaxSize(xSize, ySize);
        chessboardPane.setPrefSize(xSize, ySize);
    }
}
