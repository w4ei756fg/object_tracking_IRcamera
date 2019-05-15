package com.wei.cheapvr.Tracker;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Collection containing vectors
 *
 * @author wei
 */
public class Structure {

	private ArrayList<Vector3> points = new ArrayList<Vector3>();
	
	public Structure(float[] data) {
        for(int i = 0; i < data.length; i += 3)
            points.add(new Vector3(data[i], data[i + 1], data[i + 2]));
        for(int i = 0; i < points.size(); i++) {
            points.get(i).giveParent(this);
            points.get(i).sortConnectionList();
        }
    }
    public Structure(Vector3[] data) {
        for(Vector3 p : data)
            points.add(p);
        for(int i = 0; i < points.size(); i++) {
            points.get(i).giveParent(this);
            points.get(i).sortConnectionList();
        }
    }
    
    public Vector3 get(int i) {
        return points.get(i);
    }
    
	public void set(int i, float x, float y, float z) {
	    points.get(i).set(x, y, z);
	}
	
	public void add(Vector3 p) {
	    points.add(p);
        for(int i = 0; i < points.size(); i++) {
            points.get(i).giveParent(this);
            points.get(i).sortConnectionList();
        }
    }
    
    void swapPoint (int i, int j){
        Vector3 ti = points.get(i), tj = points.get(j);
        points.remove(j);
        points.add(j, ti);
        points.remove(i);
        points.add(i, tj);
    }
    
    Vector3 getCentroid() {
        Vector3 centroid = new Vector3();
        Iterator<Vector3> it = points.iterator();
        while(it.hasNext())
            centroid = centroid.plus(it.next());
        
        return centroid.mul(1/(float)points.size());
    }
    
    Vector3 getSubCentroid(int[] map) {
        Vector3 centroid = new Vector3();
        for(int i = 0; i < map.length; i++)
            centroid = centroid.plus(points.get(i));
        
        return centroid.mul(1/(float)map.length);
    }
    
    /**
     * Rotates this structure by Euler angle
     * @param rx The pitch angle
     * @param ry The yaw angle
     * @param rz The roll angle
     */
    public void roll(float rx, float ry, float rz) {
        Iterator<Vector3> it = points.iterator();
        Vector3 p;
        while(it.hasNext()) {
            p = it.next();
            p.roll(rx, ry, rz);
        }
    }
    
    /**
     * Rotates this structure around a axis
     * @param axis rotation axis
     * @param rad  angle to rotate
     */
    public void roll(Vector3 axis, float rad) {
        roll(Quaternion.getRoll(axis, rad));
    }
    
    /**
     * Rotates this structure with quaternion
     * @param q the quaternion
     */
    public void roll(Quaternion q) {
        Iterator<Vector3> it = points.iterator();
        Vector3 p;
        while(it.hasNext()) {
            p = it.next();
            p = p.roll(q);
        }
    }
    
    public int length() {
        return points.size();
    }
}

