package com.optic.socialchat.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.optic.socialchat.R;
import com.optic.socialchat.activities.PublicChatDetailActivity;
import com.optic.socialchat.models.NewPublic;
import com.optic.socialchat.providers.NewPublicProvider;
import com.optic.socialchat.providers.UsersProvider;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class PublicChatAdapter extends FirestoreRecyclerAdapter<NewPublic, PublicChatAdapter.ViewHolder> {

    Context context;
    UsersProvider mUsersProvider;
    TextView mTextViewNumberFilter;

    public  PublicChatAdapter(FirestoreRecyclerOptions<NewPublic> options, Context context){
        super(options);
        this.context = context;
        mUsersProvider = new UsersProvider();

    }

    public  PublicChatAdapter(FirestoreRecyclerOptions<NewPublic> options, Context context, TextView textView){
        super(options);
        this.context = context;
        mUsersProvider = new UsersProvider();
        mTextViewNumberFilter = textView;

    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull NewPublic model) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String postId = document.getId();

        if (mTextViewNumberFilter != null){
            int numberFilter = getSnapshots().size();
            mTextViewNumberFilter.setText(String.valueOf(numberFilter));
        }


        holder.textViewMessage.setText(model.getMenssagepublic());
        if (model.getImage1() != null){
            if (!model.getImage1().isEmpty()){
                Picasso.with(context).load(model.getImage1()).into(holder.imageViewPost);
            }
        }
        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PublicChatDetailActivity.class);
                intent.putExtra("id", postId);
                context.startActivity(intent);
            }
        });

        getUserInfo(model.getIdUser(), holder);

    }

    private void getUserInfo(String idUser, ViewHolder holder) {
        mUsersProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("username")){
                        String username = documentSnapshot.getString("username");
                        holder.textViewUsername.setText(username.toUpperCase());
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_chatpublic, parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewMessage;
        ImageView imageViewPost;
        TextView textViewUsername;
        View viewHolder;


        public ViewHolder(View view){
            super(view);
            textViewMessage = view.findViewById(R.id.textViewPublicChat);
            textViewUsername = view.findViewById(R.id.textViewUsernamePublicChat);
            imageViewPost = view.findViewById(R.id.imageViewPostCard);
            viewHolder = view;
        }
    }

}
