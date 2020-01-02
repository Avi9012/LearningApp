package com.example.qa;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {


    private Context context;

    private List<Questions> questionList;

    private DatabaseReference ref;

    String name;

    public QuestionAdapter(Context context, List<Questions> questionList) {
        this.context = context;
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.queslist, parent, false);

        return new QuestionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Questions question = questionList.get(position);
        holder.ques.setText("Que: "+question.getString());

        holder.ques.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref = FirebaseDatabase.getInstance().getReference().child("Users").child(question.getUserId()).child("name");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        name = dataSnapshot.getValue().toString();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Intent intent = new Intent(context, ShowAnswers.class);
                intent.putExtra("time", question.getTime());
                intent.putExtra("date", question.getDate());
                intent.putExtra("answers", question.getString());
                intent.putExtra("question", question.getId());
                intent.putExtra("ans", question.getAnswers());
                intent.putExtra("User_id", question.getUserId());
               // Toast.makeText(context, "Answers: "+String.valueOf(question.getAnswers()), Toast.LENGTH_SHORT).show();
                context.startActivity(intent);
            }
        });
        holder.reply.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    String input = v.getText().toString();
                    if(input.equals(""))
                    {
                        Toast.makeText(context, "Empty Answers can not be posted.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        String id;
                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        ref = FirebaseDatabase.getInstance().getReference().child("Questions").child(question.getId());
                        ref.child("Answers").setValue(question.getAnswers() + 1);
                        ref = FirebaseDatabase.getInstance().getReference().child("Answers");
                        Date date = new Date();
                        String Date = DateFormat.getDateInstance().format(date);
                        String Time = DateFormat.getTimeInstance().format(date);
                        id = ref.push().getKey();
                        HashMap<String, Object> ma = new HashMap<>();
                        ma.put("UserId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        ma.put("QueId", question.getId());
                        ma.put("string", input);
                        ma.put("date", Date);
                        ma.put("time", Time);
                        ma.put("id", id);
                        ref.child(id).setValue(ma);
                        holder.reply.setText("");
                        Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();
                    }
                    return true; // consume.
                }
                else
                {
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return questionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView ques;

        public EditText reply;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            ques = itemView.findViewById(R.id.question);
            reply = itemView.findViewById(R.id.reply);
        }
    }
}