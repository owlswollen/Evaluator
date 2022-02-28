/*
 * Created by Osman Balci on 2022.1.7
 * Copyright © 2022 Osman Balci. All rights reserved.
 */
package edu.vt.FacadeBeans;

import edu.vt.EntityBeans.Project;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ProjectFacade extends AbstractFacade<Project> {
    /*
    ---------------------------------------------------------------------------------------------
    The EntityManager is an API that enables database CRUD (Create Read Update Delete) operations
    and complex database searches. An EntityManager instance is created to manage entities
    that are defined by a persistence unit. The @PersistenceContext annotation below associates
    the entityManager instance with the persistence unitName identified below.
    ---------------------------------------------------------------------------------------------
     */
    @PersistenceContext(unitName = "EvaluatorPU")
    private EntityManager entityManager;

    // Obtain the object reference of the EntityManager instance in charge of
    // managing the entities in the persistence context identified above.
    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    /*
    This constructor method invokes its parent AbstractFacade's constructor method,
    which in turn initializes its entity class type T and entityClass instance variable.
     */
    public ProjectFacade() {
        super(Project.class);
    }

    // Searches EvaluatorDB for projects whose administrator is the username given
    public List<Project> findProjectsWhoseAdminIsUsername(String username) {
        // Place the % wildcard before and after the search string to search for it anywhere in the admin_usernames column
        username = "%" + username + "%";
        // Conduct the search in a case-insensitive manner and return the results in a list.
        return getEntityManager().createQuery(
                "SELECT p FROM Project p WHERE p.adminUsernames LIKE :username")
                .setParameter("username", username)
                .getResultList();
    }

    // Searches EvaluatorDB for projects whose evaluator is the username given
    public List<Project> findProjectsWhoseEvaluatorIsUsername(String username) {
        // Place the % wildcard before and after the search string to search for it anywhere in the evaluator_usernames column
        username = "%" + username + "%";
        // Conduct the search in a case-insensitive manner and return the results in a list.
        return getEntityManager().createQuery(
                "SELECT p FROM Project p WHERE p.evaluatorUsernames LIKE :username")
                .setParameter("username", username)
                .getResultList();
    }
}
