package com.optic.socialchat.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.optic.socialchat.R;
import com.optic.socialchat.adapters.MyPostAdapter;
import com.optic.socialchat.models.NewPublic;
import com.optic.socialchat.providers.AuthProvider;
import com.optic.socialchat.providers.NewPublicProvider;
import com.optic.socialchat.providers.UsersProvider;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    LinearLayout mLinearLayoutEditProfile;
    TextView mTextViewUsername;
    TextView mTextViewEmail;
    TextView mTextViewPhone;
    TextView mTextViewPostNumber;
    TextView mTextViewPostExist;
    CircleImageView mCircleImageViewProfile;
    ImageView mImageViewCover;
    RecyclerView mRecyclerView;
    Toolbar mToolbar;
    FloatingActionButton mFabChat;

    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    NewPublicProvider mNewPublicProvider;

    MyPostAdapter mAdapter;

    String mExtraIdUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mLinearLayoutEditProfile = findViewById(R.id.linearLayoutEditProfile);
        mTextViewEmail = findViewById(R.id.textViewEmail);
        mTextViewUsername = findViewById(R.id.textViewUsername);
        mTextViewPhone = findViewById(R.id.textViewPhone);
        mTextViewPostNumber = findViewById(R.id.textViewPostNumber);
        mTextViewPostExist = findViewById(R.id.textViewPostExist);
        mCircleImageViewProfile = findViewById(R.id.circleImageProfile);
        mImageViewCover = findViewById(R.id.imageViewCover);
        mRecyclerView = findViewById(R.id.recyclerViewMyPost);
        mFabChat = findViewById(R.id.fabChat);
        mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UserProfileActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);


        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        mNewPublicProvider = new NewPublicProvider();

        mExtraIdUser = getIntent().getStringExtra("idUser");

        if (mAuthProvider.getUid().equals(mExtraIdUser)){
            mFabChat.setEnabled(false);
        }

        mFabChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChatActivity();
            }
        });

        getUser();
        getPostNumber();
        checkIfExistPost();
    }

    private void goToChatActivity() {
        Intent intent = new Intent(UserProfileActivity.this, ChatActivity.class);
        intent.putExtra("idUser1", mAuthProvider.getUid());
        intent.putExtra("idUser2", mExtraIdUser);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mNewPublicProvider.getPostByUser(mExtraIdUser);
        FirestoreRecyclerOptions<NewPublic> options =
                new FirestoreRecyclerOptions.Builder<NewPublic>()
                        .setQuery(query, NewPublic.class)
                        .build();

        mAdapter = new MyPostAdapter(options, UserProfileActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    private void checkIfExistPost() {
        mNewPublicProvider.getPostByUser(mExtraIdUser).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                int numberPost = queryDocumentSnapshots.size();
                if (numberPost > 0){
                    mTextViewPostExist.setText("Mensajes Publicos");
                    mTextViewPostExist.setTextColor(Color.RED);
                }else {
                    mTextViewPostExist.setText("No hay Mensajes Publicos");
                    mTextViewPostExist.setTextColor(Color.GRAY);
                }
            }
        });

    }


    private void getPostNumber(){
        mNewPublicProvider.getPostByUser(mExtraIdUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberPost = queryDocumentSnapshots.size();
                mTextViewPostNumber.setText(String.valueOf(numberPost));
            }
        });
    }

    private void getUser(){
        mUsersProvider.getUser(mExtraIdUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("email")){
                        String email = documentSnapshot.getString("email");
                        mTextViewEmail.setText(email);
                    }
                    if (documentSnapshot.contains("phone")){
                        String phone = documentSnapshot.getString("phone");
                        mTextViewPhone.setText(phone);
                    }
                    if (documentSnapshot.contains("username")){
                        String username = documentSnapshot.getString("username");
                        mTextViewUsername.setText(username);
                    }
                    if (documentSnapshot.contains("image_profile")){
                        String imageProfile = documentSnapshot.getString("image_profile");
                        if (imageProfile != null){
                            if (!imageProfile.isEmpty()){
                                Picasso.with(UserProfileActivity.this).load(imageProfile).into(mCircleImageViewProfile);
                            }
                        }
                    }
                    if (documentSnapshot.contains("image_cover")){
                        String imageCover = documentSnapshot.getString("image_cover");
                        if (imageCover != null){
                            if (!imageCover.isEmpty()){
                                Picasso.with(UserProfileActivity.this).load(imageCover).into(mImageViewCover);
                            }
                        }
                    }
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }
}
