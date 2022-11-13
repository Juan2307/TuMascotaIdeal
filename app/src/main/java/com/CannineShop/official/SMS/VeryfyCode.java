package com.CannineShop.official.SMS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.CannineShop.official.Destinos.Destinos;
import com.CannineShop.official.ForgotPassword;
import com.CannineShop.official.Home;
import com.CannineShop.official.MainActivity;
import com.CannineShop.official.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VeryfyCode extends AppCompatActivity {

    // FireBase
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth mAuth;

    private EditText inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6;
    private String verificationId;
    Bundle datos;
    Button buttonVerify;
    ProgressBar progressBar;
    String nombre, email, password, telefono;
    TextView textMobile, textResendOTP;
    private boolean resendEnabled = false;
    //Variable Transicion
    public static int zoomOut = R.anim.zoom_out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veryfy_code);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //Instancio el Objeto de Firebase
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Usuarios");
        mAuth = FirebaseAuth.getInstance();
        //Finish Firebase
        datos = getIntent().getExtras();
        textMobile = findViewById(R.id.textMobile);
        textResendOTP = findViewById(R.id.textResendOTP);
        startCountDownTimer();


        //Datos Pasados...
        nombre = datos.getString("nombre");
        email = datos.getString("email");
        password = datos.getString("password");
        telefono = datos.getString("mobile");
        textMobile.setText(String.format("+57-%s", getIntent().getStringExtra("mobile")));

        inputCode1 = findViewById(R.id.inputCode1);
        inputCode2 = findViewById(R.id.inputCode2);
        inputCode3 = findViewById(R.id.inputCode3);
        inputCode4 = findViewById(R.id.inputCode4);
        inputCode5 = findViewById(R.id.inputCode5);
        inputCode6 = findViewById(R.id.inputCode6);

        setupOTPInputs();
        progressBar = findViewById(R.id.progressBar);
        buttonVerify = findViewById(R.id.buttonVerify);
        verificationId = getIntent().getStringExtra("verificationId");

        //Clicks
        buttonVerify.setOnClickListener(v -> {
            if (inputCode1.getText().toString().trim().isEmpty()
                    || inputCode2.getText().toString().trim().isEmpty()
                    || inputCode3.getText().toString().trim().isEmpty()
                    || inputCode4.getText().toString().trim().isEmpty()
                    || inputCode5.getText().toString().trim().isEmpty()
                    || inputCode6.getText().toString().trim().isEmpty()) {
                Toast.makeText(VeryfyCode.this, "Por favor, introduzca un código válido", Toast.LENGTH_SHORT).show();
                return;
            }
            String code = inputCode1.getText().toString() + inputCode2.getText().toString() +
                    inputCode3.getText().toString() + inputCode4.getText().toString() +
                    inputCode5.getText().toString() + inputCode6.getText().toString();
            if (verificationId != null) {
                progressBar.setVisibility(View.VISIBLE);
                buttonVerify.setVisibility(View.INVISIBLE);
                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                        verificationId, code
                );
                FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    buttonVerify.setVisibility(View.VISIBLE);
                    if (task.isSuccessful()) {
                        //Ir Al Metodo Registrar
                        registrar(nombre, email, password, telefono);
                    }
                }).addOnFailureListener(e -> Toast.makeText(VeryfyCode.this, "El código de verificación ingresado no es válido", Toast.LENGTH_SHORT).show());
            }
        });
        textResendOTP.setOnClickListener(v -> {
            if (resendEnabled) {
                startCountDownTimer();
                PhoneAuthProvider.getInstance().verifyPhoneNumber("+57" + getIntent().getStringExtra("mobile"),
                        60, TimeUnit.SECONDS, VeryfyCode.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(VeryfyCode.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                verificationId = newVerificationId;
                                Toast.makeText(VeryfyCode.this, "SMS Enviado", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        });
    }

    //🡣🡣🡣Validar Si La Cuenta Existe o La Crea🡣🡣🡣
    public void registrar(String nombre, String email, String password, String telefono) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Destinos destinos = new Destinos(nombre, email, telefono);
                myRef.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).setValue(destinos);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.sendEmailVerification();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                Toast.makeText(this, "Cuenta Creada", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Cuenta Ya Vinculada", Toast.LENGTH_SHORT).show());
    }

    private void startCountDownTimer() {
        resendEnabled = false;
        textResendOTP.setTextColor(Color.parseColor("#99000000"));

        new CountDownTimer(30000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                textResendOTP.setText("REENVIAR CODIGO (" + (millisUntilFinished / 1000) + ")");
            }

            @Override
            public void onFinish() {
                resendEnabled = true;
                textResendOTP.setText("REENVIAR CODIGO");
                textResendOTP.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        }.start();
    }

    private void setupOTPInputs() {
        inputCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    //Flecha Atras
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(0, zoomOut);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}