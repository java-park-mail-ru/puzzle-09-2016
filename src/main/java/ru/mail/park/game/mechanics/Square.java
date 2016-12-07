package ru.mail.park.game.mechanics;

import ru.mail.park.game.config.GameSettings;

import java.util.Arrays;

public class Square {
    private static final int SIZE = GameSettings.getSquareSize();
    private static final int MIN_VALUE = GameSettings.getSquareMinValue();
    private static final int MAX_VALUE = GameSettings.getSquareMaxValue();
    private int[][] matrix;

    public Square() {
        initMatrix();
    }

    public Square(int scramble) {
        initMatrix();
        for (int i = 0; i < scramble; i++) {
            activate((int) (Math.random() * SIZE), (int) (Math.random() * SIZE), 2, 1);
        }
    }

    public int[][] getMatrix() {
        return matrix;
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

    private void initMatrix() {
        matrix = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                matrix[i][j] = MIN_VALUE;
            }
        }
    }

    @SuppressWarnings("OverlyComplexBooleanExpression")
    private void add(int row, int col, int value) {
        if (row >= 0 && col >= 0 && row < SIZE && col < SIZE) {
            matrix[row][col] += value;
            if (matrix[row][col] > MAX_VALUE) {
                matrix[row][col] = MAX_VALUE;
            } else if (matrix[row][col] < MIN_VALUE) {
                matrix[row][col] = MIN_VALUE;
            }
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
