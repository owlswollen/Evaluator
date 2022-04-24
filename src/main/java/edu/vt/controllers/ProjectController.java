/*
 * Created by Osman Balci on 2022.1.14
 * Copyright © 2022 Osman Balci. All rights reserved.
 */
package edu.vt.controllers;

import edu.vt.EntityBeans.Project;
import edu.vt.EntityBeans.User;
import edu.vt.FacadeBeans.ProjectFacade;
import edu.vt.FacadeBeans.UserFacade;
import edu.vt.globals.Methods;
import edu.vt.controllers.util.JsfUtil;
import edu.vt.controllers.util.JsfUtil.PersistAction;
import edu.vt.pojo.Indicator;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;

@Named("projectController")
@SessionScoped
public class ProjectController implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private List<Project> listOfProjects = null;
    private Project selectedProject;
    private List<User> selectedAdministrators;
    private List<User> selectedEvaluators;

    @EJB
    private ProjectFacade projectFacade;

    @EJB
    private UserFacade userFacade;

    @Inject
    private EditorController editorController;

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public List<Project> getListOfProjects() {
        if (listOfProjects == null) {
            Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            String signedInUsername = (String) sessionMap.get("username");
            listOfProjects = projectFacade.findProjectsWhoseAdminIsUsername(signedInUsername);
        }
        return listOfProjects;
    }

    public void setListOfProjects(List<Project> listOfProjects) {
        this.listOfProjects = listOfProjects;
    }

    public Project getSelectedProject() {
        return selectedProject;
    }

    public void setSelectedProject(Project selectedProject) {
        this.selectedProject = selectedProject;
    }

    public List<User> getSelectedAdministrators() {
        return selectedAdministrators;
    }

    public void setSelectedAdministrators(List<User> selectedAdministrators) {
        this.selectedAdministrators = selectedAdministrators;
    }

    public List<User> getSelectedEvaluators() {
        return selectedEvaluators;
    }

    public void setSelectedEvaluators(List<User> selectedEvaluators) {
        this.selectedEvaluators = selectedEvaluators;
    }

    /*
    ================
    Instance Methods
    ================
     */

    /*
     ******************************
     Get All Users Except SuperUser
     ******************************
     */
    public List<User> getAllUsersExceptSuperUser() {
        List<User> listOfAllUsersExceptSuperUser = new ArrayList<>();
        List<User> allUsers = userFacade.findAll();
        for (User user : allUsers) {
            if (!user.getUsername().equals("SuperUser")) {
                listOfAllUsersExceptSuperUser.add(user);
            }
        }
        return listOfAllUsersExceptSuperUser;
    }

    /*
     *************************
     Unselect Selected Project
     *************************
     */
    public void unselect() {
        selectedProject = null;
    }

    /*
     *************************************
     Cancel and Show the Project List Page
     *************************************
     */
    public String cancel() {
        // Unselect previously selectedProject object if any
        selectedProject = null;
        return "/project/List?faces-redirect=true";
    }

    public List<Project> getListOfAllProjects() {
        return projectFacade.findAll();
    }

    /*
     ********************************************
     Return project's admin names separated by \n
     ********************************************
     */
    public String adminFirstLastNames(Project project) {
        String admin_usernames = project.getAdminUsernames();
        return getFullNamesFromUsernames(admin_usernames);
    }

    /*
     ************************************************
     Return project's evaluator names separated by \n
     ************************************************
     */
    public String evaluatorFirstLastNames(Project project) {
        String evaluator_usernames = project.getEvaluatorUsernames();
        return getFullNamesFromUsernames(evaluator_usernames);
    }

    /*
     ************************************************
     Return project's evaluator names separated by \n
     ************************************************
     */
    public String evaluatorFirstLastNamesAndUsernames(Project project) {
        String evaluator_usernames = project.getEvaluatorUsernames();
        return getFullNamesAndUsernames(evaluator_usernames);
    }

    /*
     ************************************
     Return project's evaluator usernames
     ************************************
     */
    public String[] evaluatorUsernames(Project project) {
        return project.getEvaluatorUsernames().split(",");
    }

    /*
     **************
     Utility Method
     **************
     */
    public String getFullNamesFromUsernames(String usernames) {
        String[] arrayOfUsernames = usernames.split(",");
        StringBuilder stringBuilder = new StringBuilder();
        for (String username : arrayOfUsernames) {
            User user = userFacade.findByUsername(username);
            if (user != null) {
                String fullName = user.getFirstName() + " " + user.getLastName();
                stringBuilder.append(fullName).append("\n");
            }
        }
        String firstLastNames = stringBuilder.toString();
        // Drop the last \n
        return firstLastNames.substring(0, firstLastNames.length() - 1);
    }

    public String getFullNamesAndUsernames(String usernames) {
        String[] arrayOfUsernames = usernames.split(",");
        StringBuilder stringBuilder = new StringBuilder();
        for (String username : arrayOfUsernames) {
            User user = userFacade.findByUsername(username);
            if (user != null) {
                String fullNameAndUsername = String.format("%s %-20s (%s)", user.getFirstName(), user.getLastName(), username);
                stringBuilder.append(fullNameAndUsername).append("\n");
            }
        }
        String firstLastNamesAndUsernames = stringBuilder.toString();
        // Drop the last \n
        return firstLastNamesAndUsernames.substring(0, firstLastNamesAndUsernames.length() - 1);
    }

    /*
     ***********************************************
     Return project's admin users' object references
     ***********************************************
     */
    public List<User> adminUserObjRefs(Project project) {
        String admin_usernames = project.getAdminUsernames();
        return getUserObjRefsFromUsernames(admin_usernames);
    }

    /*
     ***************************************************
     Return project's evaluator users' object references
     ***************************************************
     */
    public List<User> evaluatorUserObjRefs(Project project) {
        String evaluator_usernames = project.getEvaluatorUsernames();
        return getUserObjRefsFromUsernames(evaluator_usernames);
    }

    /*
     **************
     Utility Method
     **************
     */
    public List<User> getUserObjRefsFromUsernames(String usernames) {
        String[] arrayOfUsernames = usernames.split(",");
        List<User> userObjRefs = new ArrayList<>();
        for (String username : arrayOfUsernames) {
            User user = userFacade.findByUsername(username);
            if (user != null) {
                userObjRefs.add(user);
            }
        }
        return userObjRefs;
    }

    /*
    ***********************************************************
    Prepare Edit of the Selected Project and Show the Edit Page
    ***********************************************************
     */
    public String prepareEdit() {
        editorController.setEditorContent(selectedProject.getDescription());
        return "/project/Edit?faces-redirect=true";
    }

    /*
    *************************************
    Reset for Edit and Show the List Page
    *************************************
     */
    public void cancelEdit() {
        editorController.setEditorContent("");
        /*
         return "/project/List?faces-redirect=true"; does not work.
         Therefore, we use externalContext to redirect to the page.
         */
        Methods.preserveMessages();
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        /*
        getRequestContextPath() returns the URI of the webapp directory of the application.
        Obtain the URI of the page to redirect to.
         */
        String redirectPageURI = externalContext.getRequestContextPath() + "/project/List.xhtml";

        // Redirect to show the page
        try {
            externalContext.redirect(redirectPageURI);
        } catch (IOException e) {
            Methods.showMessage("Fatal Error", "Navigation Problem!",
                    "Unable to redirect to show the List.xhtml page!");
        }
    }

    /*
    ************************************************
    Create an Empty Project and Show the Create Page
    ************************************************
     */
    public String prepareCreate() {
        selectedProject = new Project();

        // Initialize instance variables
        selectedAdministrators = null;
        selectedEvaluators = null;
        editorController.setEditorContent("");

        return "/project/Create?faces-redirect=true";
    }

    /*
    *********************************************
    Create Project and Show the Project List Page
    *********************************************
     */
    public String createProject() {
        Methods.preserveMessages();

        // Project title is set directly from the JSF page

        // Set project description from the editor
        selectedProject.setDescription(editorController.getEditorContent());

        if (!selectedAdministrators.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();

            for (User admin : selectedAdministrators) {
                stringBuilder.append(admin.getUsername()).append(",");
            }

            String admin_names = stringBuilder.toString();
            // Drop the last comma
            String adminUserNames = admin_names.substring(0, admin_names.length() - 1);

            selectedProject.setAdminUsernames(adminUserNames);
        }

        if (!selectedEvaluators.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();

            for (User evaluator : selectedEvaluators) {
                stringBuilder.append(evaluator.getUsername()).append(",");
            }

            String evaluator_names = stringBuilder.toString();
            // Drop the last comma
            String evaluatorUserNames = evaluator_names.substring(0, evaluator_names.length() - 1);

            selectedProject.setEvaluatorUsernames(evaluatorUserNames);
        }

        // Execute the create() method below to create the new project
        create();

        return "/project/List?faces-redirect=true";
    }

    /*
    *********************************************
    Update Project and Show the Project List Page
    *********************************************
     */
    public String updateProject() {
        Methods.preserveMessages();

        // Project title is set directly from the JSF page

        // Set project description from the editor
        selectedProject.setDescription(editorController.getEditorContent());

        if (!selectedAdministrators.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();

            for (User admin : selectedAdministrators) {
                stringBuilder.append(admin.getUsername()).append(",");
            }

            String admin_names = stringBuilder.toString();
            // Drop the last comma
            String adminUserNames = admin_names.substring(0, admin_names.length() - 1);

            selectedProject.setAdminUsernames(adminUserNames);
        }

        if (!selectedEvaluators.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();

            for (User evaluator : selectedEvaluators) {
                stringBuilder.append(evaluator.getUsername()).append(",");
            }

            String evaluator_names = stringBuilder.toString();
            // Drop the last comma
            String evaluatorUserNames = evaluator_names.substring(0, evaluator_names.length() - 1);

            selectedProject.setEvaluatorUsernames(evaluatorUserNames);
        }
        
        // Update evaluators in the indicators graph
        Indicator removedEvaluator = null;
        for (Indicator indicator : selectedProject.getIndicatorsGraph().getIndicatorList()) {
            if (indicator.isEvaluator() && selectedEvaluators.stream().noneMatch(eval -> eval.getUsername().equals(indicator.getName()))) {
                removedEvaluator = indicator;
                break;
            }
        }
        if (removedEvaluator != null) {
            selectedProject.getIndicatorsGraph().getIndicatorList().remove(removedEvaluator);
            for (Indicator indicator : selectedProject.getIndicatorsGraph().getIndicatorList()) {
                indicator.deleteComparisons(removedEvaluator);
                indicator.getChildIndicators().remove(removedEvaluator);
                indicator.getEvaluatorScores().remove(removedEvaluator.getName());
                indicator.getEvaluatorNotes().remove(removedEvaluator.getName());
            }
            selectedProject.getIndicatorsGraph().solve();
        }

        // Execute the update() method below to update the selected project
        update();

        return "/project/List?faces-redirect=true";
    }

    /*
    ********************************************************************
    Username of the deleted User must be removed from the adminUsernames
    and evaluatorUsernames attributes of all projects if it is included.
    ********************************************************************
     */
    public void updateAllProjectsAfterAUserIsDeleted(String username) {
        List<Project> allProjects = projectFacade.findAll();
        for (Project project : allProjects) {
            selectedProject = null;
            String adminUserNames = project.getAdminUsernames();
            String evaluatorUserNames = project.getEvaluatorUsernames();
            /*
             adminUserNames Example
             "JenniferLawrence,ScarlettJohansson,BradleyCooper,ChrisHemsworth"
             */
            if (adminUserNames.contains(username + ",")) {
                String newAdminUserNames = adminUserNames.replace(username + ",", "");
                project.setAdminUsernames(newAdminUserNames);
                selectedProject = project;
            } else if (adminUserNames.contains(username)) {
                String newAdminUserNames = adminUserNames.replace(username, "");
                project.setAdminUsernames(newAdminUserNames);
                selectedProject = project;
            }
            /*
             evaluatorUserNames Example
             "JenniferLawrence,ScarlettJohansson,BradleyCooper,ChrisHemsworth"
             */
            if (evaluatorUserNames.contains(username + ",")) {
                String newEvaluatorUserNames = evaluatorUserNames.replace(username + ",", "");
                project.setEvaluatorUsernames(newEvaluatorUserNames);
                selectedProject = project;
            } else if (evaluatorUserNames.contains(username)) {
                String newEvaluatorUserNames = evaluatorUserNames.replace(username, "");
                project.setEvaluatorUsernames(newEvaluatorUserNames);
                selectedProject = project;
            }

            if (selectedProject != null) {
                persist(PersistAction.UPDATE, "Project " + project.getTitle() + " is updated!");
            }
        }
    }

    // The constants CREATE, DELETE and UPDATE are defined in JsfUtil.java

    /*
    ********************
    Create a New Project
    ********************
     */
    public void create() {
        Methods.preserveMessages();
        persist(PersistAction.CREATE,"Project was Successfully Created!");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The CREATE operation is successfully performed.
            selectedProject = null;     // Remove selection
            listOfProjects = null;      // Invalidate listOfProjects to trigger re-query
        }
    }

    /*
     *******************************
     *   Update Selected Project   *
     *******************************
     */
    public void update() {
        Methods.preserveMessages();
        persist(PersistAction.UPDATE, "Project was Successfully Updated!");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The UPDATE operation is successfully performed.
            selectedProject = null;     // Remove selection
            listOfProjects = null;      // Invalidate listOfProjects to trigger re-query
        }
    }

    /*
     *******************************
     *   Delete Selected Project   *
     *******************************
     */
    public void destroy() {
        Methods.preserveMessages();
        persist(PersistAction.DELETE, "Project was Successfully Deleted!");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The DELETE operation is successfully performed.
            selectedProject = null;     // Remove selection
            listOfProjects = null;      // Invalidate listOfProjects to trigger re-query
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
        if (selectedProject != null) {
            try {
                if (persistAction != PersistAction.DELETE) {
                    /*
                     ------------------------------------------------
                     Perform CREATE or EDIT operation in the database
                     ------------------------------------------------
                     The edit(selectedProject) method performs the SAVE (STORE) operation of the "selectedProject"
                     object in the database regardless of whether the object is a newly
                     created object (CREATE) or an edited (updated) object (EDIT or UPDATE).

                     ProjectFacade inherits the edit(selectedProject) method from the AbstractFacade class.
                     */
                    projectFacade.edit(selectedProject);
                } else {
                    /*
                     ----------------------------------------
                     Perform DELETE operation in the database
                     ----------------------------------------
                     The remove(selectedProject) method performs the DELETE operation of the "selectedProject"
                     object in the database.

                     ProjectFacade inherits the remove(selectedProject) method from the AbstractFacade class.
                     */
                    projectFacade.remove(selectedProject);
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
