package com.example.proyectodm;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.support.v4.content.ContextCompat.getSystemService;
import static com.example.proyectodm.MainActivity.idcel;

public class FragmentMaps extends SupportMapFragment implements OnMapReadyCallback {

    double lat, lon;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    ArrayList<Marker> tmpRealtimeMarker = new ArrayList<>();
    ArrayList<Marker> realtimeMarker = new ArrayList<>();

    ArrayList<Marker> tmpRealtimeMarker1 = new ArrayList<>();
    ArrayList<Marker> realtimeMarker1 = new ArrayList<>();

    public FragmentMaps() { }


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View rootView = super.onCreateView(layoutInflater, viewGroup, bundle);


        if(getArguments() != null) {
            this.lat = getArguments().getDouble("lat");
            this.lon = getArguments().getDouble("lon");
        }

        getMapAsync(this);


        return rootView;
    }



    @Override
    public void onMapReady(final GoogleMap googleMap) {

        final LatLng latLng = new LatLng(lat, lon);
        float zoom = 17;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
/*
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.addMarker(new MarkerOptions().position(latLng));
        UiSettings settings = googleMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
        */

        databaseReference.child("marcadores").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (Marker marker:realtimeMarker){
                    marker.remove();
                }
                String cel = "";
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Contacto con = snapshot.getValue(Contacto.class);
                    Double latitud = con.getLatitud();
                    Double longitud = con.getLongitud();
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(latitud, longitud));
                    markerOptions.title(con.getNombre());
                    if (con.getId() != "") {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.punto_reu1));
                    }
                    tmpRealtimeMarker.add(googleMap.addMarker(markerOptions));

                    cel = con.getIdcelular();
                }
                realtimeMarker.clear();
                realtimeMarker.addAll(tmpRealtimeMarker);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("contacto").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (Marker marker:realtimeMarker1){
                    marker.remove();
                }

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Contacto con = snapshot.getValue(Contacto.class);
                    Double latitud = con.getLatitud();
                    Double longitud = con.getLongitud();
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(latitud, longitud));
                    markerOptions.title(con.getNombre());

                    tmpRealtimeMarker1.add(googleMap.addMarker(markerOptions));


                }


                realtimeMarker1.clear();
                realtimeMarker1.addAll(tmpRealtimeMarker1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                MainActivity.lan = latLng.latitude;
                MainActivity.lon = latLng.longitude;

            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                LatLng latLng1 = marker.getPosition();
                MainActivity.lane = latLng1.latitude;
                MainActivity.lone = latLng1.longitude;

                return false;
            }
        });
    }



}
