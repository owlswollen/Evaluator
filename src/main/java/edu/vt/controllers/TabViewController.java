/*
 * Created by Gokce Onen on 2022.2.20
 * Copyright © 2022 Gokce Onen. All rights reserved.
 */
package edu.vt.controllers;

import edu.vt.EntityBeans.ScoreSet;
import edu.vt.controllers.util.JsfUtil;
import edu.vt.pojo.IndicatorsGraph;
import edu.vt.pojo.Comparison;
import edu.vt.pojo.Indicator;
import org.primefaces.event.*;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.radial.RadialScales;
import org.primefaces.model.charts.axes.radial.linear.RadialLinearTicks;
import org.primefaces.model.charts.optionconfig.elements.Elements;
import org.primefaces.model.charts.optionconfig.elements.ElementsLine;
import org.primefaces.model.charts.radar.RadarChartDataSet;
import org.primefaces.model.charts.radar.RadarChartModel;
import org.primefaces.model.charts.radar.RadarChartOptions;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.criteria.CriteriaBuilder;
import java.io.Serializable;
import java.util.*;

@Named("tabViewController")
@SessionScoped
public class TabViewController implements Serializable {

    //===================
    // Instance Variables
    //===================

    private Indicator selectedIndicator;
    private IndicatorsGraph indicatorsGraph;
    private Indicator rootIndicator;
    private String previousActiveTabTitle;
    private String activeTabIndex;
    private List<List<Comparison>> comparisons;
    private boolean sliderVisible;
    private String comparedIndicator1;
    private String comparedIndicator2;
    private int editedRowIndex;
    private int editedColumnIndex;
    private Double sliderValue;
    private String score;
    private List<String> childScores;
    private List<String> evaluatorScores;
    private boolean showLegends;
    private List<String> leafIndicatorTabs;
    private List<String> branchIndicatorTabs;
    private String selectedEvaluatorName;
    private Indicator selectedEvaluator;
    private boolean scoreSetSelected;
    private boolean scoresPropagated;

    @Inject
    private EditorController editorController;

    @Inject
    private ScoreSetController scoreSetController;

    //=============
    // Constructors
    //=============

    @PostConstruct
    public void init() {
        sliderVisible = false;
        leafIndicatorTabs = Arrays.asList("Overview", "Evaluators", "Nominal Scores", "Weights", "Scores", "Notes");
        branchIndicatorTabs = Arrays.asList("Overview", "Nominal Scores", "Weights", "Scores");
        activeTabIndex = "-1";
        previousActiveTabTitle = "Overview";
        scoresPropagated = false;
    }

    //==========================
    // Getter and Setter Methods
    //==========================

    public Indicator getSelectedIndicator() {
        return selectedIndicator;
    }

    public void setSelectedIndicator(Indicator selectedIndicator) {
        this.selectedIndicator = selectedIndicator;
    }

    public Indicator getRootIndicator() {
        return rootIndicator;
    }

    public void setRootIndicator(Indicator rootIndicator) {
        this.rootIndicator = rootIndicator;
    }

    public IndicatorsGraph getIndicatorsGraph() {
        return indicatorsGraph;
    }

    public void setIndicatorsGraph(IndicatorsGraph indicatorsGraph) {
        this.indicatorsGraph = indicatorsGraph;
    }

    public List<List<Comparison>> getComparisons() {
        return comparisons;
    }

    public void setComparisons(List<List<Comparison>> comparisons) {
        if (comparisons == null) {
            getCriticalityWeightings();
        }
        this.comparisons = comparisons;
    }

    public boolean isSliderVisible() {
        return sliderVisible;
    }

    public void setSliderVisible(boolean sliderVisible) {
        this.sliderVisible = sliderVisible;
    }

    public String getComparedIndicator1() {
        return comparedIndicator1;
    }

    public void setComparedIndicator1(String comparedIndicator1) {
        this.comparedIndicator1 = comparedIndicator1;
    }

    public String getComparedIndicator2() {
        return comparedIndicator2;
    }

    public void setComparedIndicator2(String comparedIndicator2) {
        this.comparedIndicator2 = comparedIndicator2;
    }

    public int getEditedRowIndex() {
        return editedRowIndex;
    }

    public void setEditedRowIndex(int editedRowIndex) {
        this.editedRowIndex = editedRowIndex;
    }

    public int getEditedColumnIndex() {
        return editedColumnIndex;
    }

    public void setEditedColumnIndex(int editedColumnIndex) {
        this.editedColumnIndex = editedColumnIndex;
    }

    public Double getSliderValue() {
        sliderValue = comparisons.get(editedRowIndex).get(editedColumnIndex).getValue();
        return sliderValue;
    }

