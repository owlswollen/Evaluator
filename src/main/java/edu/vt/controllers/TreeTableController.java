/*
 * Created by Gokce Onen on 2022.2.20
 * Copyright © 2022 Gokce Onen. All rights reserved.
 */
package edu.vt.controllers;

import edu.vt.EntityBeans.Project;
import edu.vt.FacadeBeans.ProjectFacade;
import edu.vt.controllers.util.JsfUtil;
import edu.vt.managers.BinarySerializationManager;
import edu.vt.managers.DatabaseSerializationManager;
import edu.vt.pojo.Ahp;
import edu.vt.pojo.Indicator;
import edu.vt.pojo.SampleProject;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("treeTableController")
@SessionScoped
public class TreeTableController implements Serializable {

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

    // Name of the new node added by the user
    private String newNodeName;

    // Selected node to which the new node will be added as a child, sibling, or parent
    private TreeNode<Indicator> selectedNode;

    // Parent or child options to show in select one menu while adding a new parent or a new child
    // Same list is used for both parent options and child options because both have the same conditions
    private List<String> parentAndChildOptionNames = new ArrayList<>();

    // Sibling options to show in select one menu while adding a new sibling
    private List<String> siblingOptionNames = new ArrayList<>();

    // Is the node to be added already in graph
    private boolean existingNode;

    // AHP object holding the indicator hierarchy and alternatives
    private Ahp ahp;

    // DB ID of the serialized object
    private Long serializedId;

    @EJB
    private ProjectFacade projectFacade;

    //=============
    // Constructors
    //=============

    @PostConstruct
    public void init() {
        rootIndicator = new Indicator("Temporary Root");

        // Create the default acyclic graph and show it in the tree table
//        createDefaultGraphAndTree();
    }

    //==========================
    // Getter and Setter Methods
    //==========================

    public TreeNode<Indicator> getRootTreeNode() {
        return rootTreeNode;
    }

    public String getNewNodeName() {
        return newNodeName;
    }

    public void setNewNodeName(String newNodeName) {
        this.newNodeName = newNodeName;
    }

