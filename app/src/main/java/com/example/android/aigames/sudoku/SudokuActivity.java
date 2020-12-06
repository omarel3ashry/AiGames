package com.example.android.aigames.sudoku;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.android.aigames.R;
import com.example.android.aigames.databinding.ActivitySudokuBinding;
import com.example.android.aigames.sudoku.model.Cell;
import com.example.android.aigames.sudoku.tagSphere.VectorDrawableTagItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SudokuActivity extends AppCompatActivity {

    ActivitySudokuBinding sudokuBinding;
    private SudokuViewModel viewModel;
    private int level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sudokuBinding = DataBindingUtil.setContentView(this, R.layout.activity_sudoku);
        setSupportActionBar(sudokuBinding.sudokuToolbar);
        sudokuBinding.sudokuBackImgView.setOnClickListener(view -> onBackPressed());
        viewModel = new ViewModelProvider(this).get(SudokuViewModel.class);
        level = getIntent().getIntExtra("LEVEL_EXTRA", 1);
        viewModel.sudokuGame.newGame(this.level);
        viewModel.sudokuGame.selectedCellLiveData.observe(this, this::polishSelectedCell);
        viewModel.sudokuGame.cellsLiveData.observe(this, this::updateCells);
        viewModel.sudokuGame.isTakingNoteLiveData.observe(this, this::polishTakingNote);
        viewModel.sudokuGame.isGameSolvedLiveData.observe(this, b -> {
            if (b) showSuccessPolish();
        });
        sudokuBinding.sudokuBoardView.setOnCellTouchListener((row, col) -> viewModel.sudokuGame.updateSelectedCell(row, col));
        initSphereView();
        sudokuBinding.noteBtnToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) ->
                viewModel.sudokuGame.changeTakingNoteState());
        sudokuBinding.hintBtn.setOnClickListener(v -> viewModel.sudokuGame.fillHints());
        sudokuBinding.deleteBtn.setOnClickListener(v -> viewModel.sudokuGame.delete());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sudoku_items, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newGameAction:
                playAgainPolish();
                return true;
            case R.id.solverAction:
                viewModel.sudokuGame.solveTheGame();
                return true;
        }
        return false;
    }

    private void showSuccessPolish() {
        sudokuBinding.deleteBtn.setEnabled(false);
        sudokuBinding.noteToggleBtn.setEnabled(false);
        sudokuBinding.successLayout.setVisibility(View.VISIBLE);
        sudokuBinding.numSphereView.setVisibility(View.GONE);
        sudokuBinding.lottieAnimationView.playAnimation();
        sudokuBinding.playAgainBtn.setOnClickListener(v -> playAgainPolish());
        sudokuBinding.exitBtn.setOnClickListener(v -> finish());
    }

    private void playAgainPolish() {
        sudokuBinding.deleteBtn.setEnabled(true);
        sudokuBinding.noteToggleBtn.setEnabled(true);
        sudokuBinding.successLayout.setVisibility(View.GONE);
        sudokuBinding.numSphereView.setVisibility(View.VISIBLE);
        sudokuBinding.lottieAnimationView.clearAnimation();
        viewModel.sudokuGame.newGame(level);
    }

    private void polishSelectedCell(Pair<Integer, Integer> pair) {
        if (pair != null) sudokuBinding.sudokuBoardView.polishSelectedCell(pair.first, pair.second);
    }

    private void polishTakingNote(boolean isTakingNote) {

    }

    private void updateCells(List<List<Cell>> cells) {
        if (cells != null) sudokuBinding.sudokuBoardView.updateCells(cells);
    }

    private void initSphereView() {
        List<Drawable> numericalDrawables = new ArrayList<>
                (Arrays.asList(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_num_1, null),
                        ResourcesCompat.getDrawable(getResources(), R.drawable.ic_num_2, null),
                        ResourcesCompat.getDrawable(getResources(), R.drawable.ic_num_3, null),
                        ResourcesCompat.getDrawable(getResources(), R.drawable.ic_num_4, null),
                        ResourcesCompat.getDrawable(getResources(), R.drawable.ic_num_5, null),
                        ResourcesCompat.getDrawable(getResources(), R.drawable.ic_num_6, null),
                        ResourcesCompat.getDrawable(getResources(), R.drawable.ic_num_7, null),
                        ResourcesCompat.getDrawable(getResources(), R.drawable.ic_num_8, null),
                        ResourcesCompat.getDrawable(getResources(), R.drawable.ic_num_9, null)));
        List<VectorDrawableTagItem> drawableTagItems = new ArrayList<>();
        for (int i = 0; i < numericalDrawables.size(); i++) {
            Drawable drawable = numericalDrawables.get(i);
            drawableTagItems.add(new VectorDrawableTagItem(drawable, String.valueOf(i + 1)));
        }
        sudokuBinding.numSphereView.addTagList(drawableTagItems);
        sudokuBinding.numSphereView.setRadius(5f);
        sudokuBinding.numSphereView.setOnTagTapListener(tagItem ->
                viewModel.sudokuGame.handleInput(Integer.parseInt(tagItem.getTagId())));

    }
}