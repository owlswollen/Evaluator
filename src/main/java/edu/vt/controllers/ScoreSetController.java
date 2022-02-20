/*
 * Created by Osman Balci on 2022.1.13
 * Copyright © 2022 Osman Balci. All rights reserved.
 */
package edu.vt.controllers;

import edu.vt.EntityBeans.ScoreSet;
import edu.vt.EntityBeans.User;
import edu.vt.controllers.util.JsfUtil;
import edu.vt.controllers.util.JsfUtil.PersistAction;
import edu.vt.FacadeBeans.ScoreSetFacade;
import edu.vt.globals.Methods;
import edu.vt.pojo.ScoreSetRow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;

@Named("scoreSetController")
@SessionScoped
public class ScoreSetController implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private List<ScoreSet> listOfScoreSets = null;
    private ScoreSet selected;

    @EJB
    private ScoreSetFacade scoreSetFacade;

    /*
    ==================
    Constructor Method
    ==================
     */
    public ScoreSetController() {
    }

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public List<ScoreSet> getListOfScoreSets() {
        if (listOfScoreSets == null) {
            listOfScoreSets = scoreSetFacade.findAll();
        }
        return listOfScoreSets;
    }

    public void setListOfScoreSets(List<ScoreSet> listOfScoreSets) {
        this.listOfScoreSets = listOfScoreSets;
    }

    public ScoreSet getSelected() {
        return selected;
    }

    public void setSelected(ScoreSet selected) {
        this.selected = selected;
    }

    public ScoreSetFacade getScoreSetFacade() {
        return scoreSetFacade;
    }

    /*
    ================
    Instance Methods
    ================
     */

    /*
     ***************************
     Unselect Selected Score Set
     ***************************
     */
    public void unselect() {
        selected = null;
    }

    /*
    **************************************************
    Get List of Score Set Rows for the Given Score Set
    **************************************************
     */
    public List<ScoreSetRow> getListOfScoreSetRows(ScoreSet scoreSet) {
        List<ScoreSetRow> listOfScoreSetRows = new ArrayList<>();
        /*
         String.split() parameter is a regular expression (RegEx) and
         vertical bar is a special character that needs to be escaped.
         */
        String[] arrayOfScoreSetRows = scoreSet.getDefinition().split("\\|");
        /*
         Score Set is recorded in the database as:
         ('Excellent to Poor', 'Excellent,80,100|Good,60,79.999|Average,40,59.999|Marginal,20,39.999|Poor,0,19.999')
         title      = 'Excellent to Poor'
         definition = 'Excellent,80,100|Good,60,79.999|Average,40,59.999|Marginal,20,39.999|Poor,0,19.999'
         */
        for (String row : arrayOfScoreSetRows) {
            // Excellent,80,100
            String[] rowParts = row.split(",");
            // rowParts[0] = Excellent
            String name = rowParts[0];
            // rowParts[1] = 80
            Double lowScore = Double.parseDouble(rowParts[1]);
            // rowParts[2] = 100
            Double highScore = Double.parseDouble(rowParts[2]);

            ScoreSetRow rowObject = new ScoreSetRow(name, lowScore, highScore);
            listOfScoreSetRows.add(rowObject);
        }
        return listOfScoreSetRows;
    }

    /*
    **************************************************
    Create an Empty Score Set and Show the Create Page
    **************************************************
     */
    public String prepareCreate() {
        selected = new ScoreSet();
        return "/scoreSet/Create?faces-redirect=true";
    }

    /*
     *****************************************
     *   Create the New Score Set If Valid   *
     *****************************************
     */
    public void createAfterValidation() {
        Methods.preserveMessages();
        boolean scoreSetDefinitionIsValid = true;
        /*
         String.split() parameter is a regular expression (RegEx) and
         vertical bar is a special character that needs to be escaped.
         */
        String[] arrayOfScoreSetRows = selected.getDefinition().split("\\|");
        if (arrayOfScoreSetRows.length < 2) {
            scoreSetDefinitionIsValid = false;
            Methods.showMessage("Fatal Error", "Invalid Definition!",
                    "Score set does not contain at least two rows!");
        } else {
            /*
             Score Set is recorded in the database as:
             ('Excellent to Poor', 'Excellent,80,100|Good,60,79.999|Average,40,59.999|Marginal,20,39.999|Poor,0,19.999')
             title      = 'Excellent to Poor'
             definition = 'Excellent,80,100|Good,60,79.999|Average,40,59.999|Marginal,20,39.999|Poor,0,19.999'
             */
            for (String row : arrayOfScoreSetRows) {
                if (row == null || row.isEmpty()) {
                    scoreSetDefinitionIsValid = false;
                    Methods.showMessage("Fatal Error", "Invalid Definition!",
                            "Score set row is empty!");
                } else {
                    // Excellent,80,100
                    String[] rowParts = row.split(",");
                    if (rowParts.length != 3) {
                        scoreSetDefinitionIsValid = false;
                        Methods.showMessage("Fatal Error", "Invalid Definition!",
                                "Score set row " + Arrays.toString(rowParts) + " is invalid!");
                    } else {
                        try {
                            double lowScore = Double.parseDouble(rowParts[1]);
                            try {
                                double highScore = Double.parseDouble(rowParts[2]);
                                if (highScore <= lowScore) {
                                    scoreSetDefinitionIsValid = false;
                                    Methods.showMessage("Fatal Error", "Invalid Definition!",
                                            "High score must be greater than low score!");
                                }
                            } catch (NumberFormatException nfe) {
                                scoreSetDefinitionIsValid = false;
                                Methods.showMessage("Fatal Error", "Invalid Definition!",
                                        "Score set high score is not a numeric value!");
                            }
                        } catch (NumberFormatException nfe) {
                            scoreSetDefinitionIsValid = false;
                            Methods.showMessage("Fatal Error", "Invalid Definition!",
                                    "Score set low score is not a numeric value!");
                        }
                    }
                }
            }
        }

        if (scoreSetDefinitionIsValid) {
            // Execute the create() method below to create the new score set
            create();
        }
    }

    /*
    **********************
    Create a New Score Set
    **********************
     */
    public void create() {
        Methods.preserveMessages();
        persist(PersistAction.CREATE,"Score Set was Successfully Created!");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The CREATE operation is successfully performed.
            selected = null;            // Remove selection
            listOfScoreSets = null;     // Invalidate listOfScoreSets to trigger re-query
        }
    }

    /*
     ******************************************
     *   Update Selected Score Set If Valid   *
     ******************************************
     */
    public void updateAfterValidation() {
        Methods.preserveMessages();
        boolean scoreSetUpdateIsValid = true;
        /*
         String.split() parameter is a regular expression (RegEx) and
         vertical bar is a special character that needs to be escaped.
         */
        String[] arrayOfScoreSetRows = selected.getDefinition().split("\\|");
        if (arrayOfScoreSetRows.length < 2) {
            scoreSetUpdateIsValid = false;
            listOfScoreSets = null;
            Methods.showMessage("Fatal Error", "Invalid Definition!",
                    "Score set does not contain at least two rows!");
        } else {
            /*
             Score Set is recorded in the database as:
             ('Excellent to Poor', 'Excellent,80,100|Good,60,79.999|Average,40,59.999|Marginal,20,39.999|Poor,0,19.999')
             title      = 'Excellent to Poor'
             definition = 'Excellent,80,100|Good,60,79.999|Average,40,59.999|Marginal,20,39.999|Poor,0,19.999'
             */
            for (String row : arrayOfScoreSetRows) {
                if (row == null || row.isEmpty()) {
                    scoreSetUpdateIsValid = false;
                    listOfScoreSets = null;
                    Methods.showMessage("Fatal Error", "Invalid Definition!",
                            "Score set row is empty!");
                } else {
                    // Excellent,80,100
                    String[] rowParts = row.split(",");
                    if (rowParts.length != 3) {
                        scoreSetUpdateIsValid = false;
                        listOfScoreSets = null;
                        Methods.showMessage("Fatal Error", "Invalid Definition!",
                                "Score set row " + Arrays.toString(rowParts) + " is invalid!");
                    } else {
                        try {
                            double lowScore = Double.parseDouble(rowParts[1]);
                            try {
                                double highScore = Double.parseDouble(rowParts[2]);
                                if (highScore <= lowScore) {
                                    scoreSetUpdateIsValid = false;
                                    listOfScoreSets = null;
                                    Methods.showMessage("Fatal Error", "Invalid Definition!",
                                            "High score must be greater than low score!");
                                }
                            } catch (NumberFormatException nfe) {
                                scoreSetUpdateIsValid = false;
                                listOfScoreSets = null;
                                Methods.showMessage("Fatal Error", "Invalid Definition!",
                                        "Score set high score is not a numeric value!");
                            }
                        } catch (NumberFormatException nfe) {
                            scoreSetUpdateIsValid = false;
                            listOfScoreSets = null;
                            Methods.showMessage("Fatal Error", "Invalid Definition!",
                                    "Score set low score is not a numeric value!");
                        }
                    }
                }
            }
        }

        if (scoreSetUpdateIsValid) {
            // Execute the update() method below to update the selected score set
            update();
        }
    }

    /*
     *********************************
     *   Update Selected Score Set   *
     *********************************
     */
    public void update() {
        Methods.preserveMessages();
        persist(PersistAction.UPDATE, "Score Set was Successfully Updated!");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The UPDATE operation is successfully performed.
            selected = null;            // Remove selection
            listOfScoreSets = null;     // Invalidate listOfScoreSets to trigger re-query
        }
    }

    /*
     *********************************
     *   Delete Selected Score Set   *
     *********************************
     */
    public void destroy() {
        Methods.preserveMessages();
        persist(PersistAction.DELETE, "Score Set was Successfully Deleted!");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The DELETE operation is successfully performed.
            selected = null;            // Remove selection
            listOfScoreSets = null;     // Invalidate listOfScoreSets to trigger re-query
        }
    }

    /*
     **********************************************************************************************
     *   Perform CREATE, UPDATE (EDIT), and DELETE (DESTROY, REMOVE) Operations in the Database   *
     **********************************************************************************************
     */

    /**
     * @param persistAction  refers to CREATE, UPDATE (Edit) or DELETE action
     * @param successMessage displayed to inform the user about the result
     */
    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            try {
                if (persistAction != PersistAction.DELETE) {
                    /*
                     ------------------------------------------------
                     Perform CREATE or EDIT operation in the database
                     ------------------------------------------------
                     The edit(selected) method performs the SAVE (STORE) operation of the "selected"
                     object in the database regardless of whether the object is a newly
                     created object (CREATE) or an edited (updated) object (EDIT or UPDATE).

                     ScoreSetFacade inherits the edit(selected) method from the AbstractFacade class.
                     */
                    scoreSetFacade.edit(selected);
                } else {
                    /*
                     ----------------------------------------
                     Perform DELETE operation in the database
                     ----------------------------------------
                     The remove(selected) method performs the DELETE operation of the "selected"
                     object in the database.

                     ScoreSetFacade inherits the remove(selected) method from the AbstractFacade class.
                     */
                    scoreSetFacade.remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
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

}
