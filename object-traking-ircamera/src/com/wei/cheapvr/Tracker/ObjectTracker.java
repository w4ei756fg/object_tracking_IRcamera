package com.wei.cheapvr.Tracker;

import com.wei.cheapvr.Tracker.Quaternion;


public class ObjectTracker {
    private Structure target, source;
    private int[] map;
    //private double x, y, z, dx, dy, dz;
    private Vector3 pos = new Vector3(0, 0, 0);
    private Quaternion dir;
    public int setTarget(double[] data) {
        if(data.length % 3 != 0) {
            System.out.println("E:입력 데이터의 갯수는 3의 배수이어야 합니다.");
            return -2;
        }
        target = new Structure(data.length / 3);
        target.setPointsPos(data);
        return 0;
    }
    public int putData(double[] data) {
        if(data.length % 3 != 0) {
            System.out.println("E:입력 데이터의 갯수는 3의 배수이어야 합니다.");
            return -2;
        }
        source = new Structure(data.length / 3);
        source.setPointsPos(data);
        return 0;
    }
    
    public int updateTracking() {
        Vector3 tc = new Vector3(target.getSubCentroid(map)), sc = new Vector3(source.getCentroid());
        Vector3 t1, s1, vt, vs; //R1
        Vector3 t2, s2, vs1, vs2, vt1, vt2; //R2
        Quaternion q;
        dir = Quaternion.zero();
        for(int i = 0; i < map.length - 1; i++) {
            
            t1 = target.getPoint(map[i]);
            s1 = source.getPoint(i);
            t2 = target.getPoint(map[i + 1]);
            s2 = source.getPoint(i + 1);
            
            //R1
            vt = t1.minus(tc);
            vs = s1.minus(sc);
            
            Vector3 rp, rm; // +-방향으로 돌려볼 벡터
            double rad = Math.acos( vt.dot(vs) / (vt.abs()*vs.abs()) );
            rp = (new Quaternion(0, vt).roll(Quaternion.getRoll(vt.cross(vs), rad))).toVector();
            rm = (new Quaternion(0, vt).roll(Quaternion.getRoll(vt.cross(vs), -rad))).toVector();
            if(Math.acos( rp.dot(vs) / (rp.abs()*vs.abs()) ) > Math.acos( rm.dot(vs) / (rp.abs()*vs.abs()) ))
                rad = -rad;
            
            q = Quaternion.getRoll(vt.cross(vs), rad);
            
            //R2
            show("회전 전" + Math.acos( vt.dot(vs) / (vt.abs()*vs.abs()) ));
            vt1 = (new Quaternion(0, vt).roll(q)).toVector();
            show("회전 후" + Math.acos( vt1.dot(vs) / (vt1.abs()*vs.abs()) ));
            vs1 = vs;
            vt2 = (new Quaternion(0, t2.minus(tc)).roll(q)).toVector();
            vs2 = s2.minus(sc);
            
            Vector3 z = Vector3.Z();
            Quaternion q2;
            rad = Math.acos( z.dot(vs1) / (z.abs()*vs1.abs()) );
            rp = (new Quaternion(0, vt1).roll(Quaternion.getRoll(z.cross(vs1), rad))).toVector();
            rm = (new Quaternion(0, vt1).roll(Quaternion.getRoll(z.cross(vs1), -rad))).toVector();
            if(Math.acos( rp.dot(z) / (rp.abs()*z.abs()) ) > Math.acos( rm.dot(z) / (rm.abs()*z.abs()) ))
                rad = -rad;
            
            q2 = Quaternion.getRoll(z.cross(vs1), rad);
            
            show("회전 전" + Math.acos( vt1.dot(z) / (vt1.abs()*z.abs()) ));
            vt1 = (new Quaternion(0, vt1).roll(q2)).toVector();
            show("회전 후" + Math.acos( vt1.dot(z) / (vt1.abs()*z.abs()) ));
            vs1 = (new Quaternion(0, vs1).roll(q2)).toVector();
            vt2 = (new Quaternion(0, vt2).roll(q2)).toVector();
            vs2 = (new Quaternion(0, vs2).roll(q2)).toVector();
            
            Vector3 s = new Vector3(vs2.x, vs2.y, 0), t = new Vector3(vt2.x, vt2.y, 0);
            
            
            rad = Math.acos( s.dot(t) / (s.abs()*t.abs()) );
            rp = (new Quaternion(0, t).roll(Quaternion.getRoll(z, rad))).toVector();
            rm = (new Quaternion(0, t).roll(Quaternion.getRoll(z, -rad))).toVector();
            if(Math.acos( rp.dot(s) / (rp.abs()*s.abs()) ) > Math.acos( rm.dot(s) / (rm.abs()*s.abs()) ))
                rad = -rad;
            
            q = Quaternion.getRoll(vs, rad).qProduct(q);
            
            
            
            show("회전 전" + Math.acos( s.dot(t) / (s.abs()*t.abs()) ));
            t = (new Quaternion(0, t).roll(Quaternion.getRoll(z, +rad))).toVector();
            show("회전 후" + Math.acos( s.dot(t) / (s.abs()*t.abs()) ));
            show(" direction: " + q.toString());
            
            dir = dir.plus(q);
        }
        //방향 확정
        dir = dir.mul((double)1 / (double)(map.length - 1));
        
        //위치 target의 0,0,0이 오프셋임
        tc = new Vector3(target.getSubCentroid(map));
        sc = new Vector3(source.getCentroid());
        
        tc = (new Quaternion(0, tc)).roll(dir).toVector();
        
        pos = sc.minus(tc).reduceError();
        
        return 0;
    }
    
