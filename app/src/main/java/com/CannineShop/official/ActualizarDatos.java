package com.CannineShop.official;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.CannineShop.official.Internet.Internet;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActualizarDatos extends AppCompatActivity {
    //Atributos
    TextView txtname, nameTextField, telefonoTextField, emailTextView;
    MaterialButton btnActualizarDatos, ActualizarPerfil;
    CircleImageView profileImageView;
    //Atributos De Transicion
    public static int zoomOut = R.anim.zoom_out;
    public static int translateRight = R.anim.translate_right_side;
    //Atributos FireBase.
    DatabaseReference databaseReference;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
    FirebaseUser user;

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_datos);
        setTitle("Actualizar Datos");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        profileImageView = findViewById(R.id.dp);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        txtname = findViewById(R.id.txtname);
        getUserinfo();

        //Instancio los TextField del ID
        nameTextField = findViewById(R.id.nameTextField);
        telefonoTextField = findViewById(R.id.TelefonoTextField);
        emailTextView = findViewById(R.id.emailTextView);
        //Instancio los Botones del ID
        btnActualizarDatos = findViewById(R.id.ActualizarDatos);
        ActualizarPerfil = findViewById(R.id.ActualizarPerfil);
        //Objeto FireBase User
        user = FirebaseAuth.getInstance().getCurrentUser();
        //Obtengo los datos de ID.
        databaseReference.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {//Objeto datos DATASNAPSHOT
                // contendrá valores de los datos exactos a los que se hace referencia.
                //Si el User Existe.
                if (dataSnapshot.exists()) {
                    //Guardo variables
                    String name = Objects.requireNonNull(dataSnapshot.child("nombre").getValue()).toString();
                    String telefono = Objects.requireNonNull(dataSnapshot.child("telefono").getValue()).toString();

                    //Actualizo las Variables
                    nameTextField.setText(name);
                    telefonoTextField.setText(telefono);
                }
                //Objeto FireBase User
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                //Distinto a nulo
                if (user != null) {
                    emailTextView.setText(user.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        //Instrucciones Al Dar Click
        btnActualizarDatos.setOnClickListener(v -> {
            if (Internet.isOnline(this)) {
                validate();
            } else {
                Toast.makeText(ActualizarDatos.this, "¡Sin Acceso A Internet, Verifique Su Conexión.!", Toast.LENGTH_SHORT).show();
            }
        });
        ActualizarPerfil.setOnClickListener(view -> {
            startActivity(new Intent(ActualizarDatos.this, ActualizarFoto.class));
            overridePendingTransition(0, translateRight);
            finish();
        });
    }

    private void getUserinfo() {
        databaseReference.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if (datasnapshot.exists() && datasnapshot.getChildrenCount() > 0) {
                    String name = Objects.requireNonNull(datasnapshot.child("nombre").getValue()).toString();
                    txtname.setText(name);

                    if (datasnapshot.hasChild("image")) {
                        String image = Objects.requireNonNull(datasnapshot.child("image").getValue()).toString();
                        Picasso.get().load(image).into(profileImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //🡣🡣🡣Validacion De Los Campos🡣🡣🡣
    public void validate() {
        //Recoger Datos Nombre,Apellido y Telefono.
        String name = Objects.requireNonNull(nameTextField.getText()).toString().trim().toLowerCase();
        String telefono = Objects.requireNonNull(telefonoTextField.getText()).toString().trim();
        //Campo Nombre
//Campo Nombre
        if (name.isEmpty()) {
            nameTextField.setError("¡Campo Vacio!");
            return;
        } else if (name.length() < 3) {
            nameTextField.setError("¿Cual Es Tu Nombre");
            return;
        } else {
            nameTextField.setError(null);
        }
        //Campo Telefono
        //Campo Telefono
        if (telefono.isEmpty()) {
            telefonoTextField.setError("¡Campo Vacio!");
            return;
        } else if (telefono.length() < 10) {
            telefonoTextField.setError("Se Necesitan Mas De 10 numeros");
            return;
        }else {
            telefonoTextField.setError(null);
        }
        update_Data(name, telefono);
    }

    //🡣🡣🡣Cambio De Datos En FireBase🡣🡣🡣
    public void update_Data(String name, String telefono) {
        //Actualiza Los Datos
        Map<String, Object> Data = new HashMap<>();
        //HTTP
        Data.put("nombre", name);
        Data.put("telefono", telefono);
        databaseReference.child(id).updateChildren(Data).
                addOnSuccessListener(unused -> Toast.makeText(this, "Datos Actualizados", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Hubo Un Error Al Actualizar Los Datos", Toast.LENGTH_SHORT).show());
    }

    //🡣🡣🡣Proceso Al Dar Click a Retroceder🡣🡣🡣
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, Home.class));
        overridePendingTransition(0, zoomOut);
        finish();
    }

    //🡣🡣🡣Flecha Atras🡣🡣🡣
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(ActualizarDatos.this, Home.class));
        overridePendingTransition(0, zoomOut);
        finish();
        return super.onOptionsItemSelected(item);
    }
}