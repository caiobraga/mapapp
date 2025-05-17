package com.caiobraga.mapapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Mapactivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    String cidade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mapactivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        cidade = intent.getStringExtra("cidade");


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        changeLocation();



    }

    public void changeLocation(){
        if(mMap == null){
            return;
        }

        BancoDadosSingleton db = BancoDadosSingleton.getInstance();
        LatLng target = null;
        String mapType = "";
        String titulo = "";

        switch (cidade){
            case "viçosa" :
                target = new LatLng(-20.76313446588187, -42.86664841063025);
                titulo = "Viçosa";
                mapType = "HYBRID";
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case "Colatina" :
                target = new LatLng(-19.54061360351423, -40.63590846745496);
                titulo = "Colatina";
                mapType = "TERRAIN";
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case "DPI" :
                target = new LatLng(-20.765134339893592, -42.86846994734335);
                titulo = "DPI";
                mapType = "SATELLITE";
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            default:
                target = new LatLng(-20.76313446588187, -42.86664841063025);
                titulo = "Default";
                mapType = "NORMAL";
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }

        if (target != null) {
            mMap.addMarker(new MarkerOptions().position(target).title(titulo));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 15));
        }

        // Insert log in database
        Cursor c = db.buscar("Location", new String[]{"id"}, "descricao = '" + cidade + "'", "");
        if (c.moveToFirst()) {
            int id_location = c.getInt(0);
            ContentValues logValues = new ContentValues();
            logValues.put("msg", "Visitou " + cidade);
            logValues.put("timestamp", String.valueOf(System.currentTimeMillis()));
            logValues.put("id_location", id_location);
            db.inserir("logs", logValues);
        }
        c.close();
    }


    public void selectCidade(View view) {
        String cidade_selecionada = view.getTag().toString();
        Toast.makeText(Mapactivity.this, "Clicado: " + cidade_selecionada, Toast.LENGTH_SHORT).show();
        cidade = cidade_selecionada;
        changeLocation();
    }
}