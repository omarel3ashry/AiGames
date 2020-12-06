package com.example.android.aigames.queens;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.example.android.aigames.R;

/**
 * Created by Omar Elashry on 2020-11-20.
 */
public class QueensBoardView extends View {

    private Paint thickLinePaint;
    private Paint thinLinePaint;
    private Paint blackCellPaint;
    private Paint mixedCellPaint;
    private Paint selectedCellPaint;
    private Paint conflictingCellPaint;
    private Paint duplicateCellPaint;
    private Paint textPaint;
    private Paint starterCellTextPaint;
    private int boardSize = 8;
    private double sqrtSize = Math.sqrt(boardSize);

    private float cellDimPx = 0f;

    private int selectedColumn = -1;
    private int selectedRow = -1;
    private boolean[][] cells;
    private OnCellTouchListener onCellTouchListener;
    private boolean boardDisabled = false;

    public QueensBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        thickLinePaint = new Paint();
        thickLinePaint.setStyle(Paint.Style.STROKE);
        thickLinePaint.setStrokeWidth(5f);
        thickLinePaint.setColor(getResources().getColor(R.color.prussianBlue, null));

        thinLinePaint = new Paint();
        thinLinePaint.setStyle(Paint.Style.STROKE);
        thinLinePaint.setStrokeWidth(2f);
        thinLinePaint.setColor(getResources().getColor(R.color.black, null));

        blackCellPaint = new Paint();
        blackCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        blackCellPaint.setColor(getResources().getColor(R.color.lighterGray2, null));

        mixedCellPaint = new Paint();
        mixedCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mixedCellPaint.setColor(getResources().getColor(R.color.grayedRose, null));

        selectedCellPaint = new Paint();
        selectedCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        selectedCellPaint.setColor(getResources().getColor(R.color.lighterRose2, null));

        conflictingCellPaint = new Paint();
        conflictingCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        conflictingCellPaint.setColor(getResources().getColor(R.color.lighterRose2_1, null));

        duplicateCellPaint = new Paint();
        duplicateCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        duplicateCellPaint.setColor(getResources().getColor(R.color.lightRed, null));

        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setColor(getResources().getColor(R.color.black, null));

        starterCellTextPaint = new Paint();
        starterCellTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        starterCellTextPaint.setColor(getResources().getColor(R.color.black, null));
        starterCellTextPaint.setTypeface(Typeface.DEFAULT_BOLD);

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
//        drawText(canvas);
        drawQueens(canvas);
        super.onDraw(canvas);
    }

    private void measurementsHelper(int width) {
        cellDimPx = (float) width / boardSize;
        textPaint.setTextSize(cellDimPx / 1.5f);
        starterCellTextPaint.setTextSize(cellDimPx / 1.5f);
    }

    private void drawLines(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), thickLinePaint);
        for (int i = 0; i < boardSize; i++) {
            canvas.drawLine(i * cellDimPx, 0f,
                    i * cellDimPx, getHeight(),
                    thinLinePaint);
            canvas.drawLine(0, i * cellDimPx,
                    getWidth(), i * cellDimPx,
                    thinLinePaint);
        }
    }

    private void fillCells(Canvas canvas) {
        if (cells == null) return;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if ((i + j) % 2 != 0)
                    fillCell(canvas, i, j, blackCellPaint);
                if (i == selectedRow && j == selectedColumn) {
                    fillCell(canvas, i, j, selectedCellPaint);
                } else if (i == selectedRow || j == selectedColumn) {
                    if ((i + j) % 2 != 0)
                        fillCell(canvas, i, j, mixedCellPaint);
                    else fillCell(canvas, i, j, conflictingCellPaint);
                } else if (Math.abs(i - selectedRow) == Math.abs(j - selectedColumn) && selectedRow != -1) {
                    if ((i + j) % 2 != 0)
                        fillCell(canvas, i, j, mixedCellPaint);
                    else fillCell(canvas, i, j, conflictingCellPaint);
                }
            }
        }
    }


    private void fillCell(Canvas canvas, int row, int col, Paint paint) {
        canvas.drawRect(col * cellDimPx, row * cellDimPx,
                (col + 1) * cellDimPx, (row + 1) * cellDimPx,
                paint);
    }

    private void drawQueens(Canvas canvas) {
        if (cells == null) return;
        for (int i = 0; i < boardSize; i++)
            for (int j = 0; j < boardSize; j++) {
                if (cells[i][j]) {
                    Drawable queenDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_queen_small_gold, null);
                    assert queenDrawable != null;
                    canvas.save();
                    canvas.translate((j * cellDimPx) + (cellDimPx / 2) - ((float) queenDrawable.getIntrinsicWidth() / 2),
                            (i * cellDimPx) + (cellDimPx / 2) - ((float) queenDrawable.getIntrinsicHeight()) / 2);
                    queenDrawable.setBounds(
                            0,
                            0,
                            queenDrawable.getIntrinsicWidth(),
                            queenDrawable.getIntrinsicHeight());
                    queenDrawable.draw(canvas);
                    canvas.restore();
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

    public void updateCells(boolean[][] cells) {
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
