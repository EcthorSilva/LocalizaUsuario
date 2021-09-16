package com.example.localizausuario;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputLayout;

import android.content.pm.PackageManager;
import android.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.example.localizausuario.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    // variaveis globais
    private GoogleMap mMap;

    // Criando o Array de strings para as permissões do maps
    private String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    // Gerenciador de localização
    private LocationManager locationManager;
    //Lista de localizações
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Minimo zoom da camera
        mMap.setMinZoomPreference(6.0f);
        //Máximo zoom da camera
        mMap.setMaxZoomPreference(15.0f);
        //Cria o controle de zoom no mapa
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //Mostrar a bussola no mapa - a partir de um zoom de posicionamento.
        mMap.getUiSettings().setCompassEnabled(true);
        //Mostra os níveis dos andares no interior do estabelecimento. Mapa interno
        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        //Barra de ferramentas do mapa.
        mMap.getUiSettings().setMapToolbarEnabled(true);
        //Gestos de Zoom
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        //Gestos de rolagem (movimento)
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        //Gestos de inclinação
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        //Gestos de rotação
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        //Câmera e Visualização
        mMap.setBuildingsEnabled(true);

        // Add a marker in Sydney and move the camera
        LatLng Local = new LatLng(-23.7027395, -46.6893217);
        mMap.addMarker(new MarkerOptions().position(Local).title("Localização"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Local));
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