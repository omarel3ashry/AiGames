package com.example.android.aigames;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.android.aigames.databinding.ActivityMainBinding;
import com.example.android.aigames.queens.QueensActivity;
import com.example.android.aigames.sudoku.SudokuActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mainBinding;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        bottomSheetBehavior = BottomSheetBehavior.from(mainBinding.sudokuBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        mainBinding.sudokuCardView.setOnClickListener(v ->
//                startActivity(new Intent(MainActivity.this, SudokuActivity.class)));
        mainBinding.sudokuCardView.setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });
        mainBinding.queenCardView.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, QueensActivity.class)));
//        startActivity(new Intent(MainActivity.this, QueensActivity.class));
        mainBinding.easyLevelRow.setOnClickListener(v -> startActivityWithLevel(1));
        mainBinding.mediumLevelRow.setOnClickListener(v -> startActivityWithLevel(2));
        mainBinding.hardLevelRow.setOnClickListener(v -> startActivityWithLevel(3));
        mainBinding.overkillLevelRow.setOnClickListener(v -> startActivityWithLevel(4));

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void startActivityWithLevel(int level) {
        Intent intent = new Intent(MainActivity.this, SudokuActivity.class);
        intent.putExtra("LEVEL_EXTRA", level);
        startActivity(intent);
    }
}