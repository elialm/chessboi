package org.efac.chess;

import java.security.InvalidParameterException;

import org.efac.chess.Chessboard;

public class BoardLocation {
    int xLocation;
    int yLocation;
    
    public int getXLocation() { return xLocation; }
    public int getYLocation() { return yLocation; }

    public BoardLocation(int x, int y, Chessboard chessboard) {
        if (x >= chessboard.getXSize()) {
            throw new InvalidParameterException("x must be smaller then chessboard's x size");
        }

        if (y >= chessboard.getYSize()) {
            throw new InvalidParameterException("y must be smaller then chessboard's y size");
        }
        
        xLocation = x;
        yLocation = y;
    }
}
