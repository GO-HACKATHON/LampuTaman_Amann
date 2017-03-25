package com.example.ai.amann_gohack;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class MainActivity extends AppCompatActivity {

    //Deklarasi variable
    private static final String TAG = "MainActivity";
    private Context context;
    EditText etUser, etPassword;
    Button btnLogin, btnRegister;
    String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        etUser = (EditText) findViewById(R.id.etUser);
        etPassword = (EditText) findViewById(R.id.etPassword);

        Log.d(TAG, "token = " + token);

        // buttonOperation adalah fungsi untuk untuk mengidentifikasi button yang ditekan
        btnLogin.setOnClickListener(buttonOperation);
//        btnRegister.setOnClickListener(buttonOperation);

    }
// Command ketika button di tekan
    private View.OnClickListener buttonOperation = new View.OnClickListener(){
        @Override
        public void onClick (View v) {
            switch (v.getId()){

                case R.id.btnLogin:
                    String username = etUser.getText().toString();
                    String password = etPassword.getText().toString();

                    if (username.equals("") || password.equals("")) {
                        etUser.setError("Harus diisi!");
                        break;
                    }
                    if (password.equals("")) {
                        etPassword.setError("Harus diisi!");
                        break;
                    }
                    loginCheck(username, password);
            }
        }
    };
// Fungsi untuk mengirim input username dan password ke web service dan mengecek apakah login berhasil atau sukses
    public void loginCheck( final String uname, final String pass ){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://10.17.10.210/amann_api/public/api/login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.v("POST", response);
                            JSONObject get = new JSONObject(response);
                            String resp = get.getString("response");
                            String msg = get.getString("message");

                            if(resp.equals("405")){
                                Log.d(TAG, "hasil " + resp + msg);

                            }
                            else if(resp.equals("200")){
                                Log.d(TAG, "hasil " + resp + msg);
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
                params.put("key", "matapancing");
                params.put("email_pengguna", uname);
                params.put("password_pengguna", pass);
                Log.d(TAG, "var pass" + uname + pass);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }



}
