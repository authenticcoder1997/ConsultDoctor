package com.abhishek360.dev.hellodoctor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class LabReportActivity extends AppCompatActivity {

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    List<ImageLabReport> imageLabReportList;

    FirebaseFirestore firebaseFirestore;

    FirebaseAuth auth;

    RecyclerView recyclerView;
    ReportsAdapter reportsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_report);

        firebaseFirestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();

        imageLabReportList=new ArrayList<>();

        recyclerView=findViewById(R.id.rvreports);



        firebaseFirestore.collection("labreports").document(auth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String url=documentSnapshot.getString("url");
                String docname=documentSnapshot.getString("sentby");
                ImageLabReport sampleImageLabReport=new ImageLabReport();
                sampleImageLabReport.setImgurl(url);
                sampleImageLabReport.setSenderName(docname);
                imageLabReportList.add(sampleImageLabReport);
                initRecycleView(imageLabReportList);
            }
        });


    }

    private void initRecycleView(List<ImageLabReport> imageLabReports) {

        reportsAdapter=new ReportsAdapter(LabReportActivity.this,imageLabReportList);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(reportsAdapter);




    }

}
