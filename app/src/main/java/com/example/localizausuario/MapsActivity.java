package com.example.localizausuario;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.CircleOptions;

import android.content.pm.PackageManager;
import android.app.AlertDialog;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    // variaveis globais
    private GoogleMap mMap;

    // Criando o Array de strings para as permissões do maps
    private String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    // Gerenciador de localização
    private LocationManager locationManager;
    //Lista de localizaçõe
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Validando as permissões
        Permissoes.validarPermissoes(permissoes, this, 1);

        //Criando objeto para gerenciar localização do usuario
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //Istanciando o listener para gerenciar localização do usuario
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.d("Localização", "onLocationChanged: " + location.toString());
            }
        };

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        double etLat = Double.parseDouble(com.example.localizausuario.MainActivity.etLat.getText().toString());
        double etLong = Double.parseDouble(com.example.localizausuario.MainActivity.etLong.getText().toString());

        LatLng Local = new LatLng(etLat, etLong);
        mMap.addMarker(new MarkerOptions()
                .position(Local)
                .snippet("Local")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.placeholder))
                .title("Localização"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Local, 11));

        // Gerando o circulo
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(Local);
        circleOptions.fillColor(Color.argb(50, 0, 255, 255));
        circleOptions.strokeWidth(10);
        circleOptions.strokeColor(Color.argb(50, 47, 79, 79));
        //Medida em metros
        circleOptions.radius(10000.00);
        //Aplicando o circulo no mapa
        mMap.addCircle(circleOptions);

        mMap.setMinZoomPreference(6.0f); // Minimo zoom da camera
        mMap.setMaxZoomPreference(16.0f); // Máximo zoom da camera
        mMap.getUiSettings().setZoomControlsEnabled(true); // Cria o controle de zoom no mapa
        mMap.getUiSettings().setCompassEnabled(true); // Mostrar a bussola no mapa - a partir de um zoom de posicionamento.
        mMap.getUiSettings().setZoomGesturesEnabled(true); //Gestos de Zoom
        mMap.getUiSettings().setScrollGesturesEnabled(true); //Gestos de rolagem (movimento)
        mMap.getUiSettings().setRotateGesturesEnabled(true); //Gestos de rotação

    }

    //Criando a janela para permissões do usuario a sua localização
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Percorrendo a permissão do usuário
        for (int permissaoResultado : grantResults) {
            //Se permissão for negada
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                //Mostra um alerta
                validacaoUsuario();

            }
            //Se permissão for concedida
            else if (permissaoResultado == PackageManager.PERMISSION_GRANTED) {
                //Recupera localização do usuário
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            0, 0,
                            locationListener
                    );
                }

            }
        }
    }

    //Criando o alertDialog
    private void validacaoUsuario() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissão negada!");
        builder.setMessage("Para utilizar este aplicativo é necessário aceitar as permissões!");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", (dialogInterface, i) -> {
            finish();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}