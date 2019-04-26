package com.wei.cheapvr.Tracker;

public interface Number<T> {
    T plus(T other);
    T minus(T other);
    T mul(double r);
    double distTo(T other);
    
    
    String toString();
    double abs();
}
