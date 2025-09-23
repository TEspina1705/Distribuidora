package com.example.distribuidora;

import static android.widget.Toast.*;

import android.Manifest;
import android.app.jank.RelativeFrameTimeHistogram;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class DistanciaActivity2 extends AppCompatActivity  {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private TextView txtubi ;
    private TextView monto;

    private TextView valorDespacho;

    private TextView total;

    private TextView sinCobro;

    private TextView dist;

    // Radio de la Tierra en kilómetros
    private static final double RADIO_TIERRA = 6371.0;
    //latidid y longitud de usuario ubicacion
    double latitudUbi ;
    double longitudUbi ;
    //ubicacion de Bodega Distribuidora
    private static final double latitudPlaza = -33.438056;
    private static final double LongitudPlaza = -70.650278;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_distancia2);


        txtubi = findViewById(R.id.txt_ubi);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        monto = findViewById(R.id.txt_monto);
        valorDespacho = findViewById(R.id.edit_valorDespacho);
        total = findViewById(R.id.edit_Total);
        sinCobro = findViewById(R.id.txt_sincobro);
        dist = findViewById(R.id.txt_dist);





        obtenerUbicacionActual();



        Button botonCalcular = findViewById(R.id.btn_calcular);
        botonCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!monto.getText().toString().trim().isEmpty()){
                    //obtenerUbicacionActual();
                    calculoDespacho();
                }else{
                    makeText(DistanciaActivity2.this, "Debe ingresar monto", LENGTH_SHORT).show();
                }
            }
        });
    }


    private void obtenerUbicacionActual(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
            if(location != null){
                txtubi.setText( "Latitud: "+location.getLatitude() + "\n" + "Longitud: "+location.getLongitude());

                 latitudUbi = location.getLatitude();
                 longitudUbi = location.getLongitude();
                //double latitudPlaza = -33.438056;
               // double LongitudPlaza = -70.650278;

               /* double distancia = calcularDistancia(latitudUbi,longitudUbi, latitudPlaza, LongitudPlaza );

                String costoDespacho ;
                Double costo = 0D;
                if (distancia > 20) {
                    sinCobro.setText("Despacho no disponible, excede el límite de 20 km.");
                    valorDespacho.setText("0");
                } else if (Integer.parseInt(monto.getText().toString().trim()) >= 50000) {
                    valorDespacho.setText("0");
                } else if (Integer.parseInt(monto.getText().toString().trim()) >= 25000 && Integer.parseInt(monto.getText().toString().trim()) < 50000) {
                    costo = distancia * 150;
                    valorDespacho.setText(String.valueOf(Math.round(costo)));
                } else {
                    costo = distancia * 300;
                    valorDespacho.setText(String.valueOf(Math.round(costo)));
                }

                Double tot = Double.parseDouble(monto.getText().toString().trim()) + costo;
                total.setText(String.valueOf(Math.round(tot)));


                */
            }
        });

    }


    public void onRequestPermissionsResult(int requestCode,  @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si el usuario aceptó, vuelves a pedir la ubicación
                obtenerUbicacionActual();
            } else {
                makeText(this, "Permiso denegado", LENGTH_SHORT).show();
            }
        }
    }


    public static double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        // Convertir de grados a radianes
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        // Fórmula de Haversine
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.cos(Math.toRadians(lat1)) *
                        Math.cos(Math.toRadians(lat2)) *
                        Math.pow(Math.sin(dLon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        return RADIO_TIERRA * c;
    }


    private void calculoDespacho (){

        double distancia = calcularDistancia(latitudUbi,longitudUbi, latitudPlaza, LongitudPlaza );
        dist.setText(String.valueOf(Math.round(distancia)) + "Km");

        String costoDespacho ;
        Double costo = 0D;
        if (distancia > 20) {
            sinCobro.setText("Despacho no disponible, excede el límite de 20 km.");
            valorDespacho.setText("0");
        } else if (Integer.parseInt(monto.getText().toString().trim()) >= 50000) {
            valorDespacho.setText("0");
        } else if (Integer.parseInt(monto.getText().toString().trim()) >= 25000 && Integer.parseInt(monto.getText().toString().trim()) < 50000) {
            costo = distancia * 150;
            valorDespacho.setText(String.valueOf(Math.round(costo)));
        } else {
            costo = distancia * 300;
            valorDespacho.setText(String.valueOf(Math.round(costo)));
        }

        Double tot = Double.parseDouble(monto.getText().toString().trim()) + costo;
        total.setText(String.valueOf(Math.round(tot)));

    }



}