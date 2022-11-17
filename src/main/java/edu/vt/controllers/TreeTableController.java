/*
 * Created by Gokce Onen on 2022.2.20
 * Copyright © 2022 Gokce Onen. All rights reserved.
 */
package edu.vt.controllers;

import edu.vt.EntityBeans.Project;
import edu.vt.FacadeBeans.ProjectFacade;
import edu.vt.controllers.util.JsfUtil;
import edu.vt.globals.Constants;
import edu.vt.pojo.IndicatorsGraph;
import edu.vt.pojo.Indicator;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.file.UploadedFile;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("treeTableController")
@SessionScoped
public class TreeTableController implements Serializable {

    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    // Root of the indicators graph
    private Indicator rootIndicator;

    // A tree table to show the indicators graph in a tree structure
    // This is an artificial root to make the actual root visible in tree table
    // (PrimeFaces TreeTable does not display the root)
    private TreeNode<Indicator> rootTreeNode;

    // Actual root of the tree table
    private TreeNode<Indicator> actualRootTreeNode;

    // Name of the new node added by the user (as child, sibling, or parent)
    private String newNodeName;

    // Selected node to which the new node will be added as a child, sibling, or parent
    private TreeNode<Indicator> selectedNode;

    // Parent options to show in select one menu while adding a new parent
    private List<String> parentOptionNames = new ArrayList<>();

    // AHP object holding the indicator hierarchy and alternatives
    private IndicatorsGraph indicatorsGraph;

    // Opened project
    private Project selectedProject;

    // Previous expansion status of the TreeNodes in TreeTable
    Map<String, Boolean> expansionStatus;

    // Binary file of the indicators graph to be exported
    private StreamedContent indicatorsGraphFile;

    @EJB
    private ProjectFacade projectFacade;

    /*
    ============
    Constructors
    ============
     */
    @PostConstruct
    public void init() {
        rootIndicator = new Indicator("Temporary Root");
    }

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
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

    public StreamedContent getIndicatorsGraphFile() throws IOException {
        return exportIndicatorsGraph();
    }

    public void setIndicatorsGraphFile(StreamedContent indicatorsGraphFile) {
        this.indicatorsGraphFile = indicatorsGraphFile;
    }

    public Project getSelectedProject() {
        return selectedProject;
    }

    public void setSelectedProject(Project selectedProject) {
        this.selectedProject = selectedProject;
    }

    /*
    ================
    Instance Methods
    ================
     */
    /*
     * Open the Project Specification page showing selected project's indicators hierarchy
     */
    public String openProject(Project selectedProject) {
        this.selectedProject = selectedProject;
        indicatorsGraph = selectedProject.getIndicatorsGraph();

        if (indicatorsGraph != null) {
            showGraphOnTreeTable(indicatorsGraph, false);
            rootTreeNode.setExpanded(true);
            actualRootTreeNode.setExpanded(true);
        } else {
            rootTreeNode = new DefaultTreeNode(null, null);
        }
        checkExpansionStatus();

        return "/project/Project?faces-redirect=true";
    }

    //-------------------------------------------------------------
    // Root, Child, Sibling, Parent, Delete, Export, Import Buttons
    //-------------------------------------------------------------
    /*
     * Root
     */
    public void createRoot() {
        rootIndicator = new Indicator(newNodeName);

        // Run the AHP algorithm for the updated graph
        indicatorsGraph = new IndicatorsGraph(rootIndicator);
        indicatorsGraph.solve();

        rootTreeNode = new DefaultTreeNode(null, null);
        actualRootTreeNode = new DefaultTreeNode(rootIndicator, rootTreeNode);

        newNodeName = null;

        // Save graph to database
        saveGraph();
    }

    /*
     * Child
     */
    public void addChildToGraph() {
        // Check if the new node name is already in the indicators graph
        if (newNodeNameAlreadyInGraph()) {
            return;
        }

        // Create new indicator to add to the graph
        Indicator childIndicatorToAdd = new Indicator(newNodeName);

        // Find the indicator to which the child indicator will be added
        Indicator parentIndicator = findSelectedNodeInIndicatorsGraph();

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

        // Add a row and a column for the new child indicator to the pairwise comparison matrix of the parent indicator
        // Set 1 to the newly added cells as the default comparison value
        for (Indicator siblingIndicator : parentIndicator.getChildIndicators()) {
            parentIndicator.compareIndicators(childIndicatorToAdd, siblingIndicator, 1.0);
        }

        // Run the AHP algorithm again for the updated graph
        indicatorsGraph = new IndicatorsGraph(rootIndicator);
        indicatorsGraph.solve();
        newNodeName = null;

        // Show tree table with the new node added
        showGraphOnTreeTable(indicatorsGraph, true);

        // Save graph to database
        saveGraph();
    }

