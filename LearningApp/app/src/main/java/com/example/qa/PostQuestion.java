package com.example.qa;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaeger.library.StatusBarUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class PostQuestion extends AppCompatActivity {

    private EditText question;
    private Button btn;

    private DatabaseReference reference;

    private Intent intent;
    boolean key;
    String id, ans, queid, User_id;
    Questions questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_question);
        StatusBarUtil.setTransparent(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            if (true) {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                // We want to change tint color to white again.
                // You can also record the flags in advance so that you can turn UI back completely if
                // you have set other flags before, such as translucent or full screen.
                decor.setSystemUiVisibility(0);
            }
        }
        //StatusBarUtil.setTransparent(this);
        question = (EditText)findViewById(R.id.Question);
        btn = (Button)findViewById(R.id.btn);
        //reference = FirebaseDatabase.getInstance().getReference().child("Questions");
        intent = getIntent();
        key = intent.getBooleanExtra("key", false);
        if(key)
        {
            ans = intent.getStringExtra("ans");
            id = intent.getStringExtra("id");
            queid = intent.getStringExtra("queid");
            User_id = intent.getStringExtra("UserId");
            question.setText(ans);
            btn.setText("Update");
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!key) {
                    post();
                }
                else
                {
                    post1();
                }
            }
        });

        /*

        // for bottom bar

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    */
    }

    private void post1() {

        reference = FirebaseDatabase.getInstance().getReference().child("Answers");
        String que = question.getText().toString();
        if(que.equals(""))
        {
            Toast.makeText(this, "Empty Answer can not be posted.", Toast.LENGTH_SHORT).show();
        }
        else {
            String Id = id;
            Date date = new Date();
            String Date = DateFormat.getDateInstance().format(date);
            String Time = DateFormat.getTimeInstance().format(date);
            HashMap<String, Object> map = new HashMap<>();
            map.put("QueId", queid);
            map.put("string", que);
            map.put("date", Date);
            map.put("time", Time);
            map.put("id", Id);
            map.put("UserId", User_id);
            reference.child(Id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(PostQuestion.this, "Answer is successfully Updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PostQuestion.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            question.setText("");
            Intent intent = new Intent(PostQuestion.this, ShowAnswers.class);
            intent.putExtra("time", questions.getTime());
            intent.putExtra("date", questions.getDate());
            intent.putExtra("answers", questions.getString());
            intent.putExtra("question", questions.getId());
            intent.putExtra("ans", questions.getAnswers());
            intent.putExtra("User_id", questions.getUserId());
            startActivity(intent);
            finish();
        }
    }

    private void post() {

        String que = question.getText().toString();
        if(que.equals(""))
        {
            Toast.makeText(this, "Empty Question can not be posted.", Toast.LENGTH_SHORT).show();
        }
        else {
            String id = reference.push().getKey();
            Date date = new Date();
            String Date = DateFormat.getDateInstance().format(date);
            String Time = DateFormat.getTimeInstance().format(date);
            HashMap<String, Object> map = new HashMap<>();
            map.put("string", que);
            map.put("Time", Time);
            map.put("Date", Date);
            map.put("Answers", 0);
            map.put("Id", id);
            map.put("UserId", FirebaseAuth.getInstance().getCurrentUser().getUid());
            reference.child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(PostQuestion.this, "Question is successfully added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PostQuestion.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            question.setText("");
            Intent intent = new Intent(PostQuestion.this, ScrollingActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(key)
        {
            reference = FirebaseDatabase.getInstance().getReference().child("Questions").child(queid);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    questions = dataSnapshot.getValue(Questions.class);
                    Intent intent = new Intent(PostQuestion.this, ShowAnswers.class);
                    intent.putExtra("time", questions.getTime());
                    intent.putExtra("date", questions.getDate());
                    intent.putExtra("answers", questions.getString());
                    intent.putExtra("question", questions.getId());
                    intent.putExtra("ans", questions.getAnswers());
                    intent.putExtra("User_id", questions.getUserId());
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else
        {
            Intent intent = new Intent(PostQuestion.this, ScrollingActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
