package com.CannineShop.official;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.CannineShop.official.Destinos.Destinos;
import com.CannineShop.official.Internet.Internet;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.regex.Pattern;

public class SignUpFragment extends Fragment {

    // FireBase
    FirebaseDatabase database;
    private DatabaseReference myRef;
    FirebaseAuth mAuth;
    //Variables
    TextInputEditText nameTextField, telefonoTextField, emailEditText, passwordEditText, confirmPasswordEditText;
    Button singin;
    // Transicion
    public static int translateUp = R.anim.slide_out_up;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_sign_up, container, false);

        //Instancio el Objeto de Firebase
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Usuarios");
        mAuth = FirebaseAuth.getInstance();
        //Finish Firebase
        nameTextField = root.findViewById(R.id.nameTextField);
        emailEditText = root.findViewById(R.id.emailEditText_SignUp);
        passwordEditText = root.findViewById(R.id.passwordEditText_SignUp);
        confirmPasswordEditText = root.findViewById(R.id.confirmPasswordEditText);
        telefonoTextField = root.findViewById(R.id.telefonoTextField);
        singin = root.findViewById(R.id.btnSignup);
        singin.setOnClickListener(v -> {
            if (Internet.isOnline(getContext())) {
                validate();
            } else {
                Toast.makeText(getContext(), "¡Sin Acceso A Internet, Verifique Su Conexión.!", Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }

    public void validate() {
        String nombre = Objects.requireNonNull(Objects.requireNonNull(nameTextField.getText()).toString().trim().toLowerCase());
        String email = Objects.requireNonNull(emailEditText.getText()).toString().trim().toLowerCase();
        String password = Objects.requireNonNull(passwordEditText.getText()).toString().trim();
        String confirmPassword = Objects.requireNonNull(confirmPasswordEditText.getText()).toString().trim();
        String telefono = Objects.requireNonNull(telefonoTextField.getText()).toString().trim();
        //Campo Nombre
        if (nombre.isEmpty()) {
            nameTextField.setError("¡Campo Vacio!");
            Toast.makeText(getContext(), "Faltan Mas Campos", Toast.LENGTH_SHORT).show();
            return;
        } else if (nombre.length() < 3) {
            nameTextField.setError("¿Cual Es Tu Nombre");
            Toast.makeText(getContext(), "Verifica El Campo Nombre", Toast.LENGTH_SHORT).show();
            return;
        } else {
            nameTextField.setError(null);
        }
        //Campo Email
        if (email.isEmpty()) {
            emailEditText.setError("¡Campo Vacio!");
            Toast.makeText(getContext(), "Faltan Mas Campos Porfavor Verificar", Toast.LENGTH_SHORT).show();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("¡Correo Invalido!");
            return;
        } else {
            emailEditText.setError(null);
        }
        //Campo Password
        if (password.isEmpty()) {
            passwordEditText.setError("¡Campo Vacio!");
            Toast.makeText(getContext(), "Faltan Mas Campos Porfavor Verificar", Toast.LENGTH_SHORT).show();
            return;
        } else if (password.length() < 8) {
            passwordEditText.setError("Se Necesitan Mas De 8 Caracteres");
            return;
        } else if (!Pattern.compile("[0-9]").matcher(password).find()) {
            passwordEditText.setError("Al Menos Un Numero");
            return;
        } else {
            passwordEditText.setError(null);
        }
        //Campo ConfirmPassword
        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.setError("¡Campo Vacio!");
            Toast.makeText(getContext(), "Faltan Mas Campos Porfavor Verificar", Toast.LENGTH_SHORT).show();
            return;
        } else if (!confirmPassword.equals(password)) {
            confirmPasswordEditText.setError("Deben Ser Iguales");
            return;
        } else {
            confirmPasswordEditText.setError(null);
        }
        //Campo Telefono
        if (telefono.isEmpty()) {
            telefonoTextField.setError("¡Campo Vacio!");
            Toast.makeText(getContext(), "Faltan Mas Campos Porfavor Verificar", Toast.LENGTH_SHORT).show();
            return;
        } else if (telefono.length() < 10) {
            telefonoTextField.setError("Se Necesitan Mas De 10 numeros");
            return;
        } else {
            telefonoTextField.setError(null);
            //Ir Al Metodo Registrar
            registrar(nombre, email, password,telefono);
        }
    }

    //🡣🡣🡣Validar Si La Cuenta Existe o La Crea🡣🡣🡣
    public void registrar(String nombre,String email, String password,String telefono) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Destinos destinos = new Destinos(nombre, email,telefono);
                myRef.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).setValue(destinos);
                getActivity().overridePendingTransition(0, translateUp);
                startActivity(new Intent(getContext(), Home.class));
                getActivity().finish();
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Cuenta Ya Vinculada", Toast.LENGTH_SHORT).show());
    }

}