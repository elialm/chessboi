package org.efac.chess;

import java.util.ArrayList;
import java.util.Arrays;

import org.efac.chess.ChessPiece.Color;
import org.efac.chess.piece.Bishop;
import org.efac.chess.piece.Queen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ChessboardTest {
    
    @Test
    public void testIteratingOverChessboard() {
        Chessboard chessboard = new Chessboard(8, 8);

        ChessPiece expected[] = {
            new Bishop(Color.BLACK),
            new Bishop(Color.WHITE),
            new Queen(Color.BLACK),
            new Queen(Color.WHITE),
            new Bishop(Color.BLACK),
            new Queen(Color.BLACK)
        };

        Point points[] = {
            new Point(0, 0),
            new Point(0, 7),
            new Point(7, 2),
            new Point(3, 3),
            new Point(5, 1),
            new Point(7, 7)
        };

        for (int i = 0; i < expected.length; i++) {
            Point point = points[i];
            ChessPiece piece = expected[i];
            
            chessboard.setPiece(point.getXComponent(), point.getYComponent(), piece);
        }

        ArrayList<ChessPiece> actual = new ArrayList<>();
        chessboard.getPieces().forEach(actual::add);

        assertAll("Should find all expected pieces",
            () -> assertEquals(expected.length, actual.size()),
            () -> assertTrue(actual.containsAll(Arrays.asList(expected))),
            () -> assertTrue(Arrays.asList(expected).containsAll(actual))
        );
    }

    @Test
    public void testSomething() {
        Chessboard chessboard = new Chessboard(8, 8);
        
        ChessPiece expected[] = {
            new Bishop(Color.BLACK),
            new Bishop(Color.WHITE),
            new Queen(Color.BLACK),
            new Queen(Color.WHITE),
            new Bishop(Color.BLACK),
            new Queen(Color.BLACK)
        };

        Point points[] = {
            new Point(0, 0),
            new Point(0, 7),
            new Point(7, 2),
            new Point(3, 3),
            new Point(5, 1),
            new Point(7, 7)
        };

        for (int i = 0; i < expected.length; i++) {
            Point point = points[i];
            ChessPiece piece = expected[i];
            
            chessboard.setPiece(point.getXComponent(), point.getYComponent(), piece);
        }
    }
}
