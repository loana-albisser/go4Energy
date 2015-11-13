package com.example.loana.go4energy;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Loana on 13.11.2015.
 */
public class SingleplayerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleplayer_leveloverview);

        GridView gridView = (GridView) findViewById(R.id.gridView);
        String[] levelNumber = new String[]{"1", "2","3","4","5","7","8","9","10","11","12","13" };
        final List<String> levelList = new ArrayList(Arrays.asList(levelNumber));

        gridView.setAdapter(new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, levelList) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                );
                textView.setLayoutParams(layoutParams);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textView.getLayoutParams();

                // Set the width/height of GridView Item
                params.width = getPixelsFromDPs(SingleplayerActivity.this, 75);
                params.height = getPixelsFromDPs(SingleplayerActivity.this, 75);

                textView.setLayoutParams(params);
                textView.setGravity(Gravity.CENTER);
                textView.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
                textView.setText(levelList.get(position));
                textView.setBackgroundColor(Color.parseColor("#99ffffff"));

                return textView;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(),selectedItem,Toast.LENGTH_LONG).show();
                }
        });
    }

    // Method for converting DP value to pixels
    public static int getPixelsFromDPs(Activity activity, int dps){
        Resources r = activity.getResources();
        int  px = (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dps, r.getDisplayMetrics()));
        return px;
    }
}
