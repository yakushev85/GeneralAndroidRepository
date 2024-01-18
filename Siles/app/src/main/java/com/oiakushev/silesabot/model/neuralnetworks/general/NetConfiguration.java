package com.oiakushev.silesabot.model.neuralnetworks.general;

import java.io.Serializable;
import java.util.List;

public class NetConfiguration implements Serializable {
    private int inCount;
    private int[] neuronCounts;
    private int maxLearningIterations;
    private double initialWeightValue;
    private double alpha, speed;
    private List<TeachDataEntity> teachData;

    public int getInCount() {
        return inCount;
    }

    public void setInCount(int inCount) {
        this.inCount = inCount;
    }

    public int[] getNeuronCounts() {
        return neuronCounts;
    }

    public void setNeuronCounts(int[] neuronCounts) {
        this.neuronCounts = neuronCounts;
    }

    public List<TeachDataEntity> getTeachData() {
        return teachData;
    }

    public void setTeachData(List<TeachDataEntity> teachData) {
        this.teachData = teachData;
    }

    public int getMaxLearningIterations() {
        return maxLearningIterations;
    }

    public void setMaxLearningIterations(int maxLearningIterations) {
        this.maxLearningIterations = maxLearningIterations;
    }

    public double getInitialWeightValue() {
        return initialWeightValue;
    }

    public void setInitialWeightValue(double initialWeightValue) {
        this.initialWeightValue = initialWeightValue;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
