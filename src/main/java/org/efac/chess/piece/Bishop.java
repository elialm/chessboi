package org.efac.chess.piece;

import org.efac.chess.ChessPiece;
import org.efac.chess.BoardLocation;
import org.efac.chess.Point;
import java.util.ArrayList;

public class Bishop extends ChessPiece {
    
    private static Point relativeDirections[] = {
        new Point(-1, -1),
        new Point(-1,  1),
        new Point( 1, -1),
        new Point( 1,  1)
    };

    /**
     *      p  -  -  -  p
     *      -  p  -  p  -
     *      -  -  X  -  - 
     *      -  p  -  p  -
     *      p  -  -  -  p
     */
    public ArrayList<BoardLocation> getPossibleMoves(BoardLocation location) {
        ArrayList<BoardLocation> possibleMoves = new ArrayList<BoardLocation>();

        for (Point direction : relativeDirections) {
            int xComponent = direction.getXComponent();
            int yComponent = direction.getYComponent();

            for (BoardLocation nextLocation = location.getRelativeLocation(xComponent, yComponent); nextLocation != null; nextLocation = nextLocation.getRelativeLocation(xComponent, yComponent)) {
                possibleMoves.add(nextLocation);
            }
        }

        return possibleMoves;
    }
}
