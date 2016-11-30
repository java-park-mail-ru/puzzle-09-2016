package ru.mail.park.game.messaging;

public class ServerSnap {
    private String player;
    private String opponent;
    private int[][] playerMatrix;
    private int[][] opponentMatrix;
    private int[][] target;
    private boolean gameOver;
    private boolean win;

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public int[][] getPlayerMatrix() {
        return playerMatrix;
    }

    public void setPlayerMatrix(int[][] playerMatrix) {
        this.playerMatrix = playerMatrix;
    }

    public int[][] getOpponentMatrix() {
        return opponentMatrix;
    }

    public void setOpponentMatrix(int[][] opponentMatrix) {
        this.opponentMatrix = opponentMatrix;
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

    public boolean isWin() {
        return win;
    }

    public void setWin(boolean win) {
        this.win = win;
    }
}
