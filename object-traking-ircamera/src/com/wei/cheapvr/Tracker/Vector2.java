package com.wei.cheapvr.Tracker;

public class Vector2 {
    public double x, y;
    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public Vector2 plus(Vector2 p) {
        return new Vector2(x + p.x, y + p.y);
    }
    public Vector2 minus(Vector2 p) {
        return new Vector2(x - p.x, y - p.y);
    }
    public Vector2 invert() {
        return new Vector2(-x, -y);
    }
    public String toString() { return "[" + x + ", " + y + "]"; }
    public double getDistanceTo(Vector2 p) {
        return Math.hypot(p.x - x, p.y - y);
    }
}