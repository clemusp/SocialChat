package com.optic.socialchat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.optic.socialchat.R;
import com.optic.socialchat.models.Users;
import com.optic.socialchat.providers.AuthProvider;
import com.optic.socialchat.providers.UsersProvider;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class CompleteProfileActivity extends AppCompatActivity {


    TextInputEditText mTextInputUsername;
    TextInputEditText mTextInputPhone;
    Button mButtonRegister;
    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;
    AlertDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);


        mTextInputUsername = findViewById(R.id.textInputUsername);
        mTextInputPhone = findViewById(R.id.textInputPhone);
        mButtonRegister = findViewById(R.id.btnRegister);
        mAuthProvider = new AuthProvider();
        mUsersProvider = new UsersProvider();
        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });


    }

    private void register(){
        String username = mTextInputUsername.getText().toString();
        String phone = mTextInputPhone.getText().toString();
        if(!username.isEmpty() ){
            updateUser(username, phone);
        }else{
            Toast.makeText(this, "Para continuar completa todos los campos", Toast.LENGTH_LONG).show();
        }

    }

    private void updateUser(final String username, final String phone){
        String id = mAuthProvider.getUid();
        Users users = new Users();
        users.setUsername(username);
        users.setPhone(phone);
        users.setId(id);
        users.setTimestamp(new Date().getTime());

        mDialog.show();
        mUsersProvider.update(users).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDialog.dismiss();
                if (task.isSuccessful()){
                    Intent intent = new Intent(CompleteProfileActivity.this, HomeActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(CompleteProfileActivity.this, "El usaurio no se pudo registrar en la base de datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}