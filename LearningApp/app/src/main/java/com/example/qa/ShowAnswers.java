package com.example.qa;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

public class ShowAnswers extends AppCompatActivity {

    TextView t1, t2, t3, t4;
    boolean admin;
    private RecyclerView ansList;

    private AnswersAdapter AnsAdap;

    private List<Answers> answers;

    private String question, date, time, queid, str1, User_id, isAdmin, name, user_id;
    private DatabaseReference ref, ref1;

    Questions questions;

    private Long ans;

    FirebaseUser user;

    int x;

    //@RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_answers);
        getSupportActionBar().setTitle("Answers");

        admin = false;
        ansList = (RecyclerView)findViewById(R.id.answers);
        ansList.setHasFixedSize(true);
        ansList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        user = FirebaseAuth.getInstance().getCurrentUser();
        User_id = user.getUid();

        t4 = (TextView)findViewById(R.id.user_name);
        t1 = (TextView)findViewById(R.id.question);
        t2 = (TextView)findViewById(R.id.date);
        t3 = (TextView)findViewById(R.id.time);

        Intent i = getIntent();
        question = "Que: "+ i.getStringExtra("answers");
        time = i.getStringExtra("time");
        date = i.getStringExtra("date");
        queid = i.getStringExtra("question");
        user_id = i.getStringExtra("User_id");
        ref = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("name");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.getValue().toString();
                t4.setText(name);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ans = (Long) i.getLongExtra("ans", 0);
        t1.setText(question);
        t2.setText(date);
        t3.setText(time);
        answers = new ArrayList<>();
        //Toast.makeText(ShowAnswers.this, String.valueOf(ans), Toast.LENGTH_SHORT).show();
        if(ans > 0) {
            FetchAnswer();
        }

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

    private void FetchAnswer() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Answers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                answers.clear();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Answers answer = dataSnapshot1.getValue(Answers.class);

