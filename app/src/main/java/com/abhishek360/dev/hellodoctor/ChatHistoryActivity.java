package com.abhishek360.dev.hellodoctor;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import static com.abhishek360.dev.hellodoctor.LoginActivity.spKey;

public class ChatHistoryActivity extends AppCompatActivity
{

    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter adapter;

    private FirebaseFirestore firebaseFirestore;
    private String myUID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);
        FirebaseApp.initializeApp(this);

        ActionBar actionBar= getSupportActionBar();
        actionBar.setTitle("Home");

        firebaseFirestore = FirebaseFirestore.getInstance();
        sharedPreferences= getSharedPreferences(spKey,MODE_PRIVATE);
        myUID= sharedPreferences.getString(LoginActivity.spUID,null);


        recyclerView= (RecyclerView)findViewById(R.id.chat_history_recycler);
        setupChatListAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void setupChatListAdapter()
    {
        Query q = firebaseFirestore.collection("/DOCTORS/"+myUID+"/messages").orderBy("timeStamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatListAdapter> res = new FirestoreRecyclerOptions.Builder<ChatListAdapter>()
                .setQuery(q, ChatListAdapter.class).build();

        adapter = new FirestoreRecyclerAdapter<ChatListAdapter, ChatListAdapter.ChatListViewHolder>(res)
        {


            @NonNull
            @Override
            public ChatListAdapter.ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                LayoutInflater inf = LayoutInflater.from(parent.getContext());

                View view = inf.inflate(R.layout.chat_list_view_holder,parent,false);

                return new ChatListAdapter.ChatListViewHolder(view);
            }

            @Override
            public void onError(@NonNull FirebaseFirestoreException e)
            {
                super.onError(e);
                Log.e("error", e.getMessage());
                //Toast.makeText(getContext(),""+e,Toast.LENGTH_LONG).show();


            }

            @Override
            protected void onBindViewHolder(@NonNull final ChatListAdapter.ChatListViewHolder holder, int position, @NonNull final ChatListAdapter model)
            {
                holder.name.setText(""+model.getUsername());
                holder.chatListCard.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent chatIntent= new Intent(ChatHistoryActivity.this,ChatActivity.class);
                                chatIntent.putExtra("docID",model.getuID());
                                chatIntent.putExtra("docName",model.getUsername());
                                startActivity(chatIntent);
                            }
                        });
                holder.chatListCard.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        holder.chatListCard.setForeground(new ColorDrawable());
                        return false;
                    }
                });

            }



        };

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_doc_home, menu);
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
