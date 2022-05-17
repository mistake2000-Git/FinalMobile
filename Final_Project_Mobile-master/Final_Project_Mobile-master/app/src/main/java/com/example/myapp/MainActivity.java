
package com.example.myapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.myapp.module.BodyUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    private  static final int MY_REQUEST_CODE = 100;
    ProgressDialog progressDialog;

    TextView username;
    TextView password;
    Button login_btn;
    Button register_but;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //test oop




        username = (TextView) findViewById(R.id.username);
        password = (TextView) findViewById(R.id.password);
        login_btn = (Button) findViewById(R.id.loginbtn);
        register_but = (Button) findViewById(R.id.button_register);


        register_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                Toast.makeText(MainActivity.this,"Start to Register ",Toast.LENGTH_SHORT).show();
//                startActivity(intent);
                startActivityForResult(intent,MY_REQUEST_CODE);
            }


        });






        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = username.getText().toString();
                String pas = password.getText().toString();
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Verifying...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                click_call_api(name,pas);
            }
        });

    }

    public void openHomePage()
    {


    }















    //register


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (MY_REQUEST_CODE == requestCode && RESULT_OK == resultCode)
        {
            Intent mIntent = getIntent();
            String uName = mIntent.getStringExtra("username");
            String passWord = mIntent.getStringExtra("password");
            username.setText(uName);
            password.setText(passWord);

        }
    }



    private void click_call_api(String username,String password)
    {
        BodyUser x = new BodyUser(username,password);
        ApiService.apiService.loginUser(x).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User test = response.body();
                String user_id = response.body().userid;
                Log.d(user_id, "onResponse: ");
                Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
                intent.putExtra("score",test.point);
                intent.putExtra("user_id",user_id);
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(MainActivity.this,"call api Unsuccessful",Toast.LENGTH_SHORT).show();
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });
    }
}