package com.kingleader.cashybird;

public class Player
{
    private int serialNumber;
    private String userName;
    private int score;
    private int checkOut;

    public Player(int serialNumber, String userName, int score, int checkOut) {
        this.serialNumber = serialNumber;
        this.userName = userName;
        this.score = score;
        this.checkOut = checkOut;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(int checkOut) {
        this.checkOut = checkOut;
    }
}
