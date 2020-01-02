package com.example.gangu.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class SignUp extends AppCompatActivity {

    EditText email, pass, User_name;
    Button btn;


    FirebaseAuth auth;
    DatabaseReference reference;
    private static final int PER_LOGIN = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        User_name = (EditText)findViewById(R.id.User_name);
        email = (EditText)findViewById(R.id.email);
        pass = (EditText)findViewById(R.id.pass);
        btn = (Button)findViewById(R.id.Sign_up);
        auth = FirebaseAuth.getInstance();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(User_name.getText().toString().isEmpty())
                {
                    User_name.setError("User Name is required");
                    User_name.requestFocus();
                }
                else if(email.getText().toString().isEmpty())
                {
                    email.setError("Email Address is required");
                    email.requestFocus();
                }
                else if(pass.getText().toString().isEmpty())
                {
                    pass.setError("Password is required");
                    pass.requestFocus();
                }
                else if(pass.getText().toString().length() < 6)
                {
                    pass.setError("Password must be at least 6 character long...");
                    pass.requestFocus();
                }
                else
                {
                   register(User_name.getText().toString(), email.getText().toString(), pass.getText().toString());
                }
            }
        });
    }


        public void register(final String User_name, final String Email, final String Pass)
        {
            final ProgressDialog pd = ProgressDialog.show(SignUp.this, "Please wait...", "Progressing...", true);

            auth.createUserWithEmailAndPassword(Email, Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    pd.dismiss();
                    if(task.isSuccessful())
                    {
                        auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(SignUp.this, "Check your email for verification", Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = auth.getCurrentUser();

                                    assert user != null;
                                    final String userid = user.getUid();


                                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                                    Map<String, String> Hmap = new HashMap<>();
                                    Hmap.put("id", userid);
                                    Hmap.put("username", User_name);
                                    Hmap.put("status", "Offline");
                                    Hmap.put("imageUrl", "default");
                                    Hmap.put("Emailverify", "Not_Verify");

                                    reference.setValue(Hmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //Toast.makeText(SignUp.this, "Now you are the part of ChatApp", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(SignUp.this, LogIn.class);
                                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK)
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(SignUp.this, "something is wrong", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                else
                                {
                                    Toast.makeText(SignUp.this, "Registration failed...", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(SignUp.this, "You can't register with this Email and Password", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}


