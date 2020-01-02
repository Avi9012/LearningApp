package com.example.qa;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText email;
    Button sendlink;
    FirebaseAuth mfirebaseauth;
    ProgressDialog mprog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        getSupportActionBar().hide();
        email=findViewById(R.id.textView4);
        sendlink=findViewById(R.id.button3);
        mfirebaseauth=FirebaseAuth.getInstance();
        mprog=new ProgressDialog(this);

        sendlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mprog.setMessage("Sending...");
                mprog.show();
                String emailid=email.getText().toString();
                mfirebaseauth.sendPasswordResetEmail(emailid).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mprog.dismiss();
                            Intent i=new Intent(ForgotPasswordActivity.this,MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            Toast.makeText(ForgotPasswordActivity.this,"Password sent to your EmailId!",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else{
                            Toast.makeText(ForgotPasswordActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
