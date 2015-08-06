package com.example.allench.googleimagesearcher.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.allench.googleimagesearcher.R;
import com.example.allench.googleimagesearcher.models.ImageResult;
import com.squareup.picasso.Picasso;

public class ImageDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
        // hide action bar
        getSupportActionBar().hide();
        // get img from Intent
        ImageResult img = (ImageResult) getIntent().getSerializableExtra("img");
        // get ImageView
        ImageView ivImageFull = (ImageView) findViewById(R.id.ivImageFull);
        // bind click event to close layer
        ivImageFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // assign url to ivImageFull
        Picasso.with(this).load(img.url).placeholder(R.drawable.progress_animation).into(ivImageFull);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return false;
    }
}
