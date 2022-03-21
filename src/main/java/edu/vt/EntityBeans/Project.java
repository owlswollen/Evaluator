/*
 * Created by Osman Balci on 2022.1.11
 * Copyright © 2022 Osman Balci. All rights reserved.
 */
package edu.vt.EntityBeans;

import edu.vt.pojo.IndicatorsGraph;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "Project")
@NamedQueries({
    @NamedQuery(name = "Project.findAll", query = "SELECT p FROM Project p")
    , @NamedQuery(name = "Project.findById", query = "SELECT p FROM Project p WHERE p.id = :id")
    , @NamedQuery(name = "Project.findByTitle", query = "SELECT p FROM Project p WHERE p.title = :title")})

public class Project implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "title")
    private String title;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 65535)
    @Column(name = "description")
    private String description;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1024)
    @Column(name = "admin_usernames")
    private String adminUsernames;

    @Size(min = 1, max = 6400)
    @Column(name = "evaluator_usernames")
    private String evaluatorUsernames;

//    @Size(min = 1, max = 16777215)
    @Column(name = "indicators_graph")
    private IndicatorsGraph indicatorsGraph;

    /*
    =================
    Class Constructor
    =================
     */
    public Project() {
    }

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdminUsernames() {
        return adminUsernames;
    }

    public void setAdminUsernames(String adminUsernames) {
        this.adminUsernames = adminUsernames;
    }

    public String getEvaluatorUsernames() {
        return evaluatorUsernames;
    }

    public void setEvaluatorUsernames(String evaluatorUsernames) {
        this.evaluatorUsernames = evaluatorUsernames;
    }

    public IndicatorsGraph getIndicatorsGraph() {
        return indicatorsGraph;
    }

    public void setIndicatorsGraph(IndicatorsGraph indicatorsGraph) {
        this.indicatorsGraph = indicatorsGraph;
    }

    /*
    ================================
    Instance Methods Used Internally
    ================================
     */

    // Generate and return a hash code value for the object with database primary key id
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    /*
     Checks if the Entity object identified by 'object' is the same as the Entity object identified by 'id'
     Parameter object = Entity object identified by 'object'
     Returns True if the Entity 'object' and 'id' are the same; otherwise, returns False
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Project)) {
            return false;
        }
        Project other = (Project) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    // Return String representation of database primary key id
    @Override
    public String toString() {
        return id.toString();
    }
    
}
