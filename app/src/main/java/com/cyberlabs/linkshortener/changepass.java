package com.cyberlabs.linkshortener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class changepass extends AppCompatActivity {
    EditText newPass;
    EditText rePass;
    ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepass);
        newPass=findViewById(R.id.newpass1);
        rePass=findViewById(R.id.repass1);
        pb=findViewById(R.id.pb1);
    }

    public void changePassword(View view) {
        String newpassword=newPass.getText().toString();
        String repassword=rePass.getText().toString();
        if(!newpassword.equals(repassword)){
            rePass.setError("Password does not match");
            rePass.requestFocus();
        }
        else if(newpassword.isEmpty()||newpassword.length()<6){
            newPass.setError("Password should be atleast 6 characters long");
            newPass.requestFocus();
        }
        else{
            pb.setVisibility(View.VISIBLE);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            user.updatePassword(newpassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                pb.setVisibility(View.INVISIBLE);
                                Toast.makeText(changepass.this, "Password changed successfully", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(changepass.this,Profile.class));
                                Log.d("Password Change", "User password updated.");
                            }
                            else{
                                Toast.makeText(changepass.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                                pb.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
        }
    }
}