package com.example.qa.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.qa.HomeActivity;
import com.example.qa.R;
import com.example.qa.UserAdap;
import com.example.qa.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {

    private RecyclerView recyclerview;

    private UserAdap useradap;

    private List<User> users;

    private FirebaseAuth mfirebaseauth;

    String user_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragmentchat, container, false);
        recyclerview = view.findViewById(R.id.recyclerview);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        mfirebaseauth = FirebaseAuth.getInstance();
        /*
        GoogleSignInAccount acc= GoogleSignIn.getLastSignedInAccount(getContext());
        if(acc!=null){
            user_id=acc.getId();
        }
        else{
            user_id=mfirebaseauth.getCurrentUser().getUid();
        }
        */
        user_id = mfirebaseauth.getCurrentUser().getUid();
        users = new ArrayList<>();

        FetchUsers();

        return view;
    }

    private void FetchUsers() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    User user1 = dataSnapshot1.getValue(User.class);

                    assert user1 != null;
                    if(!user_id.equals(user1.getUserId()) && user1.getEmailverify().equals("True"))
                    {
                        users.add(user1);
                    }

                }

                useradap = new UserAdap(getContext(), users, true);

                recyclerview.setAdapter(useradap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    
}
