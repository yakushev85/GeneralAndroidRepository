package com.alex.yakushev.app.soblocks.model;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Oleksandr on 16-Aug-17.
 */

public class ScoreData implements Comparable<ScoreData> {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMM-dd-yyyy HH:mm", Locale.US);

    private Date timeStamp;
    private long score;

    public ScoreData(Date t, long s) {
        timeStamp = t;
        score = s;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public String toHtml() {
        String scoreStr = Long.toString(score);
        while (scoreStr.length() < 4) scoreStr = "0"+scoreStr;
        return scoreStr + " (" + DATE_FORMAT.format(timeStamp) + ")";
    }

    @Override
    public int compareTo(@NonNull ScoreData scoreData) {
        return (int) (scoreData.getScore()-this.getScore());
    }
}