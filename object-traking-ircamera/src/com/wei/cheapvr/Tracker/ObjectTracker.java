package com.wei.cheapvr.Tracker;

import com.wei.cheapvr.Tracker.Quaternion;


public class ObjectTracker {
    private Structure target, source;
    private int[] map;
    //private float x, y, z, dx, dy, dz;
    private Vector3 pos = new Vector3(0, 0, 0);
    private Quaternion dir;
    public int setTarget(float[] data) {
        if(data.length % 3 != 0) {
            System.out.println("E:입력 데이터의 갯수는 3의 배수이어야 합니다.");
            return -2;
        }
        target = new Structure(data);
        return 0;
    }
    public int putData(float[] data) {
        if(data.length % 3 != 0) {
            System.out.println("E:입력 데이터의 갯수는 3의 배수이어야 합니다.");
            return -2;
        }
        source = new Structure(data);
        return 0;
    }
    
    public int updateTracking() {
        Vector3 tc = target.getSubCentroid(map), sc = source.getCentroid();
        Vector3 t1, s1, vt, vs; //R1
        Vector3 t2, s2, vs1, vs2, vs3, vt1, vt2, vt3; //R2
        Quaternion q;
        dir = Quaternion.zero();
        for(int i = 0; i < map.length - 1; i++) {
            
            t1 = target.get(map[i]);
            s1 = source.get(i);
            t2 = target.get(map[i + 1]);
            s2 = source.get(i + 1);
            
            //R1
            vt = t1.minus(tc);
            vs = s1.minus(sc);
            
            q = getRollToVector(vt, vs);
            
            //R2
            show("회전 전" + vt.angleOf(vs));
            vt1 = vt.roll(q);
            show("회전 후" + vt1.angleOf(vs));
            vs1 = vs;
            vt2 = t2.minus(tc).roll(q);
            vs2 = s2.minus(sc);
            
            vt3 = vt1.cross(vt2);
            vs3 = vs1.cross(vs2);
            
            float rad = getRadianToVector(vt3, vs3);
            Vector3 rp, rm; // +-방향으로 돌려볼 벡터
            rp = vt2.roll(vt1, rad);
            rm = vt2.roll(vt1, -rad);
            if(rp.angleOf(vs2) > rm.angleOf(vs2))
                rad = -rad;
            
            show("회전 전" + vt3.angleOf(vs3));
            vt2 = vt2.roll(vt1, rad);
            show("회전 후" + vt1.cross(vt2).angleOf(vs3));
            
            q = Quaternion.getRoll(vt1, rad).qProduct(q);
            show(" direction: " + q.toString());
            
            dir = dir.plus(q);
        }
        //방향 확정
        dir = dir.mul((float)1 / (float)(map.length - 1));
        
        //위치 target의 0,0,0이 오프셋임
        tc = target.getSubCentroid(map);
        sc = source.getCentroid();
        
        tc = tc.roll(dir);
        
        pos = sc.minus(tc).reduceError();
        
        return 0;
    }
    Quaternion getRollToVector(Vector3 source, Vector3 target) {
        return Quaternion.getRoll(source.cross(target), getRadianToVector(source, target));
    }
    float getRadianToVector(Vector3 source, Vector3 target) {
        Vector3 rp, rm; // +-방향으로 돌려볼 벡터
        float rad = source.angleOf(target);
        rp = source.roll(source.cross(target), rad);
        rm = source.roll(source.cross(target), -rad);
            if(rp.angleOf(target) > rm.angleOf(target))
                rad = -rad;
        return rad;
    }
    
    public int mapPoint() {
        int[] result = new int[source.length()];
        float[][] diff = new float[source.length()][target.length()];
        Vector3 p1, p2;
        float tmp;
        
        //유사도 구함
        for(int i = 0; i < source.length(); i++) {
            p1 = source.get(i);
            for(int j = 0; j < target.length(); j++) {
                p2 = target.get(j);
                tmp = 0f;
                for(int k = 1; k < source.length(); k++) {
                    int min = 0;
                    float dist1 = p1.distTo(source.get(p1.distance[k]));
                    for(int l = 1; l < target.length(); l++) {
                        float dist2 = p2.distTo(target.get(p2.distance[l]));
                        if(Math.abs(dist1 - dist2) < Math.abs(dist1 - p2.distTo(target.get(p2.distance[min])))) //min
                            min = l;
                    }
                    tmp += Math.abs(dist1 - p2.distTo(target.get(p2.distance[min]))); // p1:source[i]와 p2:target[j]의 유사도 합
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
        show("Centroid: " + target.getCentroid().toString());
        for(int i = 0; i < target.length(); i++) {
            show("pos[" + i + "]: " + target.get(i).toString());
            point = target.get(i);
            for(int j = 1; j < point.distance.length; j++) {
                point2 = target.get(point.distance[j]);
                show(" pos[" + point.distance[j] + "]: " + point2.toString() + " (dist: " + point.distTo(point2) + ")");
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


