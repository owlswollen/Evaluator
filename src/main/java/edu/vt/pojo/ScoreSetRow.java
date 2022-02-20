/*
 * Created by Osman Balci on 2022.1.12
 * Copyright © 2022 Osman Balci. All rights reserved.
 */
package edu.vt.pojo;

// This class provides a Plain Old Java Object (POJO) representing a score set row
public class ScoreSetRow {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    String name;
    Double lowScore;
    Double highScore;

    /*
    ==================
    Constructor Method
    ==================
     */
    public ScoreSetRow(String name, Double lowScore, Double highScore) {
        this.name = name;
        this.lowScore = lowScore;
        this.highScore = highScore;
    }

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLowScore() {
        return lowScore;
    }

    public void setLowScore(Double lowScore) {
        this.lowScore = lowScore;
    }

    public Double getHighScore() {
        return highScore;
    }

    public void setHighScore(Double highScore) {
        this.highScore = highScore;
    }
}