                    assert answer != null;
                    if (answer != null && answer.getQueId().equals(queid)) {
                        answers.add(answer);
                        //Toast.makeText(ShowAnswers.this, " Answer: " + answer.getString() + " Date: " + answer.getDate() + " Time: " + answer.getTime(), Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(ShowAnswers.this, "NULL String", Toast.LENGTH_SHORT).show();
                    }
                }

                AnsAdap = new AnswersAdapter(ShowAnswers.this, answers);

                new ItemTouchHelper(simpleCallback).attachToRecyclerView(ansList);

                ansList.setAdapter(AnsAdap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
            ref = FirebaseDatabase.getInstance().getReference().child("Users").child(User_id).child("isAdmin");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    isAdmin = dataSnapshot.getValue().toString();
                    if(isAdmin.equals("true")) {
                        admin = true;
                        if (i == ItemTouchHelper.LEFT) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShowAnswers.this);
                            alertDialog.setTitle("Alert!!!");
                            alertDialog.setMessage("Do you want to delete this Answer?");
                            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    final int position = viewHolder.getAdapterPosition();
                                    final Answers answer;
                                    str1 = answers.get(position).getId();
                                    answer = answers.get(position);
                                    ref1 = FirebaseDatabase.getInstance().getReference().child("Questions").child(answers.get(position).getQueId());
                                    ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            questions = dataSnapshot.getValue(Questions.class);
                                            ref1.child("Answers").setValue(questions.getAnswers() - 1);

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    ref = FirebaseDatabase.getInstance().getReference().child("Answers");
                                    ref.child(str1).removeValue();
                                    answers.remove(position);
                                    AnsAdap.notifyDataSetChanged();
                                    Snackbar.make(viewHolder.itemView, "1 item is Deleted", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //Toast.makeText(getContext(), "size of answers after undo: "+ String.valueOf(answers.size()), Toast.LENGTH_SHORT).show();
                                            HashMap<String, Object> ma = new HashMap<>();
                                            ma.put("QueId", answer.getQueId());
                                            ma.put("string", answer.getString());
                                            ma.put("date", answer.getDate());
                                            ma.put("time", answer.getTime());
                                            ma.put("id", answer.getId());
                                            ma.put("UserId", User_id);
                                            ref.child(answer.getId()).setValue(ma);
                                            answers.add(position, answer);
                                            AnsAdap.notifyItemInserted(position);
                                            ref1.child("Answers").setValue(questions.getAnswers());

                                        }
                                    }).show();
                                }
                            });
                            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //Toast.makeText(getContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                                    AnsAdap = new AnswersAdapter(ShowAnswers.this, answers);
                                    ansList.setAdapter(AnsAdap);
                                }
                            });
                            alertDialog.create().show();
                        }
                        else
                        {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShowAnswers.this);
                            alertDialog.setTitle("Alert!!!");
                            alertDialog.setMessage("Do you want to Edit this Answer?");
                            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final int position = viewHolder.getAdapterPosition();
                                    Answers answer;
                                    str1 = answers.get(position).getId();
                                    answer = answers.get(position);
                                    Intent intent = new Intent(ShowAnswers.this, PostQuestion.class);
                                    intent.putExtra("key", true);
                                    intent.putExtra("UserId", User_id);
                                    intent.putExtra("id", answer.getId());
                                    intent.putExtra("ans", answer.getString());
                                    intent.putExtra("queid", answer.getQueId());
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //Toast.makeText(getContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                                    AnsAdap = new AnswersAdapter(ShowAnswers.this, answers);
                                    ansList.setAdapter(AnsAdap);
                                }
                            });
                            alertDialog.create().show();
                        }
                    }
                    else if(answers.get(viewHolder.getAdapterPosition()).getUserId().equals(User_id))
                    {
                        if (i == ItemTouchHelper.LEFT) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShowAnswers.this);
                            alertDialog.setTitle("Alert!!!");
                            alertDialog.setMessage("Do you want to delete this Answer?");
                            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    final int position = viewHolder.getAdapterPosition();
                                    final Answers answer;
                                    str1 = answers.get(position).getId();
                                    answer = answers.get(position);
                                    ref1 = FirebaseDatabase.getInstance().getReference().child("Questions").child(answers.get(position).getQueId());
                                    ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            questions = dataSnapshot.getValue(Questions.class);
                                            ref1.child("Answers").setValue(questions.getAnswers() - 1);

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    ref = FirebaseDatabase.getInstance().getReference().child("Answers");
                                    ref.child(str1).removeValue();
                                    answers.remove(position);
                                    AnsAdap.notifyDataSetChanged();
                                    Snackbar.make(viewHolder.itemView, "1 item is Deleted", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //Toast.makeText(getContext(), "size of answers after undo: "+ String.valueOf(answers.size()), Toast.LENGTH_SHORT).show();
                                            HashMap<String, Object> ma = new HashMap<>();
                                            ma.put("QueId", answer.getQueId());
                                            ma.put("string", answer.getString());
                                            ma.put("date", answer.getDate());
                                            ma.put("time", answer.getTime());
                                            ma.put("id", answer.getId());
                                            ma.put("UserId", User_id);
                                            ref.child(answer.getId()).setValue(ma);
                                            answers.add(position, answer);
                                            AnsAdap.notifyItemInserted(position);
                                            ref1.child("Answers").setValue(questions.getAnswers());

                                        }
                                    }).show();
                                }
                            });
                            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //Toast.makeText(getContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                                    AnsAdap = new AnswersAdapter(ShowAnswers.this, answers);
                                    ansList.setAdapter(AnsAdap);
                                }
                            });
                            alertDialog.create().show();
                        } else {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShowAnswers.this);
                            alertDialog.setTitle("Alert!!!");
                            alertDialog.setMessage("Do you want to Edit this Answer?");
                            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final int position = viewHolder.getAdapterPosition();
                                    Answers answer;
                                    str1 = answers.get(position).getId();
                                    answer = answers.get(position);
                                    Intent intent = new Intent(ShowAnswers.this, PostQuestion.class);
                                    intent.putExtra("key", true);
                                    intent.putExtra("UserId", User_id);
                                    intent.putExtra("id", answer.getId());
                                    intent.putExtra("ans", answer.getString());
                                    intent.putExtra("queid", answer.getQueId());
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //Toast.makeText(getContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                                    AnsAdap = new AnswersAdapter(ShowAnswers.this, answers);
                                    ansList.setAdapter(AnsAdap);
                                }
                            });
                            alertDialog.create().show();
                        }
                    }
                    else{
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShowAnswers.this);
                        alertDialog.setTitle("Ooopss!");
                        alertDialog.setMessage("Seems that this Answer does not belong to you.");
                        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                AnsAdap = new AnswersAdapter(ShowAnswers.this, answers);
                                ansList.setAdapter(AnsAdap);

                            }
                        }).create().show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    };
}
