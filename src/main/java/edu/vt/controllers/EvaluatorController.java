/*
 * Created by Gokce Onen on 2022.02.22
 * Copyright © 2022 Gokce Onen. All rights reserved.
 */
package edu.vt.controllers;

import edu.vt.EntityBeans.Project;
import edu.vt.EntityBeans.ScoreSet;
import edu.vt.FacadeBeans.ProjectFacade;
import edu.vt.FacadeBeans.UserFacade;
import edu.vt.controllers.util.JsfUtil;
import edu.vt.pojo.IndicatorsGraph;
import edu.vt.pojo.Indicator;
import edu.vt.pojo.Score;
import edu.vt.pojo.ScoreSetRow;
import org.primefaces.event.NodeSelectEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;

@Named("evaluatorController")
@SessionScoped
public class EvaluatorController implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private List<Project> listOfProjects = null;
    private List<Indicator> listOfLeafIndicators = null;
    private Indicator selectedIndicator;
    private Project selectedProject;
    private List<ScoreSet> listOfScoreSets = null;
    private ScoreSet selectedScoreSet;
    private String signedInEvaluatorUsername;
    private String scoreText = "";

    @EJB
    private ProjectFacade projectFacade;

    @EJB
    private UserFacade userFacade;

    @Inject
    private TreeTableController treeTableController;

    @Inject
    private EditorController editorController;

    @Inject
    private ScoreSetController scoreSetController;

    /*
    ============
    Constructors
    ============
     */
    public EvaluatorController() {
    }

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

    public Indicator getSelectedIndicator() {
        return selectedIndicator;
    }

    public void setSelectedIndicator(Indicator selectedIndicator) {
        this.selectedIndicator = selectedIndicator;
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
        IndicatorsGraph indicatorsGraph = selectedProject.getIndicatorsGraph();
        if (indicatorsGraph != null) {
            treeTableController.showGraphOnTreeTable(indicatorsGraph, false);
            treeTableController.expandAllNodes(treeTableController.getRootTreeNode());
        }
        this.selectedProject = selectedProject;
    }

    public String getScoreText(Indicator leafIndicator) {
        IndicatorsGraph indicatorsGraph = selectedProject.getIndicatorsGraph();
        if (indicatorsGraph != null) {
            if (leafIndicator.getEvaluatorScores().containsKey(signedInEvaluatorUsername)) {
                scoreText = "[" + String.format("%.2f", leafIndicator.getEvaluatorScores().get(signedInEvaluatorUsername).getLow()) + " .. " + String.format("%.2f", leafIndicator.getEvaluatorScores().get(signedInEvaluatorUsername).getHigh()) + "]";
            } else {
                scoreText = "Not evaluated";
            }
        }
        return scoreText;
    }

    public void setScoreText(String scoreText) {
        this.scoreText = scoreText;
    }

    public String getSignedInEvaluatorUsername() {
        return signedInEvaluatorUsername;
    }

    public void setSignedInEvaluatorUsername(String signedInEvaluatorUsername) {
        this.signedInEvaluatorUsername = signedInEvaluatorUsername;
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
        IndicatorsGraph indicatorsGraph = selectedProject.getIndicatorsGraph();
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
            if (leafIndicator.equals(selectedIndicator)) {
                leafIndicator.getEvaluatorScores().put(signedInEvaluatorUsername, new Score(scoreSetRow.getLowScore(), scoreSetRow.getHighScore()));
                leafIndicator.getEvaluatorNotes().put(signedInEvaluatorUsername, editorController.getEditorContent());
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

    public boolean isIndicatorEvaluable(Indicator indicator) {
        // If the indicator is a leaf indicator and the signed-in user is assigned to it as an evaluator
        return indicator.isLeaf() && indicator.getChildIndicators().stream().anyMatch(eval -> eval.getName().equals(signedInEvaluatorUsername));
    }

    public boolean isIndicatorEvaluated(Indicator indicator) {
        // If the indicator is evaluated by the signed-in evaluator
        return indicator.getEvaluatorScores().keySet().stream().anyMatch(eval -> eval.equals(signedInEvaluatorUsername));
    }

    public String getStyleForEvaluableIndicators(Indicator indicator) {
        String style = "";
        if (isIndicatorEvaluable(indicator)) {
            if (isIndicatorEvaluated(indicator)) {
                style = "color: darkgreen; font-weight: bold";
            } else {
                style = "color: maroon; font-weight: bold";
            }
        }
        return style;
    }

    public void onIndicatorSelect(NodeSelectEvent event) {
        selectedIndicator = (Indicator) event.getTreeNode().getData();
        editorController.setEditorContent(selectedIndicator.getEvaluatorNotes().get(signedInEvaluatorUsername));
        scoreSetController.reset();
    }

    public String evaluate() {
        if (selectedProject.getIndicatorsGraph() == null) {
            FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "This project does not have an indicators graph yet.", "");
            FacesContext.getCurrentInstance().addMessage(null, facesMsg);
            return "/evaluator/Projects?faces-redirect=false";
        }

        return "/evaluator/Indicators?faces-redirect=true";
    }
}
