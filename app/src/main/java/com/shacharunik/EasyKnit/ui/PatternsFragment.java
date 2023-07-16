package com.shacharunik.EasyKnit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shacharunik.EasyKnit.DataController;
import com.shacharunik.EasyKnit.PatternAdapter;
import com.shacharunik.EasyKnit.PatternFullCard;
import com.shacharunik.EasyKnit.R;
import com.shacharunik.EasyKnit.RecyclerItemClickListener;
import com.shacharunik.EasyKnit.databinding.FragmentPatternsBinding;
import com.shacharunik.EasyKnit.models.Pattern;

import java.util.ArrayList;
import java.util.List;

public class PatternsFragment extends Fragment {

    private FragmentPatternsBinding binding;
    private RecyclerView recyclerViewPatterns;
    private PatternAdapter patternAdapter;
    private List<Pattern> patterns;
    private List<String> patternsKeys = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPatternsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        recyclerViewPatterns = root.findViewById(R.id.fragPatterns_RECVIEW_recyclerViewPatterns);
        recyclerViewPatterns.setHasFixedSize(true);
        recyclerViewPatterns.setLayoutManager(new LinearLayoutManager(root.getContext()));

        patterns = new ArrayList<>();

        patternAdapter = new PatternAdapter(root.getContext(), patterns);
        recyclerViewPatterns.setAdapter(patternAdapter);
        DataController.getPatterns(patterns, patternAdapter, patternsKeys);
        recyclerViewPatterns.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), recyclerViewPatterns ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent patternPage = new Intent(getContext(), PatternFullCard.class);
                        patternPage.putExtra("name", patterns.get(position).getName());
                        patternPage.putExtra("materials", patterns.get(position).getMaterials());
                        patternPage.putExtra("difficulty", patterns.get(position).getDifficulty());
                        patternPage.putExtra("creator", patterns.get(position).getCreator());
                        patternPage.putExtra("image", patterns.get(position).getImg());
                        patternPage.putExtra("steps", (ArrayList<String>)patterns.get(position).getInstructions());
                        startActivity(patternPage);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        DataController.getInstance().addOrRemovePatternToFavoriteByKey(patternsKeys.get(position));
                    }
                })
        );

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}