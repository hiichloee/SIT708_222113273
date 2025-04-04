package com.example.quizapp;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuizActivity extends AppCompatActivity{
    private TextView tvQuestion, tvProgress, tvWelcome;;
    private SeekBar progressBar;
    private RadioGroup radioGroup;
    private Button btnSubmit;

    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private String userName;
    private boolean isSeekBarTouched = false; // Prevent users from repeatedly sliding to interfere with logic

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Bind UI components
        tvQuestion = findViewById(R.id.tvQuestion);
        tvProgress = findViewById(R.id.tvProgress);
        tvWelcome = findViewById(R.id.tvWelcome);
        progressBar = findViewById(R.id.progressBar);
        radioGroup = findViewById(R.id.radioGroup);
        btnSubmit = findViewById(R.id.btnSubmit); // Bind progress text

        userName = getIntent().getStringExtra("USER_NAME");
        questions = loadQuestions();

        showQuestion();
        tvWelcome.setText("Welcome " + userName + "!");

        btnSubmit.setOnClickListener(v -> checkAnswer());

        // Listen for progress bar sliding
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // Calculate question index based on progress
                    isSeekBarTouched = true;
                    int newIndex = progress * questions.size() / 100;
                    if (newIndex != currentQuestionIndex) {
                        currentQuestionIndex = newIndex;
                        showQuestion();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekBarTouched = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekBarTouched = false;
            }
        });
    }

    /** Question class */
    public class Question {
        private String title;
        private List<String> options;
        private int correctAnswerIndex;

        public Question(String title, List<String> options, int correctAnswerIndex) {
            this.title = title;
            this.options = options;
            this.correctAnswerIndex = correctAnswerIndex;
        }

        public String getTitle() { return title; }
        public List<String> getOptions() { return options; }
        public int getCorrectAnswerIndex() { return correctAnswerIndex; }
    }

    /** Load questions */
    private List<Question> loadQuestions() {
        List<Question> questionList = new ArrayList<>();
        questionList.add(new Question("What is the main programming language used for Android development?",Arrays.asList("Java", "Swift", "C++"), 0));
        questionList.add(new Question("Which company developed the Android operating system?", Arrays.asList("Apple", "Google", "Microsoft"), 1));
        questionList.add(new Question("What is the name of the file used to define an Android app’s UI layout?",Arrays.asList("styles.xml", "AndroidManifest.xml", "activity_main.xml"), 2));
        questionList.add(new Question("What is the name of the official IDE for Android development?",Arrays.asList("Eclipse", "Android Studio", "Visual Studio"), 1));
        questionList.add(new Question("What is the name of the XML file that contains information about an Android app, such as permissions and activities?",Arrays.asList("strings.xml", "AndroidManifest.xml", "build.gradle"), 1));
        questionList.add(new Question("Which component is used to display a list of items in Android?",Arrays.asList("RecyclerView", "Button", "ImageView"), 0));
        return questionList;
    }

    /** Display the question */
    private void showQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Question question = questions.get(currentQuestionIndex);
            tvQuestion.setText(question.getTitle());
            radioGroup.clearCheck();

            for (int i = 0; i < 3; i++) {
                RadioButton rb = (RadioButton) radioGroup.getChildAt(i);
                rb.setText(question.getOptions().get(i));
                // **Reset option styles**
                rb.setBackgroundResource(R.drawable.custom_radiobutton);
            }

            // Update progress and button
            int progress = (currentQuestionIndex + 1) * 100 / questions.size();
            progressBar.setProgress(progress);
            tvProgress.setText((currentQuestionIndex + 1) + "/" + questions.size());

            btnSubmit.setText("Submit");
            btnSubmit.setOnClickListener(v -> checkAnswer()); // Reset listener

        }
    }

    /** Check the answer */
    private void checkAnswer() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) return;

        RadioButton selectedRb = findViewById(selectedId);
        int selectedIndex = radioGroup.indexOfChild(selectedRb);
        Question currentQuestion = questions.get(currentQuestionIndex);

        // **重置所有选项为默认白色**
        for (int i = 0; i < 3; i++) {
            RadioButton rb = (RadioButton) radioGroup.getChildAt(i);
            rb.setBackgroundResource(R.drawable.custom_radiobutton);
        }

        for (int i = 0; i < 3; i++) {
            RadioButton rb = (RadioButton) radioGroup.getChildAt(i);
            if (i == currentQuestion.getCorrectAnswerIndex()) {
//                rb.setBackgroundColor(getResources().getColor(R.color.custom_green));
                rb.setBackgroundResource(R.drawable.custom_correct_radiobutton);
            } else if (i == selectedIndex) {
//                rb.setBackgroundColor(getResources().getColor(R.color.custom_red));
                rb.setBackgroundResource(R.drawable.custom_wrong_radiobutton);
            } else {
                rb.setBackgroundResource(R.drawable.custom_radiobutton);  // Restore default style for others
            }
        }

        if (selectedIndex == currentQuestion.getCorrectAnswerIndex()) {
            score++;
        }

        btnSubmit.setText("Next");
        btnSubmit.setOnClickListener(v -> nextQuestion());
    }


    /** Move to the next question */
    private void nextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questions.size()) {
            showQuestion();
        } else {
            Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
            intent.putExtra("SCORE", score);
            intent.putExtra("TOTAL", questions.size());
            intent.putExtra("USER_NAME", userName);
            startActivity(intent);
            finish();
        }
    }


}
