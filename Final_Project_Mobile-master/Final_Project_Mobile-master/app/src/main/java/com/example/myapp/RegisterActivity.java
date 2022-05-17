package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapp.module.BodyRegister;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText email;
    EditText uname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        String[] stringArray = getIntent().getStringArrayExtra("key");
        int number_acc = getIntent().getIntExtra("number", 0);
        Button register_butt = (Button) findViewById(R.id.bt_register);

        uname = (EditText) findViewById(R.id.uname_register);
        EditText password = (EditText) findViewById(R.id.password_regis);
        EditText confirm_password = (EditText) findViewById(R.id.confirm_password_regis);
        email = (EditText)findViewById(R.id.email_text);

        register_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String UserName = uname.getText().toString();
                String PassWord = password.getText().toString();
                String ConfirmPassWord = confirm_password.getText().toString();
                String Email = email.getText().toString();

                Log.d("username",UserName);
                Log.d("password",PassWord);
                Log.d("email",Email);
                if (PassWord.equals(ConfirmPassWord))
                {


                        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                        intent.putExtra("username",UserName);
                        intent.putExtra("password",PassWord);
                        call_api_register(uname.getText().toString(),PassWord,Email);
                        Toast.makeText(RegisterActivity.this,"Register Successful ",Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK,intent);
                        finish();
                }
                else
                {
                        Toast.makeText(RegisterActivity.this,"Password and Confirm Password is not match ",Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button back_to_login = (Button) findViewById(R.id.button_back);
        back_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }

    public  boolean check_username(String [] a,String name,int length) {
        boolean check = true;
        for (int i = 0;i < length;i++) {
            if (a[i].equals(name))
            {
                check = false;
            }
        }
        return check;
    }

    private void call_api_register(String username,String password,String email)
    {
        BodyRegister User = new BodyRegister(username,password,email);
        ApiService.apiService.RegisterUser(User).enqueue(new Callback<com.example.myapp.User>() {
            @Override
            public void onResponse(Call<com.example.myapp.User> call, Response<com.example.myapp.User> response) {
              //  Toast.makeText(RegisterActivity.this,"call api successful",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<com.example.myapp.User> call, Throwable t) {
             //   Toast.makeText(RegisterActivity.this,"call api unsuccessful",Toast.LENGTH_SHORT).show();
            }
        });
    }









}