package com.example.allench.googleimagesearcher.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.allench.googleimagesearcher.R;
import com.example.allench.googleimagesearcher.models.ImageResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageResultsAdapter extends ArrayAdapter<ImageResult> {

     public ImageResultsAdapter(Context context, ArrayList<ImageResult> images) {
        super(context, R.layout.fragment_grid_item, images );
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get data from position
        ImageResult m = getItem(position);

        // lookup recycle
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_grid_item, parent, false);
        }

        // load title
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        tvTitle.setText(String.format("%d.%s", position + 1, Html.fromHtml(m.title)));

        // load image
        ImageView ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
        ivImage.setImageResource(0);
        ivImage.setBackgroundColor(0);
        Picasso.with(getContext()).load(m.tbUrl).placeholder(R.drawable.progress_animation).into(ivImage);

        return convertView;
    }
}
