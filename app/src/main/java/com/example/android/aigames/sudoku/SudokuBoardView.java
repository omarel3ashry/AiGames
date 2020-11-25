package com.example.android.aigames.sudoku;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.android.aigames.R;
import com.example.android.aigames.sudoku.model.Cell;

import java.util.List;

/**
 * Created by Omar Elashry on 2020-11-20.
 */
public class SudokuBoardView extends View {

    private Paint thickLinePaint;
    private Paint thickerLinePaint;
    private Paint thinLinePaint;
    private Paint selectedCellPaint;
    private Paint starterCellPaint;
    private Paint conflictingCellPaint;
    private Paint duplicateCellPaint;
    private Paint duplicateStarterCellPaint;
    private Paint textPaint;
    private Paint starterCellTextPaint;
    private Paint noteTextPaint;
    private int boardSize = 9;
    private double sqrtSize = Math.sqrt(boardSize);

    private float cellDimPx = 0f;
    private float noteDimPx = 0f;

    private int selectedColumn = -1;
    private int selectedRow = -1;
    private List<List<Cell>> cells;
    private OnCellTouchListener onCellTouchListener;
    private boolean boardDisabled = false;

    public SudokuBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        thickLinePaint = new Paint();
        thickLinePaint.setStyle(Paint.Style.STROKE);
        thickLinePaint.setStrokeWidth(5f);
        thickLinePaint.setColor(getResources().getColor(R.color.prussianBlue, null));

        thickerLinePaint = new Paint();
        thickerLinePaint.setStyle(Paint.Style.STROKE);
        thickerLinePaint.setStrokeWidth(9f);
        thickerLinePaint.setColor(getResources().getColor(R.color.prussianBlue, null));

        thinLinePaint = new Paint();
        thinLinePaint.setStyle(Paint.Style.STROKE);
        thinLinePaint.setStrokeWidth(2f);
        thinLinePaint.setColor(getResources().getColor(R.color.black, null));

        selectedCellPaint = new Paint();
        selectedCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        selectedCellPaint.setColor(getResources().getColor(R.color.lighterRose2, null));

        starterCellPaint = new Paint();
        starterCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        starterCellPaint.setColor(getResources().getColor(R.color.lighterGray, null));

        conflictingCellPaint = new Paint();
        conflictingCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        conflictingCellPaint.setColor(getResources().getColor(R.color.lighterRose3, null));

        duplicateCellPaint = new Paint();
        duplicateCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        duplicateCellPaint.setColor(getResources().getColor(R.color.lightRed, null));

        duplicateStarterCellPaint = new Paint();
        duplicateStarterCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        duplicateStarterCellPaint.setColor(getResources().getColor(R.color.grayedRed, null));

        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setColor(getResources().getColor(R.color.black, null));
//        textPaint.setTextSize(60f);

        starterCellTextPaint = new Paint();
        starterCellTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        starterCellTextPaint.setColor(getResources().getColor(R.color.black, null));
//        starterCellTextPaint.setTextSize(65f);
        starterCellTextPaint.setTypeface(Typeface.DEFAULT_BOLD);

        noteTextPaint = new Paint();
        noteTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        noteTextPaint.setColor(getResources().getColor(R.color.black, null));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int dimenPx = Math.min(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(dimenPx, dimenPx);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        measurementsHelper(getWidth());
        fillCells(canvas);
        drawLines(canvas);
        drawText(canvas);
        super.onDraw(canvas);
    }

    private void measurementsHelper(int width) {
        cellDimPx = (float) width / boardSize;
        noteDimPx = (float) (cellDimPx / (float) sqrtSize);
        textPaint.setTextSize(cellDimPx / 1.5f);
        starterCellTextPaint.setTextSize(cellDimPx / 1.5f);
        noteTextPaint.setTextSize(cellDimPx / 3.5f);
    }

