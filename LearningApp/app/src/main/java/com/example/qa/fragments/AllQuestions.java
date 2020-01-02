package com.example.qa.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.qa.Answers;
import com.example.qa.QuestionAdapter;
import com.example.qa.Questions;
import com.example.qa.R;

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

import static android.content.DialogInterface.*;

public class AllQuestions extends Fragment {

    private RecyclerView queList;

    boolean admin;

    DatabaseReference ref;

    private QuestionAdapter QuesAdap;

    private List<Questions> questions, question1;

    private List<Answers> answers;

    private List<String> ids;

    public String str, str1;

    public Questions question;

    public int position;

    FirebaseUser user;
    String isAdmin, User_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_questions2, container, false);

        queList = view.findViewById(R.id.questions);
        queList.setHasFixedSize(true);
        queList.setLayoutManager(new LinearLayoutManager(getContext()));

        user = FirebaseAuth.getInstance().getCurrentUser();
        User_id = user.getUid();
        questions = new ArrayList<>();
        answers = new ArrayList<>();

        admin = false;
        ids = new ArrayList<>();

        FetchQuestion();

        return view;
    }

    private void FetchQuestion() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Questions");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                questions.clear();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Questions question = dataSnapshot1.getValue(Questions.class);
                    assert question != null;
                    if (question != null) {
                        questions.add(question);
                        //Toast.makeText(getContext(), " Question: " + question + " Date: " + question.getDate() + " Time: " + question.getTime() + " Answers: " + question.getAnswers(), Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(getContext(), "NULL", Toast.LENGTH_SHORT).show();
                    }
                }

                QuesAdap = new QuestionAdapter(getContext(), questions);

                new ItemTouchHelper(simpleCallback).attachToRecyclerView(queList);

                queList.setAdapter(QuesAdap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
            ref = FirebaseDatabase.getInstance().getReference().child("Users").child(User_id).child("isAdmin");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    isAdmin = dataSnapshot.getValue().toString();
                    if (isAdmin.equals("true")) {
                        admin = true;
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                        alertDialog.setTitle("Alert!!!");
                        alertDialog.setMessage("Do you want to delete this Question?");
                        alertDialog.setPositiveButton("Yes", new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                position = viewHolder.getAdapterPosition();
                                str1 = questions.get(position).getId();
                                question = questions.get(position);
                                ref = FirebaseDatabase.getInstance().getReference().child("Answers");
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        ids.clear();
                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                            Answers answer = dataSnapshot1.getValue(Answers.class);
                                            assert answer != null;
                                            if (answer.getQueId().equals(str1)) {
                                                ids.add(answer.getId());
                                                answers.add(answer);
                                                //Toast.makeText(getContext(), " Answer id: " + answer.getId() + "    Answer: " + answer.getString() + " True", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Toast.makeText(getContext(), " Question: " + str1 + " Answer: " + answer.getId(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        //Toast.makeText(getContext(), "size of answers before undo: " + String.valueOf(answers.size()), Toast.LENGTH_SHORT).show();
                                        ref = FirebaseDatabase.getInstance().getReference().child("Answers");
                                        for (int i = 0; i < ids.size(); i++) {
                                            ref.child(ids.get(i)).removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                                ref = FirebaseDatabase.getInstance().getReference();
                                ref.child("Questions").child(str1).removeValue();
                                questions.remove(position);
                                QuesAdap.notifyDataSetChanged();
                                str = null;
                                Snackbar.make(viewHolder.itemView, "1 item is Deleted", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(getContext(), "size of answers after undo: " + String.valueOf(answers.size()), Toast.LENGTH_SHORT).show();
                                        ref = FirebaseDatabase.getInstance().getReference().child("Answers");
                                        for (int i = 0; i < answers.size(); i++) {
                                            Answers answer = answers.get(i);
                                            HashMap<String, Object> ma = new HashMap<>();
                                            ma.put("QueId", answer.getQueId());
                                            ma.put("string", answer.getString());
                                            ma.put("date", answer.getDate());
                                            ma.put("time", answer.getTime());
                                            ma.put("id", answer.getId());
                                            ma.put("UserId", answer.getUserId());
                                            ref.child(answer.getId()).setValue(ma);
                                        }
                                        answers.clear();
                                        ref = FirebaseDatabase.getInstance().getReference().child("Questions");
                                        HashMap<String, Object> map = new HashMap<>();
                                        map.put("string", question.getString());
                                        map.put("Time", question.getTime());
                                        map.put("Date", question.getDate());
                                        map.put("Answers", question.getAnswers());
                                        map.put("Id", question.getId());
                                        map.put("UserId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        ref.child(question.getId()).setValue(map);
                                        questions.add(position, question);
                                        QuesAdap.notifyItemInserted(position);

                                    }
                                }).show();
                            }
                        });
                        alertDialog.setNegativeButton("No", new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                QuesAdap = new QuestionAdapter(getContext(), questions);
                                queList.setAdapter(QuesAdap);
                            }
                        });
                        alertDialog.create().show();
                    } else if (questions.get(viewHolder.getAdapterPosition()).getUserId().equals(User_id) && admin == false) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                        alertDialog.setTitle("Alert!!!");
                        alertDialog.setMessage("Do you want to delete this Question?");
                        alertDialog.setPositiveButton("Yes", new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                position = viewHolder.getAdapterPosition();
                                str1 = questions.get(position).getId();
                                question = questions.get(position);
                                ref = FirebaseDatabase.getInstance().getReference().child("Answers");
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        ids.clear();
                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                            Answers answer = dataSnapshot1.getValue(Answers.class);
                                            assert answer != null;
                                            if (answer.getQueId().equals(str1)) {
                                                ids.add(answer.getId());
                                                answers.add(answer);
                                                //Toast.makeText(getContext(), " Answer id: " + answer.getId() + "    Answer: " + answer.getString() + " True", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Toast.makeText(getContext(), " Question: " + str1 + " Answer: " + answer.getId(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        //Toast.makeText(getContext(), "size of answers before undo: " + String.valueOf(answers.size()), Toast.LENGTH_SHORT).show();
                                        ref = FirebaseDatabase.getInstance().getReference().child("Answers");
                                        for (int i = 0; i < ids.size(); i++) {
                                            ref.child(ids.get(i)).removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                                ref = FirebaseDatabase.getInstance().getReference();
                                ref.child("Questions").child(str1).removeValue();
                                questions.remove(position);
                                QuesAdap.notifyDataSetChanged();
                                str = null;
                                Snackbar.make(viewHolder.itemView, "1 item is Deleted", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(getContext(), "size of answers after undo: " + String.valueOf(answers.size()), Toast.LENGTH_SHORT).show();
                                        ref = FirebaseDatabase.getInstance().getReference().child("Answers");
                                        for (int i = 0; i < answers.size(); i++) {
                                            Answers answer = answers.get(i);
                                            HashMap<String, Object> ma = new HashMap<>();
                                            ma.put("QueId", answer.getQueId());
                                            ma.put("string", answer.getString());
                                            ma.put("date", answer.getDate());
                                            ma.put("time", answer.getTime());
                                            ma.put("id", answer.getId());
                                            ma.put("UserId", answer.getUserId());
                                            ref.child(answer.getId()).setValue(ma);
                                        }
                                        answers.clear();
                                        ref = FirebaseDatabase.getInstance().getReference().child("Questions");
                                        HashMap<String, Object> map = new HashMap<>();
                                        map.put("string", question.getString());
                                        map.put("Time", question.getTime());
                                        map.put("Date", question.getDate());
                                        map.put("Answers", question.getAnswers());
                                        map.put("Id", question.getId());
                                        map.put("UserId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        ref.child(question.getId()).setValue(map);
                                        questions.add(position, question);
                                        QuesAdap.notifyItemInserted(position);

                                    }
                                }).show();
                            }
                        });
                        alertDialog.setNegativeButton("No", new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                QuesAdap = new QuestionAdapter(getContext(), questions);
                                queList.setAdapter(QuesAdap);
                            }
                        });
                        alertDialog.create().show();
                    } else if (admin == false) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                        alertDialog.setTitle("Ooopss!");
                        alertDialog.setMessage("Seems that this Question does not belong to you.");
                        alertDialog.setPositiveButton("Ok", new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                QuesAdap = new QuestionAdapter(getContext(), questions);
                                queList.setAdapter(QuesAdap);
                            }
                        }).create().show();
                    }
                    admin = false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    };
}