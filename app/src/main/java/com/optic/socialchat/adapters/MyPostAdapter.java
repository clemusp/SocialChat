package com.optic.socialchat.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.optic.socialchat.R;
import com.optic.socialchat.activities.PublicChatDetailActivity;
import com.optic.socialchat.models.NewPublic;
import com.optic.socialchat.providers.AuthProvider;
import com.optic.socialchat.providers.NewPublicProvider;
import com.optic.socialchat.providers.UsersProvider;
import com.optic.socialchat.utils.RelativeTime;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostAdapter extends FirestoreRecyclerAdapter<NewPublic, MyPostAdapter.ViewHolder> {

    Context context;
    UsersProvider mUsersProvider;
    NewPublicProvider  mNewPublicProvider;
    AuthProvider mAuthProvider;

    public MyPostAdapter(FirestoreRecyclerOptions<NewPublic> options, Context context){
        super(options);
        this.context = context;
        mUsersProvider = new UsersProvider();
        mNewPublicProvider = new  NewPublicProvider();
        mAuthProvider = new AuthProvider();

    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull NewPublic model) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String postId = document.getId();
        String relativeTime = RelativeTime.getTimeAgo(model.getTimestamp(),context);
        holder.textViewRelativeTime.setText(relativeTime);
        holder.textViewMessage.setText(model.getMenssagepublic());
        if (model.getIdUser().equals(mAuthProvider.getUid())){
            holder.imageViewDelete.setVisibility(View.VISIBLE);
        }else{
            holder.imageViewDelete.setVisibility(View.GONE);
        }


        if (model.getImage1() != null){
            if (!model.getImage1().isEmpty()){
                Picasso.with(context).load(model.getImage1()).into(holder.circleImagePost);
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

        holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDelete(postId);
            }
        });

    }

    private void showConfirmDelete(String postId) {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Eliminar Publicacion")
                .setMessage("Estas seguro de realizar esta accion")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePost(postId);
                    }
                })
                .setNegativeButton("NO", null)
                .show();

    }

    private void deletePost(String postId) {
        mNewPublicProvider.delete(postId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(context, "El chat se elimino correctamente", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "No se pudo eliminar el chat", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_mypost, parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewMessage;
        TextView textViewRelativeTime;
        CircleImageView circleImagePost;
        ImageView imageViewDelete;
        View viewHolder;


        public ViewHolder(View view){
            super(view);
            textViewMessage = view.findViewById(R.id.textViewPublicChatMyPost);
            textViewRelativeTime = view.findViewById(R.id.textViewRelativeTimeMyPost);
            circleImagePost = view.findViewById(R.id.circleImageMyPost);
            imageViewDelete = view.findViewById(R.id.imageViewDeleteMyPost);
            viewHolder = view;
        }
    }

}
