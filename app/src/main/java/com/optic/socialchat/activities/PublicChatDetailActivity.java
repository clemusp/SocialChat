package com.optic.socialchat.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.optic.socialchat.R;
import com.optic.socialchat.adapters.CommentAdapter;
import com.optic.socialchat.adapters.PublicChatAdapter;
import com.optic.socialchat.adapters.SliderAdapter;
import com.optic.socialchat.models.Comment;
import com.optic.socialchat.models.FCMBody;
import com.optic.socialchat.models.FCMResponse;
import com.optic.socialchat.models.NewPublic;
import com.optic.socialchat.models.SliderItem;
import com.optic.socialchat.providers.AuthProvider;
import com.optic.socialchat.providers.CommentsProvider;
import com.optic.socialchat.providers.NewPublicProvider;
import com.optic.socialchat.providers.NotificationProvider;
import com.optic.socialchat.providers.TokenProvider;
import com.optic.socialchat.providers.UsersProvider;
import com.optic.socialchat.utils.RelativeTime;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.security.AccessController.getContext;

public class PublicChatDetailActivity extends AppCompatActivity {

    SliderView mSliderView;
    SliderAdapter mSliderAdapter;
    List<SliderItem> mSliderItems = new ArrayList<>();

    NewPublicProvider mNewPublicProvider;
    UsersProvider mUsersProvider;
    CommentsProvider mCommentsProvider;
    AuthProvider mAuthProvider;
    NotificationProvider mNotificationProvider;
    TokenProvider mTokenProvider;

    CommentAdapter mAdapter;

    String mExtraPostId;

    TextView mTextViewPublicChat;
    TextView mTextViewUsername;
    TextView mTextViewPhone;
    TextView mTextViewNameCategory;
    TextView mTextViewRelativeTime;
    ImageView mImageViewCategory;
    CircleImageView mCircleImageViewProfile;
    Button mButtonShowProfile;
    FloatingActionButton mfabComment;
    RecyclerView mRecyclerView;
    Toolbar mToolbar;

