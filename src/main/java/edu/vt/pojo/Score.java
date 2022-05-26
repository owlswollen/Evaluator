/*
 * Created by Gokce Onen on 2022.2.20
 * Copyright © 2022 Gokce Onen. All rights reserved.
 */
package edu.vt.pojo;

import java.io.Serializable;

public class Score implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private Double low;
    private Double high;

    /*
    ============
    Constructors
    ============
     */
    public Score() {
        this.low = 0.0;
        this.high = 0.0;
    }

    public Score(Double low, Double high) {
        this.low = low;
        this.high = high;
    }

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    /*
    ================
    Instance Methods
    ================
     */
    public Score multiply(Double x) {
        return new Score(this.low * x, this.high * x);
    }

    public void add(Score s) {
        this.low += s.low;
        this.high += s.high;
    }
}
