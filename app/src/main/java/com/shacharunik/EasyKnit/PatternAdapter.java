package com.shacharunik.EasyKnit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shacharunik.EasyKnit.models.Pattern;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PatternAdapter extends RecyclerView.Adapter<PatternAdapter.ViewHolder> {

    private Context context;
    private List<Pattern> patterns;

    public PatternAdapter(Context context, List<Pattern> patterns) {
        this.context = context;
        this.patterns = patterns;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pattern_card, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pattern pattern = patterns.get(position);
        holder.patternCard_TXT_name.setText(pattern.getName());
        holder.patternCard_TXT_difficulty.setText("difficulty: " + pattern.getDifficulty());
        holder.patternCard_TXT_materials.setText("materials: " + pattern.getMaterials());
        holder.patternCard_TXT_steps.setText("steps: " + pattern.getInstructions().size());
        holder.patternCard_TXT_creator.setText("author: " + pattern.getCreator());

        String imageUri = null;
        imageUri = pattern.getImg();
        Picasso.get().load(imageUri).into(holder.patternCard_IMG_img);
    }

    @Override
    public int getItemCount() {
        return patterns.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView patternCard_IMG_img;
        private TextView patternCard_TXT_name, patternCard_TXT_materials, patternCard_TXT_steps, patternCard_TXT_creator, patternCard_TXT_difficulty;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            patternCard_IMG_img = itemView.findViewById(R.id.patternCard_IMG_img);
            patternCard_TXT_name = itemView.findViewById(R.id.patternCard_TXT_name);
            patternCard_TXT_materials = itemView.findViewById(R.id.patternCard_TXT_materials);
            patternCard_TXT_steps = itemView.findViewById(R.id.patternCard_TXT_steps);
            patternCard_TXT_creator = itemView.findViewById(R.id.patternCard_TXT_creator);
            patternCard_TXT_difficulty = itemView.findViewById(R.id.patternCard_TXT_difficulty);
        }

    }
}