    private void drawLines(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), thickerLinePaint);
        for (int i = 0; i < boardSize; i++) {
            Paint paint = i % sqrtSize == 0 ? thickLinePaint : thinLinePaint;
            canvas.drawLine(i * cellDimPx, 0f,
                    i * cellDimPx, getHeight(),
                    paint);
            canvas.drawLine(0, i * cellDimPx,
                    getWidth(), i * cellDimPx,
                    paint);
        }
    }

    private void fillCells(Canvas canvas) {
        if (cells == null) return;
        int selectedCellValue = selectedRow == -1 ? 0 : cells.get(selectedRow).get(selectedColumn).getValue();
        for (List<Cell> colCell : cells) {
            for (Cell cell : colCell) {
                int i = cell.getRow();
                int j = cell.getCol();
                if (cell.isStarterCell()) {
                    if (selectedCellValue != 0 && duplicateWithStart(selectedCellValue, i, j, cell.getValue()))
                        fillCell(canvas, i, j, duplicateStarterCellPaint);
                    else fillCell(canvas, i, j, starterCellPaint);
                } else if (i == selectedRow && j == selectedColumn) {
                    fillCell(canvas, i, j, selectedCellPaint);
                } else if (i == selectedRow || j == selectedColumn) {
                    if (selectedCellValue != 0 && selectedCellValue == cell.getValue())
                        fillCell(canvas, i, j, duplicateCellPaint);
                    else fillCell(canvas, i, j, conflictingCellPaint);
                } else if ((int) (i / sqrtSize) == (int) (selectedRow / sqrtSize)
                        && (int) (j / sqrtSize) == (int) (selectedColumn / sqrtSize)
                        && selectedRow != -1) {
                    if (selectedCellValue != 0 && selectedCellValue == cell.getValue())
                        fillCell(canvas, i, j, duplicateCellPaint);
                    else fillCell(canvas, i, j, conflictingCellPaint);
                }
            }
        }
    }

    private boolean duplicateWithStart(int selectedCellValue, int row, int col, int value) {
        if (selectedCellValue == value)
            if (row == selectedRow || col == selectedColumn)
                return true;
            else return (int) (row / sqrtSize) == (int) (selectedRow / sqrtSize)
                    && (int) (col / sqrtSize) == (int) (selectedColumn / sqrtSize)
                    && selectedRow != -1;
        return false;
    }

    private void fillCell(Canvas canvas, int row, int col, Paint paint) {
        canvas.drawRect(col * cellDimPx, row * cellDimPx,
                (col + 1) * cellDimPx, (row + 1) * cellDimPx,
                paint);
    }

    private void drawText(Canvas canvas) {
        if (cells == null) return;
        for (List<Cell> colCells : cells)
            for (Cell cell : colCells) {
                Rect textBounds = new Rect();
                if (cell.getValue() == 0) {
                    for (int noteValue : cell.getNotes()) {
                        String noteValueString = String.valueOf(noteValue);
                        int rowIndex = (noteValue - 1) / (int) sqrtSize;
                        int colIndex = (noteValue - 1) % (int) sqrtSize;
                        noteTextPaint.getTextBounds(noteValueString, 0, noteValueString.length(), textBounds);
                        float textWidth = noteTextPaint.measureText(noteValueString);
                        float textHeight = textBounds.height();
                        canvas.drawText(noteValueString,
                                (cell.getCol() * cellDimPx) + (noteDimPx * colIndex) + (noteDimPx / 2f) - (textWidth / 2f),
                                (cell.getRow() * cellDimPx) + (noteDimPx * rowIndex) + (noteDimPx / 2f) + (textHeight / 2f),
                                noteTextPaint);
                    }
                } else {
                    Paint paintToUse = cell.isStarterCell() ? starterCellTextPaint : textPaint;
                    String valueString = String.valueOf(cell.getValue());
                    paintToUse.getTextBounds(valueString, 0, valueString.length(), textBounds);
                    float textWidth = paintToUse.measureText(valueString);
                    float textHeight = textBounds.height();
                    canvas.drawText(valueString, (cell.getCol() * cellDimPx) + (cellDimPx / 2) - (textWidth / 2),
                            (cell.getRow() * cellDimPx) + (cellDimPx / 2) + (textHeight / 2),
                            paintToUse);
                }
            }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (boardDisabled) return false;
        if (event.getAction() == MotionEvent.ACTION_DOWN
                || (event.getAction() == MotionEvent.ACTION_MOVE
                && event.getY() <= getHeight())) {
            handleTouchEvent(event.getX(), event.getY());
            return true;
        }
        return false;
    }

    private void handleTouchEvent(float x, float y) {
        int touchedRow = (int) (y / cellDimPx);
        int touchedCol = (int) (x / cellDimPx);
        onCellTouchListener.cellTouched(touchedRow, touchedCol);
    }

    public void disableBoard() {
        boardDisabled = true;
    }

    public void enableBoard() {
        boardDisabled = false;
    }

    public void polishSelectedCell(int row, int col) {
        selectedRow = row;
        selectedColumn = col;
        invalidate();
    }

    public void updateCells(List<List<Cell>> cells) {
        this.cells = cells;
        invalidate();
    }

    public void setOnCellTouchListener(OnCellTouchListener listener) {
        onCellTouchListener = listener;
    }

    interface OnCellTouchListener {
        void cellTouched(int row, int col);
    }
}
