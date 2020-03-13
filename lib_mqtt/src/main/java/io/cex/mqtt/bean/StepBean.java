package io.cex.mqtt.bean;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class StepBean implements Serializable {
    @Expose
    public int type;
    public String code;
    public int decimalValue;
}