    /*
     * Sibling
     */
    public void addSiblingToGraph() {
        // Check if the new node name is already in the indicators graph
        if (newNodeNameAlreadyInGraph()) {
            return;
        }

        // Create new indicator to add to the graph
        Indicator siblingIndicatorToAdd = new Indicator(newNodeName);

        Indicator siblingIndicator = findSelectedNodeInIndicatorsGraph();
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

        // Run the AHP algorithm again for the updated graph
        indicatorsGraph = new IndicatorsGraph(rootIndicator);
        indicatorsGraph.solve();
        newNodeName = null;

        // Show tree table with the new node added
        showGraphOnTreeTable(indicatorsGraph, true);

        // Save graph to database
        saveGraph();
    }

    /*
     * Parent
     */
    public void addParentToGraph() {
        // Find the parent node in the graph
        Indicator parentIndicator = null;
        for (Indicator indicator : indicatorsGraph.getIndicatorList()) {
            if (indicator.getName().equals(newNodeName)) {
                parentIndicator = indicator;
                break;
            }
        }

        // Remove the evaluators of the parent indicator added by default when the parent indicator used to be a leaf indicator
        assert parentIndicator != null;
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

        // Add a row and a column for the new child indicator to the pairwise comparison matrix of the parent indicator
        // Set 1 to the newly added cells as the default comparison value
        for (Indicator nodeToCompare : parentIndicator.getChildIndicators()) {
            parentIndicator.compareIndicators(nodeToCompare, selectedNode.getData(), 1);
        }

        // Run the AHP algorithm again for the updated graph
        indicatorsGraph = new IndicatorsGraph(rootIndicator);
        indicatorsGraph.solve();
        newNodeName = null;

        // Show tree table with the new node added
        showGraphOnTreeTable(indicatorsGraph, true);

        // Save graph to database
        saveGraph();
    }

    /*
     * Delete
     */
    public void deleteIndicatorFromGraph() {
        if (selectedNode.getData().isRoot()) {
            indicatorsGraph = null;
            actualRootTreeNode = null;
        } else {
            // Find the indicator to delete
            Indicator indicatorToDelete = findSelectedNodeInIndicatorsGraph();

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

        selectedNode = null;

        // Show tree table without the deleted node
        showGraphOnTreeTable(indicatorsGraph, true);

        // Save graph to database
        saveGraph();
    }

    /*
     * Export
     */
    public StreamedContent exportIndicatorsGraph() throws IOException {
        String fileName = selectedProject.getTitle() + "-Exported.bin";
        String directory = Constants.FILES_ABSOLUTE_PATH;
        File file = new File(directory, fileName);
        FileOutputStream fileOut = new FileOutputStream(file);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);

        IndicatorsGraph graphToExport = new IndicatorsGraph(selectedNode.getData());
        out.writeObject(graphToExport);
        out.close();
        fileOut.close();
        FileInputStream fileStream = new FileInputStream(file);
        String contentType = FacesContext.getCurrentInstance().getExternalContext().getMimeType(file.getAbsolutePath());
        indicatorsGraphFile = DefaultStreamedContent.builder()
                .name(fileName)
                .contentType(contentType)
                .stream(() -> fileStream)
                .build();
        return indicatorsGraphFile;
    }

