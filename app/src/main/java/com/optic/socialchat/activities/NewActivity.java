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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.optic.socialchat.R;
import com.optic.socialchat.models.NewPublic;
import com.optic.socialchat.providers.AuthProvider;
import com.optic.socialchat.providers.ImageProvider;
import com.optic.socialchat.providers.NewPublicProvider;
import com.optic.socialchat.utils.FileUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class NewActivity extends AppCompatActivity {

    ImageView mImageViewPost1;
    ImageView mImageViewPost2;
    File mImageFile;
    File mImageFile2;
    Button mButtonNewPublic;
    CircleImageView mCircleImageBack;
    ImageProvider mImageProvider;
    NewPublicProvider mNewPublicProvider;
    AuthProvider mAuthProvider;
    TextInputEditText mTextMessagePublic;
    ImageView mImageViewColegio;
    ImageView mImageViewPadres;
    ImageView mImageViewAlumnos;
    ImageView mImageViewMaestros;
    TextView mTextViewCategory;

        String mCategory = "";
    String mNewMessage = "";
    AlertDialog mDialog;
    AlertDialog.Builder mBuilderSelector;
    CharSequence options[];
    private final int GALLERY_REQUEST_CODE = 1;
    private final int GALLERY_REQUEST_CODE_2 = 2;
    private final int PHOTO_REQUEST_CODE_3 = 3;
    private final int PHOTO_REQUEST_CODE_4 = 4;

    String mAbsolutePhotoPath;
    String mPhotoPath;
    File mPhotoFile;

    String mAbsolutePhotoPath2;
    String mPhotoPath2;
    File mPhotoFile2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        mImageProvider = new ImageProvider();
        mNewPublicProvider = new NewPublicProvider();
        mAuthProvider = new AuthProvider();

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();

        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Selecciona una opción");
        options = new CharSequence[]{
                "imagen de galería",
                "Tomar foto"
        };

        mImageViewPost1 = findViewById(R.id.imageViewPost1);
        mImageViewPost2 = findViewById(R.id.imageViewPost2);
        mButtonNewPublic = findViewById(R.id.btnNewPublic);
        mTextMessagePublic = findViewById(R.id.textInputMessagePublic);
        mImageViewColegio = findViewById(R.id.imageViewColegio);
        mImageViewPadres = findViewById(R.id.imageViewPadres);
        mImageViewAlumnos = findViewById(R.id.imageViewAlumnos);
        mImageViewMaestros = findViewById(R.id.imageViewMaestros);
        mTextViewCategory = findViewById(R.id.textViewCategory);
        mCircleImageBack = findViewById(R.id.circleImageBack);

        mCircleImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mImageViewColegio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategory = "Colegio";
                mTextViewCategory.setText(mCategory);
            }
        });

        mImageViewPadres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategory = "Padres";
                mTextViewCategory.setText(mCategory);
            }
        });

        mImageViewAlumnos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategory = "Alumnos";
                mTextViewCategory.setText(mCategory);
            }
        });

        mImageViewMaestros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategory = "Maestros";
                mTextViewCategory.setText(mCategory);
            }
        });

        mButtonNewPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPost();
            }
        });
        mImageViewPost1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectOptionImage(1);

            }
        });

        mImageViewPost2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectOptionImage(2);
            }
        });
    }

    private void SelectOptionImage(final int numberImage) {

        mBuilderSelector.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (which == 0){
                    if (numberImage == 1){
                        openGallery(GALLERY_REQUEST_CODE);
                    }else if (numberImage == 2){
                        openGallery(GALLERY_REQUEST_CODE_2);
                    }

                } else if(which == 1){
                    if (numberImage == 1){
                        takePhoto(PHOTO_REQUEST_CODE_3);
                    }else if (numberImage == 2){
                        takePhoto(PHOTO_REQUEST_CODE_4);
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
                Uri photoUri = FileProvider.getUriForFile(NewActivity.this,"com.optic.socialchat", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, PHOTO_REQUEST_CODE_3);
            }
        }

    }

    private File createPhotoFile(int requestCode) throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(
                new  Date() + "_photo",
                ".jpg",
                storageDir
        );
        if (requestCode == PHOTO_REQUEST_CODE_3){
            mPhotoPath = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath = photoFile.getAbsolutePath();
        }else if (requestCode == PHOTO_REQUEST_CODE_4){
            mPhotoPath2 = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath2 = photoFile.getAbsolutePath();
        }

        return photoFile;

    }


    private void clickPost() {

        mNewMessage = mTextMessagePublic.getText().toString();

        if (!mNewMessage.isEmpty() && !mCategory.isEmpty()){
            //both of gallery
            if (mImageFile != null && mImageFile2 != null){
                saveImage(mImageFile, mImageFile2);
            }
            //both of camera
            else if (mPhotoFile != null && mPhotoFile2 != null){
                saveImage(mPhotoFile, mPhotoFile2);
            }
            //1 gallery 2 camera
            else if (mImageFile != null && mPhotoFile2 != null){
                saveImage(mImageFile, mPhotoFile2);
            }
            // 1 camera 2 gallery
            else if (mPhotoFile != null && mImageFile2 != null){
                saveImage(mPhotoFile, mImageFile2);
            } else {
                Toast.makeText(this, "Debes seleccionar una imagen", Toast.LENGTH_SHORT).show();
            }

        }else {
            Toast.makeText(this, "Complete los campos para publicar", Toast.LENGTH_SHORT).show();
        }

    }

    private void saveImage(File imageFile1, final File imageFile2) {

        mDialog.show();

        mImageProvider.save(NewActivity.this,imageFile1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String url = uri.toString();

                            mImageProvider.save(NewActivity.this,imageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage2) {
                                    if (taskImage2.isSuccessful()){
                                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {
                                                String url2 = uri2.toString();
                                                NewPublic newPublic = new NewPublic();
                                                newPublic.setImage1(url);
                                                newPublic.setImage2(url2);
                                                newPublic.setMenssagepublic(mNewMessage);
                                                newPublic.setCategory(mCategory);
                                                newPublic.setIdUser(mAuthProvider.getUid());
                                                newPublic.setTimestamp(new Date().getTime());
                                                mNewPublicProvider.save(newPublic).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> taskSave) {
                                                        mDialog.dismiss();
                                                        if (taskSave.isSuccessful()){
                                                            clearForm();
                                                            Toast.makeText(NewActivity.this, "La publicación se almaceno correctamente", Toast.LENGTH_SHORT).show();
                                                        }else {

                                                            Toast.makeText(NewActivity.this, "No se pudo completar", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    } else {
                                        mDialog.dismiss();
                                        Toast.makeText(NewActivity.this, "La imagen no se pudo guardar", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }
                    });
                    Toast.makeText(NewActivity.this, "La imagen se almaceno correctamente", Toast.LENGTH_LONG).show();
                } else {
                    mDialog.dismiss();
                    Toast.makeText(NewActivity.this, "Hubo error al almacenar la imagen", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void clearForm() {

        mTextMessagePublic.setText("");
        mTextViewCategory.setText("");
        mImageViewPost1.setImageResource(R.drawable.camara);
        mImageViewPost2.setImageResource(R.drawable.camara);
        mNewMessage = "";
        mCategory = "";
        mImageFile = null;
        mImageFile2 = null;

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
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            try {
                mPhotoFile = null;
                mImageFile = FileUtil.from(this, data.getData());
                mImageViewPost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            }catch (Exception e){
                Log.d("ERROR","Se produjo un error" + e.getMessage());
                Toast.makeText(this, "Se produjo un error" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == GALLERY_REQUEST_CODE_2 && resultCode == RESULT_OK){
            try {
                mPhotoFile2 = null;
                mImageFile2 = FileUtil.from(this, data.getData());
                mImageViewPost2.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));
            }catch (Exception e){
                Log.d("ERROR","Se produjo un error" + e.getMessage());
                Toast.makeText(this, "Se produjo un error" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        // SELECION DE FOTO
        if (requestCode == PHOTO_REQUEST_CODE_3 && resultCode == RESULT_OK){
            mImageFile = null;
            mPhotoFile = new File(mAbsolutePhotoPath);
            Picasso.with(NewActivity.this).load(mPhotoPath).into(mImageViewPost1);

        }

        if (requestCode == PHOTO_REQUEST_CODE_4 && resultCode == RESULT_OK){
            mImageFile2 = null;
            mPhotoFile2 = new File(mAbsolutePhotoPath2);
            Picasso.with(NewActivity.this).load(mPhotoPath2).into(mImageViewPost2);

        }
    }
}