    public int mapPoint() {
        int[] result = new int[source.length()];
        double[][] diff = new double[source.length()][target.length()];
        Vector3 p1, p2;
        double tmp;
        
        
        /*if(getDistTo(parent.getPoint(a[i])) < getDistTo(parent.getPoint(a[left]))) { // 거리
            Vector3 self = p1.addPoint(centroid.invertPoint());
                        if(self.dot(parent.getPoint(a[i]).addPoint(centroid.invertPoint())) < self.dot(parent.getPoint(a[left]).addPoint(centroid.invertPoint()))) { // 내적 */
        
        
        //유사도 구함
        for(int i = 0; i < source.length(); i++) {
            p1 = source.getPoint(i);
            for(int j = 0; j < target.length(); j++) {
                p2 = target.getPoint(j);
                tmp = 0d;
                for(int k = 1; k < source.length(); k++) {
                    int min = 0;
                    double dist1 = p1.getDistTo(source.getPoint(p1.distance[k]));
                    for(int l = 1; l < target.length(); l++) {
                        double dist2 = p2.getDistTo(target.getPoint(p2.distance[l]));
                        if(Math.abs(dist1 - dist2) < Math.abs(dist1 - p2.getDistTo(target.getPoint(p2.distance[min])))) //min
                            min = l;
                    }
                    tmp += Math.abs(dist1 - p2.getDistTo(target.getPoint(p2.distance[min]))); // p1:source[i]와 p2:target[j]의 유사도 합
                }
                diff[i][j] = tmp;
                System.out.println("[" + i + "," + j + "]" + tmp);
            }
        }
        for(int i = 0; i < source.length(); i++) {
            int min = 0;
            for(int j = 0; j < target.length(); j++)
                if(diff[i][j] < diff[i][min])
                    min = j;
                result[i] = min;
        }
        map = result.clone();
        return 0;
    }
    public void showMap() {
        show("I:source << target mapping data(ObjectTracker)");
        for(int i : map) show("" + i);
        show("===================================");
    }
    public void showTargetInfo() {
        Vector3 point, point2;
        show("I:Target information(ObjectTracker)");
        double[] pos = target.getCentroid();
        show("Centroid: (" + pos[0] + ", " + pos[1] + ", " + pos[2] + ")");
        for(int i = 0; i < target.length(); i++) {
            show("pos[" + i + "]: " + target.getPoint(i).toString());
            point = target.getPoint(i);
            for(int j = 1; j < point.distance.length; j++) {
                point2 = target.getPoint(point.distance[j]);
                pos = point2.getPos();
                show(" pos[" + point.distance[j] + "]: " + pos[0] + ", " + pos[1] + ", " + pos[2] + " (dist: " + point.getDistTo(point2) + ")");
            }
        }
        show("===================================");
    }
    public void showTrackingInfo() {
        show("I:Tracking information(ObjectTracker)");
        show(" position: " + pos.toString());
        show(" direction: " + dir.toString());
        
        show((new Vector3(50, 0, 0)).roll(dir).reduceError().toString());
        show("===================================");
    }
    private void show(String str) { System.out.println(str); }
}


