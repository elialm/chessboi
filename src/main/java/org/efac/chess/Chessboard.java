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

import java.security.InvalidParameterException;

import org.efac.chess.iter.BoardLocationIterable;
import org.efac.chess.iter.ChessPieceIterable;
import org.efac.chess.iter.FilledBoardLocationIterable;

public class Chessboard {
    int xSize;
    int ySize;
    BoardLocation[][] boardLocations;

    public int getXSize() { return xSize; }
    public int getYSize() { return ySize; }
    public BoardLocationIterable getLocations() { return new BoardLocationIterable(this); }
    public FilledBoardLocationIterable getFilledLocations() { return new FilledBoardLocationIterable(getLocations()); }
    public ChessPieceIterable getPieces() { return new ChessPieceIterable(getFilledLocations()); }

    public Chessboard(int xSize, int ySize) {
        if (xSize < 0) {
            throw new InvalidParameterException("xSize must be positive");
        }

        if (ySize < 0) {
            throw new InvalidParameterException("ySize must be positive");
        }

        this.xSize = xSize;
        this.ySize = ySize;

        boardLocations = new BoardLocation[xSize][ySize];
        for (int column = 0; column < xSize; column++) {
            for (int row = 0; row < ySize; row++) {
                boardLocations[column][row] = new BoardLocation(column, row, this);
            }
        }
    }

    public BoardLocation getLocation(int xLocation, int yLocation) {
        return isInBounds(xLocation, yLocation) ? boardLocations[xLocation][yLocation] : null;
    }

    public BoardLocation getLocationSafe(int xLocation, int yLocation) {
        checkIfInBounds(xLocation, yLocation);
        return boardLocations[xLocation][yLocation];
    }

    public boolean isLocationFree(int xLocation, int yLocation) {
        return getLocation(xLocation, yLocation).getPiece() == null;
    }

    public ChessPiece getPiece(int xLocation, int yLocation) {
        checkIfInBounds(xLocation, yLocation);
        return boardLocations[xLocation][yLocation].getPiece();
    }

    public void setPiece(int xLocation, int yLocation, ChessPiece piece) {
        checkIfInBounds(xLocation, yLocation);
        boardLocations[xLocation][yLocation].setPiece(piece);
    }

    public void checkIfInBounds(BoardLocation location) {
        checkIfInBounds(location.getXLocation(), location.getYLocation());
    }

    public void checkIfInBounds(int xLocation, int yLocation) {
        if (!isInBounds(xLocation, yLocation)) {
            throw new InvalidParameterException("Location is out of bounds, requested ("
                    + xLocation + ", " + yLocation + ") from board of size " + xSize
                    + " by " + ySize);
        }
    }

    public boolean isInBounds(int xLocation, int yLocation) {
        return xLocation >= 0 && yLocation >= 0 && xLocation < xSize && yLocation < ySize;
    }
}