    /*
     * Import
     */
    public void importIndicatorsGraph(FileUploadEvent event) throws IOException {
        // Get the imported graph
        IndicatorsGraph importedBranch = null;
        UploadedFile uploadedFile = event.getFile();
        if (uploadedFile != null && uploadedFile.getContent() != null && uploadedFile.getContent().length > 0 && uploadedFile.getFileName() != null) {
            String filename = event.getFile().getFileName();
            try (InputStream inputStream = event.getFile().getInputStream()) {
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);
                File targetFile = new File(Constants.FILES_ABSOLUTE_PATH, filename);
                OutputStream outStream;
                outStream = new FileOutputStream(targetFile);
                outStream.write(buffer);
                outStream.close();

                FileInputStream fileIn = new FileInputStream(targetFile);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                importedBranch = (IndicatorsGraph) in.readObject();
                in.close();
                fileIn.close();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (importedBranch == null) {
            FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Indicators Graph is empty.", "");
            FacesContext.getCurrentInstance().addMessage(null, facesMsg);
        } else {
            // Parent indicators of the root indicator of the imported branch will change
            importedBranch.getRoot().setParentIndicators(new ArrayList<>());
            if (indicatorsGraph != null) {
                if (isBranchValidForImport(importedBranch)) {
                    // Remove the evaluators of the parent indicator if the parent indicator used to be a leaf indicator
                    Indicator selectedIndicator = selectedNode.getData();
                    if (selectedIndicator.isLeaf()) {
                        for (Indicator evaluator : selectedIndicator.getChildIndicators()) {
                            evaluator.getParentIndicators().remove(selectedIndicator);
                            selectedIndicator.deleteComparisons(evaluator);
                        }
                        selectedIndicator.getChildIndicators().clear();
                        selectedIndicator.getEvaluatorScores().clear();
                    }

                    // Add the imported branch to the children of the parent indicator
                    Indicator newIndicator = importedBranch.getRoot();
                    selectedIndicator.addChildIndicator(newIndicator);

                    // Add a row and a column for the new child indicator to the pairwise comparison matrix of the parent indicator
                    // Set 1 to the newly added cells as the default comparison value
                    for (Indicator siblingIndicator : selectedIndicator.getChildIndicators()) {
                        selectedIndicator.compareIndicators(newIndicator, siblingIndicator, 1.0);
                    }
                } else {
                    FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Branch could not be imported because it contains some indicators which are already in the indicators graph.", "");
                    FacesContext.getCurrentInstance().addMessage("successInfo", facesMsg);
                    return;
                }
            } else {
                indicatorsGraph = importedBranch;
            }
            saveGraph();
            showGraphOnTreeTable(indicatorsGraph, true);
        }
    }

    //-------------------------------------------------------------------------
    // Helper methods for adding child, sibling, parent, and an imported branch
    //-------------------------------------------------------------------------
    /*
     * Is new node name already in the indicators graph
     */
    private boolean newNodeNameAlreadyInGraph() {
        // Check if the new node name is already in the indicators graph
        boolean alreadyInGraph = false;
        for (Indicator indicator : indicatorsGraph.getIndicatorList()) {
            if (indicator.getName().equals(newNodeName)) {
                alreadyInGraph = true;
                break;
            }
        }

        // Show a warning if the new node is already in the graph
        if (alreadyInGraph) {
            FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, newNodeName + " is already in the graph.", "");
            FacesContext.getCurrentInstance().addMessage("successInfo", facesMsg);
            return true;
        }

        return false;
    }

    /*
     * Find the selected node in the indicators graph
     */
    private Indicator findSelectedNodeInIndicatorsGraph() {
        for (Indicator indicator : indicatorsGraph.getIndicatorList()) {
            if (indicator.getName().equals(selectedNode.getData().getName())) {
                return indicator;
            }
        }
        return null;
    }

    /*
     * Find the parent name of the selected sibling to add the new sibling
     * to the correct parent in the indicators graph
     */
    private String findCommonParentName(TreeNode<Indicator> node, String commonParentName) {
        List<TreeNode<Indicator>> subChildren = node.getChildren();
        for (TreeNode<Indicator> treeNode : subChildren) {
            // Find the selected node in the tree table
            if (treeNode.equals(selectedNode)) {
                // Return common parent name to add the sibling to the correct parent in the indicators graph
                return treeNode.getParent().getData().getName();
            }
            commonParentName = findCommonParentName(treeNode, commonParentName);
        }
        return commonParentName;
    }

    /*
     * Check if the imported branch has any indicators that exist in the indicators graph.
     * We do not let importing branches that has an indicator that exists in the indicators graph
     * because it can lead to inconsistency (e.g. the same indicator in two different places with different children).
     * If the user wants to add a branch which already exist in the graph, they can use the Add Parent functionality.
     */
    private boolean isBranchValidForImport(IndicatorsGraph importedBranch) {
        for (Indicator importedIndicator : importedBranch.getIndicatorList()) {
            for (Indicator existingIndicator : indicatorsGraph.getIndicatorList()) {
                if (importedIndicator.getName().equals(existingIndicator.getName())) {
                    return false;
                }
            }
        }
        return true;
    }

