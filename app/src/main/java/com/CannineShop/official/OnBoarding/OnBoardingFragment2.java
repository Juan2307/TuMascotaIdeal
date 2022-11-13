package com.CannineShop.official.OnBoarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.CannineShop.official.MainActivity;
import com.CannineShop.official.R;

import java.util.Objects;

public class OnBoardingFragment2 extends Fragment {

    TextView skip;
    //Variables Transicion
    public static int desplazamiento_arriba = R.anim.slide_out_up;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_on_boarding2, container, false);

        skip = root.findViewById(R.id.Skip);
        skip.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            getActivity().overridePendingTransition(0, desplazamiento_arriba);
            startActivity(intent);
            getActivity().finish();
        });
        return root;
    }

}
