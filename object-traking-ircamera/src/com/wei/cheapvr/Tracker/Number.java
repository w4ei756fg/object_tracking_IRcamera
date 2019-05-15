package com.wei.cheapvr.Tracker;

public interface Number<T> {
    T plus(T other);
    T minus(T other);
    T mul(float r);
    float distTo(T other);
    
    
    String toString();
    float abs();
}
