package com.CannineShop.official;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.CannineShop.official.Internet.Internet;
import com.CannineShop.official.SMS.VeryfyCode;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;

public class SignUpFragment extends Fragment {

    //Variables
    TextInputEditText nameTextField, telefonoTextField, emailEditText, passwordEditText, confirmPasswordEditText;
    Button singUp;
    ProgressBar progressBar;
    AlertDialog mdialog;
    // Transicion
    public static int translateUp = R.anim.slide_out_up;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_sign_up, container, false);

        nameTextField = root.findViewById(R.id.nameTextField);
        emailEditText = root.findViewById(R.id.emailEditText_SignUp);
        passwordEditText = root.findViewById(R.id.passwordEditText_SignUp);
        confirmPasswordEditText = root.findViewById(R.id.confirmPasswordEditText);
        telefonoTextField = root.findViewById(R.id.telefonoTextField);
        progressBar = root.findViewById(R.id.progressBar);
        mdialog = new SpotsDialog.Builder().setContext(getContext()).setMessage("Espere Un Momento").setCancelable(false).build();

        singUp = root.findViewById(R.id.btnSignup);
        singUp.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            singUp.setVisibility(View.INVISIBLE);
            if (Internet.isOnline(requireActivity())) {
                validate();
            } else {
                progressBar.setVisibility(View.GONE);
                singUp.setVisibility(View.VISIBLE);
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
            progressBar.setVisibility(View.GONE);
            singUp.setVisibility(View.VISIBLE);
            return;
        } else if (nombre.length() < 3) {
            nameTextField.setError("¿Cual Es Tu Nombre");
            Toast.makeText(getContext(), "Verifica El Campo Nombre", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            singUp.setVisibility(View.VISIBLE);
            return;
        } else {
            nameTextField.setError(null);
        }
        //Campo Email
        if (email.isEmpty()) {
            emailEditText.setError("¡Campo Vacio!");
            Toast.makeText(getContext(), "Faltan Mas Campos Porfavor Verificar", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            singUp.setVisibility(View.VISIBLE);
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("¡Correo Invalido!");
            progressBar.setVisibility(View.GONE);
            singUp.setVisibility(View.VISIBLE);
            return;
        } else {
            emailEditText.setError(null);
        }
        //Campo Password
        if (password.isEmpty()) {
            passwordEditText.setError("¡Campo Vacio!");
            Toast.makeText(getContext(), "Faltan Mas Campos Porfavor Verificar", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            singUp.setVisibility(View.VISIBLE);
            return;
        } else if (password.length() < 8) {
            passwordEditText.setError("Se Necesitan Mas De 8 Caracteres");
            progressBar.setVisibility(View.GONE);
            singUp.setVisibility(View.VISIBLE);
            return;
        } else if (!Pattern.compile("[0-9]").matcher(password).find()) {
            passwordEditText.setError("Al Menos Un Numero");
            progressBar.setVisibility(View.GONE);
            singUp.setVisibility(View.VISIBLE);
            return;
        } else {
            passwordEditText.setError(null);
        }
        //Campo ConfirmPassword
        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.setError("¡Campo Vacio!");
            Toast.makeText(getContext(), "Faltan Mas Campos Porfavor Verificar", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            singUp.setVisibility(View.VISIBLE);
            return;
        } else if (!confirmPassword.equals(password)) {
            confirmPasswordEditText.setError("Deben Ser Iguales");
            progressBar.setVisibility(View.GONE);
            singUp.setVisibility(View.VISIBLE);
            return;
        } else {
            confirmPasswordEditText.setError(null);
        }
        //Campo Telefono
        if (telefono.isEmpty()) {
            telefonoTextField.setError("¡Campo Vacio!");
            Toast.makeText(getContext(), "Faltan Mas Campos Porfavor Verificar", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            singUp.setVisibility(View.VISIBLE);
            return;
        } else if (telefono.length() < 10) {
            telefonoTextField.setError("Se Necesitan Mas De 10 numeros");
            progressBar.setVisibility(View.GONE);
            singUp.setVisibility(View.VISIBLE);
            return;
        } else {
            telefonoTextField.setError(null);
            //Ir Al Metodo Validar
            verificarEmailInFirebase(nombre, email, password, telefono);
        }
    }

    public void verificarEmailInFirebase(String nombre, String email, String password, String telefono) {
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean check = !Objects.requireNonNull(task.getResult().getSignInMethods()).isEmpty();
                if (check) {
                    Toast.makeText(getActivity(), "El email " + email + " esta en uso", Toast.LENGTH_LONG).show();
                    emailEditText.setText("");
                    progressBar.setVisibility(View.GONE);
                    singUp.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getActivity(), "Validando Información", Toast.LENGTH_LONG).show();
                    enviarSMS(nombre, email, password, telefono);
                }
            }
        });
    }

    private void enviarSMS(String nombre, String email, String password, String telefono) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+57" + telefono, 60, TimeUnit.SECONDS, requireActivity(), new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                progressBar.setVisibility(View.GONE);
                singUp.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                progressBar.setVisibility(View.GONE);
                singUp.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getActivity(), VeryfyCode.class);
                intent.putExtra("nombre", nombre);
                intent.putExtra("email", email);
                intent.putExtra("password", password);
                intent.putExtra("mobile", telefono);
                intent.putExtra("verificationId", verificationId);
                startActivity(intent);
                getActivity().finish();
                nameTextField.setText("");
                emailEditText.setText("");
                passwordEditText.setText("");
                confirmPasswordEditText.setText("");
                telefonoTextField.setText("");
            }
        });
    }



}