package com.golan.amit.ibabyamplifier;

public class BabyPlayHelper {

    // "יטחזוהדגבא",
    public static final String[] CHARS = {
            "0123456789",
            "אבגדהוזחטי",
            "ABCDEFGHIJ"
    };

    public static final int DIGITS = 0;
    public static final int HEBREW = 1;
    public static final int ENGLISH = 2;
    public static final int FAILSNUMBER = 3;

    private int random_picker;
    private int type_picker;
    private int fails;

    public BabyPlayHelper() {
        type_picker = HEBREW;
        fails = 0;
        generate();
    }

    public void generate() {
        random_picker = (int)(Math.random() * CHARS[0].length());
    }

    public String getCharAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append(CHARS[getType_picker()].charAt(getRandom_picker()));
        return sb.toString();
    }

    public String getCharByIndex(int ind) {
        if(ind < 0 || ind > CHARS[getType_picker()].length())
            return "";
        StringBuilder sb = new StringBuilder();
        char tmpCh = CHARS[getType_picker()].charAt(ind);
        sb.append(tmpCh);
        return sb.toString();
    }

    /**
     * Getters & Setters
     */

    public int getRandom_picker() {
        return random_picker;
    }

    public void setRandom_picker(int random_picker) {
        this.random_picker = random_picker;
    }

    public int getType_picker() {
        return type_picker;
    }

    public void setType_picker(int type_picker) {
        this.type_picker = type_picker;
    }

    public int getFails() {
        return fails;
    }

    public void setFails(int fails) {
        this.fails = fails;
    }

    public void increaseFails() {
        this.fails++;
    }

    public void resetFails() {
        this.fails = 0;
    }
}
