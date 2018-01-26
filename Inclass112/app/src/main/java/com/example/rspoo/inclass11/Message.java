package com.example.rspoo.inclass11;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;

public class Message extends AppCompatActivity implements View.OnClickListener , ChatAdapter.ItemClickCallBack {
    TextView username;
    ImageView logout;
    FirebaseUser user;
    ImageView btnSend,bntGal;
    EditText chatmsg;
    private RecyclerView recyclerView;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    static final int REQUEST_IMAGE_GET = 1;
    private Uri fullPhotoUri;
    private ChatAdapter chatAdapter;
    StorageReference storageRef;
    DatabaseReference mRoot  = FirebaseDatabase.getInstance().getReference();
    DatabaseReference  mConditionRef ;

    ArrayList<Chats> allChats = new ArrayList<Chats>();
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            fullPhotoUri = data.getData();
            // imageView.setImageURI(fullPhotoUri);


            final Chats toSend = new Chats();
            toSend.setMessage("");
            toSend.setFulname(user.getDisplayName());
            toSend.setWhen(new Date());
            toSend.setComments("");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final String key1 = mRoot.child("chats").push().getKey();
            toSend.setKey(key1);
            toSend.setUserID(user.getUid());
            //the below line also can be used
            // mRoot.child("expenses").child("/"+user.getUid()+"/").child("/"+key1+"/").setValue(newExpense);
            // mRoot.child("CompleteChat").child(key1).setValue(toSend);


            storageRef = storage.getReference();
            StorageReference riversRef = storageRef.child("chats/images/" + key1 + ".png");


            UploadTask uploadTask = riversRef.putFile(fullPhotoUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    toSend.setImageUrl(downloadUrl.toString());
                    mRoot.child("CompleteChat").child(key1).setValue(toSend);
                    //  Toast.makeText(Home.this, downloadUrl.toString(), Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    System.out.println("Upload is " + progress + "% done");
                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    System.out.println("Upload is paused");
                }
            });

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        username = (TextView)findViewById(R.id.textViewUser);
        logout = (ImageView)findViewById(R.id.imageViewLogout);
        logout.setOnClickListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        username.setText( user.getDisplayName());
        recyclerView = (RecyclerView) findViewById(R.id.container);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        chatAdapter = new ChatAdapter(allChats,this,R.layout.in_chat_list,user.getUid());
        chatAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(chatAdapter);




        btnSend =  (ImageView)findViewById(R.id.imageViewsend);
        bntGal =  (ImageView)findViewById(R.id.imageViewGal);
        chatmsg = (EditText)findViewById(R.id.editTextchat);

        bntGal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new  Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_GET);
                }
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chats toSend = new Chats();
                toSend.setMessage(chatmsg.getText().toString());
                toSend.setFulname(user.getDisplayName());
                toSend.setImageUrl("NA");
                toSend.setWhen(new Date());
                toSend.setComments("");
                // toSend.setCom(new Comments());
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String key1 = mRoot.child("chats").push().getKey();
                toSend.setKey(key1);
                toSend.setUserID(user.getUid());
                //the below line also can be used
                // mRoot.child("expenses").child("/"+user.getUid()+"/").child("/"+key1+"/").setValue(newExpense);
                mRoot.child("CompleteChat").child(key1).setValue(toSend);



            }
        });



        mConditionRef = mRoot.child("CompleteChat");

        //showProgressDialog();
        mConditionRef.addChildEventListener(new ChildEventListener() {


            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chats toadd = new Chats();
                toadd.setCom(new ArrayList<Comments>());
                toadd= dataSnapshot.getValue(Chats.class);

                allChats.add(toadd);
                if(allChats.size()!= 0)
                {
                    // textViewmesg.setVisibility(View.INVISIBLE);
                }
                Log.d("XXXXXX",allChats.toString());
                chatAdapter.notifyDataSetChanged();



            }



            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                // allUsers.re(toadd);


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                /*Expense toremove = dataSnapshot.getValue(Expense.class);
                MyList.remove(toremove);
                adapter.notifyDataSetChanged();*/
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onClick(View v) {
        FirebaseAuth.getInstance().signOut();
        Intent loggOut = new Intent(Message.this,MainActivity.class);
        loggOut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loggOut);
    }

    @Override
    public void OnItemClick(int p) {


        Chats toDelete = allChats.get(p);
        mConditionRef = mRoot.child("CompleteChat");
        mConditionRef.child(toDelete.getKey()).removeValue();
        allChats.remove(toDelete);
        chatAdapter.notifyDataSetChanged();

        if(!toDelete.getImageUrl().toString().equals("NA")) {
            storageRef = storage.getReference();
            StorageReference riversRef = storageRef.child("chats/images/" + toDelete.getKey() + ".png");

            riversRef.delete().addOnSuccessListener(new OnSuccessListener() {

                @Override
                public void onSuccess(Object o) {
                    Toast.makeText(Message.this, " Deleted",
                            Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                }
            });
        }

    }

    @Override
    public void OnItemCommentsClick(int p) {

        final  int pos = p;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("" +
                "Comments");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT );
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //  m_Text = input.getText().toString();

                Chats toupdate = allChats.get(pos);
                //mConditionRef = mRoot.child("CompleteChat");
                toupdate.setComments(toupdate.getComments()+  input.getText().toString());


                ArrayList <Comments> coms =  toupdate.getCom();

                if(coms == null)
                {
                    coms = new  ArrayList <Comments>();
                }
                coms.add(new Comments(input.getText().toString(),new Date()));
                toupdate.setCom(coms);
                mRoot.child("CompleteChat").child(toupdate.getKey()).setValue(toupdate);

                //mConditionRef.child(toupdate.getKey()).removeValue();
                //allChats.remove(toupdate);
                chatAdapter.notifyDataSetChanged();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();


    }
}
