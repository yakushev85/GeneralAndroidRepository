package com.oiakushev.silesabot.model.datamodel;

import android.content.Context;

import com.oiakushev.silesabot.model.datamodel.reaction.Reaction;
import com.oiakushev.silesabot.model.datamodel.reaction.ReactionModel;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

public class MainModel {
	public static final String PERCEPTRON_DATA_FILE = "perceptron.dat";

	private final ReactionModel reactionModel;
	private final PerceptronModel perceptronModel;
	private final Context context;
	
	public MainModel(Context context, List<Reaction> userReactions, List<Reaction> teachReactions) throws IOException, XmlPullParserException {
		this.context = context;
		perceptronModel = new PerceptronModel(context, PERCEPTRON_DATA_FILE);
		reactionModel = new ReactionModel(perceptronModel, userReactions, teachReactions);
	}

	public String evaluateAnswer(String userMsg) {
		return reactionModel.getAnswer(userMsg);
	}
	
	public ReactionModel getReactionModel() {
		return reactionModel;
	}

	public PerceptronModel getPerceptronModel() {
		return perceptronModel;
	}

	public Context getContext() {
		return context;
	}
}
