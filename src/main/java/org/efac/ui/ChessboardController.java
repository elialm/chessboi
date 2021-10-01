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
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.scene.layout.BorderPane;
import javafx.concurrent.ScheduledService;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.math.BigInteger;

import org.efac.chess.Chessboard;
import org.efac.chess.BoardLocation;
import org.efac.chess.ChessPiece;
import org.efac.chess.ChessPiece.Color;
import org.efac.chess.Point;
import org.efac.chess.piece.Bishop;
import org.efac.chess.piece.Queen;
import org.efac.chess.solver.DominationSolver;
import org.efac.chess.iter.ThreadedSolutionIterable;
import org.efac.chess.iter.ThreadedSolutionIterator;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.base.Stopwatch;

public class ChessboardController {
    
    private Pattern numberFormatExceptionPattern;
    private Chessboard chessboard;
    private ChessboardBuilder chessboardBuilder;
    private EventHandler<MouseEvent> chessboardLocationMouseEventHandler;
    private ScheduledService<Void> progressBarUpdater;
    private Thread solverThread;
    private Task<Void> solverTask;

    @FXML
    private TextField chessboardWidth;

    @FXML
    private TextField chessboardHeight;

    @FXML
    private GridPane chessboardPane;

    @FXML
    private Button chessboardUpdate;

    @FXML
    private ComboBox<String> chessPieceType;

    @FXML
    private ListView<ChessPiece> solverChessPieces;

    @FXML
    private Button solveDomination;

    @FXML
    private Button solveBishopDomination;

    @FXML
    private ProgressBar dominationProgressIndicator;

