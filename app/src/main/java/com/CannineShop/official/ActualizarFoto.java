package com.CannineShop.official;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.CannineShop.official.Internet.Internet;
import com.canhub.cropper.CropImage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActualizarFoto extends AppCompatActivity {

    String id;
    CircleImageView profileImageView;
    Button closeButton, saveButton, deleteButton;
    TextView profileChangeBtn;

    DatabaseReference databaseReference;
    FirebaseAuth mAuth;

    Uri imagenUri;
    String myUri = "";
    StorageTask uploadTask;
    StorageReference storageProfilePicsRef;
    //Atributos De Transicion
    public static int zoomOut = R.anim.zoom_out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_foto);
        setTitle("Actualizar Foto");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        storageProfilePicsRef = FirebaseStorage.getInstance().getReference().child("Profile Pic");
        //Obtengo el ID Ingresado
        id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        //Asignacion de Id
        profileImageView = findViewById(R.id.profile_image);
        closeButton = findViewById(R.id.btnClose);
        saveButton = findViewById(R.id.btnSave);
        deleteButton = findViewById(R.id.btnDelete);
        profileChangeBtn = findViewById(R.id.change_profile_btn);

        //Proceso Al Dar Click
        profileChangeBtn.setOnClickListener(v -> CropImage.activity()
                .setAspectRatio(1, 1)
                .start(ActualizarFoto.this));

        closeButton.setOnClickListener(view -> {
            startActivity(new Intent(ActualizarFoto.this, ActualizarDatos.class));
            finish();
        });

        saveButton.setOnClickListener(view -> {
            if (Internet.isOnline(ActualizarFoto.this)) {
                uploadProfileImage();
            } else {
                Toast.makeText(ActualizarFoto.this, "¡Sin Acceso A Internet, Verifique Su Conexión.!", Toast.LENGTH_SHORT).show();
            }
        });

        deleteButton.setOnClickListener(v -> {
            if (Internet.isOnline(ActualizarFoto.this)) {
                final StorageReference fileRef = storageProfilePicsRef.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + ".jpg");
                databaseReference.child(id).child("image").removeValue()
                        .addOnSuccessListener(unused -> {
                            fileRef.delete();
                            imagenUri = null;
                            profileImageView.setImageResource(R.drawable.sinfoto);
                            Toast.makeText(ActualizarFoto.this, "¡Foto Eliminada!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(ActualizarFoto.this, "Error al eliminar foto: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(ActualizarFoto.this, "¡Sin Acceso A Internet, Verifique Su Conexión.!", Toast.LENGTH_SHORT).show();
            }
        });
        getUserinfo();
    }

    private void getUserinfo() {
        databaseReference.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if (datasnapshot.exists() && datasnapshot.getChildrenCount() > 0) {
                    if (datasnapshot.hasChild("image")) {
                        Object imgObj = datasnapshot.child("image").getValue();
                        if (imgObj != null) {
                            String image = imgObj.toString();
                            if (!image.isEmpty()) {
                                Picasso.get().load(image).placeholder(R.drawable.sinfoto).into(profileImageView);
                            } else {
                                profileImageView.setImageResource(R.drawable.sinfoto);
                            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK && result != null) {
                imagenUri = result.getUriContent();
                profileImageView.setImageURI(imagenUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result != null ? result.getError() : null;
                Toast.makeText(ActualizarFoto.this, "Error al recortar la imagen: " + (error != null ? error.getMessage() : "Desconocido"), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadProfileImage() {
        if (imagenUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Actualizar foto de perfil");
            progressDialog.setMessage("Por favor espere mientras se sube la imagen...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            final StorageReference fileRef = storageProfilePicsRef.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + ".jpg");
            uploadTask = fileRef.putFile(imagenUri);

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                return fileRef.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                progressDialog.dismiss();
                if (task.isSuccessful() && task.getResult() != null) {
                    Uri downloadUrl = task.getResult();
                    myUri = downloadUrl.toString();

                    HashMap<String, Object> userMap = new HashMap<>();
                    userMap.put("image", myUri);

                    databaseReference.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap)
                            .addOnSuccessListener(unused -> Toast.makeText(ActualizarFoto.this, "¡Foto de perfil actualizada exitosamente!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(ActualizarFoto.this, "Error al guardar en base de datos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                } else {
                    String errorMsg = task.getException() != null ? task.getException().getMessage() : "Error desconocido";
                    Toast.makeText(ActualizarFoto.this, "Error al subir la imagen: " + errorMsg, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(ActualizarFoto.this, "Por favor seleccione una imagen primero", Toast.LENGTH_SHORT).show();
        }
    }

    //🡣🡣🡣Proceso Al Dar Click a Retroceder🡣🡣🡣
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ActualizarDatos.class));
        overridePendingTransition(0, zoomOut);
        finish();
    }

    //🡣🡣🡣Flecha Atras🡣🡣🡣
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, ActualizarDatos.class));
        overridePendingTransition(0, zoomOut);
        finish();
        return super.onOptionsItemSelected(item);
    }
}