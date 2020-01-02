package com.example.qa;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    TextView forgotpass;
    Button signin,signup;
    EditText user,pass;
    FirebaseAuth mFirebaseauth;
    ProgressDialog mprog;
    private DatabaseReference mdatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        //getSupportActionBar().hide();
        mdatabase=FirebaseDatabase.getInstance().getReference().child("Users");
        mFirebaseauth=FirebaseAuth.getInstance();
        signin=findViewById(R.id.button);
        user=findViewById(R.id.editText5);
        pass=findViewById(R.id.editText4);
        signup=findViewById(R.id.btn_reg);
        forgotpass=findViewById(R.id.textView5);

        /*
        mauthlistener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null && firebaseAuth.getCurrentUser().isEmailVerified()){
                    Intent i=new Intent(MainActivity.this,HomeActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            }
        };


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(getApplicationContext());
        builder.enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toast.makeText(MainActivity.this, "You Got an Error", Toast.LENGTH_SHORT).show();
            }
        });
        builder.addApi(Auth.GOOGLE_SIGN_IN_API, gso);
        mGoogleApiClient= builder
                .build();

        mGooglebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });*/

        mprog=new ProgressDialog(this);


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=user.getText().toString();
                String passw=pass.getText().toString();

                if(username.isEmpty()){
                    user.setError("Please enter the Username!");
                    user.requestFocus();
                }
                else if(passw.isEmpty()){
                    pass.setError("Please enter the Username!");
                    pass.requestFocus();
                }
                else{
                    mprog.setMessage("Signing In");
                    mprog.show();
                    mFirebaseauth.signInWithEmailAndPassword(username,passw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                mprog.dismiss();
                                Toast.makeText(MainActivity.this,"Login Error, Please try again!",Toast.LENGTH_LONG).show();
                            }
                            else{
                                if(mFirebaseauth.getCurrentUser().isEmailVerified()) {
                                    FirebaseUser user = mFirebaseauth.getCurrentUser();
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                                    HashMap<String, Object> Hmap = new HashMap<>();
                                    Hmap.put("emailverify", "True");
                                    reference.updateChildren(Hmap);
                                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                                else
                                {
                                    Toast.makeText(MainActivity.this, "Please verify your email first...", Toast.LENGTH_SHORT).show();
                                }
                                //checkuser();
                            }
                        }
                    });
                }
            }
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
            }
        });

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ForgotPasswordActivity.class));
            }
        });
    }
    /*
    private void checkuser(){
        if(mFirebaseauth.getCurrentUser().isEmailVerified()) {
            mprog.dismiss();
            final String user_id = mFirebaseauth.getCurrentUser().getUid();
            mdatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(user_id)) {
                        Intent i = new Intent(MainActivity.this, HomeActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "You need to set up your account!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            Toast.makeText(MainActivity.this,"Please Verify your Email ID!",Toast.LENGTH_SHORT).show();
            mprog.dismiss();
        }

    }

    /*private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount acc=result.getSignInAccount();
                firebaseAuthWithGoogle(acc);
            } else{

            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseauth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG,"signInWithCredential:onComplete:"+task.isSuccessful());
                        if(!task.isSuccessful()){
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(MainActivity.this,"AuthenticationFailed",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            mdatabase.child(account.getId()).child("isAdmin").setValue("false");
                        }

                        // ...
                    }
                });
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        //mFirebaseauth.addAuthStateListener(mauthlistener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(MainActivity.this,LearningApp_activity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if ( mprog!=null && mprog.isShowing() ){
            mprog.cancel();
        }
    }
}

