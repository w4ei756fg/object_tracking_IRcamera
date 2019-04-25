package com.wei.cheapvr.Tracker;

public class Structure {
	private Vector3[] points;
	public Structure(int count) {
        points = new Vector3[count];
        for(int i = 0; i < points.length; i++) {
            points[i] = new Vector3();
            points[i].giveParent(this);
        }
    }
    Vector3 getPoint(int i) { return points[i]; }
	void setPointPos(int i, double x, double y, double z) { points[i].set(x, y, z); }
    public void setPointsPos(double[] data) {
        for(int i = 0; i < data.length; i += 3)
            points[i / 3].set(data[i], data[i + 1], data[i + 2]);
        for(int i = 0; i < points.length; i++)
            points[i].sortConnectionList();
    }
    void swapPoint (int i, int j){
        Vector3 temp;
        temp = points[i];
        points[i] = points[j];
        points[j] = temp;
    }
    double[] getCentroid() {
        double[] pos = {0,0,0};
        for(int i = 0; i < points.length; i++)
        for(int j = 0; j < 3; j++) {
            pos[j] += points[i].getPos()[j];
        }
        for(int j = 0; j < 3; j++)
            pos[j] /= (double)points.length;
        return pos;
    }
    double[] getSubCentroid(int[] map) {
        double[] pos = {0,0,0};
        for(int i = 0; i < map.length; i++)
        for(int j = 0; j < 3; j++) {
            pos[j] += points[map[i]].getPos()[j];
        }
        for(int j = 0; j < 3; j++)
            pos[j] /= (double)map.length;
        return pos;
    }
    public double[] getPointPos(int i) { return points[i].getPos(); }
    public void roll(double rx, double ry, double rz) {
        for(Vector3 p : points)
            p.roll(rx, ry, rz);
    }
    public int length() { return points.length; }
}

