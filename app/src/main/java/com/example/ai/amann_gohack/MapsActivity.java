package com.example.ai.amann_gohack;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager myLocationManager;
    private TheLocationListener myLocationListener;
    private Marker currentMarker;
    private Button btnTandai, btnEmergency;
    private TextView warning;
    private String longitude, latitude;
    static final private String TAG = "MapsActivity";
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        warning = (TextView)findViewById(R.id.warning);

        btnTandai = (Button) findViewById(R.id.btnTandai);
        btnTandai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                final AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                final LayoutInflater inflater = MapsActivity.this.getLayoutInflater();

                final View dialogContent = inflater.inflate(R.layout.mark_area, null);

                builder.setView(dialogContent);

                final TextView etLatitude = (TextView) dialogContent.findViewById(R.id.etLatitude);
                final TextView etLongitude = (TextView) dialogContent.findViewById(R.id.etLongitude);
                final EditText etDeskripsi = (EditText) dialogContent.findViewById(R.id.etDeskripsi);

                final String lat = Double.toString(location.getLatitude());
                final String lng = Double.toString(location.getLongitude());

                etLatitude.setText(lat);
                etLongitude.setText(lng);

                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                final AlertDialog dialog = builder.create();

                final Button positiveButton = (Button) dialogContent.findViewById(R.id.posButton);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String deskripsi = etDeskripsi.getText().toString();
                        MapsActivity.this.insertMarker(Double.parseDouble(lat), Double.parseDouble(lng), deskripsi);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        btnEmergency = (Button)findViewById(R.id.btnEmergency);
        btnEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get token
                String token = FirebaseInstanceId.getInstance().getToken();

                // Log and toast
                Location location = myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                latitude = Double.toString(location.getLatitude());
                longitude = Double.toString(location.getLongitude());

                Log.d(TAG, "token = " + token);
                Log.d(TAG, "longitude = " + longitude);
                Log.d(TAG, "latitude = " + latitude);
            }
        });
    }

    public void tokenCheck(final String token, final String longitude, final String latitude ){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.api_domain+"token",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject get = new JSONObject(response);
                            String resp = get.getString("response");
                            String message = get.getString("message");

                            if(resp.equals("200")){
                                Toast.makeText(MapsActivity.this, message,
                                        Toast.LENGTH_LONG).show();
                            }
                            else if(resp.equals("500")){
//                              // Mengirim SMS
                                String number[] = message.split("=");
                                for(String current: number){
                                    current = current.replaceAll("\\s","");
                                    //Toast.makeText(MapsActivity.this, "isi " + current, Toast.LENGTH_LONG).show();
                                    if(current != ""){
                                        //Toast.makeText(MapsActivity.this, "current " + current, Toast.LENGTH_LONG).show();
                                        sendSMS(current, "Aplikasi Amann. Teman anda telah mengirimkan sinyal darurat di koordinat " + latitude + "," + longitude  );
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("key", Config.key);
                params.put("longitude", longitude);
                params.put("latitude", latitude);
                params.put("token", token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
        requestQueue.add(stringRequest);
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
        boolean isGPSEnabled = myLocationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = myLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled) {
            this.goToGPSSetting();
        }
        myLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, myLocationListener);
        myLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 0, myLocationListener);
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

            TheMarker theMarker = new TheMarker(MapsActivity.this);
            theMarker.getCurrentStatus(lat, lng, new TheMarker.VolleyCallback() {
                @Override
                public void onSuccessResponse(String result) {
                    if(result.equals("aman")){
                        warning.setText("Anda berada dalam daerah aman");
                        warning.setTextColor(Color.parseColor("#4285f4"));
                    } else if(result.equals("sedang")){
                        warning.setText("Anda berada dalam daerah rawan");
                        warning.setTextColor(Color.parseColor("#D2DE52"));
                        Vibrator vibrator = (Vibrator) MapsActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(2000);
                    } else if(result.equals("rawan")){
                        warning.setText("Anda berada dalam daerah sangat rawan");
                        warning.setTextColor(Color.parseColor("#F63131"));
                        Vibrator vibrator = (Vibrator) MapsActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(5000);
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

    private void insertMarker(final Double latitude, final Double longitude, final String deskripsi){
        Log.d("url", Config.api_domain+"insertpin");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.api_domain+"insertpin",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("RESPONSE", "resp" + response);
                            JSONObject get = new JSONObject(response);
                            String resp = get.getString("response");
                            String message = get.getString("message");

                            if(resp.equals("500")){
                                Toast.makeText(MapsActivity.this, message,
                                        Toast.LENGTH_LONG).show();
                                return;
                            }
                            else if(resp.equals("200")){
                                Toast.makeText(MapsActivity.this, message,
                                        Toast.LENGTH_LONG).show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                SharedPreferences prefs = getSharedPreferences(Config.MY_PREFS_NAME, MODE_PRIVATE);
                String email = prefs.getString("email", "null");
                Log.d("email", email);

                //String nama_daerah = getAreaName(lat, lng);
                String nama_daerah = "Indonesia";

                Map<String, String> params = new HashMap<String, String>();
                params.put("key", Config.key);
                params.put("email_pengguna", email);
                params.put("nama_daerah", nama_daerah);
                params.put("deskripsi_daerah", deskripsi);
                params.put("lat_daerah", Double.toString(latitude));
                params.put("lng_daerah", Double.toString(longitude));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
        requestQueue.add(stringRequest);
    }

    private String getAreaName(Double latitude, Double longitude){
        String result = null;
        return result;
    }

    private void sendSMS(String phoneNumber, String message) {

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

//---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "SMS sent");
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        //Toast.makeText(getBaseContext(), "Generic failure", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Generic failure");
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
//                        Toast.makeText(getBaseContext(), "No service", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "No servicet");
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
//                        Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Null PDU");
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        //Toast.makeText(getBaseContext(), "Radio off",Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Radio off");
                        break;
                }
            }
        }, new IntentFilter(SENT));

//---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        //Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "SMS delivered");
                        break;
                    case Activity.RESULT_CANCELED:
                        //Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "SMS not delivered");
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
//        Toast.makeText(getApplicationContext(), "SMS sent.",
//                            Toast.LENGTH_LONG).show();

    }
}
