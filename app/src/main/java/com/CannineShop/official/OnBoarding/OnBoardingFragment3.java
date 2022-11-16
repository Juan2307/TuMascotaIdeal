package com.CannineShop.official.OnBoarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.CannineShop.official.Home;
import com.CannineShop.official.MainActivity;
import com.CannineShop.official.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OnBoardingFragment3 extends Fragment {

    FloatingActionButton button;
    TextView skip;
    //Variables Transicion
    public static int desplazamiento_arriba = R.anim.slide_out_up;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_on_boarding3, container, false);
        button = root.findViewById(R.id.fab);
        skip = root.findViewById(R.id.Skip);


        button.setOnClickListener(view -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
            if (user != null || account != null) {
                getActivity().overridePendingTransition(0, desplazamiento_arriba);
                startActivity(new Intent(getActivity(), Home.class));
                getActivity().finish();
            }else{
                Intent intent = new Intent(getActivity(), MainActivity.class);
                getActivity().overridePendingTransition(0, desplazamiento_arriba);
                startActivity(intent);
                getActivity().finish();
            }
        });

        skip.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
            if (user != null || account != null) {
                getActivity().overridePendingTransition(0, desplazamiento_arriba);
                startActivity(new Intent(getActivity(), Home.class));
                getActivity().finish();
            }else{
                Intent intent = new Intent(getActivity(), MainActivity.class);
                getActivity().overridePendingTransition(0, desplazamiento_arriba);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return root;
    }
}
