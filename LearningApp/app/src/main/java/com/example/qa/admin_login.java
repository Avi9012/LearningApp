package com.example.qa;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class admin_login extends AppCompatActivity {

    Button signin;
    EditText user,pass;
    FirebaseAuth mauth;
    DatabaseReference dref;
    ProgressDialog mprog;
    private FirebaseAuth.AuthStateListener mauthlistener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        getSupportActionBar().hide();
        signin=findViewById(R.id.button);
        user=findViewById(R.id.editText5);
        pass=findViewById(R.id.editText4);
        mauth= FirebaseAuth.getInstance();
        dref=FirebaseDatabase.getInstance().getReference().child("Users");
        mprog=new ProgressDialog(this);

        mauthlistener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){
                    Intent i=new Intent(admin_login.this,AdminActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            }
        };

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailid=user.getText().toString();
                String passd=pass.getText().toString();
                if(emailid.isEmpty()){
                    user.setError("Please enter email");
                    user.requestFocus();
                }
                else if(passd.isEmpty()){
                    pass.setError("Please enter password");
                    pass.requestFocus();
                }
                else if(!(passd.isEmpty() && emailid.isEmpty())){
                    mprog.setMessage("SigningIn");
                    mprog.show();
                    mauth.signInWithEmailAndPassword(emailid,passd).addOnCompleteListener(admin_login.this,  new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                mprog.dismiss();
                                String admin_id=mauth.getCurrentUser().getUid();
                                DatabaseReference adref=FirebaseDatabase.getInstance().getReference().child("Users").child(admin_id).child("isAdmin");
                                adref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.getValue().toString()=="true"){
                                            Intent i=new Intent(admin_login.this, AdminActivity.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(i);
                                            finish();
                                        }
                                        else{
                                            Toast.makeText(getApplicationContext(),"You are not Admin",Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                            else{
                                mprog.dismiss();
                                Toast.makeText(getApplicationContext(),"Login Error, Please try again!",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mauth.addAuthStateListener(mauthlistener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(admin_login.this,LearningApp_activity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
