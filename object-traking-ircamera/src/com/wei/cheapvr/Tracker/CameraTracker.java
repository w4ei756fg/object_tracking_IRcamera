package com.wei.cheapvr.Tracker;

import java.util.ArrayList;
import java.util.Iterator;

public class CameraTracker {
    private ArrayList<Camera> cam = new ArrayList<Camera>();
    private ArrayList<Vector3> points = new ArrayList<Vector3>();
    public CameraTracker() {
        addCamera(61.2,1.19,4.15, 0, 0);
        addCamera(96.3 - 29.26,27.25,47.0, 0, -Math.PI/2);
        updateCamera();
        findPoint();
        
        showPoints();
    }
    public void addCamera(double x, double y, double z, double pan, double tilt) {
        Camera c = new Camera(x, y, z, pan, tilt);
        c.setCameraParameter(Camera.NOTE8_BACK);
        //c.setCameraParameter(100,100, Math.PI/4,Math.PI/4);
        
        cam.add(c);
    }
    public void updateCamera() {
        Iterator<Camera> iterator = cam.iterator();
        Camera c;
        while(iterator.hasNext()) {
            c = iterator.next();
            c.updateTraceLine();
            c.showInfo();
        }
    }
    public void findPoint() {
        double th = 0.6;
        points = new ArrayList<Vector3>();
        ArrayList<Vector3>[] tl = new ArrayList[cam.size()];
        for(int i = 0; i < tl.length; i++)
            tl[i] = cam.get(i).getTraceLine();
        
        double nearDist = th;
        Vector3 near = new Vector3(0, 0, 0);
        
        for(int i = 0; i < tl.length - 1; i++)
        for(int j = 0; j < 5/*tl[i].size()*/; j++) 
        for(int k = i + 1; k < tl.length; k++) {
            nearDist = th;
        for(int l = 5; l < 10/*tl[k].size()*/; l++)
        if (i != k) { // not self
            Vector3 p1 = cam.get(i).getPos(), v1 = tl[i].get(j), p2 = cam.get(k).getPos(), v2 = tl[k].get(l);
            double dist = distLineToLine(p1, v1, p2, v2);
            
            show("");
            
            if(dist < th) {
                show("I:점 감지됨");
                Vector3 v3 = v1.cross(v2).cross(v1); // 최단거리 벡터와 v1이 이루는 평면의 법선벡터
                double u = v3.dot(p1.minus(p2)) / v2.dot(v3);
                Vector3 p3 = p2.plus(v2.mul(u)); // l2측 최근점
                Vector3 p4 = p3.plus(v1.cross(v2).mul(-dist / v1.cross(v2).abs())); // l1측 최근점
                //show("직선 (p1,v1)측 근접점: " + p3.getX() + ", " + p3.getY() + ", " + p3.getZ());
                //show("직선 (p2,v2)측 근접점: " + p4.getX() + ", " + p4.getY() + ", " + p4.getZ());
                Vector3 p = p3.plus(p4).mul(0.5);
                if (dist < nearDist) { // 같은 카메라 안에서 가장 오차 적은 거만 인식
                    near = p;
                    nearDist = dist;
                }
                show("중간점: " + p.toString());
            }
            show("dist: " + dist + "(" + i + "번 카메라의 " + j + "번째 광선과 " + k + "번 카메라의 " + l + "번째 광선)" );
        }
            if (nearDist != th && near != null) {
                points.add(near);
                
            }
        }
    }
    double distLineToLine(Vector3 p1, Vector3 v1, Vector3 p2, Vector3 v2) {
        return Math.abs(p2.minus(p1).dot(v1.cross(v2))) / v1.cross(v2).abs();
    }
    public void showPoints() {
        Iterator<Vector3> it = points.iterator();
        while(it.hasNext()) {
            show(it.next().toString());
        }
    }
    
    private void show(String str) { System.out.println(str); }
}


class Camera {
    public static double[] NOTE8_BACK = {4032, 3024, 0.567005, 0.445538};
    private double x, y, z, pan, tilt;
    private double mw, mh, cx, cy, dw, dh; // 시야각 radian dw, dh
    private ArrayList<Vector2> points = new ArrayList<Vector2>();
    private ArrayList<Vector3> traceLine = new ArrayList<Vector3>();
    
    Camera(double x, double y, double z, double pan, double tilt) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pan = pan;
        this.tilt = tilt;
    }
    
    void setCameraParameter(double mw, double mh, double dw, double dh) {
        this.mw = mw;
        this.mh = mh;
        cx = this.mw / 2;
        cy = this.mh / 2;
        this.dw = dw;
        this.dh = dh;
    }
    void setCameraParameter(double[] profile) {
        this.mw = profile[0];
        this.mh = profile[1];
        cx = this.mw / 2;
        cy = this.mh / 2;
        this.dw = profile[2];
        this.dh = profile[3];
    }
    
    int updateTraceLine() {
        if(findPoint() == 0) return -2;
        traceLine = new ArrayList<Vector3>(); // reset list
        double proj = mw/2 / Math.tan(dw);
        Vector3 p;
        
        for(int i = 0; i < points.size(); i++) {
            Vector2 pp = points.get(i);
            p = new Vector3((pp.x - cx) / proj, (pp.y - cy) / proj, 1);
            //좌표계 변환
            /*
            p = p.mul(1/p.abs()); // norm
            Quaternion q = new Quaternion(0, p);
            q = q.roll(Quaternion.getRollFromEuler(-Math.PI/2 + tilt, 0, pan));
            p = q.toVector();
            */
            p.roll(-Math.PI/2 + tilt, 0, pan);
            traceLine.add(p);
        }
        
        return 0;
    }
     int findPoint() {
        points = new ArrayList<Vector2>(); // reset list
        
        points.add(new Vector2(1456, 1778));
        points.add(new Vector2(1309, 1993));
        points.add(new Vector2(2933, 2329));
        points.add(new Vector2(2833, 1865));
        points.add(new Vector2(2794, 1861));
        
        points.add(new Vector2(1118, 557));
        points.add(new Vector2(2135, 1144));
        points.add(new Vector2(2155, 1165));
        points.add(new Vector2(1885, 2306));
        points.add(new Vector2(1229, 1649));
        //points.add(new Vector2(mw/2 + Math.random()*6 - 3, mh/2 + Math.random()*6 - 3));
        //points.add(new Vector2(mw/2 + Math.random()*6 - 3, mh/2 + Math.random()*6 - 103));
        //random
        //for(int i = 0; i < tmp; i++)
        //    points.add(new Vector2(Math.random()*mw, Math.random()*mh));
        
        return points.size();
    }
    ArrayList<Vector3> getTraceLine() { return traceLine; }
    
    public void showInfo() {
        show("I:Camera information(Camera)");
        show("Centroid: (" + x + ", " + y + ", " + z + ")");
        show("direction: " + (pan/Math.PI*180) + ", " + (tilt/Math.PI*180));
        show("-----------------------------------");
        show("-Points");
        for(int i = 0; i < points.size(); i++) {
            Vector2 p = (Vector2)points.get(i);
            show("" + i + ": " + p.toString());
        }
        show("-----------------------------------");
        show("-TraceLine");
        for(int i = 0; i < traceLine.size(); i++) {
            Vector3 p = (Vector3)traceLine.get(i);
            show("" + i + ": " + p.toString());
        }
        show("===================================");
    }
    Vector3 getPos() { return new Vector3(x, y, z); }
    
    private void show(String str) { System.out.println(str); }
}
