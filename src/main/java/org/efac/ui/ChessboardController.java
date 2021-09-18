package org.efac.ui;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

public class ChessboardController {
    
    @FXML
    private TextField chessboardWidth;

    @FXML
    private TextField chessboardHeight;

    @FXML
    public void pressed(ActionEvent event) {
        System.out.println("pressed!");
    }
}
