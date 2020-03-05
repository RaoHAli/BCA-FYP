package com.bca.deepthinker.bca;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import IdHelpers.IdHelper;

public class LoginAs extends AppCompatActivity {
    private Button buyerbtn;
    private Button sellerbtn;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_as);

        mAuth = FirebaseAuth.getInstance();
    buyerbtn=(Button)findViewById(R.id.BuyerButton);
    sellerbtn=findViewById(R.id.SellerButton);
    buyerbtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            IdHelper.Seller=false;
            Intent intent=new Intent(LoginAs.this, success.class);
            startActivity(intent);
        }
    });
    sellerbtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            IdHelper.Seller=true;
            Intent intent=new Intent(LoginAs.this, success.class);
            startActivity(intent);
        }
    });

    }

    @Override
    protected void onStart() {
        super.onStart();
//        FirebaseUser currentuser= mAuth.getCurrentUser();
//        if (currentuser==null)
//        {
//            SendUserToLoginActivity();
//        }
    }

    private void SendUserToLoginActivity()
    {
        Intent loginIntent= new Intent(LoginAs.this,Login_form.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

}
