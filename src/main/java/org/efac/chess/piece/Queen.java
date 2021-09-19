package org.efac.chess.piece;

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

import org.efac.chess.ChessPiece;
import org.efac.chess.BoardLocation;
import org.efac.chess.Point;

import java.util.ArrayList;

public class Queen extends ChessPiece {
    private static Point relativeDirections[] = {
        new Point(-1, -1),
        new Point(-1,  0),
        new Point(-1,  1),
        new Point( 0,  1),
        new Point( 1, -1),
        new Point( 1,  0),
        new Point( 1,  1),
        new Point( 0,  1),
        new Point( 0, -1)
    };

    public Queen(Color color) {
        super(Type.QUEEN, color);
    }

    public ArrayList<BoardLocation> getPossibleMoves(BoardLocation location) {
        return getPossibleMovesInLine(location, relativeDirections);
    }
}
