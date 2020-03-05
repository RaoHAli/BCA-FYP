package com.bca.deepthinker.bca;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import IdHelpers.IdHelper;
import IdHelpers.IdsChatHelper;

public class SellerProvideServicesActivity extends AppCompatActivity {
    private String TitleofService="Nothing";
    TextView tv,tvrs,tvc;
    private ImageView Im1,Im2;

    private FirebaseFirestore mdb;
    FirebaseStorage storage;

    Map<String, String> citydata ;
    Map<String, String> Schat ;

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;

    // private RecordButton recordButton = null;
    private MediaRecorder recorder = null;
    private boolean mStartRecording = true;

    // private PlayButton   playButton = null;
    private MediaPlayer player = null;
    private boolean mStartPlaying = true;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    private boolean reco=false;

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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_provide_services);

        tv=findViewById(R.id.textViewTitles);
        tvc=findViewById(R.id.textViewcitys);
        tvrs=findViewById(R.id.textViewRecordinstatus2);
        Im1=findViewById(R.id.imageView23);
        Im2=findViewById(R.id.imageView22);
        mdb= FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance("gs://fir-bca.appspot.com");


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
                tvrs.setVisibility(View.INVISIBLE);
                Im2.setVisibility(View.INVISIBLE);
                Im1.setVisibility(View.INVISIBLE);

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

    public void SelectCitys(View view) {
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
    private void SetCityId(){
        String cit=tvc.getText().toString();
        if (!(cit.equals("Select City"))) {
            citydata = new HashMap<>();
            citydata.put("City", cit);
            citydata.put("Title", TitleofService);
            citydata.put("Email",IdHelper.Email);
            mdb.collection("Seller").add(citydata)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            ShowToast("ServiceUploaded");
                        }
                    });


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

    public void SendData(View view) {
        if (reco) {
            StartChat();
        } else {
            SetCityId();
        }
    }

    private void StartChat() {

        final StorageReference storageRef = storage.getReference();
        Calendar cal = Calendar.getInstance();
        int second = cal.get(Calendar.SECOND);
        int minute = cal.get(Calendar.MINUTE);
        int hourofday = cal.get(Calendar.HOUR_OF_DAY);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        int mont=cal.get(Calendar.MONTH);
        int year=cal.get(Calendar.YEAR);
        final String AudioId= String.valueOf(year+mont+dayofweek+hourofday+minute+second);




            if (IdsChatHelper.id==0) {

                Schat= new HashMap<>();
                Schat.put("Email",IdsChatHelper.mail);
                Schat.put("SMail",IdHelper.Email);
                Schat.put("ID",AudioId);
                mdb.collection("Buyer").add(Schat)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                StorageReference riversRef = storageRef.child("Buyer").child(IdHelper.Email).child(AudioId+".3gp");
                                Uri file = Uri.fromFile(new File(fileName));

                                riversRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        ShowToast("ServiceUploaded");
                                    }
                                });

                            }
                        });

            }else {

                Schat= new HashMap<>();
                Schat.put("Email",IdsChatHelper.mail);
                Schat.put("ID",AudioId);
                mdb.collection(IdsChatHelper.mail+IdHelper.Email).add(Schat)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {

                                StorageReference riversRef = storageRef.child("Chat").child(IdsChatHelper.mail+IdHelper.Email).child(AudioId+".3gp");
                                Uri file = Uri.fromFile(new File(fileName));

                                riversRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        ShowToast("ServiceUploaded");
                                    }
                                });

                            }
                        });
            }







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
}
