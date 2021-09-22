package org.efac.chess;

import java.security.InvalidParameterException;

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

public class Point {
    int xComponent;
    int yComponent;

    public int getXComponent() { return xComponent; }
    public int getYComponent() { return yComponent; }

    public Point(int xComponent, int yComponent) {
        this.xComponent = xComponent;
        this.yComponent = yComponent;
    }

    public static Point fromIndex(int index, int xSize, int ySize) {
        final int numberOfPoints = xSize * ySize;
        
        if (index >= numberOfPoints) {
            throw new IndexOutOfBoundsException();
        }

        return new Point(index % xSize, index / xSize);
    }
}
