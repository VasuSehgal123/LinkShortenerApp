package com.cyberlabs.linkshortener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    EditText mail;
    EditText pass;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mail=findViewById(R.id.email1);
        pass=findViewById(R.id.pass1);
        progressBar=findViewById(R.id.progress);
    }

    public void signupform(View view) {
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

    public void signin(View view) {
        String email,password;
        email=mail.getText().toString();
        password=pass.getText().toString();

        if(password.isEmpty()){
            pass.setError("Please Enter Password");
            pass.requestFocus();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()||email.isEmpty()){
            mail.setError("Please Enter a valid email");
            mail.requestFocus();
        }

        else {
            progressBar.setVisibility(View.VISIBLE);
            mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("MSG", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                progressBar.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(MainActivity.this,linkmain.class));
                                finish();
                            }
                            else if((task.getException() instanceof FirebaseAuthInvalidCredentialsException)||task.getException() instanceof FirebaseAuthInvalidUserException){
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(MainActivity.this, "Invalid Email or Password",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Log.w("MSG", "signInWithEmail:failure", task.getException());
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }



                        }
                    });
        }


    }
}