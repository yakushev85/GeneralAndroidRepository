package com.alex.yakushev.app.soblocks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.alex.yakushev.app.soblocks.model.DrawThread;
import com.alex.yakushev.app.soblocks.model.ScoreStorage;
import com.alex.yakushev.app.soblocks.view.GameCanvas;

import java.io.File;

public class GameActivity extends AppCompatActivity implements DrawThread.DrawThreadListener {
    private static String TAG = "soblocks.GameActivity";

    private GameCanvas gameCanvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameCanvas = (GameCanvas) this.findViewById(R.id.gameCanvas);
        TextView textLevel = (TextView) this.findViewById(R.id.textLevel);
        TextView textScore = (TextView) this.findViewById(R.id.textScore);

        File scoreFile = new File(getApplicationContext().getFilesDir(),
                ScoreStorage.SCORE_STORAGE_FILENAME);

        gameCanvas.setDrawThreadListener(this);
        gameCanvas.setTextLevel(textLevel);
        gameCanvas.setTextScore(textScore);
        gameCanvas.setScoreFile(scoreFile);
        gameCanvas.setBottomOffset(getResources().getDimension(R.dimen.activity_vertical_margin));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.game_pause_menuitem) {
            gameCanvas.toggleThread();
        } else if (id == R.id.game_back) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        gameCanvas.getDrawThread().pause();
        super.onPause();
    }

    @Override
    public void onFinish() {
        Intent intentScoreGame = new Intent(this, ScoreActivity.class);
        startActivity(intentScoreGame);
        finish();
    }
}
