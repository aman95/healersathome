package com.aman_kumar.bookmarks360;

import android.app.FragmentManager;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.aman_kumar.bookmarks360.activities.SettingsActivity;
import com.aman_kumar.bookmarks360.fragments.MyBookmarks;
import com.aman_kumar.bookmarks360.fragments.NewBookmarks;
import com.aman_kumar.bookmarks360.utils.SharedPrefs;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FragmentManager fragmentManager;
    Toolbar toolbar;
    TextView userEmail, userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*
        Adding HeaderView to Navigation Drawer
         */
        View v = navigationView.inflateHeaderView(R.layout.nav_header_main);
        ((TextView)v.findViewById(R.id.username)).setText(SharedPrefs.getPrefs("userName", "Guest"));
        ((TextView)v.findViewById(R.id.useremail)).setText(SharedPrefs.getPrefs("userEmail","No Email!!!"));
        Picasso.with(this).load(SharedPrefs.getPrefs("userDP", "http://placehold.it/128x128")).into((ImageView)v.findViewById(R.id.userDP));



        fragmentManager = getFragmentManager();

        navigationView.setCheckedItem(R.id.nav_my_bookmarks);
        toolbar.setTitle("My Bookmarks");
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MyBookmarks.newInstance("" , "My Bookmarks"))
                .commit();

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
        getMenuInflater().inflate(R.menu.main, menu);
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
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_my_bookmarks) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, MyBookmarks.newInstance("" + id, "My Bookmarks"))
                    .commit();
            toolbar.setTitle("My Bookmarks");


        } else if (id == R.id.nav_share_bookmarks) {

        } else if (id == R.id.nav_shared_with_me) {

        } else if (id == R.id.nav_starred_bookmarks) {

        } else if (id == R.id.nav_add_bookmark) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, NewBookmarks.newInstance("" + id, "Add Bookmarks"))
                    .commit();
            toolbar.setTitle("Add Bookmarks");

        } else if (id == R.id.nav_account) {

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
