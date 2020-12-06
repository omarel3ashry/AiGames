package com.example.android.aigames.queens;

import android.util.Pair;

import androidx.lifecycle.MutableLiveData;

import com.example.android.aigames.queens.model.ChessBoard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Omar Elashry on 2020-11-25.
 */
@SuppressWarnings("FieldMayBeFinal")
public class QueensGame {

    public MutableLiveData<Pair<Integer, Integer>> selectedChessCellLiveData = new MutableLiveData<>();
    public MutableLiveData<boolean[][]> chessCellsLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> isGameSolvedLiveData = new MutableLiveData<>();

    private int selectedRow = -1;
    private int selectedCol = -1;
    private ChessBoard board;
    private boolean[][] tempCells = new boolean[8][8];
    private List<boolean[][]> allSolutions = new ArrayList<>();
    private List<boolean[][]> possibleSolutions = new ArrayList<>();
    private boolean[] rowForSol = new boolean[8];
    private boolean[] colForSol = new boolean[8];
    private boolean[] scRight = new boolean[15];
    private boolean[] scLeft = new boolean[15];

    public QueensGame() {
        newGame();
    }

    public void newGame() {
        board = new ChessBoard(8);
        for (int i = 0; i < 8; i++)
            getAllPossibleSolutions(0, i);
        selectedChessCellLiveData.postValue(Pair.create(selectedRow, selectedRow));
        chessCellsLiveData.postValue(board.getCells());
    }

    private void getAllPossibleSolutions(int r, int c) {
        if (rowForSol[r] || colForSol[c] || scLeft[r + c] || scRight[r + (7 - c)]) return;
        tempCells[r][c] = true;
        rowForSol[r] = colForSol[c] = scLeft[r + c] = scRight[r + (7 - c)] = true;

        if (r < 7)
            for (int j = 0; j < 8; ++j)
                getAllPossibleSolutions(r + 1, j);
        else if (r == 7)
            allSolutions.add(getCopyOf(tempCells));

        tempCells[r][c] = false;
        rowForSol[r] = colForSol[c] = scLeft[r + c] = scRight[r + (7 - c)] = false;
    }

    public int getSolutionsCount() {
        possibleSolutions = new ArrayList<>();
        for (boolean[][] solution : allSolutions) {
            boolean flag = true;
            for (int i = 0; i < board.getSize(); i++)
                for (int j = 0; j < board.getSize(); j++)
                    if (board.getCell(i, j))
                        if (!solution[i][j]) {
                            flag = false;
                            break;
                        }
            if (flag) possibleSolutions.add(solution);
        }
        return possibleSolutions.size();
    }

    private boolean[][] getCopyOf(boolean[][] cells) {
        boolean[][] copy = new boolean[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(cells[i], 0, copy[i], 0, 8);
        }
        return copy;
    }

    public void handleInput() {
        if (selectedRow == -1 || selectedCol == -1 || queensCount() == 8) return;
        board.setCell(selectedRow, selectedCol, true);
        chessCellsLiveData.postValue(board.getCells());
        isGameSolvedLiveData.postValue(gameSolved());
    }

    private int queensCount() {
        int count = 0;
        for (int i = 0; i < board.getSize(); i++)
            for (int j = 0; j < board.getSize(); j++)
                if (board.getCell(i, j))
                    count += 1;
        return count;

    }

    private boolean gameSolved() {
        if (queensCount() > 8) return false;
        for (boolean[][] solution : allSolutions)
            if (Arrays.deepEquals(solution, board.getCells()))
                return true;
        return false;
    }

    public void solveTheGame() {
        board.setCells(possibleSolutions.get(0));
        chessCellsLiveData.postValue(board.getCells());
        isGameSolvedLiveData.postValue(true);
    }

    public void updateSelectedChessCell(int row, int col) {
        selectedRow = row;
        selectedCol = col;
        selectedChessCellLiveData.postValue(Pair.create(selectedRow, selectedCol));
    }

    public void delete() {
        if (selectedRow == -1 || selectedCol == -1) return;
        board.setCell(selectedRow, selectedCol, false);
        chessCellsLiveData.postValue(board.getCells());
    }


}
