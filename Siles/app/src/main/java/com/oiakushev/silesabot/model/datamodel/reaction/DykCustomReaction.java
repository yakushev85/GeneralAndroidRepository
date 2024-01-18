package com.oiakushev.silesabot.model.datamodel.reaction;

import android.util.Log;

import com.oiakushev.silesabot.model.datamodel.PerceptronModel;

import java.io.IOException;

public class DykCustomReaction extends CustomReaction {
    private static final String TAG = PerceptronModel.class.getName();

    public DykCustomReaction(Reaction creaction) {
        super(creaction);
    }

    @Override
    public String getCustomAnswer(String msg) {
        try {
            return getSectionTextFromMainPage("#main-dyk");
        } catch (IOException e) {
            Log.i(TAG,e.getMessage());
            return "";
        }
    }
}
