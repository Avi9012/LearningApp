package com.example.qa;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.util.SortedList;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v4.util.Pair;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.navigation.ui.AppBarConfiguration;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.qa.api.ApiClient;
import com.example.qa.api.ApiInterface;
import com.example.qa.model.Article;
import com.example.qa.model.News;
import com.google.android.gms.common.api.Response;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {


    public static final String API_KEY = "dceab6933c3c4c3191aff2a8fbb04a4d";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Article> articles = new ArrayList<>();
    private com.example.qa.Adapter adapter;
    private String TAG = MainActivity.class.getSimpleName();
    //private TextView topHeadline;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout errorLayout;
    private ImageView errorImage;
    private TextView errorTitle, errorMessage;
    private Button btnRetry;


    private AppBarConfiguration mAppBarConfiguration;
    FirebaseUser user;
    //private FirebaseAuth.AuthStateListener mauthlistener;
    private MenuItem menu;
    private String user_id;
    DatabaseReference reference,ref1,ref2;
    private DrawerLayout dl;
    private CircleImageView img;
    private TextView text1, text2;
    String image, name, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        //StatusBarUtil.setTranslucent(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Articles");

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
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        */
        user=FirebaseAuth.getInstance().getCurrentUser();
        user_id = user.getUid();
        status("Online");
        email("True");
        /*
        GoogleSignInAccount acc= GoogleSignIn.getLastSignedInAccount(HomeActivity.this);
        if(acc!=null){
            user_id=acc.getId();
        }
        else{
            user_id=mfirebaseauth.getCurrentUser().getUid();
        }
        */
        menu=findViewById(R.id.signout);
        /*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
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
        //drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open_drawer, R.string.close_drawer);
        //Toast.makeText(HomeActivity.this, String.valueOf(ref1)+" : "+String.valueOf(ref2)+" : "+String.valueOf(reference), Toast.LENGTH_LONG).show();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                image = dataSnapshot.getValue().toString();
                if(!image.equals("default")) {
                    Glide.with(HomeActivity.this).load(image).into(img);
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
                //Toast.makeText(HomeActivity.this, email, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //Toast.makeText(HomeActivity.this, image+" : "+name+" : "+email, Toast.LENGTH_SHORT).show();
        //Toast.makeText(HomeActivity.this, image+" : "+name+" : "+email+" : "+String.valueOf(user)+" : "+user_id, Toast.LENGTH_SHORT).show();


        //img.setImageURI(user.getPhotoUrl());
        navigationView.setCheckedItem(R.id.nav_home);




        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        //topHeadline = findViewById(R.id.topheadelines);
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(HomeActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        onLoadingSwipeRefresh("");

        errorLayout = findViewById(R.id.errorLayout);
        errorImage = findViewById(R.id.errorImage);
        errorTitle = findViewById(R.id.errorTitle);
        errorMessage = findViewById(R.id.errorMessage);
        btnRetry = findViewById(R.id.btnRetry);

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


    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id=menuItem.getItemId();
        switch (id) {
            case R.id.nav_home:
                break;
            case R.id.signout:
                Toast.makeText(HomeActivity.this, "signing out..", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                if(user != null) {
                    status("Offline");
                }
                Intent i = new Intent(HomeActivity.this, LearningApp_activity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
                break;
            case R.id.QA:
                Intent i1 = new Intent(HomeActivity.this, ScrollingActivity.class);
                startActivity(i1);
                break;
            case R.id.Chatbox:
                Intent i2 = new Intent(HomeActivity.this, LoggedIn.class);
                startActivity(i2);
                break;
            case R.id.profile:
                Intent i3 = new Intent(HomeActivity.this, UserProfile.class);
                startActivity(i3);
                break;
        }
        dl.closeDrawer(GravityCompat.START);
        return true;
    }

    public void status(String status) {
        //Toast.makeText(HomeActivity.this, "value of User after: "+String.valueOf(user_id), Toast.LENGTH_SHORT).show();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        //Toast.makeText(HomeActivity.this, String.valueOf(user), Toast.LENGTH_SHORT).show();
        HashMap<String, Object> Hmap = new HashMap<>();
        Hmap.put("status", status);
        reference.updateChildren(Hmap);
    }

    public void email(String status) {

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        HashMap<String, Object> Hmap = new HashMap<>();
        Hmap.put("emailverify", status);
        reference.updateChildren(Hmap);

    }


    public void LoadJson(final String keyword){

        errorLayout.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        String country = Utils.getCountry();
        String language = Utils.getLanguage();

        Call<News> call;

        if (keyword.length() > 0 ){
            call = apiInterface.getNewsSearch(keyword, language, "publishedAt", API_KEY);
        } else {
            call = apiInterface.getNews(country, API_KEY);
        }

        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, retrofit2.Response<News> response) {

                if (response.isSuccessful() && response.body().getArticle() != null){

                    if (!articles.isEmpty()){
                        articles.clear();
                    }

                    articles = response.body().getArticle();
                    adapter = new com.example.qa.Adapter(articles, HomeActivity.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    initListener();

                    //topHeadline.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);


                } else {

                    //topHeadline.setVisibility(View.INVISIBLE);
                    swipeRefreshLayout.setRefreshing(false);

                    String errorCode;
                    switch (response.code()) {
                        case 404:
                            errorCode = "404 not found";
                            break;
                        case 500:
                            errorCode = "500 server broken";
                            break;
                        default:
                            errorCode = "unknown error";
                            break;
                    }

                    showErrorMessage(
                            R.drawable.no_result,
                            "No Result",
                            "Please Try Again!\n"+
                                    errorCode);

                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {

                //topHeadline.setVisibility(View.INVISIBLE);
                swipeRefreshLayout.setRefreshing(false);
                showErrorMessage(
                        R.drawable.oops,
                        "Oops..",
                        "Network failure, Please Try Again\n"+
                                t.toString());

            }
        });
    }



    private void initListener(){

        adapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                ImageView imageView = view.findViewById(R.id.img);
                Intent intent = new Intent(HomeActivity.this, NewsDetailActivity.class);

                Article article = articles.get(position);
                intent.putExtra("url", article.getUrl());
                intent.putExtra("title", article.getTitle());
                intent.putExtra("img",  article.getUrlToImage());
                intent.putExtra("date",  article.getPublishedAt());
                intent.putExtra("source",  article.getSource().getName());
                intent.putExtra("author",  article.getAuthor());

                Pair<View, String> pair = Pair.create((View)imageView, ViewCompat.getTransitionName(imageView));
                ActivityOptionsCompat optionsCompat = (ActivityOptionsCompat) ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this, pair);
                //ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this,
                //        pair
                //);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    startActivity(intent, optionsCompat.toBundle());
                }else {
                    startActivity(intent);
                }

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search Latest News...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 2){
                    onLoadingSwipeRefresh(query);
                }
                else {
                    Toast.makeText(HomeActivity.this, "Type more than two letters!", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchMenuItem.getIcon().setVisible(false, false);

        return true;
    }

    @Override
    public void onRefresh() {
        LoadJson("");
    }

    private void onLoadingSwipeRefresh(final String keyword){

        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        LoadJson(keyword);
                    }
                }
        );

    }

    private void showErrorMessage(int imageView, String title, String message){

        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
        }

        errorImage.setImageResource(imageView);
        errorTitle.setText(title);
        errorMessage.setText(message);

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoadingSwipeRefresh("");
            }
        });

    }

}
