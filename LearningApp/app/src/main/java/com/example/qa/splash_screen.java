package com.example.qa;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaeger.library.StatusBarUtil;

public class splash_screen extends AppCompatActivity {

    private int splash_time_out=1000;
    //FirebaseAuth.AuthStateListener mauthlistener;
    //DatabaseReference dref;
    FirebaseAuth mauth;
    DatabaseReference dref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        //StatusBarUtil.setColor(this, Color.BLACK);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        mauth=FirebaseAuth.getInstance();
        //dref= FirebaseDatabase.getInstance().getReference().child("Users");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mauth=FirebaseAuth.getInstance();
                dref=FirebaseDatabase.getInstance().getReference().child("Users");
                if(mauth.getCurrentUser()!=null){
                    if(mauth.getCurrentUser()!=null && mauth.getCurrentUser().isEmailVerified()){
                        DatabaseReference ref=dref.child(mauth.getCurrentUser().getUid()).child("isAdmin");
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue()=="true"){
                                    Intent i=new Intent(splash_screen.this,AdminActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    finish();
                                }
                                else{
                                    Intent i=new Intent(splash_screen.this,HomeActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
                else {
                    Intent i = new Intent(splash_screen.this, LearningApp_activity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
            }
        },splash_time_out);
    }

}
