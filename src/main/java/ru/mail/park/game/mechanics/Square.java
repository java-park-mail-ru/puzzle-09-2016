package ru.mail.park.game.mechanics;

import java.util.Arrays;

public class Square {
    public static final int SIZE = 8;
    public static final int MIN_VALUE = 1;
    public static final int MAX_VALUE = 9;
    private int[][] matrix;

    public Square() {
        initMatrix();
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public void initMatrix() {
        matrix = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                matrix[i][j] = MIN_VALUE;
            }
        }
    }

    public void activate(int row, int col, int targetDiff, int neighbourDiff) {
        add(row, col, targetDiff);
        add(row - 1, col - 1, neighbourDiff);
        add(row - 1, col, neighbourDiff);
        add(row - 1, col + 1, neighbourDiff);
        add(row, col - 1, neighbourDiff);
        add(row, col + 1, neighbourDiff);
        add(row + 1, col - 1, neighbourDiff);
        add(row + 1, col, neighbourDiff);
        add(row + 1, col + 1, neighbourDiff);
    }

    private void add(int row, int col, int value) {
        try {
            matrix[row][col] += value;
            if (matrix[row][col] > MAX_VALUE) {
                matrix[row][col] = MAX_VALUE;
            } else if (matrix[row][col] < MIN_VALUE) {
                matrix[row][col] = MIN_VALUE;
            }
        } catch (IndexOutOfBoundsException ignore) {
        }
    }

    public void scramble(int moves) {
        initMatrix();
        for (int i = 0; i < moves; i++) {
            activate((int) (Math.random() * SIZE), (int) (Math.random() * SIZE), 2, 1);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Square square = (Square) o;
        return Arrays.deepEquals(matrix, square.matrix);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(matrix);
    }
}
