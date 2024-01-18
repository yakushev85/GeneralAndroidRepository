package com.oiakushev.silesabot.model.datamodel.reaction;

import android.util.Log;

import com.oiakushev.silesabot.model.datamodel.PerceptronModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class WadCustomReaction extends CustomReaction {
    private static final String TAG = PerceptronModel.class.getName();

    public WadCustomReaction(Reaction creaction) {
        super(creaction);
    }

    @Override
    public String getCustomAnswer(String msg) {
        try {
            return getSummaryForCurrentDay();
        } catch (IOException e) {
            Log.i(TAG,e.getMessage());
            return "";
        }
    }

    private String getSummaryForCurrentDay() throws IOException {
        Document doc = Jsoup.connect(URL_WIKI).get();
        Elements mainWad = doc.select("#main-itd");
        Elements titleWad = mainWad.select("h2");
        Elements preWad = mainWad.select("p");

        return titleWad.text() + ". " + preWad.text() + '.';
    }
}
