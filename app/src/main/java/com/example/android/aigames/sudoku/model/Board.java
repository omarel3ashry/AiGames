package com.example.android.aigames.sudoku.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Omar Elashry on 2020-11-21.
 */
public class Board {

    private final int size;
    private List<List<Cell>> cells;

    public Board(int size, List<List<Cell>> cells) {
        this.size = size;
        this.cells = cells;
    }

    public Board(int size) {
        this.size = size;
        zeroBoard(size);
    }

    private void zeroBoard(int size) {
        cells = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            cells.add(new ArrayList<>());
            for (int j = 0; j < size; j++) {
                cells.get(i).add(new Cell(i, j, 0, false));
            }
        }
    }

    public int getSize() {
        return size;
    }

    public List<List<Cell>> getCells() {
        return cells;
    }

    public Cell getCell(int row, int col) {
        return cells.get(row).get(col);
    }
}
