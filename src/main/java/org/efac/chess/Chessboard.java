package org.efac.chess;

import org.efac.chess.ChessPiece;
import org.efac.chess.BoardLocation;
import java.security.InvalidParameterException;

public class Chessboard {
    int xSize;
    int ySize;
    ChessPiece[][] boardPieces;

    public int getXSize() {
        return xSize;
    }

    public int getYSize() {
        return ySize;
    }

    public Chessboard(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;

        boardPieces = new ChessPiece[xSize][ySize];
    }

    public BoardLocation createLocation(int xLocation, int yLocation) {
        return new BoardLocation(xLocation, yLocation, this);
    }

    public boolean isLocationFree(int xLocation, int yLocation) {
        return getPiece(xLocation, yLocation) == null;
    }

    public ChessPiece getPiece(int xLocation, int yLocation) {
        checkIfInBounds(xLocation, yLocation);
        return boardPieces[xLocation][yLocation];
    }

    public void setPiece(int xLocation, int yLocation, ChessPiece piece) {
        checkIfInBounds(xLocation, yLocation);
        boardPieces[xLocation][yLocation] = piece;
    }

    public void checkIfInBounds(BoardLocation location) {
        checkIfInBounds(location.getXLocation(), location.getYLocation());
    }

    public void checkIfInBounds(int xLocation, int yLocation) {
        if (!isInBounds(xLocation, yLocation)) {
            throw new InvalidParameterException("Location is out of bounds, requested ("
                    + xLocation + ", " + yLocation + ") from board of size " + xSize + " by "
                    + ySize);
        }
    }

    private boolean isInBounds(int xLocation, int yLocation) {
        return xLocation < xSize && yLocation < ySize;
    }
}
