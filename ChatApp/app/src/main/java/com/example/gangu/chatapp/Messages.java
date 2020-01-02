package com.example.gangu.chatapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gangu.chatapp.Adapter.MessageAdap;
import com.example.gangu.chatapp.model.Chat;
import com.example.gangu.chatapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Messages extends AppCompatActivity {

    CircleImageView userimage;
    TextView username;

    FirebaseUser fireuser;
    DatabaseReference reference;

    Intent intent;

    RecyclerView recyclerView;
    List<Chat> chatList;

    MessageAdap messageAdap;

    ImageButton send, doc;

    ValueEventListener valueEvent;

    String userid;

    EditText message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Messages.this, LoggedIn.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        intent = getIntent();
        userid = intent.getStringExtra("userid");


        userimage = (CircleImageView)findViewById(R.id.image);
        username = (TextView)findViewById(R.id.user);

        send = (ImageButton) findViewById(R.id.send);
        doc = (ImageButton) findViewById(R.id.doc);
        doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Messages.this, Document.class));
            }
        });
        message = (EditText) findViewById(R.id.message);

        fireuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = message.getText().toString();
                if(msg.equals(""))
                {
                    Toast.makeText(Messages.this, "Empty message can not be send", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    sendMessage(msg, fireuser.getUid(), userid);
                }
                message.setText("");
            }

        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if(user.getUsername().equals("default"))
                {
                    userimage.setImageResource(R.mipmap.ic_launcher_round);
                }
                else
                {
                    Glide.with(getApplicationContext()).load(user.getImageUrl()).into(userimage);
                }

                readMessage(user.getImageUrl(), fireuser.getUid(), userid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Messages.this, "Data Retrieval is canceled due to "+databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        messageseen(userid);

    }

    private void messageseen(final String userid)
    {
        reference = FirebaseDatabase.getInstance().getReference("Chats");

        valueEvent = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(fireuser.getUid()) && chat.getSender().equals(userid))
                    {
                        HashMap<String, Object> Hmap = new HashMap<>();
                        Hmap.put("isseen", true);
                        snapshot.getRef().updateChildren(Hmap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String msg, String uid, final String userid) {

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> Hmap = new HashMap<>();
        Hmap.put("sender", uid);
        Hmap.put("receiver", userid);
        Hmap.put("message", msg);
        Hmap.put("isseen", false);

        reference1.child("Chats").push().setValue(Hmap);

        final DatabaseReference chatref = FirebaseDatabase.getInstance().getReference("Chatlist");
        chatref.child(fireuser.getUid()).child(userid);

        chatref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                {
                    chatref.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessage(final String imageUrl, final String myid, final String userid)
    {

        chatList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                chatList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Chat chat = snapshot.getValue(Chat.class);
                    if((chat.getReceiver().equals(myid) && chat.getSender().equals(userid)) || (chat.getReceiver().equals(userid) && chat.getSender().equals(myid)))
                    {
                        chatList.add(chat);
                    }
                    /*
                    if(chat == null)
                    {
                        Toast.makeText(Messages.this, "Chat in null", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(Messages.this, chat.getReceiver()+" , "+chat.getSender()+" , "+chat.isIsseen(), Toast.LENGTH_SHORT).show();
                    }
                    */
                }
                messageAdap = new MessageAdap(Messages.this, chatList, imageUrl);
                recyclerView.setAdapter(messageAdap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fireuser.getUid());

        HashMap<String, Object> Hmap = new HashMap<>();
        Hmap.put("status", status);
        reference.updateChildren(Hmap);

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();

        status("Offline");
    }

}
