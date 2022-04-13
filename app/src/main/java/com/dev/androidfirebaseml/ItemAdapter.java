package com.dev.androidfirebaseml;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ItemAdapter extends ArrayAdapter<Item> {
    ArrayList<Item> events;

    public ItemAdapter(Context context, int resource, ArrayList<Item> objects) {
        super(context, resource, objects);
        events = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.list_item, parent, false);
        }

        // change background colour for even items, the odd items are unchanged
        if (position % 2 == 0) {
            convertView.setBackgroundColor(Color.parseColor("#e6e6e6"));
        }

        Item event = events.get(position);

        ImageView icon = (ImageView) convertView.findViewById(R.id.imageViewIcon);
        Picasso.get().load(event.getImageUrl()).into(icon);



        TextView textViewTitle = (TextView) convertView.findViewById(R.id.textViewTitle);
        textViewTitle.setText(event.getReader());


        return convertView;
    }

}