    public void setSliderValue(Double sliderValue) {
        this.sliderValue = sliderValue;
    }

    public String getScore() {
        score = "[" + String.format("%.2f", selectedIndicator.getScore().getLow()) + " .. " + String.format("%.2f", selectedIndicator.getScore().getHigh()) + "]";
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public List<String> getChildScores() {
        childScores = new ArrayList<>();
        for (Indicator childNode : selectedIndicator.getChildIndicators()) {
            String score = "[" + String.format("%.2f", childNode.getScore().getLow()) + " .. " + String.format("%.2f", childNode.getScore().getHigh()) + "]";
            childScores.add(score);
        }
        return childScores;
    }

    public void setChildScores(List<String> childScores) {
        this.childScores = childScores;
    }

    public List<String> getEvaluatorScores() {
        evaluatorScores = new ArrayList<>();
        for (Indicator childNode : selectedIndicator.getChildIndicators()) {
            String score;
            if (selectedIndicator.getEvaluatorScores().containsKey(childNode.getName())) {
                score = "[" + String.format("%.2f", selectedIndicator.getEvaluatorScores().get(childNode.getName()).getLow()) + " .. " + String.format("%.2f", selectedIndicator.getEvaluatorScores().get(childNode.getName()).getHigh()) + "]";
            } else {
                score = "Not evaluated";
            }
            evaluatorScores.add(score);
        }
        return evaluatorScores;
    }

    public void setEvaluatorScores(List<String> evaluatorScores) {
        this.evaluatorScores = evaluatorScores;
    }

    public boolean isShowLegends() {
        return showLegends;
    }

    public void setShowLegends(boolean showLegends) {
        this.showLegends = showLegends;
    }

    public String getActiveTabIndex() {
        return activeTabIndex;
    }

    public void setActiveTabIndex(String activeTabIndex) {
        this.activeTabIndex = activeTabIndex;
    }

    public String getSelectedEvaluatorName() {
        return selectedEvaluatorName;
    }

    public void setSelectedEvaluatorName(String selectedEvaluatorName) {
        selectedEvaluator = selectedIndicator.getChildIndicators().stream().filter(evaluator -> evaluator.getName().equals(selectedEvaluatorName)).findAny().orElse(null);
        if (selectedEvaluator == null) {
            selectedEvaluator = new Indicator(selectedEvaluatorName);
            selectedEvaluator.setEvaluator(true);
        }
        this.selectedEvaluatorName = selectedEvaluatorName;
    }

    public Indicator getSelectedEvaluator() {
        return selectedEvaluator;
    }

    public void setSelectedEvaluator(Indicator selectedEvaluator) {
        this.selectedEvaluator = selectedEvaluator;
    }

    public boolean isScoreSetSelected() {
        return scoreSetSelected;
    }

    public void setScoreSetSelected(boolean scoreSetSelected) {
        this.scoreSetSelected = scoreSetSelected;
    }

    public boolean isScoresPropagated() {
        return scoresPropagated;
    }

    public void setScoresPropagated(boolean scoresPropagated) {
        this.scoresPropagated = scoresPropagated;
    }

    //=================
    // Instance Methods
    //=================

    public void openProject(IndicatorsGraph indicatorsGraph) {
        this.indicatorsGraph = indicatorsGraph;
        if (indicatorsGraph != null) {
            rootIndicator = indicatorsGraph.getRoot();
            scoresPropagated = indicatorsGraph.isSolved();
        }
    }

    public void onNodeSelect(NodeSelectEvent event) {
        // Leaf and branch indicators have different tabs in the tab view
        // The active tab will remain the same if the newly selected indicator has the previous active tab
        // Otherwise the active tab will be "Overview" by default
        activeTabIndex = "0";
        Indicator newSelectedIndicator = (Indicator) event.getComponent().getAttributes().get("selectedIndicator");
//        if (selectedIndicator != null) {
//            activeTabIndex = String.valueOf(newSelectedIndicator.isLeaf() ? leafIndicatorTabs.indexOf(previousActiveTabTitle) : branchIndicatorTabs.indexOf(previousActiveTabTitle));
//        }
//        if (activeTabIndex.equals("-1")) {
//            activeTabIndex = "0"; // Overview tab
//        }

        getTabContent(newSelectedIndicator);
    }

    public void onTabChange(TabChangeEvent event) {
        previousActiveTabTitle = event.getTab().getTitle();
        getTabContent((Indicator) event.getComponent().getAttributes().get("selectedIndicator"));
    }

    // Get data to show in the tab view from the selected node in the tree table
    public void getTabContent(Indicator selectedIndicator) {
        sliderVisible = false;

        this.selectedIndicator = selectedIndicator;
        getCriticalityWeightings();
        if (selectedIndicator != null) {
            editorController.setEditorContent(selectedIndicator.getDescription());
        }
        selectedEvaluatorName = null;
        selectedEvaluator = null;
    }

    //-------------
    // OverView Tab
    //-------------

    public void updateDescription() {
        selectedIndicator.setDescription(editorController.getEditorContent());
        JsfUtil.addSuccessMessage("Indicator description was saved.");
    }

    //---------------
    // Evaluators Tab
    //---------------

    public void assignEvaluator(Indicator evaluator) {
        selectedIndicator.addChildIndicator(evaluator);
        // Add a row and a column for the newly added evaluator to the pairwise comparison matrix of the leaf leafIndicator
        // Set 1 to the newly added cells as the default comparison value
        for (Indicator siblingEvaluator : selectedIndicator.getChildIndicators()) {
            selectedIndicator.compareIndicators(evaluator, siblingEvaluator, 1.0);
        }
        indicatorsGraph = new IndicatorsGraph(rootIndicator);
        indicatorsGraph.solve();
        if (!allEvaluatorScoresGiven()) {
            scoresPropagated = false;
            indicatorsGraph.setSolved(false);
        }
        selectedEvaluatorName = null;
        selectedEvaluator = null;
    }

    public void removeEvaluator(Indicator evaluator) {
        // Remove the evaluators from the selected leaf indicator
        evaluator.getParentIndicators().remove(selectedIndicator);
        selectedIndicator.deleteComparisons(evaluator);
        selectedIndicator.getChildIndicators().remove(evaluator);
        selectedIndicator.getEvaluatorScores().remove(evaluator.getName());
        selectedIndicator.getEvaluatorNotes().remove(evaluator.getName());
        indicatorsGraph = new IndicatorsGraph(rootIndicator);
        indicatorsGraph.solve();
        if (allEvaluatorScoresGiven() && indicatorsGraph.isSolved()) {
            scoresPropagated = true;
        }
        selectedEvaluatorName = null;
        selectedEvaluator = null;
    }

    public boolean isEvaluatorAlreadyAssigned(Indicator evaluator) {
        return selectedIndicator.getChildIndicators().stream().anyMatch(eval -> eval.getName().equals(evaluator.getName()));
    }

    //-------------------
    // Nominal Scores Tab
    //-------------------

    public void selectScoreSet(ScoreSet scoreSet) {
        selectedIndicator.setScoreSet(scoreSet);
        scoreSetSelected = true;
        scoreSetController.setSelectedScoreSetId(null);
        scoreSetController.setSelectedScoreSet(null);
    }

    //------------
    // Weights Tab
    //------------

    // Get pairwise comparison matrix data for the weights tab
    public void getCriticalityWeightings() {
        if (selectedIndicator != null) {
            comparisons = new ArrayList<>();
            for (Indicator childNodeRow : selectedIndicator.getChildIndicators()) {
                List<Comparison> comparisonRow = new ArrayList<>();
                for (Indicator childNodeColumn : selectedIndicator.getChildIndicators()) {
                    Comparison comparison = new Comparison();
                    comparison.setIndicator1(childNodeRow);
                    comparison.setIndicator2(childNodeColumn);
                    comparison.setValue(selectedIndicator.getComparisonMatrix().get(childNodeRow).get(childNodeColumn));
                    comparisonRow.add(comparison);
                }
                comparisons.add(comparisonRow);
            }
        }
    }

    // Set pairwise comparison matrix data after it is edited in the weights tab
    public void setCriticalityWeightings() {
        comparisons.get(editedRowIndex).get(editedColumnIndex).setValue(sliderValue);
        comparisons.get(editedColumnIndex).get(editedRowIndex).setValue(1. / sliderValue);

        for (List<Comparison> comparisonRow : comparisons) {
            for (Comparison comparison : comparisonRow) {
                selectedIndicator.compareIndicators(comparison.getIndicator1(), comparison.getIndicator2(), comparison.getValue());
            }
        }

        IndicatorsGraph indicatorsGraph = new IndicatorsGraph(rootIndicator);
        indicatorsGraph.solve();
    }

    // Create radar chart model to show in the Weights tab
    public RadarChartModel getRadarModel() {
        RadarChartModel radarModel = new RadarChartModel();
        if (selectedIndicator == null) {
            return radarModel;
        }

        ChartData data = new ChartData();

        RadarChartDataSet radarDataSet = new RadarChartDataSet();
        radarDataSet.setLabel(selectedIndicator.getName());
        radarDataSet.setFill(true);
        radarDataSet.setBackgroundColor("rgba(255, 99, 132, 0.2)");
        radarDataSet.setBorderColor("rgb(255, 99, 132)");
        radarDataSet.setPointBackgroundColor("rgb(255, 99, 132)");
        radarDataSet.setPointBorderColor("#fff");
        radarDataSet.setPointHoverBackgroundColor("#fff");
        radarDataSet.setPointHoverBorderColor("rgb(255, 99, 132)");

        List<Number> dataVal = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<String> legends = new ArrayList<>();

        Double sum = 0.0;
        int count = 0;

        for (Indicator childNode : selectedIndicator.getChildIndicators()) {
            Double value = selectedIndicator.getChildWeights().get(childNode);
            dataVal.add(value);
            labels.add(childNode.getName());
            legends.add(String.valueOf(count));

            if (value != null) {
                sum += value;
            }
            count++;
        }
        Double average = sum / count;

        radarDataSet.setData(dataVal);
        data.addChartDataSet(radarDataSet);
        if (showLegends) {
            data.setLabels(legends);
        } else {
            data.setLabels(labels);
        }

        /* Options */
        RadarChartOptions options = new RadarChartOptions();
        Elements elements = new Elements();
        ElementsLine elementsLine = new ElementsLine();
        elementsLine.setTension(0);
        elementsLine.setBorderWidth(3);
        elements.setLine(elementsLine);
        options.setElements(elements);

        RadialLinearTicks ticks = new RadialLinearTicks();
        ticks.setBeginAtZero(true);
        ticks.setMin(0);
        ticks.setStepSize(0.1);
        RadialScales scales = new RadialScales();
        scales.setTicks(ticks);
        options.setScales(scales);

        List<Number> averageData = new ArrayList<>();
        for (int i = 0; i < selectedIndicator.getChildWeights().size(); i++) {
            averageData.add(average);
        }

        RadarChartDataSet averageDataset = new RadarChartDataSet();
        averageDataset.setLabel("Equal Weights Case");
        averageDataset.setFill(false);
        averageDataset.setBackgroundColor("rgba(102, 153, 204, 0.2)");
        averageDataset.setBorderColor("rgba(102, 153, 204, 1)");
        averageDataset.setPointBackgroundColor("rgba(102, 153, 204, 1)");
        averageDataset.setPointBorderColor("#fff");
        averageDataset.setPointHoverRadius(5);
        averageDataset.setPointHoverBackgroundColor("#fff");
        averageDataset.setPointHoverBorderColor("rgba(102, 153, 204, 1)");
        averageDataset.setData(averageData);

        data.addChartDataSet(averageDataset);

        radarModel.setOptions(options);
        radarModel.setData(data);

        return radarModel;
    }

    // Create pairwise comparison slider
    public void createSlider(int rowIndex, int columnIndex) {
        editedRowIndex = rowIndex;
        editedColumnIndex = columnIndex;
        sliderVisible = true;
        comparedIndicator1 = comparisons.get(rowIndex).get(columnIndex).getIndicator1().getName();
        comparedIndicator2 = comparisons.get(rowIndex).get(columnIndex).getIndicator2().getName();
    }

    // Pairwise comparison slider event
    public void onSlideEnd(SlideEndEvent event) {
        sliderValue = event.getValue();
        setCriticalityWeightings();
    }

    //-----------
    // Scores Tab
    //-----------

    public boolean allEvaluatorScoresGiven() {
        if (indicatorsGraph != null) {
            for (Indicator indicator : indicatorsGraph.getIndicatorList()) {
                if (indicator.isLeaf()) {
                    for (Indicator evaluator : indicator.getChildIndicators()) {
                        if (!indicator.getEvaluatorScores().containsKey(evaluator.getName())) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public void propagateScores() {
        scoresPropagated = true;
        indicatorsGraph.setSolved(true);
    }

    public List<String> getEvaluatorsNotCompletedScoring() {
        List<String> evaluatorsNotCompleted = new ArrayList<>();
        for (Indicator indicator : indicatorsGraph.getIndicatorList()) {
            if (indicator.isLeaf()) {
                for (Indicator evaluator : indicator.getChildIndicators()) {
                    if (!indicator.getEvaluatorScores().containsKey(evaluator.getName())) {
                        if (!evaluatorsNotCompleted.contains(evaluator.getName())) {
                            evaluatorsNotCompleted.add(evaluator.getName());
                        }
                    }
                }
            }
        }
        return evaluatorsNotCompleted;
    }

    public boolean allIndicatorsAssignedAnEvaluator() {
        if (indicatorsGraph != null) {
            for (Indicator indicator : indicatorsGraph.getIndicatorList()) {
                if (indicator.isLeaf() && indicator.getChildIndicators().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    //-----------
    // Notes Tab
    //-----------

    public String getNotes(String evaluatorUsername) {
        return selectedIndicator.getEvaluatorNotes().get(evaluatorUsername);
    }
}