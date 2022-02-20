/*
 * Created by Gokce Onen on 2022.2.20
 * Copyright © 2022 Gokce Onen. All rights reserved.
 */
package edu.vt.controllers;

import edu.vt.pojo.Ahp;
import edu.vt.pojo.Comparison;
import edu.vt.pojo.Indicator;
import org.primefaces.event.SlideEndEvent;
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
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Named("tabViewController")
@SessionScoped
public class TabViewController implements Serializable {

    //===================
    // Instance Variables
    //===================

    private List<String> tabNames;
    private Indicator selectedIndicator;
    private Indicator rootIndicator;
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

    //=============
    // Constructors
    //=============

    @PostConstruct
    public void init() {
        tabNames = Arrays.asList("Weights", "Scores", "Attributes");
        sliderVisible = false;
    }

    //==========================
    // Getter and Setter Methods
    //==========================

    public List<String> getTabNames() {
        return tabNames;
    }

    public void setTabNames(List<String> tabNames) {
        this.tabNames = tabNames;
    }

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
            String score = "[" + String.format("%.2f", selectedIndicator.getEvaluatorScores().get(childNode).getLow()) + " .. " + String.format("%.2f", selectedIndicator.getEvaluatorScores().get(childNode).getHigh()) + "]";
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

    //=================
    // Instance Methods
    //=================

    // Get data to show in the tab view from the selected node in the tree table
    public void getTabContent(Indicator selectedIndicator, Indicator rootIndicator) {
        sliderVisible = false;

        this.selectedIndicator = selectedIndicator;
        this.rootIndicator = rootIndicator;
        getCriticalityWeightings();
    }

    //-----------------
    // Weights Tab Data
    //-----------------

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

        Ahp ahp = new Ahp(rootIndicator);
        ahp.solve();
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

            sum += value;
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

}