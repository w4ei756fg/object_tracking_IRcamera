package com.wei.cheapvr;

import com.wei.cheapvr.Tracker.*;
import static com.wei.cheapvr.Utils.*;

public class Main {
    final static float ERROR_DEADZONE = 5;

    /**
     * @param args
     */
    public static void main(String[] args) {
        //*
	    float[] hmd_data = {0f,0f,0f, 
                             100f,0f,0f, 
                             90f,90f,0f, 
                             0f,40f,0f};
        float[] img_data = {93.30127f,25f,0f, 
                             6.698729f,-25f,0f,
                             39.641016f,97.942286f,0f, 
                             -13.3015f,9.639480f,0f};
        float[] img2_data = {0.0f, 0.0f, 0.0f,
0.0f, 0.0f, -100.0f,
0.0f, 90.0f, -90.0f,
0.0f, 40.0f, 0.0f};
        float[] img3_data = {3.0f, 0.0f, 0.0f,
3.0f, 90.0f, -90.0f,
3.0f, 0.0f, -100.0f,
3.0f, 40.0f, 0.0f};
        /*
        Structure str = new Structure(4);
        str.setPointsPos(hmd_data);
        for(int i = 0; i < str.length(); i++)
            show("" + str.getPointPos(i)[0] + ", " + str.getPointPos(i)[1] + ", " + str.getPointPos(i)[2]);
        str.roll(0, 90d/180d*Math.PI, 0);
        for(int i = 0; i < str.length(); i++)
            show("" + str.getPointPos(i)[0] + ", " + str.getPointPos(i)[1] + ", " + str.getPointPos(i)[2]);
        //float[] hmd_data = new float[30];
        //for(int i = 0; i < 30; i++) hmd_data[i] = Math.random() * 100;
        //*/
        //*

        //testImageLoader("G:\\mk\\Desktop\\1556291731463.png");

        //testImageLoader("G:\\mk\\Desktop\\1556291807438.png");
        
        //testImageLoader("G:\\mk\\Desktop\\20190427_000530.jpg");
        //----
        
        //ImageLoader imageLoader = new ImageLoader("172.30.1.55");
        
        
        //-----
        //testObjectTracker(hmd_data, img3_data);
        
        //--------
        
        //*/
        
        testCameraTracker();
        //------
        
        //쿼터니언 테스트
        /*
        Quaternion p = new Quaternion(0, 2, 0, 0);
        show(p.toString());
        
        float rad = Math.PI/2;
        Vector3 axis = new Vector3(0, 0, 1);
        show(axis.norm().toString());
        Quaternion q1 = Quaternion.getRoll(axis, rad);
        
        
        axis = new Vector3(1, 0, 0);
        show(axis.norm().toString());
        Quaternion q2 = Quaternion.getRoll(axis, rad);
        
        show(q1.toString());
        show(q2.toString());
        show(q2.qProduct(q1).toString());
        show(p.roll(q2.qProduct(q1)).toString());
        //*/
    }
    
    public static void testCameraTracker() {
    	CameraTracker ct = new CameraTracker();

        ct.addCamera(13.6f, 0f, 0f, 0.03f, 0f, Camera.OV2640_CIF, "172.30.1.91");
        ct.addCamera(0f, 0f, 0f, 0f, 0f, Camera.OV2640_CIF, "172.30.1.92");
        //ct.addCamera(0f, 0f, 14f, -0.85582f, -0.680354f, "172.30.1.91");
        //ct.addCamera(29.7f, 0f, 14f, 0.950546f, -0.683191f, "172.30.1.92");
        //ct.addCamera(61.2,1.19,4.15, 0, 0, 0);
        //ct.addCamera(96.3 - 29.26,27.25,47.0, 0, -Math.PI/2, 1);

        TrackingViewer viewer = new TrackingViewer(ct);
        viewer.setVisible(true);
        
    	for(int i = 0; i < 1000; i++) {
            ct.findPoint();
            viewer.updateTrackingPanel();
            viewer.updateCameraPanel(0);
            viewer.updateCameraPanel(1);
            show(i + "번==================");
        }
	}

	public static void testImageLoader(String filename) {
    	try {
			ImageLoader loader = new ImageLoader(filename, 0);
			
			Vector2[] data = loader.getData();
			for(Vector2 d : data)
				show(d.toString());
			show("-----");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public static void testObjectTracker(float[] hmd_data, float[] img_data) {
        ObjectTracker track = new ObjectTracker();
        
        track.setTarget(hmd_data);
        track.putData(img_data);
        track.showTargetInfo();
        
        track.mapPoint();
        track.showMap();
        track.updateTracking();
        track.showTrackingInfo();
    }
}