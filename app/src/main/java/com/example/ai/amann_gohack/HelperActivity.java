package com.example.ai.amann_gohack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.R.attr.id;
import static android.R.attr.password;
import static com.example.ai.amann_gohack.R.id.etEmail;
import static com.example.ai.amann_gohack.R.id.etNama;
import static com.example.ai.amann_gohack.R.id.etNohp;
import static com.example.ai.amann_gohack.R.id.etPassword;
import static com.example.ai.amann_gohack.R.id.etUser;

public class HelperActivity extends AppCompatActivity {

    private static final String TAG = "HelperActivity";
    private EditText etUser1, etUser2, etUser3, etUser4, etUser5, etUser6;
    private Button btnRegister;
    private String username, password, nohp, nama, tgl, man, woman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper);
        man = "";
        woman = "";
        etUser1 = (EditText) findViewById(R.id.etUser1);
        etUser2 = (EditText) findViewById(R.id.etUser2);
        etUser3 = (EditText) findViewById(R.id.etUser3);
        etUser4 = (EditText) findViewById(R.id.etUser4);
        etUser5 = (EditText) findViewById(R.id.etUser5);
        etUser6 = (EditText) findViewById(R.id.etUser6);

        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");
        password = extras.getString("password");
        nohp = extras.getString("hp");
        nama = extras.getString("nama");
        tgl = extras.getString("tgl");

        if (extras.getString("sex").equals("Male")){
            man = extras.getString("sex");
        }else{
            woman = extras.getString("sex");
        }

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(buttonOperation);
    }

    /*
     * Fungsi untuk mengenali button mana yang ditekan oleh user
     * btnRegister untuk masuk ke halaman pendaftaran
     */
    private View.OnClickListener buttonOperation = new View.OnClickListener(){
        @Override
        public void onClick (View v) {
            switch (v.getId()){

                case R.id.btnRegister:
                    String user1 = etUser1.getText().toString();
                    String user2 = etUser2.getText().toString();
                    String user3 = etUser3.getText().toString();
                    String user4 = etUser4.getText().toString();
                    String user5 = etUser5.getText().toString();
                    String user6 = etUser6.getText().toString();

                    if (user1.equals("") ) {
                        etUser1.setError("Harus diisi!");
                        break;
                    }
                    if (user2.equals("") ) {
                        etUser2.setError("Harus diisi!");
                        break;
                    }
                    if (user3.equals("") ) {
                        etUser3.setError("Harus diisi!");
                        break;
                    }
                    if (user4.equals("") ) {
                        etUser4.setError("Harus diisi!");
                        break;
                    }
                    if (user5.equals("") ) {
                        etUser5.setError("Harus diisi!");
                        break;
                    }
                    if (user6.equals("") ) {
                        etUser6.setError("Harus diisi!");
                        break;
                    }
                    if(man.equals("")) registerCheck(username, password, nohp, nama, tgl, user1, user2, user3, user4, user5, user6, woman);
                    else registerCheck(username, password, nohp, nama, tgl, user1, user2, user3, user4, user5, user6, man);

                    Log.d(TAG, "params before " + username + " " + password + " " + nama + " " + nohp + " " + man + woman + " " + tgl + " " + user1 + " " + user2 + " " + user3 + " " + user4 + " " + user5 + " " + user6);


            }
        }
    };

    /*
  * Fungi registerCheck digunakan untuk mendaftarkan pengguna
  * Menggunakan POST method request dan library VOLLEY
  */
    public void registerCheck(final String username, final String password, final String nohp, final String nama, final String tgl, final String user1,
                               final String user2, final String user3, final String user4, final String user5, final String user6, final String sex ){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://10.17.10.210/amann_api/public/api/register",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.v("POST", response);
                            JSONObject get = new JSONObject(response);
                            String resp = get.getString("response");
                            String msg = get.getString("message");

                            if(resp.equals("200")){
                                Log.d(TAG, "hasil " + resp + msg);

                            }
                            else if(resp.equals("500")){
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
                params.put("email_pengguna", username);
                params.put("password_pengguna", password);
                params.put("nama_pengguna", nama);
                params.put("nohp_pengguna", nohp);
                params.put("sex", sex);
                params.put("tgl_lahir", tgl);
                params.put("nohp_helper_1", user1);
                params.put("nohp_helper_2", user2);
                params.put("nohp_helper_3", user3);
                params.put("nohp_helper_4", user4);
                params.put("nohp_helper_5", user5);
                params.put("nohp_helper_6", user6);

                Log.d(TAG, "params " + username + " " + password + " " + nama + " " + nohp + " " + sex + " " + tgl + " " + user1 + " " + user2 + " " + user3 + " " + user4 + " " + user5 + " " + user6);


                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(HelperActivity.this);
        requestQueue.add(stringRequest);
    }


}
