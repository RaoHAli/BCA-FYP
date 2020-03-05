package com.bca.deepthinker.bca;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import IdHelpers.IdHelper;

public class Signin_form extends AppCompatActivity {

    private EditText txtEmailLogin;
    private EditText txtpwd;
    private FirebaseAuth firebaseAuth;
    private TextView forgetpasswordlink;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_form);

        txtEmailLogin=findViewById(R.id.EmailForLogin);
        txtpwd=findViewById(R.id.PasswordForLogin);
        firebaseAuth=FirebaseAuth.getInstance();
        forgetpasswordlink= findViewById(R.id.forgetpasswordlink);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sign In");
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    forgetpasswordlink.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(Signin_form.this,ResetPassword.class));
        }
    });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
        }


    public void btnLogin(View v){
        final ProgressDialog progressDialog= ProgressDialog.show(Signin_form.this, "Please Wait...", "Processing...", true);
        (firebaseAuth.signInWithEmailAndPassword(txtEmailLogin.getText().toString(),txtpwd.getText().toString()))
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                      progressDialog.dismiss();
                     if (task.isSuccessful()){
                        IdHelper.Email=txtEmailLogin.getText().toString();
                          Toast.makeText(Signin_form.this,"Login Successfull", Toast.LENGTH_LONG).show();

                         Intent i=new Intent(Signin_form.this,LoginAs.class);

                         i.putExtra("Email",firebaseAuth.getCurrentUser().getEmail());


                          startActivity(i);
                          finish();
                      }
                      else{
                          Log.e("Error1", task.getException().toString());
                          Toast.makeText(Signin_form.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                     }
                    }
                });


    }
}
