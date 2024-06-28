package com.sandhata.converter.impl.camunda8;

public class BPMNElement8 {

    private Class type;
    private Double height;
    private Double width;

    public BPMNElement8(Class inputType, Double inputHeight, Double inputWidth) {
        type = inputType;
        height = inputHeight;
        width = inputWidth;
    }

    public Class getType() {
        return type;
    }

    public Double getHeight() {
        return height;
    }

    public Double getWidth() {
        return width;
    }
}
