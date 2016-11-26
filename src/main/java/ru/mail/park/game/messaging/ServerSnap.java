package ru.mail.park.game.messaging;

public class ServerSnap {
    private String firstPlayer;
    private String secondPlayer;
    private int[][] firstMatrix;
    private int[][] secondMatrix;
    private int[][] target;
    private boolean gameOver;
    private String winner;

    public String getFirstPlayer() {
        return firstPlayer;
    }

    public void setFirstPlayer(String firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    public String getSecondPlayer() {
        return secondPlayer;
    }

    public void setSecondPlayer(String secondPlayer) {
        this.secondPlayer = secondPlayer;
    }

    public int[][] getFirstMatrix() {
        return firstMatrix;
    }

    public void setFirstMatrix(int[][] firstMatrix) {
        this.firstMatrix = firstMatrix;
    }

    public int[][] getSecondMatrix() {
        return secondMatrix;
    }

    public void setSecondMatrix(int[][] secondMatrix) {
        this.secondMatrix = secondMatrix;
    }

    public int[][] getTarget() {
        return target;
    }

    public void setTarget(int[][] target) {
        this.target = target;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }
}
