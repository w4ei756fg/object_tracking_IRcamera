package com.wei.cheapvr.Tracker;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * CameraTracker
 * Find tracking points from images that IR cameras captured
 * , and save to 'points(ArrayList<Vector3>)'
 * CameraTracker can have camera objects. Cameras stored to 'cam(ArrayList<Camera>)'
 * 
 * 정확한 의미 전달을 위해 메소드 주석은 주로 한국어로 작성됨
 * For expressing exactly, this class's javadoc is written in Korean. Sorry for my bad English..
 *
 * @author wei
 * 
 * @see Camera
 * @see ImageLoader
 */
public class CameraTracker {
	
	/**
	 * CameraTracker에 등록된 카메라
	 */
    private ArrayList<Camera> cam = new ArrayList<Camera>();
	
	/**
	 * 카메라로부터 추적한 point들
	 */
    private ArrayList<Vector3> points = new ArrayList<Vector3>();
    
	/**
	 * CameraTracker에 새로운 카메라를 추가합니다
     * @param x 카메라의 X좌표
     * @param y 카메라의 Y좌표
     * @param z 카메라의 Z좌표
     * @param pan 카메라의 yaw방향
     * @param tilt 카메라의 pitch방향
     * @param ip 카메라의 source ip
     * 
     * @see Camera
	 */
    public void addCamera(float x, float y, float z, float pan, float tilt, String ip) {
        Camera c = new Camera(x, y, z, pan, tilt, ip);
        c.setCameraParameter(Camera.NOTE8_BACK);
        
        cam.add(c);
    }
    
	/**
	 * 모든 카메라로부터 traceLine을 새로 로드합니다
	 * 
	 * @see #findPoint()
	 * @see Camera#updateTraceLine()
	 */
    public void updateCamera() {
        Iterator<Camera> iterator = cam.iterator();
        Camera c;
        while(iterator.hasNext()) {
            c = iterator.next();
            c.updateTraceLine();
            c.showInfo();
        }
    }
    
	/**
	 * traceLine으로부터 point를 찾아 points에 저장합니다
	 * 
	 * @see #updateCamera()
	 * @see #points
	 */
    public void findPoint() {
    	updateCamera(); // Capture camera 
    	
        float th = 0.6f; // 이 값보다 먼 거리의 traceLine은 서로 다른 tracking point를 지나는 것으로 간주함
        points = new ArrayList<Vector3>();
        ArrayList<Vector3>[] tl = new ArrayList[cam.size()]; // i번 카메라에서 캡처된 traceLine들을 저장함
        for(int i = 0; i < tl.length; i++)
            tl[i] = cam.get(i).getTraceLine(); // camera에서 traceLine을 불러옵니다
        
        float nearDist = th;
        Vector3 near = new Vector3();
        
        //traceLine으로부터 교차점 찾기
        for(int i = 0; i < tl.length - 1; i++)
        for(int j = 0; j < tl[i].size(); j++) 
        for(int k = i + 1; k < tl.length; k++) {
            nearDist = th;
        for(int l = 0; l < tl[k].size(); l++)
        if (i != k) { // 비교하는 traceLine이 자신이 아닐 때만
            Vector3 p1 = cam.get(i).getPos(), v1 = tl[i].get(j), p2 = cam.get(k).getPos(), v2 = tl[k].get(l);
            float dist = distLineToLine(p1, v1, p2, v2);
            
            show("");
            
            if(dist < th) {
                show("I:점 감지됨");
                Vector3 v3 = v1.cross(v2).cross(v1); // 최단거리 벡터와 v1이 이루는 평면의 법선벡터  v3 = (v1×v2)×v1
                float u = v3.dot(p1.minus(p2)) / v2.dot(v3); // u = (v3·(p1-p2)) / (v2·v3)
                Vector3 p3 = p2.plus(v2.mul(u)); // l2측 최근점   p3 = p2 + u·v2
                Vector3 p4 = p3.plus(v1.cross(v2).mul(-dist / v1.cross(v2).abs())); // l1측 최근점  p4 = p3 + ( -dist / abs(v1×v2) )·(v1×v2)
                Vector3 p = p3.plus(p4).mul(0.5f); // (p3 + p4) / 2
                //show("직선 (p1,v1)측 근접점: " + p3.getX() + ", " + p3.getY() + ", " + p3.getZ());
                //show("직선 (p2,v2)측 근접점: " + p4.getX() + ", " + p4.getY() + ", " + p4.getZ());
                if (dist < nearDist) { // 같은 카메라 안에서 하나만 인식(고스트 현상 방지)
                    if (nearDist == th) {
                        near = p;
                        nearDist = dist;
                    } else {
                        near = null;
                        nearDist = -1;
                    }
                }
                show("중간점: " + p.toString());
            }
            show("dist: " + dist + "(" + i + "번 카메라의 " + j + "번째 광선과 " + k + "번 카메라의 " + l + "번째 광선)" );
        }
            if (nearDist != th && near != null) {
                points.add(near);
                
            }
        }
        
        showPoints();
    }
    float distLineToLine(Vector3 p1, Vector3 v1, Vector3 p2, Vector3 v2) {
    	/** 
    	 * return (p2 - p1)·(v1×v2) / abs(v1×v2)
    	 */
        return Math.abs(p2.minus(p1).dot(v1.cross(v2))) / v1.cross(v2).abs();
    }
    public void showPoints() {
        Iterator<Vector3> it = points.iterator();
        while(it.hasNext()) {
            show(it.next().toString());
        }
    }
    
