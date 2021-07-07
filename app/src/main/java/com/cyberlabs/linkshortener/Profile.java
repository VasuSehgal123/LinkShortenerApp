package com.cyberlabs.linkshortener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    EditText name;
    EditText email;
    ProgressBar pb;
    CircleImageView img;
    String upImgUrl;
    String tempPath;
    Uri filepath;
    Uri myfilepath;
    Bitmap bitmap;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        name=(EditText) findViewById(R.id.name11);
        email=(EditText) findViewById(R.id.email11);
        pb=findViewById(R.id.pb);
        img=findViewById(R.id.proimg);
        setUserInfo();

    }
    void setUserInfo(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            name.setText(user.getDisplayName());
            email.setText(user.getEmail());
            if(user.getPhotoUrl()!=null)  Picasso.get().load(user.getPhotoUrl().toString()).into(img);
        } else {
            startActivity(new Intent(Profile.this,MainActivity.class));
        }
    }

    public void chpass(View view) {
        startActivity(new Intent(Profile.this,changepass.class));
    }

    public void editName(View view) {
        name.setEnabled(true);
        name.requestFocus();
        InputMethodManager imm = (InputMethodManager) Profile.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        name.setSelection(name.getText().length());

    }

    public void editEmail(View view) {
        email.setEnabled(true);
        email.requestFocus();
        InputMethodManager imm = (InputMethodManager) Profile.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        email.setSelection(email.getText().length());
    }

    public void updateProfile(View view) {
        String mynewname=name.getText().toString().trim();
        String mynewemail=email.getText().toString().trim();

        if(mynewname.isEmpty()){
            name.setError("Please enter your Name");
            name.requestFocus();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(mynewemail).matches()||mynewemail.isEmpty()){
            email.setError("Please Enter a valid email");
            email.requestFocus();
        }
        else{

                Bitmap bm=((BitmapDrawable)img.getDrawable()).getBitmap();
                myfilepath=getImageUri(Profile.this,bm);

                final ProgressDialog dialog=new ProgressDialog(this);
                dialog.setTitle("Uploading Image");
                dialog.show();
                pb.setVisibility(View.VISIBLE);

                FirebaseStorage storage=FirebaseStorage.getInstance();
                StorageReference uploader = storage.getReference().child(user.getUid());
                uploader.putFile(myfilepath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                dialog.dismiss();
                                uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        upImgUrl=uri.toString();
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(mynewname)
                                                .setPhotoUri(Uri.parse(upImgUrl))
                                                .build();

                                        user.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                            user.updateEmail(mynewemail)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                pb.setVisibility(View.INVISIBLE);
                                                                                name.setEnabled(false);
                                                                                email.setEnabled(false);

                                                                                getContentResolver().delete(myfilepath,null,null);

                                                                                Toast.makeText(Profile.this, "User Profile Updated", Toast.LENGTH_SHORT).show();
                                                                                Log.d("user email", "User email address updated.");
                                                                            }
                                                                            else{
                                                                                pb.setVisibility(View.INVISIBLE);
                                                                                Toast.makeText(Profile.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });

                                                            Log.d("user profile", "User profile updated.");
                                                        }
                                                        else{
                                                            pb.setVisibility(View.INVISIBLE);
                                                            Toast.makeText(Profile.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                float percent=(100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                                dialog.setMessage("Uploaded: "+(int)percent+"%");
                            }
                        });




        }

    }



    public void selectImg(View view) {
        Dexter.withActivity(Profile.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent,"Select Your Image"),1);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==1 && resultCode==RESULT_OK){
            filepath=data.getData();
            try{
                InputStream inputStream=getContentResolver().openInputStream(filepath);
                bitmap= BitmapFactory.decodeStream(inputStream);
                img.setImageBitmap(bitmap);
            }
            catch (Exception exp){}
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        tempPath=path;
        return Uri.parse(path);
    }
}