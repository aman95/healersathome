package com.aman_kumar.bookmarks360.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.aman_kumar.bookmarks360.R;
import com.aman_kumar.bookmarks360.listners.TimePickerFragment;
import com.aman_kumar.bookmarks360.utils.SharedPrefs;
import com.squareup.picasso.Picasso;

public class BookDoctor extends AppCompatActivity {

    RatingBar rating;
    TextView name, clinic, charges, avail;
    ImageView pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_doctor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        name = (TextView) findViewById(R.id.doctor_name);
        clinic = (TextView) findViewById(R.id.clinic_name);
        charges = (TextView) findViewById(R.id.doctor_charges);
        avail = (TextView) findViewById(R.id.is_available);
        rating = (RatingBar)findViewById(R.id.ratingBar);
        pic = (ImageView)findViewById(R.id.doctor_pic);
        Bundle extras = getIntent().getExtras();

        if(extras != null) {
            name.setText(extras.getString("name"));
            clinic.setText(extras.getString("clinic"));
            charges.setText("Visit Charges: Rs."+extras.getInt("charges"));
            avail.setText(extras.getBoolean("isAvailable",true)?"Available Today":"Not available");
            rating.setRating(Float.parseFloat(""+extras.getInt("rating")));

        }

        Picasso.with(this).load("http://i.ebayimg.com/00/s/MTYwMFgxNTAw/z/UxUAAOSwxYxUs9sy/$_35.JPG").fit().into(pic);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void selectTime(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

}