    public Vector3[] getPoints() {
        Vector3[] point = new Vector3[points.size()];
        for(int i = 0; i < point.length; i++)
            point[i] = points.get(i);
        
        return point;
    }
    
    private void show(String str) { System.out.println(str); }
}


class Camera {
    public static float[] NOTE8_BACK = {4032, 3024, 0.567005f, 0.445538f};
    public static float[] OV2640 = {640, 480, 0.349344f, 0.265042f};
    private float x, y, z, pan, tilt;
    private String ip;
    private ImageLoader imgLoader;
    private float mw, mh, cx, cy, dw, dh; // 시야각 radian dw, dh
    private ArrayList<Vector2> points = new ArrayList<Vector2>();
    private ArrayList<Vector3> traceLine = new ArrayList<Vector3>();
    
    Camera(float x, float y, float z, float pan, float tilt, String ip) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pan = pan;
        this.tilt = tilt;
        this.ip = ip;
        imgLoader = new ImageLoader(this.ip);
    }
    
    void setCameraParameter(float mw, float mh, float dw, float dh) {
        this.mw = mw;
        this.mh = mh;
        cx = this.mw / 2;
        cy = this.mh / 2;
        this.dw = dw;
        this.dh = dh;
    }
    void setCameraParameter(float[] profile) {
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
        float proj = mw/2 / (float)Math.tan(dw);
        Vector3 p;
        
        for(int i = 0; i < points.size(); i++) {
            Vector2 pp = points.get(i);
            p = new Vector3((pp.x - cx) / proj, (pp.y - cy) / proj, 1);
            /* 좌표계 변환
             *
            p = p.mul(1/p.abs()); // norm
            Quaternion q = new Quaternion(0, p);
            q = q.roll(Quaternion.getRollFromEuler(-Math.PI/2 + tilt, 0, pan));
            p = q.toVector();
             */
            p.roll(-(float)Math.PI/2 + tilt, 0, pan);
            traceLine.add(p);
        }
        
        return 0;
    }
     int findPoint() {
        points = new ArrayList<Vector2>(); // reset list
        try {
			Vector2[] data = imgLoader.getData();
			
			for(Vector2 d : data)
				points.add(d);
			
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
        
        /*
         * load from local image (for test)
        try {
			ImageLoader loader = new ImageLoader("G:\\mk\\Desktop\\" + id + ".png");
			System.out.println("A:File opened(" + "G:\\mk\\Desktop\\" + id + ".png)");
			Vector2[] data = loader.getData();
			for(Vector2 d : data)
				points.add(d);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		*/
        /*
         * input data directly (for test)
        if (id == 0) {
        	points.add(new Vector2(1457, 1778));
        	points.add(new Vector2(1309, 1993));
        	points.add(new Vector2(2932, 2329));
        	points.add(new Vector2(2833, 1865));
        	points.add(new Vector2(2794, 1861));
        }

        if (id == 1) {
        	points.add(new Vector2(1118, 557));
        	points.add(new Vector2(2135, 1144));
        	points.add(new Vector2(2155, 1165));
        	points.add(new Vector2(1885, 2306));
        	points.add(new Vector2(1229, 1649));
        }
        */
        
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
