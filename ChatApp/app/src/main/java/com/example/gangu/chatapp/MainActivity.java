package com.example.gangu.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Button btn1, btn2;

    FirebaseUser fireuser;

    @Override
    protected void onStart() {

        super.onStart();

        fireuser = FirebaseAuth.getInstance().getCurrentUser();

        if(fireuser != null && fireuser.isEmailVerified())
        {
            Intent i = new Intent(MainActivity.this, LoggedIn.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onbtnpress();
    }
    public void onbtnpress()
    {
        btn1 = (Button)findViewById(R.id.login);
        btn2 = (Button)findViewById(R.id.regs);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LogIn.class);
                startActivity(intent);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUp.class);
                startActivity(intent);
            }
        });
    }
}
