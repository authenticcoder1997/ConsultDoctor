
package com.abhishek360.dev.hellodoctor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import static com.abhishek360.dev.hellodoctor.LoginActivity.spKey;

public class MainActivity extends AppCompatActivity
{
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEditor;
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private String myUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        firebaseFirestore = FirebaseFirestore.getInstance();
        //firebaseStorage= FirebaseStorage.getInstance();

        sharedPreferences= getSharedPreferences(spKey,MODE_PRIVATE);
        myUID= sharedPreferences.getString(LoginActivity.spUID,null);

        recyclerView= (RecyclerView)findViewById(R.id.team_member_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setupDoctorsAdapter();

    }



    private void setupDoctorsAdapter()
    {
        Query q = firebaseFirestore.collection("/DOCTORS/");

        FirestoreRecyclerOptions<DoctorsAdapter> res = new FirestoreRecyclerOptions.Builder<DoctorsAdapter>()
                .setQuery(q, DoctorsAdapter.class).build();

        adapter = new FirestoreRecyclerAdapter<DoctorsAdapter, DoctorsAdapter.DoctorViewHolder>(res)
        {


            @NonNull
            @Override
            public DoctorsAdapter.DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                LayoutInflater inf = LayoutInflater.from(parent.getContext());

                View view = inf.inflate(R.layout.doctor_view_holder,parent,false);

                return new DoctorsAdapter.DoctorViewHolder(view);
            }

            @Override
            public void onError(@NonNull FirebaseFirestoreException e)
            {
                super.onError(e);
                Log.e("error", e.getMessage());
                //Toast.makeText(getContext(),""+e,Toast.LENGTH_LONG).show();


            }

            @Override
            protected void onBindViewHolder(@NonNull final DoctorsAdapter.DoctorViewHolder holder, int position, @NonNull final DoctorsAdapter model)
            {

                holder.name.setText(""+model.getName());
                holder.qualification.setText(""+model.getQual());
                holder.speciality.setText(""+model.getSpecs());
                holder.chatButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent chatIntent= new Intent(MainActivity.this,ChatActivity.class);
                        chatIntent.putExtra("docID",model.getDocID());
                        chatIntent.putExtra("docName",model.getName());
                        startActivity(chatIntent);




                    }
                });



                /*try
                {
                    storageReference=firebaseStorage.getReference().child("/SPONSORS_LOGO/"+model.getPicUrl());
                    Glide.with(getApplicationContext()).using(new FirebaseImageLoader()).load(storageReference)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.logo_sponsors);


                }
                catch (Exception e)
                {
                    Log.d("Event Image:",""+e);
                    holder.logo_sponsors.setImageResource(R.drawable.userdefaultpic);

                }*/
                /*storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                {
                    @Override
                    public void onSuccess(Uri uri)
                    {
                        try
                        {


                        }
                        catch (Exception e)
                        {
                            Log.d("Picture Load:",""+e);
                        }                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Log.d("Picture Load:",""+e);
                        //tosty(getActivity(),""+e);
                        holder.logo_sponsors.setImageResource(R.drawable.loading_icon);
                    }
                });*/




            }



        };

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                sharedPreferences.edit().clear().apply();
                FirebaseAuth.getInstance().signOut();

                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
                return true;
            case R.id.menu_lab_report:

                Intent labreportIntent = new Intent(this, LabReportActivity.class);
                startActivity(labreportIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);

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
}
