package com.iakushev.oleksandr.englishwordsformemory;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.iakushev.english.wordsprogressing.LearnProcessingStorage;
import org.iakushev.english.wordsprogressing.LearnProcessingWord;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import static com.iakushev.oleksandr.englishwordsformemory.MainConstants.AD_APP_ID;
import static com.iakushev.oleksandr.englishwordsformemory.MainConstants.AD_TEST_DEVICE_ID;


public class LearnActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {
    private static final int WORDS_PER_SESSION = 10;
    private static final String TAG = "LearnActivity";

    private LearnProcessingStorage learnStorage;
    private List<LearnProcessingWord> knownWords;
    private List<LearnProcessingWord> failedWords;
    private List<LearnProcessingWord> currentWords;
    private int displayedWordCount;
    private int correctAnswers;
    private TextView taskText;
    private EditText answerEdit;
    private TextView textStatistics;
    private TextToSpeech textToSpeech;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        taskText = (TextView) this.findViewById(R.id.textViewTask);
        answerEdit = (EditText) this.findViewById(R.id.editTextAnswer);
        Button btnNext = (Button) this.findViewById(R.id.buttonNext);
        btnNext.setOnClickListener(this);
        textStatistics = (TextView) this.findViewById(R.id.textStatistics);

        MobileAds.initialize(getApplicationContext(), AD_APP_ID);
        mAdView = (AdView) findViewById(R.id.adViewLearn);
        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AD_TEST_DEVICE_ID)
                .build();
        mAdView.loadAd(adRequest);


        initialTextToVoice();
        initialWordStorage();
        setTaskMessage();
    }

    private void initialTextToVoice() {
        textToSpeech = new TextToSpeech(this, this);
    }

    private void initialWordStorage() {
        currentWords = new ArrayList<> ();
        knownWords = loadWordsFile(MainConstants.KNOWN_WORDS_FILENAME);
        failedWords = loadWordsFile(MainConstants.FAILED_WORDS_FILENAME);

        try {
            learnStorage = new LearnProcessingStorage();
            learnStorage.setKnownWords(knownWords);
            int countOfRandomValues = WORDS_PER_SESSION - failedWords.size();
            if (countOfRandomValues > 0) {
                currentWords.addAll(learnStorage.getRandomUnknownWords(countOfRandomValues));
                if (countOfRandomValues != WORDS_PER_SESSION) {
                    currentWords.addAll(failedWords);
                }
            } else {
                currentWords.addAll(failedWords.subList(0, WORDS_PER_SESSION));
            }

            displayedWordCount = 0;
            correctAnswers = 0;
        } catch (IOException e) {
            showToastMessage(e.getMessage(), Toast.LENGTH_LONG);
            Log.e(TAG, "IO error", e);
        }
    }

    private void setTaskMessage() {
        //Log.d(TAG, "Description:"+currentWords.get(displayedWordCount).getDescription());
        taskText.setText("["+(displayedWordCount+1)+" из "+WORDS_PER_SESSION+"] "
                +currentWords.get(displayedWordCount).getDescription());
        answerEdit.setText("");

        textStatistics.setText("Общая статистика: "+knownWords.size()
                +" из "+learnStorage.getAllWords().size());
    }

    private List<LearnProcessingWord> loadWordsFile(String filename) {
        List<LearnProcessingWord> resList = new ArrayList<>();
        Context context = this.getApplicationContext();

        //Save known words
        File fileKnownWords = new File(context.getFilesDir(), filename);

        if (!fileKnownWords.exists()) {
            Log.i(TAG, "File "+filename+" is not exists!");
            return resList;
        }

        try {
            ObjectInputStream inStream =
                    new ObjectInputStream((new FileInputStream(fileKnownWords)));

            Object readObj = null;
            try {
                while ((readObj = inStream.readObject()) != null) {
                    if (readObj instanceof LearnProcessingWord) {
                        resList.add((LearnProcessingWord) readObj);
                    } else {
                        throw new ClassNotFoundException();
                    }
                }
            } catch (ClassNotFoundException e) {
                showToastMessage(e.getMessage(), Toast.LENGTH_LONG);
                Log.e(TAG, "Can't read "+filename, e);
            } catch (EOFException e) {
                Log.i(TAG, "End of "+filename);
            }

            inStream.close();
        } catch (IOException e) {
            showToastMessage(e.getMessage(), Toast.LENGTH_LONG);
            Log.e(TAG, "Can't load file", e);
        }

        return resList;
    }

    private void saveWordsList(List<LearnProcessingWord> listWords, String filename) {
        Context context = this.getApplicationContext();

        try {
            //Save known words
            File fileWords = new File(context.getFilesDir(), filename);
            ObjectOutputStream outStream =
                    new ObjectOutputStream(new FileOutputStream(fileWords));
            for (LearnProcessingWord item : listWords) {
                outStream.writeObject(item);
                outStream.flush();
            }
            outStream.close();

        } catch (IOException e) {
            showToastMessage(e.getMessage(), Toast.LENGTH_LONG);
            Log.e(TAG, "IO error", e);
        }

    }

    private void saveProgress() {
        Context context = this.getApplicationContext();

        try {
            saveWordsList(knownWords, MainConstants.KNOWN_WORDS_FILENAME);
            saveWordsList(failedWords, MainConstants.FAILED_WORDS_FILENAME);

            //Save total progress
            File fileTotalProgress =
                    new File(context.getFilesDir(), MainConstants.TOTAL_PROGRESS_FILENAME);
            Formatter formatter = new Formatter(fileTotalProgress);
            formatter.format("%f", (knownWords.size()*1.0)/(learnStorage.getAllWords().size()*1.0));
            formatter.close();
        } catch (IOException e) {
            showToastMessage(e.getMessage(), Toast.LENGTH_LONG);
            Log.e(TAG, "IO error", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_learn, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_back) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        String answer = answerEdit.getText().toString().trim();
        LearnProcessingWord questionWord = currentWords.get(displayedWordCount);
        if (answer.equals(questionWord.getWord())) {
            knownWords.add(questionWord);
            correctAnswers++;

            if (failedWords.contains(questionWord)) {
                failedWords.remove(questionWord);
            }

            showToastMessage("Правильно!", Toast.LENGTH_SHORT);
        } else {
            questionWord.incCountAnswer();
            if (!failedWords.contains(questionWord)) {
                failedWords.add(questionWord);
            }

            showToastMessage("Неправильно! Должно быть так "+questionWord.getWord(),
                    Toast.LENGTH_SHORT);
        }

        speakOut(questionWord.getWord());

        displayedWordCount++;

        if (displayedWordCount >= WORDS_PER_SESSION) {
            showToastMessage("Вы правильно ответили на "+
                    correctAnswers+" из "+WORDS_PER_SESSION, Toast.LENGTH_LONG);
            displayedWordCount = 0;
            correctAnswers = 0;
            saveProgress();
            this.finish();
        } else {
            setTaskMessage();
        }
    }

    private void showToastMessage(String txt, int typeLong) {
        Toast toast = Toast.makeText(getApplicationContext(), txt, typeLong);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "US locale not supported!");
            }
        } else {
            Log.e(TAG, "Voice initialization failed!");
        }
    }

    private void speakOut(String txt) {
        if (textToSpeech != null) {
            textToSpeech.speak(txt, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

}
