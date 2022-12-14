package com.CannineShop.official.SMS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.CannineShop.official.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SendCode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_code);

        final EditText inputMobile = findViewById(R.id.inputMobile);
        Button buttonGetOTP = findViewById(R.id.buttonGetOTP);

        final ProgressBar progressBar = findViewById(R.id.progressBar);

        buttonGetOTP.setOnClickListener(v -> {
            if (inputMobile.getText().toString().trim().isEmpty()) {
                Toast.makeText(SendCode.this, "Ingrese Un Numero", Toast.LENGTH_SHORT).show();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            buttonGetOTP.setVisibility(View.INVISIBLE);
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+57" + inputMobile.getText().toString().trim(),
                    60,
                    TimeUnit.SECONDS,
                    SendCode.this,
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            progressBar.setVisibility(View.GONE);
                            buttonGetOTP.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            progressBar.setVisibility(View.GONE);
                            buttonGetOTP.setVisibility(View.VISIBLE);
                            Toast.makeText(SendCode.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            progressBar.setVisibility(View.GONE);
                            buttonGetOTP.setVisibility(View.VISIBLE);
                            Intent intent = new Intent(getApplicationContext(), VeryfyCode.class);
                            intent.putExtra("mobile", inputMobile.getText().toString());
                            intent.putExtra("verificationId", verificationId);
                            startActivity(intent);
                        }
                    }
            );
        });
    }
}