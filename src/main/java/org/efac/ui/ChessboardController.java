package org.efac.ui;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ChessboardController {
    
    private Pattern numberFormatExceptionPattern;

    @FXML
    private TextField chessboardWidth;

    @FXML
    private TextField chessboardHeight;

    @FXML
    private GridPane chessboardPane;

    public ChessboardController() {
        numberFormatExceptionPattern = Pattern.compile("For input string: \"(.*)\"");
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
        
        System.out.println("pressed!");
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
}
