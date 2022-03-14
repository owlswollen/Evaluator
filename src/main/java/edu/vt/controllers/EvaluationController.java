/*
 * Created by Osman Balci on 2022.1.14
 * Copyright © 2022 Osman Balci. All rights reserved.
 */
package edu.vt.controllers;

import edu.vt.EntityBeans.Project;
import edu.vt.EntityBeans.ScoreSet;
import edu.vt.FacadeBeans.ProjectFacade;
import edu.vt.FacadeBeans.UserFacade;
import edu.vt.controllers.util.JsfUtil;
import edu.vt.pojo.Ahp;
import edu.vt.pojo.Indicator;
import edu.vt.pojo.Score;
import edu.vt.pojo.ScoreSetRow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;

@Named("evaluationController")
@SessionScoped
public class EvaluationController implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private List<Project> listOfProjects = null;
    private List<Indicator> listOfLeafIndicators = null;
    private Indicator selectedLeafIndicator;
    private Project selectedProject;
    private List<ScoreSet> listOfScoreSets = null;
    private ScoreSet selectedScoreSet;
    private String signedInEvaluatorUsername;
    private String scoreText = "";

    @EJB
    private ProjectFacade projectFacade;

    @EJB
    private UserFacade userFacade;

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public List<Project> getListOfProjects() {
        if (listOfProjects == null) {
            Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            signedInEvaluatorUsername = (String) sessionMap.get("username");
            listOfProjects = projectFacade.findProjectsWhoseEvaluatorIsUsername(signedInEvaluatorUsername);
        }
        return listOfProjects;
    }

    public void setListOfProjects(List<Project> listOfProjects) {
        this.listOfProjects = listOfProjects;
    }

    public List<Indicator> getListOfLeafIndicators() {
        if (listOfLeafIndicators == null) {
            getLeafIndicatorsOfEvaluator();
        }
        return listOfLeafIndicators;
    }

    public void setListOfLeafIndicators(List<Indicator> listOfLeafIndicators) {
        this.listOfLeafIndicators = listOfLeafIndicators;
    }

    public Indicator getSelectedLeafIndicator() {
        return selectedLeafIndicator;
    }

    public void setSelectedLeafIndicator(Indicator selectedLeafIndicator) {
        this.selectedLeafIndicator = selectedLeafIndicator;
    }

    public List<ScoreSet> getListOfScoreSets() {
        return listOfScoreSets;
    }

    public void setListOfScoreSets(List<ScoreSet> listOfScoreSets) {
        this.listOfScoreSets = listOfScoreSets;
    }

    public ScoreSet getSelectedScoreSet() {
        return selectedScoreSet;
    }

    public void setSelectedScoreSet(ScoreSet selectedScoreSet) {
        this.selectedScoreSet = selectedScoreSet;
    }

    public ProjectFacade getProjectFacade() {
        return projectFacade;
    }

    public void setProjectFacade(ProjectFacade projectFacade) {
        this.projectFacade = projectFacade;
    }

    public UserFacade getUserFacade() {
        return userFacade;
    }

    public void setUserFacade(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    public Project getSelectedProject() {
        return selectedProject;
    }

    public void setSelectedProject(Project selectedProject) {
        this.selectedProject = selectedProject;
    }

    public String getScoreText(Indicator leafIndicator) {
        Ahp indicatorsGraph  = selectedProject.getIndicatorsGraph();
        if (indicatorsGraph != null) {
            Indicator signedInEvaluator = indicatorsGraph.getIndicatorList().stream().filter(ind -> ind.getName().equals(signedInEvaluatorUsername)).findAny().orElse(null);

            if (leafIndicator.getEvaluatorScores().containsKey(signedInEvaluator) && !leafIndicator.getHasDefaultScores()) {
                scoreText = "[" + String.format("%.2f", leafIndicator.getEvaluatorScores().get(signedInEvaluator).getLow()) + " .. " + String.format("%.2f", leafIndicator.getEvaluatorScores().get(signedInEvaluator).getHigh()) + "]";
            } else {
                scoreText = "Not evaluated";
            }
        }
        return scoreText;
    }

    public void setScoreText(String scoreText) {
        this.scoreText = scoreText;
    }

    /*
    ================
    Instance Methods
    ================
     */

    // Get list of leaf indicators in the selected project whose evaluator is the signed-in user
    public void getLeafIndicatorsOfEvaluator() {
        listOfLeafIndicators = new ArrayList<>();
        getListOfProjects();
        Ahp indicatorsGraph = selectedProject.getIndicatorsGraph();
        if (indicatorsGraph != null) {
            for (Indicator indicator : indicatorsGraph.getIndicatorList()) {
                if (indicator.isLeaf()) {
                    listOfLeafIndicators.add(indicator);
                }
            }
        }
    }

    public void saveScore(ScoreSetRow scoreSetRow) {
        List<Indicator> indicatorsOfSelectedProject = selectedProject.getIndicatorsGraph().getIndicatorList();
        for (Indicator leafIndicator : indicatorsOfSelectedProject) {
            if (leafIndicator.equals(selectedLeafIndicator)) {
                Indicator signedInEvaluator = indicatorsOfSelectedProject.stream().filter(ind -> ind.getName().equals(signedInEvaluatorUsername)).findAny().orElse(null);
                if (leafIndicator.getHasDefaultScores()) {
                    leafIndicator.setHasDefaultScores(false);
                    // Remove the evaluators of the leafIndicator added by default
                    for (Indicator evaluator : leafIndicator.getChildIndicators()) {
                        evaluator.getParentIndicators().remove(leafIndicator);
                        leafIndicator.deleteComparisons(evaluator);
                    }
                    leafIndicator.getChildIndicators().clear();
                    leafIndicator.getEvaluatorScores().clear();
                }

                if (!leafIndicator.getChildIndicators().contains(signedInEvaluator)) {
                    if (signedInEvaluator == null) {
                        signedInEvaluator = new Indicator(signedInEvaluatorUsername);
                        signedInEvaluator.setEvaluator(true);
                    }
                    leafIndicator.addChildIndicator(signedInEvaluator);
                    // Add a row and a column for the newly added evaluator to the pairwise comparison matrix of the leaf leafIndicator
                    // Set 1 to the newly added cells as the default comparison value
                    for (Indicator siblingEvaluator : leafIndicator.getChildIndicators()) {
                        leafIndicator.compareIndicators(signedInEvaluator, siblingEvaluator, 1.0);
                    }
                }
                leafIndicator.getEvaluatorScores().put(signedInEvaluator, new Score(scoreSetRow.getLowScore(), scoreSetRow.getHighScore()));
                selectedProject.getIndicatorsGraph().solve();
                break;
            }
        }

        try {
            projectFacade.edit(selectedProject);
            JsfUtil.addSuccessMessage("Score was successfully saved!");
        } catch (EJBException ex) {
            String msg = "";
            Throwable cause = ex.getCause();
            if (cause != null) {
                msg = cause.getLocalizedMessage();
            }
            if (msg.length() > 0) {
                JsfUtil.addErrorMessage(msg);
            } else {
                JsfUtil.addErrorMessage(ex, "A persistence error occurred!");
            }
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, "A persistence error occurred");
        }
    }
}
