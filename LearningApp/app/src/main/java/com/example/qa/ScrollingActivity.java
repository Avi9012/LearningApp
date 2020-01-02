package com.example.qa;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.qa.fragments.AllQuestions;
import com.example.qa.fragments.MyQuestions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaeger.library.StatusBarUtil;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ScrollingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private String user_id;
    DatabaseReference reference, ref1,ref2;
    private DrawerLayout dl;
    private CircleImageView img;
    private TextView text1, text2;
    String image, name, email;

    private FirebaseUser fireuser;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_scrolling);
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Q/A");

        TabLayout tablayout = (TabLayout) findViewById(R.id.tab);

        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);

        ViewPagerAdapter viewpagerAdap = new ViewPagerAdapter(getSupportFragmentManager());

        viewpagerAdap.addFragments(new AllQuestions(), "All");

        viewpagerAdap.addFragments(new MyQuestions(), "My");

        viewPager.setAdapter(viewpagerAdap);

        tablayout.setupWithViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScrollingActivity.this, PostQuestion.class);
                intent.putExtra("key", false);
                startActivity(intent);
                finish();
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
        dl.addDrawerListener(ADT);
        ADT.syncState();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("imageUrl");
        ref1=FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("name");
        ref2=FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("Email");
        //Toast.makeText(ScrollingActivity.this, String.valueOf(ref1)+" : "+String.valueOf(ref2)+" : "+String.valueOf(reference), Toast.LENGTH_LONG).show();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                image = dataSnapshot.getValue().toString();
                if(!image.equals("default")) {
                    Glide.with(ScrollingActivity.this).load(image).into(img);
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
                email = dataSnapshot.getValue().toString();
                text2.setText(email);
                //Toast.makeText(ScrollingActivity.this, email, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //Toast.makeText(HomeActivity.this, image+" : "+name+" : "+email, Toast.LENGTH_SHORT).show();
        //Toast.makeText(HomeActivity.this, image+" : "+name+" : "+email+" : "+String.valueOf(user)+" : "+user_id, Toast.LENGTH_SHORT).show();


        //img.setImageURI(user.getPhotoUrl());
        navigationView.setCheckedItem(R.id.QA);


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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id=menuItem.getItemId();
        switch (id) {
            case R.id.nav_home:
                Intent i2 = new Intent(ScrollingActivity.this, HomeActivity.class);
                startActivity(i2);
                finish();
                break;
            case R.id.signout:
                Toast.makeText(ScrollingActivity.this, "signing out..", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                status("Offline");
                Intent i = new Intent(ScrollingActivity.this, LearningApp_activity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
                break;
            case R.id.QA:
                break;
            case R.id.Chatbox:
                Intent i1 = new Intent(ScrollingActivity.this, LoggedIn.class);
                startActivity(i1);
                finish();
                break;
            case R.id.profile:
                Intent i3 = new Intent(ScrollingActivity.this, UserProfile.class);
                startActivity(i3);
                finish();
                break;
        }
        dl.closeDrawer(GravityCompat.START);
        return true;
    }

    public static class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private final ArrayList<Fragment> fragments;

        private final ArrayList<String> titles;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {

            return fragments.size();
        }

        public void addFragments(Fragment fragment, String title) {
            fragments.add(fragment);

            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {

            return titles.get(position);
        }

    }

    public void status(String status) {

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fireuser.getUid());
        HashMap<String, Object> Hmap = new HashMap<>();
        Hmap.put("status", status);
        reference.updateChildren(Hmap);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
