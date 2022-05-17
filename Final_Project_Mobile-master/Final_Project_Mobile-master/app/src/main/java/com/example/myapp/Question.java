package com.example.myapp;
import  com.google.gson.annotations.SerializedName;

public class Question {
    @SerializedName("question_name")
    public String question;

    public int question_id;

    public String answerA;
    public String answerB;
    public String answerC;
    public String answerD;

    public String result;

    public String True_choice()
    {
        if (this.answerA.equals(result))
        {
            return "A";
        }
        else if (this.answerB.equals(result))
        {
            return  "B";
        }
        else if (this.answerC.equals(result))
        {
            return "C";
        }
        else
        {
            return "D";
        }
    }

}
