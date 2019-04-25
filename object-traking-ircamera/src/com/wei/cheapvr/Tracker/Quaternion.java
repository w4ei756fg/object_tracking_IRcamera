package com.wei.cheapvr.Tracker;

import com.wei.cheapvr.Tracker.Quaternion;


public class Quaternion {
    public double w, x, y, z;
    
    public Quaternion(double w, double x, double y, double z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Quaternion(double w, Vector3 v) {
        this.w = w;
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }
    public Quaternion() {}
    
    public static Quaternion zero() {
        return new Quaternion(0, 0, 0, 0);
    }
    
    public Quaternion mul(double w, double x, double y, double z) {
        double newW, newX, newY, newZ;
        newW = this.w*w - this.x*x - this.y*y - this.z*z;
        newX = this.w*x + this.x*w + this.y*z - this.z*y;
        newY = this.w*y - this.x*z + this.y*w + this.z*x;
        newZ = this.w*z + this.x*y - this.y*x + this.z*w;
        
        return new Quaternion(newW, newX, newY, newZ);
    }
    public Quaternion mul(Quaternion q) {
        return mul(q.w, q.x, q.y, q.z);
    }
    public Quaternion mul(double r) {
        return new Quaternion(w*r, x*r, y*r, z*r);
    }
    public Quaternion plus(double w, double x, double y, double z) {
        return new Quaternion(this.w + w, this.x + x, this.y + y, this.z + z);
    }
    public Quaternion plus(Quaternion q) {
        return plus(q.w, q.x, q.y, q.z);
    }
    public Quaternion minus(double w, double x, double y, double z) {
        return new Quaternion(this.w - w, this.x - x, this.y - y, this.z - z);
    }
    public Quaternion minus(Quaternion q) {
        return minus(q.w, q.x, q.y, q.z);
    }
    public Quaternion qProduct(Quaternion q) {
        double w1 = this.w, w2 = q.w;
        Vector3 v1 = this.toVector(), v2 = q.toVector();
        double w = w1*w2 - v1.dot(v2);
        Vector3 v = v2.mul(w1).plus(v1.mul(w2).plus(v1.cross(v2)));
        return new Quaternion(w, v);
    }
    
    
    
    public static Quaternion getRollFromEuler(double rx, double ry, double rz) {
        // zyx
        Quaternion qx = Quaternion.getRoll(Vector3.X(), rx), qy = Quaternion.getRoll(Vector3.Y(), ry), qz = Quaternion.getRoll(Vector3.Z(), rz);
        
        return qx.qProduct(qy.qProduct(qz));
    }
    
    
    public Quaternion roll(Vector3 axis, double rad) {
        return roll(getRoll(axis, rad));
    }
    public Quaternion roll(Quaternion q) {
        return q.mul(this).mul(q.conj());
    }
    public static Quaternion getRoll(Vector3 axis, double rad) {
        return new Quaternion(Math.cos(rad/2), axis.norm().mul(1* Math.sin(rad/2)));
    }
    public double abs() {
        return Math.sqrt(w*w + x*x + y*y + z*z);
    }
    public Quaternion conj() {
        return new Quaternion(w, -x, -y, -z);
    }
    public Vector3 toVector() {
        return new Vector3(x, y, z);
    }
    public String toString() {
        return "[" + w + "," + x + "," + y + "," + z + "]";
    }
}
