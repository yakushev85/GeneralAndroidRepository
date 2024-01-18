package com.oiakushev.silesabot.model.datamodel.reaction;

import android.content.Context;
import android.util.Log;

import com.oiakushev.silesabot.model.datamodel.PerceptronModel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReactionModel {
	private static final String TAG = PerceptronModel.class.getName();

	public static final String TXT_CUSTOMCLASSWIWKI = "wiki";
	public static final String TXT_CUSTOMCLASSDYK = "dyk";
	public static final String TXT_CUSTOMCLASSNEWS = "news";
	public static final String TXT_CUSTOMCLASSWAD = "wad";
	public static final String TXT_CUSTOMCLASSMATH = "math";

	private final PerceptronModel perceptronModel;
	private List<Reaction> userReactions;
	private List<Reaction> teachReactions;
	private ArrayList<BackReaction> backReactions;
	private Context context;
	
	public ReactionModel(PerceptronModel pm, List<Reaction> userReactions, List<Reaction> teachReactions) throws IOException {
		perceptronModel = pm;
		setReactions(userReactions, teachReactions);
		loadPerceptronData();
		backReactions = new ArrayList<BackReaction>();
		Log.i(TAG,"Reaction model has been started.");
	}
	
	public void setReactions(List<Reaction> userReactions, List<Reaction> teachReactions) {
		this.userReactions = userReactions;
		this.teachReactions = teachReactions;
	}

	private void loadPerceptronData() throws IOException {
		perceptronModel.setLearningData(teachReactions);

		try {
			perceptronModel.loadPerceptronFromFile();
		} catch (FileNotFoundException e) {
			Log.i(TAG,"File not found!");
			perceptronModel.learn();
		} catch (ClassNotFoundException e) {
			Log.i(TAG,e.getMessage());
		}
	}

	public String getAnswer(String msg) {
		return getAnswerSentence(msg);
	}
	
	public String getAnswerSentence(String sent) {
		// Manual users reactions
		for (Reaction reaction : userReactions) {
			if (reaction.checkStringForReaction(sent)) {
				if (reaction.getCustomClass() == null) {
					return reaction.getReaction();
				} else {
					return getCustomAnswerSentence(sent, reaction, false);
				}
			}
		}
		
		// Teach reactions
		String teachResult = perceptronModel.exam(sent);

		if (teachResult.isEmpty()) {
			backReactions.add(new BackReaction(sent));
		}

		return teachResult;
	}
	
	public String getCustomAnswerSentence(String sentence, Reaction reaction, boolean isSkipSubValue) {
		String customClass = reaction.getCustomClass();

		if (customClass.equals(TXT_CUSTOMCLASSDYK)) {
			CustomReaction dykReaction = new DykCustomReaction(reaction);
			return dykReaction.getCustomAnswer(sentence);
		} else if (customClass.equals(TXT_CUSTOMCLASSNEWS)) {
			CustomReaction newsReaction = new NewsCustomReaction(reaction);
			return newsReaction.getCustomAnswer(sentence);
		} else if (customClass.equals(TXT_CUSTOMCLASSWAD)) {
			CustomReaction wadReaction = new WadCustomReaction(reaction);
			return wadReaction.getCustomAnswer(sentence);
		} else if (customClass.equals(TXT_CUSTOMCLASSWIWKI)) {
			CustomReaction wikiReaction = new WikiCustomReaction(reaction);
			wikiReaction.setSkipFilreringSubValue(isSkipSubValue);
			return wikiReaction.getCustomAnswer(sentence);
		} else if (customClass.equals(TXT_CUSTOMCLASSMATH)) {
			CustomReaction mathReaction = new MathCustomReaction(reaction);
			mathReaction.setSkipFilreringSubValue(isSkipSubValue);
			return mathReaction.getCustomAnswer(sentence);
		}
		
		return "";
	}
	
	public List<Reaction> getTeachReactions() {
		return teachReactions;
	}

	public List<Reaction> getUserReactions() {
		return userReactions;
	}

	public ArrayList<BackReaction> getBackReactions() {
		return backReactions;
	}

	public void removeFirstBackReaction() {
		backReactions.remove(0);
	}
}
