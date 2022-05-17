package com.example.myapp;

public class User {
   public String userid;
   public String account;
   public String password;
   public String account_type;
   public String email;
   public int point;

   public User(String username, String password, String email)
   {
      this.account = username;
      this.password = password;
      this.email = email;
   }

   public User()
   {

   }
}
