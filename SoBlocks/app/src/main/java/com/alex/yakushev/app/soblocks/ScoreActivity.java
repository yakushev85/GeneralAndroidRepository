package com.alex.yakushev.app.soblocks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.alex.yakushev.app.soblocks.model.ScoreData;
import com.alex.yakushev.app.soblocks.model.ScoreStorage;

import java.io.File;
import java.util.List;

public class ScoreActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        this.findViewById(R.id.btnBackScore).setOnClickListener(this);

        File scoreFile = new File(getApplicationContext().getFilesDir(),
                ScoreStorage.SCORE_STORAGE_FILENAME);
        ScoreStorage scoreStorage = new ScoreStorage(scoreFile);
        List<ScoreData> listOfScore = scoreStorage.getListOfScore();

        ListView listScore = (ListView) this.findViewById(R.id.listScore);
        listScore.setAdapter(new ScoreArrayAdapter(this, R.layout.item_list, listOfScore));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnBackScore) {
            this.finish();
        }
    }
}
