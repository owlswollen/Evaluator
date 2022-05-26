/*
 * Created by Gokce Onen on 2022.2.20
 * Copyright © 2022 Gokce Onen. All rights reserved.
 */
package edu.vt.pojo;

public class Comparison {

    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private Indicator indicator1;
    private Indicator indicator2;
    private Double value;
    private String formattedValue;

    /*
    ============
    Constructors
    ============
     */
    public Comparison() {
    }

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public Indicator getIndicator1() {
        return indicator1;
    }

    public void setIndicator1(Indicator indicator1) {
        this.indicator1 = indicator1;
    }

    public Indicator getIndicator2() {
        return indicator2;
    }

    public void setIndicator2(Indicator indicator2) {
        this.indicator2 = indicator2;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getFormattedValue() {
        formattedValue = String.format("%.5f", value);
        return formattedValue;
    }

    public void setFormattedValue(String formattedValue) {
        this.formattedValue = formattedValue;
    }

}
