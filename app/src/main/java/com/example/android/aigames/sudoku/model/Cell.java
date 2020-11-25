package com.example.android.aigames.sudoku.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Omar Elashry on 2020-11-21.
 */
public class Cell {

    private final int row;
    private final int col;
    private int value;
    private boolean isStarterCell;
    private Set<Integer> notes;

    public Cell(int row, int col, int value, boolean isStarterCell) {
        this.row = row;
        this.col = col;
        this.value = value;
        this.isStarterCell = isStarterCell;
    }

    public boolean isStarterCell() {
        return isStarterCell;
    }

    public void makeStarterCell(boolean b) {
        this.isStarterCell = b;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Set<Integer> getNotes() {
        if (notes == null) notes = new HashSet<>();
        return notes;
    }

    public void setNotes(Set<Integer> notes) {
        this.notes = notes;
    }
}
