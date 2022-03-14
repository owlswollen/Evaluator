/*
 * Created by Gokce Onen on 2022.2.20
 * Copyright © 2022 Gokce Onen. All rights reserved.
 */
package edu.vt.pojo;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import java.util.Arrays;

public class SampleProject {

    //===================
    // Instance Variables
    //===================

    // An acyclic graph of Indicators
    private Indicator rootIndicator;

    // A tree table to show the acyclic graph in a tree structure
    // An artificial root to make the actual root visible in tree table
    private TreeNode<Indicator> rootTreeNode;

    // Actual root of the tree table
    private TreeNode<Indicator> actualRootTreeNode;

    // AHP object holding the indicator hierarchy and alternatives
    private Ahp ahp;

    //==========================
    // Getter and Setter Methods
    //==========================

    public Indicator getRootIndicator() {
        return rootIndicator;
    }

    public void setRootIndicator(Indicator rootIndicator) {
        this.rootIndicator = rootIndicator;
    }

    public TreeNode<Indicator> getRootTreeNode() {
        return rootTreeNode;
    }

    public void setRootTreeNode(TreeNode<Indicator> rootTreeNode) {
        this.rootTreeNode = rootTreeNode;
    }

    public TreeNode<Indicator> getActualRootTreeNode() {
        return actualRootTreeNode;
    }

    public void setActualRootTreeNode(TreeNode<Indicator> actualRootTreeNode) {
        this.actualRootTreeNode = actualRootTreeNode;
    }

    public Ahp getAhp() {
        return ahp;
    }

    public void setAhp(Ahp ahp) {
        this.ahp = ahp;
    }

    //=================
    // Instance Methods
    //=================

