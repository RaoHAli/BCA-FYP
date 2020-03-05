package com.bca.deepthinker.bca;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import IdHelpers.IdHelper;
import IdHelpers.IdsChatHelper;

public class BuyerRequesServiceActivity extends AppCompatActivity {
    private String TitleofService="Nothing";
    TextView tv,tvrs,tvc;



    FirebaseStorage storage;
    private FirebaseFirestore mdb;
    Map<String, String> citydata ;
    Map<String, String> Schat ;
    private String no="0",av="0",name,location;

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;

   // private RecordButton recordButton = null;
    private MediaRecorder recorder = null;
    private boolean mStartRecording = true;

   // private PlayButton   playButton = null;
    private MediaPlayer   player = null;
    private boolean mStartPlaying = true;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }


    private boolean reco=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_reques_service);
        tv=findViewById(R.id.textViewTitle);
        tvrs=findViewById(R.id.textViewRecordinstatus);
        tvc=findViewById(R.id.textViewcity);



        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Request For Services");
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("Title")!= null)
        {
            if(!(bundle.getString("Title").equals("Record")))
            {



            }else {
                tvc.setVisibility(View.INVISIBLE);

                reco=true;
            }

            TitleofService=bundle.getString("Title");
        }

        tv.setText("You Selected "+TitleofService);

        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.3gp";

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        storage = FirebaseStorage.getInstance("gs://fir-bca.appspot.com");
        mdb= FirebaseFirestore.getInstance();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }

    public void Record(View view) {
        onRecord(mStartRecording);
        if (mStartRecording) {
            tvrs.setText("Recording Start");
        } else {
            tvrs.setText("Recording Stoped");
        }
        mStartRecording = !mStartRecording;

    }

    public void Play(View view) {
        onPlay(mStartPlaying);

        mStartPlaying = !mStartPlaying;
    }

    public void SendAudio(View view) {
        StorageReference storageRef = storage.getReference();

        Calendar cal = Calendar.getInstance();
        int second = cal.get(Calendar.SECOND);
        int minute = cal.get(Calendar.MINUTE);
        int hourofday = cal.get(Calendar.HOUR_OF_DAY);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        int mont=cal.get(Calendar.MONTH);
        int year=cal.get(Calendar.YEAR);
        final String AudioId= String.valueOf(year+mont+dayofweek+hourofday+minute+second);


        if (reco) {

            StorageReference riversRef = storageRef.child("Chat").child(IdHelper.Email + IdsChatHelper.mail).child(AudioId + ".3gp");
            Uri file = Uri.fromFile(new File(fileName));

            riversRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    SetCityId(AudioId, "No Need");
                }
            });


        }else {

            if (!(tvc.getText().toString().equals("Select City"))) {
                final String citys = tvc.getText().toString();


                StorageReference riversRef = storageRef.child(citys).child(TitleofService).child(IdHelper.Email).child(AudioId + ".3gp");
                Uri file = Uri.fromFile(new File(fileName));

                riversRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        SetCityId(AudioId, citys);
                    }
                });


            }
        }

    }
    private void ShowToast(String s){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
        new Handler() .postDelayed (new Runnable(){
            @Override
            public void run(){
                finish();
            }

        }, 2000);
    }

    public void SelectCity(View view) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

        builderSingle.setTitle("Click on Your City");

        final ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);



        arrayAdapter.add("Mailsi");
        arrayAdapter.add("Vehari");
        arrayAdapter.add("Lahore");
        arrayAdapter.add("Multan");
        arrayAdapter.add("Karachi");




        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = String.valueOf(arrayAdapter.getItem(which));
                tvc.setText(strName);

            }
        });
        builderSingle.show();
    }
    private void SetCityId(String s, final String city){
        if (reco){
            Schat= new HashMap<>();
            Schat.put("Email",IdsChatHelper.mail);
            Schat.put("ID",s);

            mdb.collection(IdHelper.Email+ IdsChatHelper.mail).add(Schat)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            ShowToast("ServiceRequestUploaded");

                        }
                    });

        }else {
            citydata = new HashMap<>();
            citydata.put("Email", IdHelper.Email);
            citydata.put("ID", s);
            citydata.put("City", city);
            mdb.collection(TitleofService)
                    .add(citydata)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            CheckSeller(city);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
    }

    private void CheckSeller(final String city) {
        final boolean[] ye = {false};
        mdb.collection(TitleofService)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (city.equals(document.get("City"))) {

                        ShowToast("ServiceRequestUploaded");
                        ye[0] =true;
                    }
                }
                if (!(ye[0])) {
                    ShowToast("ServiceRequestUploaded But Seller are not Avalibale At this city for this service");
                }

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ShowToast("Error in Download mails"+e);
                    }
                });


    }




}
