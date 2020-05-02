package com.wei.cheapvr.Tracker;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import static com.wei.cheapvr.Utils.*;

public class Camera {
    public static float[] NOTE8_BACK = {4032, 3024, 0.567005f, 0.445538f};
    public static float[] OV2640_CIF = {400, 296, 0.349344f, 0.265042f};

    private float x, y, z, pan, tilt;
    private String ip;
    public final int id;
    private ImageLoader imgLoader;
    private float mw, mh, cx, cy, dw, dh; // 시야각 radian dw, dh
    private ArrayList<Vector2> points = new ArrayList<>();
    private ArrayList<Vector3> traceLine = new ArrayList<>();

    Camera(float x, float y, float z, float pan, float tilt, String ip, int id) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pan = pan;
        this.tilt = tilt;
        this.ip = ip;
        this.id = id;
        imgLoader = new ImageLoader(this.ip, this.id);
        try {
            MjpegRunner mjpegRunner = new MjpegRunner(imgLoader, new URL("http://" + ip + ":81/stream"));
            new Thread(mjpegRunner).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 카메라 내부 파라미터를 설정합니다.
     *
     * @param mw 픽셀 width
     * @param mh 픽셀 height
     * @param dw 수평 시야각
     * @param dh 수직 시야각
     */
    void setCameraParameter(float mw, float mh, float dw, float dh) {
        this.mw = mw;
        this.mh = mh;
        cx = this.mw / 2;
        cy = this.mh / 2;
        this.dw = dw;
        this.dh = dh;
    }

    /**
     * 카메라 내부 파라미터를 설정합니다.
     *
     * @param profile [픽셀 width, 픽셀 height, 수평 시야각, 수직 시야각]
     */
    void setCameraParameter(float[] profile) {
        setCameraParameter(profile[0], profile[1], profile[2], profile[3]);
    }

    /**
     * traceLine을 추적합니다.
     *
     * @return
     */
    int updateTraceLine() {
        if (findPoint() == 0) return -2;
        traceLine = new ArrayList<Vector3>(); // reset list
        float proj = mw / 2 / (float) Math.tan(dw);
        Vector3 p;

        for (int i = 0; i < points.size(); i++) {
            Vector2 pp = points.get(i);
            p = new Vector3((pp.x - cx) / proj, (pp.y - cy) / proj, 1);
            /* 좌표계 변환
             *
            p = p.mul(1/p.abs()); // norm
            Quaternion q = new Quaternion(0, p);
            q = q.roll(Quaternion.getRollFromEuler(-Math.PI/2 + tilt, 0, pan));
            p = q.toVector();
             */
            p.roll(-(float) Math.PI / 2 + tilt, 0, pan);
            traceLine.add(p);
        }

        return 0;
    }

    /**
     * ImageLoader로부터 추적된 Point를 points에 저장합니다.
     *
     * @return
     * @see ImageLoader#getData()
     */
    int findPoint() {
        points = new ArrayList<Vector2>(); // reset list
        try {
            Vector2[] data = imgLoader.getData();

            points.addAll(Arrays.asList(data));

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

    /**
     * 현재 추적된 traceLine를 반환합니다.
     *
     * @return
     */
    ArrayList<Vector3> getTraceLine() {
        return traceLine;
    }

    /**
     * 카메라 정보를 출력합니다.
     */
    public void showInfo() {
        show("I:Camera information(Camera)");
        show("Centroid: (" + x + ", " + y + ", " + z + ")");
        show("direction: " + (pan / Math.PI * 180) + ", " + (tilt / Math.PI * 180));
        show("-----------------------------------");
        show("-Points");
        for (int i = 0; i < points.size(); i++) {
            Vector2 p = (Vector2) points.get(i);
            show("" + i + ": " + p.toString());
        }
        show("-----------------------------------");
        show("-TraceLine");
        for (int i = 0; i < traceLine.size(); i++) {
            Vector3 p = (Vector3) traceLine.get(i);
            show("" + i + ": " + p.toString());
        }
        show("===================================");
    }

    /**
     * 카메라의 현재 원본 이미지를 반환합니다.
     * @return
     */
    public Image getRawImage() {
        return imgLoader.getRawImage();
    }

    /**
     * 카메라의 현재 그레이스케일 이미지를 반환합니다.
     * @return
     */
    public Image getGrayImage() {
        return imgLoader.getGrayImage();
    }

    /**
     * 카메라의 현재 흑백 이미지를 반환합니다.
     * @return
     */
    public Image getImage() {
        return imgLoader.getImage();
    }

    /**
     * 카메라의 좌표를 반환합니다.
     * @return
     */
    Vector3 getPos() {
        return new Vector3(x, y, z);
    }

    /**
     * 카메라의 ip주소를 반환합니다.
     * @return
     */
    String getIP() {
        return ip;
    }

    /**
     * 카메라의 틸트 값을 반환합니다.
     * @return
     */
    float getTilt() {
        return tilt;
    }

    /**
     * 카메라의 팬 값을 반환합니다.
     * @return
     */
    float getPan() {
        return pan;
    }
}
