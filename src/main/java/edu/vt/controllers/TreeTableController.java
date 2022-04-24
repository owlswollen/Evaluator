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
import edu.vt.pojo.IndicatorsGraph;
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
import javax.inject.Inject;
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

    // Parent options to show in select one menu while adding a new parent
    private List<String> parentOptionNames = new ArrayList<>();

    // Child options to show in select one menu while adding a new child
    private List<String> childOptionNames = new ArrayList<>();

    // Sibling options to show in select one menu while adding a new sibling
    private List<String> siblingOptionNames = new ArrayList<>();

    // AHP object holding the indicator hierarchy and alternatives
    private IndicatorsGraph indicatorsGraph;

    // DB ID of the serialized object
    private Long serializedId;

    @EJB
    private ProjectFacade projectFacade;

    @Inject
    private TabViewController tabViewController;

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

    public List<String> getParentOptionNames() {
        parentOptionNames = new ArrayList<>();
        getParentOptions(rootIndicator);
        return parentOptionNames;
    }

    public void setParentOptionNames(List<String> parentOptionNames) {
        this.parentOptionNames = parentOptionNames;
    }

    public List<String> getChildOptionNames() {
        childOptionNames = new ArrayList<>();
        getChildOptions(rootIndicator);
        return childOptionNames;
    }

    public void setChildOptionNames(List<String> childOptionNames) {
        this.childOptionNames = childOptionNames;
    }

    public List<String> getSiblingOptionNames() {
        siblingOptionNames = new ArrayList<>();
        getSiblingOptions(rootIndicator);
        return siblingOptionNames;
    }

    public void setSiblingOptionNames(List<String> siblingOptionNames) {
        this.siblingOptionNames = siblingOptionNames;
    }

    public IndicatorsGraph getIndicatorsGraph() {
        return indicatorsGraph;
    }

    public void setIndicatorsGraph(IndicatorsGraph indicatorsGraph) {
        this.indicatorsGraph = indicatorsGraph;
    }

    public TreeNode<Indicator> getActualRootTreeNode() {
        return actualRootTreeNode;
    }

    public void setActualRootTreeNode(TreeNode<Indicator> actualRootTreeNode) {
        this.actualRootTreeNode = actualRootTreeNode;
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
        sampleGraph = sampleGraph.createDefaultGraphAndTree(rootIndicator, indicatorsGraph, rootTreeNode, actualRootTreeNode);
        rootIndicator = sampleGraph.getRootIndicator();
        indicatorsGraph = sampleGraph.getAhp();
        rootTreeNode = sampleGraph.getRootTreeNode();
        actualRootTreeNode = sampleGraph.getActualRootTreeNode();
        rootTreeNode.setExpanded(true);
        actualRootTreeNode.setExpanded(true);
    }

    /*
     * Get options for the child to be added to the selected node among the existing nodes
     */
    private void getChildOptions(Indicator graphRoot) {
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
                // and if the current node is not one of the ancestors of the selected node
                // and if the current node is not one of the children of the selected node
                // and if the current node is not an evaluator
                // then add its name to the parent and child options list
                Indicator finalNode = node;
                if (!node.isRoot()
                        && !node.equals(selectedNode.getData())
                        && !causesCycleWhileAddingChild(selectedNode.getData(), node)
                        && selectedNode.getData().getChildIndicators().stream().noneMatch(object -> object.equals(finalNode))
                        && !node.isEvaluator()) {

                    childOptionNames.add(node.getName());
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

    /*
     * Check if the child selected to be added among the existing nodes causes a cycle in the graph
     */
    private boolean causesCycleWhileAddingChild(Indicator selectedNode, Indicator currentNode) {
        for (Indicator parent : selectedNode.getParentIndicators()) {
            if (parent.equals(currentNode)) {
                return true;
            }
            if (causesCycleWhileAddingChild(parent, currentNode)){
                return true;
            }
        }
        return false;
    }

    /*
     * Get options for the parent to be added to the selected node among the existing nodes
     */
    private void getParentOptions(Indicator graphRoot) {
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
                // and if the current node is not one of the descendents of the selected node
                // and if the current node is not an evaluator
                // then add its name to the parent and child options list
                if (!node.isRoot()
                        && !node.equals(selectedNode.getData())
                        && node.getChildIndicators().stream().noneMatch(object -> object.equals(selectedNode.getData()))
                        && !causesCycleWhileAddingParent(selectedNode.getData(), node)
                        && !node.isEvaluator()) {

                    parentOptionNames.add(node.getName());
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

    /*
     * Check if the parent selected to be added among the existing nodes causes a cycle in the graph
     */
    private boolean causesCycleWhileAddingParent(Indicator selectedNode, Indicator currentNode) {
        for (Indicator child : selectedNode.getChildIndicators()) {
            if (child.equals(currentNode)) {
                return true;
            }
            if (causesCycleWhileAddingParent(child, currentNode)){
                return true;
            }
        }
        return false;
    }

    /*
     * Get options for the sibling to be added to the selected node among the existing nodes
     */
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
                // and if the current node is not one of the siblings of the selected node
                // and if the current node is not one of the descendents of the selected node
                // and if the current node is not an evaluator
                // then add its name to the sibling options list
                Indicator finalNode = node;
                if (!node.isRoot()
                        && selectedNode.getParent().getChildren().stream().noneMatch(object -> object.getData().equals(finalNode))
                        && !causesCycleWhileAddingChild(selectedNode.getData(), node)
                        && !node.isEvaluator()) {
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

    // TODO: refactor open, retrieve, store, import, export
    public String openProject(Project selectedProject) {
        indicatorsGraph = selectedProject.getIndicatorsGraph();

        if (indicatorsGraph != null) {
            showGraphOnTreeTable(indicatorsGraph);
            rootTreeNode.setExpanded(true);
            actualRootTreeNode.setExpanded(true);
        } else {
            rootTreeNode = new DefaultTreeNode(null, null);
        }

        return "/project/Project?faces-redirect=true";
    }

    //--------------------------------------------------------
    // Methods for storing to DB and retrieving from DB
    //--------------------------------------------------------

    public void storeToDb() throws ClassNotFoundException, SQLException {
        Connection connection = createConnection();

        // serializing java object to mysql database
        serializedId = DatabaseSerializationManager.serializeJavaObjectToDB(connection, indicatorsGraph);

        FacesMessage message = new FacesMessage("Stored to DB", "Graph is serialized to database");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void retrieveFromDb() throws ClassNotFoundException, SQLException, IOException {
        Connection connection = createConnection();

        // de-serializing java object from mysql database
        indicatorsGraph = (IndicatorsGraph) DatabaseSerializationManager.deSerializeJavaObjectFromDB(connection, serializedId);
        connection.close();

        showGraphOnTreeTable(indicatorsGraph);
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
        BinarySerializationManager.storeGraph(indicatorsGraph);
        FacesMessage message = new FacesMessage("Graph Exported", "Serialized graph is saved in GraphData.bin");
        System.out.println(System.getProperty("user.dir"));
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void importGraph() {
        indicatorsGraph = BinarySerializationManager.retrieveGraph();
        showGraphOnTreeTable(indicatorsGraph);
    }

    //----------------------------------------------
    // Methods for displaying the graph on TreeTable
    //----------------------------------------------

    public void showGraphOnTreeTable(IndicatorsGraph newGraph) {
        indicatorsGraph = newGraph;
        rootIndicator = indicatorsGraph.getRoot();
        rootTreeNode = new DefaultTreeNode(null, null);
        actualRootTreeNode = new DefaultTreeNode(rootIndicator, rootTreeNode);
        addChildrenToTreeTableNodes(actualRootTreeNode, rootIndicator);
    }

    public void expandAllNodes(final TreeNode<Indicator> node) {
        for (final TreeNode<Indicator> child : node.getChildren()) {
            expandAllNodes(child);
        }
        node.setExpanded(true);
    }

    private void addChildrenToTreeTableNodes(TreeNode<Indicator> treeTableNode, Indicator graphNode) {
        for (int i = 0; i < graphNode.getChildIndicators().size(); i++) {
            if (!graphNode.getChildIndicators().get(i).isEvaluator()) {
                treeTableNode.getChildren().add(new DefaultTreeNode(graphNode.getChildIndicators().get(i)));
                addChildrenToTreeTableNodes(treeTableNode.getChildren().get(i), graphNode.getChildIndicators().get(i));
            }
        }
    }

    //--------------------------------------------------------
    // Create Root, Add Child, Add Sibling, Add Parent, Delete
    //--------------------------------------------------------

    /***************
     * CREATE ROOT *
     ***************/
    public void createRoot(Project selectedProject) {
        rootIndicator = new Indicator(newNodeName);

        // Run the AHP algorithm for the updated graph
        indicatorsGraph = new IndicatorsGraph(rootIndicator);
        indicatorsGraph.solve();

        rootTreeNode = new DefaultTreeNode(null, null);
        actualRootTreeNode = new DefaultTreeNode(rootIndicator, rootTreeNode);

        newNodeName = null;

        // Save graph to database
        saveGraph(selectedProject);
    }

    /**********************
     * ADD CHILD TO GRAPH *
     **********************/
    public void addChildToGraph(Project selectedProject) {

        // Check if the new node is already in the graph
        boolean existingIndicator = true;
        Indicator childIndicatorToAdd = null;
        childIndicatorToAdd = findNewNodeNameInGraph(rootIndicator, childIndicatorToAdd);
        // Show a warning if the new node is already in the graph
        if (childIndicatorToAdd != null) {
            FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, newNodeName + " is already in the graph.", "");
            FacesContext.getCurrentInstance().addMessage("successInfo", facesMsg);
            return;
        }

        // If it is not found in the graph create a new node with the given name and default attributes
        existingIndicator = false;

        // Create new indicator to add to the graph
        childIndicatorToAdd = new Indicator(newNodeName);

        // Find the indicator to which the child indicator will be added
        Indicator parentIndicator = null;
        parentIndicator = findSelectedNodeInIndicatorsGraph(rootIndicator, parentIndicator);

        // Remove the evaluators of the parent indicator if the parent indicator used to be a leaf indicator
        if (parentIndicator.isLeaf()) {
            for (Indicator evaluator : parentIndicator.getChildIndicators()) {
                evaluator.getParentIndicators().remove(parentIndicator);
                parentIndicator.deleteComparisons(evaluator);
            }
            parentIndicator.getChildIndicators().clear();
            parentIndicator.getEvaluatorScores().clear();
        }

        // Add the new indicator to the children of the found indicator
        parentIndicator.addChildIndicator(childIndicatorToAdd);

        // TODO: move this into Indicator
        // Add a row and a column for the new child indicator to the pairwise comparison matrix of the parent indicator
        // Set 1 to the newly added cells as the default comparison value
        for (Indicator siblingIndicator : parentIndicator.getChildIndicators()) {
            parentIndicator.compareIndicators(childIndicatorToAdd, siblingIndicator, 1.0);
        }

        // TODO: Move into TreeTableController
        if (childIndicatorToAdd.isLeaf()) {
            tabViewController.setScoresPropagated(true);
            indicatorsGraph.setSolved(true);
            saveGraph(selectedProject);
        }

        // Add the new node to the tree table
        addChildToTreeTable(rootTreeNode, childIndicatorToAdd, existingIndicator);

        // Run the AHP algorithm again for the updated graph
        indicatorsGraph = new IndicatorsGraph(rootIndicator);
        indicatorsGraph.solve();
        newNodeName = null;

        // Save graph to database
        saveGraph(selectedProject);
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
                    originalNode = findNewNodeNameInTreeTable(rootTreeNode, originalNode);

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
    public void addSiblingToGraph(Project selectedProject) {

        // Check if the new node is already in the graph
        boolean existingIndicator = true;
        Indicator siblingIndicatorToAdd = null;
        siblingIndicatorToAdd = findNewNodeNameInGraph(rootIndicator, siblingIndicatorToAdd);
        // Show a warning if the new node is already in the graph
        if (siblingIndicatorToAdd != null) {
            FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, newNodeName + " is already in the graph.", "");
            FacesContext.getCurrentInstance().addMessage("successInfo", facesMsg);
            return;
        }

        // If it is not found in the graph create a new node with the given name and default attributes
        existingIndicator = false;

        // Create new indicator to add to the graph
        siblingIndicatorToAdd = new Indicator(newNodeName);

        Indicator siblingIndicator = null;
        siblingIndicator = findSelectedNodeInIndicatorsGraph(rootIndicator, siblingIndicator);
        String commonParentName = "";
        commonParentName = findCommonParentName(rootTreeNode, commonParentName);
        for (Indicator parentIndicator : siblingIndicator.getParentIndicators()) {
            // Find the parent to which the new node will be added
            if (parentIndicator.getName().equals(commonParentName)) {
                // Add the new indicator to the children of the selected indicator's parent
                // Thus selected indicator and new indicator will be siblings
                parentIndicator.addChildIndicator(siblingIndicatorToAdd);

                // TODO: move this into Indicator
                // Add a row and a column for the new indicator to the pairwise comparison matrix of the parent indicator
                // Set 1 to the newly added cells as the default comparison value
                for (Indicator nodeToCompare : parentIndicator.getChildIndicators()) {
                    parentIndicator.compareIndicators(nodeToCompare, siblingIndicatorToAdd, 1);
                }
            }
        }

        // TODO: Move into TreeTableController
        if (siblingIndicatorToAdd.isLeaf()) {
            tabViewController.setScoresPropagated(true);
            indicatorsGraph.setSolved(true);
            saveGraph(selectedProject);
        }

        // Add the new node to the tree table
        addSiblingToTreeTable(rootTreeNode, siblingIndicatorToAdd, existingIndicator);

        // Run the AHP algorithm again for the updated graph
        indicatorsGraph = new IndicatorsGraph(rootIndicator);
        indicatorsGraph.solve();
        newNodeName = null;

        // Save graph to database
        saveGraph(selectedProject);
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
                    originalNode = findNewNodeNameInTreeTable(rootTreeNode, originalNode);

                    // When an Indicator object is present in multiple places in the acyclic graph,
                    // it is displayed as two different tree nodes with identical attributes in the tree table.
                    TreeNode<Indicator> copyNode = new DefaultTreeNode(nodeToAdd);
                    createCopy(originalNode, copyNode);

                    // Add the new node to the children of the selected node's parents
                    // Thus selected node and new node will be siblings
                    List<TreeNode<Indicator>> parentNodes = new ArrayList<>();
                    parentNodes = findNodesByName(rootTreeNode, treeNode.getParent().getData().getName(), parentNodes);
                    for (TreeNode<Indicator> parentNode : parentNodes) {
                        parentNode.getChildren().add(copyNode);
                    }
                } else {
                    // Add the new node to the children of the selected node's parents
                    List<TreeNode<Indicator>> parentNodes = new ArrayList<>();
                    parentNodes = findNodesByName(rootTreeNode, treeNode.getParent().getData().getName(), parentNodes);
                    for (TreeNode<Indicator> parentNode : parentNodes) {
                        parentNode.getChildren().add(new DefaultTreeNode(nodeToAdd));
                    }
                }
                break;
            }
            addSiblingToTreeTable(treeNode, nodeToAdd, existingIndicator);
        }
    }

    /***********************
     * ADD PARENT TO GRAPH *
     ***********************/
    public void addParentToGraph(Project selectedProject) {
        // Find the parent node in the graph
        Indicator parentIndicator = null;
        parentIndicator = findNewNodeNameInGraph(rootIndicator, parentIndicator);

        // Show a warning if it is not found
        if (parentIndicator == null) {
            FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Parent not found", "");
            FacesContext.getCurrentInstance().addMessage("successInfo", facesMsg);
            return;
        }

        // Remove the evaluators of the parent indicator added by default when the parent indicator used to be a leaf indicator
        if (parentIndicator.isLeaf()) {
            for (Indicator evaluator : parentIndicator.getChildIndicators()) {
                evaluator.getParentIndicators().remove(parentIndicator);
                parentIndicator.deleteComparisons(evaluator);
            }
            parentIndicator.getChildIndicators().clear();
            parentIndicator.getEvaluatorScores().clear();
        }

        // Add the selected indicator to the children of the found indicator
        parentIndicator.addChildIndicator(selectedNode.getData());

        // TODO: move this into indicator
        // Add a row and a column for the new child indicator to the pairwise comparison matrix of the parent indicator
        // Set 1 to the newly added cells as the default comparison value
        for (Indicator nodeToCompare : parentIndicator.getChildIndicators()) {
            parentIndicator.compareIndicators(nodeToCompare, selectedNode.getData(), 1);
        }

        // Add the new node to the tree table
        addParentToTreeTable(rootTreeNode);

        // Run the AHP algorithm again for the updated graph
        indicatorsGraph = new IndicatorsGraph(rootIndicator);
        indicatorsGraph.solve();
        newNodeName = null;

        // Save graph to database
        saveGraph(selectedProject);
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

    /*******************************
     * DELETE INDICATOR FROM GRAPH *
     *******************************/
    public void deleteIndicatorFromGraph(Project selectedProject) {
        if (selectedNode.getData().isRoot()) {
            indicatorsGraph = null;
            actualRootTreeNode = null;
        } else {
            // Find the indicator to delete
            Indicator indicatorToDelete = null;
            indicatorToDelete = findSelectedNodeInIndicatorsGraph(rootIndicator, indicatorToDelete);

            // Remove indicator from graph
            for (Indicator parent : indicatorToDelete.getParentIndicators()) {
                // Remove indicator from the child indicators of its parents
                parent.getChildIndicators().remove(indicatorToDelete);

                // Remove indicator from the child weights from its parents
                parent.getChildWeights().remove(indicatorToDelete);

                // Remove indicator from comparison matrices of its parents
                parent.deleteComparisons(indicatorToDelete);
            }
            // Remove indicator from the parents of its children
            for (Indicator child : indicatorToDelete.getChildIndicators()) {
                child.getParentIndicators().remove(indicatorToDelete);
            }

            // Run the AHP algorithm again for the updated graph
            indicatorsGraph = new IndicatorsGraph(rootIndicator);
            indicatorsGraph.solve();
        }
        // Delete node from tree table
        deleteNodeFromTreeTable();

        selectedNode = null;

        // Save graph to database
        saveGraph(selectedProject);
    }

    /*******************************
     * DELETE NODE FROM TREE TABLE *
     *******************************/
    public void deleteNodeFromTreeTable() {
        // Find the nodes to delete
        List<TreeNode<Indicator>> nodesToDelete = new ArrayList<>();
        findNodesByName(rootTreeNode, selectedNode.getData().getName(), nodesToDelete);
        for (TreeNode<Indicator> node : nodesToDelete) {
            node.getParent().getChildren().remove(node);
        }
    }

    //-----------------------------------------------------
    // Helper methods for adding child, sibling, and parent
    //-----------------------------------------------------

    /*
     * Find the selected node in the acyclic graph
     */
    private Indicator findSelectedNodeInIndicatorsGraph(Indicator graphRoot, Indicator indicatorFound) {
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
            indicatorFound = findSelectedNodeInIndicatorsGraph(childNode, indicatorFound);
        }
        return indicatorFound;
    }

    /*
     * Find indicator by its name in the acyclic graph
     */
    private Indicator findNewNodeNameInGraph(Indicator graphRoot, Indicator indicatorFound) {
        List<Indicator> subChildren = graphRoot.getChildIndicators();
        for (Indicator childNode : subChildren) {
            // Find the node whose name is entered as the new node in the acyclic graph
            if (childNode.getName().equals(newNodeName)) {
                return childNode;
            }
            indicatorFound = findNewNodeNameInGraph(childNode, indicatorFound);
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
    private TreeNode<Indicator> findNewNodeNameInTreeTable(TreeNode<Indicator> treeRoot, TreeNode<Indicator> foundNode) {
        List<TreeNode<Indicator>> subChildren = treeRoot.getChildren();
        for (TreeNode<Indicator> treeNode : subChildren) {
            // Find the node with the entered name as the new node name
            if (treeNode.getData().getName().equals(newNodeName)) {
                return treeNode;
            }
            foundNode = findNewNodeNameInTreeTable(treeNode, foundNode);
        }
        return foundNode;
    }

    /*
     * Find tree nodes by the name
     */
    private List<TreeNode<Indicator>> findNodesByName(TreeNode<Indicator> treeRoot, String name, List<TreeNode<Indicator>> foundNodes) {
        List<TreeNode<Indicator>> subChildren = treeRoot.getChildren();
        for (TreeNode<Indicator> treeNode : subChildren) {
            if (treeNode.getData().getName().equals(name)) {
                foundNodes.add(treeNode);
            }
            findNodesByName(treeNode, name, foundNodes);
        }
        return foundNodes;
    }

    /*
     * Save graph to database
     */
    private void saveGraph(Project selectedProject) {
        selectedProject.setIndicatorsGraph(indicatorsGraph);
        try {
            projectFacade.edit(selectedProject);
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
