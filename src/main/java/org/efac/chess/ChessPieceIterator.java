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

import org.efac.chess.ChessPiece;

import java.util.Iterator;

public class ChessPieceIterator implements Iterable<ChessPiece> {
    
    private Chessboard chessboard;

    public ChessPieceIterator(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    @Override
    public Iterator<ChessPiece> iterator() {
        Iterator<ChessPiece> it = new Iterator<ChessPiece>() {
            private int currentX = 0;
            private int currentY = 0;
            private ChessPiece next = null;
            
            @Override
            public boolean hasNext() {
                if (next != null) {
                    return true;
                } else {
                    next = findNext();
                    return next != null;
                }
            }

            @Override
            public ChessPiece next() {
                if (next == null) {
                    return findNext();
                }

                ChessPiece piece = next;
                next = null;
                return piece;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            private ChessPiece findNext() {
                ChessPiece piece = null; 

                columnLoop:
                for (; currentX < chessboard.getXSize(); currentX++) {
                    for (; currentY < chessboard.getYSize(); currentY++) {
                        piece = chessboard.getPiece(currentX, currentY);

                        if (piece != null) {
                            currentY++;
                            break columnLoop;
                        }
                    }
                }

                if (currentY == chessboard.getYSize()) {
                    currentX++;
                    currentY = 0;
                }

                return piece;
            }
        };

        return it;
    }
}
