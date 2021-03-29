package com.optic.socialchat.fragments;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.PostProcessor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.optic.socialchat.R;
import com.optic.socialchat.activities.MainActivity;
import com.optic.socialchat.activities.NewActivity;
import com.optic.socialchat.adapters.PublicChatAdapter;
import com.optic.socialchat.models.NewPublic;
import com.optic.socialchat.providers.AuthProvider;
import com.optic.socialchat.providers.NewPublicProvider;


public class ChatPublicFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener{

    View mView;
    FloatingActionButton mNewChat;
    MaterialSearchBar mSearchBar;

    AuthProvider mAuthProvider;
    RecyclerView mRecyclerView;
    NewPublicProvider mNewPublicProvider;
    PublicChatAdapter mPublicChatAdapter;
    PublicChatAdapter mPublicChatAdapterSearch;



    public ChatPublicFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        mView = inflater.inflate(R.layout.fragment_chat_public, container, false);
        mNewChat = mView.findViewById(R.id.newChat);
        mRecyclerView = mView.findViewById(R.id.recyclerViewHome);
        mSearchBar = mView.findViewById(R.id.searchBar);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);


        setHasOptionsMenu(true);
        mAuthProvider = new AuthProvider();
        mNewPublicProvider = new NewPublicProvider();

        mSearchBar.setOnSearchActionListener(this);
        mSearchBar.inflateMenu(R.menu.main_menu);
        mSearchBar.getMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.itemLogout){
                    logout();
                }

                return true;
            }
        });


        mNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPost();
            }
        });
        return mView;
    }

    private void searchByMenssagePublic(String menssagepublic){
        Query query = mNewPublicProvider.getPostByMessagePublic(menssagepublic);
        FirestoreRecyclerOptions<NewPublic> options =
                new FirestoreRecyclerOptions.Builder<NewPublic>()
                        .setQuery(query, NewPublic.class)
                        .build();
        mPublicChatAdapterSearch = new PublicChatAdapter(options, getContext());
        mPublicChatAdapterSearch.notifyDataSetChanged();
        mRecyclerView.setAdapter(mPublicChatAdapterSearch);
        mPublicChatAdapterSearch.startListening();
    }

    private void getAllMessagePublic(){
        Query query = mNewPublicProvider.getAll();
        FirestoreRecyclerOptions<NewPublic> options =
                new FirestoreRecyclerOptions.Builder<NewPublic>()
                        .setQuery(query, NewPublic.class)
                        .build();
        mPublicChatAdapter = new PublicChatAdapter(options, getContext());
        mPublicChatAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mPublicChatAdapter);
        mPublicChatAdapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        getAllMessagePublic();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPublicChatAdapter.stopListening();
        if (mPublicChatAdapterSearch != null){
            mPublicChatAdapterSearch.stopListening();
        }
    }

    private void goToPost() {
        Intent intent = new Intent(getContext(), NewActivity.class);
        startActivity(intent);
    }



    private void logout() {

        mAuthProvider.logout();
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if (!enabled){
            getAllMessagePublic();
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        searchByMenssagePublic(text.toString().toLowerCase());
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}