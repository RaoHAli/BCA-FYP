package com.bca.deepthinker.bca;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;

import IdHelpers.CustomAdapter;
import IdHelpers.CustomAdapter2;
import IdHelpers.IdHelper;
import IdHelpers.IdsChatHelper;

public class ChatActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {
    ListView view;
    private CustomAdapter2 CA;
    private ArrayList<String> name = new ArrayList<String>();



    private ArrayList<String> mail = new ArrayList<String>();
    private ArrayList<String> ids = new ArrayList<String>();
    private ArrayList<String> Chat_ids = new ArrayList<String>();

    private MediaPlayer mMediaplayer;
    private boolean play=true;


    String url;
    String service[],city;


    private FirebaseFirestore mdb;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        view = (ListView) findViewById(R.id.chatlist);
        mdb= FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance("gs://fir-bca.appspot.com");


        if (IdHelper.Seller) {
            if (IdsChatHelper.id == 0) {
                name.add(IdsChatHelper.name);
                StorageReference storageRef = storage.getReference();
                StorageReference riversRef = storageRef.child(IdsChatHelper.city).child(IdsChatHelper.service).child(IdsChatHelper.mail).child(IdsChatHelper.ids + ".3gp");

                riversRef.getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                url = uri.toString();
                                ShowChat();
                            }
                        })

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                ShowToast("Error in Download " + exception);

                            }
                        });

            }else {


                mdb.collection(IdsChatHelper.mail+IdHelper.Email)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.get("Email").equals(IdHelper.Email))
                            ids.add(String.valueOf(document.get("ID")));
                            name.add(IdsChatHelper.name);

                        }
                        Playv();




                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {




                    }
                });


            }
        }else {
            if (IdsChatHelper.id == 0) {
                name.add(IdsChatHelper.name);
                StorageReference storageRef = storage.getReference();
                StorageReference riversRef = storageRef.child("Buyer").child(IdsChatHelper.mail).child(IdsChatHelper.ids+".3gp");

                riversRef.getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                url = uri.toString();
                                ShowChat();
                            }
                        })

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                ShowToast("Error in Download " + exception);

                            }
                        });
            }else {

                mdb.collection(IdHelper.Email+IdsChatHelper.mail)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (IdsChatHelper.mail.equals(String.valueOf(document.get("Email")))){
                                ids.add(String.valueOf(document.get("ID")));
                                name.add(IdsChatHelper.name);
                            }
                        }


                        Playv();





                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {




                    }
                });


            }
        }






    }

    private void Playv(){
        if (!(play)) {
            mMediaplayer.release();
            mMediaplayer=null;
            play=true;
        }
        final StorageReference storageRef = storage.getReference();

        CA=new CustomAdapter2(name,this);
        view.setAdapter(CA);
        view.setOnItemClickListener( new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(final AdapterView<?> parent, View v, int position,
                                    long id)
            {

                StorageReference riversRef = null;
if (IdHelper.Seller) {


     riversRef = storageRef.child("Chat").child(IdsChatHelper.mail + IdHelper.Email).child(ids.get(position) + ".3gp");

}else  {
    riversRef = storageRef.child("Chat").child(IdHelper.Email+IdsChatHelper.mail).child(ids.get(position) + ".3gp");

}
                riversRef.getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                url = uri.toString();
                                mMediaplayer = new MediaPlayer();
                                mMediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                                try {
                                    mMediaplayer.setDataSource(url);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                // wait for media player to get prepare
                                mMediaplayer.setOnPreparedListener(ChatActivity.this);
                                mMediaplayer.prepareAsync();
                                play=false;

                            }
                        })

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                ShowToast("Error in Download " + exception);

                            }
                        });





            }
        } );








    }

    private void ShowChat(){
        if (!(play)) {
            mMediaplayer.release();
            mMediaplayer=null;
            play=true;
        }
        CA=new CustomAdapter2(name,this);
        view.setAdapter(CA);
        view.setOnItemClickListener( new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position,
                                    long id)
            {


                mMediaplayer = new MediaPlayer();
                mMediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                try {
                    mMediaplayer.setDataSource(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // wait for media player to get prepare
                mMediaplayer.setOnPreparedListener(ChatActivity.this);
                mMediaplayer.prepareAsync();
                play=false;



            }
        } );
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
    private void ShowToast(String s){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

    public void GotoRecord(View view) {

        if (IdHelper.Seller) {
            Intent intent = new Intent(this, SellerProvideServicesActivity.class);
            // passing data to the book activity
            intent.putExtra("Title", "Record");
            // start the activity
            startActivity(intent);
        }else {
            Intent intent = new Intent(this, BuyerRequesServiceActivity.class);
            // passing data to the book activity
            intent.putExtra("Title", "Record");
            // start the activity
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent newint=new Intent(ChatActivity.this,success.class);
        newint.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(newint);
    }
}
