package com.optic.socialchat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.optic.socialchat.R;
import com.optic.socialchat.adapters.PublicChatAdapter;
import com.optic.socialchat.models.NewPublic;
import com.optic.socialchat.providers.AuthProvider;
import com.optic.socialchat.providers.NewPublicProvider;

public class FiltersActivity extends AppCompatActivity {

    AuthProvider mAuthProvider;
    RecyclerView mRecyclerView;
    NewPublicProvider mNewPublicProvider;
    PublicChatAdapter mPublicChatAdapter;

    TextView mTextViewNumberFilter;
    Toolbar mToolbar;

    String mExtraCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        mRecyclerView = findViewById(R.id.recyclerViewFilter);
        mToolbar = findViewById(R.id.toolbar);
        mTextViewNumberFilter = findViewById(R.id.textViewNumberFilters);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Filtros");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView.setLayoutManager(new GridLayoutManager(FiltersActivity.this,2));

        mExtraCategory=getIntent().getStringExtra("category");

        mAuthProvider = new AuthProvider();
        mNewPublicProvider = new NewPublicProvider();

    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mNewPublicProvider.getPostByCategoryAndTimestamp(mExtraCategory);
        FirestoreRecyclerOptions<NewPublic> options = new FirestoreRecyclerOptions.Builder<NewPublic>()
                .setQuery(query, NewPublic.class)
                .build();

        mPublicChatAdapter = new PublicChatAdapter(options, FiltersActivity.this, mTextViewNumberFilter);
        mRecyclerView.setAdapter(mPublicChatAdapter);
        mPublicChatAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPublicChatAdapter.stopListening();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }
}