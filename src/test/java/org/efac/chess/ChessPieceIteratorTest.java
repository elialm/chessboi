package org.efac.chess;

import org.efac.chess.ChessPiece.Color;
import org.efac.chess.piece.Bishop;
import org.efac.chess.piece.Queen;
import org.junit.jupiter.api.Test;

public class ChessPieceIteratorTest {
    
    @Test
    public void testIteratingOverChessboard() {
        Chessboard chessboard = new Chessboard(8, 8);
        chessboard.setPiece(0, 0, new Bishop(Color.BLACK));
        chessboard.setPiece(0, 7, new Bishop(Color.WHITE));
        chessboard.setPiece(7, 2, new Queen(Color.BLACK));
        chessboard.setPiece(3, 3, new Queen(Color.WHITE));
        chessboard.setPiece(5, 1, new Bishop(Color.BLACK));
        chessboard.setPiece(7, 7, new Queen(Color.BLACK));

        for (ChessPiece piece : chessboard.getPieces()) {
            System.out.println(piece);
        }
    }
}
