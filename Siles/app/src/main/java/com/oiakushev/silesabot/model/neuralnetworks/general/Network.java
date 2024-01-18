package com.oiakushev.silesabot.model.neuralnetworks.general;

public interface Network {
    double[] execute(double[] inVector);
    void learn(boolean showInfo);
}
