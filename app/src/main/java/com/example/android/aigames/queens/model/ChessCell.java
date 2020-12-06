package com.example.android.aigames.queens.model;

/**
 * Created by Omar Elashry on 2020-11-25.
 */
public class ChessCell {

    private final int row;
    private final int col;
    private boolean value;

    public ChessCell(int row, int col, boolean value) {
        this.row = row;
        this.col = col;
        this.value = value;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean getValue() {
        return value;
    }

    public String getValueString() {
        return value ? "Q" : "";
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
