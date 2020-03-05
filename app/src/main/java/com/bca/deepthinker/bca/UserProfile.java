package com.bca.deepthinker.bca;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;

import IdHelpers.IdHelper;
import IdHelpers.IdsChatHelper;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {


    private EditText UserFullName;
    private EditText UserLocation;
    private Button saveButton,ratinButton;
    private CircleImageView profileimage;
    private FirebaseAuth mAuth;

    private String no1="0",av1="0",name,location;

    private static int RESULT_LOAD_IMAGE = 1;
    Bitmap bmp = null;


    private FirebaseFirestore mdb;
    FirebaseStorage storage;

    private DatabaseReference UserReference;

    String CurrentUserId;
    private String no="0",av="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Toolbar toolbar=findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("User Profile");
        toolbar.setTitleTextColor(Color.WHITE);
        ActionBar actionbar=getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);


        mAuth=FirebaseAuth.getInstance();
        mdb= FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance("gs://fir-bca.appspot.com");

      //  CurrentUserId=mAuth.getCurrentUser().getUid();
//        UserReference=FirebaseDatabase.getInstance().getReference().child(CurrentUserId);

        UserFullName=(EditText) findViewById(R.id.userfullname);
        UserLocation=(EditText)findViewById(R.id.userlocation);
        saveButton=(Button)findViewById(R.id.savebtn);
        profileimage=(CircleImageView)findViewById(R.id.user_profile_image);
        ratinButton=(Button)findViewById(R.id.buttonratingp);

        ratinButton.setVisibility(View.INVISIBLE);

        if (IdHelper.rating){

            saveButton.setVisibility(View.INVISIBLE);
            ratinButton.setVisibility(View.VISIBLE);
        }


        mdb.collection("Users_profile").document(IdHelper.Email).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc = task.getResult();

                        UserFullName.setText((CharSequence) doc.get("FullName"));
                        UserLocation.setText((CharSequence) doc.get("Location"));
                        no=doc.getString("No");
                        av=doc.getString("Av");


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


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveprofileinformation();
            }
        });
    }

    private void saveprofileinformation() {


        String username = UserFullName.getText().toString();
        String userlocation = UserLocation.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please write your Full Name", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(userlocation)) {
            Toast.makeText(this, "Please write your Location", Toast.LENGTH_SHORT).show();
        } else {
            HashMap usermap = new HashMap();


            final boolean[] ch = {false};

            mdb.collection("Users_profile").document(IdHelper.Email).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot doc = task.getResult();
                            if (IdHelper.Email.equals(doc.get("Email"))){
                                ch[0] =true;
                            }

                        }
                    });



            if (ch[0]){
                usermap.put("FullName", username);
                usermap.put("Location", userlocation);
                usermap.put("No",no);
                usermap.put("Av",av);

                mdb.collection("Users_profile").document(IdHelper.Email)
                        .update(usermap).addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        UploadPimage();
                    }
                });

            }else {
                usermap.put("FullName", username);
                usermap.put("Location", userlocation);
                usermap.put("No","0");
                usermap.put("Av","0");
                //   usermap.put("Date of Birth","none");
                mdb.collection("Users_profile").document(IdHelper.Email)
                        .set(usermap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        UploadPimage();
                    }
                });

            }

        }


//                 updateChildren(usermap).addOnCompleteListener(new OnCompleteListener() {
//                @Override
//                public void onComplete(@NonNull Task task)
//                {
//                    if(task.isSuccessful())
//                    {
//                        SendUserToMainActivity();
//                        Toast.makeText(UserProfile.this, "your Profile is Updated Successfully.", Toast.LENGTH_LONG).show();
//
//                    }
//                    else
//                    {
//                        String message =  task.getException().getMessage();
//                        Toast.makeText(UserProfile.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
//
//                    }
//                }
//            });
//        }

    }

    private void UploadPimage(){

        if (bmp!=null) {

            StorageReference storageRef = storage.getReference();
            StorageReference mountainsRef = storageRef.child("Profile").child(IdHelper.Email+".jpg");

            Bitmap bitmap = bmp;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = mountainsRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    Toast.makeText(UserProfile.this, "your Profile is Updated Successfully.", Toast.LENGTH_LONG).show();
                    SendUserToMainActivity();
                    // ...
                }
            });
        }
    }


    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(UserProfile.this, success.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }



    public void LoadImage(View view) {

        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();



            bmp = null;
            try {
                bmp = getBitmapFromUri(selectedImage);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            profileimage.setImageBitmap(bmp);

        }


    }



    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
    public void GiveRating(View view) {
        mdb.collection("Users_profile").document(IdsChatHelper.mail).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc = task.getResult();
                        name=doc.getString("FullName");
                        location= doc.getString("Location");
                        no1=doc.getString("No");
                        av1=doc.getString("Av");
                        SetRating();
                    }
                });
    }
    private void SetRating() {
        final HashMap usermap = new HashMap();
        usermap.put("FullName", name);
        usermap.put("Location", location);


        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);

        LinearLayout linearLayout = new LinearLayout(this);
        final RatingBar rating = new RatingBar(this);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        rating.setLayoutParams(lp);
        rating.setNumStars(5);
        rating.setStepSize(1);

        //add ratingBar to linearLayout
        linearLayout.addView(rating);


        popDialog.setIcon(android.R.drawable.btn_star_big_on);
        popDialog.setTitle("Add Rating: ");

        //add linearLayout to dailog
        popDialog.setView(linearLayout);



        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                System.out.println("Rated val:"+v);
            }
        });



        // Button OK
        popDialog.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {




                        int noo= Integer.parseInt(no1);
                        noo=noo+1;
                        String no1= String.valueOf(noo);

                        int avv= Integer.parseInt(av1);
                        avv= Integer.parseInt(avv+String.valueOf(rating.getProgress()));
                        String av1= String.valueOf(avv);

                        usermap.put("No",no1);
                        usermap.put("Av",av1);


                        mdb.collection("Users_profile").document(IdHelper.Email)
                                .update(usermap).addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                ShowToast("Sucessfully Rating");
                            }
                        });
                        dialog.dismiss();
                    }

                })

                // Button Cancel
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        popDialog.create();
        popDialog.show();


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

}
