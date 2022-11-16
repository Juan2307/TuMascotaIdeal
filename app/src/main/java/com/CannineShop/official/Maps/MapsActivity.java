package com.CannineShop.official.Maps;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.CannineShop.official.Home;
import com.CannineShop.official.R;
import com.CannineShop.official.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    ActivityMapsBinding binding;
    Button myTypeBtn,MyTypeBtn2,MyTypeBtn3;
    public static int zoomOut = R.anim.zoom_out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        myTypeBtn = findViewById(R.id.btnSatelite);
        MyTypeBtn2 = findViewById(R.id.btnHybrid);
        MyTypeBtn3 = findViewById(R.id.btnNormal);

        myTypeBtn.setOnClickListener(v -> mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE));
        MyTypeBtn2.setOnClickListener(v -> mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID));
        MyTypeBtn3.setOnClickListener(v -> mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL));

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        miSitio(googleMap);
        // Add a marker in Sydney and move the camera

        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    public void miSitio(GoogleMap googleMap){
        mMap = googleMap;
        final LatLng punto1 = new LatLng(4.6733086,-74.1123841);
        mMap.addMarker(new MarkerOptions().position(punto1).title("Fundacion Corazon Peludito"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(punto1));


        final LatLng punto2 = new LatLng(4.6545505,-74.0649343);
        mMap.addMarker(new MarkerOptions().position(punto2).title("Centro de Adopción Amor Animal").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(punto2));


        final LatLng punto3 = new LatLng(4.7176354,-74.2051707);
        mMap.addMarker(new MarkerOptions().position(punto3).title("Fundacion Huellas Huerfanas").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(punto3));

    }

    //Dar Click Hacia Atras
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, Home.class));
        overridePendingTransition(0, zoomOut);
        finish();
    }


}