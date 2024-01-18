package com.iakushev.oleksandr.englishwordsformemory;

import android.content.Context;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.iakushev.english.wordsprogressing.MainWordsStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.iakushev.oleksandr.englishwordsformemory.MainConstants.AD_APP_ID;
import static com.iakushev.oleksandr.englishwordsformemory.MainConstants.AD_TEST_DEVICE_ID;


public class ShowDictionaryActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, View.OnClickListener {
    private static final int MAX_SEARCH_RESULTS = 20;
    private static final String TAG = "ShowDictionaryActivity";
    private TextToSpeech textToSpeech;
    private ListView listDict;
    private EditText seekText;
    private Button btnBack;
    private Button btnSearch;
    private ProgressBar progressBar;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_dictionary);

        textToSpeech = new TextToSpeech(this, this);

        btnBack = (Button) this.findViewById(R.id.buttonDictBack);
        btnBack.setOnClickListener(this);

        btnSearch = (Button) this.findViewById(R.id.buttonSearchDict);
        btnSearch.setOnClickListener(this);

        progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        listDict = (ListView) this.findViewById(R.id.listDictView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (this, R.layout.item_list, (new ArrayList<String>()).toArray(new String[0]));
        listDict.setAdapter(adapter);

        listDict.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view != null && view instanceof TextView) {
                    String textToSpeak = ((TextView) view).getText().toString();
                    textToSpeak = textToSpeak.substring(0, textToSpeak.indexOf(" - ")).trim();
                    if (textToSpeech != null) {
                        textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            }
        });

        seekText = (EditText) this.findViewById(R.id.seekText);

        MobileAds.initialize(getApplicationContext(), AD_APP_ID);
        mAdView = (AdView) findViewById(R.id.adViewDict);
        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AD_TEST_DEVICE_ID)
                .build();
        mAdView.loadAd(adRequest);
    }

    private boolean isRussianLettersIn(String inStr) {
        return inStr.toLowerCase().matches("[а-я](.)*");
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
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == R.id.buttonSearchDict) {
            String filterText = seekText.getText().toString();
            (new SearchInDictionaryAsyncTask(this)).execute(filterText);
        } else if (viewId == R.id.buttonDictBack) {
            finish();
        }
    }

    private class SearchInDictionaryAsyncTask extends AsyncTask<String, Void, List<String>> {
        private Exception exception;
        private Context context;

        public SearchInDictionaryAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            btnBack.setEnabled(false);
//            btnSearch.setEnabled(false);
            btnSearch.setVisibility(View.GONE);
            seekText.setEnabled(false);
        }

        @Override
        protected List<String> doInBackground(String... params) {
            exception = null;
            String searching = params[0];
            List<String> resultSearchList = new ArrayList<> ();

            MainWordsStorage dictStorage = null;
            if (isRussianLettersIn(searching)) {
                dictStorage = new MainWordsStorage();
            } else {
                dictStorage = new MainWordsStorage(
                        MainWordsStorage.getDictionaryFileNameByWord(searching));
            }

            Map<String, String> dictMap = null;
            try {
                if (isRussianLettersIn(searching)) {
                    dictMap = dictStorage.searchByDescription(searching, MAX_SEARCH_RESULTS);
                } else {
                    dictMap = dictStorage.searchByWord(searching, MAX_SEARCH_RESULTS);
                }
                for (String keyWord : dictMap.keySet()) {
                    String key = keyWord.replaceAll(searching, "<u>"+searching+"</u>");
                    String description = dictMap.get(keyWord)
                            .replaceAll(searching, "<u>"+searching+"</u>");
                    resultSearchList.add("<b>" + key + "</b> - " + description);
                }
            } catch (IOException e) {
                exception = e;
            }

            return resultSearchList;
        }

        @Override
        protected void onPostExecute(List<String> resultSearchList) {
            listDict.setAdapter(new WordArrayAdapter(context, R.layout.item_list, resultSearchList));
            progressBar.setVisibility(View.GONE);
            btnBack.setEnabled(true);
//            btnSearch.setEnabled(true);
            btnSearch.setVisibility(View.VISIBLE);
            seekText.setEnabled(true);
            listDict.invalidate();

            if (exception != null) {
                Log.e(TAG, "Error while read dictionary ", exception);
                showToastMessage(exception.getMessage());
            }
        }
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

    private void showToastMessage(String txt) {
        Toast toast = Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
}
