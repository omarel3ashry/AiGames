package com.example.android.aigames.queens;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.android.aigames.R;
import com.example.android.aigames.databinding.ActivityQueensBinding;

public class QueensActivity extends AppCompatActivity {

    ActivityQueensBinding queensBinding;
    private QueensViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queensBinding = DataBindingUtil.setContentView(this, R.layout.activity_queens);
        queensBinding.queensBackImgView.setOnClickListener(v -> finish());
        viewModel = new ViewModelProvider(this).get(QueensViewModel.class);
        viewModel.queensGame.selectedChessCellLiveData.observe(this, this::polishSelectedChessCell);
        viewModel.queensGame.chessCellsLiveData.observe(this, this::updateChessCells);
        viewModel.queensGame.isGameSolvedLiveData.observe(this, b -> {
            if (b) showSuccessPolish();
        });
        queensBinding.queensBoardView.setOnCellTouchListener((row, col) ->
                viewModel.queensGame.updateSelectedChessCell(row, col));
        queensBinding.placeQueenBtn.setOnClickListener(v -> viewModel.queensGame.handleInput());
        queensBinding.checkQueensBtn.setOnClickListener(v -> {
            queensBinding.solutionsTxtView.setVisibility(View.VISIBLE);
            queensBinding.setSolutionsFound(viewModel.queensGame.getSolutionsCount());
        });
        queensBinding.solveQueensBtn.setOnClickListener(v -> {
            if (viewModel.queensGame.getSolutionsCount() == 0)
                Toast.makeText(QueensActivity.this, "no solutions for your inputs!", Toast.LENGTH_LONG).show();
            else viewModel.queensGame.solveTheGame();
        });
        queensBinding.deleteBtn.setOnClickListener(v -> viewModel.queensGame.delete());
    }

    private void polishSelectedChessCell(Pair<Integer, Integer> pair) {
        if (pair != null) queensBinding.queensBoardView.polishSelectedCell(pair.first, pair.second);
    }

    private void updateChessCells(boolean[][] cells) {
        if (cells != null) queensBinding.queensBoardView.updateCells(cells);
        queensBinding.solutionsTxtView.setVisibility(View.INVISIBLE);
    }

    private void showSuccessPolish() {
        queensBinding.deleteBtn.setEnabled(false);
        queensBinding.checkQueensBtn.setEnabled(false);
        queensBinding.solveQueensBtn.setEnabled(false);
        queensBinding.successLayout.setVisibility(View.VISIBLE);
        queensBinding.solutionsTxtView.setVisibility(View.INVISIBLE);
        queensBinding.placeQueenBtn.setVisibility(View.GONE);
        queensBinding.lottieAnimationView.playAnimation();
        queensBinding.playAgainBtn.setOnClickListener(v -> playAgainPolish());
        queensBinding.exitBtn.setOnClickListener(v -> finish());
    }

    private void playAgainPolish() {
        queensBinding.deleteBtn.setEnabled(true);
        queensBinding.checkQueensBtn.setEnabled(true);
        queensBinding.solveQueensBtn.setEnabled(true);
        queensBinding.successLayout.setVisibility(View.GONE);
        queensBinding.solutionsTxtView.setVisibility(View.INVISIBLE);
        queensBinding.placeQueenBtn.setVisibility(View.VISIBLE);
        queensBinding.lottieAnimationView.clearAnimation();
        viewModel.queensGame.newGame();
    }
}