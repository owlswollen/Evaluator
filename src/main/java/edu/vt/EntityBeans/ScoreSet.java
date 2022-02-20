/*
 * Created by Osman Balci on 2022.1.11
 * Copyright © 2022 Osman Balci. All rights reserved.
 */
package edu.vt.EntityBeans;

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
@Table(name = "ScoreSet")
@NamedQueries({
    @NamedQuery(name = "ScoreSet.findAll", query = "SELECT s FROM ScoreSet s")
    , @NamedQuery(name = "ScoreSet.findById", query = "SELECT s FROM ScoreSet s WHERE s.id = :id")
    , @NamedQuery(name = "ScoreSet.findByTitle", query = "SELECT s FROM ScoreSet s WHERE s.title = :title")
    , @NamedQuery(name = "ScoreSet.findByDefinition", query = "SELECT s FROM ScoreSet s WHERE s.definition = :definition")})

public class ScoreSet implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "title")
    private String title;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1024)
    @Column(name = "definition")
    private String definition;

    /*
    =================
    Class Constructor
    =================
     */
    public ScoreSet() {
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

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
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
        if (!(object instanceof ScoreSet)) {
            return false;
        }
        ScoreSet other = (ScoreSet) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    // Return String representation of database primary key id
    @Override
    public String toString() {
        return id.toString();
    }
    
}
