package com.cyberlabs.linkshortener;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.Context.CLIPBOARD_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class Adapter extends FirebaseRecyclerAdapter<Model,Adapter.Holder> {

    public Adapter(@NonNull FirebaseRecyclerOptions<Model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull Holder holder, int position, @NonNull Model model) {
        holder.originalURL.setText(model.getOriginalurl());
        holder.myshortURL.setText(model.getShorturl());
        holder.datetime.setText(model.getTime());

    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
        return new Holder(view);
    }

    class Holder extends RecyclerView.ViewHolder{
            EditText originalURL,myshortURL;
            TextView datetime;
            ImageView cpy;

            public Holder(@NonNull View itemView) {
                super(itemView);
                originalURL=itemView.findViewById(R.id.org1);
                myshortURL=itemView.findViewById(R.id.short1);
                datetime=itemView.findViewById(R.id.datetime1);
                cpy=itemView.findViewById(R.id.hcopy);
                itemView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String t=datetime.getText().toString();
                        FirebaseUser usr= FirebaseAuth.getInstance().getCurrentUser();
                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference Ref = db.getReference("uid");
                        DatabaseReference uidRef = Ref.child(usr.getUid());
                        DatabaseReference timeRef=uidRef.child(t);
                        timeRef.removeValue();

                    }
                });
                cpy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Short URL",myshortURL.getText().toString());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(v.getContext(), "link copied!", Toast.LENGTH_SHORT).show();
                    }
                });

            }

    }

}