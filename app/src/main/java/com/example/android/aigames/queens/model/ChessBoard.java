package com.example.android.aigames.queens.model;

import com.example.android.aigames.sudoku.model.Cell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Omar Elashry on 2020-11-21.
 */
public class ChessBoard {

    private final int size;
    private boolean[][] cells;

    public ChessBoard(int size, boolean[][] cells) {
        this.size = size;
        this.cells = cells;
    }

    public ChessBoard(int size) {
        this.size = size;
        cells = new boolean[size][size];
    }

    public int getSize() {
        return size;
    }

    public boolean[][] getCells() {
        return cells;
    }

    public boolean getCell(int row, int col) {
        return cells[row][col];
    }

    public void setCells(boolean[][] cells) {
        this.cells = cells;
    }

    public void setCell(int row, int col, boolean value) {
        this.cells[row][col] = value;
    }
}
