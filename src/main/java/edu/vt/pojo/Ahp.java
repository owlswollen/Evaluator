/*
 * Created by Gokce Onen on 2022.2.20
 * Copyright © 2022 Gokce Onen. All rights reserved.
 */
package edu.vt.pojo;

import java.io.Serializable;
import java.util.*;


public class Ahp implements Serializable {
    // TODO: rename as indicators graph

    //===================
    // Instance Variables
    //===================

    private final Indicator root;
    private List<Indicator> indicatorList = new ArrayList<>();

    //=============
    // Constructors
    //=============

    public Ahp(Indicator root) {
        this.root = root;
    }

    //==========================
    // Getter and Setter Methods
    //==========================

    public Indicator getRoot() {
        return root;
    }

    public List<Indicator> getIndicatorList() {
        indicatorList.clear();
        getListOfAllIndicators(root);
        return indicatorList;
    }

    public void setIndicatorList(List<Indicator> indicatorList) {
        this.indicatorList = indicatorList;
    }

    //=================
    // Instance Methods
    //=================

    //--------------------------
    // Main solver method of AHP
    //--------------------------
    public void solve() {
        // Calculate the child relative weights
        // Calculate lambda max, consistency index, consistency ratio, absolute weight for each indicator in the graph
        bfsSolverForIndicator(root);

        // Calculate child relative weights of the indicators recursively starting from the root indicator
        calculateRecursiveWeight(root);

        // Calculate scores of the indicators recursively starting from the leaf indicators
        calculateRecursiveScores(root);
    }

    //---------------
    // Helper methods
    //---------------

    // Calculate recursive child relative weights of indicators starting from root
    private void calculateRecursiveWeight(Indicator indicator) {
        indicator.calculateWeight();
        for (Indicator childIndicator : indicator.getChildIndicators()) {
            childIndicator.calculateWeight();
        }
    }

    // Calculate recursive scores of indicators starting from leaves
    private void calculateRecursiveScores(Indicator indicator) {
        Score sum = new Score();
        for (Indicator childIndicator : indicator.getChildIndicators()) {
            if (childIndicator.isEvaluator()) {
                sum.add(indicator.getEvaluatorScores().get(childIndicator).multiply(indicator.getChildWeights().get(childIndicator)));
            } else {
                calculateRecursiveScores(childIndicator);
                sum.add(childIndicator.getScore().multiply(indicator.getChildWeight(childIndicator)));
            }
        }
        indicator.setScore(sum);
    }

    private void getListOfAllIndicators(Indicator currentRoot) {
        indicatorList.add(currentRoot);
        for (Indicator child : currentRoot.getChildIndicators()) {
            if (!indicatorList.contains(child)) {
                getListOfAllIndicators(child);
            }
        }
    }

    // Run individual indicator solver for all the indicators in the graph
    private void bfsSolverForIndicator(Indicator indicator) {
        // Using BFS to complete the calculations of the parents before the calculations of children
        Queue<Indicator> queue = new ArrayDeque<>();
        Indicator currentIndicator;
        Set<Indicator> visited = new HashSet<>();

        visited.add(indicator);
        // Root indicator is added to the top of the queue
        queue.add(indicator);

        while (queue.size() != 0) {
            // Remove the top element of the queue
            currentIndicator = queue.poll();

            // Run individual indicator solver for all the indicators in the graph to calculate
            // absolute weight, lambda max, consistency index, and consistency ratio of the indicator
            currentIndicator.solve();

            for (Indicator child : currentIndicator.getChildIndicators()) {
                // Only insert indicators into queue if they have not been explored already
                if (!visited.contains(child)) {
                    visited.add(child);
                    queue.add(child);
                }
            }
        }
        for (Indicator anIndicator : getIndicatorList()) {
            anIndicator.setCalculated(false);
        }
    }

    public void printSolution() {
        System.out.println("");
        System.out.println("Results:");
        System.out.println("");
        System.out.println("*******************************************");
        System.out.println("-------------------------------------------");
        System.out.println("*******************************************");
        printScores();
    }

    private void printScores() {
        for (Indicator indicator : indicatorList) {
            indicator.printScore();
        }
    }

}
