package com.bca.deepthinker.bca;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import IdHelpers.IdHelper;
import de.hdodenhof.circleimageview.CircleImageView;



public class   success extends AppCompatActivity {

    List<Category_List> category;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView postList;
    private Toolbar mtoolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth mAuth;
    private Menu menu;
    private CircleImageView profileimage;
    private TextView username;


    private FirebaseFirestore mdb;
    FirebaseStorage storage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        mAuth = FirebaseAuth.getInstance();
        category=new ArrayList<>();
        category.add(new Category_List("IT","Description",R.drawable.itpic));
        category.add(new Category_List("Plumbers","Description",R.drawable.plumberspic));
        category.add(new Category_List("Electricians","Description",R.drawable.electricianpic));
        category.add(new Category_List("Clothes","Description",R.drawable.clothespic));
        category.add(new Category_List("Hair Saloon","Description",R.drawable.hairsaloonpic));


        RecyclerView myrv=(RecyclerView)findViewById(R.id.all_users_post_list);
        RecyclerViewAdapter myAdapter= new RecyclerViewAdapter(this,category);
        myrv.setLayoutManager(new GridLayoutManager(this,2));
        myrv.setAdapter(myAdapter);




        mtoolbar= findViewById(R.id.main_page_toolbar);

        setSupportActionBar(mtoolbar);
        if (IdHelper.Seller){
            getSupportActionBar().setTitle("Seller");
        }else {
            getSupportActionBar().setTitle("Buyer");
        }

        drawerLayout= findViewById(R.id.drawable_Layout);
        actionBarDrawerToggle=new ActionBarDrawerToggle(success.this,drawerLayout, R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView= findViewById(R.id.navigation_View);

        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);

        profileimage=navView.findViewById(R.id.nav_profile_show);
        username=navView.findViewById(R.id.usernameshow);

        mdb= FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance("gs://fir-bca.appspot.com");

        menu=navigationView.getMenu();
        MenuItem nav_seller = menu.findItem(R.id.nav_seller);

        if (IdHelper.Seller){

            nav_seller.setTitle("Buyer");

        }else {
            nav_seller.setTitle("Seller");

        }



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                UserMenuSelector(item);
                return false;
            }
        });


        mdb.collection("Users_profile").document(IdHelper.Email).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc = task.getResult();

                            username.setText((CharSequence) doc.get("FullName"));


                    }
                });


        StorageReference storageRef = storage.getReference();
        StorageReference islandRef = storageRef.child("Profile/"+IdHelper.Email+".jpg");


        final long ONE_MEGABYTE = (1024 * 1024)*5;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profileimage.setImageBitmap(Bitmap.createScaledBitmap(bmp, profileimage.getWidth(),
                        profileimage.getHeight(), false));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });



    }




    private void SendUserToLoginActivity()
    {
        Intent loginIntent= new Intent(success.this,Login_form.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
        public void onBackPressed()
    {
     final AlertDialog.Builder builder=new AlertDialog.Builder(success.this);
     builder.setMessage("Are you sure want to do this?");
     builder.setCancelable(true);
     builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialogInterface, int which) {
             dialogInterface.cancel();
         }
     });
     builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
         @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
         @Override
         public void onClick(DialogInterface dialog, int which) {
            finishAffinity();

         }
     });
     AlertDialog alertDialog=builder.create();
     alertDialog.show();
    }
        private void UserMenuSelector(MenuItem item){

        switch (item.getItemId())
        {


            case R.id.nav_profile:
                Intent i=new Intent(success.this,UserProfile.class);
                startActivity(i);
                break;

            case R.id.nav_message:
                if (IdHelper.Seller){


                    Intent messadeIntent = new Intent(success.this, MessageListActivity.class);
                    messadeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(messadeIntent);

                }else {

                    Intent messadeIntent = new Intent(success.this,  MessageListActivity.class);
                    messadeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(messadeIntent);

                }
                break;

            case R.id.nav_seller:
                if (IdHelper.Seller){

                    IdHelper.Seller=false;
                    Intent mainIntent = new Intent(success.this, success.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish();
                }else {
                    IdHelper.Seller=true;
                    Intent mainIntent = new Intent(success.this, success.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish();
                }
                break;

            case R.id.nav_visitwebsite:
                Toast.makeText(this,"Visit Our Website", Toast.LENGTH_SHORT).show();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.twitter.com/RaoHAli"));
                startActivity(browserIntent);
                break;

            case R.id.nav_logout:
                mAuth.signOut();
                SendUserToLoginActivity();

                break;

            case R.id.nav_exit:

                Toast.makeText(this,"Exit", Toast.LENGTH_SHORT).show();
                break;
        }

        }



}
