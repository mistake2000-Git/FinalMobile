package com.example.myapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.mannan.translateapi.Language;
import com.mannan.translateapi.TranslateAPI;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PronunciationActivity extends AppCompatActivity {

    private String User_ID;
    private int score;
    private TextView point;
    private TextView back_to_choice;
    private TextView question_text;
    private Button call_ques;
    private ImageView speaker;
    TextToSpeech textToSpeech;
    protected static final int RESULT_SPEECH = 1;
    ImageView mic;
    TextView result_text;
    VoiceQuestion VoiceQuestion;
    Pronun pronun;
    TextView pronun_text;
    Button call_pronun_but;
    Button translate_but;
    TextView translate_result;
    int voice_id =1000;
    TextView check_answer;
    Button btn_check;
    Boolean check_answer_already = false;

    private MediaPlayer true_sound;
    private MediaPlayer false_sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pronunciation);
        Intent mIntent = getIntent();
        int score_intent = mIntent.getIntExtra("score", 0);
        String user_id = mIntent.getStringExtra("user_id");
        User_ID = user_id;
        Log.d(user_id, "onCreate: ");
        score = score_intent;

        point = (TextView) findViewById(R.id.point);
        String temp = String.valueOf(score);
        point.setText(temp);
        question_text = (TextView) findViewById(R.id.question_text);
        back_to_choice = (TextView)findViewById(R.id.back_menu);
        speaker = (ImageView)findViewById(R.id.button_speak);
        call_ques = (Button)findViewById(R.id.call_question);
        mic = (ImageView)findViewById(R.id.micro_phone);
        result_text = (TextView)findViewById(R.id.result_voice);
        pronun_text = (TextView)findViewById(R.id.pronun_textview);
        translate_result = (TextView)findViewById(R.id.vietnam_translate);
        check_answer = (TextView)findViewById(R.id.check_true_or_false);

        check_answer.setVisibility(View.INVISIBLE);
        result_text.setText("");
        voice_id = getRandomNumber();
        call_voice_question(voice_id);



        true_sound = MediaPlayer.create(this,R.raw.mixkit);
        false_sound = MediaPlayer.create(this,R.raw.fail_question);





        back_to_choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PronunciationActivity.this, EnglishGameChoiceActivity.class);
                intent.putExtra("score",score);
                Toast.makeText(PronunciationActivity.this,"Back to Choice ",Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK,intent);
                finish();
            }
        });

//        question_text = (TextView) findViewById(R.id.textView_question);

        call_ques.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_answer.setVisibility(View.INVISIBLE);
                result_text.setText("");
                voice_id = getRandomNumber();
                call_voice_question(voice_id);
                check_answer_already = false;
            }
        });

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS)
                {
                    int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });

        speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = question_text.getText().toString();
                int speech =  textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH,null);
            }
        });

        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_answer.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en-US");
                try {
                    startActivityForResult(intent,RESULT_SPEECH);
                    result_text.setText("");

                }
                catch (ActivityNotFoundException e)
                {
                    Toast.makeText(getApplicationContext(),"ur device doesn't support speech",Toast.LENGTH_SHORT);
                    e.printStackTrace();
                }


            }

    });
    }

    private void get_image(int id)
    {
        ApiService.apiService.get_url(id).enqueue(new Callback<IMG>() {
            @Override
            public void onResponse(Call<IMG> call, Response<IMG> response) {
//                Toast.makeText(PronunciationActivity.this,"call api Successful",Toast.LENGTH_SHORT).show();
                IMG x = response.body();
//                question_name.setText(x.img_question.toString());
//                url_img = x.img_url.toString();
////                ImageQuestionActivity.LoadImage loadImage = new ImageQuestionActivity.LoadImage(img_view);
////                loadImage.execute(url_img.toString());
//                result = x.img_result;
//                show_image(result);
                question_text.setText(x.img_result.toString());


            }

            @Override
            public void onFailure(Call<IMG> call, Throwable t) {
//                Toast.makeText(PronunciationActivity.this,"call api Unsuccessful",Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case RESULT_SPEECH:
                if (resultCode == RESULT_OK && data != null)
                {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    result_text.setText(text.get(0));
                }
                break;
        }

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                check_answer.setVisibility(View.VISIBLE);
                setCheck_answer();
            }
        }, 1000);

    }

    protected  void call_voice_question(int id)
    {
        ApiService.apiService.get_voice(id).enqueue(new Callback<VoiceQuestion>() {
            @Override
            public void onResponse(Call<VoiceQuestion> call, Response<VoiceQuestion> response) {
//                Toast.makeText(PronunciationActivity.this,"call api Successful",Toast.LENGTH_SHORT).show();
                VoiceQuestion = response.body();
                question_text.setText(VoiceQuestion.voice_result.toString());
                call_pronun(question_text.getText().toString());
                TranslateAPI translateAPI = new TranslateAPI(
                        Language.ENGLISH,
                        Language.VIETNAMESE,
                        question_text.getText().toString()
                );
                translateAPI.setTranslateListener(new TranslateAPI.TranslateListener() {
                    @Override
                    public void onSuccess(String translatedText) {
                        translate_result.setText(translatedText.toString());
                    }

                    @Override
                    public void onFailure(String ErrorText) {
                        Toast.makeText(PronunciationActivity.this,"fail",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<VoiceQuestion> call, Throwable t) {
//                Toast.makeText(PronunciationActivity.this,"call api Unsuccessful",Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void call_pronun(String word)
    {
        ApiService.apiService.get_pronun(word).enqueue(new Callback<Pronun>() {
            @Override
            public void onResponse(Call<Pronun> call, Response<Pronun> response) {
             //   Toast.makeText(PronunciationActivity.this,"call api Successful",Toast.LENGTH_SHORT).show();
                pronun = response.body();
                pronun_text.setText(pronun.all.toString());
            }

            @Override
            public void onFailure(Call<Pronun> call, Throwable t) {
            //    Toast.makeText(PronunciationActivity.this,"call api Unsuccessful",Toast.LENGTH_SHORT).show();
            }
        });
    }

    void setCheck_answer()
    {
        String text_answer = result_text.getText().toString().toLowerCase(Locale.ROOT);
        if (text_answer.equals(question_text.getText()) && check_answer_already == false)
        {
            String u_id = User_ID;
            User x = new User();
            x.point = score + 1;
            x.userid = u_id;
            call_api_update_point(User_ID,x);
            check_answer_already = true;
            String temp_point = String.valueOf(x.point);
            point.setText(temp_point);
            true_sound.start();
            check_answer.setText("✔");
            check_answer.setTextColor(Color.rgb(35,84,20));
        }
        else
        {
            false_sound.start();
            check_answer.setText("✘");
            check_answer.setTextColor(Color.rgb(165,0,1));
        }
    }


    public static int returnRandom(ArrayList al)
    {
        double x = Math.random();
        x = x * 4000;

        if (al.contains(x))
        {
            returnRandom(al);
        }

        else {
            return (int) x;
        }
        return 0;
    }

     int  getRandomNumber()
    {

        ArrayList al = new ArrayList();
        int retu = 0;
        for (int i = 0; i < 1; i++)
        {
            retu = returnRandom(al);
            System.out.println(retu);
        }

        return retu;
    }

    private void call_api_update_point(String User_ID, User account)
    {
        ApiService.apiService.updatePoint(User_ID,account).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                User test = response.body();
                score = test.point;

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

}