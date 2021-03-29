package com.optic.socialchat.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.optic.socialchat.R;
import com.optic.socialchat.adapters.MessageAdapter;
import com.optic.socialchat.adapters.MyPostAdapter;
import com.optic.socialchat.models.Chat;
import com.optic.socialchat.models.Message;
import com.optic.socialchat.models.NewPublic;
import com.optic.socialchat.providers.AuthProvider;
import com.optic.socialchat.providers.ChatsProvider;
import com.optic.socialchat.providers.MessageProvider;
import com.optic.socialchat.providers.UsersProvider;
import com.optic.socialchat.utils.RelativeTime;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String mExtraIdUser1;
    String mExtraIdUser2;
    String mExtraIdChat;

    ChatsProvider mChatsProvider;
    MessageProvider mMessageProvider;
    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;

    EditText mEditTextMessage;
    CircleImageView mCircleImageSendMessage;

    CircleImageView mCircleImageProfile;
    TextView mTextViewUsername;
    TextView mTextViewRelativeTime;
    ImageView mImageViewBack;
    RecyclerView mRecyclerViewMessage;

    LinearLayoutManager mLinearLayoutManager;

    MessageAdapter mAdapter;

    View mActionBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatsProvider = new ChatsProvider();
        mMessageProvider = new MessageProvider();
        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();



        mEditTextMessage = findViewById(R.id.editTextMessage);
        mCircleImageSendMessage = findViewById(R.id.circleImageSendMessage);
        mRecyclerViewMessage = findViewById(R.id.recyclerViewMessage);

        mLinearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerViewMessage.setLayoutManager(mLinearLayoutManager);


        mExtraIdUser1 = getIntent().getStringExtra("idUser1");
        mExtraIdUser2 = getIntent().getStringExtra("idUser2");
        mExtraIdChat = getIntent().getStringExtra("idChat");

        showCustomToolbar(R.layout.custom_chat_toolbar);

        mCircleImageSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        checkIfChatExist();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null){
            mAdapter.startListening();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    private void getMessageChat(){
        Query query = mMessageProvider.getMessageByChat(mExtraIdChat);
        FirestoreRecyclerOptions<Message> options =
                new FirestoreRecyclerOptions.Builder<Message>()
                        .setQuery(query, Message.class)
                        .build();

        mAdapter = new MessageAdapter(options, ChatActivity.this);
        mRecyclerViewMessage.setAdapter(mAdapter);
        mAdapter.startListening();
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                updateViewed();
                int numberMessage = mAdapter.getItemCount();
                int lastMessagePosition = mLinearLayoutManager.findLastVisibleItemPosition();

                if (lastMessagePosition == -1 || (positionStart >= (numberMessage -1) && lastMessagePosition == (positionStart -1))){
                    mRecyclerViewMessage.scrollToPosition(positionStart);
                }
            }
        });
    }

    private void sendMessage() {

        String textMessage = mEditTextMessage.getText().toString();
        if (!textMessage.isEmpty()){
            Message message = new Message();
            message.setIdChat(mExtraIdChat);
            if (mAuthProvider.getUid().equals(mExtraIdUser1)){
                message.setIdSender(mExtraIdUser1);
                message.setIdReceiver(mExtraIdUser2);
            }else {
                message.setIdSender(mExtraIdUser2);
                message.setIdReceiver(mExtraIdUser1);
            }
            message.setTimestamp(new Date().getTime());
            message.setViewed(false);
            message.setIdChat(mExtraIdChat);
            message.setMessage(textMessage);

            mMessageProvider.create(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        mEditTextMessage.setText("");
                        mAdapter.notifyDataSetChanged();
                    }else {
                        Toast.makeText(ChatActivity.this, "El mensaje no se pudo crear", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }

    }

    private void showCustomToolbar(int resource) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mActionBarView = inflater.inflate(resource, null);
        actionBar.setCustomView(mActionBarView);
        mCircleImageProfile = mActionBarView.findViewById(R.id.circleImageProfile);
        mTextViewUsername = mActionBarView.findViewById(R.id.textViewUsername);
        mTextViewRelativeTime = mActionBarView.findViewById(R.id.textViewRelativeTime);
        mImageViewBack = mActionBarView.findViewById(R.id.imageViewBack);

        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getUserInfo();
    }

    private void getUserInfo() {
        String idUserInfo = "";
        if (mAuthProvider.getUid().equals(mExtraIdUser1)){
            idUserInfo = mExtraIdUser2;
        }else {
            idUserInfo = mExtraIdUser1;
        }
        mUsersProvider.getUserRealTime(idUserInfo).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("username")){
                        String username = documentSnapshot.getString("username");
                        mTextViewUsername.setText(username);
                    }
                    if (documentSnapshot.contains("online")){
                        boolean online = documentSnapshot.getBoolean("online");
                        if (online){
                            mTextViewRelativeTime.setText("En linea");
                        }else if (documentSnapshot.contains("lastConnect")){
                            long lastConnect = documentSnapshot.getLong("lastConnect");
                            String relativeTime = RelativeTime.getTimeAgo(lastConnect, ChatActivity.this);
                            mTextViewRelativeTime.setText(relativeTime);
                        }


                    }
                    if (documentSnapshot.contains("image_profile")){
                        String imageProfile = documentSnapshot.getString("image_profile");
                        if (imageProfile != null){
                            if (!imageProfile.equals("")){
                                Picasso.with(ChatActivity.this).load(imageProfile).into(mCircleImageProfile);
                            }
                        }
                    }
                }
            }
        });
    }

    private void checkIfChatExist(){
        mChatsProvider.getChatByUser1AndUser2(mExtraIdUser1, mExtraIdUser2).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.size();
                if (size == 0){
                    createChat();
                }else{
                    mExtraIdChat = queryDocumentSnapshots.getDocuments().get(0).getId();
                    getMessageChat();
                    updateViewed();
                }
            }
        });
    }

    private void updateViewed() {
        String idSender = "";
        if (mAuthProvider.getUid().equals(mExtraIdUser1)){
            idSender = mExtraIdUser2;
        }else {
            idSender = mExtraIdUser1;
        }
        mMessageProvider.getMessageByChatAndSender(mExtraIdChat, idSender).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()){
                    mMessageProvider.updateViewed(document.getId(), true);
                }
            }
        });
    }

    private void createChat() {

        Chat chat = new Chat();
        chat.setIdUser1(mExtraIdUser1);
        chat.setIdUser2(mExtraIdUser2);
        chat.setWriting(false);
        chat.setTimestamp(new Date().getTime());
        chat.setId(mExtraIdUser1 + mExtraIdUser2);

        ArrayList<String> ids = new ArrayList<>();
        ids.add(mExtraIdUser1);
        ids.add(mExtraIdUser2);
        chat.setIds(ids);
        mChatsProvider.create(chat);
        mExtraIdChat = chat.getId();
        getMessageChat();

    }
}