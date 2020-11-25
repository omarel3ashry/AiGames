package com.example.android.aigames.sudoku;

import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.MutableLiveData;

import com.example.android.aigames.sudoku.model.Board;
import com.example.android.aigames.sudoku.model.Cell;

import java.util.List;
import java.util.Random;

/**
 * Created by Omar Elashry on 2020-11-21.
 */
public class SudokuGame {
    public MutableLiveData<Pair<Integer, Integer>> selectedCellLiveData = new MutableLiveData<>();
    public MutableLiveData<List<List<Cell>>> cellsLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> isTakingNoteLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> isGameSolvedLiveData = new MutableLiveData<>();
    private int selectedRow = -1;
    private int selectedCol = -1;
    private boolean isTakingNotes = false;
    private Board board;
    private Random randomForClues;
    private Random randomForRemover;
    private int difficultyLevel = 1;

    public SudokuGame() {
//        newGame(1);
    }

    public void newGame(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
        selectedRow = -1;
        selectedCol = -1;
        randomForClues = new Random();
        randomForRemover = new Random();
        board = new Board(9);
        do {
            gameGenerator(0, 0, board);
        }
        while (!gameSolver(board));
        logSolve(board);
        lockBoard(board);
        selectedCellLiveData.postValue(Pair.create(selectedRow, selectedCol));
        cellsLiveData.postValue(board.getCells());
        isTakingNoteLiveData.postValue(isTakingNotes);
    }

    public void solveTheGame() {
        cleanUserValues(board);
        isGameSolvedLiveData.postValue(gameSolver(board));
        cellsLiveData.postValue(board.getCells());
    }

    public void gameGenerator(int blockRow, int blockCol, Board board) {
        for (int i = blockRow; i < 7; i += 3) {
            for (int j = blockCol; j < 7; j += 3) {
                int rowRandomHolder = randomForClues.nextInt(3) + i;
                int colRandomHolder = randomForClues.nextInt(3) + j;
                int valueRandomHolder = randomForClues.nextInt(9) + 1;
                if (validValue(rowRandomHolder, colRandomHolder, valueRandomHolder, board))
                    board.getCell(rowRandomHolder, colRandomHolder).setValue(valueRandomHolder);
                else gameGenerator(((i / 3) * 3), ((j / 3) * 3), board);
            }
        }
    }

    public boolean validValue(int row, int col, int value, Board board) {
        for (int i = 0; i < 9; i++)
            if (board.getCell(row, i).getValue() == value)
                return false;
        for (int i = 0; i < 9; i++)
            if (board.getCell(i, col).getValue() == value)
                return false;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board.getCell(((row / 3) * 3) + i, ((col / 3) * 3) + j).getValue() == value)
                    return false;
        return true;
    }

    private void cleanUserValues(Board board) {
        for (List<Cell> rowCell : board.getCells())
            for (Cell cell : rowCell)
                if (!cell.isStarterCell())
                    cell.setValue(0);
    }

    public void setDifficultyLevel(int level) {
        this.difficultyLevel = level;
    }

    public boolean gameSolver(Board board) {
        int row, col;
        if (containsZero(board)[0] == 0)
            return true;
        else {
            row = containsZero(board)[1];
            col = containsZero(board)[2];
        }
        for (int k = 1; k < 10; k++)
            if (validValue(row, col, k, board)) {
                board.getCell(row, col).setValue(k);
                if (gameSolver(board))
                    return true;
                board.getCell(row, col).setValue(0);
            }
        return false;
    }

    public void randomRemover(Board board) {
        int emptyCellsCount = (difficultyLevel * 5) + 40;
        while (emptyCellsCount(board) < emptyCellsCount)
            for (int i = 0; i < 9; i++) {
                if (emptyCellsCount(board) == emptyCellsCount)
                    break;
                int indexToRemove = randomForRemover.nextInt(9);
                while (board.getCell(i, indexToRemove).getValue() == 0)
                    indexToRemove = randomForRemover.nextInt(9);
                board.getCell(i, indexToRemove).setValue(0);
            }
    }

    private int emptyCellsCount(Board board) {
        int count = 0;
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                if (board.getCell(i, j).getValue() == 0)
                    count += 1;
        return count;
    }

    private void lockBoard(Board board) {
        randomRemover(board);
        for (List<Cell> rowCells : board.getCells())
            for (Cell cell : rowCells)
                if (cell.getValue() != 0)
                    cell.makeStarterCell(true);
    }

    public boolean invalidateSolvedBoard(Board board) {
        for (List<Cell> rowCells : board.getCells()) {
            for (Cell cell : rowCells) {
                int row = cell.getRow();
                int col = cell.getCol();
                int value = cell.getValue();
                cell.setValue(0);
                for (int i = 0; i < 9; i++)
                    if (board.getCell(row, i).getValue() == value) {
                        cell.setValue(value);
                        return false;
                    }
                for (int i = 0; i < 9; i++)
                    if (board.getCell(i, col).getValue() == value) {
                        cell.setValue(value);
                        return false;
                    }
                for (int i = 0; i < 3; i++)
                    for (int j = 0; j < 3; j++)
                        if (board.getCell(((row / 3) * 3) + i, ((col / 3) * 3) + j).getValue() == value) {
                            cell.setValue(value);
                            return false;
                        }
                cell.setValue(value);
            }
        }
        return true;
    }

    private boolean gameSolved(Board board) {
        if (containsZero(board)[0] == 1) return false;
        return invalidateSolvedBoard(board);
    }

    private int[] containsZero(Board board) {
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                if (board.getCell(i, j).getValue() == 0)
                    return new int[]{1, i, j};
        return new int[]{0, 0, 0};
    }

    private void logSolve(Board board) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                builder.append(board.getCell(i, j).getValue()).append("  ");
                if ((j + 1) % 3 == 0) builder.append("|  ");
            }
            if ((i + 1) % 3 == 0) builder.append("\n___________________________________\n \n");
            else builder.append("\n");
        }
        Log.i("SOLVE", builder.toString());
    }

    public void handleInput(int value) {
        if (selectedRow == -1 || selectedCol == -1) return;
        Cell cell = board.getCell(selectedRow, selectedCol);
        if (cell.isStarterCell()) return;
        if (isTakingNotes) {
            cell.setValue(0);
            if (cell.getNotes().contains(value)) cell.getNotes().remove(value);
            else cell.getNotes().add(value);
        } else {
            cell.setValue(value);
        }
        cellsLiveData.postValue(board.getCells());
        isGameSolvedLiveData.postValue(gameSolved(board));
    }

    public void updateSelectedCell(int row, int col) {
        if (!board.getCell(row, col).isStarterCell()) {
            selectedRow = row;
            selectedCol = col;
            selectedCellLiveData.postValue(Pair.create(selectedRow, selectedCol));
        }
    }

    public void changeTakingNoteState() {
        isTakingNotes = !isTakingNotes;
        isTakingNoteLiveData.postValue(isTakingNotes);
    }

    public void delete() {
        if (selectedRow == -1 || selectedCol == -1) return;
        board.getCell(selectedRow, selectedCol).getNotes().clear();
        board.getCell(selectedRow, selectedCol).setValue(0);
        cellsLiveData.postValue(board.getCells());
    }
}
