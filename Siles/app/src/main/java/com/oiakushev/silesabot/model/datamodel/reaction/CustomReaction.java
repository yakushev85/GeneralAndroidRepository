package com.oiakushev.silesabot.model.datamodel.reaction;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class CustomReaction {
	protected static final String URL_WIKI = "https://ru.wikipedia.org/wiki/";

	protected Reaction customReaction;
	private boolean skipFilreringSubValue;
	
	public CustomReaction(Reaction creaction) {
		skipFilreringSubValue = false;
		customReaction = creaction;
	}
	
	public String getSubValue(String m) {
		if (skipFilreringSubValue) {
			return m.trim().toLowerCase();
		} else {
			Pattern pattern = Pattern.compile(customReaction.getPatternFromMsg(m));
			Matcher matcher = pattern.matcher(m.trim().toLowerCase());
			matcher.matches();
			return matcher.group(matcher.groupCount());
		}
	}
	
	abstract public String getCustomAnswer(String msg);

	public boolean isSkipFilreringSubValue() {
		return skipFilreringSubValue;
	}

	public void setSkipFilreringSubValue(boolean skipFilreringSubValue) {
		this.skipFilreringSubValue = skipFilreringSubValue;
	}

	protected String getSectionTextFromMainPage(String cssQuery) throws IOException {
		Document doc = Jsoup.connect(URL_WIKI).get();
		Elements mainDyk = doc.select(cssQuery);
		Elements listDyk = mainDyk.select("ul").select("li");

		List<String> textsDyk = new ArrayList();
		for (Element itemDyk : listDyk) {
			String textDyk = itemDyk.text();

			if (textDyk.trim().endsWith(".")) {
				textsDyk.add(textDyk);
			}
		}

		if (textsDyk.size() > 0) {
			Random rand = new Random();
			int indexDyk = rand.nextInt(textsDyk.size() - 1);

			return textsDyk.get(indexDyk);
		} else {
			return "";
		}
	}
}
