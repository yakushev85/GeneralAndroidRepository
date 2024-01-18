package com.oiakushev.silesabot.model.datamodel.reaction;

import android.util.Log;

import com.oiakushev.silesabot.model.datamodel.PerceptronModel;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WikiCustomReaction extends CustomReaction {
	private static final String TAG = PerceptronModel.class.getName();

	private static final int MAX_LENGTHRESULT = 1000;
	public static final String TXT_NOFOUND = "Я не знаю про ";
	
	public WikiCustomReaction(Reaction creaction) {
		super(creaction);
	}
	
	public String getCustomAnswer(String msg) {
		String findWords = this.getSubValue(msg).replaceAll("\\s", "_");
		
		try {
			String answerTxt = getWikiTextFromSubValue(findWords);
			return (answerTxt != null) ? answerTxt : TXT_NOFOUND + findWords + ".";
		} catch (IOException e) {
			Log.i(TAG,e.getMessage());
			return "";
		}
	}
	
	public String getWikiTextFromSubValue(String subValueText) throws IOException {
		Document doc = Jsoup.connect(URL_WIKI + subValueText).get();
		Elements newsHeadlines = doc.select("p");
        String resTxt = newsHeadlines.text();
        
        if (resTxt.isEmpty()) {
        	return null;
        }
        
        resTxt = resTxt
				.replaceAll("\\[\\d*\\]", "")
				.replaceAll("\\(\\{\\{.*\\}\\}\\)", "");
        
        if (resTxt.length() > MAX_LENGTHRESULT) {
        	resTxt = resTxt.substring(0, MAX_LENGTHRESULT);
        	
        	int lastPoint = resTxt.lastIndexOf('.');
        	
        	resTxt = resTxt.substring(0,lastPoint+1)+"..";
        }
        
        return resTxt;
	}

}
