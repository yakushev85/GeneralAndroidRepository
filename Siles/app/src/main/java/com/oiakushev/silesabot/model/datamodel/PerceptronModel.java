package com.oiakushev.silesabot.model.datamodel;

import android.content.Context;
import android.util.Log;

import com.oiakushev.silesabot.model.datamodel.reaction.Reaction;
import com.oiakushev.silesabot.model.neuralnetworks.general.NetConfiguration;
import com.oiakushev.silesabot.model.neuralnetworks.general.TeachDataEntity;
import com.oiakushev.silesabot.model.neuralnetworks.multilayer.MultiNetwork;


import java.io.*;
import java.util.*;

public class PerceptronModel {
    private static final String TAG = PerceptronModel.class.getName();
    
    private final String fileDataName;
    private final HashSet<String> words;
    private List<Reaction> reactions;
    private MultiNetwork multiNetwork;
    private List<TeachDataEntity> teachDataEntityList;
    private final Context context;

    public PerceptronModel(Context context, String fileDataName) {
        this.context = context;
        this.fileDataName = fileDataName;
        words = new HashSet<String>();
    }

    public void setLearningData(List<Reaction> reactionList) {
        reactions = reactionList;

        for (Reaction reaction : reactions) {
            for (String request : reaction.getPatternTags()) {
                words.addAll(getWords(request));
            }
        }

        int iReaction = 0;
        teachDataEntityList = new ArrayList<TeachDataEntity>();

        for (Reaction reaction : reactions) {
            teachDataEntityList.addAll(getTeachDataEntityFromReaction(reaction, iReaction));
            iReaction++;
        }
    }

    private List<TeachDataEntity> getTeachDataEntityFromReaction(Reaction reaction, int iReaction) {
        List<TeachDataEntity> result = new ArrayList<TeachDataEntity>();
        double[] output = new double[reactions.size()];

        for (int i=0;i<reactions.size();i++) {
            if (i == iReaction) {
                output[i] = 1;
            } else {
                output[i] = 0;
            }
        }

        for (String request : reaction.getPatternTags()) {
            double[] vector = getVectorFromMessage(request);

            result.add(new TeachDataEntity(vector, output));
        }

        return result;
    }

    private Set<String> getWords(String sentence) {
        Set<String> sentenceWords = new HashSet<String>();

        String[] splitResults = sentence.trim().toLowerCase().split("[^а-я]");
        sentenceWords.addAll(Arrays.asList(splitResults));

        return sentenceWords;
    }

    public void learn() throws IOException {
        learn(teachDataEntityList, getInCount(), getNeuronCount());
        savePerceptronIntoFile();
    }

    private int getInCount() {
        return words.size();
    }

    private int getNeuronCount() {
        return reactions.size();
    }

    private void learn(List<TeachDataEntity> teachData, int inCount, int neuronCount) throws IOException {
        if (!isReadyForLearning()) {
            Log.i(TAG,"no data for learning!");
            return;
        }

        NetConfiguration netConfiguration = new NetConfiguration();
        netConfiguration.setInCount(inCount);
        netConfiguration.setTeachData(teachData);
        netConfiguration.setNeuronCounts(new int[] {neuronCount});
        netConfiguration.setMaxLearningIterations(100000);
        netConfiguration.setInitialWeightValue(0.1);
        netConfiguration.setAlpha(0.5);
        netConfiguration.setSpeed(0.1);

        multiNetwork = new MultiNetwork(netConfiguration);

        Log.i(TAG, multiNetwork.toString());
        Log.i(TAG,"Learning...");
        multiNetwork.learn(true);
    }

    public String exam(String msg) {
        if (!isReadyForExam()) {
            Log.i(TAG,"no data for exam!");
            return "";
        }

        double[] result = multiNetwork.execute(getVectorFromMessage(msg));
        return convertExamResultToResponse(result);
    }

    private String convertExamResultToResponse(double[] result) {
        int indexResult = 0;

        for (int i=0;i<result.length;i++) {
            if (result[indexResult] < result[i]) {
                indexResult = i;
            }
        }

        if (result[indexResult] > 0.5) {
            return reactions.get(indexResult).getReaction();
        }

        return "";
    }

    private double[] getVectorFromMessage(String msg) {
        double[] vector = new double[words.size()];
        Set<String> wordsInRequest = getWords(msg);

        int i=0;
        for (String word : words) {
            if (wordsInRequest.contains(word)) {
                vector[i] = 1;
            } else {
                vector[i] = 0;
            }

            i++;
        }

        return vector;
    }

    public boolean isReadyForLearning() {
        return teachDataEntityList != null && !teachDataEntityList.isEmpty();
    }

    public boolean isReadyForExam() {
        return multiNetwork != null;
    }

    public void loadPerceptronFromFile() throws IOException, ClassNotFoundException {
        File filePerceptron = new File(context.getFilesDir(), fileDataName);
        FileInputStream fileInputStream = new FileInputStream(filePerceptron);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        try {
            multiNetwork = (MultiNetwork) objectInputStream.readObject();
        } finally {
            objectInputStream.close();
            fileInputStream.close();
        }
    }

    private void savePerceptronIntoFile() throws IOException {
        if (!isReadyForExam()) {
            return;
        }

        File filePerceptron = new File(context.getFilesDir(), fileDataName);
        FileOutputStream fileOutputStream = new FileOutputStream(filePerceptron);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

        try {
            objectOutputStream.writeObject(multiNetwork);
            objectOutputStream.flush();
        } finally {
            objectOutputStream.close();
            fileOutputStream.close();
        }
    }
}
