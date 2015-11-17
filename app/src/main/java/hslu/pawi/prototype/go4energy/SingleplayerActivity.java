package hslu.pawi.prototype.go4energy;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
public class SingleplayerActivity extends AppCompatActivity {


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
                    params.width = getPixelsFromDPs(SingleplayerActivity.this);
                    params.height = getPixelsFromDPs(SingleplayerActivity.this);

                    textView.setLayoutParams(params);
                    textView.setGravity(Gravity.CENTER);
                    textView.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
                    textView.setText(levelList.get(position));
                    textView.setBackground(getResources().getDrawable(R.drawable.button));

                    return textView;
                }
            });

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String selectedItem = parent.getItemAtPosition(position).toString();
            Toast.makeText(getApplicationContext(),selectedItem,Toast.LENGTH_LONG).show();
                setContentView(R.layout.activity_question);
                }
        });
    }

    // Method for converting DP value to pixels
    private static int getPixelsFromDPs(Activity activity){
        Resources r = activity.getResources();
        return (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 75, r.getDisplayMetrics()));
    }

    public void chooseAnswer(View view){
        setContentView(R.layout.activity_answer);
    }

    public void nextQuestion(View view){
        setContentView(R.layout.activity_question);
    }

}