    /*
     * Create the default acyclic graph
     * and show it in the tree table
     */
    public SampleProject createDefaultGraphAndTree(Indicator rootIndicator, Ahp ahp, TreeNode rootTreeNode, TreeNode actualRootTreeNode) {
        //-----------
        // Indicators
        //-----------

        // Root Indicator
        rootIndicator = new Indicator("Source Code Quality");

        Indicator adaptability = new Indicator("Adaptability");
        Indicator correctness = new Indicator("Correctness");
        Indicator efficiency = new Indicator("Efficiency");
        Indicator maintainability = new Indicator("Maintainability");
        Indicator portability = new Indicator("Portability");
        Indicator reliability = new Indicator("Reliability");
        Indicator reusability = new Indicator("Reusability");
        Indicator testability = new Indicator("Testability");

        // Children of Root
        rootIndicator.addChildIndicators(Arrays.asList(adaptability, correctness, efficiency, maintainability, portability, reliability, reusability, testability));

        // Pairwise Comparisons of Children of Root
        rootIndicator.compareIndicators(adaptability, correctness, 0.12);
        rootIndicator.compareIndicators(adaptability, efficiency, 4.1);
        rootIndicator.compareIndicators(adaptability, maintainability, 0.16);
        rootIndicator.compareIndicators(adaptability, portability, 4.9);
        rootIndicator.compareIndicators(adaptability, reliability, 0.15);
        rootIndicator.compareIndicators(adaptability, reusability, 0.14);
        rootIndicator.compareIndicators(adaptability, testability, 0.18);
        rootIndicator.compareIndicators(correctness, efficiency, 9.0);
        rootIndicator.compareIndicators(correctness, maintainability, 5.3);
        rootIndicator.compareIndicators(correctness, portability, 1.0);
        rootIndicator.compareIndicators(correctness, reliability, 3.5);
        rootIndicator.compareIndicators(correctness, reusability, 6.7);
        rootIndicator.compareIndicators(correctness, testability, 8.5);
        rootIndicator.compareIndicators(efficiency, maintainability, 0.15);
        rootIndicator.compareIndicators(efficiency, portability, 0.18);
        rootIndicator.compareIndicators(efficiency, reliability, 0.16);
        rootIndicator.compareIndicators(efficiency, reusability, 0.15);
        rootIndicator.compareIndicators(efficiency, testability, 2.1);
        rootIndicator.compareIndicators(maintainability, portability, 5.2);
        rootIndicator.compareIndicators(maintainability, reliability, 5.6);
        rootIndicator.compareIndicators(maintainability, reusability, 1.0);
        rootIndicator.compareIndicators(maintainability, testability, 7.4);
        rootIndicator.compareIndicators(portability, reliability, 0.15);
        rootIndicator.compareIndicators(portability, reusability, 0.14);
        rootIndicator.compareIndicators(portability, testability, 1.0);
        rootIndicator.compareIndicators(reliability, reusability, 7.6);
        rootIndicator.compareIndicators(reliability, testability, 6.3);
        rootIndicator.compareIndicators(reusability, testability, 7.7);

        // Adaptability Indicator
        Indicator concurrentDocumentation = new Indicator("Concurrent Documentation");
        Indicator functionalDecomposition = new Indicator("Functional Decomposition");
        Indicator hierarchicalDecomposition = new Indicator("Hierarchical Decomposition");
        Indicator informationHiding = new Indicator("Information Hiding");
        Indicator stepwiseRefinement = new Indicator("Stepwise Refinement");
        Indicator structuredProgramming = new Indicator("Structured Programming");

        // Children of Adaptability
        adaptability.addChildIndicators(Arrays.asList(concurrentDocumentation, functionalDecomposition, hierarchicalDecomposition, informationHiding, stepwiseRefinement, structuredProgramming));

        // Pairwise Comparisons of Children of Adaptability
        adaptability.compareIndicators(concurrentDocumentation, functionalDecomposition, 1.0);
        adaptability.compareIndicators(concurrentDocumentation, hierarchicalDecomposition, 1.0);
        adaptability.compareIndicators(concurrentDocumentation, informationHiding, 1.0);
        adaptability.compareIndicators(concurrentDocumentation, stepwiseRefinement, 1.0);
        adaptability.compareIndicators(concurrentDocumentation, structuredProgramming, 1.0);
        adaptability.compareIndicators(functionalDecomposition, hierarchicalDecomposition, 1.0);
        adaptability.compareIndicators(functionalDecomposition, informationHiding, 1.0);
        adaptability.compareIndicators(functionalDecomposition, stepwiseRefinement, 1.0);
        adaptability.compareIndicators(functionalDecomposition, structuredProgramming, 1.0);
        adaptability.compareIndicators(hierarchicalDecomposition, informationHiding, 1.0);
        adaptability.compareIndicators(hierarchicalDecomposition, stepwiseRefinement, 1.0);
        adaptability.compareIndicators(hierarchicalDecomposition, structuredProgramming, 1.0);
        adaptability.compareIndicators(informationHiding, stepwiseRefinement, 1.0);
        adaptability.compareIndicators(informationHiding, structuredProgramming, 1.0);
        adaptability.compareIndicators(stepwiseRefinement, structuredProgramming, 1.0);

        // Correctness Indicator
        Indicator lifeCycleVerification = new Indicator("Life Cycle Verification");

        // Children of Correctness
        correctness.addChildIndicators(Arrays.asList(hierarchicalDecomposition, lifeCycleVerification, stepwiseRefinement, structuredProgramming));

        // Pairwise Comparisons of Children of Correctness
        correctness.compareIndicators(hierarchicalDecomposition, lifeCycleVerification, 1.0);
        correctness.compareIndicators(hierarchicalDecomposition, stepwiseRefinement, 1.0);
        correctness.compareIndicators(hierarchicalDecomposition, structuredProgramming, 1.0);
        correctness.compareIndicators(lifeCycleVerification, stepwiseRefinement, 1.0);
        correctness.compareIndicators(lifeCycleVerification, structuredProgramming, 1.0);
        correctness.compareIndicators(stepwiseRefinement, structuredProgramming, 1.0);

        // Efficiency Indicator
        Indicator diskStorageEfficiency = new Indicator("Disk Storage Efficiency");
        Indicator executionEfficiency = new Indicator("Execution Efficiency");
        Indicator memoryEfficiency = new Indicator("Memory Efficiency");

        // Children of Efficiency
        efficiency.addChildIndicators(Arrays.asList(diskStorageEfficiency, executionEfficiency, memoryEfficiency));

        // Pairwise Comparisons of Children of Efficiency
        efficiency.compareIndicators(diskStorageEfficiency, executionEfficiency, 1.0);
        efficiency.compareIndicators(diskStorageEfficiency, memoryEfficiency, 1.0);
        efficiency.compareIndicators(executionEfficiency, memoryEfficiency, 1.0);

        // Maintainability Indicator
        // Children of Maintainability
        maintainability.addChildIndicators(Arrays.asList(concurrentDocumentation, functionalDecomposition, hierarchicalDecomposition, informationHiding, stepwiseRefinement, structuredProgramming));

        // Pairwise Comparisons of Children of Maintainability
        maintainability.compareIndicators(concurrentDocumentation, functionalDecomposition, 0.25);
        maintainability.compareIndicators(concurrentDocumentation, hierarchicalDecomposition, 0.17);
        maintainability.compareIndicators(concurrentDocumentation, informationHiding, 0.22);
        maintainability.compareIndicators(concurrentDocumentation, stepwiseRefinement, 0.14);
        maintainability.compareIndicators(concurrentDocumentation, structuredProgramming, 0.32);
        maintainability.compareIndicators(functionalDecomposition, hierarchicalDecomposition, 0.63);
        maintainability.compareIndicators(functionalDecomposition, informationHiding, 0.33);
        maintainability.compareIndicators(functionalDecomposition, stepwiseRefinement, 0.2);
        maintainability.compareIndicators(functionalDecomposition, structuredProgramming, 0.61);
        maintainability.compareIndicators(hierarchicalDecomposition, informationHiding, 0.64);
        maintainability.compareIndicators(hierarchicalDecomposition, stepwiseRefinement, 0.4);
        maintainability.compareIndicators(hierarchicalDecomposition, structuredProgramming, 0.63);
        maintainability.compareIndicators(informationHiding, stepwiseRefinement, 0.28);
        maintainability.compareIndicators(informationHiding, structuredProgramming, 0.62);
        maintainability.compareIndicators(stepwiseRefinement, structuredProgramming, 1.0);

        // Portability Indicator
        // Children of Portability
        portability.addChildIndicators(Arrays.asList(concurrentDocumentation, functionalDecomposition));

        // Pairwise Comparisons of Children of Portability
        portability.compareIndicators(concurrentDocumentation, functionalDecomposition, 1.0);

        // Reliability Indicator
        // Children of Reliability
        reliability.addChildIndicators(Arrays.asList(hierarchicalDecomposition, informationHiding, stepwiseRefinement, structuredProgramming));

        // Pairwise Comparisons of Children of Reliability
        reliability.compareIndicators(hierarchicalDecomposition, informationHiding, 1.0);
        reliability.compareIndicators(hierarchicalDecomposition, stepwiseRefinement, 1.0);
        reliability.compareIndicators(hierarchicalDecomposition, structuredProgramming, 1.0);
        reliability.compareIndicators(informationHiding, stepwiseRefinement, 1.0);
        reliability.compareIndicators(informationHiding, structuredProgramming, 1.0);
        reliability.compareIndicators(stepwiseRefinement, structuredProgramming, 1.0);

        // Reusability Indicator
        // Children of Reusability
        reusability.addChildIndicators(Arrays.asList(concurrentDocumentation, functionalDecomposition, hierarchicalDecomposition, informationHiding));

        // Pairwise Comparisons of Children of Reusability
        reusability.compareIndicators(concurrentDocumentation, functionalDecomposition, 1.0);
        reusability.compareIndicators(concurrentDocumentation, hierarchicalDecomposition, 1.0);
        reusability.compareIndicators(concurrentDocumentation, informationHiding, 1.0);
        reusability.compareIndicators(functionalDecomposition, hierarchicalDecomposition, 1.0);
        reusability.compareIndicators(functionalDecomposition, informationHiding, 1.0);
        reusability.compareIndicators(hierarchicalDecomposition, informationHiding, 1.0);

        // Testability Indicator
        // Children of Testability
        testability.addChildIndicators(Arrays.asList(functionalDecomposition, hierarchicalDecomposition, informationHiding, lifeCycleVerification, stepwiseRefinement, structuredProgramming));

        // Pairwise Comparisons of Children of Testability
        testability.compareIndicators(functionalDecomposition, hierarchicalDecomposition, 1.0);
        testability.compareIndicators(functionalDecomposition, informationHiding, 1.0);
        testability.compareIndicators(functionalDecomposition, lifeCycleVerification, 1.0);
        testability.compareIndicators(functionalDecomposition, stepwiseRefinement, 1.0);
        testability.compareIndicators(functionalDecomposition, structuredProgramming, 1.0);
        testability.compareIndicators(hierarchicalDecomposition, informationHiding, 1.0);
        testability.compareIndicators(hierarchicalDecomposition, lifeCycleVerification, 1.0);
        testability.compareIndicators(hierarchicalDecomposition, stepwiseRefinement, 1.0);
        testability.compareIndicators(hierarchicalDecomposition, structuredProgramming, 1.0);
        testability.compareIndicators(informationHiding, lifeCycleVerification, 1.0);
        testability.compareIndicators(informationHiding, stepwiseRefinement, 1.0);
        testability.compareIndicators(informationHiding, structuredProgramming, 1.0);
        testability.compareIndicators(lifeCycleVerification, stepwiseRefinement, 1.0);
        testability.compareIndicators(lifeCycleVerification, structuredProgramming, 1.0);
        testability.compareIndicators(stepwiseRefinement, structuredProgramming, 1.0);

        // Concurrent Documentation Indicator
        Indicator complexity = new Indicator("Complexity");
        Indicator easeOfChange = new Indicator("Ease of Change");
        Indicator readability = new Indicator("Readability");
        Indicator traceability = new Indicator("Traceability");

        // Children of Concurrent Documentation
        concurrentDocumentation.addChildIndicators(Arrays.asList(complexity, easeOfChange, readability, traceability));

        // Pairwise Comparisons of Children of Concurrent Documentation
        concurrentDocumentation.compareIndicators(complexity, easeOfChange, 1.0);
        concurrentDocumentation.compareIndicators(complexity, readability, 1.0);
        concurrentDocumentation.compareIndicators(complexity, traceability, 1.0);
        concurrentDocumentation.compareIndicators(easeOfChange, readability, 1.0);
        concurrentDocumentation.compareIndicators(easeOfChange, traceability, 1.0);
        concurrentDocumentation.compareIndicators(readability, traceability, 1.0);

        // Functional Decomposition Indicator
        Indicator cohesion = new Indicator("Cohesion");
        Indicator coupling = new Indicator("Coupling");

        // Children of Functional Decomposition
        functionalDecomposition.addChildIndicators(Arrays.asList(cohesion, complexity, coupling, easeOfChange));

        // Pairwise Comparisons of Children of Functional Decomposition
        functionalDecomposition.compareIndicators(cohesion, complexity, 1.0);
        functionalDecomposition.compareIndicators(cohesion, coupling, 1.0);
        functionalDecomposition.compareIndicators(cohesion, easeOfChange, 1.0);
        functionalDecomposition.compareIndicators(complexity, coupling, 1.0);
        functionalDecomposition.compareIndicators(complexity, easeOfChange, 1.0);
        functionalDecomposition.compareIndicators(coupling, easeOfChange, 1.0);

        // Hierarchical Decomposition Indicator
        // Children of Hierarchical Decomposition
        hierarchicalDecomposition.addChildIndicators(Arrays.asList(cohesion, complexity, coupling, easeOfChange));

        // Pairwise Comparisons of Children of Hierarchical Decomposition
        hierarchicalDecomposition.compareIndicators(cohesion, complexity, 1.0);
        hierarchicalDecomposition.compareIndicators(cohesion, coupling, 1.0);
        hierarchicalDecomposition.compareIndicators(cohesion, easeOfChange, 1.0);
        hierarchicalDecomposition.compareIndicators(complexity, coupling, 1.0);
        hierarchicalDecomposition.compareIndicators(complexity, easeOfChange, 1.0);
        hierarchicalDecomposition.compareIndicators(coupling, easeOfChange, 1.0);

        // Information Hiding Indicator
        Indicator wellDefinedInterfaces = new Indicator("Well-Defined Interfaces");

        // Children of Information Hiding
        informationHiding.addChildIndicators(Arrays.asList(cohesion, complexity, coupling, easeOfChange, wellDefinedInterfaces));

        // Pairwise Comparisons of Children of Information Hiding
        informationHiding.compareIndicators(cohesion, complexity, 1.0);
        informationHiding.compareIndicators(cohesion, coupling, 1.0);
        informationHiding.compareIndicators(cohesion, easeOfChange, 1.0);
        informationHiding.compareIndicators(cohesion, wellDefinedInterfaces, 1.0);
        informationHiding.compareIndicators(complexity, coupling, 1.0);
        informationHiding.compareIndicators(complexity, easeOfChange, 1.0);
        informationHiding.compareIndicators(complexity, wellDefinedInterfaces, 1.0);
        informationHiding.compareIndicators(coupling, easeOfChange, 1.0);
        informationHiding.compareIndicators(coupling, wellDefinedInterfaces, 1.0);
        informationHiding.compareIndicators(easeOfChange, wellDefinedInterfaces, 1.0);

        // Stepwise Refinement Indicator
        // Children of Stepwise Refinement
        stepwiseRefinement.addChildIndicators(Arrays.asList(cohesion, complexity, coupling));

        // Pairwise Comparisons of Children of Stepwise Refinement
        stepwiseRefinement.compareIndicators(cohesion, complexity, 1.0);
        stepwiseRefinement.compareIndicators(cohesion, coupling, 1.0);
        stepwiseRefinement.compareIndicators(complexity, coupling, 1.0);

        // Structured Programming Indicator
        // Children of Structured Programming
        structuredProgramming.addChildIndicators(Arrays.asList(complexity, readability));

        // Pairwise Comparisons of Children of Structured Programming
        structuredProgramming.compareIndicators(complexity, readability, 1.0);

        // Life Cycle Verification Indicator
        Indicator earlyErrorDetection = new Indicator("Early Error Detection");
        Indicator visibilityOfBehavior = new Indicator("Visibility of Behavior");

        // Children of Life Cycle Verification
        lifeCycleVerification.addChildIndicators(Arrays.asList(earlyErrorDetection, visibilityOfBehavior));

        // Pairwise Comparisons of Children of Life Cycle Verification
        lifeCycleVerification.compareIndicators(earlyErrorDetection, visibilityOfBehavior, 1.0);

        // Add evaluators to leaf indicators
        Indicator einstein = new Indicator("AlbertEinstein");
        Indicator bonaparte = new Indicator("NapoleonBonaparte");
        Indicator washington = new Indicator("GeorgeWashington");
        Indicator reagan = new Indicator("RonaldReagan");
        Indicator lincoln = new Indicator("AbrahamLincoln");
        einstein.setEvaluator(true);
        bonaparte.setEvaluator(true);
        washington.setEvaluator(true);
        reagan.setEvaluator(true);
        lincoln.setEvaluator(true);

        complexity.addChildIndicators(Arrays.asList(einstein, washington, lincoln));
        complexity.compareIndicators(einstein, washington, 7.3);
        complexity.compareIndicators(einstein, lincoln, 0.38);
        complexity.compareIndicators(washington, lincoln, 6.6);
        complexity.addEvaluatorScore(einstein, new Score(40.0, 75.0));
        complexity.addEvaluatorScore(washington, new Score(78.0, 85.0));
        complexity.addEvaluatorScore(lincoln, new Score(70.0, 75.0));
        complexity.setHasDefaultScores(false);

        easeOfChange.addChildIndicators(Arrays.asList(washington, reagan, lincoln));
        easeOfChange.compareIndicators(washington, reagan, 1.0);
        easeOfChange.compareIndicators(washington, lincoln, 1.0);
        easeOfChange.compareIndicators(reagan, lincoln, 1.0);
        easeOfChange.addEvaluatorScore(washington, new Score(60.0, 75.0));
        easeOfChange.addEvaluatorScore(reagan, new Score(88.0, 92.0));
        easeOfChange.addEvaluatorScore(lincoln, new Score(68.0, 72.0));
        easeOfChange.setHasDefaultScores(false);

        readability.addChildIndicators(Arrays.asList(bonaparte, washington, lincoln));
        readability.compareIndicators(bonaparte, washington, 5.9);
        readability.compareIndicators(bonaparte, lincoln, 0.29);
        readability.compareIndicators(washington, lincoln, 6.2);
        readability.addEvaluatorScore(bonaparte, new Score(90.0, 96.0));
        readability.addEvaluatorScore(washington, new Score(67.0, 77.0));
        readability.addEvaluatorScore(lincoln, new Score(80.0, 94.0));
        readability.setHasDefaultScores(false);

        traceability.addChildIndicators(Arrays.asList(einstein, washington, lincoln));
        traceability.compareIndicators(einstein, washington, 1.0);
        traceability.compareIndicators(einstein, lincoln, 1.0);
        traceability.compareIndicators(washington, lincoln, 1.0);
        traceability.addEvaluatorScore(einstein, new Score(40.0, 60.0));
        traceability.addEvaluatorScore(washington, new Score(78.0, 83.0));
        traceability.addEvaluatorScore(lincoln, new Score(72.0, 78.0));
        traceability.setHasDefaultScores(false);

        cohesion.addChildIndicators(Arrays.asList(einstein, washington, reagan));
        cohesion.compareIndicators(einstein, washington, 5.3);
        cohesion.compareIndicators(einstein, reagan, 7.5);
        cohesion.compareIndicators(washington, reagan, 0.29);
        cohesion.addEvaluatorScore(einstein, new Score(70.0, 90.0));
        cohesion.addEvaluatorScore(washington, new Score(86.0, 93.0));
        cohesion.addEvaluatorScore(reagan, new Score(67.0, 74.0));
        cohesion.setHasDefaultScores(false);

        coupling.addChildIndicators(Arrays.asList(bonaparte, washington, reagan));
        coupling.compareIndicators(bonaparte, washington, 0.15);
        coupling.compareIndicators(bonaparte, reagan, 7.4);
        coupling.compareIndicators(washington, reagan, 5.4);
        coupling.addEvaluatorScore(bonaparte, new Score(78.0, 90.0));
        coupling.addEvaluatorScore(washington, new Score(67.0, 71.0));
        coupling.addEvaluatorScore(reagan, new Score(82.0, 89.0));
        coupling.setHasDefaultScores(false);

        wellDefinedInterfaces.addChildIndicators(Arrays.asList(einstein, bonaparte, lincoln));
        wellDefinedInterfaces.compareIndicators(einstein, bonaparte, 0.57);
        wellDefinedInterfaces.compareIndicators(einstein, lincoln, 0.78);
        wellDefinedInterfaces.compareIndicators(bonaparte, lincoln, 0.7);
        wellDefinedInterfaces.addEvaluatorScore(einstein, new Score(55.0, 75.0));
        wellDefinedInterfaces.addEvaluatorScore(bonaparte, new Score(61.0, 63.0));
        wellDefinedInterfaces.addEvaluatorScore(lincoln, new Score(60.0, 70.0));
        wellDefinedInterfaces.setHasDefaultScores(false);

        earlyErrorDetection.addChildIndicators(Arrays.asList(bonaparte, washington, reagan));
        earlyErrorDetection.compareIndicators(bonaparte, washington, 5.6);
        earlyErrorDetection.compareIndicators(bonaparte, reagan, 0.18);
        earlyErrorDetection.compareIndicators(washington, reagan, 4.1);
        earlyErrorDetection.addEvaluatorScore(bonaparte, new Score(78.0, 85.0));
        earlyErrorDetection.addEvaluatorScore(washington, new Score(80.0, 80.0));
        earlyErrorDetection.addEvaluatorScore(reagan, new Score(89.0, 93.0));
        earlyErrorDetection.setHasDefaultScores(false);

        visibilityOfBehavior.addChildIndicators(Arrays.asList(einstein, bonaparte, reagan));
        visibilityOfBehavior.compareIndicators(einstein, bonaparte, 7.7);
        visibilityOfBehavior.compareIndicators(einstein, reagan, 6.0);
        visibilityOfBehavior.compareIndicators(bonaparte, reagan, 6.3);
        visibilityOfBehavior.addEvaluatorScore(einstein, new Score(30.0, 60.0));
        visibilityOfBehavior.addEvaluatorScore(bonaparte, new Score(73.0, 73.0));
        visibilityOfBehavior.addEvaluatorScore(reagan, new Score(61.0, 61.0));
        visibilityOfBehavior.setHasDefaultScores(false);

        diskStorageEfficiency.addChildIndicators(Arrays.asList(einstein, reagan, lincoln));
        diskStorageEfficiency.compareIndicators(einstein, reagan, 8.7);
        diskStorageEfficiency.compareIndicators(einstein, lincoln, 5.4);
        diskStorageEfficiency.compareIndicators(reagan, lincoln, 0.37);
        diskStorageEfficiency.addEvaluatorScore(einstein, new Score(40.0, 80.0));
        diskStorageEfficiency.addEvaluatorScore(reagan, new Score(87.0, 92.0));
        diskStorageEfficiency.addEvaluatorScore(lincoln, new Score(68.0, 75.0));
        diskStorageEfficiency.setHasDefaultScores(false);

        executionEfficiency.addChildIndicators(Arrays.asList(einstein, bonaparte, washington));
        executionEfficiency.compareIndicators(einstein, bonaparte, 6.0);
        executionEfficiency.compareIndicators(einstein, washington, 5.5);
        executionEfficiency.compareIndicators(bonaparte, washington, 5.3);
        executionEfficiency.addEvaluatorScore(einstein, new Score(60.0, 90.0));
        executionEfficiency.addEvaluatorScore(bonaparte, new Score(90.0, 93.0));
        executionEfficiency.addEvaluatorScore(washington, new Score(80.0, 90.0));
        executionEfficiency.setHasDefaultScores(false);

        memoryEfficiency.addChildIndicators(Arrays.asList(bonaparte, reagan, lincoln));
        memoryEfficiency.compareIndicators(bonaparte, reagan, 1.0);
        memoryEfficiency.compareIndicators(bonaparte, lincoln, 1.0);
        memoryEfficiency.compareIndicators(reagan, lincoln, 1.0);
        memoryEfficiency.addEvaluatorScore(bonaparte, new Score(89.0, 94.0));
        memoryEfficiency.addEvaluatorScore(reagan, new Score(90.0, 90.0));
        memoryEfficiency.addEvaluatorScore(lincoln, new Score(75.0, 82.0));
        memoryEfficiency.setHasDefaultScores(false);

        //-------------
        // AHP Solution
        //-------------
        // Run the AHP algorithm for the default graph
        ahp = new Ahp(rootIndicator);
        ahp.solve();

        //------------------
        // Default TreeTable
        //------------------

        // Create an artificial root node to make the actual root node visible in the tree
        rootTreeNode = new DefaultTreeNode(null, null);
        // Create the actual root
        actualRootTreeNode = new DefaultTreeNode(rootIndicator, rootTreeNode);

        TreeNode<Indicator> adaptabilityOfRoot = new DefaultTreeNode(adaptability, actualRootTreeNode);
        TreeNode<Indicator> correctnessOfRoot = new DefaultTreeNode(correctness, actualRootTreeNode);
        TreeNode<Indicator> efficiencyOfRoot = new DefaultTreeNode(efficiency, actualRootTreeNode);
        TreeNode<Indicator> maintainabilityOfRoot = new DefaultTreeNode(maintainability, actualRootTreeNode);
        TreeNode<Indicator> portabilityOfRoot = new DefaultTreeNode(portability, actualRootTreeNode);
        TreeNode<Indicator> reliabilityOfRoot = new DefaultTreeNode(reliability, actualRootTreeNode);
        TreeNode<Indicator> reusabilityOfRoot = new DefaultTreeNode(reusability, actualRootTreeNode);
        TreeNode<Indicator> testabilityOfRoot = new DefaultTreeNode(testability, actualRootTreeNode);

        TreeNode<Indicator> concurrentDocumentationOfAdaptability = new DefaultTreeNode(concurrentDocumentation, adaptabilityOfRoot);
        TreeNode<Indicator> functionalDecompositionOfAdaptability = new DefaultTreeNode(functionalDecomposition, adaptabilityOfRoot);
        TreeNode<Indicator> hierarchicalDecompositionOfAdaptability = new DefaultTreeNode(hierarchicalDecomposition, adaptabilityOfRoot);
        TreeNode<Indicator> informationHidingOfAdaptability = new DefaultTreeNode(informationHiding, adaptabilityOfRoot);
        TreeNode<Indicator> stepwiseRefinementOfAdaptability = new DefaultTreeNode(stepwiseRefinement, adaptabilityOfRoot);
        TreeNode<Indicator> structuredProgrammingOfAdaptability = new DefaultTreeNode(structuredProgramming, adaptabilityOfRoot);

        TreeNode<Indicator> hierarchicalDecompositionOfCorrectness = new DefaultTreeNode(hierarchicalDecomposition, correctnessOfRoot);
        TreeNode<Indicator> lifeCycleVerificationOfCorrectness = new DefaultTreeNode(lifeCycleVerification, correctnessOfRoot);
        TreeNode<Indicator> stepwiseRefinementOfCorrectness = new DefaultTreeNode(stepwiseRefinement, correctnessOfRoot);
        TreeNode<Indicator> structuredProgrammingOfCorrectness = new DefaultTreeNode(structuredProgramming, correctnessOfRoot);

        efficiencyOfRoot.getChildren().add(new DefaultTreeNode(diskStorageEfficiency));
        efficiencyOfRoot.getChildren().add(new DefaultTreeNode(executionEfficiency));
        efficiencyOfRoot.getChildren().add(new DefaultTreeNode(memoryEfficiency));

        TreeNode<Indicator> concurrentDocumentationOfMaintainability = new DefaultTreeNode(concurrentDocumentation, maintainabilityOfRoot);
        TreeNode<Indicator> functionalDecompositionOfMaintainability = new DefaultTreeNode(functionalDecomposition, maintainabilityOfRoot);
        TreeNode<Indicator> hierarchicalDecompositionOfMaintainability = new DefaultTreeNode(hierarchicalDecomposition, maintainabilityOfRoot);
        TreeNode<Indicator> informationHidingOfMaintainability = new DefaultTreeNode(informationHiding, maintainabilityOfRoot);
        TreeNode<Indicator> stepwiseRefinementOfMaintainability = new DefaultTreeNode(stepwiseRefinement, maintainabilityOfRoot);
        TreeNode<Indicator> structuredProgrammingOfMaintainability = new DefaultTreeNode(structuredProgramming, maintainabilityOfRoot);

        TreeNode<Indicator> concurrentDocumentationOfPortability = new DefaultTreeNode(concurrentDocumentation, portabilityOfRoot);
        TreeNode<Indicator> functionalDecompositionOfPortability = new DefaultTreeNode(functionalDecomposition, portabilityOfRoot);

        TreeNode<Indicator> hierarchicalDecompositionOfReliability = new DefaultTreeNode(hierarchicalDecomposition, reliabilityOfRoot);
        TreeNode<Indicator> informationHidingOfReliability = new DefaultTreeNode(informationHiding, reliabilityOfRoot);
        TreeNode<Indicator> stepwiseRefinementOfReliability = new DefaultTreeNode(stepwiseRefinement, reliabilityOfRoot);
        TreeNode<Indicator> structuredProgrammingOfReliability = new DefaultTreeNode(structuredProgramming, reliabilityOfRoot);

        TreeNode<Indicator> concurrentDocumentationOfReusability = new DefaultTreeNode(concurrentDocumentation, reusabilityOfRoot);
        TreeNode<Indicator> functionalDecompositionOfReusability = new DefaultTreeNode(functionalDecomposition, reusabilityOfRoot);
        TreeNode<Indicator> hierarchicalDecompositionOfReusability = new DefaultTreeNode(hierarchicalDecomposition, reusabilityOfRoot);
        TreeNode<Indicator> informationHidingOfReusability = new DefaultTreeNode(informationHiding, reusabilityOfRoot);

        TreeNode<Indicator> functionalDecompositionOfTestability = new DefaultTreeNode(functionalDecomposition, testabilityOfRoot);
        TreeNode<Indicator> hierarchicalDecompositionOfTestability = new DefaultTreeNode(hierarchicalDecomposition, testabilityOfRoot);
        TreeNode<Indicator> informationHidingOfTestability = new DefaultTreeNode(informationHiding, testabilityOfRoot);
        TreeNode<Indicator> lifeCycleVerificationOfTestability = new DefaultTreeNode(lifeCycleVerification, testabilityOfRoot);
        TreeNode<Indicator> stepwiseRefinementOfTestability = new DefaultTreeNode(stepwiseRefinement, testabilityOfRoot);
        TreeNode<Indicator> structuredProgrammingOfTestability = new DefaultTreeNode(structuredProgramming, testabilityOfRoot);

        concurrentDocumentationOfAdaptability.getChildren().add(new DefaultTreeNode(complexity));
        concurrentDocumentationOfAdaptability.getChildren().add(new DefaultTreeNode(easeOfChange));
        concurrentDocumentationOfAdaptability.getChildren().add(new DefaultTreeNode(readability));
        concurrentDocumentationOfAdaptability.getChildren().add(new DefaultTreeNode(traceability));
        concurrentDocumentationOfMaintainability.getChildren().add(new DefaultTreeNode(complexity));
        concurrentDocumentationOfMaintainability.getChildren().add(new DefaultTreeNode(easeOfChange));
        concurrentDocumentationOfMaintainability.getChildren().add(new DefaultTreeNode(readability));
        concurrentDocumentationOfMaintainability.getChildren().add(new DefaultTreeNode(traceability));
        concurrentDocumentationOfPortability.getChildren().add(new DefaultTreeNode(complexity));
        concurrentDocumentationOfPortability.getChildren().add(new DefaultTreeNode(easeOfChange));
        concurrentDocumentationOfPortability.getChildren().add(new DefaultTreeNode(readability));
        concurrentDocumentationOfPortability.getChildren().add(new DefaultTreeNode(traceability));
        concurrentDocumentationOfReusability.getChildren().add(new DefaultTreeNode(complexity));
        concurrentDocumentationOfReusability.getChildren().add(new DefaultTreeNode(easeOfChange));
        concurrentDocumentationOfReusability.getChildren().add(new DefaultTreeNode(readability));
        concurrentDocumentationOfReusability.getChildren().add(new DefaultTreeNode(traceability));

        functionalDecompositionOfAdaptability.getChildren().add(new DefaultTreeNode(cohesion));
        functionalDecompositionOfAdaptability.getChildren().add(new DefaultTreeNode(complexity));
        functionalDecompositionOfAdaptability.getChildren().add(new DefaultTreeNode(coupling));
        functionalDecompositionOfAdaptability.getChildren().add(new DefaultTreeNode(easeOfChange));
        functionalDecompositionOfMaintainability.getChildren().add(new DefaultTreeNode(cohesion));
        functionalDecompositionOfMaintainability.getChildren().add(new DefaultTreeNode(complexity));
        functionalDecompositionOfMaintainability.getChildren().add(new DefaultTreeNode(coupling));
        functionalDecompositionOfMaintainability.getChildren().add(new DefaultTreeNode(easeOfChange));
        functionalDecompositionOfPortability.getChildren().add(new DefaultTreeNode(cohesion));
        functionalDecompositionOfPortability.getChildren().add(new DefaultTreeNode(complexity));
        functionalDecompositionOfPortability.getChildren().add(new DefaultTreeNode(coupling));
        functionalDecompositionOfPortability.getChildren().add(new DefaultTreeNode(easeOfChange));
        functionalDecompositionOfReusability.getChildren().add(new DefaultTreeNode(cohesion));
        functionalDecompositionOfReusability.getChildren().add(new DefaultTreeNode(complexity));
        functionalDecompositionOfReusability.getChildren().add(new DefaultTreeNode(coupling));
        functionalDecompositionOfReusability.getChildren().add(new DefaultTreeNode(easeOfChange));
        functionalDecompositionOfTestability.getChildren().add(new DefaultTreeNode(cohesion));
        functionalDecompositionOfTestability.getChildren().add(new DefaultTreeNode(complexity));
        functionalDecompositionOfTestability.getChildren().add(new DefaultTreeNode(coupling));
        functionalDecompositionOfTestability.getChildren().add(new DefaultTreeNode(easeOfChange));

        hierarchicalDecompositionOfAdaptability.getChildren().add(new DefaultTreeNode(cohesion));
        hierarchicalDecompositionOfAdaptability.getChildren().add(new DefaultTreeNode(complexity));
        hierarchicalDecompositionOfAdaptability.getChildren().add(new DefaultTreeNode(coupling));
        hierarchicalDecompositionOfAdaptability.getChildren().add(new DefaultTreeNode(easeOfChange));
        hierarchicalDecompositionOfCorrectness.getChildren().add(new DefaultTreeNode(cohesion));
        hierarchicalDecompositionOfCorrectness.getChildren().add(new DefaultTreeNode(complexity));
        hierarchicalDecompositionOfCorrectness.getChildren().add(new DefaultTreeNode(coupling));
        hierarchicalDecompositionOfCorrectness.getChildren().add(new DefaultTreeNode(easeOfChange));
        hierarchicalDecompositionOfMaintainability.getChildren().add(new DefaultTreeNode(cohesion));
        hierarchicalDecompositionOfMaintainability.getChildren().add(new DefaultTreeNode(complexity));
        hierarchicalDecompositionOfMaintainability.getChildren().add(new DefaultTreeNode(coupling));
        hierarchicalDecompositionOfMaintainability.getChildren().add(new DefaultTreeNode(easeOfChange));
        hierarchicalDecompositionOfReliability.getChildren().add(new DefaultTreeNode(cohesion));
        hierarchicalDecompositionOfReliability.getChildren().add(new DefaultTreeNode(complexity));
        hierarchicalDecompositionOfReliability.getChildren().add(new DefaultTreeNode(coupling));
        hierarchicalDecompositionOfReliability.getChildren().add(new DefaultTreeNode(easeOfChange));
        hierarchicalDecompositionOfReusability.getChildren().add(new DefaultTreeNode(cohesion));
        hierarchicalDecompositionOfReusability.getChildren().add(new DefaultTreeNode(complexity));
        hierarchicalDecompositionOfReusability.getChildren().add(new DefaultTreeNode(coupling));
        hierarchicalDecompositionOfReusability.getChildren().add(new DefaultTreeNode(easeOfChange));
        hierarchicalDecompositionOfTestability.getChildren().add(new DefaultTreeNode(cohesion));
        hierarchicalDecompositionOfTestability.getChildren().add(new DefaultTreeNode(complexity));
        hierarchicalDecompositionOfTestability.getChildren().add(new DefaultTreeNode(coupling));
        hierarchicalDecompositionOfTestability.getChildren().add(new DefaultTreeNode(easeOfChange));

        informationHidingOfAdaptability.getChildren().add(new DefaultTreeNode(cohesion));
        informationHidingOfAdaptability.getChildren().add(new DefaultTreeNode(complexity));
        informationHidingOfAdaptability.getChildren().add(new DefaultTreeNode(coupling));
        informationHidingOfAdaptability.getChildren().add(new DefaultTreeNode(easeOfChange));
        informationHidingOfAdaptability.getChildren().add(new DefaultTreeNode(wellDefinedInterfaces));
        informationHidingOfMaintainability.getChildren().add(new DefaultTreeNode(cohesion));
        informationHidingOfMaintainability.getChildren().add(new DefaultTreeNode(complexity));
        informationHidingOfMaintainability.getChildren().add(new DefaultTreeNode(coupling));
        informationHidingOfMaintainability.getChildren().add(new DefaultTreeNode(easeOfChange));
        informationHidingOfMaintainability.getChildren().add(new DefaultTreeNode(wellDefinedInterfaces));
        informationHidingOfReliability.getChildren().add(new DefaultTreeNode(cohesion));
        informationHidingOfReliability.getChildren().add(new DefaultTreeNode(complexity));
        informationHidingOfReliability.getChildren().add(new DefaultTreeNode(coupling));
        informationHidingOfReliability.getChildren().add(new DefaultTreeNode(easeOfChange));
        informationHidingOfReliability.getChildren().add(new DefaultTreeNode(wellDefinedInterfaces));
        informationHidingOfReusability.getChildren().add(new DefaultTreeNode(cohesion));
        informationHidingOfReusability.getChildren().add(new DefaultTreeNode(complexity));
        informationHidingOfReusability.getChildren().add(new DefaultTreeNode(coupling));
        informationHidingOfReusability.getChildren().add(new DefaultTreeNode(easeOfChange));
        informationHidingOfReusability.getChildren().add(new DefaultTreeNode(wellDefinedInterfaces));
        informationHidingOfTestability.getChildren().add(new DefaultTreeNode(cohesion));
        informationHidingOfTestability.getChildren().add(new DefaultTreeNode(complexity));
        informationHidingOfTestability.getChildren().add(new DefaultTreeNode(coupling));
        informationHidingOfTestability.getChildren().add(new DefaultTreeNode(easeOfChange));
        informationHidingOfTestability.getChildren().add(new DefaultTreeNode(wellDefinedInterfaces));

        stepwiseRefinementOfAdaptability.getChildren().add(new DefaultTreeNode(cohesion));
        stepwiseRefinementOfAdaptability.getChildren().add(new DefaultTreeNode(complexity));
        stepwiseRefinementOfAdaptability.getChildren().add(new DefaultTreeNode(coupling));
        stepwiseRefinementOfCorrectness.getChildren().add(new DefaultTreeNode(cohesion));
        stepwiseRefinementOfCorrectness.getChildren().add(new DefaultTreeNode(complexity));
        stepwiseRefinementOfCorrectness.getChildren().add(new DefaultTreeNode(coupling));
        stepwiseRefinementOfMaintainability.getChildren().add(new DefaultTreeNode(cohesion));
        stepwiseRefinementOfMaintainability.getChildren().add(new DefaultTreeNode(complexity));
        stepwiseRefinementOfMaintainability.getChildren().add(new DefaultTreeNode(coupling));
        stepwiseRefinementOfReliability.getChildren().add(new DefaultTreeNode(cohesion));
        stepwiseRefinementOfReliability.getChildren().add(new DefaultTreeNode(complexity));
        stepwiseRefinementOfReliability.getChildren().add(new DefaultTreeNode(coupling));
        stepwiseRefinementOfTestability.getChildren().add(new DefaultTreeNode(cohesion));
        stepwiseRefinementOfTestability.getChildren().add(new DefaultTreeNode(complexity));
        stepwiseRefinementOfTestability.getChildren().add(new DefaultTreeNode(coupling));

        structuredProgrammingOfAdaptability.getChildren().add(new DefaultTreeNode(complexity));
        structuredProgrammingOfAdaptability.getChildren().add(new DefaultTreeNode(readability));
        structuredProgrammingOfCorrectness.getChildren().add(new DefaultTreeNode(complexity));
        structuredProgrammingOfCorrectness.getChildren().add(new DefaultTreeNode(readability));
        structuredProgrammingOfMaintainability.getChildren().add(new DefaultTreeNode(complexity));
        structuredProgrammingOfMaintainability.getChildren().add(new DefaultTreeNode(readability));
        structuredProgrammingOfReliability.getChildren().add(new DefaultTreeNode(complexity));
        structuredProgrammingOfReliability.getChildren().add(new DefaultTreeNode(readability));
        structuredProgrammingOfTestability.getChildren().add(new DefaultTreeNode(complexity));
        structuredProgrammingOfTestability.getChildren().add(new DefaultTreeNode(readability));

        lifeCycleVerificationOfCorrectness.getChildren().add(new DefaultTreeNode(earlyErrorDetection));
        lifeCycleVerificationOfCorrectness.getChildren().add(new DefaultTreeNode(visibilityOfBehavior));
        lifeCycleVerificationOfTestability.getChildren().add(new DefaultTreeNode(earlyErrorDetection));
        lifeCycleVerificationOfTestability.getChildren().add(new DefaultTreeNode(visibilityOfBehavior));

        this.rootIndicator = rootIndicator;
        this.rootTreeNode = rootTreeNode;
        this.actualRootTreeNode = actualRootTreeNode;
        this.ahp = ahp;
        return this;
    }
}
