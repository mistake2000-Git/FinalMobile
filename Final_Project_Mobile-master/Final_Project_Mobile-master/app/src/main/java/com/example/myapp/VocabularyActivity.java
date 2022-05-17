package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.*;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VocabularyActivity extends AppCompatActivity {

    private static final long START_TIME_IN_MILLIES = 60000;
    private TextView mTextViewCountDown;

    private Button mButtonStartPause;
    private Button mButtonReset;
    private CountDownTimer mCountDownTimer;
    private boolean mTimmerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIES;
    private TextView timeup ;
    private TextView question_view;
    private TextView answerA;
    private TextView answerB;
    private TextView answerC;
    private TextView answerD;
    public boolean check_choice = false;
    public String result_of_each_question = "";
    public int index = 2;
    public boolean check_pause = true;
    public boolean check_time_exist = false;
    public TextView iconcheckA;
    public TextView iconcheckB;
    public TextView iconcheckC;
    public TextView iconcheckD;
    private TextView point;
    private TextView back_to_choice;
    public int score;
    private TextView music;
    private TextView pause_music;
    public Button rest_api;

    public String User_ID;
    public User check_user;
    private boolean check_music = true;
    private MediaPlayer mediaPlayer;
    private MediaPlayer true_sound;
    private MediaPlayer false_sound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent mIntent = getIntent();
        int score_intent = mIntent.getIntExtra("score", 0);
        String user_id = mIntent.getStringExtra("user_id");
        User_ID = user_id;
        Log.d(user_id, "onCreate: ");
        score = score_intent;
        setContentView(R.layout.activity_vocab_activities);
        mTextViewCountDown = findViewById(R.id.textView_Time);
        mButtonReset = findViewById(R.id.button_reset);
        mButtonStartPause = findViewById(R.id.button_start_pause);
        timeup = findViewById(R.id.textView_time_up);
        answerA = findViewById(R.id.text_answer_A);
        answerB = findViewById(R.id.text_answer_B);
        answerC = findViewById(R.id.text_answer_C);
        answerD = findViewById(R.id.text_answer_D);
        question_view = findViewById(R.id.textView_question);
        iconcheckA = findViewById(R.id.check_true_or_false_A);
        iconcheckB = findViewById(R.id.check_true_or_false_B);
        iconcheckC = findViewById(R.id.check_true_or_false_C);
        iconcheckD = findViewById(R.id.check_true_or_false_D);
        music = findViewById(R.id.listen_music);
        pause_music = findViewById(R.id.not_listen_music);
//        question_view.setVisibility(View.INVISIBLE);

        point = (TextView) findViewById(R.id.point);
        back_to_choice = (TextView)findViewById(R.id.back_to_choice);



        mediaPlayer = MediaPlayer.create(this,R.raw.music_app);
        mediaPlayer.start();
        true_sound = MediaPlayer.create(this,R.raw.mixkit);
        false_sound = MediaPlayer.create(this,R.raw.fail_question);

        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check_music == true)
                {
                    pause_music.setVisibility(View.VISIBLE);
                    check_music = false;
                    mediaPlayer.pause();
                }
                else
                {
                    mediaPlayer.start();
                    check_music = true;
                    pause_music.setVisibility(View.INVISIBLE);
                }
            }
        });



    back_to_choice.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(VocabularyActivity.this, EnglishGameChoiceActivity.class);
            intent.putExtra("score",score);
            Toast.makeText(VocabularyActivity.this,"Back to Choice ",Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK,intent);
            check_music = false;
            mediaPlayer.pause();
            finish();
        }
    });



        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTimmerRunning){
                    pauseTimer();
                    check_pause = true;
                }
                else
                {
                    startTimer();
                    check_pause = false;
                }
            }
        });

        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTimer();
                check_pause = true;
            }
        });
    }


    private void startTimer()
    {


        load_question(index);
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis,1000) {
            @Override
            public void onTick(long l) {
                check_time_exist = true;
                mTimeLeftInMillis = l;
                updateCountDownText();
                answerA.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        if (check_pause == true)
                        {
                            Toast.makeText(VocabularyActivity.this,"please press continue",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            if ( check_time_exist == true)
                            {
                                if (answerA.getText().toString().equals(result_of_each_question))
                                {
                                    //update point backend
                                    String u_id = User_ID;
                                    User x = new User();
                                    x.point = score + 1;
                                    x.userid = u_id;
                                    call_api_update_point(u_id,x);

                                    iconcheckA.setText("✔");
                                    true_sound.start();
                                    iconcheckA.setTextColor(Color.rgb(34,242,123));
                                    int point_player = Integer.parseInt(point.getText().toString());
                                    int point_temp = point_player + 1;
                                    String temp = String.valueOf(point_temp);
                                    point.setText(temp);
                                    score++;

                                }
                                else
                                {

                                    iconcheckA.setText("✘");
                                    false_sound.start();
                                    iconcheckA.setTextColor(Color.rgb(165,0,1));
                                    if (answerB.getText().toString().equals(result_of_each_question))
                                    {
                                        iconcheckB.setText("✔");
                                        iconcheckB.setTextColor(Color.rgb(34,242,123));


                                    }
                                    else if(answerB.getText().toString().equals(result_of_each_question))
                                    {
                                        iconcheckC.setText("✔");
                                        iconcheckC.setTextColor(Color.rgb(34,242,123));
                                    }
                                    else
                                    {
                                        iconcheckD.setText("✔");
                                        iconcheckD.setTextColor(Color.rgb(34,242,123));
                                    }

                                }

                                final Handler handler = new Handler(Looper.getMainLooper());
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Do something after 100ms
                                        index++;
                                        load_question(index);
                                    }
                                }, 1000);

                            }

                        }




                    }
                });

                answerB.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View view) {
                        if (check_pause == true)
                        {
                            Toast.makeText(VocabularyActivity.this,"please press continue",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            if (check_time_exist == true)
                            {
                                if (answerB.getText().toString().equals(result_of_each_question))
                                {
                                    String u_id = User_ID;
                                    User x = new User();
                                    x.point = score + 1;
                                    x.userid = u_id;
                                    call_api_update_point(u_id,x);
                                    true_sound.start();
                                    iconcheckB.setText("✔");
                                    iconcheckB.setTextColor(Color.rgb(34,242,123));
                                    int point_player = Integer.parseInt(point.getText().toString());
                                    int point_temp = point_player + 1;
                                    String temp = String.valueOf(point_temp);
                                    point.setText(temp);
                                    //call_api_update_point(User_ID,1);
                                    score++;


                                }
                                else
                                {
                                    false_sound.start();
                                    iconcheckB.setText("✘");
                                    iconcheckB.setTextColor(Color.rgb(165,0,1));

                                    if (answerA.getText().toString().equals(result_of_each_question))
                                    {
                                        iconcheckA.setText("✔");
                                        iconcheckA.setTextColor(Color.rgb(34,242,123));
                                    }
                                    else if(answerC.getText().toString().equals(result_of_each_question))
                                    {
                                        iconcheckC.setText("✔");
                                        iconcheckC.setTextColor(Color.rgb(34,242,123));
                                    }
                                    else
                                    {
                                        iconcheckD.setText("✔");
                                        iconcheckD.setTextColor(Color.rgb(34,242,123));
                                    }
                                }
                                final Handler handler = new Handler(Looper.getMainLooper());
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Do something after 100ms
                                        index++;
                                        load_question(index);
                                    }
                                }, 1000);
                            }

                        }


                    }
                });

                answerC.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View view) {
                        if (check_pause == true)
                        {
                            Toast.makeText(VocabularyActivity.this,"please press continue",Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            if (check_time_exist == true)
                            {
                                if (answerC.getText().toString().equals(result_of_each_question))
                                {
                                    String u_id = User_ID;
                                    User x = new User();
                                    x.point = score + 1;
                                    x.userid = u_id;
                                    call_api_update_point(u_id,x);
                                    true_sound.start();
                                    iconcheckC.setText("✔");
                                    iconcheckC.setTextColor(Color.rgb(34,242,123));
                                    int point_player = Integer.parseInt(point.getText().toString());
                                    int point_temp = point_player + 1;
                                    String temp = String.valueOf(point_temp);
                                    point.setText(temp);
                                    score++;




                                }
                                else
                                {
                                    false_sound.start();
                                    iconcheckC.setText("✘");
                                    iconcheckC.setTextColor(Color.rgb(165,0,1));

                                    if (answerA.getText().toString().equals(result_of_each_question))
                                    {
                                        iconcheckA.setText("✔");
                                        iconcheckA.setTextColor(Color.rgb(34,242,123));
                                    }
                                    else if(answerB.getText().toString().equals(result_of_each_question))
                                    {
                                        iconcheckB.setText("✔");
                                        iconcheckB.setTextColor(Color.rgb(34,242,123));
                                    }
                                    else
                                    {
                                        iconcheckD.setText("✔");
                                        iconcheckD.setTextColor(Color.rgb(34,242,123));
                                    }

                                }
                                final Handler handler = new Handler(Looper.getMainLooper());
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Do something after 100ms
                                        index++;
                                        load_question(index);
                                    }
                                }, 1000);
                            }

                        }



                    }
                });

                answerD.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View view) {
                        if (check_pause == true)
                        {
                            Toast.makeText(VocabularyActivity.this,"please press continue",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if (check_time_exist == true)
                            {
                                if (answerD.getText().toString().equals(result_of_each_question))
                                {
                                    true_sound.start();
                                    //update point back end
                                    String u_id = User_ID;
                                    User x = new User();
                                    x.point = score + 1;
                                    x.userid = u_id;
                                    call_api_update_point(u_id,x);

                                    iconcheckD.setText("✔");
                                    iconcheckD.setTextColor(Color.rgb(34,242,123));
                                    int point_player = Integer.parseInt(point.getText().toString());
                                    int point_temp = point_player + 1;
                                    String temp = String.valueOf(point_temp);
                                    point.setText(temp);
                                    score++;



                                }
                                else
                                {
                                    false_sound.start();
                                    iconcheckD.setText("✘");
                                    iconcheckD.setTextColor(Color.rgb(165,0,1));

                                    if (answerA.getText().toString().equals(result_of_each_question))
                                    {
                                        iconcheckA.setText("✔");
                                        iconcheckA.setTextColor(Color.rgb(34,242,123));
                                    }
                                    else if(answerB.getText().toString().equals(result_of_each_question))
                                    {
                                        iconcheckB.setText("✔");
                                        iconcheckB.setTextColor(Color.rgb(34,242,123));
                                    }
                                    else
                                    {
                                        iconcheckC.setText("✔");
                                        iconcheckC.setTextColor(Color.rgb(34,242,123));
                                    }

                                }

                                final Handler handler = new Handler(Looper.getMainLooper());
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Do something after 100ms
                                        index++;
                                        load_question(index);
                                    }
                                }, 1000);
                            }
                        }



                    }
                });


            }

            @Override
            public void onFinish() {
                mTimmerRunning = false;
                mButtonStartPause.setText("Start");
                mButtonStartPause.setVisibility(View.INVISIBLE);
                mButtonReset.setVisibility(View.VISIBLE);
                timeup.setVisibility(View.VISIBLE);
                check_time_exist = false;
            }
        }.start();
        mTimmerRunning = true;
        mButtonStartPause.setText("pause");
        mButtonReset.setVisibility(View.INVISIBLE);



    }


    private  void pauseTimer(){
        mCountDownTimer.cancel();
        mTimmerRunning = false;
        mButtonStartPause.setText("Start");
        mButtonReset.setVisibility(View.VISIBLE);
    }

    private void resetTimer()
    {
        mTimeLeftInMillis = START_TIME_IN_MILLIES;
        updateCountDownText();
        mButtonReset.setVisibility(View.INVISIBLE);
        mButtonStartPause.setVisibility(View.VISIBLE);
        timeup.setVisibility(View.INVISIBLE);
        question_view.setText("");
        answerA.setText("");
        answerB.setText("");
        answerC.setText("");
        answerD.setText("");
        index = 1;
        iconcheckA.setText("");
        iconcheckB.setText("");
        iconcheckC.setText("");
        iconcheckD.setText("");
//        load_question(temp.list_q[0]);
    }

    private void updateCountDownText()
    {
        int minutes = (int) mTimeLeftInMillis / 1000 / 60;
        int second = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d",minutes,second);
        mTextViewCountDown.setText(timeLeftFormatted);
    }

    public void load_question(int id)
    {
//        iconcheckA.setText("");
//        iconcheckB.setText("");
//        iconcheckC.setText("");
//        iconcheckD.setText("");
//        question_view.setText(a.Question);
//        answerA.setText(a.answerA);
//        answerB.setText(a.answerB);
//        answerC.setText(a.answerC);
//        answerD.setText(a.answerD);
//        result_of_each_question =  a.result;
//        check_choice = false;
          clickCallApi(id);
    }

    private void clickCallApi(int id) {

        ApiService.apiService.get_question(id).enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call, Response<Question> response) {
             //   Toast.makeText(VocabularyActivity.this,"call api successful",Toast.LENGTH_SHORT).show();
                Question test = response.body();
                question_view.setText(test.question);
                iconcheckA.setText("");
                iconcheckB.setText("");
                iconcheckC.setText("");
                iconcheckD.setText("");
                question_view.setText(test.question);
                answerA.setText(test.answerA);
                answerB.setText(test.answerB);
                answerC.setText(test.answerC);
                answerD.setText(test.answerD);
                result_of_each_question =  test.result;
                check_choice = false;
            }

            @Override
            public void onFailure(Call<Question> call, Throwable t) {
             //   Toast.makeText(VocabularyActivity.this,"call api failure",Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void call_api_update_point(String userid,int point)
//    {
//        ApiService.apiService.updatePoint(userid,point).enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Call<User> call, Response<User> response) {
//                Toast.makeText(VocabularyActivity.this,"call api Successful",Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailure(Call<User> call, Throwable t) {
//                Toast.makeText(VocabularyActivity.this,"call api Unsuccessful",Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void clickCallApi_user(String id) {

        ApiService.apiService.get_user(id).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
              //  Toast.makeText(VocabularyActivity.this,"call api Unsuccessful",Toast.LENGTH_SHORT).show();
                check_user = response.body();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
             //   Toast.makeText(VocabularyActivity.this,"call api Unsuccessful",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void call_api_update_point(String User_ID, User account)
    {
        ApiService.apiService.updatePoint(User_ID,account).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
             //   Toast.makeText(VocabularyActivity.this,"call api Successful",Toast.LENGTH_SHORT).show();
                User test = response.body();
                score = test.point;

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
              //  Toast.makeText(VocabularyActivity.this,"call api Unsuccessful",Toast.LENGTH_SHORT).show();
            }
        });
    }



}

