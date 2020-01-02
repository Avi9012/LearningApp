package com.example.qa;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class RegisterActivity extends AppCompatActivity {

    EditText name,lastname,email,pass;
    Button register,categ;
    DatabaseReference dref;
    FirebaseAuth mfirebaseauth;
    ProgressDialog mprog;
    String[] listitems;
    boolean[] checked;
    private boolean emailverify=false;
    ArrayList<Integer> museritems=new ArrayList<>();
    ArrayList<String> itemsselec=new ArrayList<>();
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        getSupportActionBar().hide();
        name=findViewById(R.id.editText);
        lastname=findViewById(R.id.editText2);
        email=findViewById(R.id.editText3);
        pass=findViewById(R.id.editText6);
        register=findViewById(R.id.button2);
        categ=findViewById(R.id.select_item);
        dref=FirebaseDatabase.getInstance().getReference().child("Users");
        mfirebaseauth=FirebaseAuth.getInstance();
        mprog=new ProgressDialog(this);
        listitems=getResources().getStringArray(R.array.News_Categories);
        checked=new boolean[listitems.length];


        categ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mbuilder=new AlertDialog.Builder(RegisterActivity.this);
                mbuilder.setTitle(R.string.dialog_label);
                mbuilder.setMultiChoiceItems(listitems, checked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            if (!museritems.contains(which)) {
                                museritems.add(which);
                            }
                        } else if (museritems.contains(which)) {
                            int ind=museritems.indexOf(which);
                            museritems.remove(ind);
                        }
                    }
                 });
                mbuilder.setCancelable(false);

                mbuilder.setPositiveButton(getString(R.string.ok_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                               for(int i=0;i<museritems.size();i++)
                               {
                                   itemsselec.add(listitems[museritems.get(i)]);
                               }

                    }
                });

                mbuilder.setNegativeButton(getString(R.string.dismiss_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                               dialog.dismiss();
                    }
                });

                mbuilder.setNeutralButton(getString(R.string.clear_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                           for(int i=0;i<checked.length;i++){
                               checked[i]=false;
                               museritems.clear();
                           }
                    }
                });

                AlertDialog mdialog=mbuilder.create();
                mdialog.show();
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nam=name.getText().toString();
                final String lname=lastname.getText().toString();
                final String emailid=email.getText().toString();
                final String passw=pass.getText().toString();

                if(!emailid.isEmpty()){
                    validornot obj=new validornot(emailid);
                    emailverify=obj.isValidEmail(emailid);
                }

                boolean atleast;

                if(museritems.size()==0){
                    atleast=false;
                }
                else{
                    atleast=true;
                }

                if(nam.isEmpty()){
                    name.setError("Enter Your Name");
                    name.requestFocus();
                }
                else if(lname.isEmpty()){
                    lastname.setError("Enter Your Last Name");
                    lastname.requestFocus();
                }
                else if(emailid.isEmpty()){
                    email.setError("Enter Your EmailId");
                    email.requestFocus();
                }
                else if(passw.isEmpty()){
                    pass.setError("Enter Your Password");
                    pass.requestFocus();
                }
                else if(!atleast){
                    Toast.makeText(RegisterActivity.this,"Please Select atleast 1 category",Toast.LENGTH_SHORT).show();
                }
                else if(!emailverify){
                    Toast.makeText(RegisterActivity.this,"Not a Valid Email",Toast.LENGTH_SHORT).show();
                }
                else if(!(nam.isEmpty() && lname.isEmpty() && emailid.isEmpty() && passw.isEmpty())){


                    mprog.setMessage("Registering......");
                    mprog.show();


                    mfirebaseauth.createUserWithEmailAndPassword(emailid,passw).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                FirebaseUser muser = mfirebaseauth.getCurrentUser();

                                assert muser != null;
                                muser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            mprog.dismiss();
                                            Toast.makeText(RegisterActivity.this,"Email Sent to your email id. Please Verify the Email",Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(RegisterActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                    userid = muser.getUid();
                                    DatabaseReference current_user = dref.child(userid);
                                    current_user.child("name").setValue(nam+" "+lname);
                                    current_user.child("status").setValue("Offline");
                                    current_user.child("emailverify").setValue("False");
                                    current_user.child("imageUrl").setValue("default");
                                    current_user.child("isAdmin").setValue("false");
                                    current_user.child("categories").setValue(itemsselec);
                                    current_user.child("UserId").setValue(userid);
                                    current_user.child("Email").setValue(emailid);
                                    Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                    mprog.dismiss();
                                    Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                    finish();
                                }
                            else{
                                mprog.dismiss();
                                Toast.makeText(RegisterActivity.this,"Register Error, "+userid,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            }
        });

    }

}
