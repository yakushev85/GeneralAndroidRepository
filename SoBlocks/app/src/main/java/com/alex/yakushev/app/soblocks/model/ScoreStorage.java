package com.alex.yakushev.app.soblocks.model;

import android.util.Log;

import com.alex.yakushev.app.soblocks.model.ScoreData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Oleksandr on 16-Aug-17.
 */

public class ScoreStorage {
    private static String TAG = "soblocks.ScoreStorage";

    public static int MAX_SCORES = 10;
    public static String SCORE_STORAGE_FILENAME = "scorestorage.dat";

    private File file;

    public ScoreStorage(File f) {
        file = f;
    }

    public void storeScore(Date timeStamp, long score) throws IOException {
        if (score == 0) return;

        List<ScoreData> listOfScore = getListOfScore();
        listOfScore.add(new ScoreData(timeStamp, score));
        Collections.sort(listOfScore);

        while (listOfScore.size() > MAX_SCORES) listOfScore.remove(MAX_SCORES);

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
            for (ScoreData scoreDataItem : listOfScore) {
                fileWriter.write(Long.toString(scoreDataItem.getTimeStamp().getTime())+"\n");
                fileWriter.write(Long.toString(scoreDataItem.getScore())+"\n");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in storeScore ", e);
        } finally {
            if (fileWriter != null) fileWriter.close();
        }
    }

    public List<ScoreData> getListOfScore() {
        List<ScoreData> resList = new ArrayList<>();

        if (!file.exists()) return resList;

        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String curLine = scanner.nextLine();
                if (curLine != null && !curLine.isEmpty()) {
                    long timeStampTime = Long.parseLong(curLine);
                    long score = Long.parseLong(scanner.nextLine());
                    resList.add(new ScoreData(new Date(timeStampTime), score));
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in getListOfScore method ", e);
        } finally {
            if (scanner != null) scanner.close();
        }

        return resList;
    }

    public static List<String> convertToListOfStrings(List<ScoreData> listOfData) {
        List<String> resList = new ArrayList<> ();
        for (ScoreData itemData : listOfData) {
            resList.add(itemData.toHtml());
        }
        return resList;
    }
}
