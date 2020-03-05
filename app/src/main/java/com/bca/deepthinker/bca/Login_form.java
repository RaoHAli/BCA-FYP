package com.bca.deepthinker.bca;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import IdHelpers.IdHelper;

public class Login_form extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_form);
        Button createaccount = findViewById(R.id.createaccount);
        Button signin = findViewById(R.id.signin);
        createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                goToSecondActivity();
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToThirdActivity();
            }
        });
    }
    private void goToSecondActivity(){
        Intent intent= new Intent(this, Signup_form.class);
        startActivity(intent);

    }
    private void goToThirdActivity(){
        Intent intent= new Intent(this, Signin_form.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentuser= mAuth.getCurrentUser();
        if (currentuser!=null)
        {
            Intent i=new Intent(Login_form.this,LoginAs.class);
            IdHelper.Email=currentuser.getEmail();

            i.putExtra("Email",currentuser.getEmail());


            startActivity(i);
            finish();
        }
    }
}