package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;


public class EnglishGameChoiceActivity extends AppCompatActivity {
    private  static final int MY_REQUEST_CODE = 100;
    public TextView score_of_acc;
    private TextView back_to_homescreen;
    Button start_pronunciation;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_english_choice_question);



        score_of_acc = (TextView)findViewById(R.id.score);

        Intent mIntent = getIntent();
        String user_id = mIntent.getStringExtra("user_id");
        Log.d(user_id, "UserID: ");
        int score_intent = mIntent.getIntExtra("score", 0);
        int score = score_intent;
        String score_updated =String.valueOf(score);
        score_of_acc.setText(score_updated);

        Button vocab_start = (Button) findViewById(R.id.start_voca);
        back_to_homescreen = (TextView) findViewById(R.id.back_but);
        
        vocab_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EnglishGameChoiceActivity.this, ImageQuestionActivity.class);
                i.putExtra("score",score);
                i.putExtra("user_id",user_id);
                startActivityForResult(i,MY_REQUEST_CODE);

            }
        });
        
        Button grammar_start = (Button)findViewById(R.id.start_grammar);
        grammar_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EnglishGameChoiceActivity.this, VocabularyActivity.class);
                i.putExtra("score",score);
                i.putExtra("user_id",user_id);
                startActivityForResult(i,MY_REQUEST_CODE);
            }
        });

        start_pronunciation = (Button)findViewById(R.id.start_pronunciation);
        start_pronunciation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EnglishGameChoiceActivity.this, PronunciationActivity.class);
                i.putExtra("score",score);
                i.putExtra("user_id",user_id);
                startActivityForResult(i,MY_REQUEST_CODE);
            }
        });

        back_to_homescreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String score_new = score_of_acc.getText().toString();
                Integer value_of_score = Integer.valueOf(score_new);
                Intent intent = new Intent(EnglishGameChoiceActivity.this, HomeScreenActivity.class);
                intent.putExtra("score",value_of_score);
                Toast.makeText(EnglishGameChoiceActivity.this," ",Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK,intent);
                finish();
            }
        });


    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (MY_REQUEST_CODE == requestCode && RESULT_OK == resultCode)
        {
//            String name_res = data.getStringExtra("NameofUser");
//            String pass_res = data.getStringExtra("PasswordofUser");
//            Log.d("test",name_res);
//            Log.d("test pass",pass_res);
//            person res = new person(a.number_account,name_res,pass_res);
//            boolean check = a.add_account(res);
            int score_intent = data.getIntExtra("score", 0);

            String score_updated = String.valueOf(score_intent);
            score_of_acc.setText(score_updated);

        }
    }




}
