package com.example.qa;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qa.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.ViewHolder> {


    private Context context;

    private List<Answers> answersList;

    DatabaseReference ref;

    String name;

    User user;

    public AnswersAdapter(Context context, List<Answers> answersList) {
        this.context = context;
        this.answersList = answersList;
        //Toast.makeText(context, "In Answer adapter class", Toast.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.anslist, viewGroup, false);

        //Toast.makeText(context, "In onCreateViewHolder class", Toast.LENGTH_SHORT).show();

        return new AnswersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        final Answers answer = answersList.get(i);
        ref = FirebaseDatabase.getInstance().getReference().child("Users").child(answer.getUserId()).child("name");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.getValue().toString();
                viewHolder.username.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        viewHolder.ans.setText("Ans: "+answer.getString());
        viewHolder.date.setText(answer.getDate());
        viewHolder.time.setText(answer.getTime());

        ref = FirebaseDatabase.getInstance().getReference().child("Users").child(answer.getUserId());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                viewHolder.ans.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, UseransActivity.class);
                        intent.putExtra("imageUrl", user.getImageUrl());
                        intent.putExtra("name", user.getName());
                        intent.putExtra("email", user.getEmail());
                        context.startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Toast.makeText(context, answer.getString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return answersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView ans, date, time, username;

        public ViewHolder(@NonNull View view) {

            super(view);
            username = view.findViewById(R.id.user_name);
            ans = view.findViewById(R.id.answer);
            date = view.findViewById(R.id.date);
            time = view.findViewById(R.id.time);
            //Toast.makeText(context, "In ViewHolder class", Toast.LENGTH_SHORT).show();
        }
    }
}
