package com.shacharunik.EasyKnit;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shacharunik.EasyKnit.interfaces.ImageUploadCallback;
import com.shacharunik.EasyKnit.interfaces.PatternUploadCallback;
import com.shacharunik.EasyKnit.models.Pattern;

import java.util.ArrayList;
import java.util.List;

public class DataController {
    private static DataController instance = null;
    private Context context;
    public FirebaseDatabase mDatabase;
    public DatabaseReference myRef;
    public DatabaseReference patternsRef;

    public static FirebaseStorage mStorage;

    private DataController(Context context) {
        this.context = context;
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference().child("");
        patternsRef = mDatabase.getReference().child("patterns");
        mStorage = FirebaseStorage.getInstance();
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new DataController(context);
        }
    }

    public static DataController getInstance() {
        return instance;
    }

    public static void uploadProfileIMG(Uri imageUrl, String creator, ImageUploadCallback callback, ProgressDialog dialog) {
        StorageReference filepath = mStorage.getReference().child("profile").child(imageUrl.getLastPathSegment());
        filepath.putFile(imageUrl).addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String imageUrl1 = task.getResult().toString();
                DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference().child("profile").child(creator);
                DatabaseReference newPost = profileRef.push();
                newPost.child("image").setValue(imageUrl1);
                newPost.child("user").setValue(creator);
                callback.onImageUploadSuccess(imageUrl1);
                dialog.dismiss();
            } else {
                callback.onImageUploadFailure(task.getException());
                dialog.dismiss();
            }
        })).addOnFailureListener(e -> callback.onImageUploadFailure(e));
    }

    public static void uploadPatterns(Uri imageUrl, String name, String materials, ArrayList<String> instructions, String difficulty, ProgressDialog dialog, PatternUploadCallback callback) {
        StorageReference filepath = mStorage.getReference().child("patterns").child(imageUrl.getLastPathSegment());
        filepath.putFile(imageUrl).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference().child("patterns");
                    DatabaseReference newPost = profileRef.push();
                    newPost.child("name").setValue(name);
                    newPost.child("materials").setValue(materials);
                    newPost.child("instructions").setValue(instructions);
                    newPost.child("difficulty").setValue(difficulty);
                    newPost.child("img").setValue(task.getResult().toString());
                    newPost.child("creator").setValue(firebaseAuth.getCurrentUser().getEmail());
                } else {
                    callback.onPatternUploadFailure(task.getException());
                }

            });
            downloadUrl.addOnSuccessListener(uri -> {
                callback.onPatternUploadSuccess();
                dialog.dismiss();
            });
        });
    }
    public static void addOrRemovePatternToFavoriteByKey(String snapshotKey) {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("favorites").child(userEmail);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> textList = new ArrayList<>();

                // Retrieve the existing list from the database
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String value = snapshot.getValue(String.class);
                    if (value != null) {
                        textList.add(value);
                    }
                }

                // Check if the text value already exists in the list
                if (textList.contains(snapshotKey)) {
                    SignalGenerator.getInstance().showToast("Pattern removed from favorites", 1500);

                    textList.remove(snapshotKey);
                } else {
                    SignalGenerator.getInstance().showToast("Pattern added to favorites", 1500);
                    textList.add(snapshotKey);
                }

                // Save the updated list back to the database
                myRef.setValue(textList)
                        .addOnSuccessListener(aVoid -> {
                        })
                        .addOnFailureListener(e -> {
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    public static void getPatterns(List<Pattern> patterns, PatternAdapter patternAdapter, List<String> patternsKeys) {
        DatabaseReference patternRef = DataController.getInstance().mDatabase.getReference().child("patterns");
        patternRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot_f : snapshot.getChildren()) {
                    Pattern pattern = snapshot_f.getValue(Pattern.class);
                    patterns.add(pattern);
                    patternsKeys.add(snapshot_f.getKey());
                }
                patternAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void getPatternsNameByUser(List<String> patterns, ArrayAdapter arrayAdapter, List<Pattern> patternsKeys) {
        patterns.clear();
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DatabaseReference patternRef = DataController.getInstance().mDatabase.getReference().child("patterns");
        patternRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Pattern pattern = snapshot.getValue(Pattern.class);
                if (pattern.getCreator() != null && userEmail != null) {
                    if (pattern.getCreator().equals(userEmail)) {
                        patterns.add(pattern.getName());
                        arrayAdapter.notifyDataSetChanged();
                        patternsKeys.add(pattern);
                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void getPatternsFavorites(List<String> patternsName, ArrayAdapter arrayAdapter, List<Pattern> patternsKeys) {
        patternsKeys.clear();
        patternsName.clear();
        arrayAdapter.notifyDataSetChanged();
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference patternRef = DataController.getInstance().mDatabase.getReference().child("favorites").child(userUid);
        patternRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> favoritesPatternsKeys = new ArrayList<>();
                for (DataSnapshot snapshot_f : snapshot.getChildren()) {
                    String favoriteKey = snapshot_f.getValue(String.class);
                    favoritesPatternsKeys.add(favoriteKey);
                }
                getPatterns(patternsKeys, favoritesPatternsKeys, patternsName, arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static void getPatterns(List<Pattern> favoritesPatterns, List<String> keys, List<String> patternsName, ArrayAdapter arrayAdapter) {
        DatabaseReference patternRef = DataController.getInstance().mDatabase.getReference().child("patterns");
        patternRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot_f : snapshot.getChildren()) {
                    Pattern pattern = snapshot_f.getValue(Pattern.class);
                    for (int i = 0; i < keys.size(); i++) {
                        if (keys.get(i).equals(snapshot_f.getKey())) {
                            favoritesPatterns.add(pattern);
                            patternsName.add(pattern.getName());
                        }

                    }
                }
            arrayAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}