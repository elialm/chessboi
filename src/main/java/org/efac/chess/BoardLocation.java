package org.efac.chess;

import org.efac.chess.Chessboard;
import org.efac.chess.ChessPiece;
import java.security.InvalidParameterException;

public class BoardLocation {
    int xLocation;
    int yLocation;
    Chessboard chessboard;
    
    public int getXLocation() { return xLocation; }
    public int getYLocation() { return yLocation; }
    public Chessboard getChessboard() { return chessboard; }

    public BoardLocation(int xLocation, int yLocation, Chessboard chessboard) {
        if (chessboard == null) {
            throw new NullPointerException("chessboard cannot be null");
        }
        
        if (xLocation >= chessboard.getXSize()) {
            throw new InvalidParameterException("xLocation must be smaller then chessboard's x size");
        }

        if (yLocation >= chessboard.getYSize()) {
            throw new InvalidParameterException("yLocation must be smaller then chessboard's y size");
        }

        if (xLocation < 0) {
            throw new InvalidParameterException("xLocation must be positive");
        }

        if (yLocation < 0) {
            throw new InvalidParameterException("yLocation must be positive");
        }
        
        this.xLocation = xLocation;
        this.yLocation = yLocation;
        this.chessboard = chessboard;
    }

    public ChessPiece getPiece() {
        return chessboard.getPiece(xLocation, yLocation);
    }

    public void setPiece(ChessPiece piece) {
        chessboard.setPiece(xLocation, yLocation, piece);
    }

    public BoardLocation getRelativeLocation(int xRelative, int yRelative) {
        return chessboard.createLocation(xLocation + xRelative, yLocation + yRelative);
    }
}
