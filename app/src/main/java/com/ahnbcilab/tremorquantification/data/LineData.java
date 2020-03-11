package com.ahnbcilab.tremorquantification.data;

public class LineData {
    private double total;
    private double hz;
    private double magnitude;
    private double distance;
    private double time;
    private double speed;

    public LineData(double total, double hz, double magnitude, double distance, double time, double speed) {
        this.total = total;
        this.hz = hz;
        this.magnitude = magnitude;
        this.distance = distance;
        this.time = time;
        this.speed = speed;
    }

    public double getTotal() {
        return total;
    }

    public double getHz() {
        return hz;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public double getDistance() {
        return distance;
    }

    public double getTime() {
        return time;
    }

    public double getSpeed() {
        return speed;
    }
}
