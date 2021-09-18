package org.efac.ui;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
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

    public ChessboardController() {
        numberFormatExceptionPattern = Pattern.compile("For input string: \"(.*)\"");
        chessboard = null;
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
}
