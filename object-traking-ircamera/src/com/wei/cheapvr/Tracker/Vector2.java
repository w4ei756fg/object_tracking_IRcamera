package com.wei.cheapvr.Tracker;

/**
 * 2-dimention vector
 *
 * @author wei
 */
public class Vector2 implements Number<Vector2>{
    
    /**
     * the component of this vector
     */
    public double x, y;
    
    /** 
     * Creates a vector with the given components
     * @param x The X-conponent
     * @param y The Y-conponent
     */
    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    /** 
     * Adds the given vector to this vector
     * @param other The vector
     *
     * @return      This vector added the given vector
     */
    public Vector2 plus(Vector2 other) {
        return new Vector2(x + other.x, y + other.y);
    }
    
    /** 
     * Substracts the given vector from this vector
     * @param other The vector
     *
     * @return This vector substracted the given vector
     */
    public Vector2 minus(Vector2 other) {
        return new Vector2(x - other.x, y - other.y);
    }
    
    /** 
     * Multiples the given value to this vector
     * @param r The real number
     *
     * @return  The real number multiple of this vector
     */
    public Vector2 mul(double r) {
        return new Vector2(x*r, y*r);
    }
    
    /** 
     * Inverts the sign of this vector
     *
     * @return the inverted this vector
     */
    public Vector2 invert() {
        return new Vector2(-x, -y);
    }
    
    /** 
     * Gets the distance from this vector to the given vector
     * @param other The vector
     *
     * @return      The distance
     */
    public double distTo(Vector2 other) {
        return Math.hypot(other.x - x, other.y - y);
    }
    
    /** 
     * Gets components of this vector to string
     *
     * @return String [x,y]
     */
    public String toString() { return "[" + x + ", " + y + "]"; }
    
    /** 
     * Gets the magnitude of this vector
     *
     * @return The magnitude
     */
    public double abs() {
        return Math.hypot(x, y);
    }
    
}