package com.example.gangu.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class LogIn extends AppCompatActivity {

    EditText email, pass;
    Button btn;


    FirebaseAuth auth;
    TextView reset_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        OnLogIn();
    }

    public void OnLogIn()
    {
        reset_pass = (TextView)findViewById(R.id.forgot_pass);
        email = (EditText)findViewById(R.id.email);
        pass = (EditText)findViewById(R.id.pass);
        btn = (Button)findViewById(R.id.login);
        reset_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LogIn.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().isEmpty())
                {
                    email.setError("Please Enter your Email");
                    email.requestFocus();
                }
                if(pass.getText().toString().isEmpty())
                {
                    pass.setError("Please Enter your correct Password");
                    pass.requestFocus();
                }
                else
                {
                    auth = FirebaseAuth.getInstance();
                    auth.signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                if(auth.getCurrentUser().isEmailVerified()) {
                                    FirebaseUser user = auth.getCurrentUser();
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                                    HashMap<String, Object> Hmap = new HashMap<>();
                                    Hmap.put("Emailverify", "Verify");
                                    reference.updateChildren(Hmap);
                                    Intent intent = new Intent(LogIn.this, LoggedIn.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                                else
                                {
                                    Toast.makeText(LogIn.this, "Please verify your email first...", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(LogIn.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
