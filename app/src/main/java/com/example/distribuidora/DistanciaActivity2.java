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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
//pantalla Calculo de Distancia
public class DistanciaActivity2 extends AppCompatActivity  {

    //variables
    private FusedLocationProviderClient fusedLocationProviderClient;
    private TextView txtubi ;
    private TextView monto;
    private TextView valorDespacho;
    private TextView total;
    private TextView sinCobro;
    private TextView dist;
    Button botonCalcular;
    DatabaseReference database;

    private static final double RADIO_TIERRA = 6371.0;// Radio de la Tierra en kilómetros

    double latitudUbi ;  //latidid y longitud de ubicacion de usuario
    double longitudUbi ;

    private static final double latitudPlaza = -33.438056;//ubicacion de Bodega Distribuidora plaza de armas
    private static final double LongitudPlaza = -70.650278;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_distancia2);

        //conecta "ubicaciones" con Firebase
        database = FirebaseDatabase.getInstance().getReference("ubicaciones");
        //conexion los editText y textView con codigo
        txtubi = findViewById(R.id.txt_ubi);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        monto = findViewById(R.id.txt_monto);
        valorDespacho = findViewById(R.id.edit_valorDespacho);
        total = findViewById(R.id.edit_Total);
        sinCobro = findViewById(R.id.txt_sincobro);
        dist = findViewById(R.id.txt_dist);

        //obtiene ubicacion cuando se abre la pantalla
        obtenerUbicacionActual();

        botonCalcular = findViewById(R.id.btn_calcular);//calculo monto despacho al presionar boton calcular

        botonCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!monto.getText().toString().trim().isEmpty()){
                     calculoDespacho();
                }else{
                    makeText(DistanciaActivity2.this, "Debe ingresar monto", LENGTH_SHORT).show();
                }
            }
        });
    }

    private void obtenerUbicacionActual(){
        //verifica permisos
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        //si tiene ubicacion la muestra en pantalla en "tu ubicación"
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
            if(location != null){
                txtubi.setText( "Latitud: "+location.getLatitude() + "\n" + "Longitud: "+location.getLongitude());

                 latitudUbi = location.getLatitude();//guarda en las variables la ubicacion
                 longitudUbi = location.getLongitude();

                guardarUbicacionEnFirebase(latitudUbi, longitudUbi);//llama la funcion para guardar ubicacion en Firebase
            }
        });

    }
    //funcion para guardar ubicacion en Firebase
    private void guardarUbicacionEnFirebase(double latitudUbi, double longitudUbi) {
        String id = database.push().getKey(); // genera un ID único
        Map<String, Object> datos = new HashMap<>();
        datos.put("latitud", latitudUbi);
        datos.put("longitud", longitudUbi);
        datos.put("timestamp", System.currentTimeMillis());

        if (id != null) {
            database.child(id).setValue(datos)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(this, "Ubicación guardada en Firebase", Toast.LENGTH_SHORT).show()
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        }
    }
    //metodo maneja respuesta de usuario con los permisos
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

    //fórmula de Haversine para calcular distancia en km entre dos puntos gps
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
    //calcula la distancia de usuario con la bodega
        double distancia = calcularDistancia(latitudUbi,longitudUbi, latitudPlaza, LongitudPlaza );
        dist.setText(String.valueOf(Math.round(distancia)) + "Km");
    //reglas de negocio para calcular valor despacho
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
        total.setText(String.valueOf(Math.round(tot)));//muestra total de compra + despacho

    }



}