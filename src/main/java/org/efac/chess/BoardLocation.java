package org.efac.chess;

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

import java.util.ArrayList;
import java.security.InvalidParameterException;

public class BoardLocation {
    int xLocation;
    int yLocation;
    Chessboard chessboard;
    ChessPiece chessPiece;
    
    public int getXLocation() { return xLocation; }
    public int getYLocation() { return yLocation; }
    public Chessboard getChessboard() { return chessboard; }
    public ChessPiece getPiece() { return chessPiece; }
    public void setPiece(ChessPiece piece) { chessPiece = piece; }

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
        chessPiece = null;
    }

    public BoardLocation getRelativeLocation(int xRelative, int yRelative) {
        return chessboard.getLocation(xLocation + xRelative, yLocation + yRelative);
    }

    public BoardLocation getRelativeLocationSafe(int xRelative, int yRelative) {
        return chessboard.getLocationSafe(xLocation + xRelative, yLocation + yRelative);
    }

    public ArrayList<BoardLocation> getPossibleMoves() {
        return chessPiece != null ? chessPiece.getPossibleMoves(this) : null;
    }

    public boolean isFree() {
        return chessPiece == null;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof BoardLocation)) {
            return false;
        }

        BoardLocation otherLocation = (BoardLocation)other;
        return chessboard == otherLocation.getChessboard()
            && xLocation == otherLocation.getXLocation()
            && yLocation == otherLocation.getYLocation();
    }
}
