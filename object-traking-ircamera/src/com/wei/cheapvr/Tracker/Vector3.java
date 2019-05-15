package com.wei.cheapvr.Tracker;

/**
 * 3-dimention vector
 *
 * @author wei
 */
public class Vector3 implements Number<Vector3> {
    
    /**
     * the component of this vector
     */
    public float x, y, z;
    
    /**
     * the structure to which belongs this vector
     */
    private Structure parent;
    
    /**
     * the distances from the vector in the same structure as this vector
     */
    int[] distance;
    
    /** 
     * Creates a vector with (0,0,0)
     */
    public Vector3() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }
    
    /** 
     * Creates a vector with the given components
     * @param x The X-conponent
     * @param y The Y-conponent
     * @param z The Z-conponent
     */
    public Vector3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
    }
    
    /** 
     * Creates a vector with the given components
     * @param pos The conponents
     */
    public Vector3(float[] pos) {
		this.x = pos[0];
		this.y = pos[1];
		this.z = pos[2];
    }
    
    /** 
     * Gets X-axis direction vector
     * @return the vector
     */
    public static Vector3 X() { return new Vector3(1, 0, 0); }
    
    /** 
     * Gets Y-axis direction vector
     * @return the vector
     */
    public static Vector3 Y() { return new Vector3(0, 1, 0); }
    
    /** 
     * Gets Z-axis direction vector
     * @return the vector
     */
    public static Vector3 Z() { return new Vector3(0, 0, 1); }
    
    /** 
     * Inverts the sign of this vector
     *
     * @return the inverted this vector
     */
    public Vector3 invert() {
        return new Vector3(-x, -y, -z);
    }
    
    /** 
     * Adds the given vector to this vector
     * @param other The vector
     *
     * @return      This vector added the given vector
     */
    public Vector3 plus(Vector3 other) {
        return new Vector3(x + other.x, y + other.y, z + other.z);
    }
    
    /** 
     * Substracts the given vector from this vector
     * @param other The vector
     *
     * @return This vector substracted the given vector
     */
    public Vector3 minus(Vector3 other) {
        return new Vector3(x - other.x, y - other.y, z - other.z);
    }
    
    /** 
     * Multiples the given value to this vector
     * @param r The real number
     *
     * @return  The real number multiple of this vector
     */
    public Vector3 mul(float r) {
        return new Vector3(x*r, y*r, z*r);
    }
    
    /** 
     * Gets the distance from this vector to the given vector
     * @param other The vector
     *
     * @return      The distance
     */
    public float distTo(Vector3 other) {
        return sqrt(pow(abs(x - other.x), 2) + 
                        pow(abs(y - other.y), 2) + 
                        pow(abs(z - other.z), 2));
    }
    
    /** 
     * Gets the angle both this vector and the given vector
     * @param other The vector
     *
     * @return      The angle
     */
    public float angleOf(Vector3 other) {
        return (float)Math.acos((float)(this.dot(other) / (this.abs()*other.abs()) ));
    }
    
    /** 
     * Sorts array 'distance' 
     */
    void sortConnectionList() {
        
        for(int i = 0; i < distance.length; i++) { distance[i] = i; }
        Qsort(distance, 0, distance.length - 1);
    }
    
    /** 
     * Quick Sort Argorithm
     */
    private void Qsort(int[] a, int left, int right){
        int i, last;
        if(left >= right) return;
        last = left;
        for(i = left + 1; i <= right; i++) {
            if(distTo(parent.get(a[i])) < distTo(parent.get(a[left]))) { // 嫄곕━
            //Vector3 self = this.addPoint(centroid.invertPoint());
                        //if(self.scalaProduct(parent.getPoint(a[i]).addPoint(centroid.invertPoint())) < self.scalaProduct(parent.getPoint(a[left]).addPoint(centroid.invertPoint()))) { // �궡�쟻
                swap(a, ++last, i);
            }
        }
        swap(a, left, last);
        Qsort(a, left, last - 1);
        Qsort(a, left + 1, right);
    }
    
    /** 
     * Swaps both given value
     * @param v
     * @param i
     * @param j
     */
    private void swap (int[] v, int i, int j){
        int temp;
        temp = v[i];
        v[i] = v[j];
        v[j] = temp;
    }
    
    /** 
     * Sets parent structure of this vector
     * @param parent The sructure
     */
    void giveParent(Structure parent) {
        this.parent = parent;
        distance = new int[this.parent.length()];
    }
    
    /**
     * Sets this vector with the given value
     * @param x The X-conponent
     * @param y The Y-conponent
     * @param z The Z-conponent
     */
    public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
    }
    
    /**
     * Rotates this vector by Euler angle
     * @param rx The pitch angle
     * @param ry The yaw angle
     * @param rz The roll angle
     */
    public void roll(float rx, float ry, float rz) {
        float x = this.x, y = this.y, z = this.z;
        this.x = (cos(rz)*cos(ry))*x + (cos(rz)*sin(ry)*sin(rx) - sin(rz)*cos(rx))*y + (sin(rz)*sin(rx) + cos(rz)*sin(ry)*cos(rx))*z;
        this.y = (sin(rz)*cos(ry))*x + (cos(rz)*cos(rx) + sin(rz)*sin(ry)*sin(rx))*y + (sin(rz)*sin(ry)*cos(rx) - cos(rz)*sin(rx))*z;
        this.z = -sin(ry)*x + cos(ry)*sin(rx)*y + cos(ry)*cos(rx)*z;
    }
    
    /**
     * Rotates this vector around a axis
     * @param axis rotation axis
     * @param rad  angle to rotate
     *
     * @return     the rotated vector
     */
    public Vector3 roll(Vector3 axis, float rad) {
        return roll(Quaternion.getRoll(axis, rad));
    }
    
    /**
     * Rotates this vector with quaternion
     * @param q the quaternion
     *
     * @return  the rotated vector
     */
    public Vector3 roll(Quaternion q) {
        return q.mul(new Quaternion(0, this)).mul(q.conj()).toVector();
    }
    public Vector3 cross(Vector3 p) {
        return new Vector3(y*p.z - z*p.y, z*p.x - x*p.z, x*p.y - y*p.x);
    }
    public float dot(Vector3 p) { return x*p.x + y*p.y + z*p.z; }
    float[] getPos() {
        float[] pos = {x, y, z};
		return pos;
    }
    public String toString() { return "[" + x + ", " + y + "," + z + "]"; }
    public float abs() { return (float)Math.sqrt(x*x + y*y + z*z); }
    public Vector3 norm() { return this.mul(1/abs()) ;}
    public Vector3 reduceError() {
        return new Vector3(Math.round(x * 100000) / 100000F, Math.round(y * 100000) / 100000F, Math.round(z * 100000) / 100000F);
    }
    private static float sqrt(float x) { return (float)Math.sqrt(x); }
    private static float abs(float x) { return (float)Math.abs(x); }
    private static float pow(float a, float b) { return (float)Math.pow(a, b); }
    private static float sin(float x) { return (float)Math.sin(x); }
    private static float cos(float x) { return (float)Math.cos(x); }
}