package com.abhishek360.dev.hellodoctor;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static com.abhishek360.dev.hellodoctor.LoginActivity.spKey;

public class ChatActivity extends AppCompatActivity
{
    private static final int REQ = 678;
    private static final int PERMISSION_ACCESS = 445;
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private TextView userMessage;
    private View avatar;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private String myUID,currDocName,currDocID,my_name;
    private boolean isDoc=false;
    private boolean pmgranted;
    private Uri imgUri;
    private ImageView iv;
    private Dialog dialog;
    private ProgressBar progressBar;
    private Button btn;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        FirebaseApp.initializeApp(this);

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference=FirebaseStorage.getInstance().getReference();

        ActionBar actionBar= getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        sharedPreferences= getSharedPreferences(spKey,MODE_PRIVATE);
        myUID= sharedPreferences.getString(LoginActivity.spUID,null);
        my_name= sharedPreferences.getString(LoginActivity.spFullNameKey,null);
        isDoc=sharedPreferences.getBoolean(LoginActivity.spIsDoc,false);

        currDocID=getIntent().getExtras().getString("docID",null);
        currDocName=getIntent().getExtras().getString("docName","unknown");


        userMessage=findViewById(R.id.chat_user_message);

        recyclerView= findViewById(R.id.messages_view);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        setupChatAdapter();
        actionBar.setTitle(currDocName);
    }

    private void setupChatAdapter()
    {
        Query q;
        if(isDoc)
        {

            q = firebaseFirestore.collection("/CHAT/"+currDocID+"/"+myUID).orderBy("timeStamp");

        }
        else
        {
            q = firebaseFirestore.collection("/CHAT/"+myUID+"/"+currDocID).orderBy("timeStamp");

        }


        FirestoreRecyclerOptions<ChatAdapter> res = new FirestoreRecyclerOptions.Builder<ChatAdapter>()
                .setQuery(q, ChatAdapter.class).build();

        adapter = new FirestoreRecyclerAdapter<ChatAdapter, ChatAdapter.ChatViewHolder>(res)
        {


            @NonNull
            @Override
            public ChatAdapter.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                LayoutInflater inf = LayoutInflater.from(parent.getContext());

                View view = inf.inflate(R.layout.their_message,parent,false);

                return new ChatAdapter.ChatViewHolder(view);
            }

            @Override
            public void onError(@NonNull FirebaseFirestoreException e)
            {
                super.onError(e);
                Log.e("error", e.getMessage());
                //Toast.makeText(getContext(),""+e,Toast.LENGTH_LONG).show();


            }

            @Override
            protected void onBindViewHolder(@NonNull final ChatAdapter.ChatViewHolder holder, int position, @NonNull ChatAdapter model)
            {

                if(model.getSenderID().equals(myUID))
                {
                    holder.avatar.setVisibility(View.GONE);
                    holder.their_message.setVisibility(View.GONE);
                    holder.name.setVisibility(View.GONE);
                    holder.my_message.setVisibility(View.VISIBLE);

                    holder.my_message.setText(model.getMessage());


                }
                else
                {
                    holder.name.setText(""+currDocName);
                    holder.avatar.setVisibility(View.VISIBLE);

                    holder.their_message.setVisibility(View.VISIBLE);
                    holder.name.setVisibility(View.VISIBLE);
                    holder.my_message.setVisibility(View.GONE);

                    holder.their_message.setText(model.getMessage());

                }

            }

        };

        adapter.notifyDataSetChanged();
       // recyclerView.scrollToPosition(adapter.getItemCount()-1);
        recyclerView.setAdapter(adapter);


        dialog=new Dialog(ChatActivity.this);
        dialog.setContentView(R.layout.selector_dialog);
        iv=dialog.findViewById(R.id.imageView);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pmgranted){
                    getPermission();
                }else{
                    openGallery();
                }
            }
        });
        progressBar=dialog.findViewById(R.id.progressBar);

        ((Button)dialog.findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn=dialog.findViewById(R.id.sendbtn);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                btn.setEnabled(false);
                dialog.setCancelable(false);
                final StorageReference filepath=storageReference.child("Lab_Reports").child(currDocID);
                Drawable d=iv.getDrawable();
                Bitmap bt=((BitmapDrawable)d).getBitmap();
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                bt.compress(Bitmap.CompressFormat.JPEG,100,baos);
                byte[] data=baos.toByteArray();

                filepath.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        if(taskSnapshot.getTask().isSuccessful()){
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
//                                    sndMsg(uri.toString());
                                    Map<String,Object> mp=new HashMap<>();
                                    mp.put("url",uri.toString());
                                    mp.put("sentby",my_name);
                                    firebaseFirestore.collection("labreports")
                                            .document(currDocID)
                                            .set(mp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            dialog.dismiss();
                                            Toast.makeText(ChatActivity.this, "Lab Report Sent", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dialog.setCancelable(true);
                                            btn.setEnabled(true);
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.setCancelable(true);
                        progressBar.setVisibility(View.GONE);
                        btn.setEnabled(true);
                        Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }




    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void sendMessage(View view)
    {
        Map<String, Object> user = new HashMap<>();

        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e)
        {
            Log.e("Keyboard",e.getMessage());

        }

        user.put("message",userMessage.getText().toString());
        user.put("senderID",myUID);
        user.put("timeStamp", FieldValue.serverTimestamp());

        Map<String,Object> initdata= new HashMap<>();

        initdata.put("timeStamp",FieldValue.serverTimestamp());

        String collectionPath,collectionPathMessage;

        if(isDoc)
        {
            initdata.put("uID",currDocID);
            initdata.put("username",currDocName);
            collectionPath="/DOCTORS/"+myUID+"/"+"messages";
            collectionPathMessage="/CHAT/"+currDocID+"/"+myUID;

            firebaseFirestore.collection(collectionPath).document(currDocID).set(initdata,SetOptions.merge()).addOnCompleteListener(
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Log.d("chat_init","done");
                            }
                        }
                    }
            );

        }
        else
        {
            initdata.put("uID",myUID);
            initdata.put("username",my_name);
            collectionPath="/DOCTORS/"+currDocID+"/"+"messages";
            collectionPathMessage="/CHAT/"+myUID+"/"+currDocID;
            firebaseFirestore.collection(collectionPath).document(myUID).set(initdata,SetOptions.merge()).addOnCompleteListener(
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Log.d("chat_init","done");
                            }
                        }
                    }
            );


        }

        firebaseFirestore.collection(collectionPathMessage).add(user).addOnSuccessListener(
                new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference)
                    {
                        userMessage.setText("");

                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(adapter.getItemCount()-1);


                        Log.d("Chat message","Sent");

                    }
                }
        )
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Log.e("chat message error", e.getMessage());

                    }
                });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if(isDoc)
            getMenuInflater().inflate(R.menu.doctor_menu, menu);
        else
            getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_logout:
                sharedPreferences.edit().clear().apply();
                FirebaseAuth.getInstance().signOut();

                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
                return true;

            case android.R.id.home:
                finish();
                return  true;
            case R.id.menu_lab_report:

                if(isDoc)
                    dialog.show();
                else
                    //go to labreports activity
                    startActivity(new Intent(ChatActivity.this,LabReportActivity.class));

                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void openGallery(){
        Intent gallery=new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery,REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==REQ && data!=null){
            imgUri=data.getData();
            String str=imgUri.toString();
            ((TextView)dialog.findViewById(R.id.tvfname)).setText(str.substring(str.lastIndexOf("/")+1));
            Picasso.get().load(imgUri).into(iv);
        }
    }


    private void getPermission(){
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            pmgranted=true;
        }else{
            ActivityCompat.requestPermissions(ChatActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_ACCESS);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        pmgranted=false;
        switch (requestCode){
            case PERMISSION_ACCESS:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    pmgranted=true;
                    openGallery();
                }
                break;
        }
    }
}