    /*
     * Get parent options to be added to the selected node
     * (displayed in SelectOneMenu in the Add Parent dialog)
     */
    private void getParentOptions(Indicator graphRoot) {
        // Using BFS to show the parent or child option names in a meaningful order in the select one menu
        // (First parent names, then child names)
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
                // then add its name to the parent options list
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
     * Check if the parent selected to be added causes a cycle in the graph
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
     * Save graph to database
     */
    private void saveGraph() {
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
            JsfUtil.addErrorMessage(ex, "A persistence error occurred!");
        }
    }

    //----------------------------------------------
    // Methods for displaying the graph on TreeTable
    //----------------------------------------------
    /*
     * Show the indicators graph in the TreeTable
     */
    public void showGraphOnTreeTable(IndicatorsGraph newGraph, boolean keepPreviousExpansion) {
        rootTreeNode = new DefaultTreeNode(null, null);
        indicatorsGraph = newGraph;
        if (indicatorsGraph != null) {
            rootIndicator = indicatorsGraph.getRoot();
            actualRootTreeNode = new DefaultTreeNode(rootIndicator, rootTreeNode);
            if (keepPreviousExpansion) {
                actualRootTreeNode.setExpanded(expansionStatus.getOrDefault(actualRootTreeNode.getData().getName(), false));
            }
            showGraphOnTreeTableRecursively(actualRootTreeNode, rootIndicator, keepPreviousExpansion);
        }
    }

    /*
     * Recursive function called by showGraphOnTreeTable
     */
    private void showGraphOnTreeTableRecursively(TreeNode<Indicator> treeTableNode, Indicator graphNode, boolean keepPreviousExpansion) {
        for (int i = 0; i < graphNode.getChildIndicators().size(); i++) {
            if (!graphNode.getChildIndicators().get(i).isEvaluator()) {
                DefaultTreeNode<Indicator> newNode = new DefaultTreeNode(graphNode.getChildIndicators().get(i));
                if (keepPreviousExpansion) {
                    newNode.setExpanded(expansionStatus.getOrDefault(graphNode.getChildIndicators().get(i).getName(), false));
                }
                treeTableNode.getChildren().add(newNode);
                showGraphOnTreeTableRecursively(treeTableNode.getChildren().get(i), graphNode.getChildIndicators().get(i), keepPreviousExpansion);
            }
        }
    }

    /*
     * Expand all child nodes in the TreeTable
     * Used in the TreeTable on the Evaluate page
     */
    public void expandAllNodes(final TreeNode<Indicator> node) {
        for (final TreeNode<Indicator> child : node.getChildren()) {
            expandAllNodes(child);
        }
        node.setExpanded(true);
    }

    /*
     * Get the previous expansion status of the tree nodes in TreeTable to keep the previous look after a new node added to the tree
     * Normally, only the root node is expanded by default after the TreeTable is updated (reset) by a node addition
     */
    public void checkExpansionStatus() {
        expansionStatus = new HashMap<>();
        checkExpansionStatusRecursively(rootTreeNode, expansionStatus);
    }

    /*
     * Recursive function called by getExpansionStatus
     */
    private void checkExpansionStatusRecursively(TreeNode<Indicator> treeRoot, Map<String, Boolean> expansionStatus) {
        for (TreeNode<Indicator> treeNode : treeRoot.getChildren()) {
            expansionStatus.put(treeNode.getData().getName(), treeNode.isExpanded());
            checkExpansionStatusRecursively(treeNode, expansionStatus);
        }
    }

    /*
     * Update expansion status on node expand
     */
    public void onNodeExpand(NodeExpandEvent event) {
        Indicator collapsedIndicator = (Indicator) event.getTreeNode().getData();
        expansionStatus.put(collapsedIndicator.getName(), true);
    }

    /*
     * Update expansion status on node collapse
     */
    public void onNodeCollapse(NodeCollapseEvent event) {
        Indicator collapsedIndicator = (Indicator) event.getTreeNode().getData();
        expansionStatus.put(collapsedIndicator.getName(), false);
    }
}
