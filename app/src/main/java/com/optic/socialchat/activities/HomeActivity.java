package com.optic.socialchat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.optic.socialchat.R;
import com.optic.socialchat.fragments.ChatPrivateFragment;
import com.optic.socialchat.fragments.ChatPublicFragment;
import com.optic.socialchat.fragments.FiltersFragment;
import com.optic.socialchat.fragments.ProfileFragment;
import com.optic.socialchat.providers.AuthProvider;
import com.optic.socialchat.providers.TokenProvider;
import com.optic.socialchat.providers.UsersProvider;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;
    TokenProvider mTokenProvider;
    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        mTokenProvider = new TokenProvider();
        mAuthProvider = new AuthProvider();
        mUsersProvider = new UsersProvider();

        openFragment(new ChatPublicFragment());
        createToken();

    }

    @Override
    protected void onStart() {
        super.onStart();
        updateOnline(true);
    }

    private void updateOnline(boolean status) {
        mUsersProvider.updateOnline(mAuthProvider.getUid(), status);

    }

    @Override
    protected void onStop() {
        super.onStop();
        updateOnline(false);
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId() == R.id.itemChatPublic){
                        openFragment(new ChatPublicFragment());

                    }else if (item.getItemId() == R.id.itemChatPrivate){
                        openFragment(new ChatPrivateFragment());

                    }else if (item.getItemId() == R.id.itemFilters){
                        openFragment(new FiltersFragment());

                    }else if (item.getItemId() == R.id.itemProfile){
                        openFragment(new ProfileFragment());

                    }
                    return true;
                }
            };

    private void createToken(){
        mTokenProvider.create(mAuthProvider.getUid());
    }


}