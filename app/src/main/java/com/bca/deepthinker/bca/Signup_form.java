package com.bca.deepthinker.bca;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import IdHelpers.IdHelper;


public class Signup_form extends AppCompatActivity {

    private EditText UserEmail, UserPassword, UserConfirmPassword;
    private Button Register;
    private ProgressDialog LoadingBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_form);

        mAuth= FirebaseAuth.getInstance();

       Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sign Up");
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        UserEmail= findViewById(R.id.EmailAddress);
        UserPassword= findViewById(R.id.Password);
        UserConfirmPassword= findViewById(R.id.ConfirmPassword);
        Register= findViewById(R.id.Register);
        LoadingBar= new ProgressDialog(this);

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
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

    //protected void onStart(){
      //  super.onStart();
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //if(currentUser!=null)
        //{
          //  SendUserToMainActivity();
        //}
    //}

    private void SendUserToMainActivity(){
        Intent mainIntent= new Intent(Signup_form.this,Login_form.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void CreateNewAccount()
    {
        final String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        String confirmPassword = UserConfirmPassword.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please write your email...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please write your password...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(confirmPassword))
        {
            Toast.makeText(this, "Please confirm your password...", Toast.LENGTH_SHORT).show();
        }
        else if(!password.equals(confirmPassword))
        {
            Toast.makeText(this, "your password do not match with your confirm password...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            LoadingBar.setTitle("Creating New Account");
            LoadingBar.setMessage("Please wait, while we are creating your new Account...");
            LoadingBar.show();
            LoadingBar.setCanceledOnTouchOutside(true);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                IdHelper.Email=email;
                                SendUserToSetupActivity();

                                Toast.makeText(Signup_form.this, "you are authenticated successfully...", Toast.LENGTH_SHORT).show();
                                LoadingBar.dismiss();
                            }
                            else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(Signup_form.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                LoadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void SendUserToSetupActivity()
    {
        Intent setupIntent = new Intent(Signup_form.this, success.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

}
