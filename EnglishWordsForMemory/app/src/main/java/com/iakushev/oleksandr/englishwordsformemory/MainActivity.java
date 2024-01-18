package com.iakushev.oleksandr.englishwordsformemory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private RatingBar rating_intro;
    private TextView text_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rating_intro = (RatingBar) this.findViewById(R.id.ratingBar);
        rating_intro.setIsIndicator(true);
        this.findViewById(R.id.buttonStart).setOnClickListener(this);
        this.findViewById(R.id.buttonVocabulary).setOnClickListener(this);

        text_info = (TextView) this.findViewById(R.id.textViewIntro);

        setIndicatorValue();
    }

    private void setIndicatorValue() {
        Context context = this.getApplicationContext();

        File fileTotalProgress =
                new File(context.getFilesDir(), MainConstants.TOTAL_PROGRESS_FILENAME);

        if (!fileTotalProgress.exists()) {
            Log.i(TAG, "File "+MainConstants.TOTAL_PROGRESS_FILENAME+" is not exists!");
            text_info.setText(getString(R.string.learning_progress_init));
            rating_intro.setRating(0.0f);
            return;
        }

        try {
            Scanner scanner = new Scanner(fileTotalProgress);
            if (scanner.hasNextFloat()) {
                float totalProgress = scanner.nextFloat();
                text_info.setText(getString(R.string.learning_progress)+" "+
                        String.format(Locale.US, "%.2f" ,totalProgress*100)+" %");

                rating_intro.setRating(totalProgress*rating_intro.getNumStars());
            }
            scanner.close();
        } catch (IOException e) {
            Log.e(TAG, "Can't load total progress", e);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Bad number format", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_show_learned) {
            startActivity(new Intent(this, ShowLearnedActivity.class));
        } else if (id == R.id.action_reset) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_title)
                    .setMessage(R.string.dialog_msg)
                    .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            clearKnownWords();
                        }

                    })
                    .setNegativeButton(R.string.dialog_no, null)
                    .show();
        } else if (id == R.id.action_exit) {
            this.finish();
        } else if (id == R.id.action_about) {
            showToastMessage(getString(R.string.about), Toast.LENGTH_LONG);
        }

        return super.onOptionsItemSelected(item);
    }

    private void clearKnownWords() {
        Context context = this.getApplicationContext();

        List<File> listFiles = new ArrayList<>();
        listFiles.add(new File(context.getFilesDir(), MainConstants.TOTAL_PROGRESS_FILENAME));
        listFiles.add(new File(context.getFilesDir(), MainConstants.KNOWN_WORDS_FILENAME));
        listFiles.add(new File(context.getFilesDir(), MainConstants.FAILED_WORDS_FILENAME));

        for (File itemFile : listFiles) {
            if (itemFile.exists()) {
                itemFile.delete();
            }
        }

        showToastMessage(getString(R.string.cleared_history), Toast.LENGTH_SHORT);
        setIndicatorValue();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == R.id.buttonStart) {
            startActivity(new Intent(this, LearnActivity.class));
        } else if (viewId == R.id.buttonVocabulary) {
            startActivity(new Intent(this, ShowDictionaryActivity.class));
        }
    }

    private void showToastMessage(String txt, int typeLong) {
        Toast toast = Toast.makeText(getApplicationContext(), txt, typeLong);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setIndicatorValue();
    }
}
