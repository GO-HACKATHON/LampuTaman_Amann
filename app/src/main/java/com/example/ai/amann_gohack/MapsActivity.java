package com.example.ai.amann_gohack;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager myLocationManager;
    private TheLocationListener myLocationListener;
    private Marker currentMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        new RefreshLocation().execute();
    }

    private class RefreshLocation extends AsyncTask <Void, Void, Void> {
        ProgressBar progressBar;
        @Override
        protected void onPreExecute() {
            progressBar = (ProgressBar) findViewById(R.id.refreshProgressBar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            new Thread(){
                public void run(){
                    MapsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MapsActivity.this.goToCurrentLocation();
                        }
                    });
                }
            }.start();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(MapsActivity.this, "Location Updated",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void goToCurrentLocation (){
        myLocationManager = (LocationManager) MapsActivity.this.getSystemService(LOCATION_SERVICE);
        myLocationListener = new TheLocationListener();
        myLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, myLocationListener);
        boolean isGPSEnabled = myLocationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGPSEnabled) {
            this.goToGPSSetting();
        } else {
            if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            Location location = null;
            while (location == null){
                location = myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) break;
                else location = myLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (currentMarker == null){
                currentMarker = mMap.addMarker(new MarkerOptions().position(latLng));
            } else {
                currentMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(19));
        }
    }

    private class TheLocationListener implements LocationListener{
        double lat, lng;
        @Override
        public void onLocationChanged(Location newLocation) {
            final LatLng latLng = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            Projection projection = mMap.getProjection();

            if (currentMarker == null) return;

            Point startPoint = projection.toScreenLocation(currentMarker.getPosition());
            final LatLng startLatLng = projection.fromScreenLocation(startPoint);
            final long duration = 1000;

            final LinearInterpolator interpolator = new LinearInterpolator();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed/duration);

                    lng = t * latLng.longitude + (1-t) * startLatLng.longitude;
                    lat = t * latLng.latitude + (1-t) * startLatLng.latitude;

                    if (t<1.0){
                        handler.postDelayed(this, 16);
                    } else {
                        currentMarker.setPosition(new LatLng(lat, lng));
                    }
                }
            });
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    }

    private void goToGPSSetting () {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "GPS mati. Ingin menyalakannya melalui pengaturan?";

        builder.setTitle("GPS Dalam Keadaan Mati.");
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MapsActivity.this.startActivity(new Intent(action));
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.create().show();
    }
}
