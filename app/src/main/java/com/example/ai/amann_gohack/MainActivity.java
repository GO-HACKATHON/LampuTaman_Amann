package com.example.ai.amann_gohack;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    //Variable declaration
    private static final String TAG = "MainActivity";
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
        btnRegister.setOnClickListener(buttonOperation);

    }
}
