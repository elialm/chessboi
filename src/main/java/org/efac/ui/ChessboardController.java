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

import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.util.regex.Pattern;

import org.efac.chess.Chessboard;
import org.efac.chess.ChessPiece.Color;
import org.efac.chess.piece.Bishop;

import java.util.regex.Matcher;

public class ChessboardController {
    
    private Pattern numberFormatExceptionPattern;
    private Chessboard chessboard;

    @FXML
    private TextField chessboardWidth;

    @FXML
    private TextField chessboardHeight;

    @FXML
    private GridPane chessboardPane;

    @FXML
    private Button chessboardUpdate;

    public ChessboardController() {
        numberFormatExceptionPattern = Pattern.compile("For input string: \"(.*)\"");
        chessboard = null;
    }

    @FXML
    private void initialize() {
        addTextFieldFocusedListener(chessboardWidth);
        addTextFieldFocusedListener(chessboardHeight);
    }

    @FXML
    public void setupChessboard(ActionEvent event) {
        int chessboardWidth;
        int chessboardHeight;
        
        try {
            chessboardWidth = Integer.parseInt(this.chessboardWidth.getText());
            chessboardHeight = Integer.parseInt(this.chessboardHeight.getText());
        }
        catch (NumberFormatException ex) {
            handleInvalidIntegerInput(ex);
            return;
        }

        if (chessboardWidth < 0 || chessboardHeight < 0) {
            handleNegativeIntegerInput();
            return;
        }

        chessboard = new Chessboard(chessboardWidth, chessboardHeight);
        chessboard.getLocation(chessboardWidth / 2, chessboardHeight / 2).setPiece(new Bishop(Color.WHITE));

        ChessboardBuilder.setupChessboard(chessboard, chessboardPane);
        ChessboardBuilder.updateChessboard(chessboard, chessboardPane);

        ((Button)event.getSource()).setText("Update dimensions");
    }

    @FXML
    public void handleChessboardWidthOnKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            chessboardHeight.requestFocus();
        }
    }

    @FXML
    public void handleChessboardHeightOnKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            chessboardPane.requestFocus();
            chessboardUpdate.fire();
        }
    }

    private void handleInvalidIntegerInput(NumberFormatException ex) {
        Matcher matcher = numberFormatExceptionPattern.matcher(ex.getMessage());
        matcher.matches();
        
        Alert alert = new Alert(AlertType.ERROR);
        alert.setHeaderText("Number input error");
        alert.setContentText("\"" + matcher.group(1) + "\" is not an integer value, please insert an integer value");
        alert.showAndWait();

        this.chessboardWidth.setText("8");
        this.chessboardHeight.setText("8");
    }

    private void handleNegativeIntegerInput() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setHeaderText("Number input error");
        alert.setContentText("Negative numbers are not allowed as board dimensions, please enter only positive integers");
        alert.showAndWait();

        this.chessboardWidth.setText("8");
        this.chessboardHeight.setText("8");
    }

    private void addTextFieldFocusedListener(TextField textField) {
        textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            Platform.runLater(() -> {
                textField.selectAll();
            });
        });
    }
}