package com.circle.prana.bb.Generic;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.circle.prana.bb.CommonDrawer.AboutUs;
import com.circle.prana.bb.CommonDrawer.HomeScreen;
import com.circle.prana.bb.CommonDrawer.Profile;
import com.circle.prana.bb.CommonDrawer.Transactions;
import com.circle.prana.bb.R;
import com.circle.prana.bb.UnderConstruction;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public abstract class CommonDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener  {

    public NavigationView navigationView;
    public DrawerLayout drawer;
    Toolbar toolbar;
    View headerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        replaceContentLayout(setLayout());
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerLayout = navigationView.getHeaderView(0);

        navigationView.setNavigationItemSelectedListener(this);
        onLayoutCreated();
    }

    public abstract int setLayout();

    public abstract void onLayoutCreated();



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Menu m = navigationView.getMenu();
        int id = item.getItemId();
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

        switch (id) {
            case R.id.nav_home:
                if(this.getWindow().getDecorView().getRootView().toString().contains("HomeScreen") ){
                    drawer.closeDrawer(GravityCompat.START);
                }else{
                startActivity(new Intent(this, HomeScreen.class));
                drawer.closeDrawer(GravityCompat.START);
                }
                break;
            case R.id.nav_view_profile:
                startActivity(new Intent(this, Profile.class));
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_transactions:
                startActivity(new Intent(this, Transactions.class));
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_manage:
                startActivity(new Intent(this, UnderConstruction.class));
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_share:
                try {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    String abc = "BB";
                    String sAux = "\nAmazing Banking. \nDownload:";
                    sAux = sAux + "https://play.google.com/store/apps/details?id=com.circle.prana" +
                            ".flyer";
                    intent.putExtra(Intent.EXTRA_TEXT, abc + sAux);
                    startActivity(Intent.createChooser(intent, ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                drawer.closeDrawer(GravityCompat.START);
                break;


            case R.id.nav_rating:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com" +
                        ".circle.prana.flyer"));
                startActivity(intent);
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_about_us:
                startActivity(new Intent(this, AboutUs.class));
                drawer.closeDrawer(GravityCompat.START);
                break;


            case R.id.nav_contact_us:
                startActivity(new Intent(this, UnderConstruction.class));
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_logout:

                try {

                    startActivity(new Intent(this, UnderConstruction.class));
                    Toast.makeText(this, "Sign out Successful ", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.nav_help_feedback:
                startActivity(new Intent(this, UnderConstruction.class));
                break;

            case R.id.nav_terms:
                startActivity(new Intent(this, UnderConstruction.class));
                break;
        }
        return false;
    }

    private void replaceContentLayout(int sourceId) {
        View contentLayout = findViewById(R.id.drawer_content);
        ViewGroup parent = (ViewGroup) contentLayout.getParent();
        int index = parent.indexOfChild(contentLayout);
        parent.removeView(contentLayout);
        contentLayout = getLayoutInflater().inflate(sourceId, parent, false);
        parent.addView(contentLayout, index);
    }


}