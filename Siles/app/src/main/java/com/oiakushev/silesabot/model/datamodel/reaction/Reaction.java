package com.oiakushev.silesabot.model.datamodel.reaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Reaction implements Serializable {
	private static final long serialVersionUID = 2280227280422508951L;
	
	private List<String> patternTags;
	private List<String> reactionsOut;
	private String customClass;

	public Reaction() {
		setReactions(new ArrayList<String> ());
		setPatternTags(new ArrayList<String> ());
	}
	
	public void addReaction(String r) {
		reactionsOut.add(r);
	}
	
	public List<String> getReactions() {
		return reactionsOut;
	}

	public void setReactions(List<String> reactions) {
		this.reactionsOut = reactions;
	}

	public void addPatternTag(String p) {
		patternTags.add(p);
	}
	
	public List<String> getPatternTags() {
		return patternTags;
	}

	public void setPatternTags(List<String> patternTags) {
		this.patternTags = patternTags;
	}
	
	public String getReaction() {
		if (reactionsOut.size() > 0) {
			return reactionsOut.get((int) (Math.random()*reactionsOut.size()));
		} else {
			return "";
		}
	}
	
	public boolean checkStringForReaction(String msg) {
		String msgReNew = msg.trim().toLowerCase();
		
		for (String pTag : patternTags) {
			Pattern pattern = Pattern.compile(pTag);
			Matcher matcher = pattern.matcher(msgReNew);
			
			if (matcher.matches()) {
				return true;
			}
		}
		
		return false;
	}

	public String getPatternFromMsg(String msg) {
		String msgReNew = msg.trim().toLowerCase();
		
		for (String pTag : patternTags) {
			Pattern pattern = Pattern.compile(pTag);
			Matcher matcher = pattern.matcher(msgReNew);
			
			if (matcher.matches()) {
				return pTag;
			}
		}
		
		return null;
	}
	
	public String getCustomClass() {
		return customClass;
	}

	public void setCustomClass(String customClass) {
		this.customClass = customClass;
	}
}
