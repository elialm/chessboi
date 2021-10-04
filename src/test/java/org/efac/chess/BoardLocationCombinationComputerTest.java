package org.efac.chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.efac.chess.BoardLocationCombinationComputer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;;

public class BoardLocationCombinationComputerTest {
    @Test
    public void testComputer() {
        BoardLocationCombinationComputer computer = new BoardLocationCombinationComputer(4, 4, 4);
        int indices[] = new int[] { 0, 1819, 1650, 100, 500, 455, 1104, 1105 };
        
        ArrayList<ArrayList<Integer>> expected = new ArrayList<ArrayList<Integer>>();
        expected.add(Lists.newArrayList(0, 1, 2, 3));
        expected.add(Lists.newArrayList(12, 13, 14, 15));
        expected.add(Lists.newArrayList(6, 8, 11, 13));
        expected.add(Lists.newArrayList(0, 2, 3, 13));
        expected.add(Lists.newArrayList(1, 2, 7, 11));
        expected.add(Lists.newArrayList(1, 2, 3, 4));
        expected.add(Lists.newArrayList(2, 13, 14, 15));
        expected.add(Lists.newArrayList(3, 4, 5, 6));

        for (int i = 0; i < indices.length; i++) {
            Object[] actual = computer.computeAtIndex(indices[i]).toArray();
            assertArrayEquals(expected.get(i).toArray(), actual);
        }
    }
}
