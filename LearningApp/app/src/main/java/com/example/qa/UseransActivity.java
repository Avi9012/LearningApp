package com.example.qa;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.qa.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

public class UseransActivity extends AppCompatActivity {

    TextView username, email;
    CircleImageView profile_image;
    String users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        setContentView(R.layout.activity_userans);

        getSupportActionBar().hide();
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
        Intent intent = getIntent();
        String emails = intent.getStringExtra("email");
        String names = intent.getStringExtra("name");
        String imageUrls = intent.getStringExtra("imageUrl");
        email.setText(emails);
        username.setText(names);
        if(!imageUrls.equals("defaults")) {
            Glide.with(getApplicationContext()).load(imageUrls).into(profile_image);
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

    public void onBackPressed() {
        super.onBackPressed();
    }
}
