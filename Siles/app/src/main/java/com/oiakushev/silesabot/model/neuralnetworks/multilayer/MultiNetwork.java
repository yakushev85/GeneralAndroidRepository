package com.oiakushev.silesabot.model.neuralnetworks.multilayer;

import com.oiakushev.silesabot.model.neuralnetworks.general.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MultiNetwork implements Network, Serializable {
    private final NetConfiguration configuration;
    private final ArrayList<Layer> layers;

    public MultiNetwork(NetConfiguration configuration) {
        this.configuration = configuration;

        int[] neuronCounts = configuration.getNeuronCounts();
        int preIn = configuration.getInCount();

        this.layers = new ArrayList<>();

        for (int neuronCount : neuronCounts) {
            Layer layer = new Layer(preIn, neuronCount, configuration.getInitialWeightValue());
            layers.add(layer);
            preIn = neuronCount;
        }
    }

    @Override
    public double[] execute(double[] inVector) {
        double[] layerInVector = inVector;

        for (Layer layer : layers) {
            for (Neuron neuron : layer.getNeurons()) {
                neuron.setInVector(layerInVector);
                neuron.generateOutput();
            }

            layerInVector = new double[layer.getNeurons().size()];
            for (int i=0;i<layer.getNeurons().size();i++) {
                layerInVector[i] = layer.getNeurons().get(i).getOutput();
            }
        }

        return layerInVector;
    }

    @Override
    public String toString() {
        return "MultiNetwork{" +
                "layers=" + layers +
                '}';
    }

    @Override
    public void learn(boolean showInfo) {
        int currentIteration = 1;
        double outputError = Double.MAX_VALUE - 1;

        while (outputError > 0 && currentIteration < configuration.getMaxLearningIterations()) {
            outputError = iteration();

            if (showInfo)
                System.out.println(currentIteration + ". error = " + outputError);

            currentIteration++;
        }

        System.out.println("MultiNetwork.learn finished with error = " + outputError +
                " iteration = " + currentIteration);
    }

    private double iteration() {
        double totalErrorSum = 0.0;
        List<TeachDataEntity> learningData = configuration.getTeachData();

        double alphaValue = configuration.getAlpha();
        double speedValue = configuration.getSpeed();

        for (TeachDataEntity teachData : learningData) {
            // step 1 execute net with teach data
            double[] actualOutput = execute(teachData.getVector());
            double[] expectedOutput = teachData.getOutput();

            // step 2 generate sigma for last layer and update errorSum
            for (int j=0;j<actualOutput.length;j++) {
                layers.get(layers.size()-1).getNeurons().get(j).setSigma(
                        -1.0*actualOutput[j]*(1-actualOutput[j])*(expectedOutput[j]-actualOutput[j])
                );

                totalErrorSum += (Math.abs(expectedOutput[j]-actualOutput[j]) > 0.5)? 1.0 : 0.0;
            }

            // step 3 generate sigma for other layers
            for (int i=layers.size()-2;i>=0;i--) {
                for (int j=0;j<layers.get(i).getNeurons().size();j++) {
                    double currentSigma = 0.0;
                    double output = layers.get(i).getNeurons().get(j).getOutput();
                    double preSigma = output*(1-output);
                    List<Neuron> currentLayer = layers.get(i+1).getNeurons();

                    for (Neuron neuron : currentLayer) {
                        currentSigma += neuron.getWeight(j) * neuron.getSigma();
                    }

                    currentSigma = preSigma * currentSigma;

                    layers.get(i).getNeurons().get(j).setSigma(currentSigma);
                }
            }

            // step 4.1 generate delta
            for (int k=0;k<layers.size();k++) {
                double[] output;
                if (k == 0) {
                    output = teachData.getVector();
                } else {
                    output = new double[layers.get(k-1).getNeurons().size()];

                    for (int l=0;l<output.length;l++) {
                        output[l] = layers.get(k-1).getNeurons().get(l).getOutput();
                    }
                }

                for (int j=0;j<layers.get(k).getNeurons().size();j++) {
                    double[] delta = layers.get(k).getNeurons().get(j).getDelta();
                    double[] newDelta = new double[delta.length];

                    for (int i=0;i<delta.length;i++) {
                        double currentSigma = layers.get(k).getNeurons().get(j).getSigma();
                        newDelta[i] =
                                alphaValue*delta[i]+(1-alphaValue)*speedValue*currentSigma*output[i];
                    }

                    layers.get(k).getNeurons().get(j).setDelta(newDelta);
                }
            }

            // step 4.2 update weights
            for (Layer layer : layers) {
                for (Neuron neuron : layer.getNeurons()) {
                    double[] currentDelta = neuron.getDelta();

                    for (int i=0;i<neuron.getDelta().length;i++) {
                        neuron.setWeight(i, neuron.getWeight(i) - currentDelta[i]);
                    }
                }
            }
        }

        return totalErrorSum;
    }
}
