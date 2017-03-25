package com.example.ai.amann_gohack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Context;
import android.widget.Toast;

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

    /*
     * Deklarasi variabel
     */
    private static final String TAG = "MainActivity";
    private Context context;
    private EditText etUser, etPassword;
    private Button btnLogin, btnRegister;
    private String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        etUser = (EditText) findViewById(R.id.etUser);
        etPassword = (EditText) findViewById(R.id.etPassword);

        btnLogin.setOnClickListener(buttonOperation);
        btnRegister.setOnClickListener(buttonOperation);
    }

    /*
     * Fungsi untuk mengenali button mana yang ditekan oleh user
     * btnLogin button untuk verfikasi login
     * btnRegister untuk masuk ke halaman pendaftaran
     */
    private View.OnClickListener buttonOperation = new View.OnClickListener(){
        @Override
        public void onClick (View v) {
            switch (v.getId()){

                case R.id.btnLogin:
                    String username = etUser.getText().toString();
                    String password = etPassword.getText().toString();

                    if (username.equals("") ) {
                        etUser.setError("Harus diisi!");
                        break;
                    }
                    if (password.equals("")) {
                        etPassword.setError("Harus diisi!");
                        break;
                    }
                    loginCheck(username, password);
                    break;

                case R.id.btnRegister:
                    Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(registerIntent);
                    break;
            }
        }
    };

    /*
    * Fungi loginCHek digunakan untuk memeriksa validasi pengguna
    * Menggunakan POST method request dan library VOLLEY
    * @param email : adalah email yang diinput oleh pengguna
    * @param pass : adalah password yang diinput oleh pengguna
    */
    public void loginCheck(final String email, final String pass ){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.api_domain+"login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject get = new JSONObject(response);
                            String resp = get.getString("response");
                            String message = get.getString("message");

                            if(resp.equals("405")){
                                Toast.makeText(MainActivity.this, message,
                                        Toast.LENGTH_LONG).show();
                            }
                            else if(resp.equals("200")){
                                SharedPreferences.Editor editor =
                                        getSharedPreferences(Config.MY_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putString("email", email);
                                editor.commit();
                                Intent mapActivity = new Intent(MainActivity.this, MapsActivity.class);
                                startActivity(mapActivity);
                                finish();
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
                Map<String, String> params = new HashMap<String, String>();
                params.put("key", Config.key);
                params.put("email_pengguna", email);
                params.put("password_pengguna", pass);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }
}
