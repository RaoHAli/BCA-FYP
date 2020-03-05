package com.bca.deepthinker.bca;

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
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class ResetPassword extends AppCompatActivity {

    private Button ResetPasswordSendEmailButton;
    private EditText ResetPasswordEmail;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth=FirebaseAuth.getInstance();



    ResetPasswordSendEmailButton= findViewById(R.id.btnsendemail);
    ResetPasswordEmail= findViewById(R.id.EmailForSend);

    ResetPasswordSendEmailButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String UserEmail=ResetPasswordEmail.getText().toString();
            if(TextUtils.isEmpty(UserEmail)){
                Toast.makeText(ResetPassword.this,"Please Write Your Valid Email Address...", Toast.LENGTH_SHORT).show();
            }
            else{
                mAuth.sendPasswordResetEmail(UserEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            Toast.makeText(ResetPassword.this,"Please Check Your Valid Email Account...", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ResetPassword.this, Signin_form.class));
                        }
                        else
                        {
                            String message= task.getException().getMessage();
                            Toast.makeText(ResetPassword.this,"Error Occurred" + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    });

    }
}
