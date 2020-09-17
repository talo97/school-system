package com.schoolsystem.mark;

public enum EnumGrade {
    NONE(0d),
    ONE(1d),
    TWO(2d),
    THREE(3d),
    FOUR(4d),
    FIVE(5d),
    SIX(6d);

    private double value;

    public double getValue() {
        return value;
    }

    EnumGrade(double value) {
        this.value = value;
    }
}
