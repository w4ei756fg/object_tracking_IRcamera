package com.wei.cheapvr.Tracker;

import com.wei.cheapvr.Tracker.Quaternion;

/**
 * quaternion
 *
 * @author wei
 */
public class Quaternion implements Number<Quaternion> {
    public float w, x, y, z;
    
    /** 
     * Creates a quaternion with the given components
     * @param w The W-conponent
     * @param x The X-conponent
     * @param y The Y-conponent
     * @param z The Z-conponent
     */
    public Quaternion(float w, float x, float y, float z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /** 
     * Creates a quaternion with the given components
     * @param w The scala-conponent
     * @param v The vector-conponent
     */
    public Quaternion(float w, Vector3 v) {
        this.w = w;
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }
    

    /** 
     * Creates a quaternion with (0, [0,0,0])
     */
    public Quaternion() {
        this.w = 0;
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    /** 
     * Creates zero quaternion (0, [0,0,0])
     */
    public static Quaternion zero() {
        return new Quaternion();
    }
    
    /** 
     * Gets hamilton product of this vector and the given vector
     * @param w The W-conponent of other quaternion
     * @param x The X-conponent of other quaternion
     * @param y The Y-conponent of other quaternion
     * @param z The Z-conponent of other quaternion
     *
     * @return      This vector producted the given vector
     */
    public Quaternion mul(float w, float x, float y, float z) {
        float newW, newX, newY, newZ;
        newW = this.w*w - this.x*x - this.y*y - this.z*z;
        newX = this.w*x + this.x*w + this.y*z - this.z*y;
        newY = this.w*y - this.x*z + this.y*w + this.z*x;
        newZ = this.w*z + this.x*y - this.y*x + this.z*w;
        
        return new Quaternion(newW, newX, newY, newZ);
    }
    
    /** 
     * Gets hamilton product of this vector and the given vector
     * @param q The other quaternion
     *
     * @return      This vector producted the given vector
     */
    public Quaternion mul(Quaternion q) {
        return mul(q.w, q.x, q.y, q.z);
    }
    
    /** 
     * Multiples the given value to this vector
     * @param r The real number
     *
     * @return  The real number multiple of this quaternion
     */
    public Quaternion mul(float r) {
        return new Quaternion(w*r, x*r, y*r, z*r);
    }
    
    /** 
     * Adds the given quaternion to this quaternion
     * @param w The W-conponent of other quaternion
     * @param x The X-conponent of other quaternion
     * @param y The Y-conponent of other quaternion
     * @param z The Z-conponent of other quaternion
     *
     * @return      This quaternion added the given quaternion
     */
    public Quaternion plus(float w, float x, float y, float z) {
        return new Quaternion(this.w + w, this.x + x, this.y + y, this.z + z);
    }
    
    /** 
     * Adds the given quaternion to this quaternion
     * @param q The quaternion
     *
     * @return      This quaternion added the given quaternion
     */
    public Quaternion plus(Quaternion q) {
        return plus(q.w, q.x, q.y, q.z);
    }
    
    /** 
     * Substracts the given quaternion to this quaternion
     * @param w The W-conponent of other quaternion
     * @param x The X-conponent of other quaternion
     * @param y The Y-conponent of other quaternion
     * @param z The Z-conponent of other quaternion
     *
     * @return      This quaternion substracted the given quaternion
     */
    public Quaternion minus(float w, float x, float y, float z) {
        return new Quaternion(this.w - w, this.x - x, this.y - y, this.z - z);
    }
    
    /** 
     * Substracts the given quaternion to this quaternion
     * @param q The quaternion
     *
     * @return      This quaternion substracted the given quaternion
     */
    public Quaternion minus(Quaternion q) {
        return minus(q.w, q.x, q.y, q.z);
    }
    
    /** 
     * Return distance to other quaternion
     * @param other The quaternion
     *
     * @return      Distance
     */
    public float distTo(Quaternion other) {
        return sqrt(pow(abs(w - other.w), 2) + 
                        pow(abs(x - other.x), 2) + 
                        pow(abs(y - other.y), 2) + 
                        pow(abs(z - other.z), 2));
    }
    
    /** 
     * Multiples the given quaternion to this quaternion
     * @param r The quaternion
     *
     * @return  The quaternion multiple of this quaternion and the given quaternion
     */
    public Quaternion qProduct(Quaternion q) {
        float w1 = this.w, w2 = q.w;
        Vector3 v1 = this.toVector(), v2 = q.toVector();
        float w = w1*w2 - v1.dot(v2);
        Vector3 v = v2.mul(w1).plus(v1.mul(w2).plus(v1.cross(v2)));
        return new Quaternion(w, v);
    }
    
    /** 
     * Rotates this quaternion with euler angle
     * @param rx The pitch angle to rotate
     * @param ry The yaw angle to rotate
     * @param rz The roll angle to rotate
     *
     * @return  The quaternion multiple of this quaternion and the given angle
     */
    public static Quaternion getRollFromEuler(float rx, float ry, float rz) {
        // zyx
        Quaternion qx = Quaternion.getRoll(Vector3.X(), rx), qy = Quaternion.getRoll(Vector3.Y(), ry), qz = Quaternion.getRoll(Vector3.Z(), rz);
        
        return qx.qProduct(qy.qProduct(qz));
    }
    
    /** 
     * Rotates this quaternion with vector axis
     * @param axis The vector to rotate
     * @param rad The angle to rotate
     *
     * @return  The rotated quaternion of this
     */
    public Quaternion roll(Vector3 axis, float rad) {
        return roll(getRoll(axis, rad));
    }
    
    /** 
     * Rotates this quaternion with quaternion
     * @param q The quaternion to rotate
     *
     * @return  The rotated quaternion of this
     */
    public Quaternion roll(Quaternion q) {
        return q.mul(this).mul(q.conj());
    }
    
    /** 
     * Gets a rotation quaternion
     * @param axis The rotating shaft
     * @param rad The angle to rotate
     *
     * @return  The rotation quaternion
     */
    public static Quaternion getRoll(Vector3 axis, float rad) {
        return new Quaternion((float)Math.cos(rad/2), axis.norm().mul(1* (float)Math.sin(rad/2)));
    }

    /** 
     * Return absolute value of this quaternion
     *
     * @return The absolute value of this
     */
    public float abs() {
        return (float)Math.sqrt(w*w + x*x + y*y + z*z);
    }

    /** 
     * Return conjugated quaternion of this
     *
     * @return The absolute value of this
     */
    public Quaternion conj() {
        return new Quaternion(w, -x, -y, -z);
    }
    
    /** 
     * Converts this quaternion to vector
     *
     * @return The vector
     */
    public Vector3 toVector() {
        return new Vector3(x, y, z);
    }
    
    /** 
     * Return components of this quaternion
     *
     * @return The text of components of this quaternion
     */
    public String toString() {
        return "[" + w + "," + x + "," + y + "," + z + "]";
    }
    
    private static float sqrt(float x) { return (float)Math.sqrt(x); }
    private static float abs(float x) { return (float)Math.abs(x); }
    private static float pow(float a, float b) { return (float)Math.pow(a, b); }
}
