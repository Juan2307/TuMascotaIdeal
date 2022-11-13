package com.CannineShop.official;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.CannineShop.official.Internet.Internet;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Login_Fragment extends Fragment {

    TextInputEditText emailEditText, passwordEditText;
    TextView forgetPass;
    Button singup;
    ImageView imagenView;
    FirebaseAuth mAuth;
    float v = 0;
    // Transicion
    public static int translateUp = R.anim.slide_out_up;
    public static int zoomOut = R.anim.zoom_out;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_login_, container, false);

        mAuth = FirebaseAuth.getInstance();
        emailEditText = root.findViewById(R.id.emailEditText);
        passwordEditText = root.findViewById(R.id.passwordEditText);
        forgetPass = root.findViewById(R.id.forgetPassword);
        singup = root.findViewById(R.id.login);
        imagenView = root.findViewById(R.id.imagenView);

        forgetPass.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ForgotPassword.class));
            getActivity().overridePendingTransition(0, zoomOut);
            getActivity().finish();
        });
        singup.setOnClickListener(v -> {
            if (Internet.isOnline(getContext())) {
                validate();
            } else {
                Toast.makeText(getContext(), "¡Sin Acceso A Internet, Verifique Su Conexión.!", Toast.LENGTH_SHORT).show();
            }
        });

        emailEditText.setTranslationY(300);
        passwordEditText.setTranslationY(300);
        forgetPass.setTranslationY(300);
        imagenView.setTranslationY(300);
        singup.setTranslationY(300);

        emailEditText.setAlpha(v);
        passwordEditText.setAlpha(v);
        forgetPass.setAlpha(v);
        imagenView.setAlpha(v);
        singup.setAlpha(v);

        emailEditText.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(150).start();
        passwordEditText.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(300).start();
        forgetPass.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(450).start();
        imagenView.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(450).start();
        singup.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(600).start();
        return root;
    }

    public void validate() {
        String email = Objects.requireNonNull(emailEditText.getText()).toString().trim().toLowerCase();
        String password = Objects.requireNonNull(passwordEditText.getText()).toString().trim();
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
        iniciarSesion(email, password);

    }

    //🡣🡣🡣Validar Si La Cuenta Existe🡣🡣🡣
    private void iniciarSesion(String email, String password) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!user.isEmailVerified()) {
                    Toast.makeText(getContext(), "Correo Electronico No Ha Sido Verificado...Revisar Correo En SPAM", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Inicio", Toast.LENGTH_SHORT).show();
                    getActivity().overridePendingTransition(0, translateUp);
                    startActivity(new Intent(getContext(), Home.class));
                    getActivity().finish();
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Usuario y/o Contraseña Incorrectos", Toast.LENGTH_SHORT).show());
    }


}