package com.cyberlabs.linkshortener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class history extends AppCompatActivity {
    RecyclerView recycler;
    Adapter adp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        recycler=findViewById(R.id.recycler1);
        LinearLayoutManager lm=new LinearLayoutManager(this);
        lm.setReverseLayout(true);
        lm.setStackFromEnd(true);
        recycler.setLayoutManager(lm);
        FirebaseUser u= FirebaseAuth.getInstance().getCurrentUser();
        FirebaseRecyclerOptions<Model> options =
                new FirebaseRecyclerOptions.Builder<Model>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("uid").child(u.getUid()), Model.class)
                        .build();
        adp=new Adapter(options);
        recycler.setAdapter(adp);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adp.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        adp.stopListening();
    }

}