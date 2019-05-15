package com.wei.cheapvr.Tracker;

import com.wei.cheapvr.Tracker.Quaternion;


public class Quaternion implements Number<Quaternion> {
    public float w, x, y, z;
    
    public Quaternion(float w, float x, float y, float z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Quaternion(float w, Vector3 v) {
        this.w = w;
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }
    public Quaternion() {}
    
    public static Quaternion zero() {
        return new Quaternion(0, 0, 0, 0);
    }
    
    public Quaternion mul(float w, float x, float y, float z) {
        float newW, newX, newY, newZ;
        newW = this.w*w - this.x*x - this.y*y - this.z*z;
        newX = this.w*x + this.x*w + this.y*z - this.z*y;
        newY = this.w*y - this.x*z + this.y*w + this.z*x;
        newZ = this.w*z + this.x*y - this.y*x + this.z*w;
        
        return new Quaternion(newW, newX, newY, newZ);
    }
    public Quaternion mul(Quaternion q) {
        return mul(q.w, q.x, q.y, q.z);
    }
    public Quaternion mul(float r) {
        return new Quaternion(w*r, x*r, y*r, z*r);
    }
    public Quaternion plus(float w, float x, float y, float z) {
        return new Quaternion(this.w + w, this.x + x, this.y + y, this.z + z);
    }
    public Quaternion plus(Quaternion q) {
        return plus(q.w, q.x, q.y, q.z);
    }
    public Quaternion minus(float w, float x, float y, float z) {
        return new Quaternion(this.w - w, this.x - x, this.y - y, this.z - z);
    }
    public Quaternion minus(Quaternion q) {
        return minus(q.w, q.x, q.y, q.z);
    }
    public float distTo(Quaternion other) {
        return sqrt(pow(abs(w - other.w), 2) + 
                        pow(abs(x - other.x), 2) + 
                        pow(abs(y - other.y), 2) + 
                        pow(abs(z - other.z), 2));
    }
    public Quaternion qProduct(Quaternion q) {
        float w1 = this.w, w2 = q.w;
        Vector3 v1 = this.toVector(), v2 = q.toVector();
        float w = w1*w2 - v1.dot(v2);
        Vector3 v = v2.mul(w1).plus(v1.mul(w2).plus(v1.cross(v2)));
        return new Quaternion(w, v);
    }
    
    
    
    public static Quaternion getRollFromEuler(float rx, float ry, float rz) {
        // zyx
        Quaternion qx = Quaternion.getRoll(Vector3.X(), rx), qy = Quaternion.getRoll(Vector3.Y(), ry), qz = Quaternion.getRoll(Vector3.Z(), rz);
        
        return qx.qProduct(qy.qProduct(qz));
    }
    
    
    public Quaternion roll(Vector3 axis, float rad) {
        return roll(getRoll(axis, rad));
    }
    public Quaternion roll(Quaternion q) {
        return q.mul(this).mul(q.conj());
    }
    public static Quaternion getRoll(Vector3 axis, float rad) {
        return new Quaternion((float)Math.cos(rad/2), axis.norm().mul(1* (float)Math.sin(rad/2)));
    }
    public float abs() {
        return (float)Math.sqrt(w*w + x*x + y*y + z*z);
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
    
    private static float sqrt(float x) { return (float)Math.sqrt(x); }
    private static float abs(float x) { return (float)Math.abs(x); }
    private static float pow(float a, float b) { return (float)Math.pow(a, b); }
}
