package com.example.qa;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.qa.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.jaeger.library.StatusBarUtil;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    TextView username, email;
    CircleImageView profile_image;

    DatabaseReference reference;
    String users;

    StorageReference sreference;
    private Uri uri;
    private StorageTask stask;
    private static final int IMAGE_REQUEST = 1;

    FirebaseUser fireuser;

    private String user_id;
    DatabaseReference ref1,ref2;
    private DrawerLayout dl;
    private CircleImageView img;
    private TextView text1, text2;
    String image, name, email1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Profile");

        username = (TextView) findViewById(R.id.username);

        email = (TextView)findViewById(R.id.email);

        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        /*
        GoogleSignInAccount acc= GoogleSignIn.getLastSignedInAccount(UserProfile.this);
        if(acc!=null){
            users = acc.getId();
        }
        else{
            users = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        */
        users = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(users);

        sreference = FirebaseStorage.getInstance().getReference("Uploads");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getName());
                email.setText(user.getEmail());

                if (user.getImageUrl().equals("default")) {
                    profile_image.setImageResource(R.mipmap.ic_launcher_round);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageUrl()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openImage();
            }
        });

        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        dl = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        img = (CircleImageView)headerView.findViewById(R.id.imageView);
        text1 = (TextView)headerView.findViewById(R.id.text);
        text2 = (TextView)headerView.findViewById(R.id.textView);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle ADT = new ActionBarDrawerToggle(this, dl, toolbar, R.string.open, R.string.close);
        ADT.getDrawerArrowDrawable().setColor(Color.BLACK);
        dl.addDrawerListener(ADT);
        ADT.syncState();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("imageUrl");
        ref1=FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("name");
        ref2=FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("Email");
        //Toast.makeText(UserProfile.this, String.valueOf(ref1)+" : "+String.valueOf(ref2)+" : "+String.valueOf(reference), Toast.LENGTH_LONG).show();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                image = dataSnapshot.getValue().toString();
                if(!image.equals("default")) {
                    Glide.with(UserProfile.this).load(image).into(img);
                }
                //Toast.makeText(HomeActivity.this, image, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.getValue().toString();
                text1.setText(name);
                //Toast.makeText(HomeActivity.this, name, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                email1 = dataSnapshot.getValue().toString();
                text2.setText(email1);
                //Toast.makeText(UserProfile.this, email1, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //Toast.makeText(HomeActivity.this, image+" : "+name+" : "+email, Toast.LENGTH_SHORT).show();
        //Toast.makeText(HomeActivity.this, image+" : "+name+" : "+email+" : "+String.valueOf(user)+" : "+user_id, Toast.LENGTH_SHORT).show();


        //img.setImageURI(user.getPhotoUrl());
        navigationView.setCheckedItem(R.id.profile);


        fireuser = FirebaseAuth.getInstance().getCurrentUser();


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

    public void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = UserProfile.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void uploading()
    {
        final ProgressDialog pd = new ProgressDialog(UserProfile.this);
        pd.setMessage("Uploading");
        pd.show();

        if(uri != null)
        {
            final StorageReference storageReference1 = sreference.child(System.currentTimeMillis()+"."+getFileExtension(uri));

            stask = storageReference1.putFile(uri);
            stask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri> > (){
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return storageReference1.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if(task.isSuccessful())
                    {
                        Uri downlaoded = task.getResult();
                        String str = downlaoded.toString();
                        reference = FirebaseDatabase.getInstance().getReference("Users").child(users);

                        HashMap<String, Object> Hmap = new HashMap<>();
                        Hmap.put("imageUrl", str);
                        reference.updateChildren(Hmap);

                        pd.dismiss();
                    }
                    else
                    {
                        Toast.makeText(UserProfile.this, "Failed", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(UserProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }
        else
        {
            Toast.makeText(UserProfile.this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){

            uri = data.getData();
            if(stask != null && stask.isInProgress()){

                Toast.makeText(UserProfile.this, "Upload in progress", Toast.LENGTH_SHORT).show();
            }
            else
            {
                uploading();
            }
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id=menuItem.getItemId();
        switch (id) {
            case R.id.nav_home:
                Intent i1 = new Intent(UserProfile.this, HomeActivity.class);
                startActivity(i1);
                break;
            case R.id.signout:
                Toast.makeText(UserProfile.this, "signing out..", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                //status("Offline");
                Intent i = new Intent(UserProfile.this, LearningApp_activity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
                break;
            case R.id.QA:
                Intent i3 = new Intent(UserProfile.this, ScrollingActivity.class);
                startActivity(i3);
                break;
            case R.id.Chatbox:
                Intent i2 = new Intent(UserProfile.this, LoggedIn.class);
                startActivity(i2);
                break;
            case R.id.profile:
                break;
        }
        dl.closeDrawer(GravityCompat.START);
        return true;
    }
}
