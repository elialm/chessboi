package org.efac.chess;

import org.efac.chess.ChessPiece;
import org.efac.chess.BoardLocation;
import java.security.InvalidParameterException;

public class Chessboard {
    int xSize;
    int ySize;
    BoardLocation[][] boardLocations;

    public int getXSize() { return xSize; }
    public int getYSize() { return ySize; }

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
                    + xLocation + ", " + yLocation + ") from board of size " + xSize + " by "
                    + ySize);
        }
    }

    private boolean isInBounds(int xLocation, int yLocation) {
        return xLocation < xSize && yLocation < ySize;
    }
}
