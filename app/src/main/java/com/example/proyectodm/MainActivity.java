package com.example.proyectodm;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.BitSet;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient mFusedLocationClient;

    TextView txtreunion;

    private static final long MIN_TIME = 10000;
    public static ArrayList<Contacto> fbArray = new ArrayList<Contacto>();
    public static String idcel;

    public static double lan = 0,lon = 0, lane=0, lone=0;// se usa para añadir y eliminar marcadores

    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    PendingIntent pendingIntent;
    private static final int notificacionid = 12345;
    private static final String canal = "NOTIFICACION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtreunion = (TextView) findViewById(R.id.txtreunion);

        inicializarFirebase();

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);

        } else {
            iniciarLocalizacion();
        }

        notificacion();


    }

    private void notificacion() {
        databaseReference.child("marcadores").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                idcel = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                String cel = "";
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Contacto con = snapshot.getValue(Contacto.class);

                    cel = con.getIdcelular();
                }

                if(!cel.equals(idcel)){
                    crearNotificacionCanal();
                    crearNotificacion();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }


    public void insertarpos(double la, double lo){

        idcel = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        if(la!=0 && lo!=0){
            //String idstring = databaseReference.push().getKey();

            Contacto c = new Contacto();
            c.setIdcelular("-"+idcel);
            c.setId("");
            c.setNombre(EntrarActivity.nombre);
            c.setLatitud(la);
            c.setLongitud(lo);

            firebaseDatabase.getReference("contacto").child("-"+idcel).setValue(c);
        }

    }

    private void iniciarLocalizacion() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Localizacion local = new Localizacion();

        local.setMainActivity(this);

        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!gpsEnabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, 0, local);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, 0, local);
        
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]grantResults) {
        if(requestCode == 1000) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                iniciarLocalizacion();
                return;
            }
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mimenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){

        String nombrereu = txtreunion.getText().toString();
        idcel = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String idstring = databaseReference.push().getKey();

        switch (item.getItemId()){
            case R.id.icon_reunion:

                if(lan!=0 && lon!=0){

                    Contacto c = new Contacto();
                    c.setIdcelular(idcel);
                    c.setId(idstring);
                    c.setNombre(nombrereu);
                    c.setLatitud(lan);
                    c.setLongitud(lon);
                    fbArray.add(c);

                    firebaseDatabase.getReference("marcadores").child(idstring).setValue(c);
                }

                break;

            case R.id.icon_del_loc:

                for (int i=0;i<fbArray.size();i++){
                    if(fbArray.get(i).getLatitud()==lane && fbArray.get(i).getLongitud()==lone){
                        firebaseDatabase.getReference("marcadores").child(fbArray.get(i).getId()).removeValue();
                        fbArray.remove(i);
                        break;
                    }
                }




                break;
        }
        return true;
    }

    public void crearNotificacionCanal(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Notificacion";
            NotificationChannel notificationChannel = new NotificationChannel(canal, name, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public void crearNotificacion(){
        //notificacion
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),canal);
        builder.setSmallIcon(R.drawable.ic_sms_black_24dp);
        builder.setContentTitle("Notificacion");
        builder.setContentText("Nueva Evento");
        builder.setPriority(NotificationCompat. PRIORITY_HIGH);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(notificacionid, builder.build());
    }



}
