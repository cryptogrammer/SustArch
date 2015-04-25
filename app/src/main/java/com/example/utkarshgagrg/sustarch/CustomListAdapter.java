package com.example.utkarshgagrg.sustarch;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by utkarshgagrg on 4/23/15.
 */

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemName;
    private final Integer[] imageID;

    public CustomListAdapter(Activity context, String[] itemName, Integer[] imageID) {
        super(context, R.layout.list_view_with_images_layout, itemName);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemName = itemName;
        this.imageID = imageID;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_view_with_images_layout, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.itemName);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.itemIcon);

        txtTitle.setText(itemName[position]);
        imageView.setImageResource(imageID[position]);
        return rowView;

    };
}