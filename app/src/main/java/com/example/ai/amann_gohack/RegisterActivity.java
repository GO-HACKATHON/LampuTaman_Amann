package com.example.ai.amann_gohack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class RegisterActivity extends AppCompatActivity {
    /*
    * Deklarasi variabel
     */
    private static final String TAG = "RegisterActivity";
    private EditText etEmail, etPassword, etNohp, etNama, etTgl;
    private String man, woman;
    private Integer flag = 0;
    private Button btnNext;
    private RadioGroup radioGroup;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnNext = (Button) findViewById(R.id.btnNext);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etNama = (EditText) findViewById(R.id.etNama);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etNohp = (EditText) findViewById(R.id.etNohp);
        etTgl = (EditText) findViewById(R.id.etTgl);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.rbMan) {
                    flag = 1;
                    man = "Male";
                }
                else if(i == R.id.rbWoman) {
                    flag = 2;
                    woman = "Female";
                }
            }
        });

        btnNext.setOnClickListener(buttonOperation);
    }


    /*
     * Fungsi untuk mengenali button mana yang ditekan oleh user
     * btnNext untuk lanjut ke halaman helper
     */
    private View.OnClickListener buttonOperation = new View.OnClickListener(){
        @Override
        public void onClick (View v) {
            switch (v.getId()){

                case R.id.btnNext:
                    String username = etEmail.getText().toString();
                    String password = etPassword.getText().toString();
                    String nama = etNama.getText().toString();
                    String hp = etNohp.getText().toString();
                    String tgl = etTgl.getText().toString();


                    if (username.equals("") ) {
                        etEmail.setError("Harus diisi!");
                        break;
                    }
                    if (password.equals("") ) {
                        etPassword.setError("Harus diisi!");
                        break;
                    }
                    if (nama.equals("") ) {
                        etNama.setError("Harus diisi!");
                        break;
                    }
                    if (hp.equals("") ) {
                        etNohp.setError("Harus diisi!");
                        break;
                    }
                    if (tgl.equals("")){
                        etTgl.setError("Harus diisi!");
                    }




                    Intent helperIntent = new Intent(RegisterActivity.this, HelperActivity.class);
                    helperIntent.putExtra("username", username);
                    helperIntent.putExtra("password", password);
                    helperIntent.putExtra("nama", nama);
                    helperIntent.putExtra("hp", hp);
                    helperIntent.putExtra("tgl", tgl);
                    if (flag == 1) helperIntent.putExtra("sex", man);
                    else if(flag == 2) helperIntent.putExtra("sex", woman);
                    Log.d(TAG, "gender" + man + woman + username + password + nama + hp + tgl);
                    startActivity(helperIntent);


            }
        }
    };
}