    String mIdUser = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_chat_detail);

        mSliderView = findViewById(R.id.imageSlider);
        mNewPublicProvider = new NewPublicProvider();
        mUsersProvider = new UsersProvider();
        mCommentsProvider = new CommentsProvider();
        mAuthProvider = new AuthProvider();
        mNotificationProvider = new NotificationProvider();
        mTokenProvider = new TokenProvider();

        mTextViewNameCategory = findViewById(R.id.textViewNameCategory);
        mTextViewPublicChat = findViewById(R.id.textViewPublicChat);
        mTextViewPhone = findViewById(R.id.textViewPhone);
        mTextViewUsername = findViewById(R.id.textViewUsername);
        mTextViewRelativeTime = findViewById(R.id.textViewRelativeTime);
        mImageViewCategory = findViewById(R.id.imageViewCategory);
        mCircleImageViewProfile = findViewById(R.id.circleImageProfile);
        mButtonShowProfile = findViewById(R.id.btnShowProfile);
        mfabComment = findViewById(R.id.fabComment);
        mRecyclerView = findViewById(R.id.recyclerViewComments);
        mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PublicChatDetailActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mExtraPostId = getIntent().getStringExtra("id");

        mfabComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogComment();
            }
        });

        mButtonShowProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToShowProfile();
            }
        });



        getPost();

    }

    @Override
    protected void onStart() {
        super.onStart();

        Query query = mCommentsProvider.getCommentByPost(mExtraPostId);
        FirestoreRecyclerOptions<Comment> options =
                new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();

        mAdapter = new CommentAdapter(options, PublicChatDetailActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    private void showDialogComment() {
        AlertDialog.Builder alert = new AlertDialog.Builder(PublicChatDetailActivity.this);
        alert.setTitle("RESPONDE");
        alert.setMessage("Escribe tu mensaje");

        final EditText editText = new EditText(PublicChatDetailActivity.this);
        editText.setHint("Mensaje");


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(36,0,36,36);
        editText.setLayoutParams(params);
        RelativeLayout container = new RelativeLayout(PublicChatDetailActivity.this);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        container.setLayoutParams(relativeParams);
        container.addView(editText);

        alert.setView(container);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String value = editText.getText().toString();
                if (!value.isEmpty()){
                    createComment(value);
                }
                Toast.makeText(PublicChatDetailActivity.this, "Debe escribir su mensaje", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

            }
        });

        alert.show();
    }

    private void createComment(final String value) {
        Comment comment = new Comment();
        comment.setComment(value);
        comment.setIdPost(mExtraPostId);
        comment.setIdUser(mAuthProvider.getUid());
        comment.setTimestamp(new Date().getTime());
        mCommentsProvider.create(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                sendNotification(value);
                if (task.isSuccessful()){
                    Toast.makeText(PublicChatDetailActivity.this, "El mensaje se recivio exitosamente", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(PublicChatDetailActivity.this, "El mensaje no se logro recibir", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendNotification(final String comment) {

        if (mIdUser == null){
            return;
        }
        mTokenProvider.getToken(mIdUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("token")){
                        String token = documentSnapshot.getString("token");
                        Map<String, String> data = new HashMap<>();
                        data.put("title", "NUEVO MENSAJE PUBLICO");
                        data.put("body", comment);
                        FCMBody body = new FCMBody(token, "high", "4500s", data);
                        mNotificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                            @Override
                            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                if (response.body() != null){
                                    if (response.body().getSuccess() == 1){
                                        Toast.makeText(PublicChatDetailActivity.this, "La notificacion se envio correctamente", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(PublicChatDetailActivity.this, "La notificacion no se pudo enviar", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(PublicChatDetailActivity.this, "La notificacion no se pudo enviar", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<FCMResponse> call, Throwable t) {

                            }
                        });
                    }
                }else {
                    Toast.makeText(PublicChatDetailActivity.this, "El token de notificaciones de usuario no existe", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void goToShowProfile() {
        if (!mIdUser.equals("")){
            Intent intent = new Intent(PublicChatDetailActivity.this, UserProfileActivity.class);
            intent.putExtra("idUser",mIdUser);
            startActivity(intent);
        }else {
            Toast.makeText(this, "El Id de usuario aun no se carga", Toast.LENGTH_SHORT).show();
        }

    }

    private void instanceSlider(){
        mSliderAdapter = new SliderAdapter(PublicChatDetailActivity.this, mSliderItems);
        mSliderView.setSliderAdapter(mSliderAdapter);
        mSliderView.setIndicatorAnimation(IndicatorAnimationType.THIN_WORM);//no es igual IndicatorAnimatios
        mSliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        mSliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        mSliderView.setIndicatorSelectedColor(Color.WHITE);
        mSliderView.setIndicatorUnselectedColor(Color.GRAY);
        mSliderView.setScrollTimeInSec(3);
        mSliderView.setAutoCycle(true);
        mSliderView.startAutoCycle();
    }

    private void getPost(){
        mNewPublicProvider.getPostById(mExtraPostId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){

                    if (documentSnapshot.contains("image1")){
                        String image1 = documentSnapshot.getString("image1");
                        SliderItem item = new SliderItem();
                        item.setImageurl(image1);
                        mSliderItems.add(item);
                    }
                    if (documentSnapshot.contains("image2")){
                        String image2 = documentSnapshot.getString("image2");
                        SliderItem item = new SliderItem();
                        item.setImageurl(image2);
                        mSliderItems.add(item);
                    }

                    if (documentSnapshot.contains("menssagepublic")){
                        String publiChat = documentSnapshot.getString("menssagepublic");
                        mTextViewPublicChat.setText(publiChat);
                    }
                    if (documentSnapshot.contains("category")){
                        String category = documentSnapshot.getString("category");
                        mTextViewNameCategory.setText(category);

                        if (category.equals("Colegio")){
                            mImageViewCategory.setImageResource(R.drawable.colegio);
                        }
                        else if (category.equals("Padres")){
                            mImageViewCategory.setImageResource(R.drawable.padres);
                        }
                        else if (category.equals("Maestros")){
                            mImageViewCategory.setImageResource(R.drawable.teacher);
                        }
                        else if (category.equals("Alumnos")){
                            mImageViewCategory.setImageResource(R.drawable.student);
                        }
                    }
                    if (documentSnapshot.contains("idUser")){
                        mIdUser = documentSnapshot.getString("idUser");
                        getUserInfo(mIdUser);
                    }
                    if (documentSnapshot.contains("timestamp")){
                        long timestamp = documentSnapshot.getLong("timestamp");
                        String relativeTime = RelativeTime.getTimeAgo(timestamp, PublicChatDetailActivity.this);
                        mTextViewRelativeTime.setText(relativeTime);
                    }

                    instanceSlider();
                }
            }
        });
    }

    private void getUserInfo(String idUser) {
        mUsersProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("username")){
                        String username = documentSnapshot.getString("username");
                        mTextViewUsername.setText(username);
                    }
                    if (documentSnapshot.contains("phone")){
                        String phone = documentSnapshot.getString("phone");
                        mTextViewPhone.setText(phone);
                    }
                    if (documentSnapshot.contains("image_profile")){
                        String imageProfile = documentSnapshot.getString("image_profile");
                        Picasso.with(PublicChatDetailActivity.this).load(imageProfile).into(mCircleImageViewProfile);
                    }
                }
            }
        });
    }
}