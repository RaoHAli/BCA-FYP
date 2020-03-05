package com.bca.deepthinker.bca;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;

import IdHelpers.CustomAdapter;
import IdHelpers.IdHelper;
import IdHelpers.IdsChatHelper;

public class MessageListActivity extends AppCompatActivity {
    ListView view;
    private CustomAdapter CA;
    private ArrayList<String> name = new ArrayList<String>();
    private ArrayList<Integer> count = new ArrayList<Integer>();
    private ArrayList<byte[]> pic = new ArrayList<byte[]>();

    private ArrayList<String> mail = new ArrayList<String>();
    private ArrayList<String> ids = new ArrayList<String>();
    private ArrayList<String> Chat_ids = new ArrayList<String>();
    private ArrayList<String> Rating=new ArrayList<String>();

    String service="not";
    String city="not";
   private int rating;
  private   String no,av;


    private ArrayList<String> service1 = new ArrayList<String>();


    private FirebaseFirestore mdb;
    private FirebaseStorage storage;
    int loc=0;
    int index=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        view = (ListView) findViewById(R.id.messagelist1);



        mdb= FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance("gs://fir-bca.appspot.com");

        if (IdHelper.Seller) {

            mdb.collection("Seller")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    for (QueryDocumentSnapshot doc : task.getResult()) {

                        if (doc.get("Email").equals(IdHelper.Email)) {
                            //  service=String.valueOf(doc.get("Title"));
                            service1.add(String.valueOf(doc.get("Title")));
                            city=String.valueOf(doc.get("City"));
                        }


                    }

                    SelectSerivce(service1);

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });


        }else {

            mdb.collection("Buyer")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    for (QueryDocumentSnapshot doc : task.getResult()) {

                        if (doc.get("Email").equals(IdHelper.Email)) {
                            mail.add(String.valueOf(doc.get("SMail")));
                            ids.add(String.valueOf(doc.get("ID")));
                        }


                    }
                    GetImg();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }


    }

    private void FetchBuyers() {

        mdb.collection(service)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (city.equals(document.get("City"))) {
                        mail.add(String.valueOf(document.get("Email")));
                        ids.add(String.valueOf(document.get("ID")));

                    }
                }
                GetImg();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ShowToast("Error in Download mails"+e);
                    }
                });
    }

    private void GetAll() {
        final int[] i = {0};

        for (String mail1:mail) {
            i[0]++;
            mdb.collection("Users_profile").document(mail1).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            DocumentSnapshot doc = task.getResult();
                            name.add(String.valueOf(doc.get("FullName")));

                            no=doc.getString("No");
                            av=doc.getString("Av");

                            int noo= Integer.parseInt(no);
                            int avv= Integer.parseInt(av);



                            if(!(IdHelper.Seller)) {
                                if (noo > 0) {
                                    rating = avv / noo;
                                    Rating.add(String.valueOf(rating));
                                } else {
                                    Rating.add("New");
                                }
                            }

                            if (mail.size()== i[0]){
                                GetMessageCount();
                            }



                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ShowToast("Error in Download profile Name"+e);
                }
            });
        }




    }
    private  void GetMessageCount(){

        final int[] i = {0};
        for (String mail1:mail) {



            if (IdHelper.Seller){
                mdb.collection(mail1+IdHelper.Email)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {


                        if (task.getResult().isEmpty()){
                            i[0]++;
                            count.add(1);

                        }else {
                            int cn=1;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                cn=cn+1;

                            }
                            count.add(cn);
                            i[0]++;
                        }

                        if (mail.size()== i[0]){
                            ShowLists();

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {





                    }
                });

            }else {
                mdb.collection(IdHelper.Email+mail1)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {


                        if (task.getResult().isEmpty()){
                            i[0]++;
                            count.add(1);

                        }else {
                            int cn=1;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                cn=cn+1;

                            }
                            count.add(cn);
                            i[0]++;
                        }

                        if (mail.size()== i[0]){
                            ShowLists();

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {





                    }
                });

            }


        }

    }

    private void ShowLists() {


            name.removeAll(Arrays.asList(null,""));
             pic.removeAll(Arrays.asList(null,""));
             count.removeAll(Arrays.asList(null,""));
        Rating.removeAll(Arrays.asList(null,""));

        if (name!=null&&count!=null&&pic!=null) {
            CA = new CustomAdapter(name, count,Rating, pic, this);
            view.setAdapter(CA);
            view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position,
                                        long id) {


                    IdsChatHelper.mail = mail.get(position);
                    IdsChatHelper.service = service;
                    IdsChatHelper.city = city;
                    IdsChatHelper.name = name.get(position);
                    if (count.get(position) == 1) {
                        IdsChatHelper.ids = ids.get(position);
                    } else {
                        IdsChatHelper.id = 1;
                    }

                    Intent chatIntent = new Intent(MessageListActivity.this, ChatActivity.class);
                    chatIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(chatIntent);


                }
            });
        }else {
            ShowToast("No One Needed");
        }
    }

    private void GetImg() {
        final int[] i = {0};

        for (String mail1:mail) {
            i[0]++;

            StorageReference storageRef = storage.getReference();
            StorageReference islandRef = storageRef.child("Profile/"+ mail1+".jpg");
            final long ONE_MEGABYTE = (1024 * 1024)*5;
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {

                    pic.add(bytes);
                    if (mail.size()== i[0]){
                        GetAll();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    ShowToast("Error in Download pic"+exception);
                }
            });




        }




    }

    private void ShowToast(String s){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }


    private void SelectSerivce(ArrayList<String> service1){



        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

        builderSingle.setTitle("Click on Service");

        final ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);

        for (String s:service1){

            arrayAdapter.add(s);
        }








        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                service = String.valueOf(arrayAdapter.getItem(which));
                FetchBuyers();


            }
        });
        builderSingle.show();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent newint=new Intent(MessageListActivity.this,success.class);
        newint.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(newint);
    }
}
