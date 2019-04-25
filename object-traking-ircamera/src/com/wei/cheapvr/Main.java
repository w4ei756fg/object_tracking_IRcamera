package com.wei.cheapvr;
import com.wei.cheapvr.Tracker.CameraTracker;
import com.wei.cheapvr.Tracker.ObjectTracker;
import com.wei.cheapvr.Tracker.Vector3;
import com.wei.cheapvr.Tracker.Quaternion;
import com.wei.cheapvr.Tracker.Structure;

public class Main {
    final static double ERROR_DEADZONE = 5;

    /**
     * @param args
     */
    public static void main(String[] args) {
        //*
	    double[] hmd_data = {0d,0d,0d, 
                             100d,0d,0d, 
                             90d,90d,0d, 
                             0d,40d,0d};
        double[] img_data = {93.30127,25,0, 
                             6.698729,-25,0,
                             39.641016326,97.942286441,0, 
                             -13.3015,9.639480,0};
        double[] img2_data = {0.0, 0.0, 0.0,
        						0.0, 0.0, -100.0,
        						0.0, 90.0, -90.0,
        						0.0, 40.0, 0.0};
        double[] img3_data = {3.0, 0.0, 0.0,
        						3.0, 90.0, -90.0,
        						3.0, 0.0, -100.0,
        						3.0, 40.0, 0.0};
        /*
        Structure str = new Structure(4);
        str.setPointsPos(hmd_data);
        for(int i = 0; i < str.length(); i++)
            show("" + str.getPointPos(i)[0] + ", " + str.getPointPos(i)[1] + ", " + str.getPointPos(i)[2]);
        str.roll(0, 90d/180d*Math.PI, 0);
        for(int i = 0; i < str.length(); i++)
            show("" + str.getPointPos(i)[0] + ", " + str.getPointPos(i)[1] + ", " + str.getPointPos(i)[2]);
        //double[] hmd_data = new double[30];
        //for(int i = 0; i < 30; i++) hmd_data[i] = Math.random() * 100;
        //*/
        //*
        ObjectTracker track = new ObjectTracker();
        
        track.setTarget(hmd_data);
        track.putData(img3_data);
        //track.showTargetInfo();
        
        track.mapPoint();
        track.showMap();
        track.updateTracking();
        track.showTrackingInfo();
        //*/
        
        //CameraTracker c = new CameraTracker();
        
        
        //쿼터니언 테스트
        /*
        Quaternion p = new Quaternion(0, 2, 0, 0);
        show(p.toString());
        
        double rad = Math.PI/2;
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
	public static void show(String str) { System.out.println(str); }
}
 