    public ChessboardController() {
        numberFormatExceptionPattern = Pattern.compile("For input string: \"(.*)\"");
        chessboard = null;
        chessboardBuilder = null;
        progressBarUpdater = null;
        solverThread = null;

        chessboardLocationMouseEventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                BorderPane source = (BorderPane)e.getSource();
    
                int columnIndex = GridPane.getColumnIndex(source);
                int rowIndex = GridPane.getRowIndex(source);
    
                if (e.getButton() == MouseButton.PRIMARY) {
                    addSelectedChessPiece(chessboard.getLocationSafe(columnIndex, rowIndex));
                } else if (e.getButton() == MouseButton.SECONDARY) {
                    chessboard.getLocationSafe(columnIndex, rowIndex).setPiece(null);
                    chessboardBuilder.updateChessboard();
                } 
            }
        };
    }

    @FXML
    private void initialize() {
        addTextFieldFocusedListener(chessboardWidth);
        addTextFieldFocusedListener(chessboardHeight);
    }

    public void setupController(Stage stage) {
        stage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);
    }

    @FXML
    public void setupChessboard(ActionEvent event) {
        Optional<Point> boardDimensions = getPreferredBoardSize();
        if (!boardDimensions.isPresent()) {
            return;
        }

        int chessboardWidth = boardDimensions.get().getXComponent();
        int chessboardHeight = boardDimensions.get().getYComponent();

        chessboard = new Chessboard(chessboardWidth, chessboardHeight);
        chessboardBuilder = new ChessboardBuilder(chessboard, chessboardPane, chessboardLocationMouseEventHandler);
        chessboardBuilder.setupChessboard();
        chessboardBuilder.updateChessboard();

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

    @FXML
    public void addChessPiece(ActionEvent event) {
        ChessPiece selectedPiece = getSelectedChessPiece();

        solverChessPieces.getItems().add(selectedPiece);

        solveDomination.setDisable(false);
        solveBishopDomination.setDisable(FluentIterable.from(solverChessPieces.getItems()).transform(piece -> piece.getType()).contains(ChessPiece.Type.BISHOP));
    }

    @FXML
    public void clearChessPieces(ActionEvent event) {
        solverChessPieces.getItems().clear();
        solveDomination.setDisable(true);
        solveBishopDomination.setDisable(false);
    }

    @FXML
    public void solveDominationProblem(ActionEvent event) {
        Optional<Point> boardDimensions = getPreferredBoardSize();
        if (!boardDimensions.isPresent()) {
            return;
        }

        solveDomination.setDisable(true);
        solveBishopDomination.setDisable(true);

        startSolverTask(boardDimensions.get().getXComponent(), boardDimensions.get().getYComponent());
    }

    @FXML
    public void solveBishopDominationProblem(ActionEvent event) {
        Optional<Point> boardDimensions = getPreferredBoardSize();
        if (!boardDimensions.isPresent()) {
            return;
        }

        solveDomination.setDisable(true);
        solveBishopDomination.setDisable(true);

        if (solverChessPieces.getItems().isEmpty()) {
            solverChessPieces.getItems().add(new Bishop(Color.BLACK));
        }

        startBishopSolverTask(boardDimensions.get().getXComponent(), boardDimensions.get().getYComponent());
    }

    private void closeWindowEvent(WindowEvent event) {
        if (progressBarUpdater != null) {
            progressBarUpdater.cancel();
        }

        if (solverThread != null) {
            solverTask.cancel(true);
            
            try {
                solverThread.join();
            } catch (InterruptedException ex) {
                // Application is closing, so ignore exception
            }
        }
    }

    private void startSolverTask(int chessboardWidth, int chessboardHeight) {
        solverTask = new Task<Void>() {
            @Override
            public Void call() {
                Platform.runLater(() -> {
                    dominationProgressIndicator.setVisible(true);
                    dominationProgressIndicator.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
                });

                Stopwatch sw = Stopwatch.createStarted();

                DominationSolver solver = new DominationSolver(chessboardWidth, chessboardHeight, solverChessPieces.getItems());
                ThreadedSolutionIterable solutionIterable = solver.getThreadedSolutions();
                ThreadedSolutionIterator solutionIterator = solutionIterable.iterator();

                setupProgressBar(solver.getNumberOfChessboardCombinations(), solutionIterable);
                
                BigInteger numberOfSolutions = new BigInteger("0");
                if (solutionIterator.hasNext()) {
                    chessboard = solutionIterator.next();
                    numberOfSolutions = numberOfSolutions.add(BigInteger.ONE);

                    while (solutionIterator.hasNext()) {
                        solutionIterator.next();
                        numberOfSolutions = numberOfSolutions.add(BigInteger.ONE);
                    }
                }

                sw.stop();

                if (isCancelled()) {
                    return null;
                }

                if (numberOfSolutions.compareTo(BigInteger.ZERO) != 0) {
                    final BigInteger finalNumberOfSolutions = numberOfSolutions;

                    Platform.runLater(() -> {
                        progressBarUpdater.cancel();
                        dominationProgressIndicator.setVisible(false);

                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setHeaderText("Solution result");
                        alert.setContentText("Found " + finalNumberOfSolutions.toString() + " solutions\nTime elapsed: " + formatElapsedTime(sw));
                        alert.showAndWait();

                        updateSolution();

                        solveDomination.setDisable(false);
                        solveBishopDomination.setDisable(FluentIterable.from(solverChessPieces.getItems()).transform(piece -> piece.getType()).contains(ChessPiece.Type.BISHOP));
                    });
                } else {
                    Platform.runLater(() -> {
                        progressBarUpdater.cancel();
                        dominationProgressIndicator.setVisible(false);

                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setHeaderText("Solution result");
                        alert.setContentText("Found no solutions\nTime elapsed: " + formatElapsedTime(sw));
                        alert.showAndWait();

                        solveDomination.setDisable(false);
                        solveBishopDomination.setDisable(FluentIterable.from(solverChessPieces.getItems()).transform(piece -> piece.getType()).contains(ChessPiece.Type.BISHOP));
                    });
                }

                return null;
            }
        };

        solverThread = new Thread(solverTask);
        solverThread.start();
    }

    private void startBishopSolverTask(int chessboardWidth, int chessboardHeight) {
        solverTask = new Task<Void>() {
            @Override
            public Void call() {
                BigInteger numberOfSolutions;
                chessboard = null;

                Stopwatch sw = Stopwatch.createStarted();

                do {
                    Platform.runLater(() -> {
                        dominationProgressIndicator.setVisible(true);
                        dominationProgressIndicator.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
                    });

                    DominationSolver solver = new DominationSolver(chessboardWidth, chessboardHeight, solverChessPieces.getItems());
                    ThreadedSolutionIterable solutionIterable = solver.getThreadedSolutions();
                    ThreadedSolutionIterator solutionIterator = solutionIterable.iterator();

                    setupProgressBar(solver.getNumberOfChessboardCombinations(), solutionIterable);
                    
                    numberOfSolutions = new BigInteger("0");

                    if (solutionIterator.hasNext()) {
                        chessboard = solutionIterator.next();
                        numberOfSolutions = numberOfSolutions.add(BigInteger.ONE);
    
                        while (solutionIterator.hasNext()) {
                            solutionIterator.next();
                            numberOfSolutions = numberOfSolutions.add(BigInteger.ONE);
                        }
                    }

                    Platform.runLater(() -> {
                        progressBarUpdater.cancel();
                    });

                    if (chessboard == null && !isCancelled()) {
                        solverChessPieces.getItems().add(new Bishop(Color.BLACK));
                    }
                } while (chessboard == null && !isCancelled());

                sw.stop();

                if (isCancelled()) {
                    return null;
                }

                if (numberOfSolutions.compareTo(BigInteger.ZERO) != 0) {
                    final BigInteger finalNumberOfSolutions = numberOfSolutions;

                    Platform.runLater(() -> {
                        dominationProgressIndicator.setVisible(false);

                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setHeaderText("Solution result");
                        alert.setContentText("Found " + finalNumberOfSolutions.toString() + " solutions\nTime elapsed: " + formatElapsedTime(sw));
                        alert.showAndWait();

                        updateSolution();

                        solveDomination.setDisable(false);
                        solveBishopDomination.setDisable(FluentIterable.from(solverChessPieces.getItems()).transform(piece -> piece.getType()).contains(ChessPiece.Type.BISHOP));
                    });
                } else {
                    Platform.runLater(() -> {
                        progressBarUpdater.cancel();
                        dominationProgressIndicator.setVisible(false);

                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setHeaderText("Solution result");
                        alert.setContentText("Found no solutions\nTime elapsed: " + formatElapsedTime(sw));
                        alert.showAndWait();

                        solveDomination.setDisable(false);
                        solveBishopDomination.setDisable(FluentIterable.from(solverChessPieces.getItems()).transform(piece -> piece.getType()).contains(ChessPiece.Type.BISHOP));
                    });
                }

                return null;
            }
        };

        solverThread = new Thread(solverTask);
        solverThread.start();
    }

    private String formatElapsedTime(Stopwatch sw) {
        long minutePart = sw.elapsed(TimeUnit.MINUTES);
        long secondPart = sw.elapsed(TimeUnit.SECONDS) % 60;
        long milliPart = sw.elapsed(TimeUnit.MILLISECONDS) % 1000;

        return padNumber(minutePart, 2) + ":" + padNumber(secondPart, 2) + "." + padNumber(milliPart, 3);
    }

    private String padNumber(Long value, int minWidth) {
        String valueString = value.toString();
        String padString = "0".repeat(Math.max(minWidth - valueString.length(), 0));

        return padString + valueString;
    }

    private void setupProgressBar(BigInteger numberOfChessboardCombinations, ThreadedSolutionIterable solutionIterable) {
        progressBarUpdater = createProgressUpdaterService(numberOfChessboardCombinations, solutionIterable);
        progressBarUpdater.setPeriod(Duration.seconds(1));
        progressBarUpdater.start();

        Platform.runLater(() -> {
            dominationProgressIndicator.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        });
    }

    private void updateSolution() {
        chessboardBuilder = new ChessboardBuilder(chessboard, chessboardPane, chessboardLocationMouseEventHandler);
        chessboardBuilder.setupChessboard();
        chessboardBuilder.updateChessboard();
    }

    private ScheduledService<Void> createProgressUpdaterService(BigInteger numberOfChessboardCombinations, ThreadedSolutionIterable solutionIterable) {
        return new ScheduledService<Void>(){
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    protected Void call() {
                        BigInteger currentProgress = solutionIterable.getCurrentProgress();
                        double progress = currentProgress.doubleValue() / numberOfChessboardCombinations.doubleValue();

                        Platform.runLater(() -> {
                            dominationProgressIndicator.setProgress(progress);
                        });

                        return null;
                    }
                };
            }
        };
    }

    private Optional<Point> getPreferredBoardSize() {
        int chessboardWidth;
        int chessboardHeight;

        try {
            chessboardWidth = Integer.parseInt(this.chessboardWidth.getText());
            chessboardHeight = Integer.parseInt(this.chessboardHeight.getText());
        }
        catch (NumberFormatException ex) {
            handleInvalidIntegerInput(ex);
            return Optional.absent();
        }

        if (chessboardWidth == 0 || chessboardHeight == 0) {
            handleZeroIntegerInput();
            return Optional.absent();
        }

        if (chessboardWidth < 0 || chessboardHeight < 0) {
            handleNegativeIntegerInput();
            return Optional.absent();
        }

        return Optional.of(new Point(chessboardWidth, chessboardHeight));
    }

    private ChessPiece getSelectedChessPiece() {
        switch (chessPieceType.getSelectionModel().getSelectedItem()) {
            case "Bishop": return new Bishop(Color.WHITE);
            case "Queen": return new Queen(Color.WHITE);
            
            // Code should never come here
            default: return null;
        }
    }

    private void addSelectedChessPiece(BoardLocation location) {
        if (!location.isFree()) {
            return;
        }

        location.setPiece(getSelectedChessPiece());
        chessboardBuilder.updateChessboard();
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

    private void handleZeroIntegerInput() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setHeaderText("Number input error");
        alert.setContentText("Zero is not allowed as a board dimension, please enter only non-zero, positive integers");
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
