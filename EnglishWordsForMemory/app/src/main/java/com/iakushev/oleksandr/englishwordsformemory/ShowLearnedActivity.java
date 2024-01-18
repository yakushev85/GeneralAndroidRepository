package com.iakushev.oleksandr.englishwordsformemory;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.iakushev.english.wordsprogressing.LearnProcessingWord;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class ShowLearnedActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private static final String TAG = "ShowLearnedActivity";
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_learned);

        textToSpeech = new TextToSpeech(this, this);

        this.findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ListView listLearned = (ListView) this.findViewById(R.id.listLearnedView);

        List<String> knownWords = loadWordsFileAsString(MainConstants.KNOWN_WORDS_FILENAME);

        listLearned.setAdapter(new WordArrayAdapter(this, R.layout.item_list, knownWords));

        listLearned.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view != null && view instanceof TextView) {
                    String textToSpeak = ((TextView) view).getText().toString();
                    textToSpeak = textToSpeak.substring(0, textToSpeak.indexOf("(")).trim();
                    if (textToSpeech != null) {
                        textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
             }
        });
    }

    private List<String> loadWordsFileAsString(String filename) {
        List<String> resList = new ArrayList<>();
        Context context = this.getApplicationContext();

        //Save known words
        File fileKnownWords = new File(context.getFilesDir(), filename);

        if (!fileKnownWords.exists()) {
            Log.i(TAG, "File " + filename + " is not exists!");
            return resList;
        }

        try {
            ObjectInputStream inStream =
                    new ObjectInputStream((new FileInputStream(fileKnownWords)));

            List<LearnProcessingWord> listWords = new ArrayList<> ();

            Object readObj;
            try {
                while ((readObj = inStream.readObject()) != null) {
                    if (readObj instanceof LearnProcessingWord) {
                        LearnProcessingWord itemWord = (LearnProcessingWord) readObj;
                        listWords.add(itemWord);
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

            Collections.sort(listWords);
            for (LearnProcessingWord item : listWords) {
                resList.add("<b>" + item.getWord() +
                        "(" + item.getCountAnswer() + ")</b> " +
                        "- " + item.getDescription());
            }
        } catch (IOException e) {
            showToastMessage(e.getMessage(), Toast.LENGTH_LONG);
            Log.e(TAG, "Can't load file", e);
        }

        return resList;
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

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
