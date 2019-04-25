package com.wei.cheapvr.Tracker;

public class Vector3 {
	public double x, y, z;
    private Structure parent;
    int[] distance;
    private Vector3 centroid;

    public Vector3() {}
    public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
    }
    public Vector3(double[] pos) {
		this.x = pos[0];
		this.y = pos[1];
		this.z = pos[2];
    }
    public static Vector3 X() { return new Vector3(1, 0, 0); }
    public static Vector3 Y() { return new Vector3(0, 1, 0); }
    public static Vector3 Z() { return new Vector3(0, 0, 1); }
    public Vector3 invert() {
        return new Vector3(-x, -y, -z);
    }
    public Vector3 plus(Vector3 p) {
        return new Vector3(x + p.x, y + p.y, z + p.z);
    }
    public Vector3 minus(Vector3 p) {
        return new Vector3(x - p.x, y - p.y, z - p.z);
    }
    public Vector3 mul(Vector3 p) {
        return new Vector3(x*p.x, y*p.y, z*p.z);
    }
    public Vector3 mul(double u) {
        return new Vector3(x*u, y*u, z*u);
    }
    public double getDistTo(Vector3 point) {
        return sqrt(pow(abs(x - point.x), 2) + 
                        pow(abs(y - point.y), 2) + 
                        pow(abs(z - point.z), 2));
    }
    void sortConnectionList() {
        centroid = new Vector3(parent.getCentroid());
        for(int i = 0; i < distance.length; i++) { distance[i] = i; }
        Qsort(distance, 0, distance.length - 1);
    }
    private void Qsort(int[] a, int left, int right){
        int i, last;
        if(left >= right) return;
        last = left;
        for(i = left + 1; i <= right; i++) {
            if(getDistTo(parent.getPoint(a[i])) < getDistTo(parent.getPoint(a[left]))) { // 거리
            //Vector3 self = this.addPoint(centroid.invertPoint());
                        //if(self.scalaProduct(parent.getPoint(a[i]).addPoint(centroid.invertPoint())) < self.scalaProduct(parent.getPoint(a[left]).addPoint(centroid.invertPoint()))) { // 내적
                swap(a, ++last, i);
            }
        }
        swap(a, left, last);
        Qsort(a, left, last - 1);
        Qsort(a, left + 1, right);
    }
    private void swap (int[] v, int i, int j){
        int temp;
        temp = v[i];
        v[i] = v[j];
        v[j] = temp;
    }
    void giveParent(Structure parent) {
        this.parent = parent;
        distance = new int[this.parent.length()];
    }
    public void set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
    }
    public void roll(double rx, double ry, double rz) {
        double x = this.x, y = this.y, z = this.z;
        this.x = (cos(rz)*cos(ry))*x + (cos(rz)*sin(ry)*sin(rx) - sin(rz)*cos(rx))*y + (sin(rz)*sin(rx) + cos(rz)*sin(ry)*cos(rx))*z;
        this.y = (sin(rz)*cos(ry))*x + (cos(rz)*cos(rx) + sin(rz)*sin(ry)*sin(rx))*y + (sin(rz)*sin(ry)*cos(rx) - cos(rz)*sin(rx))*z;
        this.z = -sin(ry)*x + cos(ry)*sin(rx)*y + cos(ry)*cos(rx)*z;
    }
    public Vector3 roll(Vector3 axis, double rad) {
        return roll(Quaternion.getRoll(axis, rad));
    }
    public Vector3 roll(Quaternion q) {
        return q.mul(new Quaternion(0, this)).mul(q.conj()).toVector();
    }
    public Vector3 cross(Vector3 p) {
        return new Vector3(y*p.z - z*p.y, z*p.x - x*p.z, x*p.y - y*p.x);
    }
    public double dot(Vector3 p) { return x*p.x + y*p.y + z*p.z; }
    double[] getPos() {
        double[] pos = {x, y, z};
		return pos;
    }
    public String toString() { return "[" + x + ", " + y + "," + z + "]"; }
    public double abs() { return Math.sqrt(x*x + y*y + z*z); }
    public Vector3 norm() { return this.mul(1/abs()) ;}
    public Vector3 reduceError() {
        return new Vector3(Math.round(x * 100000000000L) / 100000000000D, Math.round(y * 100000000000L) / 100000000000D, Math.round(z * 100000000000L) / 100000000000D);
    }
    private static double sqrt(double x) { return Math.sqrt(x); }
    private static double abs(double x) { return Math.abs(x); }
    private static double pow(double a, double b) { return Math.pow(a, b); }
    private static double sin(double x) { return Math.sin(x); }
    private static double cos(double x) { return Math.cos(x); }
}