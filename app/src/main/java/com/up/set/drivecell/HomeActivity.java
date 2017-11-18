package com.up.set.drivecell;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.up.set.drivecell.fragments.PlacesFragment;
import com.up.set.drivecell.fragments.MapFragment;
import com.up.set.drivecell.helper.BottomNavigationViewHelper;
import com.up.set.drivecell.helper.SectionsPagerAdapter;
import com.up.set.drivecell.model.Common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "HomeActivity";

    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager mViewPager;
    private Toolbar toolbar;
    BottomNavigationView bottomNavigationView;

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseAuth.AuthStateListener mAuthListener;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        toolbar = (Toolbar)findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("DriveCell");

        //Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(HomeActivity.this, "Something went wrong....", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
//

        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();


        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);





        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ic_map:
                        changeFragment(0);
                        break;


                    case R.id.ic_places:
                        changeFragment(1);
                        break;



                }
                return false;
            }
        });

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        changeFragment(0);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                    finish();
                }
                // ...
            }
        };

        View headerView = navigationView.getHeaderView(0);
        ImageView headerImage=(ImageView)headerView.findViewById(R.id.imageView);
        TextView txtUserName=(TextView)headerView.findViewById(R.id.txtUsername);
        TextView txtEmail   =(TextView)headerView.findViewById(R.id.txtEmail);

        setNavDetails(headerImage,txtUserName,txtEmail);


        Paper.init(this);

    }
    private void setNavDetails(ImageView headerImage, TextView txtUserName, TextView txtEmail) {
        if(FirebaseAuth.getInstance().getCurrentUser().getProviders()!=null)
        {
            if (FirebaseAuth.getInstance().getCurrentUser().getProviders().get(0).equals("facebook.com")) {

                String name =user.getDisplayName();
                String email=user.getEmail();
                Uri photoUrl=user.getPhotoUrl();

                headerImage.setImageURI(photoUrl);
                txtUserName.setText(name);
                txtEmail.setText(email);


            } else if (FirebaseAuth.getInstance().getCurrentUser().getProviders().get(0).equals("google.com")) {

                String name =user.getDisplayName();
                String email=user.getEmail();
                Uri photoUrl=user.getPhotoUrl();

                headerImage.setImageURI(photoUrl);
                txtUserName.setText(name);
                txtEmail.setText(email);

            }
        }
        else
        {
            String name =user.getDisplayName();
            String email=user.getEmail();

            txtUserName.setText(name);
            txtEmail.setText(email);

        }

    }

    private void changeFragment(int position) {

        Fragment newFragment = null;

        if (position == 0) {
            newFragment = new MapFragment();
            Menu menu = bottomNavigationView.getMenu();
            MenuItem menuItem = menu.getItem(0);
            menuItem.setChecked(true);
        } else if (position ==1) {

            Log.d(TAG, "changeFragment:Details");
            newFragment = new PlacesFragment();
            Menu menu = bottomNavigationView.getMenu();
            MenuItem menuItem = menu.getItem(1);
            menuItem.setChecked(true);

        }

        getSupportFragmentManager().beginTransaction().replace(
                R.id.relLayoutMiddle, newFragment)
                .commit();
    }
    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter mAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mAdapter.addFragment(new PlacesFragment());
        mAdapter.addFragment(new MapFragment());


        viewPager.setAdapter(mAdapter);
    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.filter) {
            showFilterDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showFilterDialog()  {
        List<String> options=new ArrayList<>();
        options.addAll(Arrays.asList("Closed Road","Accident","Fire","Robbery","Theft"));

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,options);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.filter_item, null);
        dialogBuilder.setView(dialogView);


        dialogBuilder.setTitle("Filter By");
        dialogBuilder.setMessage("Challenge Name");
        final AlertDialog b = dialogBuilder.create();
        b.show();

        }



    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id ==R.id.logOut) {

        logOut();
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logOut() {
        Log.d(TAG, "logOut: Is Logging out");
        if(FirebaseAuth.getInstance().getCurrentUser().getProviders()!=null)
        {
            if (FirebaseAuth.getInstance().getCurrentUser().getProviders().get(0).equals("facebook.com")) {

                Log.d(TAG, "logOut: Facebook");
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();


            } else if (FirebaseAuth.getInstance().getCurrentUser().getProviders().get(0).equals("google.com")) {
                Log.d(TAG, "logOut: Google");
                FirebaseAuth.getInstance().signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();

            }
        }
        else
        {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();

        }



    }
}
