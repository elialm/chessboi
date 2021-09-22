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

import com.google.common.collect.FluentIterable;

public abstract class ChessPiece implements Comparable<ChessPiece> {
    public enum Type {
        PAWN(1),
        TOWER(2),
        HORSE(3),
        BISHOP(4),
        QUEEN(5),
        KING(6);

        private int value;

        public int getValue() { return value; }

        private Type(int value) {
            this.value = value;
        }
    }

    public enum Color {
        WHITE,
        BLACK
    }

    private Type type;
    private Color color;

    public Type getType() { return type; }
    public Color getColor() { return color; }

    public ChessPiece(Type type, Color color) {
        this.type = type;
        this.color = color;
    }

    public int compareTo(ChessPiece other) {
        if (color != other.getColor()) {
            return color == Color.WHITE ? 10 : -10;
        } else {
            return type.getValue() - other.getType().getValue();
        }
    }

    public abstract ArrayList<BoardLocation> getPossibleMoves(BoardLocation location);

    @Override
    public String toString() {
        return type.toString().substring(0, 1) + type.toString().substring(1).toLowerCase();
    }

    protected ArrayList<BoardLocation> getPossibleMovesInLine(BoardLocation location, Point[] relativeDirections) {
        ArrayList<BoardLocation> possibleMoves = new ArrayList<BoardLocation>();

        for (Point direction : relativeDirections) {
            int xComponent = direction.getXComponent();
            int yComponent = direction.getYComponent();

            for (BoardLocation nextLocation = location.getRelativeLocation(xComponent, yComponent); nextLocation != null && nextLocation.isFree(); nextLocation = nextLocation.getRelativeLocation(xComponent, yComponent)) {
                possibleMoves.add(nextLocation);
            }
        }

        return possibleMoves;
    }
}
