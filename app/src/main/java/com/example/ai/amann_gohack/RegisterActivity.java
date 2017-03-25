package com.example.ai.amann_gohack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    EditText etEmail, etPassword, etNohp, etNama, etTgl;
    private String man, woman;
    Integer flag = 0;
    Button btnNext;



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

        btnNext.setOnClickListener(buttonOperation);



    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rbMan:
                if (checked)
                    man = "Male";
                    flag = 1;
                    break;
            case R.id.rbWoman:
                if (checked)
                    woman = "Female";
                    flag = 2;
                    break;
        }
    }

    // Command ketika button di tekan
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
                    if (flag == 1) helperIntent.putExtra("sex", man);
                    else if(flag == 2) helperIntent.putExtra("sex", woman);
                    Log.d(TAG, "gender" + man + woman);
                    startActivity(helperIntent);


            }
        }
    };
}