    public TreeNode<Indicator> getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode<Indicator> selectedNode) {
        this.selectedNode = selectedNode;
    }

    public void setRootTreeNode(TreeNode<Indicator> rootTreeNode) {
        this.rootTreeNode = rootTreeNode;
    }

    public Indicator getRootIndicator() {
        return rootIndicator;
    }

    public void setRootIndicator(Indicator rootIndicator) {
        this.rootIndicator = rootIndicator;
    }

    public List<String> getParentAndChildOptionNames() {
        parentAndChildOptionNames = new ArrayList<>();
        getParentAndChildOptions(rootIndicator);
        return parentAndChildOptionNames;
    }

    public void setParentAndChildOptionNames(List<String> parentAndChildOptionNames) {
        this.parentAndChildOptionNames = parentAndChildOptionNames;
    }

    public List<String> getSiblingOptionNames() {
        siblingOptionNames = new ArrayList<>();
        getSiblingOptions(rootIndicator);
        return siblingOptionNames;
    }

    public void setSiblingOptionNames(List<String> siblingOptionNames) {
        this.siblingOptionNames = siblingOptionNames;
    }

    public boolean isExistingNode() {
        return existingNode;
    }

    public void setExistingNode(boolean existingNode) {
        this.existingNode = existingNode;
    }

    //=================
    // Instance Methods
    //=================

    /*
     * Create the default acyclic graph
     * and show it in the tree table
     */
    private void createDefaultGraphAndTree() {
        SampleProject sampleGraph = new SampleProject();
        sampleGraph = sampleGraph.createDefaultGraphAndTree(rootIndicator, ahp, rootTreeNode, actualRootTreeNode);
        rootIndicator = sampleGraph.getRootIndicator();
        ahp = sampleGraph.getAhp();
        rootTreeNode = sampleGraph.getRootTreeNode();
        actualRootTreeNode = sampleGraph.getActualRootTreeNode();
        rootTreeNode.setExpanded(true);
        actualRootTreeNode.setExpanded(true);
    }

    /*
     * The same method is used to get the parent options
     * and child options because both have the same conditions
     */
    private void getParentAndChildOptions(Indicator graphRoot) {
        // Using BFS to show the parent or child option names in a meaningful order in the select one menu
        Queue<Indicator> queue = new ArrayDeque<>();
        Indicator node;
        Set<Indicator> visited = new HashSet<>();

        visited.add(graphRoot);
        // Root node is added to the top of the queue
        queue.add(graphRoot);

        while (queue.size() != 0) {
            // Remove the top element of the queue
            node = queue.poll();

            if (selectedNode != null) {
                // If the current node is not the root node
                // and if the current node is not the same node with the selected node
                // and if the current node is not one of the parents of the selected node
                // and if the current node is not one of the children of the selected node
                // then add its name to the parent and child options list
                // TODO: Do not allow options that will cause a cycle in the graph
                // TODO: Do not show evaluators in the list
                if (!node.isRoot() && !node.getName().equals(selectedNode.getData().getName())
                        && node.getParentIndicators().stream().noneMatch(object -> object.getName().equals(selectedNode.getData().getName()))
                        && node.getChildIndicators().stream().noneMatch(object -> object.getName().equals(selectedNode.getData().getName()))) {

                    parentAndChildOptionNames.add(node.getName());
                }
            }

            for (Indicator child : node.getChildIndicators()) {
                // Only insert nodes into queue if they have not been explored already
                if (!visited.contains(child)) {
                    visited.add(child);
                    queue.add(child);
                }
            }
        }
    }

    private void getSiblingOptions(Indicator graphRoot) {
        // Using BFS to show the sibling option names in a meaningful order in the select one menu
        Queue<Indicator> queue = new ArrayDeque<>();
        Indicator node;
        Set<Indicator> visited = new HashSet<>();

        visited.add(graphRoot);
        // Root node is added to the top of the queue
        queue.add(graphRoot);

        while (queue.size() != 0) {
            // Remove the top element of the queue
            node = queue.poll();

            if (selectedNode != null) {
                // If the current node is not the root node
                // and if the current node is not the same node with the selected node
                // and if the current node is not one of the siblings of the selected node
                // and if the current node is not one of the children of the selected node
                // then add its name to the sibling options list
                // TODO: Do not allow options that will cause a cycle in the graph
                // TODO: Do not show evaluators in the list
                Indicator finalNode = node;
                if (!node.isRoot() && !node.getName().equals(selectedNode.getData().getName())
                        && selectedNode.getParent().getChildren().stream().noneMatch(object -> object.getData().getName().equals(finalNode.getName()))
                        && node.getChildIndicators().stream().noneMatch(object -> object.getName().equals(selectedNode.getData().getName()))) {
                    siblingOptionNames.add(node.getName());
                }
            }

            for (Indicator child : node.getChildIndicators()) {
                // Only insert nodes into queue if they have not been explored already
                if (!visited.contains(child)) {
                    visited.add(child);
                    queue.add(child);
                }
            }
        }
    }

    public void deleteGraph() {
        rootIndicator = null;
        rootTreeNode = null;
        selectedNode = null;
        ahp = null;
    }

    // TODO: refactor save, open, retrieve, store, import, export, delete
    public void saveGraph(Project selectedProject) {
        selectedProject.setIndicatorsGraph(ahp);
        try {
            projectFacade.edit(selectedProject);
            JsfUtil.addSuccessMessage("Indicators Graph was successfully saved!");
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

    public String openProject(Project selectedProject) {
        ahp = selectedProject.getIndicatorsGraph();

        if (ahp != null) {
            showRetrievedGraphOnTreeTable();
            rootTreeNode.setExpanded(true);
            actualRootTreeNode.setExpanded(true);
        }

        return "/project/Project?faces-redirect=true";
    }

    //--------------------------------------------------------
    // Methods for storing to DB and retrieving from DB
    //--------------------------------------------------------

    public void storeToDb() throws ClassNotFoundException, SQLException {
        Connection connection = createConnection();

        // serializing java object to mysql database
        serializedId = DatabaseSerializationManager.serializeJavaObjectToDB(connection, ahp);

        FacesMessage message = new FacesMessage("Stored to DB", "Graph is serialized to database");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void retrieveFromDb() throws ClassNotFoundException, SQLException, IOException {
        Connection connection = createConnection();

        // de-serializing java object from mysql database
        ahp = (Ahp) DatabaseSerializationManager.deSerializeJavaObjectFromDB(connection, serializedId);
        connection.close();

        showRetrievedGraphOnTreeTable();
    }

    private Connection createConnection() throws ClassNotFoundException, SQLException {
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/SerializedJavaObjectsDB";
        String username = "root";
        String password = "CSD@mysql-1872";

        Properties connectionProps = new Properties();
        connectionProps.put("user", username);
        connectionProps.put("password", password);

        Class.forName(driver);
        return DriverManager.getConnection(url, connectionProps);
    }

    //-------------------------------------------------------------
    // Methods for exporting and importing a graph as a binary file
    //-------------------------------------------------------------

    public void exportGraph() {
        BinarySerializationManager.storeGraph(ahp);
        FacesMessage message = new FacesMessage("Graph Exported", "Serialized graph is saved in GraphData.bin");
        System.out.println(System.getProperty("user.dir"));
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void importGraph() {
        ahp = BinarySerializationManager.retrieveGraph();
        showRetrievedGraphOnTreeTable();
    }

    //----------------------------------------------------------------
    // Methods for displaying retrieved or imported graph on TreeTable
    //----------------------------------------------------------------

    private void showRetrievedGraphOnTreeTable() {
        rootIndicator = ahp.getRoot();
        rootTreeNode = new DefaultTreeNode(null, null);
        actualRootTreeNode = new DefaultTreeNode(rootIndicator, rootTreeNode);
        addChildrenToTreeTableNodes(actualRootTreeNode, rootIndicator);
    }

    private void addChildrenToTreeTableNodes(TreeNode<Indicator> treeTableNode, Indicator graphNode) {
        for (int i = 0; i < graphNode.getChildIndicators().size(); i++) {
            if (!graphNode.getChildIndicators().get(i).isEvaluator()) {
                treeTableNode.getChildren().add(new DefaultTreeNode(graphNode.getChildIndicators().get(i)));
                addChildrenToTreeTableNodes(treeTableNode.getChildren().get(i), graphNode.getChildIndicators().get(i));
            }
        }
    }

    //-----------------------------------
    // Create Root, Add Child, Add Sibling, Add Parent
    //-----------------------------------

    // TODO: nodes without any children give error because of a bug in PrimeFaces TreeTable

    /**********************
     * ADD CHILD TO GRAPH *
     **********************/
    public void createRoot() {
        rootIndicator = new Indicator(newNodeName);

        // Run the AHP algorithm for the updated graph
        ahp = new Ahp(rootIndicator);
        ahp.solve();

        rootTreeNode = new DefaultTreeNode(null, null);
        actualRootTreeNode = new DefaultTreeNode(rootIndicator, rootTreeNode);

        newNodeName = null;
    }

    // TODO: add evaluators to newly added child

    /**********************
     * ADD CHILD TO GRAPH *
     **********************/
    public void addChildToGraph(ActionEvent event) {
        // Check if the new node is already in the graph
        Indicator childIndicatorToAdd = null;
        childIndicatorToAdd = findIndicatorByName(rootIndicator, childIndicatorToAdd);
        boolean existingIndicator = true;

        // If it is not found in the graph create a new node with the given name and default attributes
        if (childIndicatorToAdd == null) {
            existingIndicator = false;

            // Create new indicator to add to the graph
            childIndicatorToAdd = new Indicator(newNodeName);
        }

        // Find the indicator to which the child indicator will be added
        Indicator parentIndicator = null;
        parentIndicator = findIndicatorBySelectedNode(rootIndicator, parentIndicator);

        // Add the new indicator to the children of the found indicator
        parentIndicator.addChildIndicator(childIndicatorToAdd);

        // Add a row and a column for the new indicator to the pairwise comparison matrix of the parent indicator
        // Set 1 to the newly added cells as the default comparison value
        for (Indicator nodeToCompare : parentIndicator.getChildIndicators()) {
            parentIndicator.compareIndicators(nodeToCompare, childIndicatorToAdd, 1);
        }

        // Add the new node to the tree table
        addChildToTreeTable(rootTreeNode, childIndicatorToAdd, existingIndicator);

        // Run the AHP algorithm again for the updated graph
        ahp = new Ahp(rootIndicator);
        ahp.solve();

        newNodeName = null;
    }

    /***************************
     * ADD CHILD TO TREE TABLE *
     ***************************/
    private void addChildToTreeTable(TreeNode<Indicator> treeRoot, Indicator nodeToAdd, boolean existingIndicator) {
        List<TreeNode<Indicator>> subChildren = treeRoot.getChildren();
        for (TreeNode<Indicator> treeNode : subChildren) {
            // Find the selected node in the tree table
            if (treeNode.getData().getName().equals(selectedNode.getData().getName())) {

                if (existingIndicator) {
                    // Find the existing node
                    TreeNode<Indicator> originalNode = null;
                    originalNode = findNodeByNameInTreeTable(rootTreeNode, originalNode);

                    // When an Indicator object is present in multiple places in the acyclic graph,
                    // it is displayed as two different tree nodes with identical attributes in the tree table.
                    TreeNode<Indicator> copyNode = new DefaultTreeNode(nodeToAdd);
                    createCopy(originalNode, copyNode);
                    treeNode.getChildren().add(copyNode);
                } else {
                    // Add the new node to the children of the selected node
                    treeNode.getChildren().add(new DefaultTreeNode(nodeToAdd));
                }
                break;
            }
            addChildToTreeTable(treeNode, nodeToAdd, existingIndicator);
        }
    }

    /************************
     * ADD SIBLING TO GRAPH *
     ************************/
    public void addSiblingToGraph(ActionEvent event) {
        // Check if the new node is already in the graph
        Indicator siblingIndicatorToAdd = null;
        siblingIndicatorToAdd = findIndicatorByName(rootIndicator, siblingIndicatorToAdd);
        boolean existingIndicator = true;

        // If it is not found in the graph create a new node with the given name and default attributes
        if (siblingIndicatorToAdd == null) {
            existingIndicator = false;

            // Create new indicator to add to the graph
            siblingIndicatorToAdd = new Indicator(newNodeName);
        }

        Indicator siblingIndicator = null;
        siblingIndicator = findIndicatorBySelectedNode(rootIndicator, siblingIndicator);
        String commonParentName = "";
        commonParentName = findCommonParentName(rootTreeNode, commonParentName);
        for (Indicator parentIndicator : siblingIndicator.getParentIndicators()) {
            // Find the parent to which the new node will be added
            if (parentIndicator.getName().equals(commonParentName)) {
                // Add the new indicator to the children of the selected indicator's parent
                // Thus selected indicator and new indicator will be siblings
                parentIndicator.addChildIndicator(siblingIndicatorToAdd);

                // Add a row and a column for the new indicator to the pairwise comparison matrix of the parent indicator
                // Set 1 to the newly added cells as the default comparison value
                for (Indicator nodeToCompare : parentIndicator.getChildIndicators()) {
                    parentIndicator.compareIndicators(nodeToCompare, siblingIndicatorToAdd, 1);
                }
            }
        }

        // Add the new node to the tree table
        addSiblingToTreeTable(rootTreeNode, siblingIndicatorToAdd, existingIndicator);

        // Run the AHP algorithm again for the updated graph
        ahp = new Ahp(rootIndicator);
        ahp.solve();

        newNodeName = null;
    }

    /******************************
     * ADD SIBLING TO TREE TABLE *
     ******************************/
    private void addSiblingToTreeTable(TreeNode<Indicator> treeRoot, Indicator nodeToAdd, boolean existingIndicator) {
        List<TreeNode<Indicator>> subChildren = treeRoot.getChildren();
        for (TreeNode<Indicator> treeNode : subChildren) {
            // Find the selected node in the tree table
            if (treeNode.equals(selectedNode)) {
                if (existingIndicator) {
                    // Find the existing node
                    TreeNode<Indicator> originalNode = null;
                    originalNode = findNodeByNameInTreeTable(rootTreeNode, originalNode);

                    // When an Indicator object is present in multiple places in the acyclic graph,
                    // it is displayed as two different tree nodes with identical attributes in the tree table.
                    TreeNode<Indicator> copyNode = new DefaultTreeNode(nodeToAdd);
                    createCopy(originalNode, copyNode);

                    // Add the new node to the children of the selected node's parent
                    // Thus selected node and new node will be siblings
                    treeNode.getParent().getChildren().add(copyNode);
                } else {
                    // Add the new node to the children of the selected node's parent
                    treeNode.getParent().getChildren().add(new DefaultTreeNode(nodeToAdd));
                }
                break;
            }
            addSiblingToTreeTable(treeNode, nodeToAdd, existingIndicator);
        }
    }

    /***********************
     * ADD PARENT TO GRAPH *
     ***********************/
    public void addParentToGraph(ActionEvent event) {
        // Find the parent node in the graph
        Indicator parentIndicator = null;
        parentIndicator = findIndicatorByName(rootIndicator, parentIndicator);

        // Show a warning if it is not found
        if (parentIndicator == null) {
            FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "", "");
            FacesContext.getCurrentInstance().addMessage("successInfo", facesMsg);
            return;
        }

        // Add the selected indicator to the children of the found indicator
        parentIndicator.addChildIndicator(selectedNode.getData());

        // Add a row and a column for the new child indicator to the pairwise comparison matrix of the parent indicator
        // Set 1 to the newly added cells as the default comparison value
        for (Indicator nodeToCompare : parentIndicator.getChildIndicators()) {
            parentIndicator.compareIndicators(nodeToCompare, selectedNode.getData(), 1);
        }

        // Add the new node to the tree table
        addParentToTreeTable(rootTreeNode);

        // Run the AHP algorithm again for the updated graph
        ahp = new Ahp(rootIndicator);
        ahp.solve();

        newNodeName = null;
    }

    /****************************
     * ADD PARENT TO TREE TABLE *
     ****************************/
    private void addParentToTreeTable(TreeNode<Indicator> treeRoot) {
        List<TreeNode<Indicator>> subChildren = treeRoot.getChildren();
        for (TreeNode<Indicator> treeNode : subChildren) {
            // Find the name of the new node in the tree table
            if (treeNode.getData().getName().equals(newNodeName)) {

                // Add a copy of the selected node to it as its child
                TreeNode<Indicator> nodeToAdd = new DefaultTreeNode(selectedNode.getData());
                // When an Indicator object has multiple parents in the acyclic graph,
                // it is displayed as two different tree nodes with identical attributes in the tree table.
                createCopy(selectedNode, nodeToAdd);
                treeNode.getChildren().add(nodeToAdd);
            }
            addParentToTreeTable(treeNode);
        }
    }

    //-----------------------------------------------------
    // Helper methods for adding child, sibling, and parent
    //-----------------------------------------------------

    /*
     * Find the selected node in the acyclic graph
     */
    private Indicator findIndicatorBySelectedNode(Indicator graphRoot, Indicator indicatorFound) {
        if (graphRoot == selectedNode.getData()) {
            indicatorFound = graphRoot;
            return indicatorFound;
        }
        List<Indicator> subChildren = graphRoot.getChildIndicators();
        for (Indicator childNode : subChildren) {
            // Find the selected node in the acyclic graph
            if (childNode == selectedNode.getData()) {
                return childNode;
            }
            indicatorFound = findIndicatorBySelectedNode(childNode, indicatorFound);
        }
        return indicatorFound;
    }

    /*
     * Find indicator by its name in the acyclic graph
     */
    private Indicator findIndicatorByName(Indicator graphRoot, Indicator indicatorFound) {
        List<Indicator> subChildren = graphRoot.getChildIndicators();
        for (Indicator childNode : subChildren) {
            // Find the node whose name is entered as the new node in the acyclic graph
            if (childNode.getName().equals(newNodeName)) {
                return childNode;
            }
            indicatorFound = findIndicatorByName(childNode, indicatorFound);
        }
        return indicatorFound;
    }

    /*
     * Find the parent name of the selected sibling to add
     * the new sibling to the correct parent in the acyclic graph
     */
    private String findCommonParentName(TreeNode<Indicator> node, String commonParentName) {
        List<TreeNode<Indicator>> subChildren = node.getChildren();
        for (TreeNode<Indicator> treeNode : subChildren) {
            // Find the selected node in the tree table
            if (treeNode.equals(selectedNode)) {
                // Return common parent name to add the sibling to the correct parent in the acyclic graph
                return treeNode.getParent().getData().getName();
            }
            commonParentName = findCommonParentName(treeNode, commonParentName);
        }
        return commonParentName;
    }

    /*
     * Create copy of the selected tree node containing its subtree to add it as a child to another parent.
     * This is duplicate in the tree table because the TreeTable structure prevents the same tree node to be
     * added to multiple nodes. However, they are the same Indicator object in the acyclic Indicator graph.
     */
    private void createCopy(TreeNode<Indicator> original, TreeNode<Indicator> copy) {
        List<TreeNode<Indicator>> subChildren = original.getChildren();
        int index = 0;
        for (TreeNode<Indicator> treeNode : subChildren) {
            copy.getChildren().add(new DefaultTreeNode(treeNode.getData()));
            createCopy(treeNode, copy.getChildren().get(index));
            index++;
        }
    }

    /*
     * Find the tree node by the entered name "newNodeName"
     */
    private TreeNode<Indicator> findNodeByNameInTreeTable(TreeNode<Indicator> treeRoot, TreeNode<Indicator> treeNodeToAdd) {
        List<TreeNode<Indicator>> subChildren = treeRoot.getChildren();
        for (TreeNode<Indicator> treeNode : subChildren) {
            // Find the node with the entered name as the new node name
            if (treeNode.getData().getName().equals(newNodeName)) {
                return treeNode;
            }
            treeNodeToAdd = findNodeByNameInTreeTable(treeNode, treeNodeToAdd);
        }
        return treeNodeToAdd;
    }
}
