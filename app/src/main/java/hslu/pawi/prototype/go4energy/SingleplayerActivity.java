package hslu.pawi.prototype.go4energy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    private int difficulty;

    private int numberOfQuestions = 5;
    private int questionId;
    private int index;
    private int randQuestionId;
    private int answerIndex;
    private String rightAnswer;

    private AnswerDTO selectedAnswer;

    private ArrayList<Integer> available = new ArrayList<>();
    private Random random;
    private RadioButton answersOptions;
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
        setLevelOverview();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        setContentView(R.layout.activity_singleplayer_leveloverview);
        setLevelOverview();
    }

    @Override
    protected void onStop(){
        super.onStop();
        countDownTimer.cancel();
    }

    public void setLevelOverview(){
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

        setRandomList();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                difficulty = Integer.parseInt(parent.getItemAtPosition(position).toString());
                setContentView(R.layout.activity_question);
                setupQuestionActicity();
            }
        });
    }



    public void getQuestion() {
        try {
            questions = new ArrayList<>(dbAdapter.getAllQuestionsByDifficulty(difficulty));
            questionId = getRandomQuestionId();

            final TextView questionField = (TextView) findViewById(R.id.txt_question);
            questionField.setText(questions.get(questionId).getDescription());

            answers = new ArrayList<>(dbAdapter.getAllAnswersByQuestion(questionId));
            group = (RadioGroup) findViewById(R.id.rd_answers);

            for(int i = 0; i < answers.size(); i++) {
                answersOptions = new RadioButton(this);
                answersOptions.setText(answers.get(i).getValue());
                group.addView(answersOptions);
                answersOptions.setId(i);
                if (answers.get(i).isCorrect()){
                    rightAnswer = answers.get(i).getValue();
                }
            }
            group.check(0);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public void checkAnswer(){
        TextView answer = (TextView)findViewById(R.id.txt_eval);
        TextView infoText = (TextView)findViewById(R.id.txt_answer);
        LinearLayout information = (LinearLayout)findViewById(R.id.ll_information);

        boolean right;
        answerIndex = group.getCheckedRadioButtonId();
        right = answers.get(answerIndex).isCorrect();

        if(right){
            answer.setText("Ihre Antwort ist Richtig!");
            information.setVisibility(View.INVISIBLE);
        }
        else {
            answer.setText("Ihre Antwort ist Falsch!");
            int qid = answers.get(answerIndex).getQid();
            String info = questions.get(qid).getInformations();;
            Linkify.addLinks(infoText, Linkify.WEB_URLS);
            infoText.setMovementMethod(LinkMovementMethod.getInstance());
            infoText.setText("Richtig wäre: " + rightAnswer + "\n\n" + "Weitere Informationen finden Sie unter: \n" + info);
            Linkify.addLinks(infoText, Linkify.WEB_URLS);
            infoText.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }


    public int getRandomQuestionId() {
        index = random.nextInt(available.size());
        randQuestionId = available.get(index);
        available.remove(index);
        return randQuestionId;
    }

    public void setRandomList(){
        random = new Random();
        for(int i=1; i<=numberOfQuestions;i++){
            available.add(i);
        }
    }

    public void setupQuestionActicity(){
        getQuestion();
        countDownTimer = new MyCountDownTimer(10000, 1000);
        countDownTimer.start();

    }

    // Method for converting DP value to pixels
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

    public void nextQuestion(View view){
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

