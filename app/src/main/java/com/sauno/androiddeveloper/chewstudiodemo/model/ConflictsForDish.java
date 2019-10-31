package com.sauno.androiddeveloper.chewstudiodemo.model;

public class ConflictsForDish {
    private String oneString;
    private String twoString;
    private String threeString;

    public ConflictsForDish(String oneString, String twoString, String threeString) {
        this.oneString = oneString;
        this.twoString = twoString;
        this.threeString = threeString;
    }

    public String getOneString() {
        return oneString;
    }

    public void setOneString(String oneString) {
        this.oneString = oneString;
    }

    public String getTwoString() {
        return twoString;
    }

    public void setTwoString(String twoString) {
        this.twoString = twoString;
    }

    public String getThreeString() {
        return threeString;
    }

    public void setThreeString(String threeString) {
        this.threeString = threeString;
    }
}
