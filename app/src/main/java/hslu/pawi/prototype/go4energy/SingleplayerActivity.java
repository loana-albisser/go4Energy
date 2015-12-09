package hslu.pawi.prototype.go4energy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import hslu.pawi.prototype.go4energy.database.DbAdapter;
import hslu.pawi.prototype.go4energy.dto.AnswerDTO;
import hslu.pawi.prototype.go4energy.dto.QuestionDTO;


/**
 * Created by Loana on 13.11.2015.
 */
public class SingleplayerActivity extends AppCompatActivity {

    private CountDownTimer countDownTimer;

    private DbAdapter dbAdapter;
    private ArrayList<QuestionDTO>questions = new ArrayList<>();
    private ArrayList<AnswerDTO>answers = new ArrayList<>();

    private String difficultKey = "pDifficulty";

    private int currentQuestion;
    int numberOfQuestions = 3;
    private int numberOfRightAnswers;
    private int questionIndex;
    private int randQuestionId;

    private String rightAnswer;

    private RadioGroup group;

    public SingleplayerActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.deleteDatabase(DbAdapter.DB_NAME);
        dbAdapter = new DbAdapter(getApplicationContext());
        dbAdapter.open();

        setContentView(R.layout.activity_singleplayer_leveloverview);
        GeneratePrivatePreferences();
        setupLevelOverview();
    }

    private void GeneratePrivatePreferences() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(mPrefs.getAll()== null || mPrefs.getAll().size()==0)
        {
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putInt(difficultKey,0);
            editor.commit();
        }
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(difficultKey, 0);
        editor.commit();
    }

    private int getDifficulty(){
        return PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext()).getInt(difficultKey, 0);
    }

    private void setDifficulty(int difficulty){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(difficultKey, difficulty);
        editor.commit();

    }
    @Override
    protected void onRestart() {
        super.onRestart();
        setContentView(R.layout.activity_singleplayer_leveloverview);
        setupLevelOverview();
    }

    @Override
    protected void onStop(){
        super.onStop();
        countDownTimer.cancel();
    }

    /**
     * Creates level overview activity
     */
    private void setupLevelOverview(){
        GridView gridView = (GridView) findViewById(R.id.gridView);
        String[] levelNumber = new String[]{"1", "2","3","4","5","7","8","9","10","11","12","13" };
        final List<String> levelList = new ArrayList<>(Arrays.asList(levelNumber));

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
                if (getDifficulty() == position) {
                    textView.setBackgroundColor(Color.YELLOW);
                } else if (getDifficulty() < position) {
                    textView.setBackgroundColor(Color.RED);
                } else {
                    textView.setBackgroundColor(Color.WHITE);
                }
                return textView;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position < getDifficulty() + 1) {
                    questions = new ArrayList<>(dbAdapter.getAllQuestionsByDifficulty(position));
                    setContentView(R.layout.activity_question);
                    numberOfRightAnswers = 0;
                    currentQuestion = 0;
                    setupQuestionActicity();
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SingleplayerActivity.this);
                    alertDialog.setTitle("Ungültig!");
                    alertDialog.setMessage("Dieses Level ist noch nicht verfügbar!");

                    alertDialog.setPositiveButton("OK", null);
                    alertDialog.show();
                }
            }
        });
    }

    /**
     * setup question for selected level
     */
    private void getQuestion() {
        try {
            getRandomQuestionId();
            final TextView questionField = (TextView) findViewById(R.id.txt_question);
            questionField.setText(questions.get(questionIndex).getDescription());
            answers = new ArrayList<>(dbAdapter.getAllAnswersByQuestion(randQuestionId));

            group = (RadioGroup) findViewById(R.id.rd_answers);

            for(int i = 0; i < answers.size(); i++) {
                RadioButton answersOptions = new RadioButton(this);
                answersOptions.setText(answers.get(i).getValue());
                group.addView(answersOptions);
                answersOptions.setId(i);
                if (answers.get(i).isCorrect()){
                    rightAnswer = answers.get(i).getValue();
                }
            }
            //nohting should be selected firsthand
            //group.check(0);

        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    /**
     * checks whether the answer is right or wrong
     */
    private void checkAnswer(){
        final RelativeLayout relLayout = (RelativeLayout) findViewById(R.id.relLayout);
        TextView answer = (TextView)findViewById(R.id.txt_eval);
        TextView infoText = (TextView)findViewById(R.id.txt_answer);
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        Button button = (Button)findViewById(R.id.btn_next);
        LinearLayout ll_information =(LinearLayout)findViewById(R.id.ll_information);


        boolean right=false;
        int answerIndex = group.getCheckedRadioButtonId();
        if(answerIndex>=0) {
            right = answers.get(answerIndex).isCorrect();
        }

        if(right){
            answer.setText("Ihre Antwort ist Richtig!");
            numberOfRightAnswers ++;
            ll_information.setVisibility(View.INVISIBLE);
            //infoText.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.thumbup);

        }
        else {
            answer.setText("Ihre Antwort ist Falsch!");
            imageView.setVisibility(View.INVISIBLE);
            String info = questions.get(questionIndex).getInformations();
            Linkify.addLinks(infoText, Linkify.WEB_URLS);
            infoText.setMovementMethod(LinkMovementMethod.getInstance());
            infoText.setText("Richtig wäre: " + rightAnswer + "\n\n" + "Weitere Informationen finden Sie unter: \n" + info);
            Linkify.addLinks(infoText, Linkify.WEB_URLS);
            infoText.setMovementMethod(LinkMovementMethod.getInstance());

        }
        questions.remove(questionIndex);

        if (currentQuestion == dbAdapter.getQuestionAmountByDifficulty(getDifficulty())){
            finishLevel();
        }

    }


    private void finishLevel(){
        final RelativeLayout relLayout = (RelativeLayout) findViewById(R.id.relLayout);
        final Button button = (Button)findViewById(R.id.btn_next);
        Button buttonBack = new Button(this);
        float scale =  getResources().getDisplayMetrics().density;
        int dpWidth = (int)(160*scale);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                dpWidth,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        buttonBack.setText("Zur Übersicht");
        buttonBack.setBackgroundResource(R.drawable.button);
        relLayout.addView(buttonBack);
        buttonBack.setLayoutParams(params);
        params.setMargins(20, 20, 20, 20);

        params.addRule(RelativeLayout.ABOVE, button.getId());
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_LEFT);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_singleplayer_leveloverview);
                setupLevelOverview();
            }
        });


        if (numberOfRightAnswers >= (int)(3*0.75)){

            finishLevelDialog("Super!", "Sie haben das Level geschafft!");
            button.setText("Nächstes Level");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int difficulty = getDifficulty() + 1;
                    setDifficulty(difficulty);
                    resetQuestionActivity();
                }
            });

        }
        else {
            finishLevelDialog("Probieren Sie es nocheinmal!","Sie haben das Level leider nicht geschafft!");
            button.setText("Level nocheinmal spielen");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetQuestionActivity();
                }
            });
        }


    }


    /**
     * Shows up when level is finished
     * @param title title of the alert dialog
     * @param message message of the alert dialog
     */
    private void finishLevelDialog(String title, String message){
        AlertDialog alertDialog = new AlertDialog.Builder(SingleplayerActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void resetQuestionActivity(){
        questions = new ArrayList<>(dbAdapter.getAllQuestionsByDifficulty(getDifficulty()));
        setContentView(R.layout.activity_question);
        currentQuestion = 0;
        numberOfRightAnswers = 0;
        setupQuestionActicity();
    }

    /**
     * gets a random question id by level
     */
    private void getRandomQuestionId() {
        Random random = new Random();
        questionIndex = random.nextInt(questions.size());
        randQuestionId = questions.get(questionIndex).getId();
    }

    private void setupQuestionActicity(){
        getQuestion();
        currentQuestion ++;
        countDownTimer = new MyCountDownTimer(10000, 1000);
        countDownTimer.start();
    }

    /**
     * Method for converting DP value to pixels
     */
    private static int getPixelsFromDPs(Activity activity){
        Resources r = activity.getResources();
        return (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 75, r.getDisplayMetrics()));
    }

    public void chooseAnswer(View view){
        setContentView(R.layout.activity_answer);
        checkAnswer();
        countDownTimer.cancel();
    }

    public void nextQuestion(View view) {
        setContentView(R.layout.activity_question);
        setupQuestionActicity();
    }

    private class MyCountDownTimer extends CountDownTimer {
        final TextView counter = (TextView) findViewById(R.id.counter);
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            counter.setText("Verbleibende Zeit: " + millisUntilFinished / 1000);
        }

        @Override
        public void onFinish() {
            AlertDialog alertDialog = new AlertDialog.Builder(SingleplayerActivity.this).create();
            alertDialog.setTitle("Achtung!");
            alertDialog.setMessage("Ihre Zeit ist abgelaufen!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            setContentView(R.layout.activity_answer);
                            checkAnswer();
                        }
                    });
            alertDialog.show();
        }
    }




}

