package com.shacharunik.EasyKnit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shacharunik.EasyKnit.DataController;
import com.shacharunik.EasyKnit.Login;
import com.shacharunik.EasyKnit.PatternFullCard;
import com.shacharunik.EasyKnit.R;
import com.shacharunik.EasyKnit.databinding.FragmentProfileBinding;
import com.shacharunik.EasyKnit.models.Pattern;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private TextView ProfileFrag_TXT_profileName;
    private CircleImageView ProfileFrag_IMG_profileImage;
    private ListView ProfileFrag_LST_UploadedPatterns, ProfileFrag_LST_FavoritePatterns;
    private Button ProfileFrag_BTN_logout;
    private FirebaseAuth auth;
    private List<String> myPatterns = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private List<Pattern> patternsKeys = new ArrayList<>();

    private List<String> myFavoritesPatterns = new ArrayList<>();
    private ArrayAdapter<String> adapterFavorites;
    private List<Pattern> patternsFavoritesKeys = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        auth = FirebaseAuth.getInstance();

        findViews(view);
        findUserAndPicture();

        adapter= new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1,
                myPatterns);

        ProfileFrag_LST_UploadedPatterns.setAdapter(adapter);
        DataController.getInstance().getPatternsNameByUser(myPatterns, adapter, patternsKeys);

        ProfileFrag_LST_UploadedPatterns.setOnItemClickListener((parent, view1, position, id) -> {
            Intent patternPage = new Intent(getContext(), PatternFullCard.class);
            patternPage.putExtra("name", patternsKeys.get(position).getName());
            patternPage.putExtra("materials", patternsKeys.get(position).getMaterials());
            patternPage.putExtra("difficulty", patternsKeys.get(position).getDifficulty());
            patternPage.putExtra("creator", patternsKeys.get(position).getCreator());
            patternPage.putExtra("image", patternsKeys.get(position).getImg());
            patternPage.putExtra("steps", (ArrayList<String>)patternsKeys.get(position).getInstructions());
            startActivity(patternPage);
        });

        //favorites
        adapterFavorites= new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1,
                myFavoritesPatterns);

        ProfileFrag_LST_FavoritePatterns.setAdapter(adapterFavorites);
        DataController.getInstance().getPatternsFavorites(myFavoritesPatterns, adapterFavorites, patternsFavoritesKeys);

        ProfileFrag_LST_FavoritePatterns.setOnItemClickListener((parent, view1, position, id) -> {
            Intent patternPage = new Intent(getContext(), PatternFullCard.class);
            patternPage.putExtra("name", patternsFavoritesKeys.get(position).getName());
            patternPage.putExtra("materials", patternsFavoritesKeys.get(position).getMaterials());
            patternPage.putExtra("difficulty", patternsFavoritesKeys.get(position).getDifficulty());
            patternPage.putExtra("creator", patternsFavoritesKeys.get(position).getCreator());
            patternPage.putExtra("image", patternsFavoritesKeys.get(position).getImg());
            patternPage.putExtra("steps", (ArrayList<String>)patternsFavoritesKeys.get(position).getInstructions());
            startActivity(patternPage);
        });

        ProfileFrag_BTN_logout.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
            }
        });

        return view;
    }



    private void findUserAndPicture() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            ProfileFrag_TXT_profileName.setText(email);
            Log.d("login", currentUser.getProviderId());
            retrieveProfilePicture(currentUser.getUid());

        }
    }

    private void retrieveProfilePicture(String uid) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Construct the path to the profile picture using the UID
        String profilePath = "profile/" + uid;

        databaseRef.child(profilePath).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Iterate through the children under the UID node
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();
                        String profilePicturePath = profilePath + "/" + key + "/image";

                        databaseRef.child(profilePicturePath).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String profilePictureUrl = dataSnapshot.getValue(String.class);
                                    Picasso.get().load(profilePictureUrl).into(ProfileFrag_IMG_profileImage);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle any errors that occurred during the retrieval
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occurred during the retrieval
            }
        });
    }

    private void findViews(View view) {
        ProfileFrag_TXT_profileName = view.findViewById(R.id.ProfileFrag_TXT_profileName);
        ProfileFrag_IMG_profileImage = view.findViewById(R.id.ProfileFrag_IMG_profileImage);
        ProfileFrag_LST_UploadedPatterns = view.findViewById(R.id.ProfileFrag_LST_UploadedPatterns);
        ProfileFrag_LST_FavoritePatterns = view.findViewById(R.id.ProfileFrag_LST_FavoritePatterns);
        ProfileFrag_BTN_logout = view.findViewById(R.id.ProfileFrag_BTN_logout);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}