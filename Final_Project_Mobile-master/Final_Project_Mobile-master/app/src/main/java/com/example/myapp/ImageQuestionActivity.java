package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageQuestionActivity extends AppCompatActivity {

    StorageReference storageReference;
    ProgressDialog progressDialog;
    private static final long START_TIME_IN_MILLIES = 300000;
    private TextView mTextViewCountDown;
    private ImageView img_view;
    private Button mButtonStartPause;
    private Button mButtonReset;
    private CountDownTimer mCountDownTimer;
    private boolean mTimmerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIES;
    private TextView timeup;
    private TextView question_name;
    private Button submit;
    private String url_img;
    public boolean check_choice = false;
    public String result_of_each_question = "";
    public int index = 2;
    public boolean check_pause = true;
    public boolean check_time_exist = false;
    private EditText answer_view;
    private String result ="";
    private TextView point;
    private TextView back_to_choice;
    public int score;
    private int id_img = 1;
    public Button rest_api;
    private int point_player = 0;

    public String User_ID;
    public User check_user;
    String text_answer;
    private TextView check_true_false;
    private MediaPlayer true_sound;
    private MediaPlayer false_sound;
    private boolean check_call_get_question_first = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_question);
        Intent mIntent = getIntent();
        int score_intent = mIntent.getIntExtra("score", 0);
        String user_id = mIntent.getStringExtra("user_id");
        User_ID = user_id;
        Log.d(user_id, "onCreate: ");
        score = score_intent;
        setContentView(R.layout.activity_image_question);
        mTextViewCountDown = findViewById(R.id.textView_Time);
        mButtonReset = findViewById(R.id.button_reset);
        mButtonStartPause = findViewById(R.id.button_start_pause);
        timeup = findViewById(R.id.textView_time_up);
        img_view = (ImageView)findViewById(R.id.image_question);
        question_name = (TextView)findViewById(R.id.textView_question);
        submit = (Button)findViewById(R.id.button_submit);
        answer_view = (EditText)findViewById(R.id.answer_view);
        check_true_false = (TextView)findViewById(R.id.check_answer);

        true_sound = MediaPlayer.create(this,R.raw.mixkit);
        false_sound = MediaPlayer.create(this,R.raw.fail_question);


//        question_view.setVisibility(View.INVISIBLE);

        point = (TextView) findViewById(R.id.point);
        back_to_choice = (TextView) findViewById(R.id.back_to_choice);
        back_to_choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ImageQuestionActivity.this, EnglishGameChoiceActivity.class);
                intent.putExtra("score", score);
                Toast.makeText(ImageQuestionActivity.this, "Back to Choice ", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        answer_view.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    // do something, e.g. set your TextView here via .setText()
                    InputMethodManager imm = (InputMethodManager) textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTimmerRunning) {
                    pauseTimer();
                    check_pause = true;
                } else {
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


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text_answer = answer_view.getText().toString().toLowerCase(Locale.ROOT);
                if (text_answer.equals(""))
                {
                    Toast.makeText(ImageQuestionActivity.this,"Please Fill answer",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (text_answer.equals(result.toString()))
                    {
                        //update point

                        String u_id = User_ID;
                        User x = new User();
                        x.point = score + 1;
                        x.userid = u_id;
                        call_api_update_point(u_id,x);

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        check_true_false.setText("✔");
                        true_sound.start();
                        check_true_false.setTextColor(Color.rgb(35,84,20));
                        score++;
                        point_player++;
                        String point_of_score = String.valueOf(point_player);
                        point.setText(point_of_score);
                        id_img++;
                        get_image(id_img);
                        int test = random_num_question_no_repeat();
                        String temp = String.valueOf(test);
                        Log.d(temp, " data");
                    }
                    else
                    {
                        check_true_false.setText("✘");
                        false_sound.start();
                        check_true_false.setTextColor(Color.rgb(165,0,1));
                    }


                }

            }
        });
    }

    private void startTimer()
    {
        if (check_call_get_question_first == false)
        {
            load_question(id_img);
            check_call_get_question_first = true;
        }


        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis,1000) {
            @Override
            public void onTick(long l) {
                check_time_exist = true;
                mTimeLeftInMillis = l;
                updateCountDownText();

            }

            @Override
            public void onFinish() {
                mTimmerRunning = false;
                mButtonStartPause.setText("Start");
                mButtonStartPause.setVisibility(View.INVISIBLE);
                mButtonReset.setVisibility(View.VISIBLE);
                timeup.setVisibility(View.VISIBLE);
                check_time_exist = false;
                check_call_get_question_first = false;
                img_view.setVisibility(View.INVISIBLE);
            }
        }.start();
        mTimmerRunning = true;
        mButtonStartPause.setText("pause");
        mButtonReset.setVisibility(View.INVISIBLE);



    }


    private  void pauseTimer(){
        mCountDownTimer.cancel();
        mTimmerRunning = false;
        mButtonStartPause.setText("Resume");
        mButtonReset.setVisibility(View.VISIBLE);
    }

    private void resetTimer()
    {
        mTimeLeftInMillis = START_TIME_IN_MILLIES;
        updateCountDownText();
        mButtonReset.setVisibility(View.INVISIBLE);
        mButtonStartPause.setVisibility(View.VISIBLE);
        timeup.setVisibility(View.INVISIBLE);

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
       get_image(id);
    }

    private class LoadImage extends AsyncTask<String,Void,Bitmap> {
        ImageView imageView;
        public LoadImage(ImageView imageResult)
        {
            this.imageView = imageResult;
        }


        @Override
        protected Bitmap doInBackground(String... strings) {
            String  urllink = strings[0];
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new java.net.URL(urllink).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }


    private void get_image(int id)
    {
        ApiService.apiService.get_url(id).enqueue(new Callback<IMG>() {
            @Override
            public void onResponse(Call<IMG> call, Response<IMG> response) {

                IMG x = response.body();
                question_name.setText(x.img_question.toString());
                url_img = x.img_url.toString();
//                ImageQuestionActivity.LoadImage loadImage = new ImageQuestionActivity.LoadImage(img_view);
//                loadImage.execute(url_img.toString());
                result = x.img_result;
                show_image(result);


            }

            @Override
            public void onFailure(Call<IMG> call, Throwable t) {


            }
        });
    }

    private void show_image(String img_id)
    {
        mCountDownTimer.cancel();
        mTimmerRunning = false;
        check_pause = true;
        check_true_false.setText("");
        answer_view.setText("");
        progressDialog = new ProgressDialog(ImageQuestionActivity.this);
        progressDialog.setMessage("Fetching Question...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String image_id = img_id.toString();
        storageReference = FirebaseStorage.getInstance().getReference("images/"+image_id +".jpg");
        try {
            File localfile = File.createTempFile("tempfile",".jpg");
            storageReference.getFile(localfile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                            Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                            img_view.setImageBitmap(bitmap);
                            startTimer();
                            check_pause = false;
                            mTimmerRunning = true;


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(ImageQuestionActivity.this,"Fail to load image",Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    int random_num_question_no_repeat()
    {
        int size = 20;

        ArrayList<Integer> list = new ArrayList<Integer>(size);
        for(int i = 1; i <= size; i++) {
            list.add(i);
        }

        Random rand = new Random();
        while(list.size() > 0) {
            int index = rand.nextInt(list.size());
            System.out.println("Selected: "+list.remove(index));

        }
        return index;
    }



}
