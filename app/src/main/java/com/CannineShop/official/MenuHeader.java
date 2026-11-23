package com.CannineShop.official;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MenuHeader extends AppCompatActivity {

    CircleImageView profileImageView;
    TextView nameTextView;
    TextView correoTextView;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_header);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Usuarios");

        profileImageView = findViewById(R.id.portada);
        nameTextView = findViewById(R.id.name);
        correoTextView = findViewById(R.id.correo);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (user.getEmail() != null) {
                correoTextView.setText(user.getEmail());
            }
            String id = user.getUid();
            databaseReference.child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.hasChild("nombre")) {
                            Object nameObj = dataSnapshot.child("nombre").getValue();
                            if (nameObj != null) {
                                nameTextView.setText(nameObj.toString());
                            }
                        }
                        if (dataSnapshot.hasChild("image")) {
                            Object imgObj = dataSnapshot.child("image").getValue();
                            if (imgObj != null && !imgObj.toString().isEmpty()) {
                                Picasso.get().load(imgObj.toString()).placeholder(R.drawable.sinfoto).into(profileImageView);
                            } else {
                                profileImageView.setImageResource(R.drawable.sinfoto);
                            }
                        } else {
                            profileImageView.setImageResource(R.drawable.sinfoto);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}