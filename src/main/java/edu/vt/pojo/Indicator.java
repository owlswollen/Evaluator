/*
 * Created by Gokce Onen on 2022.2.20
 * Copyright © 2022 Gokce Onen. All rights reserved.
 */
package edu.vt.pojo;

import edu.vt.EntityBeans.ScoreSet;
import edu.vt.controllers.EditorController;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Indicator implements Serializable {

    //===================
    // Instance Variables
    //===================

    // Name of the indicator
    private String name;

    // Description of the indicator
    private String description;

    // Score of the indicator
    private Score score;

    // Parent indicators of this indicator
    private List<Indicator> parentIndicators = new ArrayList<>();

    // Weights of the child indicators of this indicator
    Map<Indicator, Double> childWeights = new HashMap<>();

    // Map of <one compared indicator and the map of <the other compared indicator and their relative weighting>>
    // Holds the pairwise comparison matrix
    // Two maps are used instead of a two-dimensional array
    // In order to access the relative criticality weighting value by its indicators
    Map<Indicator, Map<Indicator, Double>> comparisonMatrix = new HashMap<>();

    // Child indicators of this indicator
    private List<Indicator> childIndicators = new ArrayList<>();

    // Low and high scores given by experts - for leaf indicators only
    private Map<Indicator, Score> evaluatorScores = new HashMap<>();

    // Is this indicator an evaluator
    private boolean isEvaluator = false;

    // Leaf indicator's score set
    private ScoreSet scoreSet;

    /* Calculated values */

    // The integrated weight calculated recursively starting from the root indicator
    // Stored to be used in the calculation of further indicators
    double recursiveWeight = 1;
    private double consistencyRatio;
    double consistencyIndex = 0;
    private Double absoluteWeight;
    private double lambdaMax;
    private boolean calculated;
    private double weight;

    //=============
    // Constructors
    //=============


    public Indicator() {
    }

    public Indicator(String name) {
        this.name = name;
    }

    //==========================
    // Getter and Setter Methods
    //==========================

    public double getConsistencyIndex() {
        return consistencyIndex;
    }

    public double getConsistencyRatio() {
        return consistencyRatio;
    }

    public Map<Indicator, Double> getChildWeights() {
        return childWeights;
    }

    public void setChildWeights(Map<Indicator, Double> childWeights) {
        this.childWeights = childWeights;
    }

    public Map<Indicator, Map<Indicator, Double>> getComparisonMatrix() {
        return comparisonMatrix;
    }

    public void setComparisonMatrix(Map<Indicator, Map<Indicator, Double>> comparisonMatrix) {
        this.comparisonMatrix = comparisonMatrix;
    }

    public void setConsistencyRatio(double consistencyRatio) {
        this.consistencyRatio = consistencyRatio;
    }

    public void setConsistencyIndex(double consistencyIndex) {
        this.consistencyIndex = consistencyIndex;
    }

    public double getLambdaMax() {
        return lambdaMax;
    }

    public void setLambdaMax(double lambdaMax) {
        this.lambdaMax = lambdaMax;
    }

    public List<Indicator> getChildIndicators() {
        return childIndicators;
    }

    public void setChildIndicators(List<Indicator> childIndicators) {
        this.childIndicators = childIndicators;
    }

    public double getRecursiveWeight() {
        return recursiveWeight;
    }

    public void setRecursiveWeight(double recursiveWeight) {
        this.recursiveWeight = recursiveWeight;
    }

    public Double getAbsoluteWeight() {
        return absoluteWeight;
    }

    public void setAbsoluteWeight(Double absoluteWeight) {
        this.absoluteWeight = absoluteWeight;
    }

    public boolean isCalculated() {
        return calculated;
    }

    public void setCalculated(boolean calculated) {
        this.calculated = calculated;
    }

    public boolean isEvaluator() {
        return isEvaluator;
    }

    public void setEvaluator(boolean evaluator) {
        isEvaluator = evaluator;
    }

    public Map<Indicator, Score> getEvaluatorScores() {
        return evaluatorScores;
    }

    public void setEvaluatorScores(Map<Indicator, Score> evaluatorScores) {
        this.evaluatorScores = evaluatorScores;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public List<Indicator> getParentIndicators() {
        return parentIndicators;
    }

    public void setParentIndicators(List<Indicator> parentIndicators) {
        this.parentIndicators = parentIndicators;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ScoreSet getScoreSet() {
        return scoreSet;
    }

    public void setScoreSet(ScoreSet scoreSet) {
        this.scoreSet = scoreSet;
    }

    //=================
    // Instance Methods
    //=================

    // Calculate the indicator's weight recursively using the weights of its parents
    public void calculateWeight() {
        double result = 0;
        if (parentIndicators.isEmpty()) {
            result = 1;
        } else {
            for (Indicator parent : parentIndicators) {
                parent.calculateWeight();
                result += parent.getChildWeight(this) * parent.getWeight();
            }
        }
        weight = result;
    }

    public boolean isConsistent() {
        return getConsistencyRatio() < 0.1;
    }

    public void addParentIndicator(Indicator parent) {
        parentIndicators.add(parent);
        parent.getChildIndicators().add(this);
    }

    public void addChildIndicators(List<Indicator> childIndicators) {
        for (Indicator indicator : childIndicators) {
            addChildIndicator(indicator);
        }
    }

    public void addChildIndicator(Indicator newChild) {
        getChildIndicators().add(newChild);
        newChild.parentIndicators.add(this);

        // Add indicator to the pairwise comparison matrix
        // Generate the row indices
        comparisonMatrix.put(newChild, new HashMap<>());

        // Set 1 to the values on the diagonal of comparison matrix
        comparisonMatrix.get(newChild).put(newChild, 1.0);
    }

    public void compareIndicators(Indicator indicator1, Indicator indicator2, double relativeWeighting) {
        // Get the row index "indicator1" of the comparison matrix
        // Add the map of the "indicator2" to be compared with "indicator1" and their relative weighting
        comparisonMatrix.get(indicator1).put(indicator2, relativeWeighting);

        // Comparison matrix is diagonally symmetric
        comparisonMatrix.get(indicator2).put(indicator1, 1.0 / relativeWeighting);
    }

    // Remove deleted child indicator from the pairwise comparisons matrix
    public void deleteComparisons(Indicator deletedIndicator) {
        // Remove indicator from the pairwise comparisons matrix
        comparisonMatrix.remove(deletedIndicator);
        for (Indicator child : childIndicators) {
            if (comparisonMatrix.get(child) != null) {
                for (Indicator comparedIndicator : comparisonMatrix.get(child).keySet()) {
                    if (comparedIndicator.equals(deletedIndicator)) {
                        comparisonMatrix.get(child).remove(comparedIndicator);
                        break;
                    }
                }
            }
        }
    }

    public void addParentIndicators(List<Indicator> parentIndicators) {
        for (Indicator parent : parentIndicators) {
            addParentIndicator(parent);
        }
    }

    // Add evaluator scores if this is a leaf indicator
    public void addEvaluatorScore(Indicator evaluator, Score score) {
        evaluatorScores.put(evaluator, score);
    }

    protected double getChildWeight(Indicator indicator) {
        return childWeights.get(indicator);
    }

    public void solve() {
        for (Indicator parent : getParentIndicators()) {
            if (!parent.isCalculated()) {
                parent.solve();
            }
        }

        // Calculate sum of each column in pairwise comparison matrix
        Map<Indicator, Double> columnSums = new HashMap<>();
        for (Indicator indicator2 : getChildIndicators()) {
            double columnSum = 0;

            for (Indicator indicator1 : getChildIndicators()) {
                columnSum += comparisonMatrix.get(indicator1).get(indicator2);
            }
            columnSums.put(indicator2, columnSum);
        }

        // Normalize the relative criticality weightings in pairwise comparison matrix
        // (Divide each cell by the sum of its column)
        // Calculate average of each row after normalization = indicatorWeights
        childWeights = new HashMap<>();
        for (Indicator indicator1 : getChildIndicators()) {
            double sum = 0;
            for (Indicator indicator2 : getChildIndicators()) {
                sum += comparisonMatrix.get(indicator1).get(indicator2) / columnSums.get(indicator2);
            }
            childWeights.put(indicator1, sum / getChildIndicators().size());
        }

        // Calculate consistency
        // Consistent if CI > 0.1
        calculateConsistencyIndex();
        calculateConsistencyRatio();
        calculateAbsoluteWeight();
        calculated = true;
        //printSolution();
    }

    private void printSolution() {
        System.out.println();
        System.out.println((isRoot() ? "Root: " : "Indicator: ") + name);
        System.out.println("Lambda_Max: " + lambdaMax);
        System.out.println("CI: " + getConsistencyIndex());
        System.out.println("CR: " + getConsistencyRatio());
        // Results
        System.out.println();
        System.out.println("Child Relative Weights:");
        for (Indicator indicator : getChildIndicators()) {
            System.out.println(indicator.name + " : " + childWeights.get(indicator));
        }

        System.out.println();
        System.out.println("---------------------------------------");
    }

    public void printScore() {
        try {
            System.out.println(this.getName() + "\nLow Score: " + this.getScore().getLow() + "\nHigh Score: " + this.getScore().getHigh() + "\n");
        } catch (NullPointerException ignored) {
        }
    }

    private void calculateAbsoluteWeight() {
        if (this.isRoot()) {
            absoluteWeight = 1.0;
            return;
        }
        double sum = 0;
        for (Indicator parent : parentIndicators) {
            parent.calculateAbsoluteWeight();
            sum += parent.absoluteWeight * parent.getChildWeight(this);
        }
        absoluteWeight = sum;
    }

    public boolean isRoot() {
        return parentIndicators.isEmpty();
    }

    public boolean isLeaf() {
        return childIndicators.size() == 0 || (childIndicators.get(0)).isEvaluator;
    }

    void calculateConsistencyIndex() {
        // Multiply each column with the indicator weight at the corresponding index
        // Calculate sum of each row = weighted sum value
        // Divide each weighted sum value by the corresponding indicator weight
        // Calculate the average all division results = lambda max
        int childCount = getChildIndicators().size();
        lambdaMax = 0;
        for (Indicator indicator1 : getChildIndicators()) {
            double weightedSumValue = 0;
            for (Indicator indicator2 : getChildIndicators()) {
                weightedSumValue += childWeights.get(indicator2) * comparisonMatrix.get(indicator1).get(indicator2);
            }
            lambdaMax += weightedSumValue / childWeights.get(indicator1);
        }
        lambdaMax /= childCount;

        // CI = (lambda max - n) / (n - 1)
        consistencyIndex = (lambdaMax - childCount) / (childCount - 1);
    }

    void calculateConsistencyRatio() {
        // Random Index
        double[] RI = new double[]{0.0, 0.0, 0.0, 0.59, 0.89, 1.11, 1.25, 1.32, 1.41, 1.45, 1.49, 1.51, 1.48, 1.56, 1.57, 1.58};

        // CR = CI / RI
        if (getChildIndicators().size() > 2) {
            consistencyRatio = consistencyIndex / RI[getChildIndicators().size()];
        } else {
            consistencyRatio = 1;
        }
    }
}

