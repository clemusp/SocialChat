package com.optic.socialchat.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.UploadTask;
import com.optic.socialchat.R;
import com.optic.socialchat.models.NewPublic;
import com.optic.socialchat.models.Users;
import com.optic.socialchat.providers.AuthProvider;
import com.optic.socialchat.providers.ImageProvider;
import com.optic.socialchat.providers.UsersProvider;
import com.optic.socialchat.utils.FileUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class EditProfileActivity extends AppCompatActivity {

    CircleImageView mCircleImageViewBack;
    CircleImageView mCircleImmageViewProfile;
    ImageView mImageViewCover;
    TextInputEditText mTextInputUsername;
    TextInputEditText mTextInputPhone;
    Button mButtonEditProfile;

    File mImageFile;
    File mImageFile2;
    AlertDialog.Builder mBuilderSelector;
    CharSequence options[];
    private final int GALLERY_REQUEST_CODE_PROFILE = 1;
    private final int GALLERY_REQUEST_CODE_COVER = 2;
    private final int PHOTO_REQUEST_CODE_PROFILE = 3;
    private final int PHOTO_REQUEST_CODE_COVER = 4;

    String mAbsolutePhotoPath;
    String mPhotoPath;
    File mPhotoFile;

    String mAbsolutePhotoPath2;
    String mPhotoPath2;
    File mPhotoFile2;

    String musername = "";
    String mphone = "";
    String mImageProfile = "";
    String mImageCover = "";

    AlertDialog mDialog;

    ImageProvider mImageProvider;
    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mCircleImageViewBack = findViewById(R.id.circleImageBack);
        mCircleImmageViewProfile = findViewById(R.id.circleImageProfile);
        mImageViewCover = findViewById(R.id.imageViewCover);
        mTextInputPhone = findViewById(R.id.textInputPhone);
        mTextInputUsername = findViewById(R.id.textInputUsername);
        mButtonEditProfile = findViewById(R.id.btnEditProfile);
        mImageProvider = new ImageProvider();
        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();


        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Selecciona una opción");
        options = new CharSequence[]{
                "imagen de galería",
                "Tomar foto"
        };

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();

        mButtonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickEditProfile();
            }
        });

        mCircleImmageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectOptionImage(1);
            }
        });

        mImageViewCover. setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectOptionImage(2);
            }
        });


        mCircleImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getUser();

    }

    private void getUser(){
        mUsersProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("username")){
                        musername = documentSnapshot.getString("username");
                        mTextInputUsername.setText(musername);
                    }
                    if (documentSnapshot.contains("phone")){
                        mphone = documentSnapshot.getString("phone");
                        mTextInputPhone.setText(mphone);
                    }
                    if (documentSnapshot.contains("image_profile")){
                        mImageProfile = documentSnapshot.getString("image_profile");
                        if (mImageProfile != null){
                            if (!mImageProfile.isEmpty()){
                                Picasso.with(EditProfileActivity.this).load(mImageProfile).into(mCircleImmageViewProfile);
                            }
                        }
                    }
                    if (documentSnapshot.contains("image_cover")){
                        mImageCover = documentSnapshot.getString("image_cover");
                        if (mImageCover != null){
                            if ((!mImageCover.isEmpty())){
                                Picasso.with(EditProfileActivity.this).load(mImageCover).into(mImageViewCover);
                            }
                        }
                    }
                }
            }
        });
    }

    private void clickEditProfile() {

        musername = mTextInputUsername.getText().toString();
        mphone = mTextInputPhone.getText().toString();
        if (!musername.isEmpty() && !mphone.isEmpty()){
            //both of gallery
            if (mImageFile != null && mImageFile2 != null){
                saveImageCoverAndProfile(mImageFile, mImageFile2);
            }
            //both of camera
            else if (mPhotoFile != null && mPhotoFile2 != null){
                saveImageCoverAndProfile(mPhotoFile, mPhotoFile2);
            }
            //1 gallery 2 camera
            else if (mImageFile != null && mPhotoFile2 != null){
                saveImageCoverAndProfile(mImageFile, mPhotoFile2);
            }
            // 1 camera 2 gallery
            else if (mPhotoFile != null && mImageFile2 != null){
                saveImageCoverAndProfile(mPhotoFile, mImageFile2);
            }
            else if (mPhotoFile !=null){
                saveImage(mPhotoFile, true);
            }
            else if (mPhotoFile2 != null){
                saveImage(mPhotoFile2, false);
            }
            else if (mImageFile != null){
                saveImage(mImageFile, true);
            }
            else if (mImageFile2 != null){
                saveImage(mImageFile2, false);
            }
            else {
                Users users = new Users();
                users.setUsername(musername);
                users.setPhone(mphone);
                users.setId(mAuthProvider.getUid());
                updateInfo(users);
            }

        }else {
            Toast.makeText(this, "Ingrese el nombre de usario y el telefono", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageCoverAndProfile(File imageFile1, final File imageFile2) {

        mDialog.show();

        mImageProvider.save(EditProfileActivity.this,imageFile1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String urlProfile = uri.toString();

                            mImageProvider.save(EditProfileActivity.this,imageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage2) {
                                    if (taskImage2.isSuccessful()){
                                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {
                                                String urlCover = uri2.toString();
                                                Users users = new Users();
                                                users.setUsername(musername);
                                                users.setPhone(mphone);
                                                users.setImageProfile(urlProfile);
                                                users.setImageCover(urlCover);
                                                users.setId(mAuthProvider.getUid());
                                                updateInfo(users);
                                            }
                                        });
                                    } else {
                                        mDialog.dismiss();
                                        Toast.makeText(EditProfileActivity.this, "La imagen no se pudo guardar", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }
                    });
                    Toast.makeText(EditProfileActivity.this, "La imagen se almaceno correctamente", Toast.LENGTH_LONG).show();
                } else {
                    mDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Hubo error al almacenar la imagen", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void saveImage(File image, final boolean isProfileImage){
        mDialog.show();

        mImageProvider.save(EditProfileActivity.this,image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String url = uri.toString();
                            Users users = new Users();
                            users.setUsername(musername);
                            users.setPhone(mphone);
                            if (isProfileImage){
                                users.setImageProfile(url);
                                users.setImageCover(mImageCover);
                            }else {
                                users.setImageCover(url);
                                users.setImageProfile(mImageProfile);
                            }

                            users.setId(mAuthProvider.getUid());
                            updateInfo(users);

                        }
                    });
                    Toast.makeText(EditProfileActivity.this, "La imagen se almaceno correctamente", Toast.LENGTH_LONG).show();
                } else {
                    mDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Hubo error al almacenar la imagen", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateInfo(Users users){
        if (mDialog.isShowing()){
            mDialog.show();
        }
        mUsersProvider.update(users).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDialog.dismiss();
                if (task.isSuccessful()){
                    Toast.makeText(EditProfileActivity.this, "La informacion se actualizo correctamente", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(EditProfileActivity.this, "La informacion no se pudo actualizar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SelectOptionImage(final int numberImage) {

        mBuilderSelector.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    if (numberImage == 1){
                        openGallery(GALLERY_REQUEST_CODE_PROFILE);
                    }else if (numberImage == 2){
                        openGallery(GALLERY_REQUEST_CODE_COVER);
                    }

                } else if(which == 1){
                    if (numberImage == 1){
                        takePhoto(PHOTO_REQUEST_CODE_PROFILE);
                    }else if (numberImage == 2){
                        takePhoto(PHOTO_REQUEST_CODE_COVER);
                    }
                }
            }
        });

        mBuilderSelector.show();

    }

    private void takePhoto(int requestCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            try {
                photoFile = createPhotoFile(requestCode);
            }catch (Exception e){
                Toast.makeText(this, "Hubo un error con el archivo", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null){
                Uri photoUri = FileProvider.getUriForFile(EditProfileActivity.this,"com.optic.socialchat", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, PHOTO_REQUEST_CODE_PROFILE);
            }
        }

    }

    private File createPhotoFile(int requestCode) throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(
                new Date() + "_photo",
                ".jpg",
                storageDir
        );
        if (requestCode == PHOTO_REQUEST_CODE_PROFILE){
            mPhotoPath = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath = photoFile.getAbsolutePath();
        }else if (requestCode == PHOTO_REQUEST_CODE_COVER){
            mPhotoPath2 = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath2 = photoFile.getAbsolutePath();
        }

        return photoFile;

    }

    private void openGallery(int requestCode) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //SELECION DE GALERIA
        if (requestCode == GALLERY_REQUEST_CODE_PROFILE && resultCode == RESULT_OK){
            try {
                mPhotoFile = null;
                mImageFile = FileUtil.from(this, data.getData());
                mCircleImmageViewProfile.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            }catch (Exception e){
                Log.d("ERROR","Se produjo un error" + e.getMessage());
                Toast.makeText(this, "Se produjo un error" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == GALLERY_REQUEST_CODE_COVER && resultCode == RESULT_OK){
            try {
                mPhotoFile2 = null;
                mImageFile2 = FileUtil.from(this, data.getData());
                mImageViewCover.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));
            }catch (Exception e){
                Log.d("ERROR","Se produjo un error" + e.getMessage());
                Toast.makeText(this, "Se produjo un error" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        // SELECION DE FOTO
        if (requestCode == PHOTO_REQUEST_CODE_PROFILE && resultCode == RESULT_OK){
            mImageFile = null;
            mPhotoFile = new File(mAbsolutePhotoPath);
            Picasso.with(EditProfileActivity.this).load(mPhotoPath).into(mCircleImmageViewProfile);

        }

        if (requestCode == PHOTO_REQUEST_CODE_COVER && resultCode == RESULT_OK){
            mImageFile2 = null;
            mPhotoFile2 = new File(mAbsolutePhotoPath2);
            Picasso.with(EditProfileActivity.this).load(mPhotoPath2).into(mImageViewCover);

        }
    }
}