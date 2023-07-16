package com.shacharunik.EasyKnit.ui;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.shacharunik.EasyKnit.DataController;
import com.shacharunik.EasyKnit.R;
import com.shacharunik.EasyKnit.SignalGenerator;
import com.shacharunik.EasyKnit.interfaces.PatternUploadCallback;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UploadFragment extends Fragment implements PatternUploadCallback {

    private Button UploadFrag_BTN_uploadPhoto, UploadFrag_BTN_addStep, UploadFrag_BTN_upload, UploadFrag_BTN_resetForm;
    private CircleImageView UploadFrag_IMG_PatternImage;
    private LinearLayout UploadFrag_LAYOUT_steps;
    private EditText UploadFrag_EDIT_materials, UploadFrag_EDIT_name;
    private Spinner UploadFrag_SPINNER_spnDifficulty;
    private ArrayList<EditText> instructions;
    private Uri imageUrl = null;
    private static final int CODE = 1;
    private int stepCounter = 0;

    public UploadFragment() {
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_upload, container, false);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        findViews(rootView);

        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        instructions = new ArrayList<>();
        //Upload a photo
        UploadFrag_BTN_uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhoto();
            }
        });

        UploadFrag_BTN_addStep.setOnClickListener(v -> {
            // Increment the step counter
            stepCounter++;

            // Create a new TextView for the step number
            TextView stepNumberTextView = new TextView(getContext());
            stepNumberTextView.setText("Step " + stepCounter);
            // Margins
            LinearLayout.LayoutParams numberLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            numberLayoutParams.setMargins(0, 0, 0, 7); // Specify left, top, right, bottom margins
            stepNumberTextView.setLayoutParams(numberLayoutParams);

            // Add the TextView to the LinearLayout
            UploadFrag_LAYOUT_steps.addView(stepNumberTextView);


            // Create a new EditText for the step description
            EditText stepDescriptionEditText = new EditText(getContext());

            LinearLayout.LayoutParams numberLayoutParams2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            numberLayoutParams2.setMargins(0, 0, 0, 10); // Specify left, top, right, bottom margins
            stepNumberTextView.setLayoutParams(numberLayoutParams2);

            // Add the EditText to the LinearLayout
            UploadFrag_LAYOUT_steps.addView(stepDescriptionEditText);

            instructions.add(stepDescriptionEditText);
        });

        String[] diff_items = new String[]{"Easy", "Medium", "Hard"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, diff_items);
        UploadFrag_SPINNER_spnDifficulty.setAdapter(adapter);

        UploadFrag_BTN_upload.setOnClickListener(v -> {
            String materials = UploadFrag_EDIT_materials.getText().toString();
            String name = UploadFrag_EDIT_name.getText().toString();
            String diff = UploadFrag_SPINNER_spnDifficulty.getSelectedItem().toString();
            if (stepCounter != 0 && !materials.isEmpty() && !instructions.isEmpty() && !name.isEmpty()) {
                ArrayList<String> instructionsStr = new ArrayList<>();
                for (int i=0; i < instructions.size(); i++) {
                    if (!instructions.get(i).getText().toString().isEmpty()) {
                        instructionsStr.add(instructions.get(i).getText().toString().trim());
                    }
                }
                ProgressDialog dialog = ProgressDialog.show(getContext(), "", "Uploading. Please wait...", true);
                DataController.uploadPatterns(imageUrl, name, materials, instructionsStr, diff, dialog, this);
            } else {
                SignalGenerator.getInstance().showToast("Make sure to fill the form", 2000);
            }
        });

        UploadFrag_BTN_resetForm.setOnClickListener(v -> {
            resetForm();
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            imageUrl = data.getData();
            UploadFrag_IMG_PatternImage.setImageURI(imageUrl);
        }
    }

    private void uploadPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, CODE);
    }

    private void findViews(View rootView) {
        UploadFrag_BTN_uploadPhoto = rootView.findViewById(R.id.UploadFrag_BTN_uploadPhoto);
        UploadFrag_BTN_addStep = rootView.findViewById(R.id.UploadFrag_BTN_addStep);
        UploadFrag_BTN_upload = rootView.findViewById(R.id.UploadFrag_BTN_upload);
        UploadFrag_IMG_PatternImage = rootView.findViewById(R.id.UploadFrag_IMG_PatternImage);
        UploadFrag_LAYOUT_steps = rootView.findViewById(R.id.UploadFrag_LAYOUT_steps);
        UploadFrag_EDIT_materials = rootView.findViewById(R.id.UploadFrag_EDIT_materials);
        UploadFrag_SPINNER_spnDifficulty = rootView.findViewById(R.id.UploadFrag_SPINNER_spnDifficulty);
        UploadFrag_EDIT_name = rootView.findViewById(R.id.UploadFrag_EDIT_name);
        UploadFrag_BTN_resetForm = rootView.findViewById(R.id.UploadFrag_BTN_resetForm);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onPatternUploadSuccess() {
        SignalGenerator.getInstance().showToast("The upload was successful", 1200);
        resetForm();
    }

    @Override
    public void onPatternUploadFailure(Exception exception) {
        SignalGenerator.getInstance().showToast("The upload was not successful", 1200);
    }

    private void resetForm() {
        UploadFrag_EDIT_name.setText("");
        instructions.clear();
        UploadFrag_LAYOUT_steps.removeAllViews();
        UploadFrag_EDIT_materials.setText("");
        UploadFrag_IMG_PatternImage.setImageURI(null);
        stepCounter = 0;
    }
}