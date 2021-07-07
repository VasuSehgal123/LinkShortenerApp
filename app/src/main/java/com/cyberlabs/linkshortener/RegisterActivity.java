package com.cyberlabs.linkshortener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {
    EditText fname;
    EditText lname;
    EditText mail;
    EditText pass;
    EditText rpass;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        fname=findViewById(R.id.fname1);
        lname=findViewById(R.id.lname1);
        mail=findViewById(R.id.email1);
        pass=findViewById(R.id.pass1);
        rpass=findViewById(R.id.rpass1);
        progressBar=findViewById(R.id.progress);

    }
    public void signup(View view) {
        String firstName,lastName,address,email,password,rpassword;
        firstName=fname.getText().toString().trim();
        lastName=lname.getText().toString().trim();
        email=mail.getText().toString().trim();
        password=pass.getText().toString();
        rpassword=rpass.getText().toString();
        boolean allright=true;

        if(!password.equals(rpassword)){
            rpass.setError("Password does not match");
            rpass.requestFocus();
            allright=false;
        }
        if(password.isEmpty()||password.length()<6){
            pass.setError("Password should be atleast 6 characters long");
            pass.requestFocus();
            allright=false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()||email.isEmpty()){
            mail.setError("Please Enter a valid email");
            mail.requestFocus();
            allright=false;
        }
        if(firstName.isEmpty()){
            fname.setError("Please Enter First Name");
            fname.requestFocus();
            allright=false;
        }


        if(allright){
            progressBar.setVisibility(View.VISIBLE);
            mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Registered Succesfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                                finish();
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(firstName+" "+lastName)
                                            .setPhotoUri(null)
                                            .build();

                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("user profile", "User profile updated.");
                                                    }
                                                }
                                            });
                                    progressBar.setVisibility(View.INVISIBLE);
                            }
                            else if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(RegisterActivity.this, "User already Registered", Toast.LENGTH_SHORT).show();
                            }
                            else if(!task.isSuccessful()){
                                Log.w("MSG", "createUserWithEmail:failure", task.getException());
                                progressBar.setVisibility(View.INVISIBLE);

                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }


    }


}