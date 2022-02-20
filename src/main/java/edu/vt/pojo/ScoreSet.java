/*
 * Created by Gokce Onen on 2022.2.20
 * Copyright © 2022 Gokce Onen. All rights reserved.
 */
package edu.vt.pojo;

import java.util.Map;

public class ScoreSet {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private Map<String, Score> score;

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public Map<String, Score> getScore() {
        return score;
    }

    public void setScore(Map<String, Score> score) {
        this.score = score;
    }
}


