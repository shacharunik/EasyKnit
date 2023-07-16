package com.shacharunik.EasyKnit;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PatternFullCard extends AppCompatActivity {
    private TextView patternFullCard_TXT_name, patternFullCard_TXT_materials, patternFullCard_TXT_difficulty, patternFullCard_TXT_creator;
    private ImageView patternFullCard_IMG_img;
    private LinearLayout patternFullCard_LAYOUT_steps;
    private int stepCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pattern_full_card);
        findViews();
        stepCounter = 1;
        Intent prevIntent = getIntent();
        String name = prevIntent.getStringExtra("name");
        String materials = prevIntent.getStringExtra("materials");
        String difficulty = prevIntent.getStringExtra("difficulty");
        String creator = prevIntent.getStringExtra("creator");
        String image = prevIntent.getStringExtra("image");
        ArrayList<String> steps = (ArrayList<String>) prevIntent.getSerializableExtra("steps");

        patternFullCard_TXT_name.setText(name);
        patternFullCard_TXT_difficulty.setText("Difficulty: " + difficulty);
        patternFullCard_TXT_materials.setText("Materials: " + materials);
        patternFullCard_TXT_creator.setText(creator);
        for (int i=0;i < steps.size(); i++) {
            addStepFromList(steps.get(i));
            stepCounter++;
        }

        Picasso.get().load(image).into(patternFullCard_IMG_img);
    }

    private void findViews() {
        patternFullCard_TXT_name = findViewById(R.id.patternFullCard_TXT_name);
        patternFullCard_TXT_difficulty = findViewById(R.id.patternFullCard_TXT_difficulty);
        patternFullCard_TXT_materials = findViewById(R.id.patternFullCard_TXT_materials);
        patternFullCard_TXT_creator = findViewById(R.id.patternFullCard_TXT_creator);
        patternFullCard_IMG_img = findViewById(R.id.patternFullCard_IMG_img);
        patternFullCard_LAYOUT_steps = findViewById(R.id.patternFullCard_LAYOUT_steps);
    }

    private void addStepFromList(String step) {
        TextView stepNumberTextView = new TextView(PatternFullCard.this);
        stepNumberTextView.setTypeface(null, Typeface.BOLD);
        stepNumberTextView.setText("Step " + stepCounter);
        // Margins
        LinearLayout.LayoutParams numberLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        numberLayoutParams.setMargins(0, 0, 0, 7); // Specify left, top, right, bottom margins
        stepNumberTextView.setLayoutParams(numberLayoutParams);

        // Add the TextView to the LinearLayout
        patternFullCard_LAYOUT_steps.addView(stepNumberTextView);


        // Create a new EditText for the step description
        TextView stepDescriptionText = new TextView(PatternFullCard.this);
        stepDescriptionText.setText(step);

        LinearLayout.LayoutParams numberLayoutParams2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        numberLayoutParams2.setMargins(0, 0, 0, 10); // Specify left, top, right, bottom margins
        stepNumberTextView.setLayoutParams(numberLayoutParams2);

        // Add the EditText to the LinearLayout
        patternFullCard_LAYOUT_steps.addView(stepDescriptionText);
    }

}